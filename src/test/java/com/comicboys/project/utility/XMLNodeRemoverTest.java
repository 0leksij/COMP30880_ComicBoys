package com.comicboys.project.utility;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import static org.junit.jupiter.api.Assertions.*;

// this by extension also tests XMLNodeRemover
class XMLNodeRemoverTest {
    @Test
    void testRemoveAllByTag() {
        Document doc = XMLFileManager.loadXMLFromFile("assets/blueprint/test/specification_test.xml");
        assertNotNull(doc);
        NodeList children = doc.getChildNodes();
        for (int i = 0 ; i < children.getLength(); i++) {
            Node child = children.item(i);
            XMLFileManager.removeAllByTag(child, "scene");
            NodeList foundElements = doc.getElementsByTagName("scene");
            assertEquals(0, foundElements.getLength());
        }
    }
    // you dont actually need to test whether it is a node or list of children passed in because if it is a node the
    // method taking in a node will just get list of node children and pass that into the method that takes in a list
    // due to method overloading written to allow for a node to be passed in, assumes function on its list of children
    @Test
    void testRemoveFirstChild() {
        Document doc = XMLFileManager.loadXMLFromFile("assets/story/audio_test/story_one_panel_two_characters.xml");
        assertNotNull(doc);
        // corresponds to children of <scene>
        NodeList documentChildren = doc.getDocumentElement().getElementsByTagName("scene").item(0).getChildNodes();
        // looking through and removing its first child, so should remove only <figures>, making its length 2 -> 1
        for (int i = 0 ; i < documentChildren.getLength(); i++) {
            Node panelNode = documentChildren.item(i);
            if (panelNode.getNodeType() == Node.ELEMENT_NODE) {
                NodeList panelChildren = panelNode.getChildNodes();
                // some code to remove non-element nodes to simplify test and allow length to only take element nodes into account
                for (int j = 0; j < panelChildren.getLength(); j++) {
                    Node panelChild = panelChildren.item(j);
                    if (panelChild.getNodeType() != Node.ELEMENT_NODE) {
                        panelChild.getParentNode().removeChild(panelChild);
                    }
                }
                // should be left with just element nodes left, right, setting and below (need to check at least 3)
                assertEquals(4, panelChildren.getLength());
                NodeList foundLeft = doc.getElementsByTagName("left");
                NodeList foundRight = doc.getElementsByTagName("right");
                NodeList foundSetting = doc.getElementsByTagName("setting");
                NodeList foundBelow = doc.getElementsByTagName("below");
                assertEquals(1, foundLeft.getLength());
                assertEquals(1, foundRight.getLength());
                assertEquals(1, foundSetting.getLength());
                assertEquals(1, foundBelow.getLength());

                // remove first child, which should be <left>
                XMLNodeRemover.removeFirstChild(panelNode);

                assertEquals(3, panelChildren.getLength());
                foundLeft = doc.getElementsByTagName("left");
                foundRight = doc.getElementsByTagName("right");
                foundSetting = doc.getElementsByTagName("setting");
                foundBelow = doc.getElementsByTagName("below");
                assertEquals(0, foundLeft.getLength());
                assertEquals(1, foundRight.getLength());
                assertEquals(1, foundSetting.getLength());
                assertEquals(1, foundBelow.getLength());
            }
        }
    }
    @Test
    void testRemoveAllChildren() {
        Document doc = XMLFileManager.loadXMLFromFile("assets/blueprint/test/specification_test.xml");
        assertNotNull(doc);
        NodeList children = doc.getChildNodes();
        XMLFileManager.removeAllChildren(doc);
        assertEquals(0, children.getLength()); // variable updated
        assertEquals(0, doc.getChildNodes().getLength()); // variable was pointer, original children have all been demolished
    }
    @Test
    void testRemoveNthFirstChild() {
        Document doc = XMLFileManager.loadXMLFromFile("assets/story/audio_test/story_one_panel_two_characters.xml");
        assertNotNull(doc);
        // corresponds to children of <scene>
        NodeList documentChildren = doc.getDocumentElement().getElementsByTagName("scene").item(0).getChildNodes();
        // looking through and removing its first child, so should remove only <figures>, making its length 2 -> 1
        for (int i = 0 ; i < documentChildren.getLength(); i++) {
            Node panelNode = documentChildren.item(i);
            if (panelNode.getNodeType() == Node.ELEMENT_NODE) {
                NodeList panelChildren = panelNode.getChildNodes();
                // some code to remove non-element nodes to simplify test and allow length to only take element nodes into account
                for (int j = 0; j < panelChildren.getLength(); j++) {
                    Node panelChild = panelChildren.item(j);
                    if (panelChild.getNodeType() != Node.ELEMENT_NODE) {
                        panelChild.getParentNode().removeChild(panelChild);
                    }
                }
                // should be left with just element nodes left, right, setting and below (need to check at least 3)
                assertEquals(4, panelChildren.getLength());
                NodeList foundLeft = doc.getElementsByTagName("left");
                NodeList foundRight = doc.getElementsByTagName("right");
                NodeList foundSetting = doc.getElementsByTagName("setting");
                NodeList foundBelow = doc.getElementsByTagName("below");
                assertEquals(1, foundLeft.getLength());
                assertEquals(1, foundRight.getLength());
                assertEquals(1, foundSetting.getLength());
                assertEquals(1, foundBelow.getLength());

                // remove first child, which should be <left>
                int nthChild = 1;
                XMLNodeRemover.removeNthChild(panelNode, nthChild);

                assertEquals(3, panelChildren.getLength());
                foundLeft = doc.getElementsByTagName("left");
                foundRight = doc.getElementsByTagName("right");
                foundSetting = doc.getElementsByTagName("setting");
                foundBelow = doc.getElementsByTagName("below");
                assertEquals(0, foundLeft.getLength());
                assertEquals(1, foundRight.getLength());
                assertEquals(1, foundSetting.getLength());
                assertEquals(1, foundBelow.getLength());
            }
        }
    }
    @Test
    void testRemoveNthSecondChild() {
        Document doc = XMLFileManager.loadXMLFromFile("assets/story/audio_test/story_one_panel_two_characters.xml");
        assertNotNull(doc);
        // corresponds to children of <scene>
        NodeList documentChildren = doc.getDocumentElement().getElementsByTagName("scene").item(0).getChildNodes();
        // looking through and removing its first child, so should remove only <figures>, making its length 2 -> 1
        for (int i = 0 ; i < documentChildren.getLength(); i++) {
            Node panelNode = documentChildren.item(i);
            if (panelNode.getNodeType() == Node.ELEMENT_NODE) {
                NodeList panelChildren = panelNode.getChildNodes();
                // some code to remove non-element nodes to simplify test and allow length to only take element nodes into account
                for (int j = 0; j < panelChildren.getLength(); j++) {
                    Node panelChild = panelChildren.item(j);
                    if (panelChild.getNodeType() != Node.ELEMENT_NODE) {
                        panelChild.getParentNode().removeChild(panelChild);
                    }
                }
                // should be left with just element nodes left, right, setting and below (need to check at least 3)
                assertEquals(4, panelChildren.getLength());
                NodeList foundLeft = doc.getElementsByTagName("left");
                NodeList foundRight = doc.getElementsByTagName("right");
                NodeList foundSetting = doc.getElementsByTagName("setting");
                NodeList foundBelow = doc.getElementsByTagName("below");
                assertEquals(1, foundLeft.getLength());
                assertEquals(1, foundRight.getLength());
                assertEquals(1, foundSetting.getLength());
                assertEquals(1, foundBelow.getLength());

                // remove second child, which should be <right>
                int nthChild = 2;
                XMLNodeRemover.removeNthChild(panelNode, nthChild);

                assertEquals(3, panelChildren.getLength());
                foundLeft = doc.getElementsByTagName("left");
                foundRight = doc.getElementsByTagName("right");
                foundSetting = doc.getElementsByTagName("setting");
                foundBelow = doc.getElementsByTagName("below");
                assertEquals(1, foundLeft.getLength());
                assertEquals(0, foundRight.getLength());
                assertEquals(1, foundSetting.getLength());
                assertEquals(1, foundBelow.getLength());
            }
        }
    }
    @Test
    void testRemoveNthThirdChild() {
        Document doc = XMLFileManager.loadXMLFromFile("assets/story/audio_test/story_one_panel_two_characters.xml");
        assertNotNull(doc);
        // corresponds to children of <scene>
        NodeList documentChildren = doc.getDocumentElement().getElementsByTagName("scene").item(0).getChildNodes();
        // looking through and removing its first child, so should remove only <figures>, making its length 2 -> 1
        for (int i = 0 ; i < documentChildren.getLength(); i++) {
            Node panelNode = documentChildren.item(i);
            if (panelNode.getNodeType() == Node.ELEMENT_NODE) {
                NodeList panelChildren = panelNode.getChildNodes();
                // some code to remove non-element nodes to simplify test and allow length to only take element nodes into account
                for (int j = 0; j < panelChildren.getLength(); j++) {
                    Node panelChild = panelChildren.item(j);
                    if (panelChild.getNodeType() != Node.ELEMENT_NODE) {
                        panelChild.getParentNode().removeChild(panelChild);
                    }
                }
                // should be left with just element nodes left, right, setting and below (need to check at least 3)
                assertEquals(4, panelChildren.getLength());
                NodeList foundLeft = doc.getElementsByTagName("left");
                NodeList foundRight = doc.getElementsByTagName("right");
                NodeList foundSetting = doc.getElementsByTagName("setting");
                NodeList foundBelow = doc.getElementsByTagName("below");
                assertEquals(1, foundLeft.getLength());
                assertEquals(1, foundRight.getLength());
                assertEquals(1, foundSetting.getLength());
                assertEquals(1, foundBelow.getLength());

                // remove third child, which should be <setting>
                int nthChild = 3;
                XMLNodeRemover.removeNthChild(panelNode, nthChild);

                assertEquals(3, panelChildren.getLength());
                foundLeft = doc.getElementsByTagName("left");
                foundRight = doc.getElementsByTagName("right");
                foundSetting = doc.getElementsByTagName("setting");
                foundBelow = doc.getElementsByTagName("below");
                assertEquals(1, foundLeft.getLength());
                assertEquals(1, foundRight.getLength());
                assertEquals(0, foundSetting.getLength());
                assertEquals(1, foundBelow.getLength());
            }
        }
    }
    @Test
    void testRemoveNChildren() {
        Document doc = XMLFileManager.loadXMLFromFile("assets/story/audio_test/story_one_panel_two_characters.xml");
        assertNotNull(doc);
        // corresponds to children of <scene>
        NodeList documentChildren = doc.getDocumentElement().getElementsByTagName("scene").item(0).getChildNodes();
        // looking through and removing its first child, so should remove only <figures>, making its length 2 -> 1
        for (int i = 0 ; i < documentChildren.getLength(); i++) {
            Node panelNode = documentChildren.item(i);
            if (panelNode.getNodeType() == Node.ELEMENT_NODE) {
                NodeList panelChildren = panelNode.getChildNodes();
                // some code to remove non-element nodes to simplify test and allow length to only take element nodes into account
                for (int j = 0; j < panelChildren.getLength(); j++) {
                    Node panelChild = panelChildren.item(j);
                    if (panelChild.getNodeType() != Node.ELEMENT_NODE) {
                        panelChild.getParentNode().removeChild(panelChild);
                    }
                }
                // should be left with just element nodes left, right, setting and below (need to check at least 3)
                assertEquals(4, panelChildren.getLength());
                NodeList foundLeft = doc.getElementsByTagName("left");
                NodeList foundRight = doc.getElementsByTagName("right");
                NodeList foundSetting = doc.getElementsByTagName("setting");
                NodeList foundBelow = doc.getElementsByTagName("below");
                assertEquals(1, foundLeft.getLength());
                assertEquals(1, foundRight.getLength());
                assertEquals(1, foundSetting.getLength());
                assertEquals(1, foundBelow.getLength());

                // remove first 2 children, so in this case should remove <left> and <right>
                XMLNodeRemover.removeNChildren(panelNode, 2);

                assertEquals(2, panelChildren.getLength());
                foundLeft = doc.getElementsByTagName("left");
                foundRight = doc.getElementsByTagName("right");
                foundSetting = doc.getElementsByTagName("setting");
                foundBelow = doc.getElementsByTagName("below");
                assertEquals(0, foundLeft.getLength());
                assertEquals(0, foundRight.getLength());
                assertEquals(1, foundSetting.getLength());
                assertEquals(1, foundBelow.getLength());
            }
        }
    }
    @Test
    void testRemoveNthChildren() {
        Document doc = XMLFileManager.loadXMLFromFile("assets/story/audio_test/story_one_panel_two_characters.xml");
        assertNotNull(doc);
        // corresponds to children of <scene>
        NodeList documentChildren = doc.getDocumentElement().getElementsByTagName("scene").item(0).getChildNodes();
        // looking through and removing its first child, so should remove only <figures>, making its length 2 -> 1
        for (int i = 0 ; i < documentChildren.getLength(); i++) {
            Node panelNode = documentChildren.item(i);
            if (panelNode.getNodeType() == Node.ELEMENT_NODE) {
                NodeList panelChildren = panelNode.getChildNodes();
                // some code to remove non-element nodes to simplify test and allow length to only take element nodes into account
                for (int j = 0; j < panelChildren.getLength(); j++) {
                    Node panelChild = panelChildren.item(j);
                    if (panelChild.getNodeType() != Node.ELEMENT_NODE) {
                        panelChild.getParentNode().removeChild(panelChild);
                    }
                }
                // should be left with just element nodes left, right, setting and below (need to check at least 3)
                assertEquals(4, panelChildren.getLength());
                NodeList foundLeft = doc.getElementsByTagName("left");
                NodeList foundRight = doc.getElementsByTagName("right");
                NodeList foundSetting = doc.getElementsByTagName("setting");
                NodeList foundBelow = doc.getElementsByTagName("below");
                assertEquals(1, foundLeft.getLength());
                assertEquals(1, foundRight.getLength());
                assertEquals(1, foundSetting.getLength());
                assertEquals(1, foundBelow.getLength());

                // remove 2 nodes starting from 2nd node, so should remove 2nd and 3rd node, i.e. <right> and <setting>
                XMLNodeRemover.removeNthChildren(panelNode, 2, 2);

                assertEquals(2, panelChildren.getLength());
                foundLeft = doc.getElementsByTagName("left");
                foundRight = doc.getElementsByTagName("right");
                foundSetting = doc.getElementsByTagName("setting");
                foundBelow = doc.getElementsByTagName("below");
                assertEquals(1, foundLeft.getLength());
                assertEquals(0, foundRight.getLength());
                assertEquals(0, foundSetting.getLength());
                assertEquals(1, foundBelow.getLength());
            }
        }
    }
}