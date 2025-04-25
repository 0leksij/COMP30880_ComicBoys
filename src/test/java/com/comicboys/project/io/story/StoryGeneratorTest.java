package com.comicboys.project.io.story;

import com.comicboys.project.client.APIClient;
import com.comicboys.project.client.APIResponse;
import com.comicboys.project.io.config.ConfigurationFile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class StoryGeneratorTest {

    private StoryGenerator storyGenerator;
    private String tempXmlFilePath;

    @BeforeEach
    public void setup() throws Exception {
        ConfigurationFile config = new ConfigurationFile();
        APIClient client = new APIClient(config);
        storyGenerator = new StoryGenerator(client);

        String xmlContent =
                "<comic>" +
                        "<scenes>" +
                        "<scene>" +
                        "<panel>" +
                        "<middle>" +
                        "<figure>" +
                        "<id>Alfie</id>" +
                        "<name>Alfie</name>" +
                        "<appearance>male</appearance>" +
                        "<skin>light brown</skin>" +
                        "<hair>dark brown</hair>" +
                        "<lips>red</lips>" +
                        "<pose>zero</pose>" +
                        "<facing>right</facing>" +
                        "</figure>" +
                        "</middle>" +
                        "<below>Scene 0</below>" +
                        "<border>white</border>" +
                        "</panel>" +
                        "<panel>" +
                        "<middle>" +
                        "<figure>" +
                        "<name>Alfie</name>" +
                        "<pose>stretching</pose>" +
                        "<facing>left</facing>" +
                        "</figure>" +
                        "</middle>" +
                        "<setting>bedroom</setting>" +
                        "<below>The day starts</below>" +
                        "</panel>" +
                        "<panel>" +
                        "<middle>" +
                        "<figure>" +
                        "<name>Betty</name>" +
                        "<pose>smiling</pose>" +
                        "<facing>right</facing>" +
                        "</figure>" +
                        "<balloon status='speech'>" +
                        "<content>yawning</content>" +
                        "</balloon>" +
                        "</middle>" +
                        "<setting>kitchen</setting>" +
                        "</panel>" +
                        "<panel>" +
                        "<left>" +
                        "<figure>" +
                        "<name>Gerry</name>" +
                        "<pose>walking</pose>" +
                        "<facing>right</facing>" +
                        "</figure>" +
                        "</left>" +
                        "<above>outside the house</above>" +
                        "<below>The sun is so bright, it almost blinds him.</below>" +
                        "</panel>" +
                        "</scene>" +
                        "</scenes>" +
                        "</comic>";

        // Create temp file
        Path tempPath = Files.createTempFile("test-comic", ".xml");
        Files.write(tempPath, xmlContent.getBytes());
        tempXmlFilePath = tempPath.toString();

        storyGenerator.loadXmlDocument(tempXmlFilePath);
    }

    @Test
    public void testGenerateSceneStory_withVariousTags() {
        String result = storyGenerator.generateSceneStory(0);
        String[] panels = result.split("\n");

        assertEquals(3, panels.length);
        assertEquals("1. Alfie is stretching in the bedroom. The day starts", panels[0]);
        assertEquals("2. Betty is yawning in the kitchen.", panels[1]);
        assertEquals("3. Gerry is walking in the outside the house. The sun is so bright, it almost blinds him.", panels[2]);

        // Clean up
        new File(tempXmlFilePath).delete();
    }

    @Test
    public void testGenerateSceneStory_emptyScene() throws Exception {
        // Test empty scene
        String emptyXml = "<comic><scenes><scene></scene></scenes></comic>";
        Path emptyPath = Files.createTempFile("empty-comic", ".xml");
        Files.write(emptyPath, emptyXml.getBytes());
        storyGenerator.loadXmlDocument(emptyPath.toString());

        String result = storyGenerator.generateSceneStory(0);
        assertEquals("", result.trim());

        Files.delete(emptyPath);
    }

    @Test
    public void testGenerateSceneStory_invalidSceneIndex() {
        String result = storyGenerator.generateSceneStory(1); // Only scene 0 exists
        assertEquals("Scene index out of bounds.", result);
    }

    @Test
    public void testGetCharactersByPanel_multipleCharacters() {
        String result = storyGenerator.getCharactersByPanel(0);
        String[] panels = result.split("\n");

        // Should skip first panel (index 0) and show panels 1-3
        assertEquals(3, panels.length);
        assertEquals("1. Alfie__", panels[0]);
        assertEquals("2. Betty__", panels[1]);
        assertEquals("3. Gerry__", panels[2]);
    }

    @Test
    public void testGetCharactersByPanel_noDocumentLoaded() {
        StoryGenerator emptyGenerator = new StoryGenerator(new APIClient(new ConfigurationFile()));
        String result = emptyGenerator.getCharactersByPanel(0);
        assertEquals("No XML document loaded. Call loadXmlDocument() first.", result);
    }

    @Test
    public void testGetAllFiguresFromPanel() throws Exception {
        // Test with a panel containing multiple figures
        String multiFigureXml =
                "<panel>" +
                        "<left>" +
                        "<figure><name>Alfie</name></figure>" +
                        "</left>" +
                        "<right>" +
                        "<figure><name>Betty</name></figure>" +
                        "</right>" +
                        "</panel>";

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new java.io.ByteArrayInputStream(multiFigureXml.getBytes()));

        Element panel = doc.getDocumentElement();
        List<Element> figures = storyGenerator.getAllFiguresFromPanel(panel);

        assertEquals(2, figures.size());
        assertEquals("Alfie", storyGenerator.getTextContent(figures.get(0), "name"));
        assertEquals("Betty", storyGenerator.getTextContent(figures.get(1), "name"));
    }

    @Test
    public void testGetBalloonForFigure() throws Exception {
        // Test finding balloon for specific figure
        String balloonXml =
                "<panel>" +
                        "<middle>" +
                        "<figure><name>Alfie</name></figure>" +
                        "<balloon status='speech'><content>Hello</content></balloon>" +
                        "</middle>" +
                        "</panel>";

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new java.io.ByteArrayInputStream(balloonXml.getBytes()));

        Element panel = doc.getDocumentElement();
        Element figure = (Element) panel.getElementsByTagName("figure").item(0);
        Element balloon = storyGenerator.getBalloonForFigure(panel, figure);

        assertNotNull(balloon);
        assertEquals("Hello", storyGenerator.getTextContent(balloon, "content"));
    }

    @Test
    public void testProcessDialogueResponse_numberedList() {
        APIResponse response = new APIResponse("1. Alfie: Hello | Description\n2. Betty: Hi there | Description");
        List<String> dialogues = storyGenerator.processDialogueResponse(response);

        assertEquals(2, dialogues.size());
        assertEquals("Alfie: Hello | Description", dialogues.get(0));
        assertEquals("Betty: Hi there | Description", dialogues.get(1));
    }

    @Test
    public void testProcessDialogueResponse_plainText() {
        APIResponse response = new APIResponse("Alfie: Hello | Description\nBetty: Hi there | Description");
        List<String> dialogues = storyGenerator.processDialogueResponse(response);

        assertEquals(2, dialogues.size());
        assertEquals("Alfie: Hello | Description", dialogues.get(0));
        assertEquals("Betty: Hi there | Description", dialogues.get(1));
    }
}