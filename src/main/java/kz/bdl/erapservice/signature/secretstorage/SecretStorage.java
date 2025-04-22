package kz.bdl.erapservice.signature.secretstorage;

import kz.bdl.erapservice.signature.config.SecretStorageConfig;
import kz.bdl.erapservice.signature.config.SignatureConfig;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.util.StringUtils.hasText;

@Slf4j
public abstract class SecretStorage {

    private static final List<SupportedSigAlgorithmData> supportedAlgorithmDatas = List.of(
            new SupportedSigAlgorithmData(Gost2015_512.SIG_ALG_NAME, Gost2015_512.CONFIG_SUFFIX));
    protected SecretStorageConfig storageConf;
    protected SignatureConfig signConf;
    protected Map<String, Model> sigAlgToModelMap;

    public SecretStorage(SecretStorageConfig storageConf, SignatureConfig signConf) throws Exception {
        this.storageConf = storageConf;
        this.signConf = signConf;
        initStorage();
        initOrResetSigAlgModelMap();
    }

    public final Model getSecretStorageModelBySigAlgName(String sigAlgName) {
        return sigAlgToModelMap.get(sigAlgName);
    }

    protected abstract void initStorage() throws Exception;

    protected abstract Map<String, String> readSecrets() throws Exception;

    protected final Model buildModel(Map<String, String> dataMap, SupportedSigAlgorithmData supportedSigAlgData) {
        final var paths = signConf.getPaths();
        final var sigAlgConfSuffix = supportedSigAlgData.getSigAlgConfSuffix();
        final var sigAlgName = supportedSigAlgData.getSigAlgName();

        final var myP12_in_Base64 = dataMap.get(paths.getMyP12() + sigAlgConfSuffix);
        final var myP12_pwd = dataMap.get(paths.getMyP12Pwd() + sigAlgConfSuffix);
        if (!hasText(myP12_in_Base64))
            throw new IllegalStateException("myP12_in_Base64 is empty. SignAlgName=" + sigAlgName);
        if (!hasText(myP12_pwd))
            throw new IllegalStateException("myP12_pwd is empty. SignAlgName=" + sigAlgName);
        return Model.builder()
                .myP12_in_Base64(myP12_in_Base64)
                .myP12_pwd(myP12_pwd)
                .build();
    }

    protected final void initOrResetSigAlgModelMap() throws Exception {
        var secretDataMap = readSecrets();
        final var sigAlgNameToModelHashMap = new HashMap<String, Model>();
        for (final SupportedSigAlgorithmData ssad : supportedAlgorithmDatas) {
            sigAlgNameToModelHashMap.put(ssad.getSigAlgName(), buildModel(secretDataMap, ssad));
        }
        this.sigAlgToModelMap = sigAlgNameToModelHashMap;
    }

    public interface Gost2015_512 { // ГОСТ 34.11–2015 (512 bits)
        String SIG_ALG_NAME = Gost2015_512.class.getSimpleName();
        String CONFIG_SUFFIX = "." + SIG_ALG_NAME;
        String SIGN_METHOD_URI = "urn:ietf:params:xml:ns:pkigovkz:xmlsec:algorithms:gostr34102015-gostr34112015-512";
        String DIGEST_METHOD_URI = "urn:ietf:params:xml:ns:pkigovkz:xmlsec:algorithms:gostr34112015-512";
    }

    @Value
    @RequiredArgsConstructor
    protected static class SupportedSigAlgorithmData {
        String sigAlgName;
        String sigAlgConfSuffix;
    }

    @Builder
    @Value
    public static class Model {
        String myP12_in_Base64;
        String myP12_pwd;
    }
}
