package kz.bdl.erapservice.signature.secretstorage;

import com.bettercloud.vault.Vault;
import com.bettercloud.vault.VaultConfig;
import kz.bdl.erapservice.signature.config.SecretStorageConfig;
import kz.bdl.erapservice.signature.config.SignatureConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@Qualifier("vaultSecretStorage")
@Lazy
public class VaultSecretStorage extends SecretStorage {

    private Vault vault;

    @Autowired
    public VaultSecretStorage(SecretStorageConfig storageConf, SignatureConfig signConf) throws Exception {
        super(storageConf, signConf);
    }

    @Override
    protected void initStorage() throws Exception {
        var vaultConfig = new VaultConfig().address(storageConf.getUrl()).token(storageConf.getToken()).build();
        this.vault = new Vault(vaultConfig, 1);
    }

    @Override
    public Map<String, String> readSecrets() throws Exception {
        try {
            return vault.logical().read(storageConf.getSecretPath()).getData();
        } catch (Exception e) {
            log.error("Vault read secret error: " + e.getMessage(), e);
            throw e;
        }
    }

}
