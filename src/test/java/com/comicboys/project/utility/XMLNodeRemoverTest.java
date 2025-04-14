package com.comicboys.project.utility;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

// this by extension also tests XMLNodeRemover
class XMLNodeRemoverTest {


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
                NodeList foundBelow = doc.getElementsByTagName("below");
                assertEquals(1, foundLeft.getLength());
                assertEquals(1, foundRight.getLength());
                assertEquals(1, foundBelow.getLength());

                // remove first child, which should be <left>
                XMLNodeRemover.removeFirstChild(panelNode);

                assertEquals(3, panelChildren.getLength());
                foundLeft = doc.getElementsByTagName("left");
                foundRight = doc.getElementsByTagName("right");
                foundBelow = doc.getElementsByTagName("below");
                assertEquals(0, foundLeft.getLength());
                assertEquals(1, foundRight.getLength());
                assertEquals(1, foundBelow.getLength());
            }
        }
    }

    @Test
    void testRemoveFirstChildFromList() {
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
                NodeList foundBelow = doc.getElementsByTagName("below");
                assertEquals(1, foundLeft.getLength());
                assertEquals(1, foundRight.getLength());
                assertEquals(1, foundBelow.getLength());

                // remove first child, which should be <left>
                XMLNodeRemover.removeFirstChildFromList(panelChildren);

                assertEquals(3, panelChildren.getLength());
                foundLeft = doc.getElementsByTagName("left");
                foundRight = doc.getElementsByTagName("right");
                foundBelow = doc.getElementsByTagName("below");
                assertEquals(0, foundLeft.getLength());
                assertEquals(1, foundRight.getLength());
                assertEquals(1, foundBelow.getLength());
            }
        }
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
                NodeList foundBelow = doc.getElementsByTagName("below");
                assertEquals(1, foundLeft.getLength());
                assertEquals(1, foundRight.getLength());
                assertEquals(1, foundBelow.getLength());

                // remove first child, which should be <left>
                int nthChild = 1;
                XMLNodeRemover.removeNthChild(panelNode, nthChild);

                assertEquals(3, panelChildren.getLength());
                foundLeft = doc.getElementsByTagName("left");
                foundRight = doc.getElementsByTagName("right");
                foundBelow = doc.getElementsByTagName("below");
                assertEquals(0, foundLeft.getLength());
                assertEquals(1, foundRight.getLength());
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
                assertEquals(1, foundLeft.getLength());
                assertEquals(1, foundRight.getLength());
                assertEquals(1, foundSetting.getLength());

                // remove second child, which should be <right>
                int nthChild = 2;
                XMLNodeRemover.removeNthChild(panelNode, nthChild);

                assertEquals(3, panelChildren.getLength());
                foundLeft = doc.getElementsByTagName("left");
                foundRight = doc.getElementsByTagName("right");
                foundSetting = doc.getElementsByTagName("setting");
                assertEquals(1, foundLeft.getLength());
                assertEquals(0, foundRight.getLength());
                assertEquals(1, foundSetting.getLength());
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
                assertEquals(1, foundLeft.getLength());
                assertEquals(1, foundRight.getLength());
                assertEquals(1, foundSetting.getLength());

                // remove third child, which should be <setting>
                int nthChild = 3;
                XMLNodeRemover.removeNthChild(panelNode, nthChild);

                assertEquals(3, panelChildren.getLength());
                foundLeft = doc.getElementsByTagName("left");
                foundRight = doc.getElementsByTagName("right");
                foundSetting = doc.getElementsByTagName("setting");
                assertEquals(1, foundLeft.getLength());
                assertEquals(1, foundRight.getLength());
                assertEquals(0, foundSetting.getLength());
            }
        }
    }

}