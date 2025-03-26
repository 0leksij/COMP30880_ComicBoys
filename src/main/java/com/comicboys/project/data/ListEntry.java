package com.comicboys.project.data;

import com.comicboys.project.Main;

import java.util.List;

// each row has 5 columns, all of which contain a list of strings
public class ListEntry extends Entry<List<String>> {
    public ListEntry(List<String> leftPose, List<String> combinedText, List<String> leftText, List<String> rightPose, List<String> backgrounds) {
        super(leftPose, combinedText, leftText, rightPose, backgrounds);
    }
    // pass in a word to pick that (so not random) from is respective column
    public StringEntry toStringEntry(String word) {
        String combinedText;
        String leftText;
        // check if word is in combined text or left text, otherwise random
        if (getCombinedText().contains(word)) {
            combinedText = word;
            leftText = getRandomWord(getLeftText());
        } else if (getLeftText().contains(word)) {
            combinedText = getRandomWord(getCombinedText());
            leftText = word;
        }
        // this should never be called but if the word doesn't exist it will result in same as random
        else {
            combinedText = getRandomWord(getCombinedText());
            leftText = getRandomWord(getLeftText());
        }
        // new StringEntry instance
        return new StringEntry(
                getRandomWord(getLeftPose()),
                combinedText,
                leftText,
                getRandomWord(getRightPose()),
                getRandomWord(getBackgrounds())
        );
    }
    // pick random word from each column
    public StringEntry toStringEntry() {
        return new StringEntry(
                getRandomWord(getLeftPose()),
                getRandomWord(getCombinedText()),
                getRandomWord(getLeftText()),
                getRandomWord(getRightPose()),
                getRandomWord(getBackgrounds())
        );
    }
    // get random word from list of possible words
    private String getRandomWord(List<String> wordList) {
        return wordList.get(Main.random.nextInt(wordList.size()));
    }
}
