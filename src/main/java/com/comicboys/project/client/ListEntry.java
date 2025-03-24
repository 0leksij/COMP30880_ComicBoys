package com.comicboys.project.client;

import java.util.List;

// each row has 5 columns, all of which can contain a list of strings (hence the array String[] for each)
public class ListEntry {
    final List<String> leftPose;
    final List<String> combinedText;
    final List<String> leftText;
    final List<String> rightPose;
    final List<String> backgrounds;
    public Entry(String[] row) {
        // this needs to be done in the case that a row has less than required number of columns,
        // to avoid IndexOutOfBounds when accessing row[i]
        String[] fullRow = {"", "", "", "", ""};
        for (int i = 0; i < row.length; i++) {
            fullRow[i] = row[i];
        }
        // fullRow has all 5 columns, so can safely access all indices
        leftPose = processColumn(fullRow[0]);
        combinedText = processColumn(fullRow[1]);
        leftText = processColumn(fullRow[2]);
        rightPose = processColumn(fullRow[3]);
        backgrounds = processColumn(fullRow[4]);
    }
    // each column passed into here is in string format, so:
    //      "to fall in love, love"
    // and this method processes it to a list:
    //      ["to fall in love", "love"]
    private List<String> processColumn(String column) {
        return List.of(column.toLowerCase().replace(", ", ",").split(","));
    }
    public String toString() {
        return "\n[" + leftPose.toString() + ", " + combinedText.toString() + ", " +
                leftText.toString() + ", " + rightPose.toString() + ", " +
                backgrounds.toString() + "]";
    }
}
