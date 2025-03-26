package com.comicboys.project.client;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.List;

import com.comicboys.project.io.ConfigurationFile;
import com.comicboys.project.utility.DenialChecker;
import org.json.JSONObject;

public class APIClient {
    private String apiKey;
    private String model;
    private String completionsUrl;
    private String sourceLanguage;
    private String targetLanguage;

    public APIClient(ConfigurationFile config) {
        this.apiKey = config.getProperty("API_KEY");
        this.model = config.getProperty("MODEL");
        this.completionsUrl = config.getProperty("COMPLETIONS_URL");
        this.sourceLanguage = config.getProperty("SOURCE_LANGUAGE");
        this.targetLanguage = config.getProperty("TARGET_LANGUAGE");
    }


    public APIResponse sendPrompt(String prompt) {
        try {
            URL url = new URL(completionsUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + apiKey);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            // Creates JSON request body
            JSONObject requestBody = new JSONObject();
            requestBody.put("model", model);
            requestBody.put("messages", new org.json.JSONArray()
                    .put(new JSONObject().put("role", "user").put("content", prompt)));

            // Sends request
            try (OutputStream os = connection.getOutputStream()) {
                os.write(requestBody.toString().getBytes());
                os.flush();
            }

            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                if (responseCode == 401) {
                    System.out.println("Invalid API Key in config.properties file!");
                }
                return new APIResponse("Error: " + responseCode);
            }

            // Reads response from OpenAI API
            Scanner scanner = new Scanner(connection.getInputStream());
            StringBuilder response = new StringBuilder();
            while (scanner.hasNext()) {
                response.append(scanner.nextLine());
            }
            scanner.close();

            return parseResponse(prompt, response.toString()); // a small change here to also get users prompt for context check
        } catch (Exception e) {
            return new APIResponse("Error: " + e.getMessage());
        }
    }

    private APIResponse parseResponse(String prompt, String jsonResponse) {
        JSONObject response = new JSONObject(jsonResponse);
        String content = response.getJSONArray("choices")
                .getJSONObject(0)
                .getJSONObject("message")
                .getString("content");


        // Check for denial of service response with context
        if (DenialChecker.isDenialOfService(prompt, content))
            return new APIResponse("[Error] OpenAI denied the request: " + content);
        return new APIResponse(content);
    }

    public APIResponse sendTranslationRequest(String sourceText) {
        try {
            String prompt = String.format("Translate the following text into %s: %s. Use only one translation/word and use no quotation marks. Send it back as a numbered list if needed.", targetLanguage, sourceText);

            APIResponse response = sendPrompt(prompt);

            return new APIResponse(response.toString().trim()); // Remove any leading/trailing whitespace

        } catch (Exception e) {
            return new APIResponse("Error: " + e.getMessage());
        }
    }

    public List<String> sendBatchTranslationRequest(List<String> sourceTexts) {
        List<String> translations = new ArrayList<>();

        try {
            // Build the prompt with numbered format
            StringBuilder promptBuilder = new StringBuilder();
            promptBuilder.append(String.format("Translate the following words/phrases into %s:", targetLanguage));

            for (int i = 0; i < sourceTexts.size(); i++) {
                promptBuilder.append(String.format("\n%d. %s", i + 1, sourceTexts.get(i))); // Numbered list input
            }

            promptBuilder.append("\nRespond with a numbered list matching the input order.");

            APIResponse response = sendPrompt(promptBuilder.toString());

            if (response.toString().startsWith("Error")) {
                System.err.println("Translation API error: " + response);
                return translations; // Return empty list on error
            }

            // Check if response is a numbered list and extract translations
            if (response.isNumberedList()) {
                translations = response.getNumberedList().getItems();
            } else {
                System.err.println("Unexpected response format: " + response);
                return translations; // Return empty list if format is incorrect
            }

            // Ensure output list size matches input size (fallback: placeholder text)
            while (translations.size() < sourceTexts.size()) {
                translations.add("[Translation missing]");
            }

        } catch (Exception e) {
            System.err.println("Error in batch translation: " + e.getMessage());
        }

        return translations;
    }


}



