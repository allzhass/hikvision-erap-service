package kz.bdl.erapservice.external;

import feign.Client;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class FeignClientConfig {
    @Bean
    public Client feignClient() {
        return new feign.okhttp.OkHttpClient(
                new OkHttpClient.Builder()
                        .connectTimeout(30, TimeUnit.SECONDS)  // Таймаут подключения
                        .readTimeout(30, TimeUnit.SECONDS)  // Таймаут чтения
                        .writeTimeout(30, TimeUnit.SECONDS)  // Таймаут записи
                        .followRedirects(true)  // Автоматическое следование редиректам
                        .followSslRedirects(true)  // Следование HTTPS-редиректам
                        .build()
        );
    }
}
