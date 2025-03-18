package com.comicboys.project.client;

import com.comicboys.project.Main;

import java.util.*;

public class Mappings {
    // will represent our mapping tsv file, where each index is row index, and each Entry is the row
    private ArrayList<Entry> mappings = new ArrayList<>();
    // add a new mapping row, input row of tsv file with columns: leftPose, combinedText, leftText, rightPose, backgrounds
    public void addEntry(String rowData) {
        String[] parsedRow = rowData.split("\t");
        mappings.add(new Entry(parsedRow));
    }
    public boolean isEmpty() {
        return mappings.isEmpty();
    }
    // finds FIRST match where either leftText or combinedText contains word
    // returns a hashmap with key-value pairs for leftPose, combinedText, etc.
    // (since ONLY finds first match, will not check any row after that may match, so may need to also add randomness
    //  for it to pick out of all possible row options, but this is a decent start)
    public Map<String, String> findMatch(String text) {
        for (Entry entry : mappings) {
            // assigning entry variables shorter names for readability
            List<String> leftPose = entry.leftPose;
            List<String> combinedText = entry.combinedText;
            List<String> leftText = entry.leftText;
            List<String> rightPose = entry.rightPose;
            List<String> backgrounds = entry.backgrounds;
            // if word we are looking for is in either text columns is a match
            if(combinedText.contains(text) || leftText.contains(text)) {
                // return a hashmap with 1:1 values, for each column picks a random word from column list
                return new HashMap<>() {{
                    put("leftPose", leftPose.get(Main.random.nextInt(leftPose.size())));
                    put("combinedText", combinedText.get(Main.random.nextInt(combinedText.size())));
                    put("leftText", leftText.get(Main.random.nextInt(leftText.size())));
                    put("rightPose", rightPose.get(Main.random.nextInt(rightPose.size())));
                    put("backgrounds", backgrounds.get(Main.random.nextInt(backgrounds.size())));
                }};

            }
        }
        return Map.of();
    }
    public String toString() {
        String result = "";
        for (Entry entry : mappings) {
            result += entry.toString();
        }
        return result;
    }
    // each row has 5 columns, all of which can contain a list of strings (hence the array String[] for each)
    private class Entry {
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
}
