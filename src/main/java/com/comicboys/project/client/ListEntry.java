package com.comicboys.project.client;

import java.util.List;

// each row has 5 columns, all of which contain a list of strings
public class ListEntry extends Entry<List<String>> {
    public ListEntry(List<String> leftPose, List<String> combinedText, List<String> leftText, List<String> rightPose, List<String> backgrounds) {
        super(leftPose, combinedText, leftText, rightPose, backgrounds);
    }
}
