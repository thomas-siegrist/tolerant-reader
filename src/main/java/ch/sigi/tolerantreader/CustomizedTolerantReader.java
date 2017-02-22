/*
 * Copyright (C) Schweizerische Bundesbahnen SBB, 2016.
 */

package ch.sigi.tolerantreader;

import ch.sigi.tolerantreader.annotation.CustomName;
import ch.sigi.tolerantreader.annotation.CustomPath;
import ch.sigi.tolerantreader.document.Document;
import ch.sigi.tolerantreader.document.json.JsonDocument;
import ch.sigi.tolerantreader.document.xml.XmlDocument;
import ch.sigi.tolerantreader.exception.DocumentReadException;
import ch.sigi.tolerantreader.exception.TolerantReaderException;
import ch.sigi.tolerantreader.exception.ValidationException;
import ch.sigi.tolerantreader.model.Node;
import ch.sigi.tolerantreader.validation.Validator;
import ch.sigi.tolerantreader.validation.impl.DefaultValidator;
import com.jayway.jsonpath.PathNotFoundException;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CustomizedTolerantReader {

    private final boolean validate;
    private final Validator validator;

    private CustomizedTolerantReader(boolean validate, Validator validator) {
        this.validate = validate;
        this.validator = validator;
    }

    public <T> T read(Document<T> document) throws TolerantReaderException {

        Class<T> clazz = document.getModelClass();

        try {

            T instance = clazz.getConstructor().newInstance();

            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                String path = document.getRootNodeIdentifier() + field.getName();
                Node node = Node.Builder.forField(field).build();
                Object value = readNodeForObject(document, path, node);
                validate(value, node);

                clazz.getMethod(setterFor(field), field.getType()).invoke(instance, nullSafeCast(field.getType(), value));
            }

            return instance;

        } catch (Throwable t) {
            throw new TolerantReaderException(t);
        }
    }

    private Object readNodeForObject(Document document, String path, Node node)
            throws TolerantReaderException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, PathNotFoundException, DocumentReadException,
            ValidationException {

        Class nodeType = node.getType();
        if (nodeType.isPrimitive())
            throw new TolerantReaderException("Mate, common!! Don't use primitive types (" + nodeType.getSimpleName()
                    + ") in a TolerantReader Model-Class. Every field must be nullable in order to be tolerant. Agree ?  ;-)");

        path = overrideWithValuesFromAnnotationsIfAny(document, path, node);

        if (!document.pathExists(path))
            return null;

        if (isStandardJavaClassOrCollection(node.getType())) {
            Object nodeValue = document.evaluatePath(path, node);

            if (nodeValue != null && Collection.class.isAssignableFrom(nodeType)) {
                Collection collection = new ArrayList<>();
                List<?> nodes = (List) nodeValue;
                int firstIndex = document.getIndexOfFirstElementInCollections();
                for (int i = firstIndex; i < nodes.size() + firstIndex; i++) {
                    Node nodeWithinCollection = Node.Builder.forTypeAndName(node.getGenericType(), node.getName()).build();
                    collection.add(readNodeForObject(document, path + "[" + i + "]", nodeWithinCollection));
                }
                return collection;
            } else {
                return nodeValue;
            }

        }

        return recursiveProcessSubtree(document, path, nodeType);
    }

    private void validate(Object nodeValue, Node node) throws ValidationException {
        if (validate)
            validator.validate(nodeValue, node);
    }

    private boolean isStandardJavaClassOrCollection(Class type) {
        return Number.class.isAssignableFrom(type) ||
                String.class.isAssignableFrom(type) ||
                Byte.class.isAssignableFrom(type) ||
                Boolean.class.isAssignableFrom(type) ||
                Character.class.isAssignableFrom(type) ||
                Collection.class.isAssignableFrom(type);
    }

    private <T> T recursiveProcessSubtree(Document document, String path, Class<T> returnType)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, TolerantReaderException, DocumentReadException, ValidationException {
        T instance = returnType.getConstructor().newInstance();
        Field[] fields = returnType.getDeclaredFields();
        for (Field field : fields) {
            String pathToSubtree = path + document.getPathDelimiter() + field.getName();
            Node node = Node.Builder.forField(field).build();
            Object value = readNodeForObject(document, pathToSubtree, node);
            returnType.getMethod(setterFor(field), field.getType()).invoke(instance, nullSafeCast(field.getType(), value));
        }
        return instance;
    }

    protected String overrideWithValuesFromAnnotationsIfAny(Document document, String path, Node node) {
        CustomPath customPath = node.getCustomPath();
        CustomName customName = node.getCustomName();
        if (customPath != null) {
            path = customPath.value();
        } else if (customName != null) {
            path = path.substring(0, path.lastIndexOf(document.getPathDelimiter()) + 1) + customName.value();
        }
        return path;
    }

    protected String setterFor(Field field) {
        return "set" + firstLetterUppercased(field.getName());
    }

    protected String firstLetterUppercased(String string) {
        String firstCharacter = string.substring(0, 1);
        return string.replaceFirst(firstCharacter, firstCharacter.toUpperCase());
    }

    protected <T> T nullSafeCast(Class<T> clazz, Object value) {
        return value == null ? null : clazz.cast(value);
    }

    /**
     * ******************************************************************************************************
     * The Builder (for config-options)
     * ******************************************************************************************************
     */
    public static final class Builder {

        private boolean validate;
        private Validator validator;

        public Builder(boolean validate, DefaultValidator validator) {
            this.validate = validate;
            this.validator = validator;
        }

        public static Builder defaultSettings() {
            return new Builder(true, new DefaultValidator());
        }

        public Builder validate(boolean validate) {
            this.validate = validate;
            return this;
        }

        public Builder validator(Validator validator) {
            this.validator = validator;
            return this;
        }

        public CustomizedTolerantReader build() {
            return new CustomizedTolerantReader(validate, validator);
        }

    }

}
