package kz.bdl.erapservice.external;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Base64;

@Component
public class FileDownloadWebClient {

    private final WebClient webClient;

    public FileDownloadWebClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_OCTET_STREAM_VALUE)
                .build();
    }

    public String downloadFileAsBase64(URI fileUri) {
        return webClient.get()
                .uri(fileUri)
                .retrieve()
                .bodyToMono(byte[].class)
                .map(bytes -> Base64.getEncoder().encodeToString(bytes))
                .onErrorResume(e -> {
                    // лог или обработка ошибки
                    return Mono.empty(); // можно вернуть null или бросить исключение
                })
                .block();
    }
}