package com.comicboys.project.io.translate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class TranslationFileManagerTest {
    private static final String TEST_FILE_PATH = "test-translations.tsv";
    private TranslationFileManager translationFileManager;

    @BeforeEach
    void setUp() throws IOException {
        // Delete the test file if it exists
        Files.deleteIfExists(Paths.get(TEST_FILE_PATH));

        // Create the test file
        File file = new File(TEST_FILE_PATH);
        if (file.createNewFile()) {
            System.out.println("Created test file: " + TEST_FILE_PATH);
        } else {
            System.err.println("Failed to create test file: " + TEST_FILE_PATH);
        }

        // Initialize TranslationFileManager
        translationFileManager = new TranslationFileManager(TEST_FILE_PATH);
    }

    @Test
    void testAppendTranslation() {
        // Append a translation
        translationFileManager.appendTranslation("hello", "ciao");

        // Verify the file contains the translation
        Map<String, String> translations = translationFileManager.loadTranslations();
        assertEquals("ciao", translations.get("hello"));
    }

    @Test
    void testAppendTranslation_Duplicate() {
        // Append the same translation twice
        translationFileManager.appendTranslation("hello", "ciao");
        translationFileManager.appendTranslation("hello", "ciao");

        // Verify only one entry exists
        Map<String, String> translations = translationFileManager.loadTranslations();
        assertEquals(1, translations.size());
        assertEquals("ciao", translations.get("hello"));
    }

    @Test
    void testLoadTranslations() {
        // Append multiple translations
        translationFileManager.appendTranslation("hello", "ciao");
        translationFileManager.appendTranslation("goodbye", "arrivederci");

        // Verify all translations are loaded
        Map<String, String> translations = translationFileManager.loadTranslations();
        assertEquals(2, translations.size());
        assertEquals("ciao", translations.get("hello"));
        assertEquals("arrivederci", translations.get("goodbye"));
    }

    @Test
    void testTranslationExists() {
        // Append a translation
        translationFileManager.appendTranslation("hello", "ciao");

        // Verify the translation exists
        assertTrue(translationFileManager.translationExists("hello"));
        assertFalse(translationFileManager.translationExists("nonexistent"));
    }


}