package com.comicboys.project.io;

import com.comicboys.project.client.APIClient;
import com.comicboys.project.data.Mappings;
import org.apiguardian.api.API;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class TranslationGeneratorTest {

    @TempDir
    Path tempDir;

    private TranslationGenerator generator;
    private ConfigurationFile config;
    private APIClient client;
    private Mappings mappings;
    private List<List<String>> processedBatches = new ArrayList<>();
    private Map<String, String> testTranslations = new HashMap<>();
    private AtomicInteger apiCallCount = new AtomicInteger(0);

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
        client = new APIClient(config);

        // Create simple mappings with test data
        mappings = new Mappings();
        mappings.addEntry("pose1\thello world\thi\tpose2\tbackground1");
        mappings.addEntry("pose3\tgoodbye\tbye\tpose4\tbackground2");

        // Set up test directory
        System.setProperty("user.dir", tempDir.toString());

        // Create directory structure
        new File(tempDir.toString() + "/assets/translations").mkdirs();

        // Create TranslationGenerator with test functionality
        //processedBatches.clear();
        //testTranslations.clear();
        apiCallCount.set(0);

        generator = new TranslationGenerator(config, client, mappings) {
            protected boolean processBatchWithRetry(List<String> batch) {
                processedBatches.add(new ArrayList<>(batch));
                apiCallCount.incrementAndGet();

                // Simulate API response
                List<String> translations = new ArrayList<>();
                for (String text : batch) {
                    translations.add("Translated: " + text);
                    testTranslations.put(text, "Translated: " + text);
                }

                // Store translations
                for (int i = 0; i < batch.size(); i++) {
                    this.getTranslationFileManager().appendTranslation(batch.get(i), translations.get(i));
                }

                return true; // Always succeed in tests unless we want to test retries
            }
        };

        generator.setMaxRetries(0);
        generator.setBatchSizeLimit(5);
        generator.setRetryDelaySeconds(1);
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

    @Test
    void testRetryLogicOnApiFailure() {
        // Create generator that will fail first two attempts
        AtomicInteger failCounter = new AtomicInteger(2);
        TranslationGenerator failingGenerator = new TranslationGenerator(config, client, mappings) {
            protected boolean processBatchWithRetry(List<String> batch) {
                if (failCounter.getAndDecrement() > 0) {
                    return false; // Simulate API failure
                }
                // On third attempt, succeed
                for (String text : batch) {
                    this.getTranslationFileManager().appendTranslation(text, "Translated: " + text);
                }
                return true;
            }
        };

        failingGenerator.setMaxRetries(3); // Allow enough retries
        failingGenerator.setRetryDelaySeconds(0); // Speed up test

        // Run the method
        failingGenerator.generateTranslations(mappings.getAllTextFragments());

        // Verify translations were eventually stored
        Map<String, String> translations = failingGenerator.getTranslations();
        assertFalse(translations.isEmpty(), "Translations should be stored after retries");
    }

    @Test
    void testBatchProcessingRespectsSizeLimit() {
        // Add enough entries to require multiple batches
        for (int i = 0; i < 50; i++) {
            mappings.addEntry("pose" + i + "\ttext" + i + "\ttext" + i + "\tpose" + (i+1) + "\tbackground" + i);
        }

        // Run the method
        generator.generateTranslations(mappings.getAllTextFragments());

        // Verify batches were processed in correct sizes
        for (List<String> batch : processedBatches) {
            assertTrue(batch.size() <= generator.getBatchSizeLimit(),
                    "Batch size should not exceed batch size limit");
        }
    }

    @Test
    void testSkipsAlreadyTranslatedTexts() {
        // Pre-populate some translations
        generator.getTranslationFileManager().appendTranslation("hello world", "Existing translation");

        // Run the method
        generator.generateTranslations(mappings.getAllTextFragments());

        // Verify the existing translation wasn't reprocessed
        boolean found = false;
        for (List<String> batch : processedBatches) {
            if (batch.contains("hello world")) {
                found = true;
                break;
            }
        }
        assertFalse(found, "Already translated text should be skipped");
    }

    @Test
    void testEmptyTextsAreSkipped() {
        // Add an entry with empty text
        mappings.addEntry("pose5\t\tempty\tpose6\tbackground3");

        // Run the method
        generator.generateTranslations(mappings.getAllTextFragments());

        // Verify empty text wasn't processed
        boolean foundEmpty = false;
        for (List<String> batch : processedBatches) {
            if (batch.contains("")) {
                foundEmpty = true;
                break;
            }
        }
        assertFalse(foundEmpty, "Empty texts should be skipped");
    }
}