package com.comicboys.project.client;

import com.comicboys.project.config.ConfigurationFile;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class VignetteGenerator {
    private final APIClient apiClient;
    private final String translationFilePath = "assets/translations/translations.tsv";
    private final TranslationFileManager translationFileManager;
    private final Mappings mappings;

    public VignetteGenerator(ConfigurationFile config, Mappings mappings) {
        this.apiClient = new APIClient(config);
        this.translationFileManager = new TranslationFileManager(translationFilePath);
        this.mappings = mappings;
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

    private void addToBatch(List<String> batch, List<String> textColumn) {
        batch.addAll(textColumn);
        batch.removeAll(List.of(""));
        // If batch reaches 5-10 items (adjustable), send it for translation
        if (batch.size() >= 5) {
            processBatch(batch);
            batch.clear(); // Clear batch after processing
        }
    }

    private void processBatch(List<String> batch) {
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
