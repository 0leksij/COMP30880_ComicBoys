package com.comicboys.project;

import com.comicboys.project.io.config.ConfigurationFile;
import com.comicboys.project.utility.LessonScheduler;
import java.util.Random;

public class Main {
    public static Random random = new Random();

    public static void main(String[] args) {
        ConfigurationFile config = new ConfigurationFile();
        LessonScheduler.generateCompleteLesson(config);
    }
}
