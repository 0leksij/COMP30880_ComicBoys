package com.comicboys.project.io.blueprint;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TextBlueprintTest {
    private TextBlueprint blueprint;

    @BeforeEach
    void setUp() {
        blueprint = new TextBlueprint("assets/blueprint/specification.xml");
    }

    @Test
    void testGetSpeechBalloonsNotEmpty() {
        List<String> balloons = blueprint.getSpeechBalloons();
        assertNotNull(balloons, "Speech balloons list should not be null");
        assertFalse(balloons.isEmpty(), "Speech balloons should not be empty");
    }

    @Test
    void testBlueprintWithNoBalloons() {
        TextBlueprint emptyBlueprint = new TextBlueprint("assets/blueprint/test/no_balloons.xml");
        List<String> balloons = emptyBlueprint.getSpeechBalloons();
        assertNotNull(balloons);
        assertTrue(balloons.isEmpty(), "Should return empty list for no balloon tags");
    }
}
