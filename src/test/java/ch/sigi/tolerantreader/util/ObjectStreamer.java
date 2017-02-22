package ch.sigi.tolerantreader.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.input.CharSequenceInputStream;
import org.junit.Assert;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

/**
 * Created by thomas on 22.02.17.
 */
public class ObjectStreamer {

    public static InputStream objectToXmlInputStream(Object object) {
        return toXmlStringInputStream(object);
    }

    public static InputStream objectToJsonInputStream(Object object) {
        return toJsonStringInputStream(object);
    }

    private static InputStream toJsonStringInputStream(Object instance) {
        String xml = null;
        try {
            xml = objectToJson(instance);
            System.out.println(xml);
        } catch (JsonProcessingException e) {
            Assert.fail("Exception during marshalling: " + e.getMessage());
        }
        return toStringInputStream(xml);
    }

    private static <T> String objectToJson(T o) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(o);
    }

    private static InputStream toXmlStringInputStream(Object instance) {
        String xml = null;
        try {
            xml = objectToXml(instance);
            System.out.println(xml);
        } catch (JAXBException e) {
            Assert.fail("Exception during marshalling: " + e.getMessage());
        }
        return toStringInputStream(xml);
    }

    private static <T> String objectToXml(T o) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(o.getClass());
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
        StringWriter sw = new StringWriter();
        jaxbMarshaller.marshal(o, sw);
        sw.flush();
        return sw.toString();
    }

    private static InputStream toStringInputStream(String str) {
        return new CharSequenceInputStream(str, StandardCharsets.UTF_8);
    }

}
