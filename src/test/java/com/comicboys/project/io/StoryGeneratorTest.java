package com.comicboys.project.io;

import com.comicboys.project.client.APIClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

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
}