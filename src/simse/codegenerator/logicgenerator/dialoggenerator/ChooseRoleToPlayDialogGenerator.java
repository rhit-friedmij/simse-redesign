/*
 * This class is responsible for generating all of the code for the logic's
 * ChooseRoleToPlayDialog component
 */

package simse.codegenerator.logicgenerator.dialoggenerator;

import simse.codegenerator.CodeGeneratorConstants;
import simse.codegenerator.CodeGeneratorUtils;
import simse.modelbuilder.actionbuilder.ActionTypeParticipant;
import simse.modelbuilder.actionbuilder.ActionType;
import simse.modelbuilder.actionbuilder.ActionTypeTrigger;
import simse.modelbuilder.actionbuilder.DefinedActionTypes;
import simse.modelbuilder.actionbuilder.UserActionTypeTrigger;
import simse.modelbuilder.objectbuilder.SimSEObjectTypeTypes;
import simse.modelbuilder.rulebuilder.Rule;

import java.awt.Button;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.util.Vector;

import javax.lang.model.element.Modifier;
import javax.swing.JOptionPane;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

public class ChooseRoleToPlayDialogGenerator implements CodeGeneratorConstants {
  private File directory; // directory to generate into
  private File crtpdFile; // file to generate
  private DefinedActionTypes actTypes; // holds all of the defined action types

  public ChooseRoleToPlayDialogGenerator(DefinedActionTypes actTypes, 
  		File directory) {
    this.directory = directory;
    this.actTypes = actTypes;
  }

  public void generate() {
	  ClassName eventHandler = ClassName.get("javafx.event", "EventHandler");
	  ClassName mouseEvent = ClassName.get("javafx.scene.input", "MouseEvent");
	  ClassName vector = ClassName.get("java.util", "Vector");
	  ClassName ruleExecClass = ClassName.get("simse.logic", "RuleExecutor");
	  ClassName employeeClass = ClassName.get("simse.adts.objects", "Employee");
	  ClassName stateClass = ClassName.get("simse.state", "State");
	  ClassName actionClass = ClassName.get("simse.adts.actions", "Action");
	  ClassName buttonClass = ClassName.get("javafx.scene.control", "Button");
	  ClassName stageClass = ClassName.get("javafx.stage", "Stage");
	  ClassName dialogClass = ClassName.get("javafx.scene.control", "Dialog");
	  ClassName vBoxClass = ClassName.get("javafx.scene.layout", "VBox");
	  ClassName hBoxClass = ClassName.get("javafx.scene.layout", "HBox");
	  ClassName labelClass = ClassName.get("javafx.scene.control", "Label");
	  ClassName paneClass = ClassName.get("javafx.scene.layout", "Pane");
	  ClassName point2DClass = ClassName.get("javafx.geometry", "Point2D");
	  ClassName fxCollectionsClass = ClassName.get("javafx.collections", "FXCollections");
	  ClassName comboBoxClass = ClassName.get("javafx.scene.control", "ComboBox");
	  ClassName windowClass = ClassName.get("javafx.stage", "Window");
	  ClassName windowEvent = ClassName.get("javafx.stage", "WindowEvent");
	  ClassName alertClass = ClassName.get("javafx.scene.control", "Alert");
	  ClassName alertTypeClass = ClassName.get("javafx.scene.control.Alert", "AlertType");
	  ClassName stringClass = ClassName.get(String.class);
	  TypeName mouseHandler = ParameterizedTypeName.get(eventHandler, mouseEvent);
	  TypeName windowHandler = ParameterizedTypeName.get(eventHandler, windowEvent);
	  TypeName stringVector = ParameterizedTypeName.get(vector, stringClass);
	  
	  
	  TypeSpec exitListener = TypeSpec.classBuilder("ExitListener")
    		  .addModifiers(Modifier.PUBLIC)
    		  .addSuperinterface(windowHandler)
    		  .addMethod(MethodSpec.methodBuilder("handle")
    				  .addAnnotation(Override.class)
    				  .addModifiers(Modifier.PUBLIC)
    				  .returns(void.class)
    				  .addParameter(windowEvent, "evt")
    				  .addStatement("close()")
    				  .build())
    		  .build();
	  
	  MethodSpec roleConstructor = MethodSpec.constructorBuilder()
			  .addModifiers(Modifier.PUBLIC)
			  .addParameter(stageClass, "owner")
			  .addParameter(stringVector, "partNames")
			  .addParameter(employeeClass, "e")
			  .addParameter(actionClass, "act")
			  .addParameter(String.class, "menText")
			  .addParameter(ruleExecClass, "re")
			  .addParameter(stateClass, "state")
			  .addStatement("$N = owner", "gui")
			  .addStatement("$N = e", "emp")
			  .addStatement("$N = act", "action")
			  .addStatement("$N = menText", "menuText")
			  .addStatement("$N = re", "ruleExec")
			  .addStatement("this.$N = state", "state")
			  .addStatement("setTitle($S)", "Choose Action Role")
			  .addStatement("$T mainPane = new $T()", vBoxClass, vBoxClass)
			  .addStatement("$T topPane = new $T()", vBoxClass, vBoxClass)
			  .addStatement("topPane.getChildren().add(new $T($S))", labelClass, "Choose role to play:")
			  .addStatement("topPane.setMinWidth(300)")
			  .addStatement("$T middlePane = new $T()", paneClass, paneClass)
			  .addStatement("$N = new $T($T.observableList(partNames))", "partNameList", comboBoxClass, fxCollectionsClass)
			  .addStatement("middlePane.getChildren().add(partNameList)")
			  .addStatement("$T bottomPane = new $T()", hBoxClass, hBoxClass)
			  .addStatement("$N = new $T($S)", "okButton", buttonClass, "OK")
			  .addStatement("$N.addEventHandler($T.MOUSE_CLICKED, this)", "okButton", mouseEvent)
			  .addStatement("bottomPane.getChildren().add($N)", "okButton")
			  .addStatement("$N.setMinWidth(75)", "okButton")
			  .addStatement("$N = new $T($S)", "cancelButton", buttonClass, "Cancel")
			  .addStatement("$N.addEventHandler($T.MOUSE_CLICKED, this)", "cancelButton", mouseEvent)
			  .addStatement("bottomPane.getChildren().add($N)", "cancelButton")
			  .addStatement("$N.setMinWidth(75)", "cancelButton")
			  .addStatement("mainPane.getChildren().add(topPane)")
			  .addStatement("mainPane.getChildren().add(middlePane)")
			  .addStatement("mainPane.getChildren().add(bottomPane)")
			  .addStatement("$T ownerLoc = new $T(owner.getX(), owner.getY())", point2DClass, point2DClass)
			  .addStatement("$T thisLoc = new $T((ownerLoc.getX() + (owner.getWidth() / 2)"
			  		+ " - (this.getWidth() / 2)),\n (ownerLoc.getY() + (owner.getHeight() / 2)"
			  		+ " - (this.getHeight() / 2)))", point2DClass, point2DClass)
			  .addStatement("this.setX(thisLoc.getX())")
			  .addStatement("this.setY(thisLoc.getY())")
			  .addStatement("this.getDialogPane().getChildren().add(mainPane)")
			  .addStatement("this.getDialogPane().getScene().getWindow().setOnCloseRequest(new ExitListener())")
			  .addStatement("this.setResizable(true)")
			  .addStatement("this.getDialogPane().setPrefSize(400, middlePane.getChildren().size() * 60 + 100)")
			  .addStatement("this.setResizable(true)")
			  .beginControlFlow("if (partNames.size() == 1) ")
			  .addStatement("onlyOneRole()")
			  .nextControlFlow(" else")
			  .addStatement("showAndWait()")
			  .addStatement("$T window = this.getDialogPane().getScene().getWindow()", windowClass)
			  .addStatement("window.fireEvent(new $T(window, $T.WINDOW_CLOSE_REQUEST))", windowEvent, windowEvent)
			  .endControlFlow()
			  .build();
	  
      // make a Vector of all the action types with user triggers:
      Vector<ActionType> userTrigActs = new Vector<ActionType>();
      Vector<ActionType> allActs = actTypes.getAllActionTypes();
      for (int j = 0; j < allActs.size(); j++) {
        ActionType userAct = allActs.elementAt(j);
        Vector<ActionTypeTrigger> allTrigs = userAct.getAllTriggers();
        for (int k = 0; k < allTrigs.size(); k++) {
          ActionTypeTrigger tempTrig = allTrigs
              .elementAt(k);
          if (tempTrig instanceof UserActionTypeTrigger) {
            userTrigActs.add(userAct);
            break;
          }
        }
      }
	  
	  MethodSpec handle = MethodSpec.methodBuilder("handle")
			  .addAnnotation(Override.class)
			  .addModifiers(Modifier.PUBLIC)
			  .returns(void.class)
			  .addParameter(mouseEvent, "evt")
			  .addStatement("$T source = evt.getSource()", Object.class)
			  .beginControlFlow("if (source == $N)", "cancelButton")
			  .addStatement("$T window = this.getDialogPane().getScene().getWindow()", windowClass)
			  .addStatement("window.fireEvent(new $T(window, $T.WINDOW_CLOSE_REQUEST))", windowEvent, windowEvent)
			  .nextControlFlow("else if (source == $N)", "okButton")
			  .addStatement("$T partName = ($T)($N.getSelectionModel().getSelectedItem())", 
					  String.class, String.class, "partNameList")
			  .beginControlFlow("if (partName == null)")
			  .addStatement("$T alert = new $T($T.WARNING, $S)", alertClass, alertClass,
					  alertTypeClass, "You must choose at least one action")
			  .addStatement("alert.setTitle($S)", "Invalid Input")
			  .addStatement("alert.show()")
			  .nextControlFlow("else")
			  .addCode(generateActionHandle(userTrigActs))

			  .addStatement("$T window = this.getDialogPane().getScene().getWindow()", windowClass)
			  .addStatement("window.fireEvent(new $T(window, $T.WINDOW_CLOSE_REQUEST))", windowEvent, windowEvent)
			  .endControlFlow()
			  .endControlFlow()
			  .build();
	  
	  MethodSpec onlyOneRole = MethodSpec.methodBuilder("onlyOneRole")
			  .addModifiers(Modifier.PUBLIC)
			  .returns(void.class)
			  .addStatement("$T partName = ($T) ($N.getItems().get(0))", 
					  String.class, String.class, "partNameList")
			  .addCode(generateActionOnlyOneRole(userTrigActs))
			  .addStatement("close()")
			  .build();
	  
	  TypeSpec roleDialog = TypeSpec.classBuilder("ChooseRoleToPlayDialog")
			  .addModifiers(Modifier.PUBLIC)
			  .superclass(dialogClass)
			  .addSuperinterface(mouseHandler)
			  .addType(exitListener)
			  .addField(stageClass, "gui", Modifier.PRIVATE)
			  .addField(employeeClass, "emp", Modifier.PRIVATE)
			  .addField(actionClass, "action", Modifier.PRIVATE)
			  .addField(stateClass, "state", Modifier.PRIVATE)
			  .addField(String.class, "menuText", Modifier.PRIVATE)
			  .addField(ruleExecClass, "ruleExec", Modifier.PRIVATE)
			  .addField(comboBoxClass, "partNameList", Modifier.PRIVATE)
			  .addField(buttonClass, "okButton", Modifier.PRIVATE)
			  .addField(buttonClass, "cancelButton", Modifier.PRIVATE)
			  .addMethod(roleConstructor)
			  .addMethod(handle)
			  .addMethod(onlyOneRole)
			  .build();
	  
	  JavaFile javaFile = JavaFile.builder("", roleDialog)
			  .build();
	  
	  
    try {
      crtpdFile = new File(directory,
          ("simse\\logic\\dialogs\\ChooseRoleToPlayDialog.java"));
      if (crtpdFile.exists()) {
        crtpdFile.delete(); // delete old version of file
      }
      
      FileWriter writer = new FileWriter(crtpdFile);
	  String toAppend = "/* File generated by: simse.codegenerator.logicgenerator.dialoggenerator.ChooseRoleToPlayDialogGenerator */\n"
	  		+ "package simse.logic.dialogs;\n"
		  		+ "\n"
		  		+ "import simse.adts.actions.*;\n"
		  		+ "import simse.adts.objects.*;\n";
		  
	      writer.write(toAppend + javaFile.toString());
      writer.close();
    } catch (IOException e) {
        JOptionPane.showMessageDialog(null, ("Error writing file "
            + crtpdFile.getPath() + ": " + e.toString()), "File IO Error",
            JOptionPane.WARNING_MESSAGE);
    }

      // other constructor:
      /*
       * writer.write("public ChooseRoleToPlayDialog(JFrame owner, Vector
       * partNames, Employee e, simse.adts.actions.Action act, String
       * menText)"); writer.write(NEWLINE); writer.write(OPEN_BRACK);
       * writer.write(NEWLINE); writer.write("super(owner, true);");
       * writer.write(NEWLINE); writer.write("emp = e;"); writer.write(NEWLINE);
       * writer.write("action = act;"); writer.write(NEWLINE);
       * writer.write("menuText = menText;"); writer.write(NEWLINE);
       * writer.write("setTitle(\"Choose Action Role\");");
       * writer.write(NEWLINE); // main pane: writer.write("Box mainPane =
       * Box.createVerticalBox();"); writer.write(NEWLINE); // top pane:
       * writer.write("JPanel topPane = new JPanel();"); writer.write(NEWLINE);
       * writer.write("topPane.add(new JLabel(\"Choose role to play:\"));");
       * writer.write(NEWLINE); writer.write("JPanel middlePane = new
       * JPanel();"); writer.write(NEWLINE); writer.write("partNameList = new
       * JComboBox(partNames);"); writer.write(NEWLINE);
       * writer.write("middlePane.add(partNameList);"); writer.write(NEWLINE);
       *  // bottom pane: writer.write("JPanel bottomPane = new JPanel();");
       * writer.write(NEWLINE); writer.write("okButton = new JButton(\"OK\");");
       * writer.write(NEWLINE);
       * writer.write("okButton.addActionListener(this);");
       * writer.write(NEWLINE); writer.write("bottomPane.add(okButton);");
       * writer.write(NEWLINE); writer.write("cancelButton = new
       * JButton(\"Cancel\");"); writer.write(NEWLINE);
       * writer.write("cancelButton.addActionListener(this);");
       * writer.write(NEWLINE); writer.write("bottomPane.add(cancelButton);");
       * writer.write(NEWLINE);
       *  // add panes to main pane: writer.write("mainPane.add(topPane);");
       * writer.write(NEWLINE); writer.write("mainPane.add(middlePane);");
       * writer.write(NEWLINE); writer.write("mainPane.add(bottomPane);");
       * writer.write(NEWLINE);
       *  // Set main window frame properties:
       * writer.write("setContentPane(mainPane);"); writer.write(NEWLINE);
       * writer.write("validate();"); writer.write(NEWLINE);
       * writer.write("pack();"); writer.write(NEWLINE);
       * writer.write("repaint();"); writer.write(NEWLINE);
       * writer.write("toFront();"); writer.write(NEWLINE); writer.write("Point
       * ownerLoc = owner.getLocationOnScreen();"); writer.write(NEWLINE);
       * writer.write("Point thisLoc = new Point();"); writer.write(NEWLINE);
       * writer.write("thisLoc.setLocation((ownerLoc.getX() + (owner.getWidth() /
       * 2) - (this.getWidth() / 2)), (ownerLoc.getY() + (owner.getHeight() / 2) -
       * (this.getHeight() / 2)));"); writer.write(NEWLINE);
       * writer.write("setLocation(thisLoc);"); writer.write(NEWLINE);
       * writer.write("if(partNames.size() == 1)"); writer.write(NEWLINE);
       * writer.write(OPEN_BRACK); writer.write(NEWLINE);
       * writer.write("onlyOneRole();"); writer.write(NEWLINE);
       * writer.write(CLOSED_BRACK); writer.write(NEWLINE);
       * writer.write("else"); writer.write(NEWLINE); writer.write(OPEN_BRACK);
       * writer.write(NEWLINE); writer.write("setVisible(true);");
       * writer.write(NEWLINE); writer.write(CLOSED_BRACK);
       * writer.write(NEWLINE); writer.write(CLOSED_BRACK);
       * writer.write(NEWLINE);
       */

  }
  
  private String generateActionHandle(Vector<ActionType> userTrigActs) {
	  String actions = "";
	  
      for (int i = 0; i < userTrigActs.size(); i++) {
        ActionType tempAct = userTrigActs.elementAt(i);
        actions += "if(action instanceof "
            + CodeGeneratorUtils.getUpperCaseLeading(tempAct.getName()) + 
            "Action)\n{\n";

        // go through all the action's user triggers:
        boolean putElse = false;
        Vector<ActionTypeTrigger> allTrigs = tempAct.getAllTriggers();
        for (int j = 0; j < allTrigs.size(); j++) {
          ActionTypeTrigger outerTrig = allTrigs.elementAt(j);
          if ((outerTrig instanceof UserActionTypeTrigger)
              && (outerTrig.getTriggerText() != null)
              && (outerTrig.getTriggerText().length() > 0)) {
            if (putElse) {
              actions += "else ";
            } else {
              putElse = true;
            }
            actions += "if(menuText.equals(\""
                + ((UserActionTypeTrigger) outerTrig).getMenuText() + "\"))\n{\n";
            actions += "emp.setOverheadText(\""
                    + ((UserActionTypeTrigger) outerTrig).getTriggerText()
                    + "\", state);\n}\n";
          }
        }

        // go through all employee participants:
        Vector<ActionTypeParticipant> allParts = tempAct.getAllParticipants();
        int numEmpParts = 0;
        for (int j = 0; j < allParts.size(); j++) {
          ActionTypeParticipant part = allParts.elementAt(j);
          if (part.getSimSEObjectTypeType() == SimSEObjectTypeTypes.EMPLOYEE) { 
          	// employee participant
            if (numEmpParts > 0) { // not on first element
              actions += "else ";
            }
            numEmpParts++;
            actions += "if(partName.equals(\"" + part.getName() + "\"))\n{\n";
            actions += "((" + CodeGeneratorUtils.getUpperCaseLeading(
            		tempAct.getName())+ "Action)action).add" + part.getName() + 
            		"(emp);\n";

            // go through all trigger rules and execute rules that are to be
            // executed on each join:
            Vector<Rule> rules = tempAct.getAllTriggerRules();
            for (int k = 0; k < rules.size(); k++) {
              Rule tempRule = rules.elementAt(k);
              if (tempRule.getExecuteOnJoins() == true) {
                actions += "ruleExec.update(gui, RuleExecutor.UPDATE_ONE, "
                    + "\"" + tempRule.getName() + "\", action);\n";
              }
            }
            actions += "}\n";
          }
        }
        actions += "}\n";
      }
      
      return actions;
  }
  
  private String generateActionOnlyOneRole(Vector<ActionType> userTrigActs) {
	  String actions = "";
      for (int i = 0; i < userTrigActs.size(); i++) {
          ActionType tempAct = userTrigActs.elementAt(i);
          if (i > 0) { // not on first element
            actions += "else ";
          }
          actions += "if(action instanceof "
              + CodeGeneratorUtils.getUpperCaseLeading(tempAct.getName()) + 
              "Action)\n{\n";

          // go through all the actions user triggers:
          boolean putElse = false;
          Vector<ActionTypeTrigger> allTrigs = tempAct.getAllTriggers();
          for (int j = 0; j < allTrigs.size(); j++) {
            ActionTypeTrigger outerTrig = allTrigs.elementAt(j);
            if ((outerTrig instanceof UserActionTypeTrigger)
                && (outerTrig.getTriggerText() != null)
                && (outerTrig.getTriggerText().length() > 0)) {
              if (putElse) {
                actions += "else ";
              } else {
                putElse = true;
              }
              actions += "if(menuText.equals(\""
                  + ((UserActionTypeTrigger) outerTrig).getMenuText() + "\"))\n{\n";
              actions += "emp.setOverheadText(\"" + outerTrig.getTriggerText()
                  + "\", state);\n}\n";
            }
          }

          // go through all employee participants:
          Vector<ActionTypeParticipant> allParts = tempAct.getAllParticipants();
          int numEmpParts = 0;
          for (int j = 0; j < allParts.size(); j++) {
            ActionTypeParticipant part = allParts.elementAt(j);
            if (part.getSimSEObjectTypeType() == SimSEObjectTypeTypes.EMPLOYEE) { 
            	// employee participant
              if (numEmpParts > 0) { // not on first element
                actions += "else ";
              }
              numEmpParts++;
              actions += "if(partName.equals(\"" + part.getName() + "\"))\n{\n";
              actions += "((" + CodeGeneratorUtils.getUpperCaseLeading(
              		tempAct.getName()) + "Action)action).add" + part.getName() + 
              		"(emp);\n";

              // go through all trigger rules and execute rules that are to be
              // executed on each join:
              Vector<Rule> rules = tempAct.getAllTriggerRules();
              for (int k = 0; k < rules.size(); k++) {
                Rule tempRule = rules.elementAt(k);
                if (tempRule.getExecuteOnJoins() == true) {
                  actions += "ruleExec.update(gui, RuleExecutor.UPDATE_ONE, "
                      + "\"" + tempRule.getName() + "\", action);\n";
                }
              }
              actions += "}\n";
            }
          }
          actions += "}\n";
        }
      
      return actions;
  }
}