package com.comicboys.project.audio;

import org.junit.jupiter.api.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Map;

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

    // Existing tests
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
    void testCalculateNextIndexWithExistingFiles() throws IOException {
        // Setup test data directly in file
        String testData = "Text 1\t0.mp3\nText 2\t1.mp3\nText 3\t5.mp3";
        Files.writeString(Path.of(TEST_INDEX_PATH), testData);

        AudioIndexManager manager = new AudioIndexManager(TEST_INDEX_PATH);
        assertEquals("6", manager.getNextAvailableFileName().replace(".mp3", ""));
    }

    // New tests from AudioHashmapperTest
    @Test
    void testLoadIndexFileWithValidEntries() throws IOException {
        // Setup test data
        String testData = "Hello\t0.mp3\nWorld\t1.mp3";
        Files.writeString(Path.of(TEST_INDEX_PATH), testData);

        AudioIndexManager manager = new AudioIndexManager(TEST_INDEX_PATH);
        Map<String, String> index = manager.getIndexMap();

        assertEquals(2, index.size());
        assertEquals("0.mp3", index.get("Hello"));
        assertEquals("1.mp3", index.get("World"));
    }

    @Test
    void testLoadIndexFileWithMalformedLines() throws IOException {
        // Setup test data with malformed line
        String testData = "Hello\t0.mp3\nBadLine\nWorld\t1.mp3";
        Files.writeString(Path.of(TEST_INDEX_PATH), testData);

        AudioIndexManager manager = new AudioIndexManager(TEST_INDEX_PATH);
        Map<String, String> index = manager.getIndexMap();

        assertEquals(2, index.size()); // Should skip malformed line
        assertEquals("0.mp3", index.get("Hello"));
        assertEquals("1.mp3", index.get("World"));
    }

    @Test
    void testLoadIndexFileWithDuplicateKeys() throws IOException {
        // Setup test data with duplicate keys
        String testData = "Hello\t0.mp3\nHello\t1.mp3\nWorld\t2.mp3";
        Files.writeString(Path.of(TEST_INDEX_PATH), testData);

        AudioIndexManager manager = new AudioIndexManager(TEST_INDEX_PATH);
        Map<String, String> index = manager.getIndexMap();

        // Should keep the last occurrence
        assertEquals(2, index.size());
        assertEquals("1.mp3", index.get("Hello"));
        assertEquals("2.mp3", index.get("World"));
    }

    @Test
    void testLoadIndexFileWithTrailingWhitespace() throws IOException {
        // Setup test data with whitespace
        String testData = "  Hello  \t  0.mp3  \n  World  \t  1.mp3  ";
        Files.writeString(Path.of(TEST_INDEX_PATH), testData);

        AudioIndexManager manager = new AudioIndexManager(TEST_INDEX_PATH);
        Map<String, String> index = manager.getIndexMap();

        assertEquals(2, index.size());
        assertEquals("0.mp3", index.get("Hello")); // Keys and values should be trimmed
        assertEquals("1.mp3", index.get("World"));
    }
}