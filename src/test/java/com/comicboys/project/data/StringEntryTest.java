package com.comicboys.project.data;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StringEntryTest {
    @Test
    void createEntry() {
        StringEntry stringEntry = new StringEntry(
                "leftPose1",
                "combinedText1",
                "leftText1",
                "rightPose1",
                "background"
        );
        assertNotNull(stringEntry);
        assertNotEquals("", stringEntry.toString());
        assertEquals("leftPose1", stringEntry.getLeftPose());
        assertEquals("combinedText1", stringEntry.getCombinedText());
        assertEquals("leftText1", stringEntry.getLeftText());
        assertEquals("rightPose1", stringEntry.getRightPose());
        assertEquals("background", stringEntry.getBackgrounds());
    }
}
