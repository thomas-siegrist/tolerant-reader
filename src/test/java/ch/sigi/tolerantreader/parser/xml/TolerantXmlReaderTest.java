/*
 * Copyright (C) Schweizerische Bundesbahnen SBB, 2016.
 */

package ch.sigi.tolerantreader.parser.xml;

import ch.sigi.tolerantreader.TolerantReader;
import ch.sigi.tolerantreader.document.Document;
import ch.sigi.tolerantreader.document.xml.XmlDocument;
import ch.sigi.tolerantreader.exception.TolerantReaderException;
import ch.sigi.tolerantreader.model.Model;
import ch.sigi.tolerantreader.model.ModelWithXPathAnnotations;
import ch.sigi.tolerantreader.parser.TolerantReaderBaseTest;
import org.apache.commons.io.input.CharSequenceInputStream;
import org.junit.Assert;
import org.junit.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

public class TolerantXmlReaderTest extends TolerantReaderBaseTest {

    @Test
    public void testAModelWithAnnotations() {
        Model model = exampleModel();
        InputStream is = objectToInputStream(model);

        try {
            ModelWithXPathAnnotations parsedModel = TolerantReader.Builder
                    .defaultSettings()
                    .build()
                    .read(documentFor(is, ModelWithXPathAnnotations.class));

            Assert.assertEquals(model.getSomeText(), parsedModel.getSomeCustomFieldName());
            Assert.assertEquals(model.getSomeBoolean(), parsedModel.getSomeBoolean());
            Assert.assertEquals(model.getSubTree().getSomeText(), parsedModel.getSubTreeText());
            Assert.assertEquals(model.getSubTree().getSomeLong(), parsedModel.getSubTreeLong());
            Assert.assertEquals(model.getSubTree().getSomeList().toArray(), parsedModel.getSubTreeList().toArray());
            Assert.assertNull(parsedModel.getInexistent());
            Assert.assertNull(parsedModel.getSomeInexistendCustomField());

        } catch (TolerantReaderException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected <T> Document<T> documentFor(InputStream is, Class<T> clazz) {
        return XmlDocument.Builder
                .forClass(clazz)
                .withInputStream(is)
                .build();
    }

    @Override
    protected InputStream objectToInputStream(Object object) {
        return toXmlStringInputStream(object);
    }

    private InputStream toXmlStringInputStream(Object instance) {
        String xml = null;
        try {
            xml = objectToXml(instance);
            System.out.println(xml);
        } catch (JAXBException e) {
            Assert.fail("Exception during marshalling: " + e.getMessage());
        }
        return toStringInputStream(xml);
    }

    private <T> String objectToXml(T o) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(o.getClass());
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
        StringWriter sw = new StringWriter();
        jaxbMarshaller.marshal(o, sw);
        sw.flush();
        return sw.toString();
    }

    private InputStream toStringInputStream(String str) {
        return new CharSequenceInputStream(str, StandardCharsets.UTF_8);
    }

}
