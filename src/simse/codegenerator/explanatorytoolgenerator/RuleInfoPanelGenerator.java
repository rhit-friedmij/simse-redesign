/*
 * This class is responsible for generating all of the code for the
 * RuleInfoPanel class in the explanatory tool
 */

package simse.codegenerator.explanatorytoolgenerator;

import simse.codegenerator.CodeGeneratorConstants;
import simse.codegenerator.CodeGeneratorUtils;
import simse.modelbuilder.actionbuilder.ActionType;
import simse.modelbuilder.actionbuilder.DefinedActionTypes;
import simse.modelbuilder.rulebuilder.Rule;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

import javax.lang.model.element.Modifier;
import javax.swing.JOptionPane;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

public class RuleInfoPanelGenerator implements CodeGeneratorConstants {
  private File directory; // directory to save generated code into
  private DefinedActionTypes actTypes;

  public RuleInfoPanelGenerator(DefinedActionTypes actTypes, File directory) {
    this.actTypes = actTypes;
    this.directory = directory;
  }

  public void generate() {
    File ruleInfoFile = new File(directory,
        ("simse\\explanatorytool\\RuleInfoPanel.java"));
    if (ruleInfoFile.exists()) {
      ruleInfoFile.delete(); // delete old version of file
    }
    try {
      FileWriter writer = new FileWriter(ruleInfoFile);
      writer
          .write("/* File generated by: simse.codegenerator.explanatorytoolgenerator.RuleInfoPanelGenerator */");
      writer.write(NEWLINE);
      writer.write("package simse.explanatorytool;");
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      writer.write("import simse.adts.actions.*;");
      writer.write(NEWLINE);
      writer.write("import simse.explanatorytool.RuleDescriptions;");
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      writer.write("import javax.swing.*;");
      writer.write(NEWLINE);
      writer.write("import javax.swing.event.ListSelectionEvent;");
      writer.write(NEWLINE);
      writer.write("import javax.swing.event.ListSelectionListener;");
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      writer.write("import java.awt.Dimension;");
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      writer
          .write("public class RuleInfoPanel extends JPanel implements ListSelectionListener {");
      writer.write(NEWLINE);
      
      ClassName eventHandler = ClassName.get("javafx.event", "EventHandler");
      ClassName pos = ClassName.get("javafx.geometry", "Pos");
      ClassName listView = ClassName.get("javafx.scene.control", "ListView");
      ClassName string = ClassName.get("java.lang.Object", "String");
      ClassName scrollPane = ClassName.get("javafx.scene.control", "ScrollPane");
      ClassName scrollBarPolicy = ClassName.get("javafx.scene.control.ScrollPane", "ScrollBarPolicy");
      ClassName selectionMode = ClassName.get("javafx.scene.control", "SelectionMode");
      ClassName textArea = ClassName.get("javafx.scene.control", "TextArea");
      ClassName titledPane = ClassName.get("javafx.scene.control", "TitledPane");
      ClassName toolTip = ClassName.get("javafx.scene.control", "Tooltip");
      ClassName mouseEvent = ClassName.get("javafx.scene.input", "MouseEvent");
      ClassName gridPane = ClassName.get("javafx.scene.layout", "GridPane");
      ClassName pane = ClassName.get("javafx.scene.layout", "Pane");
      ClassName vBox = ClassName.get("javafx.scene.layout", "VBox");
      ClassName stage = ClassName.get("javafx.stage", "Stage");
      ClassName actionClass = ClassName.get("simse.adts.actions", "Action");
      ClassName ruleCategories = ClassName.get("simse.util", "RuleCategories");
      
  	MethodSpec constructor = MethodSpec.constructorBuilder()
			.addModifiers(Modifier.PUBLIC)
			.addParameter(stage, "owner")
			.addParameter(actionClass, "action")
			.addStatement("this.action = action")
			.addStatement("$T mainPane = new $T()", gridPane, gridPane)
			.addStatement("mainPane.setPrefSize(900,600)")
			.addStatement("mainPane.setHgap(10)")
			.addStatement("$T tRulePane = new $T()", vBox, vBox)
			.addStatement("$T trigRuleTitlePane = new $T(\"Trigger Rules:\", tRulePane", titledPane, titledPane)
			.addStatement("triggerRuleList = new $T<$T>()", listView, string)
			.addStatement("triggerRuleList.setFixedCellSize(24)")
			.addStatement("triggerRuleList.getSelectionModel().setSelectionMode($T.SINGLE)", selectionMode)
			.addStatement("triggerRuleList.addEventHandler($T.MOUSE_CLICKED, this);", mouseEvent)
			.addStatement("$T triggerRuleListPane = new $T(triggerRuleList)", scrollPane, scrollPane)
			.addStatement("triggerRuleListPane.setMaxHeight(100)")
			.addStatement("triggerRuleListPane.setVbarPolicy($T.AS_NEEDED)", scrollBarPolicy)
			.addStatement("triggerRuleListPane.setHbarPolicy($T.NEVER);", scrollBarPolicy)
			.addStatement("$T trigToolTip = \"Rules that execute at the beginning of the action\"", string)
			.addStatement("trigRuleTitlePane.setTooltip(new $T(trigToolTip))", toolTip)
			.addStatement("triggerRuleList.setTooltip(new $T(trigToolTip))", toolTip)
			.addStatement("tRulePane.getChildren().add(triggerRuleListPane)")
			.addStatement("$T dRulePane = new $T()", vBox, vBox)
			.addStatement("$T destRuleTitlePane = new $T(\"Trigger Rules:\", dRulePane", titledPane, titledPane)
			.addStatement("destroyerRuleList = new $T<$T>()", listView, string)
			.addStatement("destroyerRuleList.setFixedCellSize(24)")
			.addStatement("destroyerRuleList.getSelectionModel().setSelectionMode($T.SINGLE)", selectionMode)
			.addStatement("destroyerRuleList.addEventHandler($T.MOUSE_CLICKED, this);", mouseEvent)
			.addStatement("$T destroyerRuleListPane = new $T(destroyerRuleList)", scrollPane, scrollPane)
			.addStatement("destroyerRuleListPane.setMaxHeight(100)")
			.addStatement("destroyerRuleListPane.setVbarPolicy($T.AS_NEEDED)", scrollBarPolicy)
			.addStatement("destroyerRuleListPane.setHbarPolicy($T.NEVER);", scrollBarPolicy)
			.addStatement("$T destToolTip = \"Rules that execute at the beginning of the action\"", string)
			.addStatement("destRuleTitlePane.setTooltip(new $T(destToolTip))", toolTip)
			.addStatement("destroyerRuleList.setTooltip(new $T(destToolTip))", toolTip)
			.addStatement("dRulePane.getChildren().add(destroyerRuleListPane)")
			.addStatement("$T iRulePane = new $T()", vBox, vBox)
			.addStatement("$T intRuleTitlePane = new $T(\"Trigger Rules:\", iRulePane", titledPane, titledPane)
			.addStatement("intermediateRuleList = new $T<$T>()", listView, string)
			.addStatement("intermediateRuleList.setFixedCellSize(24)")
			.addStatement("intermediateRuleList.getSelectionModel().setSelectionMode($T.SINGLE)", selectionMode)
			.addStatement("intermediateRuleList.addEventHandler($T.MOUSE_CLICKED, this);", mouseEvent)
			.addStatement("$T intermediateRuleListPane = new $T(intermediateRuleList)", scrollPane, scrollPane)
			.addStatement("intermediateRuleListPane.setMaxHeight(100)")
			.addStatement("intermediateRuleListPane.setVbarPolicy($T.AS_NEEDED)", scrollBarPolicy)
			.addStatement("intermediateRuleListPane.setHbarPolicy($T.NEVER);", scrollBarPolicy)
			.addStatement("$T intToolTip = \"Rules that execute at the beginning of the action\"", string)
			.addStatement("intRuleTitlePane.setTooltip(new $T(intToolTip))", toolTip)
			.addStatement("intermediateRuleList.setTooltip(new $T(intToolTip))", toolTip)
			.addStatement("iRulePane.getChildren().add(intermediateRuleListPane)")
			.addStatement("$T rulePane = new $T()", vBox, vBox)
			.addStatement("rulePane.getChildren().add(trigRuleTitlePane)")
			.addStatement("rulePane.getChildren().add(destRuleTitlePane)")
			.addStatement("rulePane.getChildren().add(intRuleTitlePane)")
			.addStatement("initializeRuleLists()")
			.addStatement("// description pane:")
			.addStatement("$T descriptionPane = new $T()", vBox, vBox)
			.addStatement("$T descriptionTitlePane = new $T(\"Description:\", descriptionPane)", titledPane, titledPane)
			.addStatement("// description text area:")
			.addStatement("descriptionArea = new $T()", textArea)
			.addStatement("descriptionArea.setPrefColumnCount(29)")
			.addStatement("descriptionArea.setPrefRowCount(25)")
			.addStatement("descriptionArea.setWrapText(true)")
			.addStatement("descriptionArea.setEditable(false)")
			.addStatement("$T descriptionScrollPane = new $T(descriptionArea)", scrollPane, scrollPane)
			.addStatement("descriptionScrollPane.setHbarPolicy($T.NEVER)", scrollBarPolicy)
			.addStatement("descriptionScrollPane.setVbarPolicy($T.AS_NEEDED)", scrollBarPolicy)
			.addStatement("descriptionPane.getChildren().add(descriptionScrollPane)")
			.addStatement("mainPane.add(rulePane, 0, 0)")
			.addStatement("mainPane.add(descriptionTitlePane, 1, 0, 1, 3)")
			.addStatement("mainPane.setAlignment($T.CENTER)", pos)
			.addStatement("this.getChildren().add(mainPane)")
			.build();
  	
  	String initalizeRuleListCodeBlock = "";
  	
    // go through all actions:
    Vector<ActionType> actions = actTypes.getAllActionTypes();
    boolean writeElse = false;
    for (ActionType actionItem : actions) {

    	
      if (actionItem.isVisibleInExplanatoryTool()) {
        if (writeElse) {
//          writer.write("else ");
        	initalizeRuleListCodeBlock += "else ";
        } else {
          writeElse = true;
        }
        initalizeRuleListCodeBlock += "if (action instanceof "
                + CodeGeneratorUtils.getUpperCaseLeading(actionItem.getName()) + 
                "Action) {";
//        writer.write("if (action instanceof "
//            + CodeGeneratorUtils.getUpperCaseLeading(actionItem.getName()) + 
//            "Action) {");
//        writer.write(NEWLINE);
        initalizeRuleListCodeBlock += "\n";
        Vector<Rule> trigRules = actionItem.getAllTriggerRules();
        
        
        if (trigRules.size() > 0) {
        	initalizeRuleListCodeBlock += "String[] trigList = {"; 
//          writer.write("String[] trigList = {");
//          writer.write(NEWLINE);
        	initalizeRuleListCodeBlock += "\n";

          // go through all trigger rules:
        	String arrayItems = "";
          for (int j = 0; j < trigRules.size(); j++) {
            Rule trigRule = trigRules.get(j);
            if (trigRule.isVisibleInExplanatoryTool()) {
//              writer.write("\"" + trigRule.getName() + "\",");
            	initalizeRuleListCodeBlock += "\"" + trigRule.getName() + "\",";
//              writer.write(NEWLINE);
            	initalizeRuleListCodeBlock += "\n";
            }
          }
//          writer.write("};");
//          writer.write(NEWLINE);
//          writer.write("triggerRuleList.setListData(trigList);");
//          writer.write(NEWLINE);
          initalizeRuleListCodeBlock += "};";
          initalizeRuleListCodeBlock += "\n";
          initalizeRuleListCodeBlock += "triggerRuleList.getItems().setAll(trigList);";
          initalizeRuleListCodeBlock += "\n";
        }
        Vector<Rule> destRules = actionItem.getAllDestroyerRules();
        if (destRules.size() > 0) {
        	initalizeRuleListCodeBlock += "String[] destList = {";
//          writer.write("String[] destList = {");
//          writer.write(NEWLINE);
        	initalizeRuleListCodeBlock += "\n";

          // go through all destroyer rules:
          for (int j = 0; j < destRules.size(); j++) {
            Rule destRule = destRules.get(j);
            if (destRule.isVisibleInExplanatoryTool()) {
//              writer.write("\"" + destRule.getName() + "\",");
              initalizeRuleListCodeBlock += "\"" + destRule.getName() + "\",";
//              writer.write(NEWLINE);
            }
          }
          initalizeRuleListCodeBlock += "};";
          initalizeRuleListCodeBlock += "\n";
          initalizeRuleListCodeBlock += "destroyerRuleList.getItems().setAll(destList);";
          initalizeRuleListCodeBlock += "\n";
//          writer.write("};");
//          writer.write(NEWLINE);
//          writer.write("destroyerRuleList.setListData(destList);");
//          writer.write(NEWLINE);
        }
        Vector<Rule> contRules = actionItem.getAllContinuousRules();
        if (contRules.size() > 0) {
        	initalizeRuleListCodeBlock += "String[] intList = {";
        	initalizeRuleListCodeBlock += "\n";
//          writer.write("String[] intList = {");
//          writer.write(NEWLINE);

          // go through all continuous rules:
          for (int j = 0; j < contRules.size(); j++) {
            Rule contRule = contRules.get(j);
            if (contRule.isVisibleInExplanatoryTool()) {
//              writer.write("\"" + contRule.getName() + "\",");
              initalizeRuleListCodeBlock += "\"" + contRule.getName() + "\",";
              initalizeRuleListCodeBlock += "\n";
//              writer.write(NEWLINE);
            }
          }
          initalizeRuleListCodeBlock += "};";
          initalizeRuleListCodeBlock += "\n";
          initalizeRuleListCodeBlock += "intermediateRuleList.getItems().setAll(intList);";
          initalizeRuleListCodeBlock += "\n";
//          writer.write("};");
//          writer.write(NEWLINE);
//          writer.write("intermediateRuleList.setListData(intList);");
//          writer.write(NEWLINE);
        }
        writer.write(CLOSED_BRACK);
        writer.write(NEWLINE);
      }
    }
  	
  	MethodSpec initalizeRuleList = MethodSpec.methodBuilder("initalizeRuleLists")
  			.addCode(initalizeRuleListCodeBlock)
  			.build();
  			
      
      
      // member variables:
//      writer
//          .write("private simse.adts.actions.Action action; // action in focus");
//      writer.write(NEWLINE);
//      writer.write(NEWLINE);
      writer.write("private JList triggerRuleList;");
      writer.write(NEWLINE);
      writer.write("private JList destroyerRuleList;");
      writer.write(NEWLINE);
      writer.write("private JList intermediateRuleList;");
      writer.write(NEWLINE);
      writer
          .write("private JTextArea descriptionArea; // for displaying a rule description");
      writer.write(NEWLINE);
      writer.write(NEWLINE);

      // constructor:
      writer
          .write("public RuleInfoPanel(JFrame owner, simse.adts.actions.Action action) {");
      writer.write(NEWLINE);
      writer.write("this.action = action;");
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      writer.write("// Create main panel:");
      writer.write(NEWLINE);
      writer.write("JPanel mainPane = new JPanel();");
      writer.write(NEWLINE);
      writer.write("mainPane.setPreferredSize(new Dimension(900, 550));");
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      writer.write("// Create rule pane and components:");
      writer.write(NEWLINE);
      writer.write("Box rulePane = Box.createVerticalBox();");
      writer.write(NEWLINE);
      writer.write("JPanel trigRuleTitlePane = new JPanel();");
      writer.write(NEWLINE);
      writer.write("trigRuleTitlePane.add(new JLabel(\"Trigger Rules:\"));");
      writer.write(NEWLINE);
      writer.write("rulePane.add(trigRuleTitlePane);");
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      writer.write("// rule lists:");
      writer.write(NEWLINE);
      writer.write("triggerRuleList = new JList();");
      writer.write(NEWLINE);
      writer.write("triggerRuleList.setVisibleRowCount(7);");
      writer.write(NEWLINE);
      writer.write("triggerRuleList.setFixedCellWidth(400);");
      writer.write(NEWLINE);
      writer.write("triggerRuleList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);");
      writer.write(NEWLINE);
      writer.write("triggerRuleList.addListSelectionListener(this);");
      writer.write(NEWLINE);
      writer.write("JScrollPane triggerRuleListPane = new JScrollPane(triggerRuleList);");
      writer.write(NEWLINE);
      writer.write("String trigToolTip = \"Rules that execute at the beginning of the action\";");
      writer.write(NEWLINE);
      writer.write("trigRuleTitlePane.setToolTipText(trigToolTip);");
      writer.write(NEWLINE);
      writer.write("triggerRuleList.setToolTipText(trigToolTip);");
      writer.write(NEWLINE);
      writer.write("rulePane.add(triggerRuleListPane);");
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      writer.write("JPanel destRuleTitlePane = new JPanel();");
      writer.write(NEWLINE);
      writer.write("destRuleTitlePane.add(new JLabel(\"Destroyer Rules:\"));");
      writer.write(NEWLINE);
      writer.write("rulePane.add(destRuleTitlePane);");
      writer.write(NEWLINE);
      writer.write("destroyerRuleList = new JList();");
      writer.write(NEWLINE);
      writer.write("destroyerRuleList.setVisibleRowCount(7);");
      writer.write(NEWLINE);
      writer.write("destroyerRuleList.setFixedCellWidth(400);");
      writer.write(NEWLINE);
      writer.write("destroyerRuleList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);");
      writer.write(NEWLINE);
      writer.write("destroyerRuleList.addListSelectionListener(this);");
      writer.write(NEWLINE);
      writer.write("JScrollPane destroyerRuleListPane = new JScrollPane(destroyerRuleList);");
      writer.write(NEWLINE);
      writer.write("String destToolTip = \"Rules that execute at the end of the action\";");
      writer.write(NEWLINE);
      writer.write("destRuleTitlePane.setToolTipText(destToolTip);");
      writer.write(NEWLINE);
      writer.write("destroyerRuleList.setToolTipText(destToolTip);");
      writer.write(NEWLINE);
      writer.write("rulePane.add(destroyerRuleListPane);");
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      writer.write("JPanel intRuleTitlePane = new JPanel();");
      writer.write(NEWLINE);
      writer.write("intRuleTitlePane.add(new JLabel(\"Intermediate Rules:\"));");
      writer.write(NEWLINE);
      writer.write("rulePane.add(intRuleTitlePane);");
      writer.write(NEWLINE);
      writer.write("intermediateRuleList = new JList();");
      writer.write(NEWLINE);
      writer.write("intermediateRuleList.setVisibleRowCount(7);");
      writer.write(NEWLINE);
      writer.write("intermediateRuleList.setFixedCellWidth(400);");
      writer.write(NEWLINE);
      writer.write("intermediateRuleList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);");
      writer.write(NEWLINE);
      writer.write("intermediateRuleList.addListSelectionListener(this);");
      writer.write(NEWLINE);
      writer.write("JScrollPane intermediateRuleListPane = new JScrollPane(intermediateRuleList);");
      writer.write(NEWLINE);
      writer.write("String intToolTip = \"Rules that execute every clock tick during the life of the action\";");
      writer.write(NEWLINE);
      writer.write("intRuleTitlePane.setToolTipText(intToolTip);");
      writer.write(NEWLINE);
      writer.write("intermediateRuleList.setToolTipText(intToolTip);");
      writer.write(NEWLINE);
      writer.write("rulePane.add(intermediateRuleListPane);");
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      writer.write("initializeRuleLists();");
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      writer.write("// description pane:");
      writer.write(NEWLINE);
      writer.write("Box descriptionPane = Box.createVerticalBox();");
      writer.write(NEWLINE);
      writer.write("JPanel descriptionTitlePane = new JPanel();");
      writer.write(NEWLINE);
      writer.write("descriptionTitlePane.add(new JLabel(\"Description:\"));");
      writer.write(NEWLINE);
      writer.write("descriptionPane.add(descriptionTitlePane);");
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      writer.write("// description text area:");
      writer.write(NEWLINE);
      writer.write("descriptionArea = new JTextArea(29, 30);");
      writer.write(NEWLINE);
      writer.write("descriptionArea.setLineWrap(true);");
      writer.write(NEWLINE);
      writer.write("descriptionArea.setWrapStyleWord(true);");
      writer.write(NEWLINE);
      writer.write("descriptionArea.setEditable(false);");
      writer.write(NEWLINE);
      writer
          .write("JScrollPane descriptionScrollPane = new JScrollPane(descriptionArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);");
      writer.write(NEWLINE);
      writer.write("descriptionPane.add(descriptionScrollPane);");
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      writer.write("rulePane.add(descriptionPane);");
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      writer.write("// Add panes to main pane:");
      writer.write(NEWLINE);
      writer.write("mainPane.add(rulePane);");
      writer.write(NEWLINE);
      writer.write("mainPane.add(descriptionPane);");
      writer.write(NEWLINE);
      writer.write("add(mainPane);");
      writer.write(NEWLINE);
      writer.write(NEWLINE);
      writer.write("setOpaque(true);");
      writer.write(NEWLINE);
      writer.write("validate();");
      writer.write(NEWLINE);
      writer.write("repaint();");
      writer.write(NEWLINE);
      writer.write(CLOSED_BRACK);
      writer.write(NEWLINE);
      writer.write(NEWLINE);

      // "valueChanged" method:
      
      MethodSpec handle = MethodSpec.methodBuilder("handle")
    		  .addParameter(mouseEvent, "event")
    		  .beginControlFlow("if ((event.getSource() == triggerRuleList && !triggerRuleList.getSelectionModel().isEmpty()))")
    		  .addStatement("destroyerRuleList.getSelectionModel().clearSelection()")
    		  .addStatement("intermediateRuleList.getSelectionModel().clearSelection()")
    		  .addStatement("refreshDescriptionArea()")
    		  .nextControlFlow("else if (!destroyerRuleList.getSelectionModel().isEmpty())")
    		  .addStatement("name = ($T) destroyerRuleList.getSelectionModel().getSelectedItem()", string)
    		  .nextControlFlow("else if (!intermediateRuleList.getSelectionModel().isEmpty())")
    		  .addStatement("name = ($T) intermediateRuleList.getSelectionModel().getSelectedItem()", string)
    		  .endControlFlow()
    		  .beginControlFlow("if (name != null)")
    		  .addStatement("$T text = RuleCategories.getRuleMapping(name)", string)
    		  .addStatement("descriptionArea.setText(text)")
    		  .addStatement("descriptionArea.positionCaret(0)")
    		  .endControlFlow()
    		  .build();
    		  
      writer.write("public void valueChanged(ListSelectionEvent e) {");
      writer.write(NEWLINE);
      writer.write("if ((e.getSource() == triggerRuleList && !triggerRuleList.isSelectionEmpty())) {");
      writer.write(NEWLINE);
      writer.write("destroyerRuleList.clearSelection();");
      writer.write(NEWLINE);
      writer.write("intermediateRuleList.clearSelection();");
      writer.write(NEWLINE);
      writer.write("refreshDescriptionArea();");
      writer.write(NEWLINE);
      writer.write(CLOSED_BRACK);
      writer.write(NEWLINE);
      writer.write("else if (e.getSource() == destroyerRuleList && !destroyerRuleList.isSelectionEmpty()) {");
      writer.write(NEWLINE);
      writer.write("triggerRuleList.clearSelection();");
      writer.write(NEWLINE);
      writer.write("intermediateRuleList.clearSelection();");
      writer.write(NEWLINE);
      writer.write("refreshDescriptionArea();");
      writer.write(NEWLINE);
      writer.write(CLOSED_BRACK);
      writer.write(NEWLINE);
      writer.write("else if (e.getSource() == intermediateRuleList && !intermediateRuleList.isSelectionEmpty()) {");
      writer.write(NEWLINE);
      writer.write("triggerRuleList.clearSelection();");
      writer.write(NEWLINE);
      writer.write("destroyerRuleList.clearSelection();");
      writer.write(NEWLINE);
      writer.write("refreshDescriptionArea();");
      writer.write(NEWLINE);
      writer.write(CLOSED_BRACK);
      writer.write(NEWLINE);
      writer.write(CLOSED_BRACK);
      writer.write(NEWLINE);
      writer.write(NEWLINE);

      // "initializeRuleLists" method:
      writer.write("private void initializeRuleLists() {");
      writer.write(NEWLINE);


      writer.write(CLOSED_BRACK);
      writer.write(NEWLINE);
      writer.write(NEWLINE);

      // "refreshDescriptionArea" method:
      writer
          .write("// refreshes the description area with the selected rule description");
      writer.write(NEWLINE);
      writer.write("private void refreshDescriptionArea() {");
      writer.write(NEWLINE);
      writer.write("String name = null;");
      writer.write(NEWLINE);
      writer.write("if (!triggerRuleList.isSelectionEmpty()) {");
      writer.write(NEWLINE);
      writer.write("name = (String) triggerRuleList.getSelectedValue();");
      writer.write(NEWLINE);
      writer.write(CLOSED_BRACK);
      writer.write(NEWLINE);
      writer.write("else if (!destroyerRuleList.isSelectionEmpty()) {");
      writer.write(NEWLINE);
      writer.write("name = (String) destroyerRuleList.getSelectedValue();");
      writer.write(NEWLINE);
      writer.write(CLOSED_BRACK);
      writer.write(NEWLINE);
      writer.write("else if (!intermediateRuleList.isSelectionEmpty()) {");
      writer.write(NEWLINE);
      writer.write("name = (String) intermediateRuleList.getSelectedValue();");
      writer.write(NEWLINE);
      writer.write(CLOSED_BRACK);
      writer.write(NEWLINE);
      writer.write("if (name != null) {");
      writer.write(NEWLINE);
      writer.write("String text = \"\";");
      writer.write(NEWLINE);
      


      String refreshDescriptionAreaActionBlock = "";
      
      // go through all actions:
      writeElse = false;
      for (ActionType action : actions) {
        if (action.isVisibleInExplanatoryTool()) {
          if (writeElse) {
            writer.write("else ");
            refreshDescriptionAreaActionBlock += "else ";
          } else {
            writeElse = true;
          }
//          writer.write("if (action instanceof "
//              + CodeGeneratorUtils.getUpperCaseLeading(action.getName()) + 
//              "Action) {");
          refreshDescriptionAreaActionBlock += "if (action instanceof "
                  + CodeGeneratorUtils.getUpperCaseLeading(action.getName()) + 
                  "Action) {";
//          writer.write(NEWLINE);
          refreshDescriptionAreaActionBlock += "\n";

          // go through all rules:
          Vector<Rule> rules = action.getAllRules();
          boolean writeElse2 = false;
          for (Rule rule : rules) {
            if (rule.isVisibleInExplanatoryTool()) {
              if (writeElse2) {
//                writer.write("else ");
                refreshDescriptionAreaActionBlock += "else ";
              } else {
                writeElse2 = true;
              }
//              writer.write("if (name.equals(\"" + rule.getName() + "\")) {");
              refreshDescriptionAreaActionBlock += "if (name.equals(\"" + rule.getName() + "\")) {";
//              writer.write(NEWLINE);
              refreshDescriptionAreaActionBlock += "\n";
//              writer.write("text = RuleDescriptions."
//                  + action.getName().toUpperCase() + "_"
//                  + rule.getName().toUpperCase() + ";");
              refreshDescriptionAreaActionBlock += "text = RuleDescriptions."
                      + action.getName().toUpperCase() + "_"
                      + rule.getName().toUpperCase() + ";";
//              writer.write(NEWLINE);
              refreshDescriptionAreaActionBlock += "\n";
//              writer.write(CLOSED_BRACK);
            }
          }
//          writer.write(CLOSED_BRACK);
          refreshDescriptionAreaActionBlock += "\n";
//          writer.write(NEWLINE);
        }
      }
      MethodSpec refreshDescriptionArea = MethodSpec.methodBuilder("refreshDescriptionArea")
    		  .addStatement("$T name = null", string)
    		  .beginControlFlow("if (!triggerRuleList.getSelectionModel().isEmpty())")
    		  .addStatement("name = ($T) triggerRuleList.getSelectionModel().getSelectedItem()", string)
    		  .nextControlFlow("else if (!destroyerRuleList.getSelectionModel().isEmpty())")
    		  .addStatement("name = ($T) destroyerRuleList.getSelectionModel().getSelectedItem()", string)
    		  .nextControlFlow("else if (!intermediateRuleList.getSelectionModel().isEmpty())")
    		  .addStatement("name = ($T) intermediateRuleList.getSelectionModel().getSelectedItem()", string)
    		  .endControlFlow()
    		  .beginControlFlow("if (name != null)")
    		  .addStatement("$T text = RuleCategories.getRuleMapping(name)", string, ruleCategories)
    		  .addStatement("descriptionArea.setText(text)")
    		  .addStatement("descriptionArea.positionCaret(0)")
    		  .endControlFlow()
    		  .addCode(refreshDescriptionAreaActionBlock)
    		  .addStatement("descriptionArea.setText(text)")
    		  .addStatement("descriptionArea.setCaretPosition(0)")
    		  .build();
      
	  TypeSpec actionInfoWindow = TypeSpec.classBuilder("ActionInfoWindow")
			  .superclass(stage)
			  .addField(actionClass, "action", Modifier.PRIVATE)
			  .addField(ParameterizedTypeName.get(listView, string), "triggerRuleList", Modifier.PRIVATE)
			  .addField(ParameterizedTypeName.get(listView, string), "destroyerRuleList", Modifier.PRIVATE)
			  .addField(ParameterizedTypeName.get(listView, string), "intermediateRuleList", Modifier.PRIVATE)
			  .addField(textArea, "descriptionArea", Modifier.PRIVATE)
			  .addMethod(constructor)
			  .addMethod(initalizeRuleList)
			  .addMethod(refreshDescriptionArea)
			  .addMethod(handle)
			  .build();
      
//      writer.write("descriptionArea.setText(text);");
//      writer.write(NEWLINE);
//      writer.write("descriptionArea.setCaretPosition(0);");
//      writer.write(NEWLINE);
//      writer.write(CLOSED_BRACK);
//      writer.write(NEWLINE);
//      writer.write(CLOSED_BRACK);
//      writer.write(NEWLINE);
//
//      writer.write(CLOSED_BRACK);
      writer.close();
    } catch (IOException e) {
      JOptionPane.showMessageDialog(null, ("Error writing file "
          + ruleInfoFile.getPath() + ": " + e.toString()), "File IO Error",
          JOptionPane.WARNING_MESSAGE);
    }
  }
}