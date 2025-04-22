package kz.bdl.erapservice.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "${services.smartbridge.name}",
        url = "${services.smartbridge.host}")
public interface SmartBridgeServiceClient {

    @PostMapping
    String sendMessage(@RequestBody String request);
}