package kz.bdl.erapservice.signature.secretstorage;

import kz.gov.pki.kalkan.jce.provider.KalkanProvider;
import kz.gov.pki.kalkan.xmldsig.KncaXS;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.*;
import java.security.cert.Certificate;
import java.util.Base64;
import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public final class BundleProvider {

    // PKCS#12 provides a container for one or several certificates with private keys.
    // PKCS12 содержит в себе приватный ключ и публичный(сертификат) is the binary format that stores the server certificate, the intermediate certificate and the private key in a single password-protected pfx or .p12 file.
    // PKCS7 Cодержит только сертификат, без приватного ключа. PKCS#7 format can be used to store one or more certificates without private keys (private keys can be put as a data payload and encrypted this way).
    public static final String PKCS_7 = "PKCS7", PKCS_12 = "PKCS12";
    private static final int BUNDLE_CACHE_REFRESH_PERIOD = 10 * 60 * 1000;
    public static CertificateFactory CERTIFICATE_FACTORY;

    static {
        Security.removeProvider(KalkanProvider.PROVIDER_NAME);
        Security.addProvider(new KalkanProvider());
        System.setProperty("org.apache.xml.security.ignoreLineBreaks", "true"); // xml элемент Signature будет в одну линию
        KncaXS.loadXMLSecurity();
        try {
            CERTIFICATE_FACTORY = CertificateFactory.getInstance("X.509", KalkanProvider.PROVIDER_NAME);
        } catch (CertificateException | NoSuchProviderException e) {
            log.error("Unable to build CertificateFactory: " + e.getMessage(), e);
            // todo: send notification about CertificateFactory creation exception
            System.exit(-1);
        }
    }

    private final SecretStorage secretStorage;
    private final Map<Long, BundleBySignAlg> bundleMap = new ConcurrentHashMap<>();

    @Autowired
    public BundleProvider(@Qualifier("simpleJsonSecretStorage") SecretStorage secretStorage) throws Exception {
        this.secretStorage = secretStorage;
        initializeOrUpdateBundleCache();
    }

    public void initBundleCustom(Long certId, String p12InBase64, String p12Password) throws Exception {
        BundleBySignAlg bundle = new BundleBySignAlg(
            SecretStorage.Gost2015_512.SIG_ALG_NAME,
            SecretStorage.Gost2015_512.SIGN_METHOD_URI,
            SecretStorage.Gost2015_512.DIGEST_METHOD_URI
        );
        log.info("Cerficate bundle: id={}; pwd={}; cert={}", certId, p12Password, p12InBase64);

        buildAndAssignOwnX509CertAndPrivateKey_in_P12(bundle, p12InBase64, p12Password);
        bundle.myP12_in_Base64 = p12InBase64;
        bundle.myP12_pwd = p12Password;
        bundleMap.put(certId, bundle);
    }

//    @Scheduled(initialDelay = BUNDLE_CACHE_REFRESH_PERIOD, fixedDelay = BUNDLE_CACHE_REFRESH_PERIOD)
    public void initializeOrUpdateBundleCache() throws Exception {
        secretStorage.initOrResetSigAlgModelMap();
        final var gost2015_512Model = secretStorage.getSecretStorageModelBySigAlgName(SecretStorage.Gost2015_512.SIG_ALG_NAME);
        BundleBySignAlg bundle = new BundleBySignAlg(
            SecretStorage.Gost2015_512.SIG_ALG_NAME,
            SecretStorage.Gost2015_512.SIGN_METHOD_URI,
            SecretStorage.Gost2015_512.DIGEST_METHOD_URI
        );
        initBundleFor(gost2015_512Model, bundle);
        // Store with a default ID of 0 for backward compatibility
        bundleMap.put(0L, bundle);
    }

    private void initBundleFor(SecretStorage.Model model, BundleBySignAlg bundle) throws Exception {
        final var myP12_in_Base64 = model.getMyP12_in_Base64();
        final var myP12_pwd = model.getMyP12_pwd();

        buildAndAssignOwnX509CertAndPrivateKey_in_P12(bundle, myP12_in_Base64, myP12_pwd);
        bundle.myP12_in_Base64 = myP12_in_Base64;
        bundle.myP12_pwd = myP12_pwd;
    }

    private void buildAndAssignOwnX509CertAndPrivateKey_in_P12(BundleBySignAlg bundle, final String myP12_in_Base64, final String myP12_pwd) throws Exception {
        KeyStore keyStore;
        String switchP12_alias = null;
        try {
            keyStore = KeyStore.getInstance(PKCS_12, KalkanProvider.PROVIDER_NAME);
            keyStore.load(new ByteArrayInputStream(Base64.getDecoder().decode(myP12_in_Base64)), myP12_pwd.toCharArray());
            Enumeration<String> als = keyStore.aliases();
            while (als.hasMoreElements()) {
                switchP12_alias = als.nextElement();
            }
            bundle.my_x509Cert = (X509Certificate) keyStore.getCertificate(switchP12_alias);
            bundle.my_x509CertChain = keyStore.getCertificateChain(switchP12_alias);
        } catch (KeyStoreException | NoSuchProviderException | CertificateException | IOException | NoSuchAlgorithmException e) {
            log.error("Unable to build switch cert. " + e.getMessage(), e);
            throw e;
        }
        try {
            bundle.myPrivateKey = (PrivateKey) keyStore.getKey(switchP12_alias, myP12_pwd.toCharArray());
        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
            log.error("Unable to build switch private key. " + e.getMessage(), e);
            throw e;
        }
        log.info("build_and_assign_own_x_509_cert_and_private_key_in_p_12 successfully done");
    }

    public BundleBySignAlg getSignBundle(Long certId) {
        BundleBySignAlg bundle = bundleMap.get(certId);
        if (bundle == null) {
            // Fallback to default bundle for backward compatibility
            bundle = bundleMap.get(0L);
            if (bundle == null) {
                throw new IllegalStateException("No certificate bundle found for ID: " + certId);
            }
        }
        return bundle;
    }

    public BundleBySignAlg getSignBundle() {
        return getSignBundle(0L);
    }

    @Getter
    @RequiredArgsConstructor
    public static class BundleBySignAlg {
        private final String sigAlgName;
        private final String signMethodURI;
        private final String digestMethodURI;
        private String myP12_in_Base64;
        private String myP12_pwd;
        private X509Certificate my_x509Cert;
        private Certificate[] my_x509CertChain;
        private PrivateKey myPrivateKey;
    }
}