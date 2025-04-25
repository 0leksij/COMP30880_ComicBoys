package com.comicboys.project.io.translate;

import com.comicboys.project.client.APIClient;
import com.comicboys.project.data.Mappings;
import com.comicboys.project.io.config.ConfigurationFile;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class TranslationGenerator {
    private final APIClient apiClient;
    private String translationFilePath;
    private final TranslationFileManager translationFileManager;
    private final Mappings mappings;
    private int maxRetries;
    private int retryDelaySeconds ;
    private int batchSizeLimit;

    public TranslationGenerator(ConfigurationFile config, APIClient client, Mappings mappings) {
        this.apiClient = client;
        this.mappings = mappings;
        this.maxRetries = 5;
        this.retryDelaySeconds = 30;
        this.batchSizeLimit = 20;

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
    // calls the main generateTranslations method below with String input cast to ArrayList<String>
    public void generateTranslations(String text) {
        generateTranslations(new ArrayList<>(List.of(text)));
    }
    /**
     * Generates translations for a custom list of text fragments
     * @param texts List of text strings to translate
     */
    // for reference, pass input for this as a list of strings, so if translating mappings would
    // pass in mappings.getAllTextFragments
    public void generateTranslations(List<String> texts) {
        int attempt = 0;
        boolean success = false;

        while (attempt <= maxRetries && !success) {
            attempt++;
            System.out.println("Starting translation attempt " + attempt);

            try {
                texts.remove(""); // Remove empty strings if any
                boolean hadErrors = false;

                List<String> batch = new ArrayList<>();
                for (String text : texts) {
                    if (translationFileManager.translationExists(text)) {
                        continue;
                    }

                    batch.add(text);
                    if (batch.size() >= batchSizeLimit) {
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

    private boolean processBatchWithRetryPLural(List<String> batch) {
        List<String> processedBatch = new ArrayList<>();
        for (String text : batch) {
            if (text.equalsIgnoreCase("You (Plural)")) {
                processedBatch.add("You (Plural)"); // Mark it for special handling
            } else {
                processedBatch.add(text);
            }
        }

        List<String> translations = apiClient.sendBatchTranslationRequest(processedBatch);

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

        // Handle "You (Plural)" case explicitly
        for (int i = 0; i < batch.size(); i++) {
            String original = batch.get(i);
            String translated = translations.get(i);

            if (original.equalsIgnoreCase("You (Plural)")) {
                translated = "Vous" + " (plural)"; // Modify for correct plural form
            }

            translationFileManager.appendTranslation(original, translated);
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

    public void setMaxRetries(int num){
        maxRetries = num;
    }

    public void setRetryDelaySeconds(int num){
        retryDelaySeconds = num;
    }

    public void setBatchSizeLimit(int num){
        batchSizeLimit = num;
    }

    public int getMaxRetries(){
        return maxRetries;
    }

    public int getRetryDelaySeconds(){
        return retryDelaySeconds;
    }

    public int getBatchSizeLimit(){
        return batchSizeLimit;
    }
}