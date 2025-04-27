package com.comicboys.project.utility;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import static org.junit.jupiter.api.Assertions.*;

class XMLNodeInserterTest {
    Document doc;
    Node comic = null;
    @BeforeEach
    void setUp() {
        doc = XMLFileManager.loadXMLFromFile("assets/blueprint/test/specification_test.xml");
        assertNotNull(doc);
        NodeList comicElements = doc.getElementsByTagName("comic");
        comic = null;
        // find valid comic node
        for (int i = 0; i < comicElements.getLength(); i++) {
            comic = comicElements.item(i);
            if (comic.getNodeType() == Node.ELEMENT_NODE) {
                break;
            }
        }
        // ensure is valid comic element
        assertNotNull(comic);
        assertEquals(Node.ELEMENT_NODE, comic.getNodeType());
    }
    @Test
    void testInsertFirstChild() {
        NodeList testElements = ((Element) comic).getElementsByTagName("test");
        assertEquals(0, testElements.getLength());

        Node test = doc.createElement("test");
        comic.appendChild(test);
        XMLNodeInserter.insertFirstChild(comic, test);

        testElements = ((Element) comic).getElementsByTagName("test");
        // will be length 2 because of also the text node
        assertEquals(2, testElements.getLength());

        // check for first element node that it is <test> and not something else
        for (int i = 0; i < testElements.getLength(); i++) {
            test = testElements.item(i);
            if (test.getNodeType() == Node.ELEMENT_NODE) {
                assertEquals("test", test.getNodeName());
                break;
            }
        }
    }
    @Test
    void testAppendElement() {
        Document otherDoc = XMLFileManager.loadXMLFromString(
                "<example>" +
                        "I am an example" +
                        "</example>"
        );
        assertTrue(otherDoc.getDocumentElement().getTextContent().contains("I am an example"));
        NodeList exampleNodes = otherDoc.getElementsByTagName("example");
        Node exampleNode = null;
        for (int i = 0; i < exampleNodes.getLength(); i++) {
            exampleNode = exampleNodes.item(i);
            if (exampleNode.getNodeType() == Node.ELEMENT_NODE) {
                break;
            }
        }
        assertNotNull(exampleNode);
        XMLNodeInserter.appendElement(doc, exampleNode, "comic");
    }
    @Test
    void testAppendElements() {
        Document otherDoc = XMLFileManager.loadXMLFromString(
                "<container>" +
                        "<example>" +
                        "I am example 1!" +
                        "</example>" +
                        "<example>" +
                        "I am example 2!" +
                        "</example>" +
                        "</container>"
        );
        assertTrue(otherDoc.getDocumentElement().getTextContent().contains("I am example 1!"));
        assertTrue(otherDoc.getDocumentElement().getTextContent().contains("I am example 2!"));
        NodeList exampleNodes = otherDoc.getElementsByTagName("example");
        assertNotNull(exampleNodes);
        XMLNodeInserter.appendElements(doc, exampleNodes, "comic");
    }
}
