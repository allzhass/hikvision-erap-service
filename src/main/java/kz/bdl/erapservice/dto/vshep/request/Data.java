package kz.bdl.erapservice.dto.vshep.request;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@lombok.Data
@AllArgsConstructor
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class Data {
    @XmlElement(name = "onEventShep", namespace = "http://otgroup.kz/")
    private OnEventShep onEventShep;
}
