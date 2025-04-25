package com.comicboys.project.io.blueprint;

import com.comicboys.project.utility.XMLFileManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class StoryBlueprintTest {

    private StoryBlueprint blueprint;

    @BeforeEach
    void setUp() {
        blueprint = new StoryBlueprint("assets/story/test/test_specification.xml");
    }

    @Test
    void testShortStory() throws Exception {
        List<List<String>> story = List.of(
                List.of(
                        "Alfie: Hello! ",
                        "Alfie: Oh no it's a wild betty! Shoot her! | Betty: No! ",
                        "Alfie: Winner winner chicken dinner! | Betty: Argh! | Alfie celebrates while Betty lost. "
                ),
                List.of(
                        "Alfie: Now I am become death, the destroyer of worlds. "
                )
        );
        blueprint.writeStory(story, "test_story.xml");

        Document doc = XMLFileManager.loadXMLFromFile("assets/story/test/test_story.xml");
        assertNotNull(doc);
        String fileText = doc.getDocumentElement().getTextContent();

        String delimiter = "|";
        System.out.println("USING DELIMITER " + delimiter + " FOR SEPARATING CHARACTERS");
        for (int i = 0; i < story.size(); i++) {
            for (int j = 0; j < story.get(i).size(); j++) {
                List<String> currentPanel = List.of(story.get(i).get(j).split("\\|"));
                for (int k = 0; k < currentPanel.size(); k++) {
                    String currentPanelText = currentPanel.get(k);
                    int startIndex = currentPanelText.indexOf(":");
                    if (startIndex == -1) { startIndex = 0; }
                    else { startIndex++; }
                    String currentPanelTextTrimmed = currentPanelText.substring(startIndex ).trim();
                    System.out.println("Found story text in file: " + currentPanelTextTrimmed);
                    assertTrue(fileText.contains(currentPanelTextTrimmed));
                }
            }
        }

    }

}
