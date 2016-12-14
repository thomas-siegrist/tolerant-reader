package ch.sigi.tolerantreader.document.json;

import ch.sigi.tolerantreader.document.Document;
import ch.sigi.tolerantreader.exception.DocumentReadException;
import ch.sigi.tolerantreader.model.Node;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;

import java.io.InputStream;
import java.util.Collection;

/**
 * Created by thomas on 10.12.16.
 */
public class JsonDocument<T> implements Document<T> {

    private static final String STATRT_OF_EXPRESSION = "$.";
    private static final String PATH_DELIMITER = ".";
    private static final Integer INDEX_OF_FIRST_ELEMENT_IN_COLLECTIONS = 0;

    private final DocumentContext documentContext;
    private final Class clazz;

    public JsonDocument(DocumentContext documentContext, Class<T> clazz) {
        this.documentContext = documentContext;
        this.clazz = clazz;
    }

    @Override
    public Object evaluatePath(String path, Node node) throws DocumentReadException {

        Object nodeValue = evaluateJsonPath(documentContext, path, node);
        Class nodeType = node.getType();

        if (nodeValue == null)
            return null;

        if (Integer.class.isAssignableFrom(nodeType)) {
            return ((Number) nodeValue).intValue();
        }
        if (Short.class.isAssignableFrom(nodeType)) {
            return ((Number) nodeValue).shortValue();
        }
        if (Long.class.isAssignableFrom(nodeType)) {
            return ((Number) nodeValue).longValue();
        }
        if (Double.class.isAssignableFrom(nodeType)) {
            return ((Number) nodeValue).doubleValue();
        }
        if (Float.class.isAssignableFrom(nodeType)) {
            return ((Number) nodeValue).floatValue();
        }
        if (Byte.class.isAssignableFrom(nodeType) ||
                String.class.isAssignableFrom(nodeType) ||
                Boolean.class.isAssignableFrom(nodeType) ||
                Character.class.isAssignableFrom(nodeType)) {
            return nodeValue;
        }

        // In the following case, a List is returned:
        if (Collection.class.isAssignableFrom(nodeType)) {
            return nodeValue;
        }

        throw new DocumentReadException("Only Standard Java Types and Collection.class are allowed in leaf-nodes.");
    }

    @Override
    public boolean pathExists(String path) {
        try {
            return documentContext.read(path) != null;
        } catch (PathNotFoundException e) {
            return false;
        }
    }

    @Override
    public String getRootNodeIdentifier() {
        return STATRT_OF_EXPRESSION;
    }

    @Override
    public String getPathDelimiter() {
        return PATH_DELIMITER;
    }

    @Override
    public int getIndexOfFirstElementInCollections() {
        return INDEX_OF_FIRST_ELEMENT_IN_COLLECTIONS;
    }

    @Override
    public Class<T> getModelClass() {
        return clazz;
    }

    private static Object evaluateJsonPath(DocumentContext jsonDocument, String jsonPath, Node node) {
        try {
            return jsonDocument.read(jsonPath, node.getType());
        } catch (PathNotFoundException e) {
            return null;
        }
    }

    /**
     * ******************************************************************************************************
     * The Builder
     * ******************************************************************************************************
     */
    public static final class Builder {
        private Class clazz;

        private Builder(Class clazz) {
            this.clazz = clazz;
        }

        public static Builder forClass(Class clazz) {
            return new Builder(clazz);
        }

        public BuilderForInputStream withInputStream(InputStream inputStream) {
            return new BuilderForInputStream(clazz, inputStream);
        }

        // Nested inner static class for Fluent-API:
        public static final class BuilderForInputStream {

            private Class clazz;
            private InputStream inputStream;
            private Configuration configuration;

            private BuilderForInputStream(Class clazz, InputStream inputStream) {
                this.clazz = clazz;
                this.inputStream = inputStream;
                this.configuration = Configuration.defaultConfiguration();
            }

            public BuilderForInputStream configuration(Configuration configuration) {
                this.configuration = configuration;
                return this;
            }

            public JsonDocument build() {
                DocumentContext documentContext = JsonPath.parse(inputStream, configuration);
                return new JsonDocument(documentContext, clazz);
            }
        }
    }

}
