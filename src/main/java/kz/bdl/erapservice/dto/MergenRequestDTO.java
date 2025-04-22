package kz.bdl.erapservice.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import kz.bdl.erapservice.dto.erap.ErapViolation;
import lombok.Data;

@Data
@XmlRootElement(name = "mergen_violation")
@XmlAccessorType(XmlAccessType.FIELD)
public class MergenRequestDTO {
    private String cameraIp;
    private ErapViolation violation;
}
