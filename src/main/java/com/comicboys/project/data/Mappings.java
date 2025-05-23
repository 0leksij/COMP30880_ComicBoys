package com.comicboys.project.data;

import java.util.*;


public class Mappings {
    // will represent our mapping tsv file, where each index is row index, and each ListEntry is the row
    private final ArrayList<ListEntry> mappings = new ArrayList<>();
    // add a new mapping row, input row of tsv file with columns: leftPose, combinedText, leftText, rightPose, backgrounds
    public void addEntry(String rowData) {
        String[] row = rowData.split("\t");
        // this needs to be done in the case that a row has less than required number of columns,
        // to avoid IndexOutOfBounds when accessing row[i]
        String[] fullRow = {"", "", "", "", ""};
        for (int i = 0; i < row.length; i++) { fullRow[i] = row[i]; }
        mappings.add(new ListEntry(
                processColumn(fullRow[0]),
                processColumn(fullRow[1]),
                processColumn(fullRow[2]),
                processColumn(fullRow[3]),
                processColumn(fullRow[4])
        ));
    }
    // each column passed into here is in string format, so:
    //      "to fall in love, love"
    // and this method processes it to a list:
    //      ["to fall in love", "love"]
    private List<String> processColumn(String column) {
        return List.of(column.toLowerCase().replace(", ", ",").split(","));
    }
    public boolean isEmpty() {
        return mappings.isEmpty();
    }
    // finds FIRST match where either leftText or combinedText contains word using method in ListEntry class
    public StringEntry findMatch(String text) {
        if (text.isEmpty()) { return null; }
        for (ListEntry entry : mappings) {
            // assigning entry variables shorter names for readability, and needed for Entry of strings constructor
            List<String> entryCombinedText = entry.getCombinedText();
            List<String> entryLeftText = entry.getLeftText();
            // if word we are looking for is in either text columns is a match, so can call method to construct it
            if(entryLeftText.contains(text) || entryCombinedText.contains(text)) {
                return entry.toStringEntry(text);
            }
        }
        return null;
    }
    public int size() { return mappings.size(); }


    public ArrayList<ListEntry> getEntries(){
        return mappings;
    }

    public String toString() {
        String result = "";
        for (ListEntry entry : mappings) {
            result += entry.toString();
        }
        return result;
    }

    public List<String> getAllTextFragments() {
        List<String> allTexts = new ArrayList<>();
        for (ListEntry entry : mappings) {
            allTexts.addAll(entry.getCombinedText()); // Collect all unique text fragments
        }
        return new ArrayList<>(new HashSet<>(allTexts)); // Remove duplicates
    }

    Set<String> getAllTextFragments(boolean includeCombinedText) {
        Set<String> uniqueTexts = new HashSet<>();

        for (ListEntry entry : mappings) {
            if (includeCombinedText) uniqueTexts.addAll(entry.getCombinedText());
            else uniqueTexts.addAll(entry.getLeftText());
        }

        return uniqueTexts; // Convert set to a comma-separated string
    }
    public Set<String> getCombinedText(){return getAllTextFragments(true);}
    public Set<String> getLeftText(){return getAllTextFragments(false);}

    public List<String> getAllLeftPoses() {
        Set<String> leftPoses = new HashSet<>();
        for (ListEntry entry : mappings) {
            leftPoses.addAll(entry.getLeftPose());
        }
        return new ArrayList<>(leftPoses);
    }

    public List<String> getAllBackgrounds() {
        Set<String> backgrounds = new HashSet<>();
        for (ListEntry entry : mappings) {
            backgrounds.addAll(entry.getBackgrounds());
        }
        return new ArrayList<>(backgrounds);
    }

}
