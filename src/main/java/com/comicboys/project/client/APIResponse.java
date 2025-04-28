package com.comicboys.project.client;

import com.comicboys.project.data.NumberedList;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class APIResponse {
    private String textResponse = null;
    private NumberedList numberedList = null;

    public APIResponse(String responseContent) {
        // every list will have format "1. ", a number, period, and space, but may skip and not necessarily start at 1
        Pattern listFormat = Pattern.compile("[0-9]. ");
        if (listFormat.matcher(responseContent).find()) {
            this.numberedList = new NumberedList(parseNumberedList(responseContent));
        } else {
            this.textResponse = responseContent;
        }
    }

    public boolean isNumberedList() {
        return numberedList != null;
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

    public String getTextResponse() {
        if (textResponse == null) {
            throw new IllegalStateException("This response is a numbered list, not a text response.");
        }
        return textResponse;
    }

    public NumberedList getNumberedList() {
        if (numberedList == null) {
            throw new IllegalStateException("This response is a text response, not a numbered list.");
        }
        return numberedList;
    }

    @Override
    public String toString() {
        if (isNumberedList()) {
            return numberedList.toString();
        } else {
            return textResponse;
        }
    }

}