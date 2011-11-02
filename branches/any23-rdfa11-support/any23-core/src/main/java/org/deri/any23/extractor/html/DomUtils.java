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

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * This class provides utility methods for DOM manipulation.
 * It is separated from {@link HTMLDocument} so that its methods
 * can be run on single DOM nodes without having to wrap them
 * into an HTMLDocument.
 * We use a mix of XPath and DOM manipulation.
 * <p/>
 * This is likely to be a performance bottleneck but at least
 * everything is localized here.
 * <p/>
 */
public class DomUtils {

    private static final String[] EMPTY_STRING_ARRAY = new String[0];
        
    private final static XPath xPathEngine = XPathFactory.newInstance().newXPath();

    private DomUtils(){}

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
     * Does a reverse walking of the DOM tree to generate a unique XPath
     * expression leading to this node. The XPath generated is the canonical
     * one based on sibling index: /html[1]/body[1]/div[2]/span[3] etc..
     *
     * @param node the input node.
     * @return the XPath location of node as String.
     */
    // TODO: TEXT nodes are ignored, this causes issues in error reporting.
    public static String getXPathForNode(Node node) {
        String index = "";
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            int successors = 1;
            Node previous = node.getPreviousSibling();
            while (null != previous) {
                if (previous.getNodeType() == Node.ELEMENT_NODE
                        && previous.getNodeName().equals(node.getNodeName())) {
                    successors++;
                }
                previous = previous.getPreviousSibling();
            }
            index = "/" + node.getNodeName() + "[" + successors + "]";
        }
        Node parent = node.getParentNode();
        if (null == parent)
            return index;
        else
            return getXPathForNode(parent) + index;
    }

    /**
     * Returns a list of tag names representing the path from
     * the document root to the given node <i>n</i>.
     *
     * @param n the node for which retrieve the path.
     * @return a sequence of HTML tag names.
     */
    public static String[] getXPathListForNode(Node n) {
        if(n == null) {
            return EMPTY_STRING_ARRAY;
        }
        List<String> ancestors = new ArrayList<String>();
        ancestors.add( String.format("%s[%s]", n.getNodeName(), getIndexInParent(n) ) );
        Node parent = n.getParentNode();
        while(parent != null) {
            ancestors.add(0, String.format("%s[%s]", parent.getNodeName(), getIndexInParent(parent) ) );
            parent = parent.getParentNode();
        }
        return ancestors.toArray( new String[ancestors.size()] );
    }

    /**
     * Returns the row/col location of the given node.
     *
     * @param n input node.
     * @return an array of two elements of type
     *         <code>[&lt;begin-row&gt;, &lt;begin-col&gt;, &lt;end-row&gt; &lt;end-col&gt;]</code>
     *         or <code>null</code> if not possible to extract such data.
     */
    public static int[] getNodeLocation(Node n) {
        if(n == null) throw new NullPointerException("node cannot be null.");
        final TagSoupParser.ElementLocation elementLocation =
            (TagSoupParser.ElementLocation) n.getUserData( TagSoupParser.ELEMENT_LOCATION );
        if(elementLocation == null) return null;
        return new int[]{
                elementLocation.getBeginLineNumber(),
                elementLocation.getBeginColumnNumber(),
                elementLocation.getEndLineNumber(),
                elementLocation.getEndColumnNumber()
        };
    }

    /**
     * Checks whether a node is ancestor or same of another node.
     *
     * @param candidateAncestor the candidate ancestor node.
     * @param candidateSibling the candidate sibling node.
     * @param strict if <code>true</code> is not allowed that the ancestor and sibling can be the same node.
     * @return <code>true</code> if <code>candidateSibling</code> is ancestor of <code>candidateSibling</code>,
     *         <code>false</code> otherwise.
     */
    public static boolean isAncestorOf(Node candidateAncestor, Node candidateSibling, boolean strict) {
        if(candidateAncestor == null) throw new NullPointerException("candidate ancestor cannot be null null.");
        if(candidateSibling  == null) throw new NullPointerException("candidate sibling cannot be null null." );
        if(strict && candidateAncestor.equals(candidateSibling)) return false;
        Node parent = candidateSibling;
        while(parent != null) {
            if(parent.equals(candidateAncestor)) return true;
            parent = parent.getParentNode();
        }
        return false;
    }

    /**
     * Checks whether a node is ancestor or same of another node. As
     * {@link #isAncestorOf(org.w3c.dom.Node, org.w3c.dom.Node, boolean)} with <code>strict=false</code>.
     *
     * @param candidateAncestor the candidate ancestor node.
     * @param candidateSibling the candidate sibling node.
     * @return <code>true</code> if <code>candidateSibling</code> is ancestor of <code>candidateSibling</code>,
     *         <code>false</code> otherwise.
     */
    public static boolean isAncestorOf(Node candidateAncestor, Node candidateSibling) {
        return isAncestorOf(candidateAncestor, candidateSibling, false);
    }

    /**
     * Finds all nodes that have a declared class.
     * Note that the className is transformed to lower case before being
     * matched against the DOM.
     * @param root the root node from which start searching.
     * @param className the name of the filtered class.
     * @return list of matching nodes or an empty list.
     */
    public static List<Node> findAllByClassName(Node root, String className) {
        return findAllByTagAndClassName(root, "*", className.toLowerCase());
    }

    /**
     * Finds all nodes that have a declared attribute.
     * Note that the className is transformed to lower case before being
     * matched against the DOM.
     * @param root the root node from which start searching.
     * @param attrName the name of the filtered attribue.
     * @return list of matching nodes or an empty list.
     */
    public static List<Node> findAllByAttributeName(Node root, String attrName) {
        List<Node> result = new ArrayList<Node>();
        for (Node node : findAll(root, String.format("./descendant-or-self::*[@%s]", attrName) ) ) {
                result.add(node);
        }
        return result;
    }

    public static List<Node> findAllByTag(Node root, String tagName) {
        List<Node> result = new ArrayList<Node>();
        for (Node node : findAll(root, "./descendant-or-self::" + tagName)) {
            result.add(node);
        }
        return result;
    }

    public static List<Node> findAllByTagAndClassName(Node root, String tagName, String className) {
        List<Node> result = new ArrayList<Node>();
        for (Node node : findAll(
                root,
                "./descendant-or-self::" +
                tagName +
                "[contains(translate(@class,'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'),'" +
                className + "')]")
        ) {
            if (DomUtils.hasClassName(node, className)) {
                result.add(node);
            }
        }
        return result;
    }

    /**
     * Mimics the JS DOM API, or prototype's $()
     */
    public static Node findNodeById(Node root, String id) {
        Node node;
        try {
            String xpath = "//*[@id='" + id + "']";
            node = (Node) xPathEngine.evaluate(xpath, root, XPathConstants.NODE);
        } catch (XPathExpressionException ex) {
            throw new RuntimeException("Should not happen", ex);
        }
        return node;
    }

    /**
     * Returns a NodeList composed of all the nodes that match an XPath
     * expression, which must be valid.
     */
    public static List<Node> findAll(Node node, String xpath) {
        if(node == null) {
            throw new NullPointerException("node cannot be null.");
        }
        try {
            NodeList nodes = (NodeList) xPathEngine.evaluate(xpath, node, XPathConstants.NODESET);
            List<Node> result = new ArrayList<Node>(nodes.getLength());
            for (int i = 0; i < nodes.getLength(); i++) {
                result.add(nodes.item(i));
            }
            return result;
        } catch (XPathExpressionException ex) {
            throw new IllegalArgumentException("Illegal XPath expression: " + xpath, ex);
        }
    }

    /**
     * Gets the string value of an XPath expression.
     */
    public static String find(Node node, String xpath) {
        try {
            String val = (String) xPathEngine.evaluate(xpath, node, XPathConstants.STRING);
            if (null == val)
                return "";
            return val;
        } catch (XPathExpressionException ex) {
            throw new IllegalArgumentException("Illegal XPath expression: " + xpath, ex);
        }
    }

    /**
     * Tells if an element has a class name <b>not checking the parents
     * in the hierarchy</b> mimicking the <i>CSS</i> .foo match.
     */
    public static boolean hasClassName(Node node, String className) {
        return hasAttribute(node, "class", className);
    }

    /**
     * Checks the presence of an attribute value in attributes that
     * contain whitespace-separated lists of values. The semantic is the
     * CSS classes' ones: "foo" matches "bar foo", "foo" but not "foob"
     */
    public static boolean hasAttribute(Node node, String attributeName, String className) {
        // regex love, maybe faster but less easy to understand
		// Pattern pattern = Pattern.compile("(^|\\s+)"+className+"(\\s+|$)");
        String attr = readAttribute(node, attributeName);
        for (String c : attr.split("\\s+"))
            if (c.equalsIgnoreCase(className))
                return true;
        return false;
    }

     /**
     * Checks the presence of an attribute in the given <code>node</code>.
      *
      * @param node the node container.
      * @param attributeName the name of the attribute.
      */
    public static boolean hasAttribute(Node node, String attributeName) {
        return readAttribute(node, attributeName, null) != null;
    }

    /**
     * Verifies if the given target node is an element.
     *
     * @param target
     * @return <code>true</code> if the element the node is an element,
     *         <code>false</code> otherwise.
     */
    public static boolean isElementNode(Node target) {
        return Node.ELEMENT_NODE == target.getNodeType();
    }

    /**
     * Reads the value of the specified <code>attribute</code>, returning the
     * <code>defaultValue</code> string if not present.
     *
     * @param node node to read the attribute.
     * @param attribute attribute name.
     * @param defaultValue the default value to return if attribute is not found.
     * @return the attribute value or <code>defaultValue</code> if not found.
     */
    public static String readAttribute(Node node, String attribute, String defaultValue) {
        NamedNodeMap attributes = node.getAttributes();
        if (null == attributes)
            return defaultValue;
        Node attr = attributes.getNamedItem(attribute);
        if (null==attr)
			return defaultValue;
		return attr.getNodeValue();
	}

    /**
     * Reads the value of the first <i>attribute</i> which name matches with the specified <code>attributePrefix</code>.
     * Returns the <code>defaultValue</code> if not found.
     *
     * @param node node to look for attributes.
     * @param attributePrefix attribute prefix.
     * @param defaultValue default returned value.
     * @return the value found or default.
     */
    public static String readAttributeWithPrefix(Node node, String attributePrefix, String defaultValue) {
        final NamedNodeMap attributes = node.getAttributes();
        if (null == attributes) {
            return defaultValue;
        }
        Node attribute;
        for (int a = 0; a < attributes.getLength(); a++) {
            attribute = attributes.item(a);
            if (attribute.getNodeName().startsWith(attributePrefix)) {
                return attribute.getNodeValue();
            }
        }
        return defaultValue;
    }

    /**
     * Reads the value of an <code>attribute</code>, returning the
     * empty string if not present.
     *
     * @param node node to read the attribute.
     * @param attribute attribute name.
     * @return the attribute value or <code>""</code> if not found.
     */
    public static String readAttribute(Node node, String attribute) {
        return readAttribute(node, attribute, "");
    }

    /**
     * Given a <i>DOM</i> {@link Node} produces the <i>XML</i> serialization
     * omitting the <i>XML declaration</i>.
     *
     * @param node node to be serialized.
     * @param indent if <code>true</code> the output is indented.
     * @return the XML serialization.
     * @throws TransformerException if an error occurs during the
     *         serializator initialization and activation.
     * @throws java.io.IOException
     */
    public static String serializeToXML(Node node, boolean indent) throws TransformerException, IOException {
        final DOMSource domSource = new DOMSource(node);
        final Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        if(indent) {
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        }
        final StringWriter sw = new StringWriter();
        final StreamResult sr = new StreamResult(sw);
        transformer.transform(domSource, sr);
        sw.close();
        return sw.toString();
    }

}
