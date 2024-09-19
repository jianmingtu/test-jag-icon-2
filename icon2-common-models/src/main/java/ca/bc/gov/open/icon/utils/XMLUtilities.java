package ca.bc.gov.open.icon.utils;

import java.io.*;
import jakarta.xml.bind.*;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.stream.StreamSource;

public final class XMLUtilities {

    private static final String XML_HEADER = "<?xml version=\"1.0\"?>";
    private static final Integer BUFFER_SIZE = 4000;

    public static <T> T deserializeXmlStr(String xmlString, T obj) {
        try {
            StringBuilder xml = new StringBuilder(xmlString);
            if (isUserToken(obj)) {
                xml =
                        new StringBuilder(
                                xmlString
                                        .replaceAll("<CsNumber>|<csNumber>", "<CSNumber>")
                                        .replaceAll("</CsNumber>|</csNumber>", "</CSNumber>")
                                        .replaceAll(
                                                "<SiteMinderSessionId>", "<SiteMinderSessionID>")
                                        .replaceAll(
                                                "</SiteMinderSessionId>", "</SiteMinderSessionID>")
                                        .replaceAll(
                                                "<SiteMinderTransactionId>",
                                                "<SiteMinderTransactionID>")
                                        .replaceAll(
                                                "</SiteMinderTransactionId>",
                                                "</SiteMinderTransactionID>"));
            }

            JAXBContext jaxbContext = JAXBContext.newInstance(obj.getClass());
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            StreamSource source = new StreamSource(new StringReader(xml.toString()));
            JAXBElement<T> jaxbElement =
                    (JAXBElement<T>) unmarshaller.unmarshal(source, obj.getClass());
            return jaxbElement.getValue();
        } catch (JAXBException e) {
            return null;
        }
    }

    public static <T> boolean isUserToken(T obj) {
        return (obj instanceof ca.bc.gov.open.icon.myinfo.UserToken
                || obj instanceof ca.bc.gov.open.icon.ereporting.UserToken
                || obj instanceof ca.bc.gov.open.icon.hsr.UserToken
                || obj instanceof ca.bc.gov.open.icon.message.UserToken
                || obj instanceof ca.bc.gov.open.icon.myinfo.UserToken
                || obj instanceof ca.bc.gov.open.icon.trustaccount.UserToken
                || obj instanceof ca.bc.gov.open.icon.visitschedule.UserToken
                || obj instanceof ca.bc.gov.open.icon.auth.UserToken);
    }

    public static <T> String serializeXmlStr(T obj) {
        try {
            StringWriter stringWriter = new StringWriter(BUFFER_SIZE);
            stringWriter.write(XML_HEADER);
            XMLStreamWriter xmlStreamWriter =
                    XMLOutputFactory.newInstance().createXMLStreamWriter(stringWriter);
            JAXBContext jaxbContext = JAXBContext.newInstance(obj.getClass());
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            jaxbMarshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
            JAXBElement<T> jaxbElement =
                    new JAXBElement<>(
                            new QName("", obj.getClass().getSimpleName()),
                            (Class<T>) obj.getClass(),
                            obj);
            jaxbMarshaller.marshal(jaxbElement, new CustomizedXMLStreamWriter(xmlStreamWriter));

            return stringWriter.toString();
        } catch (JAXBException | XMLStreamException e) {
            return null;
        }
    }
}
