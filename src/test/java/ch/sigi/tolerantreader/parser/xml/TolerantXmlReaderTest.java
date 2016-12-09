/*
 * Copyright (C) Schweizerische Bundesbahnen SBB, 2016.
 */

package ch.sigi.tolerantreader.parser.xml;

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
import ch.sigi.tolerantreader.model.ModelSubset;
import ch.sigi.tolerantreader.model.ModelSuperset;
import ch.sigi.tolerantreader.model.ModelWithAnnotations;
import ch.sigi.tolerantreader.model.ModelWithInvalidAnnotations;
import ch.sigi.tolerantreader.model.SubTree;

public class TolerantXmlReaderTest {

    @Test
    public void testCongruentModel() {
        Model model = exampleModel();
        InputStream is = toXmlStringInputStream(model);

        try {
            Model parsedModel = TolerantXmlReader.Builder
                    .defaultSettings()
                    .build()
                    .read(is, Model.class);

            Assert.assertEquals(model, parsedModel);

        } catch (TolerantReaderException e) {
            Assert.fail("Exception in Tolerant Reader: " + e.getMessage());
        }
    }

    @Test
    public void testASupersetOfTheModel() {
        Model model = exampleModel();
        InputStream is = toXmlStringInputStream(model);

        try {
            ModelSuperset parsedModel = TolerantXmlReader.Builder
                    .defaultSettings()
                    .build()
                    .read(is, ModelSuperset.class);

            Assert.assertEquals(model.getSomeBoolean(), parsedModel.getSomeBoolean());
            Assert.assertEquals(model.getSomeDouble(), parsedModel.getSomeDouble());
            Assert.assertEquals(model.getSomeFloat(), parsedModel.getSomeFloat());
            Assert.assertEquals(model.getSomeInteger(), parsedModel.getSomeInteger());
            Assert.assertEquals(model.getSomeShort(), parsedModel.getSomeShort());
            Assert.assertEquals(model.getSomeText(), parsedModel.getSomeText());
            Assert.assertEquals(model.getSubTree().getSomeText(), parsedModel.getSubTree().getSomeText());
            Assert.assertEquals(model.getSubTree().getSomeList().toArray(), parsedModel.getSubTree().getSomeList().toArray());
            Assert.assertEquals(model.getSubTree().getSomeLong(), parsedModel.getSubTree().getSomeLong());
            Assert.assertNull(parsedModel.getSomeAdditionalField());
            Assert.assertNull(parsedModel.getSubTree().getSomeNewText());
            Assert.assertNull(parsedModel.getSubTree().getSomeOtherList());

        } catch (TolerantReaderException e) {
            Assert.fail("Exception in Tolerant Reader: " + e.getMessage());
        }
    }

    @Test
    public void testASubsetOfTheModel() {
        Model model = exampleModel();
        InputStream is = toXmlStringInputStream(model);

        try {
            ModelSubset parsedModel = TolerantXmlReader.Builder
                    .defaultSettings()
                    .build()
                    .read(is, ModelSubset.class);

            Assert.assertEquals(model.getSomeBoolean(), parsedModel.getSomeBoolean());
            Assert.assertEquals(model.getSomeDouble(), parsedModel.getSomeDouble());
            Assert.assertEquals(model.getSomeInteger(), parsedModel.getSomeInteger());
            Assert.assertEquals(model.getSomeShort(), parsedModel.getSomeShort());
            Assert.assertEquals(model.getSomeText(), parsedModel.getSomeText());
            Assert.assertEquals(model.getSubTree().getSomeText(), parsedModel.getSubTree().getSomeText());
            Assert.assertEquals(model.getSubTree().getSomeLong(), parsedModel.getSubTree().getSomeLong());

        } catch (TolerantReaderException e) {
            Assert.fail("Exception in Tolerant Reader: " + e.getMessage());
        }
    }

    @Test
    public void testAModelWithAnnotations() {
        Model model = exampleModel();
        InputStream is = toXmlStringInputStream(model);

        try {
            ModelWithAnnotations parsedModel = TolerantXmlReader.Builder
                    .defaultSettings()
                    .build()
                    .read(is, ModelWithAnnotations.class);

            Assert.assertEquals(model.getSomeText(), parsedModel.getSomeCustomFieldName());
            Assert.assertEquals(model.getSomeBoolean(), parsedModel.getSomeBoolean());
            Assert.assertEquals(model.getSubTree().getSomeText(), parsedModel.getSubTreeText());
            Assert.assertEquals(model.getSubTree().getSomeLong(), parsedModel.getSubTreeLong());
            Assert.assertEquals(model.getSubTree().getSomeList().toArray(), parsedModel.getSubTreeList().toArray());
            Assert.assertNull(parsedModel.getInexistent());
            Assert.assertNull(parsedModel.getSomeInexistendCustomField());

        } catch (TolerantReaderException e) {
            Assert.fail("Exception in Tolerant Reader: " + e.getMessage());
        }
    }

    @Test(expected = TolerantReaderException.class)
    public void testAModelWithInvalidAnnotations() throws TolerantReaderException {
        Model model = exampleModel();
        InputStream is = toXmlStringInputStream(model);

        TolerantXmlReader.Builder
                .defaultSettings()
                .build()
                .read(is, ModelWithInvalidAnnotations.class);
        Assert.fail("TolerantReaderException Exception expected, due to invalid Annotations!");
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
