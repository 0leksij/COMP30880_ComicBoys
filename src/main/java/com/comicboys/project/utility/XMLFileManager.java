package com.comicboys.project.utility;

import org.w3c.dom.NodeList;

import java.io.File;
import java.io.FileWriter;

public class XMLFileManager {

    public static void saveXMLToFile(String xmlContent, String filePath) {
        try {
            File file = new File(filePath);

            // Ensure the directory exists
            file.getParentFile().mkdirs();

            FileWriter writer = new FileWriter(file);
            writer.write(xmlContent);
            writer.close();

            System.out.println("XML saved successfully to: " + filePath);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error saving XML file.");
        }
    }
    
}
