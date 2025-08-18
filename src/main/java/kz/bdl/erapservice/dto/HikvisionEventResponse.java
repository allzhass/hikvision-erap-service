package kz.bdl.erapservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HikvisionEventResponse {
    private String code;
    private String msg;
}