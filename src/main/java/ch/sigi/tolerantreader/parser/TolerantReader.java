package ch.sigi.tolerantreader.parser;

import ch.sigi.tolerantreader.annotation.CustomName;
import ch.sigi.tolerantreader.annotation.CustomPath;
import ch.sigi.tolerantreader.exception.TolerantReaderException;
import ch.sigi.tolerantreader.model.Node;

import java.io.InputStream;
import java.lang.reflect.Field;

/**
 * Created by thomas on 02.12.16.
 */
public abstract class TolerantReader {

    protected final String pathDelimiter;

    public TolerantReader(String pathDelimiter) {
        this.pathDelimiter = pathDelimiter;
    }

    public abstract <T> T read(InputStream is, Class<T> clazz) throws TolerantReaderException;

    protected <T> String rootElementName(Class<T> clazz) {
        String rootElementName;
        CustomName[] customNames = clazz.getAnnotationsByType(CustomName.class);
        if (customNames == null || customNames.length == 0)
            rootElementName = clazz.getSimpleName();
        else {
            rootElementName = customNames[0].value();
        }
        return rootElementName.toLowerCase();
    }

    protected String overrideWithValuesFromAnnotationsIfAny(String path, Node node) {
        CustomPath customPath = node.getCustomPath();
        CustomName customName = node.getCustomName();
        if (customPath != null) {
            path = customPath.value();
        } else if (customName != null) {
            path = path.substring(0, path.lastIndexOf(pathDelimiter) + 1) + customName.value();
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

}
