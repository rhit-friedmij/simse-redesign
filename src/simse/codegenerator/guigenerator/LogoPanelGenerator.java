/*
 * This class is responsible for generating all of the code for the Logo panel
 * in the GUI
 */

package simse.codegenerator.guigenerator;

import simse.codegenerator.CodeGeneratorConstants;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import javax.swing.JOptionPane;;

public class LogoPanelGenerator implements CodeGeneratorConstants {
  private File directory; // directory to save generated code into

  public LogoPanelGenerator(File directory) {
    this.directory = directory;
  }

  public void generate() {
    File logoPanelFile = new File(directory, ("simse\\gui\\LogoPanel.java"));
    if (logoPanelFile.exists()) {
      logoPanelFile.delete(); // delete old version of file
    }
    try {
      FileWriter writer = new FileWriter(logoPanelFile);
      FileReader reader = new FileReader("res\\static\\gui\\LogoPanel.txt");
      Scanner s = new Scanner(reader);
      
      while (s.hasNextLine()) {
      	  writer.write(s.nextLine() + "\n");
      }
      
      s.close();
      writer.close();
    } catch (IOException e) {
      JOptionPane.showMessageDialog(null, ("Error writing file "
          + logoPanelFile.getPath() + ": " + e.toString()), "File IO Error",
          JOptionPane.WARNING_MESSAGE);
    }
  }
}