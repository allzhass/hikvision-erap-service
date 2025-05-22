package kz.bdl.erapservice.external;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
@Slf4j
public class XmlApacheHttpService {
    public String sendXmlRequest(String url, String xmlContent) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/xml")
                .POST(HttpRequest.BodyPublishers.ofString(xmlContent))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        log.info("Response: status code={}; body={}", response.statusCode(), response.body());
        return response.body();
    }
}
