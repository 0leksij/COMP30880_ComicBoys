package com.comicboys.project.io;

import com.comicboys.project.data.Mappings;
import com.comicboys.project.data.StringEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.*;

class XMLGeneratorTest {
    private Mappings mappings;
    private XMLGenerator xmlGenerator;

    @BeforeEach
    void setUp() {
        mappings = new Mappings();
        // Add sample mappings
        mappings.addEntry("standing\tgreeting\thello\twaving\tpark");
        mappings.addEntry("sitting\tconversation\thi\tlistening\tcafe");
        xmlGenerator = new XMLGenerator(mappings);
    }

    @Test
    void testGenerateXML_ValidEntry_ReturnsValidXML() {
        StringEntry entry = new StringEntry("standing", "greeting", "hello", "waving", "park");
        String xml = xmlGenerator.generateXML(entry);
        String[] lines = xml.split("\n");

        assertNotNull(xml);
        assertTrue(xml.startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>"));
        assertTrue(lines[1].trim().startsWith("<comic>")); //tests if second line in xml starts with comic
        assertTrue(xml.contains("<scene>"));
        assertTrue(xml.contains("</comic>"));
    }

    @Test
    void testGenerateXML_ValidatesXMLStructure() throws Exception {
        StringEntry entry = new StringEntry("standing", "greeting", "hello", "waving", "park");
        String xml = xmlGenerator.generateXML(entry);

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new InputSource(new StringReader(xml)));

        // Verify root element
        assertEquals("comic", doc.getDocumentElement().getNodeName());

        // Verify scene count
        NodeList scenes = doc.getElementsByTagName("scene");
        assertEquals(4, scenes.getLength());

        // Verify first scene structure
        Element firstScene = (Element) scenes.item(0);
        assertNotNull(firstScene.getElementsByTagName("left").item(0));
        assertNotNull(firstScene.getElementsByTagName("right").item(0));
        assertNotNull(firstScene.getElementsByTagName("setting").item(0));
    }

    @Test
    void testCreateScene_ConsistentBackground() {
        StringEntry entry = new StringEntry("standing", "greeting", "hello", "waving", "park");

        // Generate XML and parse to check backgrounds
        String xml = xmlGenerator.generateXML(entry);
        String[] backgrounds = new String[4];

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(xml)));

            NodeList settings = doc.getElementsByTagName("setting");
            for (int i = 0; i < settings.getLength(); i++) {
                backgrounds[i] = settings.item(i).getTextContent();
            }
        } catch (Exception e) {
            fail("XML parsing failed");
        }

        // All backgrounds should be the same
        for (int i = 1; i < backgrounds.length; i++) {
            assertEquals(backgrounds[0], backgrounds[i]);
        }
    }

    @Test
    void testGetValidPose_ValidPose_ReturnsSamePose() {
        String validPose = "standing";
        String result = xmlGenerator.getValidPose(validPose);
        assertEquals(validPose, result);
    }

    @Test
    void testGetValidPose_InvalidPose_ReturnsRandomValidPose() {
        String invalidPose = "invalid_pose";
        String result = xmlGenerator.getValidPose(invalidPose);
        assertTrue(mappings.getAllLeftPoses().contains(result));
    }

    @Test
    void testGetValidBackground_ValidBackground_ReturnsSameBackground() {
        String validBackground = "park";
        String result = xmlGenerator.getValidBackground(validBackground);
        assertEquals(validBackground, result);
    }

    @Test
    void testGetValidBackground_InvalidBackground_ReturnsRandomValidBackground() {
        String invalidBackground = "invalid_background";
        String result = xmlGenerator.getValidBackground(invalidBackground);
        assertTrue(mappings.getAllBackgrounds().contains(result));
    }

    @Test
    void testGenerateXML_WithEmptyMappings_StillProducesXML() {
        // Create generator with empty mappings
        XMLGenerator emptyGenerator = new XMLGenerator(new Mappings());
        StringEntry entry = new StringEntry("unknown", "text", "hello", "unknown", "nowhere");

        String xml = emptyGenerator.generateXML(entry);
        assertNotNull(xml);
        assertTrue(xml.contains("<comic>"));
    }

    @Test
    void testBalloonDistribution_AlwaysOnePerScene() throws Exception {
        StringEntry entry = new StringEntry("standing", "greeting", "hello", "waving", "park");
        String xml = xmlGenerator.generateXML(entry);

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new InputSource(new StringReader(xml)));

        NodeList scenes = doc.getElementsByTagName("scene");
        for (int i = 0; i < scenes.getLength(); i++) {
            Element scene = (Element) scenes.item(i);
            NodeList leftBalloons = ((Element)scene.getElementsByTagName("left").item(0))
                    .getElementsByTagName("balloon");
            NodeList rightBalloons = ((Element)scene.getElementsByTagName("right").item(0))
                    .getElementsByTagName("balloon");

            assertEquals(1, leftBalloons.getLength() + rightBalloons.getLength());
        }
    }
}