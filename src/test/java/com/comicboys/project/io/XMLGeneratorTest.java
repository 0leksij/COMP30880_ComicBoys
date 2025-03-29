package com.comicboys.project.io;

import com.comicboys.project.data.ListEntry;
import com.comicboys.project.data.Mappings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class XMLGeneratorTest {
    private XMLGenerator xmlGenerator;
    private Mappings mappings;
    private ListEntry testEntry;
    private String testFilePath = "assets/mappings/test_output.xml";

    @BeforeEach
    void setUp() {
        mappings = new Mappings();
        mappings.addEntry("pose1\ttext1\tleft1\tpose2\tbackground1,background2");
        xmlGenerator = new XMLGenerator(mappings);
    }

    @Test
    void testGenerateXML_createsValidXML() throws Exception {
        String xmlContent = xmlGenerator.generateXML(0, testFilePath);
        assertNotNull(xmlContent);
        assertTrue(xmlContent.contains("<comic>"));
        assertTrue(xmlContent.contains("<scene>"));
    }

    @Test
    void testGenerateXML_createsFile() {
        xmlGenerator.generateXML(0, testFilePath);
        File file = new File(testFilePath);
        assertTrue(file.exists() && file.isFile());
    }

    @Test
    void testCreateSingleCharacterPanels() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.newDocument();
        Element sceneElement = doc.createElement("scene");
        doc.appendChild(sceneElement);

        ListEntry selectedRow = mappings.getEntries().get(0);
        xmlGenerator.createSingleCharacterPanels(doc, sceneElement, selectedRow);

        assertEquals("scene", sceneElement.getNodeName());
        assertTrue(sceneElement.getElementsByTagName("panel").getLength() > 0);
    }

    @Test
    void testCreateMultipleCharacterPanels() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.newDocument();
        Element sceneElement = doc.createElement("scene");
        doc.appendChild(sceneElement);

        ListEntry selectedRow = mappings.getEntries().get(0);
        xmlGenerator.createMultipleCharacterPanels(doc, sceneElement, selectedRow);

        assertEquals("scene", sceneElement.getNodeName());
        assertTrue(sceneElement.getElementsByTagName("panel").getLength() > 0);
    }

    @Test
    void testSaveXMLToFile() throws Exception {
        String testContent = "<comic><scene></scene></comic>";
        xmlGenerator.saveXMLToFile(testContent, testFilePath);
        Path path = Path.of(testFilePath);
        assertTrue(Files.exists(path));
        assertEquals(testContent, Files.readString(path).trim());
    }
}
