package com.comicboys.project.io;

import com.comicboys.project.data.Mappings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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

    static class TranslationGeneratorTest {

        @TempDir
        Path tempDir;

        private TranslationFileManager.TranslationGenerator generator;
        private ConfigurationFile config;
        private Mappings mappings;
        private List<String> processedBatches = new ArrayList<>();
        private Map<String, String> testTranslations = new HashMap<>();

        @BeforeEach
        void setUp() {
            // Create simple configuration
            config = new ConfigurationFile() {
                private final Map<String, String> properties = new HashMap<>();

                {
                    properties.put("API_KEY", "test-api-key");
                    properties.put("MODEL", "test-model");
                    properties.put("COMPLETIONS_URL", "https://test-url.com");
                    properties.put("SOURCE_LANGUAGE", "English");
                    properties.put("TARGET_LANGUAGE", "Spanish");
                }

                @Override
                public String getProperty(String key) {
                    return properties.get(key);
                }
            };

            // Create simple mappings with test data - using lowercase to match actual processed output
            mappings = new Mappings();
            mappings.addEntry("pose1\thello world\thi\tpose2\tbackground1");
            mappings.addEntry("pose3\tgoodbye\tbye\tpose4\tbackground2");

            // Set up test directory
            System.setProperty("user.dir", tempDir.toString());

            // Create directory structure
            new File(tempDir.toString() + "/assets/translations").mkdirs();

            // Create TranslationGenerator with test functionality
            processedBatches.clear();
            testTranslations.clear();

            generator = new TranslationFileManager.TranslationGenerator(config, mappings) {
                void processBatch(List<String> batch) {
                    // Track processed batches
                    processedBatches.add(String.join(",", batch));

                    // Simulate translation storage
                    for (String text : batch) {
                        this.getTranslationFileManager().appendTranslation(text, "Translated: " + text);
                        testTranslations.put(text, "Translated: " + text);
                    }
                }
            };
        }

        @Test
        void testConstructorCreatesCorrectFiles() {
            // Check that translation file path is correctly constructed
            String expectedPath = "assets/translations/english-to-spanish-translations.tsv";
            assertTrue(generator.getTranslationFilePath().endsWith(expectedPath),
                    "Translation file path should use language configuration");

            // Verify file exists
            File translationFile = new File(generator.getTranslationFilePath());
            assertTrue(translationFile.exists(), "Translation file should be created");
        }

        @Test
        void testGenerateTranslationsProcessesAllTexts() {
            // Run the method
            generator.generateTranslations();

            // Check that all text from mappings was processed
            Set<String> allProcessedTexts = new HashSet<>();
            for (String batchText : processedBatches) {
                allProcessedTexts.addAll(Arrays.asList(batchText.split(",")));
            }

            // Should contain all texts from our mappings - using lowercase to match
            assertTrue(allProcessedTexts.contains("hello world"), "Should process 'hello world'");
            assertTrue(allProcessedTexts.contains("hi"), "Should process 'hi'");
            assertTrue(allProcessedTexts.contains("goodbye"), "Should process 'goodbye'");
            assertTrue(allProcessedTexts.contains("bye"), "Should process 'bye'");
        }

        @Test
        void testProcessBatchStoresTranslations() throws Exception {
            // Create a test batch
            List<String> batch = Arrays.asList("hello", "world");

            // Process the batch
            generator.processBatch(batch);

            // Check translations were stored
            Map<String, String> translations = generator.getTranslations();

            assertEquals("Translated: hello", translations.get("hello"),
                    "Translation for 'hello' should be stored");
            assertEquals("Translated: world", translations.get("world"),
                    "Translation for 'world' should be stored");
        }

        @Test
        void testGetTranslationsReturnsCorrectData() throws Exception {
            // Pre-populate the translation file
            Path translationFile = Path.of(generator.getTranslationFilePath());
            Files.writeString(translationFile, "test1\tTranslated1\ntest2\tTranslated2\n");

            // Get translations
            Map<String, String> translations = generator.getTranslations();

            // Verify correct data is returned
            assertEquals(2, translations.size(), "Should return 2 translations");
            assertEquals("Translated1", translations.get("test1"), "Should return correct translation for test1");
            assertEquals("Translated2", translations.get("test2"), "Should return correct translation for test2");
        }

    }
}