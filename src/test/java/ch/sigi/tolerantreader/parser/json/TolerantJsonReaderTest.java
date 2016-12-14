/*
 * Copyright (C) Schweizerische Bundesbahnen SBB, 2016.
 */

package ch.sigi.tolerantreader.parser.json;

import ch.sigi.tolerantreader.TolerantReader;
import ch.sigi.tolerantreader.document.Document;
import ch.sigi.tolerantreader.document.json.JsonDocument;
import ch.sigi.tolerantreader.exception.TolerantReaderException;
import ch.sigi.tolerantreader.model.Model;
import ch.sigi.tolerantreader.model.ModelWithJsonPathAnnotations;
import ch.sigi.tolerantreader.parser.TolerantReaderBaseTest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.input.CharSequenceInputStream;
import org.junit.Assert;
import org.junit.Test;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class TolerantJsonReaderTest extends TolerantReaderBaseTest {

    @Test
    public void testAModelWithAnnotations() {
        Model model = exampleModel();
        InputStream is = objectToInputStream(model);

        try {
            ModelWithJsonPathAnnotations parsedModel = TolerantReader.Builder
                    .defaultSettings()
                    .build()
                    .read(documentFor(is, ModelWithJsonPathAnnotations.class));

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
        return JsonDocument.Builder
                .forClass(clazz)
                .withInputStream(is)
                .build();
    }

    @Override
    protected InputStream objectToInputStream(Object object) {
        return toJsonStringInputStream(object);
    }

    private InputStream toJsonStringInputStream(Object instance) {
        String xml = null;
        try {
            xml = objectToJson(instance);
            System.out.println(xml);
        } catch (JsonProcessingException e) {
            Assert.fail("Exception during marshalling: " + e.getMessage());
        }
        return toStringInputStream(xml);
    }

    private InputStream toStringInputStream(String str) {
        return new CharSequenceInputStream(str, StandardCharsets.UTF_8);
    }

    private <T> String objectToJson(T o) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(o);
    }

}
