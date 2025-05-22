package kz.bdl.erapservice.external;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class XmlHttpService {
    private final WebClient webClient;

    /**
     * Sends an XML request to the specified URL using HTTP POST
     *
     * @param url The target URL to send the request to
     * @param xmlContent The XML content to send
     * @return The response body as a String
     */
    public String sendXmlRequest(String url, String xmlContent) {
        log.info("Sending XML request to URL: {}", url);
        
        return webClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_XML)
                .bodyValue(xmlContent)
                .retrieve()
                .bodyToMono(String.class)
                .doOnError(error -> log.error("Error sending XML request to {}: {}", url, error.getMessage()))
                .block();
    }

    /**
     * Sends an XML request to the specified URL using HTTP POST asynchronously
     *
     * @param url The target URL to send the request to
     * @param xmlContent The XML content to send
     * @return A Mono containing the response body as a String
     */
    public Mono<String> sendXmlRequestAsync(String url, String xmlContent) {
        log.info("Sending async XML request to URL: {}", url);
        
        return webClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_XML)
                .bodyValue(xmlContent)
                .retrieve()
                .bodyToMono(String.class)
                .doOnError(error -> log.error("Error sending XML request to {}: {}", url, error.getMessage()));
    }
} 