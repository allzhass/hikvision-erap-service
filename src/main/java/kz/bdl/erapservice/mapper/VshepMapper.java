package kz.bdl.erapservice.mapper;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import kz.bdl.erapservice.dto.vshep.response.Envelope;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.time.LocalDateTime;
import java.util.UUID;

public class VshepMapper {

    public static Envelope toEnvelope(String xmlString) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(Envelope.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        return (Envelope) unmarshaller.unmarshal(new StringReader(xmlString));
    }

    public static String wrapViolation(String violationXml) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();

            // Создаём корневой элемент с namespace
            Element sendMessage = doc.createElementNS("http://bip.bee.kz/SyncChannel/v10/Types", "NS1:SendMessage");
            sendMessage.setAttribute("xmlns:xsd", "http://www.w3.org/2001/XMLSchema");
            doc.appendChild(sendMessage);

            // <request>
            Element request = doc.createElement("request");
            sendMessage.appendChild(request);

            // <requestInfo>
            Element requestInfo = doc.createElement("requestInfo");
            request.appendChild(requestInfo);

            requestInfo.appendChild(createElementWithText(doc, "messageId", UUID.randomUUID().toString()));
            requestInfo.appendChild(createElementWithText(doc, "correlationId", UUID.randomUUID().toString()));
            requestInfo.appendChild(createElementWithText(doc, "messageDate", LocalDateTime.now().toString()));
            requestInfo.appendChild(createElementWithText(doc, "serviceId", "erap_violation_receiver"));

            // <sender>
            Element sender = doc.createElement("sender");
            requestInfo.appendChild(sender);
            sender.appendChild(createElementWithText(doc, "senderId", "mergenzhetysu"));
            sender.appendChild(createElementWithText(doc, "password", "RBOyXc6c"));

            // <requestData>
            Element requestData = doc.createElement("requestData");
            request.appendChild(requestData);

            // <data>
            Element data = doc.createElement("data");
            data.setAttribute("xmlns:xs", "http://www.w3.org/2001/XMLSchema");
            data.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
            data.setAttribute("xsi:type", "xs:string");
            requestData.appendChild(data);

            // <otg:onEventShep>
            Element onEventShep = doc.createElementNS("http://otgroup.kz/", "otg:onEventShep");
            data.appendChild(onEventShep);

            // Вставляем <violation> внутрь <onEventShep>
            Node importedViolation = doc.importNode(DocumentMapper.getDocument(violationXml).getDocumentElement(), true);
            onEventShep.appendChild(importedViolation);

            return DocumentMapper.getString(doc);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private static Element createElementWithText(Document doc, String name, String text) {
        Element element = doc.createElement(name);
        element.setTextContent(text);
        return element;
    }
}
