package com.comicboys.project.client;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import com.comicboys.project.config.ConfigurationFile;
import com.comicboys.project.utility.DenialChecker;
import org.json.JSONObject;

public class APIClient {
    private String apiKey;
    private String model;
    private String completionsUrl;

    public APIClient(ConfigurationFile config) {
        this.apiKey = config.getProperty("API_KEY");
        this.model = config.getProperty("MODEL");
        this.completionsUrl = config.getProperty("COMPLETIONS_URL");
    }


    public String sendPrompt(String prompt) {
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
                return "Error: " + responseCode;
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
            return "Error: " + e.getMessage();
        }
    }

    private String parseResponse(String prompt, String jsonResponse) {
        JSONObject response = new JSONObject(jsonResponse);
        String content = response.getJSONArray("choices")
                .getJSONObject(0)
                .getJSONObject("message")
                .getString("content");


        // Check for denial of service response with context
        if (DenialChecker.isDenialOfService(prompt, content)) return "[Error] OpenAI denied the request: " + content;
        return content;
    }

}

