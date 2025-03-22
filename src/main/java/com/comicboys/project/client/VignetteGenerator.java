package com.comicboys.project.client;

import com.comicboys.project.config.ConfigurationFile;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class VignetteGenerator {
    private APIClient apiClient;
    private String translationFilePath = "src/main/resources/translations.tsv";
    private TranslationFileManager translationFileManager;
    private Mappings mappings;

    public VignetteGenerator(ConfigurationFile config, Mappings mappings) {
        this.apiClient = new APIClient(config);
        this.translationFileManager = new TranslationFileManager(translationFilePath);
        this.mappings = mappings;
    }

    public void generateTranslations() {
        List<String> batch = new ArrayList<>();

        for (Entry entry : mappings.getEntries()) {

            batch.addAll(entry.combinedText);

            batch.removeAll(List.of(""));

            // If batch reaches 5-10 items (adjustable), send it for translation
            if (batch.size() >= 5) {
                processBatch(batch);
                System.out.println(batch);
                batch.clear(); // Clear batch after processing
            }

            batch.addAll(entry.leftText);

            batch.removeAll(List.of(""));

            if (batch.size() >= 5) {
                processBatch(batch);
                System.out.println(batch);
                batch.clear(); // Clear batch after processing

            }

        }

        // Send any remaining items in the batch
        if (!batch.isEmpty()) {
            processBatch(batch);
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
