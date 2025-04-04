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
        // Set up a valid file path to a sample XML blueprint file
        blueprint = new Blueprint("assets/blueprint/specification.xml");
    }

    @Test
    void testGetFilePath() {
        assertEquals("assets/blueprint/specification.xml", blueprint.getFilePath());
    }

    @Test
    void testGetFile() {
        Document file = blueprint.getFile();
        assertNotNull(file, "Document should not be null");
        assertEquals("comic", file.getDocumentElement().getNodeName());
    }

    @Test
    void testGetSpeechBalloons() {
        List<String> speechBalloons = blueprint.getSpeechBalloons();
        assertNotNull(speechBalloons, "Speech balloons list should not be null");
        assertFalse(speechBalloons.isEmpty(), "Speech balloons list should not be empty");

        // You may want to check for specific expected phrases here
        // assertTrue(speechBalloons.contains("Expected Phrase"), "Should contain expected phrase");
    }
}
