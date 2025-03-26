package com.comicboys.project.io;

import com.comicboys.project.client.APIClient;
import com.comicboys.project.data.Mappings;

import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class TranslationFileManager {
    private String translationFilePath;

    public TranslationFileManager(String filePath) {
        this.translationFilePath = filePath;
    }

    // Append a translation to the file
    public void appendTranslation(String sourceText, String targetText) {
        // Ensure the file exists
        ensureFileExists();

        if (translationExists(sourceText)) {
            System.out.println("Translation already exists for: " + sourceText);
            return;
        }

        try (FileWriter writer = new FileWriter(translationFilePath, true)) {
            writer.write(sourceText + "\t" + targetText + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    boolean translationExists(String sourceText) {
        ensureFileExists(); // Ensure the file exists before reading
        try (BufferedReader reader = new BufferedReader(new FileReader(translationFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\t");
                if (parts.length >= 1 && parts[0].equals(sourceText)) {
                    return true; // Translation already exists
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false; // Translation does not exist
    }

    // Load translations into memory
    public Map<String, String> loadTranslations() {
        ensureFileExists(); // Ensure the file exists before reading
        Map<String, String> translations = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(translationFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\t");
                if (parts.length == 2) {
                    translations.put(parts[0], parts[1]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return translations;

    }

    // Ensure the file exists
    private void ensureFileExists() {
        File file = new File(translationFilePath);
        if (!file.exists()) {
            try {
                if (file.createNewFile()) {
                    System.out.println("Created file: " + translationFilePath);
                } else {
                    System.err.println("Failed to create file: " + translationFilePath);
                }
            } catch (IOException e) {
                System.err.println("Error creating file: " + e.getMessage());
            }
        }
    }

    public static class TranslationGenerator {
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
            Set<String> allTexts = new HashSet<>();
            allTexts.addAll(mappings.getCombinedText());
            allTexts.addAll(mappings.getLeftText());
            allTexts.remove(""); // Remove empty strings

            // Process in batches
            List<String> batch = new ArrayList<>();
            for (String text : allTexts) {
                batch.add(text);
                if (batch.size() >= 5) { // Batch size of 5
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
                TimeUnit.SECONDS.sleep(3);
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
    }
}