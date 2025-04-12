package com.comicboys.project.io;

import com.comicboys.project.utility.XMLFileManager;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public abstract class Blueprint {
    private final String filePath;
    private final Document file;
    // by default will want to select <comic> tag
    public Blueprint(String filePath) {
        this.filePath = filePath;
        file = XMLFileManager.loadXMLFromFile(this.filePath);
    }
    public String getFilePath() { return filePath; }
    public Document getFile() { return file; }
    // to select a certain tag
    protected NodeList selectElements(String element) {
        return XMLFileManager.selectElements(file, element);
    }

}
