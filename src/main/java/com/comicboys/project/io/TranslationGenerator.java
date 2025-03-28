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
        int maxRetries = 3;
        int retryDelaySeconds = 30;
        int attempt = 0;
        boolean success = false;

        while (attempt <= maxRetries && !success) {
            attempt++;
            System.out.println("Starting translation attempt " + attempt);

            try {
                List<String> allTexts = mappings.getAllTextFragments();
                allTexts.remove("");
                boolean hadErrors = false;

                List<String> batch = new ArrayList<>();
                for (String text : allTexts) {
                    if (translationFileManager.translationExists(text)) {
                        continue;
                    }

                    batch.add(text);
                    if (batch.size() >= 20) {
                        if (!processBatchWithRetry(batch)) {
                            hadErrors = true;
                            break; // Exit batch processing on error
                        }
                        batch.clear();
                    }
                }

                // Process final batch if no errors occurred
                if (!hadErrors && !batch.isEmpty()) {
                    if (!processBatchWithRetry(batch)) {
                        hadErrors = true;
                    }
                }

                if (!hadErrors) {
                    success = true;
                    System.out.println("Translation completed successfully");
                } else if (attempt <= maxRetries) {
                    System.out.println("Errors detected, retrying in " + retryDelaySeconds + "s...");
                    TimeUnit.SECONDS.sleep(retryDelaySeconds);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Translation interrupted", e);
            }
        }

        if (!success) {
            System.err.println("Failed after " + maxRetries + " attempts");
        }
    }

    private boolean processBatchWithRetry(List<String> batch) {
        List<String> translations = apiClient.sendBatchTranslationRequest(batch);

        // Check for empty list or error responses
        if (translations.isEmpty() || translations.stream().anyMatch(t -> t.startsWith("Error"))) {
            System.err.println("API Error - Empty or invalid response for batch");
            return false;
        }

        // Verify all translations were received
        if (translations.size() != batch.size()) {
            System.err.println("API Error - Missing translations (expected " +
                    batch.size() + ", got " + translations.size() + ")");
            return false;
        }

        // Store translations
        for (int i = 0; i < batch.size(); i++) {
            translationFileManager.appendTranslation(batch.get(i), translations.get(i));
        }

        try {
            TimeUnit.SECONDS.sleep(5); // Rate limiting
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }

        return true;
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