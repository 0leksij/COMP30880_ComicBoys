package com.comicboys.project.io;

import com.comicboys.project.audio.AudioGenerator;
import com.comicboys.project.utility.XMLFileManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.util.Map;

public class XMLAudioInserter extends Blueprint {

    private final Map<String, String> audioFileMap;
    // default filename for audio panel
    private String outputFileName = "sample_audio.xml";

    public XMLAudioInserter(String filePath, Map<String, String> audioFileMap) {
        super(filePath);
        this.audioFileMap = audioFileMap;
    }
    public XMLAudioInserter(String filePath, Map<String, String> audioFileMap, String outputFileName) {
        super(filePath);
        this.audioFileMap = audioFileMap;
        // if no extension, add it
        if (!outputFileName.contains(".xml")) {
            outputFileName += ".xml";
        }
        this.outputFileName = outputFileName;
    }


    public void insertAudio() {
        // get document element from superclass
        Document doc = this.getFile();
        // first make sure all panels only have one speech balloon each
        XMLFileManager.separateMultipleSpeechPanels(doc);
        NodeList scenes = XMLFileManager.selectElements(doc, "scene");
        // going through each scene
        addAudioForScenes(scenes);
        // save edited document to XML file
        String fileDirectory = XMLFileManager.getFileDirectory(getFilePath());
        String outputFilePath = fileDirectory + outputFileName; // use filename when saving
        XMLFileManager.saveXMLToFile(getFile(), outputFilePath);
        System.out.println("\nXML with audio tags saved in: " + outputFilePath);
    }
    // for each scene
    private void addAudioForScenes(NodeList scenes) {
        // for each <scene> element
        for (int i = 0; i < scenes.getLength(); i++) {
            Node scene = scenes.item(i);
            if (scene.getNodeType() == Node.ELEMENT_NODE) {
                NodeList panels = ((Element) scene).getElementsByTagName("panel");
                addAudioBalloonsForPanels(panels);
            }
        }
    }
    private void addAudioBalloonsForPanels(NodeList panels) {
        // look through all <panel> elements
        for(int j = 0; j < panels.getLength(); j++) {
            Node panel = panels.item(j);
            // ensure is valid element node
            if (panel.getNodeType() == Node.ELEMENT_NODE) {
                NodeList balloons = ((Element) panel).getElementsByTagName("balloon");
                addBalloonTextAudioInPanel(balloons);
            }
        }
    }
    // find valid balloon element in list of balloon nodes
    private void addBalloonTextAudioInPanel(NodeList balloons) {
        for (int k = 0; k < balloons.getLength(); k++) {
            Node balloon = balloons.item(k);
            if (balloon.getNodeType() == Node.ELEMENT_NODE) {
                // panel is the parent of character, which is the parent of balloon, use method in XMLFileManager
                Node panel = XMLFileManager.getParent(XMLFileManager.getParent(balloon));
                // since XMLFileManager.getParent can be null
                if (panel != null) {
                    // is balloon, so look for its text content in audio map
                    String audioKey = balloon.getTextContent().trim();
                    // in case there is no balloon
                    if (!audioKey.isEmpty()) {
                        createNewAudioChildOfPanel(panel, audioKey);
                    }
                    break; // to ensure no more audio tags added to panel in case there is an extra balloon
                }
            }
        }
    }
    // given panel and key for audio map, add audio tag with filename as text content, otherwise do not add audio tag
    private void createNewAudioChildOfPanel(Node panel, String audioKey) {
        // return null if no key found
        String audioFileName = audioFileMap.getOrDefault(audioKey, null);
        // if returned value was null, do not add audio tag
        if (audioFileName != null) {
            Document doc = panel.getOwnerDocument();
            // create new audio with file name obtained from audio map
            Element newAudio = doc.createElement("audio");
            // by default always .mp3 so append that to filename
            newAudio.setTextContent(audioFileName);
            panel.appendChild(newAudio);
        }
    }
}
