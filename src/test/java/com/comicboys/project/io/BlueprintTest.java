package com.comicboys.project.io;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import static org.junit.jupiter.api.Assertions.*;

class BlueprintTest {

    // testing superclass abstract methods, since both TextBlueprint and StoryBlueprint inherit these,
    // no need to test for both concrete classes, one instance of either will give us an idea
    private TextBlueprint blueprint;

    @BeforeEach
    void setUp() {
        blueprint = new TextBlueprint("assets/blueprint/specification.xml");
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
}
