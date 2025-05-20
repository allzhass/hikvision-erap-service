package kz.bdl.erapservice.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "${services.smartbridge-test.name}",
        url = "${services.smartbridge-test.host}")
public interface SmartBridgeServiceClientTest {

    @PostMapping
    String sendMessage(@RequestBody String request);
}