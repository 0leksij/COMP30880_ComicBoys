package com.comicboys.project.io;

import com.comicboys.project.data.ListEntry;
import com.comicboys.project.data.Mappings;
import com.comicboys.project.utility.XMLFileManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.StringReader;

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
        boolean isGenerated = xmlGenerator.generateXML(0, testFilePath);
        assertTrue(isGenerated);
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
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        InputSource is = new InputSource();
        is.setCharacterStream(new StringReader("<comic><scene></scene></comic>"));
        Document doc = db.parse(is);
        boolean isSaved = XMLFileManager.saveXMLToFile(doc, testFilePath);
        assertTrue(isSaved);
    }

    @Test
    void testGetValidPose() {
        String validPose = "pose1";
        assertEquals(validPose, xmlGenerator.getValidPose(validPose));
        assertNotNull(xmlGenerator.getValidPose("invalid_pose"));
    }

    @Test
    void testCreateBackgroundSettingElement() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.newDocument();

        Element settingElement = xmlGenerator.createBackgroundSettingElement(doc);
        assertEquals("setting", settingElement.getNodeName());
    }

    @Test
    void testCreateBalloon() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.newDocument();

        Element balloon = xmlGenerator.createBalloon(doc, "left_balloon", "Hello");
        assertEquals("left_balloon", balloon.getNodeName());
        assertEquals("speech", balloon.getAttribute("status"));
    }
}
