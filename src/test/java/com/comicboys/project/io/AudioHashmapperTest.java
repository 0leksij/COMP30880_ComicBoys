package com.comicboys.project.io;

import org.junit.jupiter.api.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class AudioHashmapperTest {
    private static final String TEST_INDEX_PATH = "test_resources/audio-hashmapper-test.tsv";

    @BeforeEach
    void setUp() throws IOException {
        // Create test file with sample data
        String testData = "Hello\t0.mp3\nWorld\t1.mp3";
        Files.writeString(Path.of(TEST_INDEX_PATH), testData);
    }

    @AfterAll
    static void cleanUp() throws IOException {
        Files.deleteIfExists(Path.of(TEST_INDEX_PATH));
    }

    @Test
    void testLoadAudioIndex() throws IOException {
        HashMap<String, String> index = AudioHashmapper.loadAudioIndex(TEST_INDEX_PATH);

        assertEquals(2, index.size());
        assertEquals("0.mp3", index.get("Hello"));
        assertEquals("1.mp3", index.get("World"));
    }

    @Test
    void testLoadAudioIndexWithMalformedLines() throws IOException {
        // Add malformed line
        Files.writeString(Path.of(TEST_INDEX_PATH), "\nBadLine\nGood\t2.mp3", StandardOpenOption.APPEND);

        HashMap<String, String> index = AudioHashmapper.loadAudioIndex(TEST_INDEX_PATH);
        assertEquals(3, index.size()); // Should skip bad line but keep others
        assertEquals("2.mp3", index.get("Good"));
    }

    @Test
    void testLoadNonExistentFile() {
        assertThrows(IOException.class, () -> {
            AudioHashmapper.loadAudioIndex("nonexistent-file.tsv");
        });
    }
}