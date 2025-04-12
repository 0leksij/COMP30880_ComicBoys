package com.comicboys.project.utility;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class XMLFileManagerTest {

    @Test
    void testLoadValidXML() {
        Document doc = XMLFileManager.loadXMLFromFile("assets/blueprint/specification.xml");
        assertNotNull(doc, "Document should load from valid XML");
        assertEquals("comic", doc.getDocumentElement().getNodeName());
    }


    @Test
    void testGetParent() {
        Document doc = XMLFileManager.loadXMLFromFile("assets/blueprint/specification.xml");
        assertNotNull(doc);
        NodeList nameElement = doc.getElementsByTagName("name");
        Node name = nameElement.item(0); // first <name>
        Node figure = XMLFileManager.getParent(name);
        assertEquals("figure", figure.getNodeName());
    }

    @Test
    void testLoadNonExistingXML() {
        Document doc = XMLFileManager.loadXMLFromFile("assets/blueprint/non_existing.xml");
        assertNull(doc, "Should return null for non existing XML file");
    }

    @Test
    void testSelectElementReturnsExpectedNodes() {
        Document doc = XMLFileManager.loadXMLFromFile("assets/blueprint/specification.xml");
        NodeList nodes = XMLFileManager.selectElements(doc, "balloon");
        assertNotNull(nodes);
        assertTrue(nodes.getLength() > 0, "Should find at least one balloon tag");
    }

    @Test
    void testSaveXMLToFileWorks(@TempDir File tempDir) {
        Document doc = XMLFileManager.loadXMLFromFile("assets/blueprint/specification.xml");
        File outputFile = new File(tempDir, "test_output.xml");
        boolean saved = XMLFileManager.saveXMLToFile(doc, outputFile.getAbsolutePath());

        assertTrue(saved, "Expected XML file to be saved successfully");
        assertTrue(outputFile.exists(), "Output file should exist");
    }


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

    @Test
    void testRemoveAllChildren() {
        Document doc = XMLFileManager.loadXMLFromFile("assets/blueprint/test/specification_test.xml");
        assertNotNull(doc);
        NodeList children = doc.getChildNodes();
        XMLFileManager.removeAllChildren(children);
        assertEquals(0, children.getLength()); // variable updated
        assertEquals(0, doc.getChildNodes().getLength()); // variable was pointer to original child nodes, also updated
    }


    @Test
    void testRemoveFirstChild() {
        Document doc = XMLFileManager.loadXMLFromFile("assets/blueprint/test/specification_test.xml");
        assertNotNull(doc);
        NodeList documentChildren = doc.getChildNodes(); // corresponds to <comic>, has children <figures> and <scenes>
        // looking through and removing its first child, so should remove only <figures>, making its length 2 -> 1
        for (int i = 0 ; i < documentChildren.getLength(); i++) {
            Node comic = documentChildren.item(i);
            NodeList comicChildren = comic.getChildNodes();
            // some code to remove non-element nodes
            for (int j = 0; j < comicChildren.getLength(); j++) {
                Node comicChild = comicChildren.item(j);
                if (comicChild.getNodeType() != Node.ELEMENT_NODE) {
                    comicChild.getParentNode().removeChild(comicChild);
                }
            }
            // should be left with JUST <figures> and <scenes>
            assertEquals(2, comicChildren.getLength());
            NodeList foundFigures = doc.getElementsByTagName("figures");
            NodeList foundScenes = doc.getElementsByTagName("scenes");
            assertEquals(1, foundFigures.getLength());
            assertEquals(1, foundScenes.getLength());

            // remove first child, which should be <figures>
            XMLFileManager.removeFirstChild(comic);

            assertEquals(1, comicChildren.getLength());
            foundFigures = doc.getElementsByTagName("figures");
            foundScenes = doc.getElementsByTagName("scenes");
            // should have no <figures> element but still have <scenes> element
            assertEquals(0, foundFigures.getLength());
            assertEquals(1, foundScenes.getLength());
        }
    }
    @Test
    void testGetFileDirectory() {
        String exampleFilePath = "random/file.xml";
        String resultDirectory = XMLFileManager.getFileDirectory(exampleFilePath);
        assertEquals("random/", resultDirectory);
    }
}
