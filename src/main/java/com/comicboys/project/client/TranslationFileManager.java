package com.comicboys.project.client;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class TranslationFileManager {
    private String translationFilePath;

    public TranslationFileManager(String filePath) {
        this.translationFilePath = filePath;
    }

    // Append a translation to the file
    public void appendTranslation(String sourceText, String targetText) {

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

    private boolean translationExists(String sourceText){
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
}