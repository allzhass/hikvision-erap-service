package kz.bdl.erapservice.signature.config;

import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "signature.secret-storage")
@Data
@ToString(exclude = {"token"})
public class SecretStorageConfig {
    private String url;
    private String token;
    private String secretPath;
}