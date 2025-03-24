package com.comicboys.project.client;

import com.comicboys.project.config.ConfigurationFile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class VignetteGeneratorTest {

    @TempDir
    Path tempDir;

    private VignetteGenerator generator;
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

        // Create VignetteGenerator with test functionality
        processedBatches.clear();
        testTranslations.clear();

        generator = new VignetteGenerator(config, mappings) {
            @Override
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
    void testAddToBatchAddsAndProcessesCorrectly() {
        List<String> batch = new ArrayList<>();
        List<String> textColumn = Arrays.asList("Text1", "Text2", "", "Text3", "Text4", "Text5");

        // Call the method
        generator.addToBatch(batch, textColumn);

        // Verify batch was processed when it reached 5 items
        assertEquals(1, processedBatches.size(), "Should process batch when it reaches 5 items");

        String processedTexts = processedBatches.get(0);
        assertTrue(processedTexts.contains("Text1"), "Processed batch should contain Text1");
        assertTrue(processedTexts.contains("Text2"), "Processed batch should contain Text2");
        assertTrue(processedTexts.contains("Text3"), "Processed batch should contain Text3");
        assertTrue(processedTexts.contains("Text4"), "Processed batch should contain Text4");
        assertTrue(processedTexts.contains("Text5"), "Processed batch should contain Text5");
        assertFalse(processedTexts.contains(" "), "Empty string should be removed");

        // Batch should be cleared after processing
        assertTrue(batch.isEmpty(), "Batch should be cleared after processing");
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