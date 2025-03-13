package com.comicboys.project;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import com.comicboys.project.config.ConfigurationFile;
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

            JSONObject requestBody = new JSONObject();
            requestBody.put("model", model);
            requestBody.put("messages", new org.json.JSONArray()
                    .put(new JSONObject().put("role", "user").put("content", prompt)));

            try (OutputStream os = connection.getOutputStream()) {
                os.write(requestBody.toString().getBytes());
                os.flush();
            }

            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                return "Error: " + responseCode;
            }

            Scanner scanner = new Scanner(connection.getInputStream());
            StringBuilder response = new StringBuilder();
            while (scanner.hasNext()) {
                response.append(scanner.nextLine());
            }
            scanner.close();

            return parseResponse(response.toString());
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    private String parseResponse(String jsonResponse) {
        JSONObject response = new JSONObject(jsonResponse);
        return response.getJSONArray("choices")
                .getJSONObject(0)
                .getJSONObject("message")
                .getString("content");
    }
}

