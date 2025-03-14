package com.comicboys.project.client;

public class APIResponse {
    private final String textResponse;
    private final NumberedList numberedList;

    public APIResponse(String textResponse) {
        this.textResponse = textResponse;
        this.numberedList = null;
    }

    public APIResponse(NumberedList numberedList) {
        this.numberedList = numberedList;
        this.textResponse = null;
    }

    public boolean isNumberedList() {
        return numberedList != null;
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