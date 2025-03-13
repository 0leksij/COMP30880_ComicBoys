package com.comicboys.project;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
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
                return "Error: " + responseCode;
            }

            // Reads response from OpenAI API
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
        String content = response.getJSONArray("choices")
                .getJSONObject(0)
                .getJSONObject("message")
                .getString("content");

        if(isNumberedList(content)){
            return new NumberedList(parseNumberedList(content)).toString();
        }

        return content;
    }

    // Extracts a numbered list from the response text
    private List<String> parseNumberedList(String text){
        List<String> list = new ArrayList<>();
        String[] lines = text.split("\n");
        for(String line : lines) {
            line = line.trim();
            if (line.matches("\\d+\\. .*")){
                list.add(line.substring(line.indexOf('.')+1).trim());
            }
        }
        return list;
    }

    private boolean isNumberedList(String response){
        return response.contains("1.");
    }

    class NumberedList{
        private List<String> items;

        public NumberedList(List<String> items){
            this.items = items;
        }

        // Retrieves an item from the list by its 1-based index
        public String getItems(int index){
            if (index < 1 || index > items.size()){
                throw new IndexOutOfBoundsException("Invalid index: " + index);
            }
            return items.get(index - 1);
        }

        @Override
        public String toString(){
            StringBuilder sb = new StringBuilder();
            for(int i = 0; i < items.size(); i++) {
                sb.append((i + 1)).append(". ").append(items.get(i)).append("\n");
            }
            return sb.toString();
        }
    }
}

