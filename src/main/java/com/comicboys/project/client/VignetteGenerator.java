package com.comicboys.project.client;

import com.comicboys.project.config.ConfigurationFile;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class VignetteGenerator {
    private final APIClient apiClient;
    private String translationFilePath;
    private final TranslationFileManager translationFileManager;
    private final Mappings mappings;

    public VignetteGenerator(ConfigurationFile config, Mappings mappings) {
        this.apiClient = new APIClient(config);
        this.mappings = mappings;

        // Generate the translations file path dynamically
        String sourceLanguage = config.getProperty("SOURCE_LANGUAGE").toLowerCase();
        String targetLanguage = config.getProperty("TARGET_LANGUAGE").toLowerCase();
        this.translationFilePath = "assets/translations/" + sourceLanguage + "-to-" + targetLanguage + "-translations.tsv";


        // Create the translations file if it doesn't exist
        ensureFileExists(translationFilePath);

        // Initialize the TranslationFileManager
        this.translationFileManager = new TranslationFileManager(translationFilePath);
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


    // translate all words in either text column
    public void generateTranslations() {
        List<String> batch = new ArrayList<>();
        // need to change this to use set oleksii made instead, duplicates area already being handled,
        // but wondering if changing it here would make it more efficient?
        for (ListEntry entry : mappings.getEntries()) {
            addToBatch(batch, entry.getCombinedText());
            addToBatch(batch, entry.getLeftText());
        }
        // Send any remaining items in the batch
        if (!batch.isEmpty()) {
            processBatch(batch);
        }
    }

    void addToBatch(List<String> batch, List<String> textColumn) {
        batch.addAll(textColumn);
        batch.removeAll(List.of(""));
        // If batch reaches 5-10 items (adjustable), send it for translation
        if (batch.size() >= 5) {
            processBatch(batch);
            batch.clear(); // Clear batch after processing
        }
    }

    void processBatch(List<String> batch) {
        try {
            List<String> translations = apiClient.sendBatchTranslationRequest(batch);

            // Store the translations
            for (int i = 0; i < batch.size(); i++) {
                translationFileManager.appendTranslation(batch.get(i), translations.get(i));
            }

            // Respect API rate limits
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

    }

    public Map<String, String> getTranslations() {
        return translationFileManager.loadTranslations();
    }
}
