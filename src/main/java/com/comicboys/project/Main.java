package com.comicboys.project;

import com.comicboys.project.client.APIClient;
import com.comicboys.project.data.Mappings;
import com.comicboys.project.io.config.ConfigurationFile;
import com.comicboys.project.io.config.MappingsFileReader;
import com.comicboys.project.io.translate.TranslationGenerator;
import com.comicboys.project.io.xml.XMLGenerator;
import com.comicboys.project.utility.LessonScheduler;
import com.comicboys.project.utility.XMLFileManager;
import org.w3c.dom.Document;

import java.util.Random;

public class Main {
    public static Random random = new Random();

    public static void main(String[] args) {
        ConfigurationFile config = new ConfigurationFile();
        LessonScheduler.generateCompleteLesson(config);
    }
}
