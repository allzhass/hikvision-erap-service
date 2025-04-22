package kz.bdl.erapservice.mapper;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;

public class DocumentMapper {
    public static Document getDocument(String xmlString) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        try {
            DocumentBuilder documentBuilder = dbf.newDocumentBuilder();
            return documentBuilder.parse(new ByteArrayInputStream(xmlString.getBytes("UTF-8")));
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static String getString(Document document) {
        StringWriter os = new StringWriter();
        try {
            TransformerFactory.newInstance().newTransformer().transform(new DOMSource(document), new StreamResult(os));
            return os.toString();
        } catch (TransformerException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
