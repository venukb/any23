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

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
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

    private final static XPath xPathEngine = XPathFactory.newInstance().newXPath();

    /**
     * Does a reverse walking of the DOM tree to generate a unique XPath
     * expression leading to this node. The XPath generated is the canonical
     * one based on sibling index: /html[1]/body[1]/div[2]/span[3] etc..
     */
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
     * Finds all nodes that have a declared class.
     * Note that the className is transformed to lower case before being
     * matched against the DOM.
     */
    public static List<Node> findAllByClassName(Node root, String className) {
        return findAllByTagAndClassName(root, "*", className.toLowerCase());
    }

    public static List<Node> findAllByTag(Node root, String tagName) {
        return findAllByTagAndClassName(root, tagName, "");
    }

    public static List<Node> findAllByTagAndClassName(Node root, String tagName, String className) {
        List<Node> result = new ArrayList<Node>();
        for (Node node : findAll(root, "./descendant-or-self::" + tagName + "[contains(@class,'" + className + "')]")) {
            if (DomUtils.hasClassName(node, className)) {
                // add this
                result.add(node);
            /*
             * to mimic the inheritance of CSS classes we should recur here
             *
             * Only one level of depth is considered though, so take care.
             */
//				// add childNodes
//				NodeList children = node.getChildNodes();
//				for(int j=0;j<children.getLength();j++){
//					Node child = children.item(j);
//					if (child.getNodeType()==Node.ELEMENT_NODE)
//						result.add(child);
//				}
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
     * in the hierarchy</b> mimicking the CSS .foo match.
     * <p/>
     * TODO Find all class checks throughout the code and use this
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
//		Pattern pattern = Pattern.compile("(^|\\s+)"+className+"(\\s+|$)");
        String attr = readAttribute(node, attributeName);
        for (String c : attr.split("\\s+"))
            if (c.equals(className))
                return true;
        return false;
    }

    public static boolean isElementNode(Node target) {
        return Node.ELEMENT_NODE == target.getNodeType();
    }

    /**
     * Reads the value of an attribute, returning an empty string
     * instead of null if it is not present.
     */
    public static String readAttribute(Node document, String attribute) {
        NamedNodeMap attributes = document.getAttributes();
        if (null == attributes)
            return "";
        Node attr = attributes.getNamedItem(attribute);
        if (null==attr)
			return "";
		return attr.getNodeValue();
	}
    
}