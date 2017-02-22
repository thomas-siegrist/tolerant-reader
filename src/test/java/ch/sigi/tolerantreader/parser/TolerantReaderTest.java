package ch.sigi.tolerantreader.parser;

import ch.sigi.tolerantreader.TolerantReader;
import ch.sigi.tolerantreader.exception.TolerantReaderException;
import ch.sigi.tolerantreader.model.ExampleData;
import ch.sigi.tolerantreader.model.Model;
import ch.sigi.tolerantreader.util.ObjectStreamer;
import org.junit.Assert;
import org.junit.Test;

import java.io.InputStream;

/**
 * Created by thomas on 22.02.17.
 */
public class TolerantReaderTest {

    @Test
    public void testCongruentModelOverStaticDefaultMethod_Xml() {
        Model model = ExampleData.getModel();
        InputStream is = ObjectStreamer.objectToXmlInputStream(model);

        try {
            Model parsedModel = TolerantReader.readXml(is, Model.class);
            Assert.assertEquals(model, parsedModel);

        } catch (TolerantReaderException e) {
            Assert.fail("Exception in Tolerant Reader: " + e.getMessage());
        }
    }

    @Test
    public void testCongruentModelOverStaticDefaultMethod_Json() {
        Model model = ExampleData.getModel();
        InputStream is = ObjectStreamer.objectToJsonInputStream(model);

        try {
            Model parsedModel = TolerantReader.readJson(is, Model.class);
            Assert.assertEquals(model, parsedModel);

        } catch (TolerantReaderException e) {
            Assert.fail("Exception in Tolerant Reader: " + e.getMessage());
        }
    }

}
