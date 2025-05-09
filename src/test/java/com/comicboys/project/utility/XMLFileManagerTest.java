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

    @Test
    void testValidateElement() {
        Document doc = XMLFileManager.loadXMLFromFile("assets/blueprint/test/specification_test.xml");
        assertNotNull(doc);
        Node scenes = XMLFileManager.validateElement(doc, "scenes");
        assertNotNull(scenes);
        Node nonExistent = XMLFileManager.validateElement(doc, "non-existent");
        assertNull(nonExistent);
    }

    @Test
    void testExtractRandomSceneElement() {
        Document doc = XMLFileManager.loadXMLFromFile("assets/blueprint/specification.xml");
        assertNotNull(doc, "Document should be loaded");

        XMLFileManager.resetSceneTracking(); // reset in case other tests have modified it

        Node randomScene = XMLFileManager.extractRandomSceneElement(doc);
        assertNotNull(randomScene, "Should extract a random scene element");
        assertEquals("scene", randomScene.getNodeName(), "Extracted node should be a <scene>");

        // After extracting once, extracting again many times should eventually return null if scenes are exhausted
        int safetyLimit = 100; // avoid infinite loops
        while (randomScene != null && safetyLimit-- > 0) {
            randomScene = XMLFileManager.extractRandomSceneElement(doc);
        }

        assertNull(randomScene, "Should return null when all scenes are exhausted");
    }

}
