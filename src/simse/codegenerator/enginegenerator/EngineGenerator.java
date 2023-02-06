/*
 * This class is responsible for generating all of the code for the engine
 * component of the simulation
 */

package simse.codegenerator.enginegenerator;

import simse.codegenerator.CodeGeneratorConstants;
import simse.codegenerator.CodeGeneratorUtils;
import simse.modelbuilder.ModelOptions;
import simse.modelbuilder.objectbuilder.Attribute;
import simse.modelbuilder.objectbuilder.AttributeTypes;
import simse.modelbuilder.objectbuilder.SimSEObjectTypeTypes;
import simse.modelbuilder.startstatebuilder.CreatedObjects;
import simse.modelbuilder.startstatebuilder.InstantiatedAttribute;
import simse.modelbuilder.startstatebuilder.SimSEObject;

import java.util.Vector;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.lang.model.element.Modifier;
import javax.swing.JOptionPane;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;

public class EngineGenerator implements CodeGeneratorConstants {
  private File directory; // directory to generate into
  private File engineFile; // file to generate
  private CreatedObjects createdObjs; // start state objects
  private StartingNarrativeDialogGenerator sndg;

  public EngineGenerator(ModelOptions options, CreatedObjects createdObjs) {
    directory = options.getCodeGenerationDestinationDirectory();
    this.createdObjs = createdObjs;
    sndg = new StartingNarrativeDialogGenerator(createdObjs, directory);
  }

  // causes the engine component to be generated
  public void generate() {
    // generate starting narrative dialog:
    sndg.generate();

    // generate Engine:
    ClassName timertask = ClassName.get("java.util", "TimerTask");
    ClassName timeline = ClassName.get("javafx.animation", "Timeline");
    ClassName keyframe = ClassName.get("javafx.animation", "KeyFrame");
    ClassName actionevent = ClassName.get("javafx.event", "ActionEvent");
    ClassName eventhandler = ClassName.get("javafx.event", "EventHandler");
    ClassName duration = ClassName.get("javafx.util", "Duration");
    ClassName acustomer = ClassName.get("simse.adts.objects", "ACustomer");
    ClassName automatedtestingtool = ClassName.get("simse.adts.objects", "AutomatedTestingTool");
    ClassName code = ClassName.get("simse.adts.objects", "Code");
    ClassName designdocument = ClassName.get("simse.adts.objects", "DesignDocument");
    ClassName designenvironment = ClassName.get("simse.adts.objects", "DesignEnvironment");
    ClassName ide = ClassName.get("simse.adts.objects", "IDE");
    ClassName requirementscapturetool = ClassName.get("simse.adts.objects", "RequirementsCaptureTool");
    ClassName requirementsdocument = ClassName.get("simse.adts.objects", "RequirementsDocument");
    ClassName seproject = ClassName.get("simse.adts.objects", "SEProject");
    ClassName softwareengineer = ClassName.get("simse.adts.objects", "SoftwareEngineer");
    ClassName systemtestplan = ClassName.get("simse.adts.objects", "SystemTestPlan");
    ClassName simsegui = ClassName.get("simse.gui", "SimSEGUI");
    ClassName logic = ClassName.get("simse.logic", "Logic");
    ClassName state = ClassName.get("simse.state", "State");
    
    Vector<SimSEObject> objs = createdObjs.getAllObjects();
    for (int i = 0; i < objs.size(); i++) {
      StringBuffer strToWrite = new StringBuffer();
      SimSEObject tempObj = objs.elementAt(i);
      String objTypeName = CodeGeneratorUtils.getUpperCaseLeading(
      		tempObj.getSimSEObjectType().getName());
      strToWrite.append(objTypeName + " a" + i + " = new " + objTypeName
          + "(");
      Vector<Attribute> atts = 
      	tempObj.getSimSEObjectType().getAllAttributes();
      if (atts.size() == tempObj.getAllAttributes().size()) { // all 
      																												// attributes
      																											  // are 
      																											  // instantiated
        boolean validObj = true;
        // go through all attributes:
        for (int j = 0; j < atts.size(); j++) {
          Attribute att = atts.elementAt(j);
          InstantiatedAttribute instAtt = 
          	tempObj.getAttribute(att.getName()); // get
																									 // the
																									 // corresponding
																									 // instantiated
																									 // attribute
          if (instAtt == null) { // no corresponding instantiated attribute
            validObj = false;
            break;
          }
          if (instAtt.isInstantiated()) { // attribute has a value
            if (instAtt.getAttribute().getType() == AttributeTypes.STRING) {
              strToWrite.append("\"" + instAtt.getValue() + "\"");
            } else { // boolean, int, or double
              strToWrite.append(instAtt.getValue().toString());
            }
            if (j < (atts.size() - 1)) { // not on last element
              strToWrite.append(", ");
            }
          } else { // attribute does not have a value -- invalidates entire
										// object
            validObj = false;
            break;
          }
        }
        if (validObj) { // if valid, finish writing:
          writer.write(strToWrite + ");");
          writer.write(NEWLINE);
          writer.write("state.get"
              + SimSEObjectTypeTypes.getText(tempObj.getSimSEObjectType()
                  .getType()) + "StateRepository().get" + objTypeName
              + "StateRepository().add(a" + i + ");");
          writer.write(NEWLINE);
        }
      }
    }
    
    MethodSpec engineConstructor = MethodSpec.constructorBuilder()
    		.addModifiers(Modifier.PUBLIC)
    		.addStatement("numSteps = 0")
    		.addStatement("logic = l")
    		.addStatement("state = s")
    		.addCode("$L", "\n")
    		.addStatement("timer = new Timeline(new KeyFrame(Duration.millis(50), this))")
    		.addStatement("timer.setCycleCount(Timeline.INDEFINITE)")
    		.addStatement("timer.setDelay(Duration.millis(100))")
    		.addStatement("timer.play()")
    		.addCode("$L", "\n")
    		.addStatement("//CodeBloc")
    		.build();
    
    try {
    	engineFile = new File(directory, ("simse\\engine\\Engine.java"));
        if (engineFile.exists()) {
          engineFile.delete(); // delete old version of file
        }
      FileWriter writer = new FileWriter(engineFile);
      writer
          .write("/* File generated by: simse.codegenerator.enginegenerator.EngineGenerator */");
      writer.write(NEWLINE);
      writer.write("package simse.engine;");
      writer.write(NEWLINE);
      writer.write("import simse.adts.objects.*;");
      writer.write(NEWLINE);
      writer.write("import simse.logic.*;");
      writer.write(NEWLINE);
      writer.write("import simse.state.*;");
      writer.write(NEWLINE);
      writer.write("import simse.gui.*;");
      writer.write(NEWLINE);
      writer.write("import java.util.*;");
      writer.write(NEWLINE);
      writer.write("import javax.swing.*;");
      writer.write(NEWLINE);
      writer.write("import java.awt.event.*;");
      writer.write(NEWLINE);
      writer.write("import javax.swing.Timer;");
      writer.write(NEWLINE);
      writer.write(NEWLINE);

      writer.write("public class Engine implements ActionListener");
      writer.write(NEWLINE);
      writer.write(OPEN_BRACK);
      writer.write(NEWLINE);

      // member variables:
      writer.write("private Logic logic;");
      writer.write(NEWLINE);
      writer.write("private State state;");
      writer.write(NEWLINE);
      writer.write("private SimSEGUI gui;");
      writer.write(NEWLINE);
      writer.write("private int numSteps;");
      writer.write(NEWLINE);
      writer.write("private boolean stopClock;");
      writer.write(NEWLINE);
      writer.write("private boolean stopAtEvents;");
      writer.write(NEWLINE);
      writer.write("private Timer timer;");
      writer.write(NEWLINE);

      // constructor:
      writer.write("public Engine(Logic l, State s)");
      writer.write(NEWLINE);
      writer.write(OPEN_BRACK);
      writer.write(NEWLINE);
      writer.write("numSteps = 0;");
      writer.write("logic = l;");
      writer.write(NEWLINE);
      writer.write("state = s;");
      writer.write(NEWLINE);
      writer.write("timer = new Timer(50, this);");
      writer.write(NEWLINE);
      // startup script: go through each object in the start state, create it,
      // and add it to the simulation:
      Vector<SimSEObject> objs = createdObjs.getAllObjects();
      for (int i = 0; i < objs.size(); i++) {
        StringBuffer strToWrite = new StringBuffer();
        SimSEObject tempObj = objs.elementAt(i);
        String objTypeName = CodeGeneratorUtils.getUpperCaseLeading(
        		tempObj.getSimSEObjectType().getName());
        strToWrite.append(objTypeName + " a" + i + " = new " + objTypeName
            + "(");
        Vector<Attribute> atts = 
        	tempObj.getSimSEObjectType().getAllAttributes();
        if (atts.size() == tempObj.getAllAttributes().size()) { // all 
        																												// attributes
        																											  // are 
        																											  // instantiated
          boolean validObj = true;
          // go through all attributes:
          for (int j = 0; j < atts.size(); j++) {
            Attribute att = atts.elementAt(j);
            InstantiatedAttribute instAtt = 
            	tempObj.getAttribute(att.getName()); // get
																									 // the
																									 // corresponding
																									 // instantiated
																									 // attribute
            if (instAtt == null) { // no corresponding instantiated attribute
              validObj = false;
              break;
            }
            if (instAtt.isInstantiated()) { // attribute has a value
              if (instAtt.getAttribute().getType() == AttributeTypes.STRING) {
                strToWrite.append("\"" + instAtt.getValue() + "\"");
              } else { // boolean, int, or double
                strToWrite.append(instAtt.getValue().toString());
              }
              if (j < (atts.size() - 1)) { // not on last element
                strToWrite.append(", ");
              }
            } else { // attribute does not have a value -- invalidates entire
										// object
              validObj = false;
              break;
            }
          }
          if (validObj) { // if valid, finish writing:
            writer.write(strToWrite + ");");
            writer.write(NEWLINE);
            writer.write("state.get"
                + SimSEObjectTypeTypes.getText(tempObj.getSimSEObjectType()
                    .getType()) + "StateRepository().get" + objTypeName
                + "StateRepository().add(a" + i + ");");
            writer.write(NEWLINE);
          }
        }
      }
      writer.write(CLOSED_BRACK);
      writer.write(NEWLINE);

      // methods:

      // giveGUI method:
      writer.write("public void giveGUI(SimSEGUI g)");
      writer.write(NEWLINE);
      writer.write(OPEN_BRACK);
      writer.write(NEWLINE);
      writer.write("gui = g;");
      writer.write(NEWLINE);
      writer
          .write("new StartingNarrativeDialog(gui);");
      writer.write(NEWLINE);
      writer.write(CLOSED_BRACK);
      writer.write(NEWLINE);
      writer.write(NEWLINE);

      // actionPerformed method:
      writer.write("public void actionPerformed(ActionEvent ae)");
      writer.write(NEWLINE);
      writer.write(OPEN_BRACK);
      writer.write(NEWLINE);
      writer.write("if(isRunning())");
      writer.write(NEWLINE);
      writer.write(OPEN_BRACK);
      writer.write(NEWLINE);
      writer.write("gui.getAttributePanel().getClockPanel().setAdvClockImage();");
      writer.write(NEWLINE);
      writer.write("if(state.getClock().isStopped())");
      writer.write(NEWLINE);
      writer.write(OPEN_BRACK);
      writer.write(NEWLINE);
      writer.write("numSteps = 0;");
      writer.write(NEWLINE);
      writer.write(CLOSED_BRACK);
      writer.write(NEWLINE);
      writer.write("else");
      writer.write(NEWLINE);
      writer.write(OPEN_BRACK);
      writer.write(NEWLINE);
      writer.write("gui.getAttributePanel().setGUIChanged();");
      writer.write(NEWLINE);
      writer.write("state.getLogger().update();");
      writer.write(NEWLINE);
      writer.write("logic.update(gui);");
      writer.write(NEWLINE);
      writer.write("gui.update();");
      writer.write(NEWLINE);
      writer.write("numSteps--;");
      writer.write(NEWLINE);
      writer
          .write("if(stopAtEvents && gui.getWorld().overheadTextDisplayed())");
      writer.write(NEWLINE);
      writer.write(OPEN_BRACK);
      writer.write(NEWLINE);
      writer.write("stopClock = true;");
      writer.write(NEWLINE);
      writer.write("numSteps = 0;");
      writer.write(NEWLINE);
      writer.write(CLOSED_BRACK);
      writer.write(NEWLINE);
      writer.write(CLOSED_BRACK);
      writer.write(NEWLINE);
      writer.write(CLOSED_BRACK);
      writer.write(NEWLINE);
      writer.write("else");
      writer.write(NEWLINE);
      writer.write(OPEN_BRACK);
      writer.write(NEWLINE);
      writer.write("gui.getAttributePanel().getClockPanel().resetAdvClockImage();");
      writer.write(NEWLINE);
      writer.write(CLOSED_BRACK);
      writer.write(NEWLINE);
      writer.write(CLOSED_BRACK);
      writer.write(NEWLINE);
      writer.write(NEWLINE);

      // isRunning method
      writer.write("public boolean isRunning()");
      writer.write(NEWLINE);
      writer.write(OPEN_BRACK);
      writer.write(NEWLINE);
      writer.write("return numSteps > 0;");
      writer.write(NEWLINE);
      writer.write(CLOSED_BRACK);
      writer.write(NEWLINE);
      writer.write(NEWLINE);

      // setStopAtEvents method
      writer.write("public void setStopAtEvents(boolean t)");
      writer.write(NEWLINE);
      writer.write(OPEN_BRACK);
      writer.write(NEWLINE);
      writer.write("stopClock = false;");
      writer.write(NEWLINE);
      writer.write("stopAtEvents = t;");
      writer.write(NEWLINE);
      writer.write(CLOSED_BRACK);
      writer.write(NEWLINE);
      writer.write(NEWLINE);

      // setSteps method
      writer.write("public void setSteps(int ns)");
      writer.write(NEWLINE);
      writer.write(OPEN_BRACK);
      writer.write(NEWLINE);
  		writer.write("timer.restart();");
  		writer.write(NEWLINE);
  		writer.write("numSteps += ns;");
  		writer.write(NEWLINE);
      writer.write(CLOSED_BRACK);
      writer.write(NEWLINE);
      writer.write(NEWLINE);

      // stop method
      writer.write("public void stop()");
      writer.write(NEWLINE);
      writer.write(OPEN_BRACK);
      writer.write(NEWLINE);
      writer.write("numSteps = 0;");
      writer.write(NEWLINE);
      writer.write("timer.stop();");
      writer.write(NEWLINE);
      writer.write(CLOSED_BRACK);
      writer.write(NEWLINE);
      writer.write(NEWLINE);

      // stopClock method
      writer.write("public boolean stopClock()");
      writer.write(NEWLINE);
      writer.write(OPEN_BRACK);
      writer.write(NEWLINE);
      writer.write("return stopClock;");
      writer.write(NEWLINE);
      writer.write(CLOSED_BRACK);
      writer.write(NEWLINE);
      
      // getTimer method
    	writer.write("public Timer getTimer() {");
    	writer.write(NEWLINE);
    	writer.write("return timer;");
    	writer.write(NEWLINE);
    	writer.write(CLOSED_BRACK);
      
      writer.write(NEWLINE);
      writer.write(CLOSED_BRACK);
      writer.close();
    } catch (IOException e) {
      JOptionPane.showMessageDialog(null, ("Error writing file "
          + engineFile.getPath() + ": " + e.toString()), "File IO Error",
          JOptionPane.WARNING_MESSAGE);
    }
  }
}