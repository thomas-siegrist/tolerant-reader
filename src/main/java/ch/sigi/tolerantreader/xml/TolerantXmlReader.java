/*
 * Copyright (C) Schweizerische Bundesbahnen SBB, 2016.
 */

package ch.sigi.tolerantreader.xml;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import ch.sigi.tolerantreader.exception.TolerantReaderException;

import com.sun.org.apache.xml.internal.dtm.ref.DTMNodeList;

public class TolerantXmlReader {

    public static <T> T read(InputStream is, Class<T> clazz) throws TolerantReaderException {

        try {
            Document dom = createDomFor(is);

            T instance = clazz.getConstructor().newInstance();

            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                String xPath = "/" + clazz.getSimpleName().toLowerCase() + "/" + field.getName();
                Object value = readForObject(dom, xPath, field.getType(), getGenericTypeClass(field));

                clazz.getMethod(setterFor(field), field.getType()).invoke(instance, nullSafeCast(field.getType(), value));
            }

            return instance;

        } catch (Throwable t) {
            throw new TolerantReaderException(t);
        }
    }

    private static Document createDomFor(InputStream is) throws IOException, SAXException, ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(is);
    }

    private static Class getGenericTypeClass(Field field) {
        Type returnType = field.getGenericType();
        if (returnType instanceof ParameterizedType) {
            ParameterizedType paramType = (ParameterizedType) returnType;
            Type[] argTypes = paramType.getActualTypeArguments();
            if (argTypes.length > 0) {
                return (Class<?>) argTypes[0];
            }
        }
        return null;
    }

    private static <T> Object readForObject(Document dom, String xPath, Class<T> returnType, Class<T> genericTypeClazz) throws TolerantReaderException, XPathExpressionException,
            InvocationTargetException, NoSuchMethodException, ClassNotFoundException, InstantiationException, IllegalAccessException {

        if (returnType.isPrimitive())
            throw new TolerantReaderException("Mate, common!! Don't use primitive types (" + returnType.getSimpleName()
                    + ") in a TolerantReader Model-Class. Every field must be nullable in order to be tolerant. Agree ?  ;-)");

        XPathExpression xPathExpression = XPathFactory.newInstance().newXPath().compile(xPath);

        if (Boolean.class.isAssignableFrom(returnType)) {
            return Boolean.valueOf((String) xPathExpression.evaluate(dom, XPathConstants.STRING));
        }
        if (Number.class.isAssignableFrom(returnType)) {
            Double doubleValue = (Double) xPathExpression.evaluate(dom, XPathConstants.NUMBER);
            if (Long.class.isAssignableFrom(returnType)) {
                return doubleValue == null ? null : doubleValue.longValue();
            }
            if (Integer.class.isAssignableFrom(returnType)) {
                return doubleValue == null ? null : doubleValue.intValue();
            }
            if (Short.class.isAssignableFrom(returnType)) {
                return doubleValue == null ? null : doubleValue.shortValue();
            }
            if (Float.class.isAssignableFrom(returnType)) {
                return doubleValue == null ? null : doubleValue.floatValue();
            }
            // Default for Numbers is Double.class
            return doubleValue;
        }
        if (String.class.isAssignableFrom(returnType)) {
            return xPathExpression.evaluate(dom, XPathConstants.STRING);
        }
        if (Collection.class.isAssignableFrom(returnType)) {
            Collection collection = new ArrayList<>();
            DTMNodeList nodes = (DTMNodeList) xPathExpression.evaluate(dom, XPathConstants.NODESET);
            for (int i = 1; i <= nodes.getLength(); i++) {
                collection.add(readForObject(dom, xPath + "[" + i + "]", genericTypeClazz, null));
            }
            return collection;
        }

        return recursiveProcessSubtree(dom, xPath, returnType);
    }

    private static <T> T recursiveProcessSubtree(Document dom, String xPath, Class<T> returnType) throws InstantiationException, IllegalAccessException, InvocationTargetException,
            NoSuchMethodException,
            XPathExpressionException, ClassNotFoundException, TolerantReaderException {
        T instance = returnType.getConstructor().newInstance();
        Field[] fields = returnType.getDeclaredFields();
        for (Field field : fields) {
            String xPathToSubtree = xPath + "/" + field.getName();
            Object value = readForObject(dom, xPathToSubtree, field.getType(), getGenericTypeClass(field));
            returnType.getMethod(setterFor(field), field.getType()).invoke(instance, nullSafeCast(field.getType(), value));
        }
        return instance;
    }

    private static String setterFor(Field field) {
        return "set" + firstLetterUppercased(field.getName());
    }

    private static String firstLetterUppercased(String string) {
        String firstCharacter = string.substring(0, 1);
        return string.replaceFirst(firstCharacter, firstCharacter.toUpperCase());
    }

    private static <T> T nullSafeCast(Class<T> clazz, Object value) {
        return value == null ? null : clazz.cast(value);
    }

}
