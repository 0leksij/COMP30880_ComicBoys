package com.comicboys.project.utility;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class XMLFileManagerTest {

    @Test
    void testLoadXMLFromFile() {
        Document doc = XMLFileManager.loadXMLFromFile("assets/blueprint/specification.xml");
        assertNotNull(doc, "The XML document should be loaded successfully.");
    }

    @Test
    void testSaveXMLToFile() {
        Document doc = XMLFileManager.loadXMLFromFile("assets/blueprint/specification.xml");
        boolean result = XMLFileManager.saveXMLToFile(doc, "assets/blueprint/test_output.xml");
        assertTrue(result, "The XML file should be saved successfully.");

        // Verify that the file exists
        File file = new File("assets/blueprint/test_output.xml");
        assertTrue(file.exists(), "The output file should exist.");
    }

    @Test
    void testSelectElement() {
        Document doc = XMLFileManager.loadXMLFromFile("assets/blueprint/specification.xml");
        NodeList nodes = XMLFileManager.selectElement(doc, "balloon");
        assertNotNull(nodes, "NodeList should not be null.");
        assertTrue(nodes.getLength() > 0, "There should be at least one balloon element in the XML.");
    }
}
