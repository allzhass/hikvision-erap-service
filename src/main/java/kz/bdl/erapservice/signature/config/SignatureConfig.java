package kz.bdl.erapservice.signature.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@Data
@Configuration
@ConfigurationProperties(prefix = "signature")
public class SignatureConfig {
    private String currentVerifyAlg;
    private String currentSignAlg;
    private Paths paths;

    @Data
    public static class Paths {
        private String myP12;
        private String myP12Pwd;
    }
}
