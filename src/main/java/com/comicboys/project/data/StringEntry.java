package com.comicboys.project.data;

// each row has 5 columns, but these will be the single strings picked from each column which may have multiple
public class StringEntry extends Entry<String> {
    public StringEntry(String leftPose, String combinedText, String leftText, String rightPose, String backgrounds) {
        super(leftPose, combinedText, leftText, rightPose, backgrounds);
    }

    public StringEntry() {
        super("", "", "", "", ""); // All fields initialized to empty strings
    }
}
