/*
 * This class is responsible for generating all of the code for the
 * PopupListener class for the GUI
 */

package simse.codegenerator.guigenerator;

import simse.codegenerator.CodeGeneratorConstants;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import javax.swing.JOptionPane;

public class PopupListenerGenerator implements CodeGeneratorConstants {
  private File directory; // directory to save generated code into

  public PopupListenerGenerator(File directory) {
    this.directory = directory;
  }

  public void generate() {
    File pulFile = new File(directory, ("simse\\gui\\PopupListener.java"));
    if (pulFile.exists()) {
      pulFile.delete(); // delete old version of file
    }
    try {
      FileWriter writer = new FileWriter(pulFile);
      FileReader reader = new FileReader("res\\static\\gui\\PopupListener.txt");
      Scanner s = new Scanner(reader);
      
      while (s.hasNextLine()) {
      	  writer.write(s.nextLine() + "\n");
      }
      
      writer.close();
      s.close();
    } catch (IOException e) {
      JOptionPane.showMessageDialog(null, ("Error writing file "
          + pulFile.getPath() + ": " + e.toString()), "File IO Error",
          JOptionPane.WARNING_MESSAGE);
    }
  }
}