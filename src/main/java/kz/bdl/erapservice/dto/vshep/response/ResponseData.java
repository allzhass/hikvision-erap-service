package kz.bdl.erapservice.dto.vshep.response;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

@Data
@XmlRootElement(name = "responseData")
@XmlAccessorType(XmlAccessType.FIELD)
public class ResponseData {

    @XmlElement(name = "data")
    private DataWrapper data;
}
