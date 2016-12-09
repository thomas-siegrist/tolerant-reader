/*
 * Copyright (C) Schweizerische Bundesbahnen SBB, 2016.
 */

package ch.sigi.tolerantreader.parser.xml;

import ch.sigi.tolerantreader.annotation.CustomName;
import ch.sigi.tolerantreader.annotation.CustomPath;
import ch.sigi.tolerantreader.exception.TolerantReaderException;
import ch.sigi.tolerantreader.model.Node;
import ch.sigi.tolerantreader.parser.TolerantReader;
import com.sun.org.apache.xml.internal.dtm.ref.DTMNodeList;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;

public class TolerantXmlReader extends TolerantReader {

    public static final String STATRT_OF_EXPRESSION = "/";
    public static final String PATH_DELIMITER = "/";

    public TolerantXmlReader() {
        super(PATH_DELIMITER);
    }

    @Override
    public <T> T read(InputStream is, Class<T> clazz) throws TolerantReaderException {

        try {
            Document dom = createDomFor(is);
            String rootElementName = rootElementName(clazz);

            T instance = clazz.getConstructor().newInstance();

            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                String xPath = STATRT_OF_EXPRESSION + rootElementName + PATH_DELIMITER + field.getName();
                Node node = Node.Builder.forField(field).build();
                Object value = readNodeForObject(dom, xPath, node);

                clazz.getMethod(setterFor(field), field.getType()).invoke(instance, nullSafeCast(field.getType(), value));
            }

            return instance;

        } catch (Throwable t) {
            throw new TolerantReaderException(t);
        }
    }

    private Document createDomFor(InputStream is) throws IOException, SAXException, ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(is);
    }

    private <T> Object readNodeForObject(Document dom, String xPath, Node node) throws TolerantReaderException, XPathExpressionException,
            InvocationTargetException, NoSuchMethodException, ClassNotFoundException, InstantiationException, IllegalAccessException {

        Class nodeType = node.getType();
        if (nodeType.isPrimitive())
            throw new TolerantReaderException("Mate, common!! Don't use primitive types (" + nodeType.getSimpleName()
                    + ") in a TolerantReader Model-Class. Every field must be nullable in order to be tolerant. Agree ?  ;-)");

        xPath = overrideWithValuesFromAnnotationsIfAny(xPath, node);
        XPathExpression xPathExpression = compileXPath(xPath);

        if (!xPathExists(dom, xPathExpression))
            return null;

        if (Boolean.class.isAssignableFrom(nodeType)) {
            return Boolean.valueOf((String) xPathExpression.evaluate(dom, XPathConstants.STRING));
        }
        if (Number.class.isAssignableFrom(nodeType)) {
            Double doubleValue = (Double) xPathExpression.evaluate(dom, XPathConstants.NUMBER);
            if (Long.class.isAssignableFrom(nodeType)) {
                return doubleValue == null ? null : doubleValue.longValue();
            }
            if (Integer.class.isAssignableFrom(nodeType)) {
                return doubleValue == null ? null : doubleValue.intValue();
            }
            if (Short.class.isAssignableFrom(nodeType)) {
                return doubleValue == null ? null : doubleValue.shortValue();
            }
            if (Float.class.isAssignableFrom(nodeType)) {
                return doubleValue == null ? null : doubleValue.floatValue();
            }
            // Default for Numbers is Double.class
            return doubleValue;
        }
        if (String.class.isAssignableFrom(nodeType)) {
            return xPathExpression.evaluate(dom, XPathConstants.STRING);
        }
        if (Collection.class.isAssignableFrom(nodeType)) {
            Collection collection = new ArrayList<>();
            DTMNodeList nodes = (DTMNodeList) xPathExpression.evaluate(dom, XPathConstants.NODESET);
            for (int i = 1; i <= nodes.getLength(); i++) {
                Node nodeWithinCollection = Node.Builder.forType(node.getGenericType()).build();
                collection.add(readNodeForObject(dom, xPath + "[" + i + "]", nodeWithinCollection));
            }
            return collection;
        }

        return recursiveProcessSubtree(dom, xPath, nodeType);
    }

    private boolean xPathExists(Document dom, XPathExpression xPathExpression) throws XPathExpressionException {
        DTMNodeList nodeList = (DTMNodeList) xPathExpression.evaluate(dom, XPathConstants.NODESET);
        return nodeList != null && nodeList.getLength() > 0;
    }

    private <T> T recursiveProcessSubtree(Document dom, String xPath, Class<T> returnType) throws InstantiationException, IllegalAccessException, InvocationTargetException,
            NoSuchMethodException,
            XPathExpressionException, ClassNotFoundException, TolerantReaderException {
        T instance = returnType.getConstructor().newInstance();
        Field[] fields = returnType.getDeclaredFields();
        for (Field field : fields) {
            String xPathToSubtree = xPath + "/" + field.getName();
            Node node = Node.Builder.forField(field).build();
            Object value = readNodeForObject(dom, xPathToSubtree, node);
            returnType.getMethod(setterFor(field), field.getType()).invoke(instance, nullSafeCast(field.getType(), value));
        }
        return instance;
    }

    private XPathExpression compileXPath(String xPath) throws XPathExpressionException {
        return XPathFactory.newInstance().newXPath().compile(xPath);
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

        public TolerantXmlReader build() {
            return new TolerantXmlReader();
        }

    }

}
