package com.comicboys.project.data;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ListEntryTest {
    @Test
    void createEntry() {
        ListEntry listEntry = new ListEntry(
                List.of("leftPose1", "leftPose2"),
                List.of("combinedText1", "combinedText1"),
                List.of("leftText1"),
                List.of("rightPose1"),
                List.of("background")
        );
        assertNotNull(listEntry);
        assertNotEquals("", listEntry.toString());
        assertEquals(List.of("leftPose1", "leftPose2"), listEntry.getLeftPose());
        assertEquals(List.of("combinedText1", "combinedText1"), listEntry.getCombinedText());
        assertEquals(List.of("leftText1"), listEntry.getLeftText());
        assertEquals(List.of("rightPose1"), listEntry.getRightPose());
        assertEquals(List.of("background"), listEntry.getBackgrounds());
    }
    @Test
    void toSpecificExistingEntry() {
        ListEntry listEntry = new ListEntry(
                List.of("leftPose1"),
                List.of("combinedText1"),
                List.of("leftText1"),
                List.of("rightPose1"),
                List.of("background")
        );
        // get string entry given specific text
        StringEntry stringEntry = listEntry.toStringEntry("leftText1");
        assertNotNull(stringEntry);
        assertNotEquals("", stringEntry.toString());
        assertEquals("leftPose1", stringEntry.getLeftPose());
        assertEquals("combinedText1", stringEntry.getCombinedText());
        assertEquals("leftText1", stringEntry.getLeftText());
        assertEquals("rightPose1", stringEntry.getRightPose());
        assertEquals("background", stringEntry.getBackgrounds());
    }
    @Test
    void toSpecificNonExistingEntry() {
        ListEntry listEntry = new ListEntry(
                List.of("leftPose1"),
                List.of("combinedText1"),
                List.of("leftText1"),
                List.of("rightPose1"),
                List.of("background")
        );
        // get string entry given specific text
        StringEntry stringEntry = listEntry.toStringEntry("NOT_IN_ENTRY");
        assertNotNull(stringEntry);
        assertNotEquals("", stringEntry.toString());
        assertEquals("leftPose1", stringEntry.getLeftPose());
        assertEquals("combinedText1", stringEntry.getCombinedText());
        assertEquals("leftText1", stringEntry.getLeftText());
        assertEquals("rightPose1", stringEntry.getRightPose());
        assertEquals("background", stringEntry.getBackgrounds());
    }
    @Test
    void toRandomExistingEntry() {
        ListEntry listEntry = new ListEntry(
                List.of("leftPose1"),
                List.of("combinedText1"),
                List.of("leftText1"),
                List.of("rightPose1"),
                List.of("background")
        );
        // get string entry given no argument, picks random but since only one string in each list will pick that string
        StringEntry stringEntry = listEntry.toStringEntry();
        assertNotNull(stringEntry);
        assertNotEquals("", stringEntry.toString());
        assertEquals("leftPose1", stringEntry.getLeftPose());
        assertEquals("combinedText1", stringEntry.getCombinedText());
        assertEquals("leftText1", stringEntry.getLeftText());
        assertEquals("rightPose1", stringEntry.getRightPose());
        assertEquals("background", stringEntry.getBackgrounds());
    }
}
