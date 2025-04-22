package kz.bdl.erapservice.dto.vshep.request;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class Request {
    @XmlElement(name = "requestInfo")
    private RequestInfo requestInfo;

    @XmlElement(name = "requestData")
    private RequestData requestData;
}
