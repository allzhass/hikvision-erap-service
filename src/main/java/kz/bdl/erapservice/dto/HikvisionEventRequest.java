package kz.bdl.erapservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HikvisionEventRequest {
    private String id;
    private String cameraIp;
    private String plateNumber;
    private String image;
    private String date;
    private ViolationData violation;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ViolationData {
        private String type;
        private Integer speed;
    }
}
