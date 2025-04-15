package com.comicboys.project.io;
import java.io.*;
import java.util.*;

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
}