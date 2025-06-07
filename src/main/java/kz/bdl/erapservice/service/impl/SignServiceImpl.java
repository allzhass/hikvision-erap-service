package kz.bdl.erapservice.service.impl;

import jakarta.xml.soap.*;
import kz.bdl.erapservice.mapper.DocumentMapper;
import kz.bdl.erapservice.service.SignService;
import kz.bdl.erapservice.signature.secretstorage.BundleProvider;
import kz.bdl.erapservice.signature.secretstorage.SecretStorage;
import org.apache.xml.security.encryption.XMLCipherParameters;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.transforms.Transforms;
import org.apache.xml.security.transforms.params.InclusiveNamespaces;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.transform.*;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.util.Base64;

@Service
public class SignServiceImpl implements SignService {

    @Override
    public String signXml(String xmlString, BundleProvider.BundleBySignAlg signBundle) {
        try {
            Document doc = DocumentMapper.getDocument(xmlString);
            XMLSignature sig = new XMLSignature(doc, "", signBundle.getSignMethodURI());
            if (doc.getFirstChild() != null) {
                doc.getFirstChild().appendChild(sig.getElement());
                Transforms transforms = new Transforms(doc);
                transforms.addTransform(Transforms.TRANSFORM_ENVELOPED_SIGNATURE);
                transforms.addTransform(XMLCipherParameters.N14C_XML_CMMNTS);
                sig.addDocument("", transforms, SecretStorage.Gost2015_512.DIGEST_METHOD_URI);
                sig.addKeyInfo(signBundle.getMy_x509Cert());
                sig.sign(signBundle.getMyPrivateKey());

                return DocumentMapper.getString(doc);
            } else {
                throw new RuntimeException("Empty xml request");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public String signSoap(String xmlString, BundleProvider.BundleBySignAlg signBundle) {
        try {
            Document doc = DocumentMapper.getDocument(xmlString);

            MessageFactory messageFactory = MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);
            SOAPMessage soapMessage = messageFactory.createMessage();
            SOAPEnvelope soapEnvelope = soapMessage.getSOAPPart().getEnvelope();

            SOAPBody soapBody = soapMessage.getSOAPBody();
            soapBody.addDocument(doc);

            soapBody.addAttribute(soapEnvelope.createName("Id", "wsu",
                    "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd"), "bodyId");

            Source src = soapMessage.getSOAPPart().getContent();
            TransformerFactory tf0 = TransformerFactory.newInstance();
            Transformer transformer = tf0.newTransformer();
            DOMResult result = new DOMResult();
            transformer.transform(src, result);
            Document soapDoc = (Document) result.getNode();

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer0 = transformerFactory.newTransformer();

            NodeList bodyNodes = soapDoc.getElementsByTagNameNS("http://schemas.xmlsoap.org/soap/envelope/","Body");
            Element bodyElement = (Element)bodyNodes.item(0);
            bodyElement.setIdAttributeNS("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd", "Id", true);

            DOMSource source1 = new DOMSource(bodyElement);
            StreamResult result1 = new StreamResult(new StringWriter());
            transformer0.transform(source1, result1);

            NodeList nodes = soapDoc.getElementsByTagNameNS("http://schemas.xmlsoap.org/soap/envelope/","Header");
            Element headerElement = (Element)nodes.item(0);

            // Create an XMLSignature instance
            Element soapsec = soapDoc.createElementNS("", "wsse:Security");
            soapsec.setAttributeNS("http://schemas.xmlsoap.org/soap/envelope/", "SOAP-ENV:mustUnderstand", "1");
            soapsec.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:wsse", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd");
            soapsec.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:wsu", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd");
            Transforms transforms = new Transforms(soapDoc);
            transforms.addTransform(Transforms.TRANSFORM_C14N_EXCL_OMIT_COMMENTS);
            XMLSignature sig = new XMLSignature(soapDoc, "#bodyId", signBundle.getSignMethodURI(), Transforms.TRANSFORM_C14N_EXCL_OMIT_COMMENTS);
            transforms.item(0).getElement().appendChild(new InclusiveNamespaces(soapDoc, "").getElement());

            soapsec.appendChild(sig.getElement());
            headerElement.appendChild(soapsec);
            sig.addDocument("#bodyId", transforms, signBundle.getDigestMethodURI());
            sig.sign(signBundle.getMyPrivateKey());

            byte[] certByte = signBundle.getMy_x509Cert().getEncoded();
            NodeList nodesSig = soapDoc.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#","Signature");
            Element soapSig = (Element)nodesSig.item(0);
            Element soapKeyInfo = soapDoc.createElementNS("", "ds:KeyInfo");
            soapKeyInfo.setAttribute("Id", "KI-BEFF7CB55C69AB1BB514762482966307");
            Element soapSecTokenRef = soapDoc.createElementNS("", "wsse:SecurityTokenReference");
            soapSecTokenRef.setAttribute("wsu:Id", "STR-BEFF7CB55C69AB1BB514762482966308");
            Element soapKeyIdent = soapDoc.createElementNS("", "wsse:KeyIdentifier");
            soapKeyIdent.setAttribute("EncodingType", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-soap-message-security-1.0#Base64Binary");
            soapKeyIdent.setAttribute("ValueType", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-x509-token-profile-1.0#X509v3");
            soapKeyIdent.appendChild(soapDoc.createTextNode(Base64.getEncoder().encodeToString(certByte)));
            soapSecTokenRef.appendChild(soapKeyIdent);
            soapKeyInfo.appendChild(soapSecTokenRef);
            soapSig.appendChild(soapKeyInfo);

            return DocumentMapper.getString(soapDoc);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
