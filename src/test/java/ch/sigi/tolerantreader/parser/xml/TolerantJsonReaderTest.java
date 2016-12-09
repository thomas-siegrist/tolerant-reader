/*
 * Copyright (C) Schweizerische Bundesbahnen SBB, 2016.
 */

package ch.sigi.tolerantreader.parser.xml;

import ch.sigi.tolerantreader.exception.TolerantReaderException;
import ch.sigi.tolerantreader.model.*;
import ch.sigi.tolerantreader.parser.json.TolerantJsonReader;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.input.CharSequenceInputStream;
import org.junit.Assert;
import org.junit.Test;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class TolerantJsonReaderTest {

    @Test
    public void testCongruentModel() throws TolerantReaderException {
        Model model = exampleModel();
        InputStream is = toJsonStringInputStream(model);

            Model parsedModel = TolerantJsonReader.Builder
                    .defaultSettings()
                    .build()
                    .read(is, Model.class);

            Assert.assertEquals(model, parsedModel);

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

    private <T> String objectToJson(T o) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(o);
    }

}
