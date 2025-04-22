package kz.bdl.erapservice.dto.vshep.response;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

@Data
@XmlRootElement(name = "responseInfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class ResponseInfo {

    @XmlElement(name = "messageId")
    private String messageId;

    @XmlElement(name = "responseDate")
    private String responseDate;

    @XmlElement(name = "status")
    private Status status;

}
