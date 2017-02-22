package ch.sigi.tolerantreader;

import ch.sigi.tolerantreader.document.Document;
import ch.sigi.tolerantreader.document.json.JsonDocument;
import ch.sigi.tolerantreader.document.xml.XmlDocument;
import ch.sigi.tolerantreader.exception.TolerantReaderException;

import java.io.InputStream;

/**
 * Created by thomas on 22.02.17.
 */
public class TolerantReader {

    /**
     * Read the given InputStream-XML with the default-settings for the Document and the TolerantReader.
     * For Customizing, please use the TolerantReader#customize() method.
     */
    public static <T> T readXml(InputStream is, Class<T> model) throws TolerantReaderException {
        return customize().build().read(
                xmlDocumentFor(is, model)
        );
    }

    private static <T> Document<T> xmlDocumentFor(InputStream is, Class<T> clazz) {
        return XmlDocument.Builder
                .forClass(clazz)
                .withInputStream(is)
                .build();
    }

    /**
     * Read the given InputStream-Json with the default-settings for the Document and the TolerantReader.
     * For Customizing, please use the static TolerantReader#customize() method.
     */
    public static <T> T readJson(InputStream is, Class<T> model) throws TolerantReaderException {
        return customize().build().read(
                jsonDocumentFor(is, model)
        );
    }

    private static <T> Document<T> jsonDocumentFor(InputStream is, Class<T> clazz) {
        return JsonDocument.Builder
                .forClass(clazz)
                .withInputStream(is)
                .build();
    }

    private static CustomizedTolerantReader.Builder customize() {
        return CustomizedTolerantReader.Builder.defaultSettings();
    }

}
