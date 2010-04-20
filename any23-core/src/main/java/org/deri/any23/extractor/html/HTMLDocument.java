/*
 * Copyright 2008-2010 Digital Enterprise Research Institute (DERI)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.deri.any23.extractor.html;

import org.deri.any23.extractor.ExtractionException;
import org.deri.any23.rdf.Any23ValueFactoryWrapper;
import org.deri.any23.rdf.RDFUtility;
import org.openrdf.model.URI;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * A wrapper around the DOM representation of an HTML document.
 * Provides convenience access to various parts of the document.
 *
 * @author Gabriele Renzi
 */
public class HTMLDocument {

    private final static XPath xPathEngine = XPathFactory.newInstance().newXPath();
    private final static Logger log        = LoggerFactory.getLogger(HTMLDocument.class);

    private static final String[] EMPTY_STRING_ARRAY = new String[0];

    private Node         document;
    private java.net.URI baseURI;

    private final Any23ValueFactoryWrapper valueFactory =
            new Any23ValueFactoryWrapper(ValueFactoryImpl.getInstance());

    /**
     * Reads a text field from the given node adding the content to the given <i>res</i> list.
     *
     * @param res list to add the content.
     * @param node the node from which read the content.
     */
    public static void readTextField(List<TextField> res, Node node) {
        final String name = node.getNodeName();
        final NamedNodeMap attributes = node.getAttributes();
        // excess of safety check, should be impossible
        if (null == attributes) {
            res.add( new TextField( node.getTextContent(), node) );
            return;
        }
        // first check if there are values inside
        List<Node> values = DomUtils.findAllByClassName(node, "value");
        if (!values.isEmpty()) {
            String val = "";
            for (Node n : values)
                val += n.getTextContent();
            res.add( new TextField( val.trim(), node) );
            return;
        }
        if ("ABBR".equals(name) && (null != attributes.getNamedItem("title"))) {
            res.add( new TextField(attributes.getNamedItem("title").getNodeValue(), node) );
        } else if ("A".equals(name)) {
            if (DomUtils.hasAttribute(node, "rel", "tag")) {

                String href = extractRelTag(attributes);
                res.add( new TextField(href, node) );
            } else
                res.add( new TextField(node.getTextContent(), node) );
        } else if ("IMG".equals(name) || "AREA".equals(name)) {
            res.add( new TextField(attributes.getNamedItem("alt").getNodeValue(), node) );
        } else {
            res.add( new TextField(node.getTextContent(), node) );
        }
    }

    /**
     * Reads an URL field from the given node adding the content to the given <i>res</i> list.
     *
     * @param res
     * @param node
     */
    public static void readUrlField(List<TextField> res, Node node) {
        String name = node.getNodeName();
        NamedNodeMap attributes = node.getAttributes();
        if (null == attributes) {
            res.add( new TextField(node.getTextContent(), node) );
            return;
        }
        if ("A".equals(name) || "AREA".equals(name)) {
            Node n = attributes.getNamedItem("href");
            res.add( new TextField(n.getNodeValue(), n) );
        } else if ("ABBR".equals(name)) {
            Node n = attributes.getNamedItem("title");
            res.add( new TextField(n.getNodeValue(), n) );
        } else if ("IMG".equals(name)) {
            Node n = attributes.getNamedItem("src");
            res.add( new TextField(n.getNodeValue(), n) );
        } else if ("OBJECT".equals(name)) {
            Node n = attributes.getNamedItem("data");
            res.add( new TextField(n.getNodeValue(), n) );
        } else {
            res.add( new TextField(node.getTextContent().trim(), node) );
        }
    }

    /**
     * Given a node this method returns the index corresponding to such node
     * within the list of the children of its parent node.
     *
     * @param n the node of which returning the index.
     * @return a non negative number.
     */
    public static int getIndexInParent(Node n) {
        Node parent = n.getParentNode();
        if(parent == null) {
            return 0;
        }
        NodeList nodes = parent.getChildNodes();
        int counter = -1;
        for(int i = 0; i < nodes.getLength(); i++) {
            Node current = nodes.item(i);
            if ( current.getNodeType() == n.getNodeType() && current.getNodeName().equals( n.getNodeName() ) ) {
                counter++;
            }
            if( current.equals(n) ) {
                return counter;
            }
        }
        throw new IllegalStateException("Cannot find a child within its parent node list.");
    }

    /**
     * Returns a list of tag names representing the path from
     * the document root to the given node <i>n</i>. 
     *
     * @param n the node for which retrieve the path.
     * @return a sequence of HTML tag names.
     */
    // TODO: move to DomUtils and merge with #getXPathForNode.
    public static String[] getPathFromRootToGivenNode(Node n) {
        if(n == null) {
            return EMPTY_STRING_ARRAY;
        }
        List<String> ancestors = new ArrayList<String>();
        ancestors.add( n.getNodeName() + getIndexInParent(n) );
        Node parent = n.getParentNode();
        while(parent != null) {
            ancestors.add(0, parent.getNodeName() + getIndexInParent(parent) );
            parent = parent.getParentNode();
        }
        return ancestors.toArray( new String[ancestors.size()] );
    }

    private static String extractRelTag(NamedNodeMap attributes) {
        String[] all = attributes.getNamedItem("href").getNodeValue().split("[#?]");
        //cleanup spurious segments
        String path = all[0];
        // get last
        all = path.split("/");
        return all[all.length - 1];
    }

    /**
     * Constructor accepting the root node.
     * 
     * @param document
     */
    public HTMLDocument(Node document) {
        if (null == document)
            throw new IllegalArgumentException("node cannot be null when constructing an HTMLDocument");
        this.document = document;
    }

    /**
     * @return An absolute URI, or null if the URI is not fixable
     * @throws ExtractionException If the base URI is invalid
     */
    public URI resolveURI(String uri) throws ExtractionException {
        return valueFactory.resolveURI(uri, getBaseURI());
    }

    public String find(String xpath) {
        return DomUtils.find(getDocument(), xpath);
    }

    public Node findNodeById(String id) {
        return DomUtils.findNodeById(getDocument(), id);
    }

    public List<Node> findAll(String xpath) {
        return DomUtils.findAll(getDocument(), xpath);
    }

    public String findMicroformattedValue(
            String objectTag,
            String object,
            String fieldTag,
            String field,
            String key
    ) {
        Node node = findMicroformattedObjectNode(objectTag, object);
        if (null == node)
            return "";
        // try to check if it is inline
        if (DomUtils.hasClassName(node, field))
            return node.getTextContent();

        // failed, try to find it in a child
        try {
            String xpath = ".//" + fieldTag + "[contains(@class, '" + field + "')]/" + key;
            String value = (String) xPathEngine.evaluate(xpath, node, XPathConstants.STRING);
            if (null == value) {
                return "";
            }
            return value;
        } catch (XPathExpressionException ex) {
            throw new RuntimeException("Should not happen, XPath expression is built locally", ex);
        }

    }

    public Node getDocument() {
        return document;
    }

    /**
     * Returns a singular text field. 
     *
     * @param className name of class containing text.
     * @return if multiple values are found just the first is returned,
     * if we want to check that there are no n-ary values use plural finder
     */
    public TextField getSingularTextField(String className) {
        TextField[] res = getPluralTextField(className);
        if (res.length == 0)
            return new TextField("", null);
        return res[0];
    }

    /**
     * Returns a plural text field.
     * 
     * @param className name of class node containing text.
     * @return list of fields.
     */
    public TextField[] getPluralTextField(String className) {
        List<TextField> res = new ArrayList<TextField>();
        List<Node> nodes = DomUtils.findAllByClassName(getDocument(), className);
        for (Node node : nodes)
            readTextField(res, node);
        return res.toArray( new TextField[res.size()] );
    }

    /**
     * Returns the URL associated to the field marked with class <i>className</i>.
     *
     * @param className name of node class containing the URL field.
     * @return if multiple values are found just the first is returned,
     *  if we want to check that there are no n-ary values use plural finder
     */
    public TextField getSingularUrlField(String className) {
        TextField[] res = getPluralUrlField(className);
        if (res.length < 1)
            return new TextField("", null);
        return res[0];
    }

    /**
     * Returns the list of URLs associated to the fields marked with class <i>className</i>.
     *
     * @param className name of node class containing the URL field.
     * @return
     */
    public TextField[] getPluralUrlField(String className) {
        List<TextField> res = new ArrayList<TextField>();
        List<Node> nodes = DomUtils.findAllByClassName(getDocument(), className);
        for (Node node : nodes)
            readUrlField(res, node);
        return res.toArray( new TextField[res.size()] );
    }

    public Node findMicroformattedObjectNode(String objectTag, String name) {
        List<Node> nodes = DomUtils.findAllByTagAndClassName(getDocument(), objectTag, name);
        if (nodes.isEmpty())
            return null;
        return nodes.get(0);
    }

    /**
     * Read an attribute avoiding NullPointerExceptions, if the attr is
     * missing it just returns an empty string.
     *
     * @param attribute the attribute name.
     * @return the string representing the attribute.
     */
    public String readAttribute(String attribute) {
        return DomUtils.readAttribute(getDocument(), attribute);
    }

    /**
     * Finds all the nodes by class name.
     *
     * @param clazz the class name.
     * @return list of matching nodes.
     */
    public List<Node> findAllByClassName(String clazz) {
        return DomUtils.findAllByClassName(getDocument(), clazz);
    }

    /**
     * Returns the text contained inside a node if leaf,
     * <code>null</code> otherwise.
     *
     * @return the text of a leaf node.
     */
    public String getText() {
        NodeList children = getDocument().getChildNodes();
        if(children.getLength() == 1 && children.item(0) instanceof Text) {
            return children.item(0).getTextContent();
        }
        return null;
    }

    /**
     * Returns the document default language.
     *
     * @return default language if any, <code>null</code> otherwise.
     */
    public String getDefaultLanguage() {
        final String xpathLanguageSelector = "/HTML";
        Node html;
        try {
            html = (Node) xPathEngine.evaluate(xpathLanguageSelector, document, XPathConstants.NODE);
        } catch (XPathExpressionException xpeee) {
            throw new IllegalStateException();
        }
        if (html == null) {
            return null;
        }
        Node langAttribute = html.getAttributes().getNamedItem("xml:lang");
        return langAttribute == null ? null : langAttribute.getTextContent();
    }

    /**
     * Returns the sequence of ancestors from the document root to the local root (document).
     *
     * @return a sequence of node names.
     */
    public String[] getPathToLocalRoot() {
        return getPathFromRootToGivenNode(document);
    }

    private java.net.URI getBaseURI() throws ExtractionException {
        if (baseURI == null) {
            try {
                if (document.getBaseURI() == null) {
                    log.warn("document.getBaseURI() is null, this should not happen");
                }
                baseURI = new java.net.URI(RDFUtility.fixAbsoluteURI(document.getBaseURI()));
            } catch (IllegalArgumentException ex) {
                throw new ExtractionException("Error in base URI: " + document.getBaseURI(), ex);
            } catch (URISyntaxException ex) {
                throw new ExtractionException("Error in base URI: " + document.getBaseURI(), ex);
            }
        }
        return baseURI;
    }

    /**
     * This class represents a text extracted from the <i>HTML</i> DOM related
     * to the node from which such test has been retrieved.
     */
    public static class TextField {
        private String value;
        private Node   source;

        public TextField(String value, Node source) {
            this.value = value;
            this.source = source;
        }

        public String value() {
            return value;
        }

        public Node source() {
            return source;
        }
    }

}
