/*
 * Copyright (C) Schweizerische Bundesbahnen SBB, 2016.
 */

package ch.sigi.tolerantreader.xml;

import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.commons.io.input.CharSequenceInputStream;
import org.junit.Assert;
import org.junit.Test;

import ch.sigi.tolerantreader.exception.TolerantReaderException;
import ch.sigi.tolerantreader.model.Model;
import ch.sigi.tolerantreader.model.SubTree;

public class TolerantXmlReaderTest {

    @Test
    public void testCongruentModel() {
        Model model = exampleModel();
        InputStream is = toXmlStringInputStream(model);

        try {
            Model parsedModel = TolerantXmlReader.read(is, Model.class);
            Assert.assertEquals(model, parsedModel);
        } catch (TolerantReaderException e) {
            Assert.fail("Exception in Tolerant Reader: " + e.getMessage());
        }
    }

    private InputStream toXmlStringInputStream(Model model) {
        String xml = null;
        try {
            xml = objectToXml(model);
            System.out.println(xml);
        } catch (JAXBException e) {
            Assert.fail("Exception during marshalling: " + e.getMessage());
        }
        return toStringInputStream(xml);
    }

    private Model exampleModel() {
        Model model = new Model();
        model.setSomeText("Some long text .....");
        model.setSomeLong(1L);
        model.setSomeBoolean(false);
        model.setSomeShort((short) 99);
        model.setSomeInteger(10_000);
        model.setSomeDouble(1.1d);
        model.setSomeFloat(1.1f);

        model.setSubTree(exampleSubtree());
        return model;
    }

    private SubTree exampleSubtree() {
        SubTree subtree = new SubTree();
        subtree.setSomeLong(99L);
        subtree.setSomeText("Subtree-Text");
        subtree.setSomeList(Arrays.asList("Element1", "Element2", "Element3"));
        return subtree;
    }

    private InputStream toStringInputStream(String str) {
        return new CharSequenceInputStream(str, StandardCharsets.UTF_8);
    }

    private <T> String objectToXml(T o) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(o.getClass());
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
        StringWriter sw = new StringWriter();
        jaxbMarshaller.marshal(o, sw);
        sw.flush();
        return sw.toString();
    }

}
