package ch.sigi.tolerantreader.parser;

import java.io.InputStream;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import ch.sigi.tolerantreader.TolerantReader;
import ch.sigi.tolerantreader.document.Document;
import ch.sigi.tolerantreader.exception.TolerantReaderException;
import ch.sigi.tolerantreader.exception.ValidationException;
import ch.sigi.tolerantreader.model.Model;
import ch.sigi.tolerantreader.model.ModelSubset;
import ch.sigi.tolerantreader.model.ModelSuperset;
import ch.sigi.tolerantreader.model.ModelSupersetWithValidations;
import ch.sigi.tolerantreader.model.ModelWithInvalidAnnotations;
import ch.sigi.tolerantreader.model.SubTree;

/**
 * Created by thomas on 11.12.16.
 */
public abstract class TolerantReaderBaseTest {

    protected abstract <T> Document<T> documentFor(InputStream is, Class<T> clazz);

    protected abstract InputStream objectToInputStream(Object object);

    @Test
    public void testCongruentModel() {
        Model model = exampleModel();
        InputStream is = objectToInputStream(model);

        try {
            Model parsedModel = TolerantReader.Builder
                    .defaultSettings()
                    .build()
                    .read(documentFor(is, Model.class));

            Assert.assertEquals(model, parsedModel);

        } catch (TolerantReaderException e) {
            Assert.fail("Exception in Tolerant Reader: " + e.getMessage());
        }
    }

    @Test
    public void testASupersetOfTheModel() {
        Model model = exampleModel();
        InputStream is = objectToInputStream(model);

        try {
            ModelSuperset parsedModel = TolerantReader.Builder
                    .defaultSettings()
                    .build()
                    .read(documentFor(is, ModelSuperset.class));

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
        InputStream is = objectToInputStream(model);

        try {
            ModelSubset parsedModel = TolerantReader.Builder
                    .defaultSettings()
                    .build()
                    .read(documentFor(is, ModelSubset.class));

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

    @Test(expected = TolerantReaderException.class)
    public void testAModelWithInvalidAnnotations() throws TolerantReaderException {
        Model model = exampleModel();
        InputStream is = objectToInputStream(model);

        TolerantReader.Builder
                .defaultSettings()
                .build()
                .read(documentFor(is, ModelWithInvalidAnnotations.class));
    }

    @Test
    public void testAModelWithSuccessfulValidations() throws TolerantReaderException {
        Model model = exampleModel();
        InputStream is = objectToInputStream(model);

        try {
            TolerantReader.Builder
                    .defaultSettings()
                    .build()
                    .read(documentFor(is, ModelSupersetWithValidations.class));
        } catch (TolerantReaderException e) {
            Assert.fail("Exception in Tolerant Reader: " + e.getMessage());
        }
    }

    @Test(expected = ValidationException.class)
    public void testAModelWithNotFulfilledNotNullValidation() throws Throwable {
        Model model = exampleModel();

        // Invalidate the Content of the Model
        model.setSomeBoolean(null);

        InputStream is = objectToInputStream(model);

        try {
            TolerantReader.Builder
                    .defaultSettings()
                    .build()
                    .read(documentFor(is, ModelSupersetWithValidations.class));
        } catch (TolerantReaderException e) {
            e.printStackTrace();
            throw e.getCause();
        }
    }

    @Test(expected = ValidationException.class)
    public void testAModelWithNotFulfilledRegexValidation() throws Throwable {
        Model model = exampleModel();

        // Invalidate the Content of the Model
        model.setSomeText(";Some$Invalid&Characters;");

        InputStream is = objectToInputStream(model);

        try {
            TolerantReader.Builder
                    .defaultSettings()
                    .build()
                    .read(documentFor(is, ModelSupersetWithValidations.class));
        } catch (TolerantReaderException e) {
            e.printStackTrace();
            throw e.getCause();
        }
    }

    protected Model exampleModel() {
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

}
