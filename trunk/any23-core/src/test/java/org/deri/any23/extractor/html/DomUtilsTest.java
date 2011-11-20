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

import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.util.List;

/**
 * Reference test class for the {@link org.deri.any23.extractor.html.DomUtils} class.
 * @author Davide Palmisano (dpalmisano@gmail.com)
 */
public class DomUtilsTest {

    private final static XPath xPathEngine = XPathFactory.newInstance().newXPath();

    @Test
    public void testGetXPathForNode() {
        check(
                "microformats/hcard/01-tantek-basic.html",
                "//DIV[@class='vcard']",
                "/HTML[1]/BODY[1]/DIV[1]"
        );
        check(
                "microformats/hcard/02-multiple-class-names-on-vcard.html",
                "//SPAN[@class='fn n']",
                "/HTML[1]/BODY[1]/DIV[1]/SPAN[1]"
        );
        check(
                "microformats/hcard/02-multiple-class-names-on-vcard.html",
                "//SPAN/SPAN[@class='fn n']",
                "/HTML[1]/BODY[1]/P[1]/SPAN[1]/SPAN[1]"
        );
        check(
                "microformats/hcard/02-multiple-class-names-on-vcard.html",
                "//SPAN/SPAN/*[@class='given-name']",
                "/HTML[1]/BODY[1]/P[1]/SPAN[1]/SPAN[1]/SPAN[1]"
        );
        check(
                "microformats/hcard/02-multiple-class-names-on-vcard.html",
                "//SPAN/SPAN/*[@class='family-name']",
                "/HTML[1]/BODY[1]/P[1]/SPAN[1]/SPAN[1]/SPAN[2]"
        );
    }

    @Test
    public void testFindAllByClassName() {
        Node dom = new HTMLFixture("microformats/hcard/02-multiple-class-names-on-vcard.html").getDOM();
        Assert.assertNotNull(dom);
        List<Node> nodes = DomUtils.findAllByClassName(dom, "vcard");
        NodeList nodeList = null;
        try {
            nodeList = (NodeList) xPathEngine.evaluate(
                    "//*[contains(@class, 'vcard')]",
                    dom,
                    XPathConstants.NODESET
            );
        } catch (XPathExpressionException e) {
            Assert.fail(e.getMessage());
        }
        Assert.assertNotNull(nodeList);
        Assert.assertEquals("vcard elements number does not match", nodes.size(), nodeList.getLength());
        for(int i=0; i<nodeList.getLength(); i++) {
            Assert.assertTrue(nodes.contains(nodeList.item(i)));
        }
    }

    @Test
	public void testFindAllByTag() {
        Node dom = new HTMLFixture("microformats/hcard/02-multiple-class-names-on-vcard.html").getDOM();
        Assert.assertNotNull(dom);
        List<Node> nodes = DomUtils.findAllByTag(dom, "SPAN");
        NodeList nodeList = null;
        try {
            nodeList = (NodeList) xPathEngine.evaluate(
                    "./descendant-or-self::SPAN",
                    dom,
                    XPathConstants.NODESET
            );
        } catch (XPathExpressionException e) {
            Assert.fail(e.getMessage());
        }

        Assert.assertEquals("number of elements does not match", nodes.size(), nodeList.getLength());

        for (int i = 0; i < nodeList.getLength(); i++) {
            Assert.assertTrue(nodes.contains(nodeList.item(i)));
        }
    }

    @Test
    public void testFindAllByTagAndClassName() {
        Node dom = new HTMLFixture("microformats/hcard/02-multiple-class-names-on-vcard.html").getDOM();
        Assert.assertNotNull(dom);
        List<Node> nodes = DomUtils.findAllByTagAndClassName(dom, "SPAN", "family-name");
        NodeList nodeList = null;
        try {
            nodeList = (NodeList) xPathEngine.evaluate(
                    "./descendant-or-self::SPAN[contains(@class,'family-name')]",
                    dom,
                    XPathConstants.NODESET
            );
        } catch (XPathExpressionException e) {
            Assert.fail(e.getMessage());
        }

        Assert.assertEquals("number of elements does not match", nodes.size(), nodeList.getLength());

        for (int i = 0; i < nodeList.getLength(); i++) {
            Assert.assertTrue(nodes.contains(nodeList.item(i)));
        }

    }

    @Test
    public void testHasClassName() {
        Node dom = new HTMLFixture("microformats/hcard/02-multiple-class-names-on-vcard.html").getDOM();
        Assert.assertNotNull(dom);
        List<Node> nodes = DomUtils.findAllByClassName(dom, "vcard");
        for(Node node : nodes) {
            Assert.assertTrue(DomUtils.hasClassName(node, "vcard"));
        }
    }

    @Test
	public void testReadAttribute() {
        Node dom = new HTMLFixture("microformats/hcard/02-multiple-class-names-on-vcard.html").getDOM();
        Assert.assertNotNull(dom);
        List<Node> nodes = DomUtils.findAllByClassName(dom, "vcard");
        for(Node node : nodes) {
            // every node in nodes should have a class attribute containing vcard.
            Assert.assertTrue(DomUtils.readAttribute(node, "class").contains("vcard"));
        }

    }

    @Test
    public void testSerializeToXML() throws ParserConfigurationException, TransformerException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        DOMImplementation impl = builder.getDOMImplementation();
        Document doc = impl.createDocument(null, null, null);
        Node n1 = doc.createElement("DIV");
        Node n2 = doc.createElement("SPAN");
        Node n3 = doc.createElement("P");
        n1.setTextContent("Content 1");
        n2.setTextContent("Content 2");
        n3.setTextContent("Content 3");
        n1.appendChild(n2);
        n2.appendChild(n3);
        doc.appendChild(n1);

        Assert.assertEquals(
                "<DIV>Content 1<SPAN>Content 2<P>Content 3</P></SPAN></DIV>",
                DomUtils.serializeToXML(doc, false)
        );
    }

    private void check(String file, String xpath, String reverseXPath) {
        Node dom = new HTMLFixture(file).getDOM();
        Assert.assertNotNull(dom);
        Node node;
        try {
            node = (Node) xPathEngine.evaluate(xpath, dom, XPathConstants.NODE);
            Assert.assertNotNull(node);
            Assert.assertEquals(Node.ELEMENT_NODE, node.getNodeType());
            String newPath = DomUtils.getXPathForNode(node);
            Assert.assertEquals(reverseXPath, newPath);
            Node newNode = (Node) xPathEngine.evaluate(newPath, dom, XPathConstants.NODE);
            Assert.assertEquals(node, newNode);

        } catch (XPathExpressionException ex) {
            Assert.fail(ex.getMessage());
        }
    }

}
