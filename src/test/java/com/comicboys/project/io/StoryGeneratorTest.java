package com.comicboys.project.io;

import com.comicboys.project.data.NumberedList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import static org.junit.jupiter.api.Assertions.*;

public class StoryGeneratorTest {

    private StoryGenerator storyGenerator;

    @BeforeEach
    public void setup() {
        storyGenerator = new StoryGenerator();
    }

    @Test
    public void testGenerateSceneStory_withVariousTags() throws Exception {
        String xml =
                "<scene>" +
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
                        "</scene>";

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new java.io.ByteArrayInputStream(xml.getBytes()));

        Node sceneNode = doc.getElementsByTagName("scene").item(0);
        NumberedList result = storyGenerator.generateSceneStory(sceneNode);

        assertEquals(3, result.getItems().size());

        // Check expected descriptions
        assertEquals("Alfie is stretching in the bedroom. The day starts", result.getItem(1));
        assertEquals("Betty is yawning in the kitchen.", result.getItem(2));
        assertEquals("Gerry is walking in the outside the house. The sun is so bright, it almost blinds him.", result.getItem(3));
    }
}
