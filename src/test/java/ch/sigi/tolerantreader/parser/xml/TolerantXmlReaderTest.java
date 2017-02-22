/*
 * Copyright (C) Schweizerische Bundesbahnen SBB, 2016.
 */

package ch.sigi.tolerantreader.parser.xml;

import ch.sigi.tolerantreader.CustomizedTolerantReader;
import ch.sigi.tolerantreader.document.Document;
import ch.sigi.tolerantreader.document.xml.XmlDocument;
import ch.sigi.tolerantreader.exception.TolerantReaderException;
import ch.sigi.tolerantreader.model.Model;
import ch.sigi.tolerantreader.model.ModelWithXPathAnnotations;
import ch.sigi.tolerantreader.parser.TolerantReaderBaseTest;
import ch.sigi.tolerantreader.util.ObjectStreamer;
import org.junit.Assert;
import org.junit.Test;

import java.io.InputStream;

public class TolerantXmlReaderTest extends TolerantReaderBaseTest {

    @Test
    public void testAModelWithAnnotations() {
        Model model = exampleModel();
        InputStream is = objectToInputStream(model);

        try {
            ModelWithXPathAnnotations parsedModel = CustomizedTolerantReader.Builder
                    .defaultSettings()
                    .build()
                    .read(documentFor(is, ModelWithXPathAnnotations.class));

            Assert.assertEquals(model.getSomeText(), parsedModel.getSomeCustomFieldName());
            Assert.assertEquals(model.getSomeBoolean(), parsedModel.getSomeBoolean());
            Assert.assertEquals(model.getSubTree().getSomeText(), parsedModel.getSubTreeText());
            Assert.assertEquals(model.getSubTree().getSomeLong(), parsedModel.getSubTreeLong());
            Assert.assertArrayEquals(model.getSubTree().getSomeList().toArray(), parsedModel.getSubTreeList().toArray());
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
        return ObjectStreamer.objectToXmlInputStream(object);
    }

}
