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
    void testGetFileDirectory() {
        String exampleFilePath = "random/file.xml";
        String resultDirectory = XMLFileManager.getFileDirectory(exampleFilePath);
        assertEquals("random/", resultDirectory);
    }
}
