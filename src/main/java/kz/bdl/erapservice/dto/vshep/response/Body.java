package kz.bdl.erapservice.dto.vshep.response;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

@Data
@XmlRootElement(name = "Body", namespace = "http://schemas.xmlsoap.org/soap/envelope/")
@XmlAccessorType(XmlAccessType.FIELD)
public class Body {

    @XmlElement(name = "SendMessageResponse", namespace = "http://bip.bee.kz/SyncChannel/v10/Types")
    private SendMessageResponse sendMessageResponse;

    public SendMessageResponse getSendMessageResponse() {
        return sendMessageResponse;
    }
}
