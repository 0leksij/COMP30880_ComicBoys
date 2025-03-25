package com.comicboys.project.client;

import java.util.List;

public interface IVignetteSchema {
    List<String> getLeftPoses();
    List<String> getRightPoses();
    List<String> getLeftText();
    List<String> getCombinedText();
    List<String> getSettingSuggestions();

    String getRandomLeftPose();
    String getRandomRightPose();
    String getRandomBackground();
    String getRandomCombinedText();
    String getRandomLeftText();
    }



