package kz.bdl.erapservice.dto.vshep.response;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

@Data
@XmlRootElement(name = "SendMessageResponse", namespace = "http://bip.bee.kz/SyncChannel/v10/Types")
@XmlAccessorType(XmlAccessType.FIELD)
public class SendMessageResponse {

    @XmlElement(name = "response")
    private Response response;

    public Response getResponse() {
        return response;
    }
}
