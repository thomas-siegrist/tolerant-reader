/*
 * Copyright (C) Schweizerische Bundesbahnen SBB, 2016.
 */

package ch.sigi.tolerantreader.parser.json;

import ch.sigi.tolerantreader.exception.TolerantReaderException;
import ch.sigi.tolerantreader.model.Node;
import ch.sigi.tolerantreader.parser.TolerantReader;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TolerantJsonReader extends TolerantReader {

    public static final String STATRT_OF_EXPRESSION = "$.";
    public static final String PATH_DELIMITER = ".";

    public TolerantJsonReader() {
        super(PATH_DELIMITER);
    }

    @Override
    public <T> T read(InputStream is, Class<T> clazz) throws TolerantReaderException {

        try {
            DocumentContext jsonDocument = JsonPath.parse(is);

            String rootElementName = rootElementName(clazz);

            T instance = clazz.getConstructor().newInstance();

            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                String jsonPath = STATRT_OF_EXPRESSION + field.getName();
                Node node = Node.Builder.forField(field).build();
                Object value = readNodeForObject(jsonDocument, jsonPath, node);

                clazz.getMethod(setterFor(field), field.getType()).invoke(instance, nullSafeCast(field.getType(), value));
            }

            return instance;

        } catch (Throwable t) {
            throw new TolerantReaderException(t);
        }
    }

    private Object readNodeForObject(DocumentContext jsonDocument, String jsonPath, Node node) throws TolerantReaderException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, PathNotFoundException {

        Class nodeType = node.getType();
        if (nodeType.isPrimitive())
            throw new TolerantReaderException("Mate, common!! Don't use primitive types (" + nodeType.getSimpleName()
                    + ") in a TolerantReader Model-Class. Every field must be nullable in order to be tolerant. Agree ?  ;-)");

        jsonPath = overrideWithValuesFromAnnotationsIfAny(jsonPath, node);

        Object nodeValue = evaluateJsonPath(jsonDocument, jsonPath, node);
        if (nodeValue == null)
            return null;

        if (Integer.class.isAssignableFrom(node.getType())) {
            return ((Number) nodeValue).intValue();
        }
        if (Short.class.isAssignableFrom(node.getType())) {
            return ((Number) nodeValue).shortValue();
        }
        if (Long.class.isAssignableFrom(node.getType())) {
            return ((Number) nodeValue).longValue();
        }
        if (Double.class.isAssignableFrom(node.getType())) {
            return ((Number) nodeValue).doubleValue();
        }
        if (Float.class.isAssignableFrom(node.getType())) {
            return ((Number) nodeValue).floatValue();
        }
        if (Byte.class.isAssignableFrom(node.getType()) ||
                String.class.isAssignableFrom(node.getType()) ||
                Boolean.class.isAssignableFrom(node.getType()) ||
                Character.class.isAssignableFrom(node.getType())) {
            return nodeValue;
        }

        if (Collection.class.isAssignableFrom(nodeType)) {
            Collection collection = new ArrayList<>();
            List<?> nodes = (List) nodeValue;
            for (int i = 0; i < nodes.size(); i++) {
                Node nodeWithinCollection = Node.Builder.forType(node.getGenericType()).build();
                collection.add(readNodeForObject(jsonDocument, jsonPath + "[" + i + "]", nodeWithinCollection));
            }
            return collection;
        }

        return recursiveProcessSubtree(jsonDocument, jsonPath, nodeType);
    }

    private Object evaluateJsonPath(DocumentContext jsonDocument, String jsonPath, Node node) {
        try {
            return jsonDocument.read(jsonPath, node.getType());
        } catch (PathNotFoundException e) {
            return null;
        }
    }

    private <T> T recursiveProcessSubtree(DocumentContext jsonDocument, String path, Class<T> returnType) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, TolerantReaderException {
        T instance = returnType.getConstructor().newInstance();
        Field[] fields = returnType.getDeclaredFields();
        for (Field field : fields) {
            String pathToSubtree = path + PATH_DELIMITER + field.getName();
            Node node = Node.Builder.forField(field).build();
            Object value = readNodeForObject(jsonDocument, pathToSubtree, node);
            returnType.getMethod(setterFor(field), field.getType()).invoke(instance, nullSafeCast(field.getType(), value));
        }
        return instance;
    }

    /**
     * ******************************************************************************************************
     * The Builder (for config-options)
     * ******************************************************************************************************
     */
    public static final class Builder {

        public static Builder defaultSettings() {
            return new Builder();
        }

        // We don't habe config-options yet

        public TolerantJsonReader build() {
            return new TolerantJsonReader();
        }

    }

}
