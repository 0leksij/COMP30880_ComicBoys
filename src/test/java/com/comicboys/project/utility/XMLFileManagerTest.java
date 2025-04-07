package com.comicboys.project.utility;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.w3c.dom.Document;
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
    void testLoadMalformedXMLReturnsNull() {
        Document doc = XMLFileManager.loadXMLFromFile("assets/blueprint/malformed.xml");
        assertNull(doc, "Should return null for malformed XML");
    }

    @Test
    void testSelectElementReturnsExpectedNodes() {
        Document doc = XMLFileManager.loadXMLFromFile("assets/blueprint/specification.xml");
        NodeList nodes = XMLFileManager.selectElement(doc, "balloon");
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
}
