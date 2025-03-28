package com.comicboys.project.io;

import com.comicboys.project.client.APIClient;
import com.comicboys.project.data.Mappings;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class TranslationGenerator {
    private final APIClient apiClient;
    private String translationFilePath;
    private final TranslationFileManager translationFileManager;
    private final Mappings mappings;

    public TranslationGenerator(ConfigurationFile config, Mappings mappings) {
        this.apiClient = new APIClient(config);
        this.mappings = mappings;

        // Generate the translations file path dynamically
        String sourceLanguage = config.getProperty("SOURCE_LANGUAGE").toLowerCase();
        String targetLanguage = config.getProperty("TARGET_LANGUAGE").toLowerCase();
        this.translationFilePath = "assets/translations/" + sourceLanguage + "-to-" + targetLanguage + "-translations.tsv";

        // Ensure directory exists
        ensureDirectoryExists("assets/translations");

        // Create the translations file if it doesn't exist
        ensureFileExists(translationFilePath);

        // Initialize the TranslationFileManager
        this.translationFileManager = new TranslationFileManager(translationFilePath);
    }

    private void ensureDirectoryExists(String directoryPath) {
        File directory = new File(directoryPath);
        if (!directory.exists() && !directory.mkdirs()) {
            System.err.println("Failed to create directory: " + directoryPath);
        }
    }

    private void ensureFileExists(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            try {
                if (file.createNewFile()) {
                    System.out.println("Created file: " + filePath);
                } else {
                    System.err.println("Failed to create file: " + filePath);
                }
            } catch (IOException e) {
                System.err.println("Error creating file: " + e.getMessage());
            }
        }
    }

    /**
     * Generates translations for all text fragments in the mappings
     */
    public void generateTranslations() {
        // Get all unique text fragments from both columns
        List<String> allTexts = mappings.getAllTextFragments();
        allTexts.remove(""); // Remove empty strings


        // Process in batches
        List<String> batch = new ArrayList<>();

        for (String text : allTexts) {
            batch.add(text);


            if (batch.size() >= 20) { // Batch size of 10

                processBatch(batch);
                batch.clear();

            }
        }

        // Process any remaining items
        if (!batch.isEmpty()) {
            processBatch(batch);
        }
    }

    /**
     * Processes a batch of texts for translation
     * @param batch List of texts to translate
     */
    void processBatch(List<String> batch) {
        try {

            List<String> translations = apiClient.sendBatchTranslationRequest(batch);

            // Store the translations
            for (int i = 0; i < batch.size(); i++) {
                translationFileManager.appendTranslation(batch.get(i), translations.get(i));
            }

            // Respect API rate limits
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Translation batch processing was interrupted");
        }
    }

    public Map<String, String> getTranslations() {
        return translationFileManager.loadTranslations();
    }

    public String getTranslationFilePath() {
        return translationFilePath;
    }

    public TranslationFileManager getTranslationFileManager() {
        return translationFileManager;
    }

    public static void main(String[] args) {
        ConfigurationFile configurationFile = new ConfigurationFile();
        Mappings mappings1 = new Mappings();

        TranslationGenerator translationGenerator = new TranslationGenerator(configurationFile,mappings1);


    }
}