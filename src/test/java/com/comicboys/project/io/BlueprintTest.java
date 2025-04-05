package com.comicboys.project.io;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BlueprintTest {

    private Blueprint blueprint;

    @BeforeEach
    void setUp() {
        blueprint = new Blueprint("assets/blueprint/specification.xml");
    }

    @Test
    void testGetFilePathReturnsCorrectPath() {
        assertEquals("assets/blueprint/specification.xml", blueprint.getFilePath());
    }

    @Test
    void testGetFileReturnsValidDocument() {
        Document file = blueprint.getFile();
        assertNotNull(file, "Document should not be null");
        assertEquals("comic", file.getDocumentElement().getNodeName());
    }

    @Test
    void testGetSpeechBalloonsNotEmpty() {
        List<String> balloons = blueprint.getSpeechBalloons();
        assertNotNull(balloons, "Speech balloons list should not be null");
        assertFalse(balloons.isEmpty(), "Speech balloons should not be empty");
    }

  /*  @Test
    void testBlueprintWithNoBalloons() {
        Blueprint emptyBlueprint = new Blueprint("assets/blueprint/no_balloons.xml");
        List<String> balloons = emptyBlueprint.getSpeechBalloons();
        assertNotNull(balloons);
        assertTrue(balloons.isEmpty(), "Should return empty list for no balloon tags");
    }*/
}
