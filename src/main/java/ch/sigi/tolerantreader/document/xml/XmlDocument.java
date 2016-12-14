package ch.sigi.tolerantreader.document.xml;

import ch.sigi.tolerantreader.annotation.CustomName;
import ch.sigi.tolerantreader.document.Document;
import ch.sigi.tolerantreader.exception.DocumentReadException;
import ch.sigi.tolerantreader.model.Node;
import com.sun.org.apache.xml.internal.dtm.ref.DTMNodeList;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by thomas on 11.12.16.
 */
public class XmlDocument<T> implements Document<T> {

    public static final String STATRT_OF_EXPRESSION = "/";
    public static final String PATH_DELIMITER = "/";
    private static final Integer INDEX_OF_FIRST_ELEMENT_IN_COLLECTIONS = 1;

    private final String rootElementName;
    private final org.w3c.dom.Document dom;
    private String encoding;
    private Class<T> clazz;

    public XmlDocument(org.w3c.dom.Document dom, String rootElementName, String encoding, Class<T> clazz) {
        this.rootElementName = rootElementName;
        this.dom = dom;
        this.encoding = encoding;
        this.clazz = clazz;
    }

    @Override
    public Object evaluatePath(String path, Node node) throws DocumentReadException {

        try {
            XPathExpression xPathExpression = compileXPath(path);
            Class nodeType = node.getType();

            if (!xPathExists(dom, xPathExpression))
                return null;

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
                if (Double.class.isAssignableFrom(nodeType)) {
                    return doubleValue;
                }
            }
            if (Boolean.class.isAssignableFrom(nodeType)) {
                return Boolean.valueOf((String) xPathExpression.evaluate(dom, XPathConstants.STRING));
            }
            if (String.class.isAssignableFrom(nodeType)) {
                return xPathExpression.evaluate(dom, XPathConstants.STRING);
            }
            if (Character.class.isAssignableFrom(nodeType)) {
                return extractStringAsCharacter(xPathExpression);
            }
            if (Byte.class.isAssignableFrom(nodeType)) {
                return extractStringAsBytes(xPathExpression);
            }
            if (Collection.class.isAssignableFrom(nodeType)) {
                Object nodeValue = xPathExpression.evaluate(dom, XPathConstants.NODESET);
                NodeList nodeList = nullSafeCast(NodeList.class, nodeValue);
                return toList(nodeList);
            }
        } catch (XPathExpressionException e) {
            throw new DocumentReadException(e);
        }

        throw new DocumentReadException("Only Standard Java Types and Collection.class are allowed in leaf-nodes.");
    }

    @Override
    public boolean pathExists(String path) {
        XPathExpression xPathExpression = null;
        try {
            xPathExpression = compileXPath(path);
            DTMNodeList nodeList = (DTMNodeList) xPathExpression.evaluate(dom, XPathConstants.NODESET);
            return nodeList != null && nodeList.getLength() > 0;
        } catch (XPathExpressionException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public String getRootNodeIdentifier() {
        return STATRT_OF_EXPRESSION + rootElementName + PATH_DELIMITER;
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

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    private XPathExpression compileXPath(String xPath) throws XPathExpressionException {
        return XPathFactory.newInstance().newXPath().compile(xPath);
    }

    private boolean xPathExists(org.w3c.dom.Document dom, XPathExpression xPathExpression) throws XPathExpressionException {
        DTMNodeList nodeList = (DTMNodeList) xPathExpression.evaluate(dom, XPathConstants.NODESET);
        return nodeList != null && nodeList.getLength() > 0;
    }

    private Character extractStringAsCharacter(XPathExpression xPathExpression) throws XPathExpressionException, DocumentReadException {
        String str = (String) xPathExpression.evaluate(dom, XPathConstants.STRING);
        if (StringUtils.isNotBlank(str) && str.length() > 1) {
            throw new DocumentReadException("Character-Field can only be of length 0 or 1!");
        }
        return str == null ? null : str.charAt(0);
    }

    private Byte extractStringAsBytes(XPathExpression xPathExpression) throws XPathExpressionException, DocumentReadException {
        String str = (String) xPathExpression.evaluate(dom, XPathConstants.STRING);
        if (StringUtils.isNotBlank(str) && str.length() > 1) {
            throw new DocumentReadException("Byte-Field can only be of length 0 or 1!");
        }

        try {
            return str == null ? null : str.getBytes(encoding)[0];
        } catch (UnsupportedEncodingException e) {
            throw new DocumentReadException(e);
        }
    }

    private static List<Object> toList(NodeList list) {
        List<Object> nodes = new ArrayList<>();

        for (int n = 0; n < list.getLength(); ++n) {
            nodes.add(list.item(n));
        }

        return nodes;
    }

    private <T> T nullSafeCast(Class<T> clazz, Object value) {
        return value == null ? null : clazz.cast(value);
    }

    /**
     * ******************************************************************************************************
     * The Builder
     * ******************************************************************************************************
     */
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

            private InputStream inputStream;
            private Class<?> clazz;
            private String encoding;


            private BuilderForInputStream(Class<?> clazz, InputStream inputStream) {
                this.inputStream = inputStream;
                this.clazz = clazz;
                this.encoding = "UTF-8";
            }

            public BuilderForInputStream encoding(String encoding) {
                this.encoding = encoding;
                return this;
            }

            public XmlDocument build() {
                try {
                    org.w3c.dom.Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputStream);
                    return new XmlDocument(document, rootElementNameForClass(), encoding, clazz);
                } catch (Throwable t) {
                    throw new RuntimeException(t);
                }
            }

            private <T> String rootElementNameForClass() {
                String rootElementName;
                CustomName[] customNames = clazz.getAnnotationsByType(CustomName.class);
                if (customNames == null || customNames.length == 0)
                    rootElementName = clazz.getSimpleName();
                else {
                    rootElementName = customNames[0].value();
                }
                return rootElementName.toLowerCase();
            }

        }
    }

}
