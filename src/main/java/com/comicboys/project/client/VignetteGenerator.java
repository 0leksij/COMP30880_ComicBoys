package com.comicboys.project.client;

import com.comicboys.project.config.ConfigurationFile;

import java.util.Map;

public class VignetteGenerator {
    private APIClient apiClient;
    private TranslationFileManager translationFileManager;
    private Mappings mappings;

    public VignetteGenerator(ConfigurationFile config, String translationFilePath) {
        this.apiClient = new APIClient(config);
        this.translationFileManager = new TranslationFileManager(translationFilePath);
        this.mappings = new TSVReader().getMappings();
    }

    public void generateTranslations(String targetLanguage) {
        // Iterate through all entries in the mappings
        for (Entry entry : mappings.getEntries()) {
            // Translate each source text fragment
            for (String sourceText : entry.combinedText) {
                APIResponse translation = apiClient.sendTranslationRequest(sourceText, targetLanguage);
                if (!translation.toString().startsWith("Error")) {
                    // Store the translation
                    translationFileManager.appendTranslation(sourceText, translation.toString());
                }
            }
        }
    }

    public Map<String, String> getTranslations() {
        return translationFileManager.loadTranslations();
    }

    public static void main(String[] args) {
        ConfigurationFile config = new ConfigurationFile();
        VignetteGenerator vignetteGenerator = new VignetteGenerator(config, "src/main/resources/translations.tsv");

        vignetteGenerator.generateTranslations("Italian");

        // Load and print translations
        Map<String, String> translations = vignetteGenerator.getTranslations();
        translations.forEach((source, target) -> System.out.println(source + " -> " + target));
    }
}
