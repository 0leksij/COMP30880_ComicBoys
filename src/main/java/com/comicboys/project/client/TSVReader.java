package com.comicboys.project.client;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class TSVReader {

 public static void main(String[] args) {
  BufferedReader reader;

  try {
   reader = new BufferedReader(new FileReader("src/main/resources/pose_pairings_with_backgrounds.tsv"));
   String line = reader.readLine();

   while (line != null) {
    System.out.println(line);
    // read next line
    line = reader.readLine();
   }

   reader.close();
  } catch (IOException e) {
   e.printStackTrace();
  }
 }

}