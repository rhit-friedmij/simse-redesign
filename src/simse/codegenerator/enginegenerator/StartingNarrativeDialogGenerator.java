/*
 * This class is responsible for generating all of the code for the
 * StartingNarrativeDialog in the engine component of the simulation
 */

package simse.codegenerator.enginegenerator;

import simse.modelbuilder.startstatebuilder.CreatedObjects;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.JOptionPane;

import simse.codegenerator.*;

public class StartingNarrativeDialogGenerator 
implements CodeGeneratorConstants {
  private File directory; // directory to generate into
  private CreatedObjects createdObjs; // start state objects

  public StartingNarrativeDialogGenerator(CreatedObjects createdObjs, 
  		File directory) {
    this.directory = directory;
    this.createdObjs = createdObjs;
  }

  // causes the class to be generated
  public void generate() {
    File snFile = new File(directory,
        ("simse\\engine\\StartingNarrativeDialog.java"));
    if (snFile.exists()) {
      snFile.delete(); // delete old version of file
    }
    try {
      FileWriter writer = new FileWriter(snFile);
      writer
          .write("/* File generated by: simse.codegenerator.enginegenerator.StartingNarrativeDialogGenerator */");
      writer.write(NEWLINE);
      writer.write("package simse.engine;");
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      writer.write("import java.util.*;");
      writer.write(NEWLINE);
      writer.write("import javax.swing.*;");
      writer.write(NEWLINE);
      writer.write("import java.awt.event.*;");
      writer.write(NEWLINE);
      writer.write("import java.awt.Color;");
      writer.write(NEWLINE);
      writer.write("import java.awt.Dimension;");
      writer.write(NEWLINE);
      writer.write("import java.awt.Point;");
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      writer
          .write("public class StartingNarrativeDialog extends JDialog implements ActionListener");
      writer.write(NEWLINE);
      writer.write(OPEN_BRACK);
      writer.write(NEWLINE);

      // member variables:
      writer.write("private JTextArea textArea;");
      writer.write(NEWLINE);
      writer.write("private JButton okButton;");
      writer.write(NEWLINE);
      writer.write(NEWLINE);

      // constructor:
      writer.write("public StartingNarrativeDialog(JFrame owner)");
      writer.write(NEWLINE);
      writer.write(OPEN_BRACK);
      writer.write(NEWLINE);
      writer.write("super(owner, true);");
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      writer.write("setTitle(\"Welcome!\");");
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      writer.write("// Create main panel (box):");
      writer.write(NEWLINE);
      writer.write("Box mainPane = Box.createVerticalBox();");
      writer.write(NEWLINE);
      writer.write("mainPane.setMaximumSize(new Dimension(300, 300));");
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      writer.write("// Create text area panel:");
      writer.write(NEWLINE);
      writer.write("textArea = new JTextArea(15, 30);");
      writer.write(NEWLINE);
      writer.write("textArea.setLineWrap(true);");
      writer.write(NEWLINE);
      writer.write("textArea.setWrapStyleWord(true);");
      writer.write(NEWLINE);
      writer.write("textArea.setEditable(false);");
      writer.write(NEWLINE);
      writer.write("textArea.setText(\"");
      char[] startNarrChars = createdObjs.getStartingNarrative().
      	replaceAll("\n", "\\\\n").replaceAll("\"", "\\\\\"").toCharArray();
      for (int i = 0; i < startNarrChars.length; i++) {
        if (startNarrChars[i] == '\n') {
          writer.write("\" + \'\\n\' + \"");
        } else {
          writer.write(startNarrChars[i]);
        }
      }
      writer.write("\" + \"\");");
      writer.write(NEWLINE);
      writer.write("textArea.setCaretPosition(0);");
      writer.write(NEWLINE);
      writer
          .write("JScrollPane scrollPane = new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);");
      writer.write(NEWLINE);
      writer.write("mainPane.add(scrollPane);");
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      writer.write("// Create okButton pane and button:");
      writer.write(NEWLINE);
      writer.write("JPanel okButtonPane = new JPanel();");
      writer.write(NEWLINE);
      writer.write("okButton = new JButton(\"OK\");");
      writer.write(NEWLINE);
      writer.write("okButton.addActionListener(this);");
      writer.write(NEWLINE);
      writer.write("okButtonPane.add(okButton);");
      writer.write(NEWLINE);
      writer.write("mainPane.add(okButtonPane);");
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      writer.write("// Set main window frame properties:");
      writer.write(NEWLINE);
      writer.write("setBackground(Color.black);");
      writer.write(NEWLINE);
      writer.write("setContentPane(mainPane);");
      writer.write(NEWLINE);
      writer.write("validate();");
      writer.write(NEWLINE);
      writer.write("pack();");
      writer.write(NEWLINE);
      writer.write("repaint();");
      writer.write(NEWLINE);
      writer.write("toFront();");
      writer.write(NEWLINE);
      writer.write("// Make it show up in the center of the screen:");
      writer.write(NEWLINE);
      writer.write("Point ownerLoc = owner.getLocationOnScreen();");
      writer.write(NEWLINE);
      writer.write("Point thisLoc = new Point();");
      writer.write(NEWLINE);
      writer
          .write("thisLoc.setLocation((ownerLoc.getX() + (owner.getWidth() / 2) - (this.getWidth() / 2)), (ownerLoc.getY() + (owner.getHeight() / 2) - (this.getHeight() / 2)));");
      writer.write(NEWLINE);
      writer.write("setLocation(thisLoc);");
      writer.write(NEWLINE);
      writer.write("setVisible(true);");
      writer.write(NEWLINE);
      writer.write(CLOSED_BRACK);
      writer.write(NEWLINE);
      writer.write(NEWLINE);

      // methods:

      // actionPerformed method:
      writer
          .write("public void actionPerformed(ActionEvent evt) // handles user actions");
      writer.write(NEWLINE);
      writer.write(OPEN_BRACK);
      writer.write(NEWLINE);
      writer.write("Object source = evt.getSource();");
      writer.write(NEWLINE);
      writer.write("if(source == okButton)");
      writer.write(NEWLINE);
      writer.write(OPEN_BRACK);
      writer.write(NEWLINE);
      writer.write("setVisible(false);");
      writer.write(NEWLINE);
      writer.write("dispose();");
      writer.write(NEWLINE);
      writer.write(CLOSED_BRACK);
      writer.write(NEWLINE);
      writer.write(CLOSED_BRACK);
      writer.write(NEWLINE);

      writer.write(CLOSED_BRACK);
      writer.close();
    } catch (IOException e) {
      JOptionPane.showMessageDialog(null, ("Error writing file "
          + snFile.getPath() + ": " + e.toString()), "File IO Error",
          JOptionPane.WARNING_MESSAGE);
    }
  }
}