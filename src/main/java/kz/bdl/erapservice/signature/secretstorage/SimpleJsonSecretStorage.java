package kz.bdl.erapservice.signature.secretstorage;

import com.fasterxml.jackson.databind.ObjectMapper;
import kz.bdl.erapservice.signature.config.SecretStorageConfig;
import kz.bdl.erapservice.signature.config.SignatureConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@Qualifier("simpleJsonSecretStorage")
@Lazy
public class SimpleJsonSecretStorage extends SecretStorage {

    @Autowired
    ResourceLoader resourceLoader;
    private String jsonStorage;

    public SimpleJsonSecretStorage(SecretStorageConfig storageConf, SignatureConfig signConf) throws Exception {
        super(storageConf, signConf);
    }

    @Override
    protected void initStorage() throws Exception {
        jsonStorage = new String(new ClassPathResource(storageConf.getSecretPath()).getInputStream().readAllBytes());
        log.info("jsonStorage: {}", jsonStorage);
    }

    @Override
    protected Map<String, String> readSecrets() throws Exception {
        final var mapper = new ObjectMapper();
        return mapper.readValue(jsonStorage, Map.class);
    }

}