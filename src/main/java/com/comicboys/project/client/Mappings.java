package com.comicboys.project.client;

import com.comicboys.project.Main;
import static com.comicboys.project.client.MappingsColumn.*;

import java.util.*;


public class Mappings {
    // will represent our mapping tsv file, where each index is row index, and each Entry is the row
    private final ArrayList<Entry> mappings = new ArrayList<>();
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
    public Map<MappingsColumn, String> findMatch(String text) {
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
                    put(LEFT_POSE, leftPose.get(Main.random.nextInt(leftPose.size())));
                    put(COMBINED_TEXT, combinedText.get(Main.random.nextInt(combinedText.size())));
                    put(LEFT_TEXT, leftText.get(Main.random.nextInt(leftText.size())));
                    put(RIGHT_POSE, rightPose.get(Main.random.nextInt(rightPose.size())));
                    put(BACKGROUNDS, backgrounds.get(Main.random.nextInt(backgrounds.size())));
                }};

            }
        }
        return Map.of();
    }


    public ArrayList<Entry> getEntries(){
        return mappings;
    }

    public String toString() {
        String result = "";
        for (Entry entry : mappings) {
            result += entry.toString();
        }
        return result;
    }

    public List<String> getAllTextFragments() {
        List<String> allTexts = new ArrayList<>();
        for (Entry entry : mappings) {
            allTexts.addAll(entry.combinedText); // Collect all unique text fragments
        }
        return new ArrayList<>(new HashSet<>(allTexts)); // Remove duplicates
    }

    String getAllTextFragmentsAsString(boolean includeCombinedText) {
        Set<String> uniqueTexts = new HashSet<>();

        for (Entry entry : mappings) {
            if (includeCombinedText) uniqueTexts.addAll(entry.combinedText);
            else uniqueTexts.addAll(entry.leftText);
        }

        return String.join(",", uniqueTexts); // Convert set to a comma-separated string
    }
    public String getCombinedText(){return getAllTextFragmentsAsString(true);}
    public String getLeftText(){return getAllTextFragmentsAsString(false);}

}
