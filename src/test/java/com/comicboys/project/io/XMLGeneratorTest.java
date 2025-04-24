package com.comicboys.project.io;

import com.comicboys.project.client.APIClient;
import com.comicboys.project.data.ListEntry;
import com.comicboys.project.data.Mappings;
import com.comicboys.project.data.StringEntry;
import com.comicboys.project.utility.XMLFileManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
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
        ConfigurationFile config = new ConfigurationFile();
        APIClient client = new APIClient(config);
        TranslationGenerator translationGenerator = new TranslationGenerator(config, client, mappings);
        xmlGenerator = new XMLGenerator(mappings, translationGenerator);
    }

    @Test
    void testGenerateXML_createsValidXML() throws Exception {
        NodeList isGenerated = xmlGenerator.generateXML(0, testFilePath);
        assertNotNull(isGenerated);
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

        StringEntry selectedRow = mappings.getEntries().getFirst().toStringEntry();
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

        StringEntry selectedRow = mappings.getEntries().getFirst().toStringEntry();
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
        Element panel = doc.createElement("panel");
        doc.appendChild(panel);

        xmlGenerator.createBalloon(doc, panel, "Hello");
        NodeList balloons = doc.getElementsByTagName("balloon");
        for (int i = 0; i < balloons.getLength(); i++) {
            Element balloon = (Element) balloons.item(i);
            assertEquals("balloon", balloon.getNodeName());
            assertEquals("speech", balloon.getAttribute("status"));
        }

    }
}
