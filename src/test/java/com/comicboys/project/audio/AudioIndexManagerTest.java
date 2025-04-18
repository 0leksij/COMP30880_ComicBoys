package com.comicboys.project.audio;

import org.junit.jupiter.api.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class AudioIndexManagerTest {
    private static final String TEST_INDEX_PATH = "test_resources/audio-index-test.tsv";
    private AudioIndexManager indexManager;

    @BeforeEach
    void setUp() throws IOException {
        // Clear test file before each test
        Files.deleteIfExists(Path.of(TEST_INDEX_PATH));
        indexManager = new AudioIndexManager(TEST_INDEX_PATH);
    }

    @AfterAll
    static void cleanUp() throws IOException {
        Files.deleteIfExists(Path.of(TEST_INDEX_PATH));
    }

    @Test
    void testNewIndexManagerCreatesFile() {
        assertTrue(Files.exists(Path.of(TEST_INDEX_PATH)));
    }

    @Test
    void testEntryExists() throws IOException {
        String testText = "Test dialogue";
        String testFile = "0.mp3";

        // Add entry directly to file
        Files.writeString(Path.of(TEST_INDEX_PATH), testText + "\t" + testFile);

        // Create new manager to load the entry
        AudioIndexManager manager = new AudioIndexManager(TEST_INDEX_PATH);
        assertTrue(manager.entryExists(testText));
    }

    @Test
    void testGetNextAvailableFileName() throws IOException {
        // Initial state should be 0.mp3
        assertEquals("0.mp3", indexManager.getNextAvailableFileName());

        // Add an entry and verify increment
        indexManager.appendEntry("Test 1", "0.mp3");
        assertEquals("1.mp3", indexManager.getNextAvailableFileName());
    }

    @Test
    void testCalculateNextIndexWithExistingFiles() {
        HashMap<String, String> testMap = new HashMap<>();
        testMap.put("Text 1", "0.mp3");
        testMap.put("Text 2", "1.mp3");
        testMap.put("Text 3", "5.mp3"); // Gap in numbering

        int nextIndex = indexManager.calculateNextIndex(testMap);
        assertEquals(6, nextIndex);
    }
}