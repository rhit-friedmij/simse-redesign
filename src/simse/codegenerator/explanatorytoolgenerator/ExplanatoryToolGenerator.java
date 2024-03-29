/*
 * This class is responsible for generating all of the code for the
 * ExplanatoryTool component of the simulation
 */

package simse.codegenerator.explanatorytoolgenerator;

import simse.modelbuilder.ModelOptions;
import simse.modelbuilder.actionbuilder.ActionType;
import simse.modelbuilder.actionbuilder.DefinedActionTypes;
import simse.codegenerator.CodeGeneratorConstants;
import simse.codegenerator.CodeGeneratorUtils;
import simse.codegenerator.explanatorytoolgenerator.ActionGraphGenerator;
import simse.codegenerator.explanatorytoolgenerator.ActionInfoPanelGenerator;
import simse.codegenerator.explanatorytoolgenerator.ObjectGraphGenerator;
import simse.codegenerator.utilgenerator.DestroyerDescriptionsGenerator;
import simse.codegenerator.utilgenerator.RuleDescriptionsGenerator;
import simse.codegenerator.utilgenerator.TriggerDescriptionsGenerator;
import simse.modelbuilder.objectbuilder.Attribute;
import simse.modelbuilder.objectbuilder.DefinedObjectTypes;
import simse.modelbuilder.objectbuilder.NumericalAttribute;
import simse.modelbuilder.objectbuilder.SimSEObjectType;
import simse.modelbuilder.objectbuilder.SimSEObjectTypeTypes;
import simse.modelbuilder.rulebuilder.Rule;
import simse.modelbuilder.startstatebuilder.CreatedObjects;
import simse.modelbuilder.startstatebuilder.SimSEObject;

import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

import javax.lang.model.element.Modifier;

import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

public class ExplanatoryToolGenerator implements CodeGeneratorConstants {
  private ModelOptions options;
  private DefinedObjectTypes objTypes;
  private CreatedObjects objs;
  private DefinedActionTypes acts;
  private ObjectGraphGenerator objGraphGen; // generates the ObjectGraph class
	private ActionGraphGenerator actGraphGen; // generates the ActionGraph class
	private CompositeGraphGenerator compGraphGen; // generates the CompositeGraph
																								// class
	private ActionInfoPanelGenerator actInfoPanelGen; // generates the
																										// ActionInfoPanel class
	private RuleInfoPanelGenerator ruleInfoPanelGen; // generates the
																										// RuleInfoPanel class
	private ActionInfoWindowGenerator actInfoWindowGen; // generates the
																											// ActionInfoWindow class
	private TriggerDescriptionsGenerator trigDescGen; // generates the
																										// TriggerDescriptions class
	private DestroyerDescriptionsGenerator destDescGen; // generates the
																											// DestroyerDescriptions
																											// class
	private RuleDescriptionsGenerator ruleDescGen; // generates the
																									// RuleDescriptions class
	private BranchGenerator branchGen; // generates the Branch class
	private MultipleTimelinesBrowserGenerator browserGen; // generates the
																												 // MulitpleTimelinesBrowser
																												 // class
	private ParticipantGenerator partcipantGen;

  public ExplanatoryToolGenerator(ModelOptions options, 
      DefinedObjectTypes objTypes, CreatedObjects objs, 
      DefinedActionTypes acts) {
    this.options = options;
    this.objTypes = objTypes;
    this.objs = objs;
    this.acts = acts;
    objGraphGen = new ObjectGraphGenerator(objTypes, objs, 
        options.getCodeGenerationDestinationDirectory(), options);
    actGraphGen = new ActionGraphGenerator(acts, 
        options.getCodeGenerationDestinationDirectory(), options);
    compGraphGen = new CompositeGraphGenerator(
        options.getCodeGenerationDestinationDirectory(), options);
    actInfoPanelGen = new ActionInfoPanelGenerator(acts, 
        options.getCodeGenerationDestinationDirectory());
    ruleInfoPanelGen = new RuleInfoPanelGenerator(acts, 
        options.getCodeGenerationDestinationDirectory());
    actInfoWindowGen = new ActionInfoWindowGenerator(
        options.getCodeGenerationDestinationDirectory());
    trigDescGen = new TriggerDescriptionsGenerator(acts, 
        options.getCodeGenerationDestinationDirectory());
    destDescGen = new DestroyerDescriptionsGenerator(acts, 
        options.getCodeGenerationDestinationDirectory());
    ruleDescGen = new RuleDescriptionsGenerator(acts, 
        options.getCodeGenerationDestinationDirectory());
    branchGen = new BranchGenerator(
    		options.getCodeGenerationDestinationDirectory());
    browserGen = new MultipleTimelinesBrowserGenerator(
    		options.getCodeGenerationDestinationDirectory());
    partcipantGen = new ParticipantGenerator(options.getCodeGenerationDestinationDirectory()); 
  }

/*
 * causes all of this component's sub-components to generate code
 */
  public void generate() { 
    // copy the JFreeChart jars:
    copyJFreeChartJars();

    objGraphGen.generate();
    actGraphGen.generate();
    compGraphGen.generate();
    actInfoPanelGen.generate();
    ruleInfoPanelGen.generate();
    actInfoWindowGen.generate();
    trigDescGen.generate();
    destDescGen.generate();
    ruleDescGen.generate();
    branchGen.generate();
    browserGen.generate();
    partcipantGen.generate();
    generateExplanatoryTool();
  }

  // generates the ExplanatoryTool class
  private void generateExplanatoryTool() { 
    File expToolFile = new File(options.
        getCodeGenerationDestinationDirectory(),
        ("simse\\explanatorytool\\ExplanatoryTool.java"));
    if (expToolFile.exists()) {
      expToolFile.delete(); // delete old version of file
    }
      
      ClassName arrayList = ClassName.get("java.util", "ArrayList");
      ClassName state = ClassName.get("simse.state", "State");
      ClassName stage = ClassName.get("javafx.stage", "Stage");
      ClassName multipleTimelinesBrowser = ClassName.get("simse.explanatorytool", "MultipleTimelinesBrowser");
      ClassName button = ClassName.get("javafx.scene.control", "Button");
      ClassName comboBox = ClassName.get("javafx.scene.control", "ComboBox");
      ClassName listView = ClassName.get("javafx.scene.control", "ListView");
      ClassName textArea = ClassName.get("javafx.scene.control", "TextArea");
      ClassName gridPane = ClassName.get("javafx.scene.layout", "GridPane");
      ClassName branch = ClassName.get("simse.explanatorytool", "Branch");
      ClassName string = ClassName.get("java.lang", "String");
      ClassName mouseEvent = ClassName.get("javafx.scene.input", "MouseEvent");
      ClassName pane = ClassName.get("javafx.scene.layout", "Pane");
      ClassName vBox = ClassName.get("javafx.scene.layout", "VBox");
      ClassName borderPane = ClassName.get("javafx.scene.layout", "BorderPane");
      ClassName label = ClassName.get("javafx.scene.control", "Label");
      ClassName scrollPane = ClassName.get("javafx.scene.control", "ScrollPane");
      ClassName scrollBarPolicy = ClassName.get("javafx.scene.control.ScrollPane", "ScrollBarPolicy");
      ClassName tilePane = ClassName.get("javafx.scene.layout", "TilePane");
      ClassName selectionMode = ClassName.get("javafx.scene.control", "SelectionMode");
      ClassName insets = ClassName.get("javafx.geometry", "Insets");
      ClassName object = ClassName.get("java.lang", "Object");
      ArrayTypeName stringArray = ArrayTypeName.of(String.class);
      ArrayTypeName objectArray = ArrayTypeName.of(object);
      ClassName alert = ClassName.get("javafx.scene.control", "Alert");
      ClassName alertType = ClassName.get("javafx.scene.control.Alert", "AlertType");
      ClassName actionGraph = ClassName.get("simse.explanatorytool", "ActionGraph");
      ClassName objectGraph = ClassName.get("simse.explanatorytool", "ObjectGraph");
      ClassName compositeGraph = ClassName.get("simse.explanatorytool", "CompositeGraph");
      ClassName toolTip = ClassName.get("javafx.scene.control", "Tooltip");
      ClassName eventHandler = ClassName.get("javafx.event", "EventHandler");
      ClassName actionEvent = ClassName.get("javafx.event", "ActionEvent");
      ClassName chartMouseListenerFX = ClassName.get("org.jfree.chart.fx.interaction", "ChartMouseListenerFX");
      ClassName observableList = ClassName.get("javafx.collections", "ObservableList");
      ClassName fxCollections = ClassName.get("javafx.collections", "FXCollections");
      ClassName scene = ClassName.get("javafx.scene", "Scene");

     
      
      Vector<SimSEObject> objects = objs.getAllObjects();
      String internalObj = "";  
      for (int i = 0; i < objects.size(); i++) {
        SimSEObject obj = objects.get(i);
        if(i == objects.size() - 1) {
            internalObj += "\"" + CodeGeneratorUtils.getUpperCaseLeading(
            		obj.getSimSEObjectType().getName()) + " " + 
            		CodeGeneratorUtils.getUpperCaseLeading(
            				SimSEObjectTypeTypes.getText(
            						obj.getSimSEObjectType().getType())) + " " + 
            						obj.getKey().getValue().toString() + "\"\n";
        }
        else {
            internalObj += "\"" + CodeGeneratorUtils.getUpperCaseLeading(
            		obj.getSimSEObjectType().getName()) + " " + 
            		CodeGeneratorUtils.getUpperCaseLeading(
            				SimSEObjectTypeTypes.getText(
            						obj.getSimSEObjectType().getType())) + " " + 
            						obj.getKey().getValue().toString() + "\",\n";
        }

      }
      CodeBlock constructorObj = CodeBlock.builder()
    		  .addStatement("$T<$T> objects = $T.observableArrayList(" + internalObj + ")", observableList, String.class, fxCollections)
    		  .build();
      
      

      
    
            
      Vector<ActionType> actions = acts.getAllActionTypes();
      String internalActions = "";
      for (int i = 0; i < actions.size(); i++) {
        ActionType act = actions.get(i);
        if (act.isVisibleInExplanatoryTool()) {
        	if(i == actions.size() - 1) {
            	internalActions += "\"" + 
                		CodeGeneratorUtils.getUpperCaseLeading(act.getName()) + "\"\n";
        	} 
        	else {
            	internalActions += "\"" + 
                		CodeGeneratorUtils.getUpperCaseLeading(act.getName()) + "\",\n";
        	}

        }
      }
      
      CodeBlock constructorActions = CodeBlock.builder()
    		  .addStatement("$T<$T> actions = $T.observableArrayList(" + internalActions + ")", observableList, String.class, fxCollections)
    		  .build();
      
      String refreshAttributeListObjectTypes = "";
      
      Vector<SimSEObjectType> objectTypes = objTypes.getAllObjectTypes();
      for (int i = 0; i < objectTypes.size(); i++) {
        SimSEObjectType objType = objectTypes.get(i);
        if (i > 0) {
          refreshAttributeListObjectTypes += "else ";
        }
        refreshAttributeListObjectTypes += "if (selectedObject.startsWith(\""
                + CodeGeneratorUtils.getUpperCaseLeading(objType.getName()) + " "
                + SimSEObjectTypeTypes.getText(objType.getType())
                + "\")) {\n";
        refreshAttributeListObjectTypes += "String[] attributes = {\n";
        Vector<Attribute> attributes = objType.getAllAttributes();
        int numVisibleNumericalAtts = 0;
        for (int j = 0; j < attributes.size(); j++) {
          Attribute att = attributes.get(j);
          if ((att instanceof NumericalAttribute)
              && ((att.isVisible()) || (att.isVisibleOnCompletion()))) {
        	  if(j == attributes.size() - 1) {
                  refreshAttributeListObjectTypes += "\"" + att.getName() + "\"\n";
        	  } 
        	  else {
        		  refreshAttributeListObjectTypes += "\"" + att.getName() + "\",\n";
        	  }
            
            numVisibleNumericalAtts++;
          }
        }
        if (numVisibleNumericalAtts == 0) {
          refreshAttributeListObjectTypes += "\"(No numerical attributes)\"\n";
        }
        refreshAttributeListObjectTypes += "};\n";
        refreshAttributeListObjectTypes += "attributeList.getItems().setAll(attributes);\n";
        if (numVisibleNumericalAtts == 0) {
          refreshAttributeListObjectTypes += "attributeList.setEditable(true);\n";
        } else {
          refreshAttributeListObjectTypes += "attributeList.scrollTo(0);\n";
          refreshAttributeListObjectTypes += "attributeList.getSelectionModel().select(0);\n";
          refreshAttributeListObjectTypes += "attributeList.getFocusModel().focus(0);\n";
        }
        refreshAttributeListObjectTypes += "}\n";
      }
//      refreshAttributeListObjectTypes += "}\n\n";
      
      MethodSpec refreshAttributeList = MethodSpec.methodBuilder("refreshAttributeList")
    		  .addModifiers(Modifier.PRIVATE)
    		  .addStatement("attributeList.getItems().removeAll()")
    		  .addStatement("$T selectedObject = ($T) objectList.getSelectionModel().getSelectedItem()", String.class, String.class)
    		  .beginControlFlow("if (selectedObject == null)")
    		  .addStatement("return")
    		  .endControlFlow()
    		  .addCode(refreshAttributeListObjectTypes)
    		  .build();
      
      MethodSpec refreshButtons = MethodSpec.methodBuilder("refreshButtons")
    		  .addModifiers(Modifier.PRIVATE)
    		  .beginControlFlow("if (attributeList.getSelectionModel().isEmpty())")
    		  .addStatement("generateObjGraphButton.setDisable(true)")
    		  .addStatement("generateCompGraphButton.setDisable(true)")
    		  .endControlFlow()
    		  .beginControlFlow("else ")
    		  .addStatement("generateObjGraphButton.setDisable(false)")
    		  .beginControlFlow("if (!actionList.getSelectionModel().isEmpty())")
    		  .addStatement("generateCompGraphButton.setDisable(false)")
    		  .endControlFlow()
    		  .endControlFlow()
    		  .beginControlFlow("if (actionList.getSelectionModel().isEmpty())")
    		  .addStatement("generateActGraphButton.setDisable(true)")
    		  .addStatement("generateCompGraphButton.setDisable(true)")
    		  .endControlFlow()
    		  .beginControlFlow("else ")
    		  .addStatement("generateActGraphButton.setDisable(false)")
    		  .endControlFlow()
    		  .build();
      
      MethodSpec constructor = MethodSpec.constructorBuilder()
    		  .addModifiers(Modifier.PUBLIC)
    		  .addParameter(ParameterizedTypeName.get(arrayList, state), "log")
    		  .addParameter(branch, "branch")
    		  .addParameter(multipleTimelinesBrowser, "browser")
    		  .addStatement("this.branch = branch")
    		  .addStatement("timelinesBrowser = browser")
    		  .addStatement("this.log = log")
    		  .addStatement("$T title = \"Explanatory Tool\"", String.class)
    		  .beginControlFlow("if (branch.getName() != null)")
    		  .addStatement("title = title.concat(\" - \" + branch.getName())")
    		  .endControlFlow()
    		  .addStatement("this.setTitle(title)")
    		  .addStatement("this.visibleGraphs = new $T<$T>()", arrayList, stage)
    		  .addStatement("$T multipleTimelinesPanel = new $T()", pane, pane)
    		  .addStatement("multipleTimelinesButton = new $T(\"Multiple Timelines Browser\")", button)
    		  .addStatement("multipleTimelinesButton.addEventHandler($T.MOUSE_CLICKED, this)", mouseEvent)
    		  .addStatement("multipleTimelinesPanel.getChildren().add(multipleTimelinesButton)")
    		  .addStatement("$T generateGraphsTitlePane = new $T()", pane, pane)
    		  .addStatement("generateGraphsTitlePane.getChildren().add(new $T(\"Generate Graph(s):\"))", label)
    		  .addStatement("$T objectPane = new $T()", vBox, vBox)
    		  .addStatement("$T objectTitlePane = new $T()", borderPane, borderPane)
    		  .addStatement("objectTitlePane.setCenter(new $T(\"Object Graph:\"))", label)
    		  .addStatement("objectPane.getChildren().add(objectTitlePane)")
    		  .addCode(constructorObj)
    		  .addStatement("objectList = new $T<$T>(objects)", comboBox, String.class)
    		  .addStatement("		objectList.setOnAction(new $T<$T>() {\r\n" + 
    		  		"			@Override\r\n" + 
    		  		"			public void handle($T arg0) {\r\n" + 
    		  		"				$N();\r\n" + 
    		  		"			}\r\n" + 
    		  		"		})", eventHandler, actionEvent, actionEvent, refreshAttributeList)
    		  .addStatement("objectPane.getChildren().add(objectList)")
    		  .addStatement("// Create attribute list pane:")
    		  .addStatement("$T attributeListTitlePane = new $T()", pane, pane)
    		  .addStatement("attributeListTitlePane.getChildren().add(new $T(\"Show Attributes:\"))", label)
    		  .addStatement("attributeListTitlePane.setMinHeight(20)")
    		  .addStatement("objectPane.getChildren().add(attributeListTitlePane)")
    		  .addStatement("attributeList = new $T<$T>()", listView, String.class)
    		  .addStatement("attributeList.setFixedCellSize(24)")
    		  .addStatement("attributeList.getSelectionModel().setSelectionMode($T.MULTIPLE)", selectionMode)
    		  .addStatement("attributeList.addEventHandler($T.MOUSE_CLICKED, this)", mouseEvent)
    		  .addStatement("attributeList.setMinWidth(550)")
    		  .addStatement("$T attributeListPane = new $T(attributeList)", scrollPane, scrollPane)
    		  .addStatement("attributeListPane.setMinHeight(120)")
    		  .addStatement("objectPane.getChildren().add(attributeListPane)")
    		  .addStatement("// Create objectBottom pane & button:")
    		  .addStatement("$T objBottomPane = new $T()", pane, pane)
    		  .addStatement("generateObjGraphButton = new $T(\"Generate Object Graph\")", button)
    		  .addStatement("generateObjGraphButton.addEventHandler($T.MOUSE_CLICKED, this)", mouseEvent)
    		  .addStatement("objBottomPane.getChildren().add(generateObjGraphButton)")
    		  .addStatement("objectPane.getChildren().add(objBottomPane)")
    		  .addStatement("// Create action pane and components:")
    		  .addStatement("$T actionPane = new $T()", vBox, vBox)
    		  .addStatement("$T actionTitlePane = new $T()", pane, pane)
    		  .addStatement("actionTitlePane.getChildren().add(new $T(\"Action Graph:\"))", label)
    		  .addStatement("actionTitlePane.setMinHeight(20)")
    		  .addStatement("actionPane.getChildren().add(actionTitlePane)")
    		  .addCode(constructorActions)
    		  .addStatement("actionList = new $T<$T>(actions)", listView, String.class)
    		  .addStatement("attributeList.setFixedCellSize(24)")
    		  .addStatement("actionList.getSelectionModel().setSelectionMode($T.MULTIPLE)", selectionMode)
    		  .addStatement("actionList.addEventHandler($T.MOUSE_CLICKED, this)", mouseEvent)
    		  .addStatement("actionPane.getChildren().add(actionList)")
    		  .addStatement("// Create actionBottom pane & buttons")
    		  .addStatement("$T actBottomPane = new $T()", pane, pane)
    		  .addStatement("generateActGraphButton = new $T(\"Generate Action Graph\")", button)
    		  .addStatement("generateActGraphButton.addEventHandler($T.MOUSE_CLICKED, this)", mouseEvent)
    		  .addStatement("actBottomPane.getChildren().add(generateActGraphButton)")
    		  .addStatement("actionPane.getChildren().add(actBottomPane)")
    		  .addStatement("// Create comp graph pane & button")
    		  .addStatement("$T generateCompGraphPane = new $T()", pane, pane)
    		  .addStatement("generateCompGraphButton = new $T(\"Generate Composite Graph\")", button)
    		  .addStatement("generateCompGraphButton.addEventHandler($T.MOUSE_CLICKED, this)", mouseEvent)
    		  .addStatement("generateCompGraphPane.getChildren().add(generateCompGraphButton)")
    		  .addStatement("$N()", refreshAttributeList)
    		  .beginControlFlow("if (actions.size() > 0)")
    		  .addStatement("actionList.scrollTo(0)")
    		  .addStatement("actionList.getSelectionModel().select(0)")
    		  .addStatement("actionList.getFocusModel().focus(0)")
    		  .endControlFlow()
    		  .addStatement("$N()", refreshButtons)
    		  .addStatement("// Create viewRuleTitlePane and label")
    		  .addStatement("$T viewRulesTitlePane = new $T()", pane, pane)
    		  .addStatement("viewRulesTitlePane.getChildren().add(new $T(\"View Rules:\"))", label)
    		  .addStatement("// Create actionsComboBoxPane")
    		  .addStatement("$T actionComboBoxPane = new $T()", pane, pane)
    		  .addStatement("actionComboBoxPane.getChildren().add(new $T(\"Actions:\"))", label)
    		  .addStatement("actionComboBox = new $T<$T>(actions)", comboBox, String.class)
    		  .addStatement("		actionComboBox.setOnAction(new EventHandler<ActionEvent>() {\r\n" + 
    		  		"			@Override\r\n" + 
    		  		"			public void handle(ActionEvent arg0) {\r\n" + 
    		  		"				if (actionComboBox.getItems().size() > 0) {\r\n" + 
    		  		"					refreshRuleLists((String) actionComboBox.getSelectionModel().getSelectedItem());\r\n" + 
    		  		"					descriptionArea.setText(\"\");\r\n" + 
    		  		"				}\r\n" + 
    		  		"			}\r\n" + 
    		  		"		})")
    		  .addStatement("actionComboBoxPane.getChildren().add(actionComboBox)")
    		  .addStatement("// Create rulesMainPane")
    		  .addStatement("$T rulesMainPane = new $T()", tilePane, tilePane)
    		  .addStatement("// Create ruleListsPane")
    		  .addStatement("$T ruleListsPane = new $T()", vBox, vBox)
    		  .addStatement("// rule lists")
    		  .addStatement("$T trigRuleTitlePane = new $T()", pane, pane)
    		  .addStatement("trigRuleTitlePane.getChildren().add(new $T(\"Trigger Rules:\"))", label)
    		  .addStatement("ruleListsPane.getChildren().add(trigRuleTitlePane)")
    		  .addStatement("triggerRuleList = new $T<$T>()", listView, String.class)
    		  .addStatement("triggerRuleList.setFixedCellSize(24)")
    		  .addStatement("triggerRuleList.getSelectionModel().setSelectionMode($T.SINGLE)", selectionMode)
    		  .addStatement("triggerRuleList.addEventHandler($T.MOUSE_CLICKED, this)", mouseEvent)
    		  .addStatement("triggerRuleList.setMinWidth(272)")
    		  .addStatement("$T triggerRuleListPane = new $T(triggerRuleList)", scrollPane, scrollPane)
    		  .addStatement("triggerRuleListPane.setMaxHeight(80)")
    		  .addStatement("ruleListsPane.getChildren().add(triggerRuleListPane)")
    		  .addStatement("$T destRuleTitlePane = new $T()", pane, pane)
    		  .addStatement("destRuleTitlePane.getChildren().add(new $T(\"Destroyer Rules:\"))", label)
    		  .addStatement("ruleListsPane.getChildren().add(destRuleTitlePane)")
    		  .addStatement("destroyerRuleList = new $T<$T>()", listView, String.class)
    		  .addStatement("destroyerRuleList.setFixedCellSize(24)")
    		  .addStatement("destroyerRuleList.getSelectionModel().setSelectionMode($T.SINGLE)", selectionMode)
    		  .addStatement("destroyerRuleList.addEventHandler($T.MOUSE_CLICKED, this)", mouseEvent)
    		  .addStatement("destroyerRuleList.setMinWidth(272)")
    		  .addStatement("$T destroyerRuleListPane = new $T(destroyerRuleList)", scrollPane, scrollPane)
    		  .addStatement("destroyerRuleListPane.setMaxHeight(80)")
    		  .addStatement("ruleListsPane.getChildren().add(destroyerRuleListPane)")
    		  .addStatement("$T intRuleTitlePane = new $T()", pane, pane)
    		  .addStatement("intRuleTitlePane.getChildren().add(new $T(\"Intermediate Rules:\"))", label)
    		  .addStatement("ruleListsPane.getChildren().add(intRuleTitlePane)")
    		  .addStatement("intermediateRuleList = new $T<$T>()", listView, String.class)
    		  .addStatement("intermediateRuleList.setFixedCellSize(24)")
    		  .addStatement("intermediateRuleList.getSelectionModel().setSelectionMode($T.SINGLE)", selectionMode)
    		  .addStatement("intermediateRuleList.addEventHandler($T.MOUSE_CLICKED, this)", mouseEvent)
    		  .addStatement("intermediateRuleList.setMinWidth(272)")
    		  .addStatement("$T intermediateRuleListPane = new $T(intermediateRuleList)", scrollPane, scrollPane)
    		  .addStatement("intermediateRuleListPane.setMaxHeight(80)")
    		  .addStatement("ruleListsPane.getChildren().add(intermediateRuleListPane)")
    		  .addStatement("ruleListsPane.setPadding(new $T(0,40,0,40))", insets)
    		  .addStatement("rulesMainPane.getChildren().add(ruleListsPane)")
    		  .addStatement("// description pane")
    		  .addStatement("$T descriptionPane = new $T()", vBox, vBox)
    		  .addStatement("$T descriptionTitlePane = new $T()", pane, pane)
    		  .addStatement("descriptionTitlePane.getChildren().add(new $T(\"Description:\"))", label)
    		  .addStatement("descriptionPane.getChildren().add(descriptionTitlePane)")
    		  .addStatement("// description text area")
    		  .addStatement("descriptionArea = new TextArea()")
    		  .addStatement("descriptionArea.setWrapText(true)")
    		  .addStatement("descriptionArea.setPrefRowCount(16)")
    		  .addStatement("descriptionArea.setPrefColumnCount(30)")
    		  .addStatement("descriptionArea.setEditable(false)")
    		  .addStatement("$T descriptionScrollPane = new $T(descriptionArea)", scrollPane, scrollPane)
    		  .addStatement("descriptionScrollPane.setHbarPolicy($T.AS_NEEDED)", scrollBarPolicy)
    		  .addStatement("descriptionScrollPane.setVbarPolicy($T.NEVER)", scrollBarPolicy)
    		  .addStatement("descriptionPane.getChildren().add(descriptionScrollPane)")
    		  .addStatement("rulesMainPane.getChildren().add(descriptionPane)")
    		  .addStatement("// Create close button pane")
    		  .addStatement("$T closeButtonPane = new $T()", pane, pane)
    		  .addStatement("closeButton = new $T(\"Close\")", button)
    		  .addStatement("closeButton.addEventHandler($T.MOUSE_CLICKED, this)", mouseEvent)
    		  .addStatement("closeButtonPane.getChildren().add(closeButton)")
    		  .beginControlFlow("if (actions.size() > 0)")
    		  .addStatement("actionComboBox.getSelectionModel().select(0)")
    		  .endControlFlow()
    		  .addComment("set up tool tips:")
    		  .addStatement("setUpToolTips()")
    		  .addStatement("mainPane = new $T()", gridPane)
    		  .addStatement("mainPane.setHgap(10)")
    		  .addStatement("mainPane.setVgap(30)")
    		  .addStatement("mainPane.setPadding(new $T(0, 10, 0, 10))", insets)
    		  .addComment("Add panes to main pane and main sub-pane:")
    		  .addStatement("$T spacerPane = new $T()", pane, pane)
    		  .addStatement("spacerPane.setMinWidth(300)")
    		  .addStatement("mainPane.add(spacerPane, 0, 0)")
    		  .addStatement("mainPane.add(multipleTimelinesPanel, 1, 0)")
    		  .addStatement("mainPane.add(generateGraphsTitlePane, 1, 1)")
    		  .addStatement("mainPane.add(objectPane, 0, 2, 2, 1)")
    		  .addStatement("mainPane.add(actionPane, 2, 2)")
    		  .addStatement("mainPane.add(generateCompGraphPane, 1, 3)")
    		  .addStatement("mainPane.add(viewRulesTitlePane, 1, 4)")
    		  .addStatement("mainPane.add(actionComboBoxPane, 1, 5)")
    		  .addStatement("mainPane.add(rulesMainPane, 0, 6, 3, 1)")
    		  .addStatement("mainPane.add(closeButtonPane, 1, 7)")
    		  .addStatement("$T spacerPane2 = new $T()", pane, pane)
    		  .addStatement("mainPane.add(spacerPane2, 0, 8, 3, 1)")
    		  .addStatement("$T scene = new $T(mainPane, 900, 720)", scene, scene)
    		  .addStatement("scene.getStylesheets().add($S)", "style.css")
    		  .addStatement("this.setScene(scene)")
    		  .addStatement("hide()")
    		  .build();
      
      
      MethodSpec handle = MethodSpec.methodBuilder("handle")
    		  .addModifiers(Modifier.PUBLIC)
    		  .addParameter(mouseEvent, "evt")
    		  .addStatement("$T source = evt.getSource()", object)
    		  .beginControlFlow("if (source == multipleTimelinesButton)")
    		  .beginControlFlow("if (timelinesBrowser.isIconified())")
    		  .addStatement("timelinesBrowser.setIconified(false)")
    		  .endControlFlow()
    		  .addStatement("timelinesBrowser.show()")
    		  .addStatement("timelinesBrowser.toFront()")
    		  .endControlFlow()
    		  .beginControlFlow("else if (source == generateObjGraphButton)")
    		  .addStatement("$T selectedObj = ($T) objectList.getSelectionModel().getSelectedItem()", String.class, String.class)
    		  .addStatement("$T words = selectedObj.split(\"\\\\s\")", stringArray)
    		  .addStatement("$T title = selectedObj + \" Attributes\"", String.class)
    		  .addStatement("$T objType = words[0]", String.class)
    		  .addStatement("$T objTypeType = words[1]", String.class)
    		  .addStatement("// add 2 for the 2 spaces")
    		  .addStatement("$T keyAttVal = selectedObj.substring(objType.length() + objTypeType.length() + 2)", String.class)
    		  .addStatement("$T selectedAtts = attributeList.getSelectionModel().getSelectedItems().toArray()", objectArray)
    		  .addStatement("$T attributes = new $T[selectedAtts.length]", stringArray, String.class)
    		  .beginControlFlow("for (int i = 0; i < selectedAtts.length; i++)")
    		  .addStatement("attributes[i] = new $T(($T) selectedAtts[i])", String.class, String.class)
    		  .endControlFlow()
    		  .beginControlFlow("if (attributes.length > 0)")
    		  .addStatement("$T graph = new $T(title, log, objTypeType, objType, keyAttVal, attributes, true, branch)", objectGraph, objectGraph)
    		  .endControlFlow()
    		  .beginControlFlow("else ")
    		  .addStatement("$T alert = new $T($T.WARNING, \"Please select at least one attribute\")", alert, alert, alertType)
    		  .addStatement("alert.show()")
    		  .endControlFlow()
    		  .endControlFlow()
    		  .beginControlFlow("else if (source == generateActGraphButton)")
    		  .addStatement("$T selectedActions = actionList.getSelectionModel().getSelectedItems().toArray()", objectArray)
    		  .addStatement("$T actions = new $T[selectedActions.length]", stringArray, String.class)
    		  .beginControlFlow("for (int i = 0; i < selectedActions.length; i++)")
    		  .addStatement("actions[i] = new $T(($T) selectedActions[i])", String.class, String.class)
    		  .endControlFlow()
    		  .beginControlFlow("if (actions.length > 0)")
    		  .addStatement("$T graph = new $T(log, actions, true, branch)", actionGraph, actionGraph)
    		  .endControlFlow()
    		  .beginControlFlow("else ")
    		  .addStatement("$T alert = new $T($T.WARNING, \"Please select at least one action\")", alert, alert, alertType)
    		  .addStatement("alert.show()")
    		  .endControlFlow()
    		  .endControlFlow()
    		  .beginControlFlow("else if (source == generateCompGraphButton)")
    		  .addStatement("$T selectedObj = ($T) objectList.getSelectionModel().getSelectedItem()", String.class, String.class)
    		  .addStatement("$T words = selectedObj.split(\"\\\\s\")", stringArray)
    		  .addStatement("$T title = selectedObj + \" Attributes\"", String.class)
    		  .addStatement("$T objType = words[0]", String.class)
    		  .addStatement("$T objTypeType = words[1]", String.class)
    		  .addStatement("// add 2 for the 2 spaces")
    		  .addStatement("$T keyAttVal = selectedObj.substring(objType.length() + objTypeType.length() + 2)", String.class)
    		  .addStatement("$T selectedAtts = attributeList.getSelectionModel().getSelectedItems().toArray()", objectArray)
    		  .addStatement("$T attributes = new String[selectedAtts.length]", stringArray)
    		  .beginControlFlow("for (int i = 0; i < selectedAtts.length; i++)")
    		  .addStatement("attributes[i] = new $T(($T) selectedAtts[i])", String.class, String.class)
    		  .endControlFlow()
    		  .beginControlFlow("if (attributes.length > 0)")
    		  .addStatement("$T objGraph = new $T(title, log, objTypeType, objType, keyAttVal, attributes, false, branch)", objectGraph, objectGraph)
    		  .addStatement("$T selectedActions = actionList.getSelectionModel().getSelectedItems().toArray()", objectArray)
    		  .addStatement("$T actions = new $T[selectedActions.length]", stringArray, String.class)
    		  .beginControlFlow("for (int i = 0; i < selectedActions.length; i++)")
    		  .addStatement("actions[i] = new $T(($T) selectedActions[i])", String.class, String.class)
    		  .endControlFlow()
    		  .beginControlFlow("if (actions.length > 0)")
    		  .addStatement("$T actGraph = new $T(log, actions, false, branch)", actionGraph, actionGraph)
    		  .addStatement("$T compGraph = new $T(objGraph, actGraph, branch)", compositeGraph, compositeGraph)
    		  .endControlFlow()
    		  .beginControlFlow("else ")
    		  .addStatement("$T alert = new $T($T.WARNING, \"Please select at least one action\")", alert, alert, alertType)
    		  .addStatement("alert.show()")
    		  .endControlFlow()
    		  .endControlFlow()
    		  .beginControlFlow("else ")
    		  .addStatement("$T alert = new $T($T.WARNING, \"Please select at least one attribute\")", alert, alert, alertType)
    		  .addStatement("alert.show()")
    		  .endControlFlow()
    		  .endControlFlow()
    		  .beginControlFlow("else if ((source == attributeList) || (source == actionList))")
    		  .addStatement("$N()", refreshButtons)
    		  .endControlFlow()
    		  .beginControlFlow("else if ((source == triggerRuleList && !triggerRuleList.getSelectionModel().isEmpty()))")
    		  .addStatement("destroyerRuleList.getSelectionModel().clearSelection()")
    		  .addStatement("intermediateRuleList.getSelectionModel().clearSelection()")
    		  .addStatement("refreshDescriptionArea(($T) triggerRuleList.getSelectionModel().getSelectedItem())", String.class)
    		  .endControlFlow()
    		  .beginControlFlow("else if (source == destroyerRuleList && !destroyerRuleList.getSelectionModel().isEmpty())")
    		  .addStatement("triggerRuleList.getSelectionModel().clearSelection()")
    		  .addStatement("intermediateRuleList.getSelectionModel().clearSelection()")
    		  .addStatement("refreshDescriptionArea(($T) destroyerRuleList.getSelectionModel().getSelectedItem())", String.class)
    		  .endControlFlow()
    		  .beginControlFlow("else if (source == intermediateRuleList && !intermediateRuleList.getSelectionModel().isEmpty())")
    		  .addStatement("triggerRuleList.getSelectionModel().clearSelection()")
    		  .addStatement("destroyerRuleList.getSelectionModel().clearSelection()")
    		  .addStatement("refreshDescriptionArea(($T) intermediateRuleList.getSelectionModel().getSelectedItem())", String.class)
    		  .endControlFlow()
    		  .beginControlFlow("else if (source == closeButton)")
    		  .addStatement("close()")
    		  .endControlFlow()
    		  .build();
      
     
    	
    	MethodSpec update = MethodSpec.methodBuilder("update")
    			.addModifiers(Modifier.PUBLIC)
    			.beginControlFlow("for (int i = 0; i < visibleGraphs.size(); i++)")
    			.addStatement("$T graph = visibleGraphs.get(i)", stage)
    			.addStatement("// remove graphs whose windows have been closed from visibleGraphs:")
    			.beginControlFlow("if (!graph.isShowing())")
    			.addStatement("visibleGraphs.remove(graph)")
    			.endControlFlow()
    			.endControlFlow()
    			.addStatement("// update timelines browser:")
    			.beginControlFlow("if (timelinesBrowser != null)")
    			.addStatement("timelinesBrowser.update()")
    			.endControlFlow()
    			.build();
      
      
      MethodSpec setUpToolTips = MethodSpec.methodBuilder("setUpToolTips")
    		  .addModifiers(Modifier.PRIVATE)
    		  .addStatement("objectList.setTooltip(new $T(\"Choose an object to graph\"))", toolTip)
    		  .addStatement("attributeList.setTooltip(new $T(\"Choose which attributes to graph\"))", toolTip)
    		  .addStatement("actionList.setTooltip(new $T(\"Choose which actions to graph\"))", toolTip)
    		  .addStatement("actionComboBox.setTooltip(new $T(\"Choose which action to show rules for\"))", toolTip)
    		  .addStatement("triggerRuleList.setTooltip(new $T(\"Rules that execute at the beginning of the action\"))", toolTip)
    		  .addStatement("destroyerRuleList.setTooltip(new $T(\"Rules that execute at the end of the action\"))", toolTip)
    		  .addStatement("intermediateRuleList.setTooltip(new $T(\"Rules that execute every clock tick during the life of the action\"))", toolTip)
    		  .build();
     
      
      String ruleBlock = "";
      
      boolean writeElse = false;
      for (ActionType action : actions) {
        if (action.isVisibleInExplanatoryTool()) {
          if (writeElse) {
            ruleBlock += "else ";
          } else {
            writeElse = true;
          }
          ruleBlock += "if (actionName.equals(\"" + 
            		CodeGeneratorUtils.getUpperCaseLeading(action.getName()) + 
              		"\")) {\n";
          Vector<Rule> trigRules = action.getAllTriggerRules();
          if (trigRules.size() > 0) {
            ruleBlock += "String[] trigList = {\n";
            
            // go through all trigger rules:
            for (int j = 0; j < trigRules.size(); j++) {
              Rule trigRule = trigRules.get(j);
              if (trigRule.isVisibleInExplanatoryTool()) {
                ruleBlock += "\"" + trigRule.getName() + "\",\n";
              }
            }
            ruleBlock += "};\n";
            ruleBlock += "triggerRuleList.getItems().setAll(trigList);\n";
          }
          Vector<Rule> destRules = action.getAllDestroyerRules();
          if (destRules.size() > 0) {
            ruleBlock += "String[] destList = {\n";

            // go through all destroyer rules:
            for (int j = 0; j < destRules.size(); j++) {
              Rule destRule = destRules.get(j);
              if (destRule.isVisibleInExplanatoryTool()) {
                ruleBlock += "\"" + destRule.getName() + "\",\n";
              }
            }
            ruleBlock += "};\n";
            ruleBlock += "destroyerRuleList.getItems().setAll(destList);\n";
          }
          Vector<Rule> contRules = action.getAllContinuousRules();
          if (contRules.size() > 0) {
            ruleBlock += "String[] intList = {\n";

            // go through all continuous rules:
            for (int j = 0; j < contRules.size(); j++) {
              Rule contRule = contRules.get(j);
              if (contRule.isVisibleInExplanatoryTool()) {
                ruleBlock += "\"" + contRule.getName() + "\",\n";
              }
            }
            ruleBlock += "};\n";
            ruleBlock += "intermediateRuleList.getItems().setAll(intList);\n";
          }
          ruleBlock += "}\n";
        }
      }

      MethodSpec refreshRuleLists = MethodSpec.methodBuilder("refreshRuleLists")
    		  .addModifiers(Modifier.PRIVATE)
    		  .addParameter(String.class, "actionName")
    		  .addStatement("triggerRuleList.getItems().setAll(new $T<$T>())", Vector.class, String.class)
    		  .addStatement("destroyerRuleList.getItems().setAll(new $T<$T>())", Vector.class, String.class)
    		  .addStatement("intermediateRuleList.getItems().setAll(new $T<$T>())", Vector.class, String.class)
    		  .addCode(ruleBlock)
    		  .build();
      
 
      String actionBlock = "";
      
      // go through all actions:
      writeElse = false;
      for (ActionType action : actions) {
        if (action.isVisibleInExplanatoryTool()) {
          // go through all rules:
          Vector<Rule> rules = action.getAllRules();
          for (Rule rule : rules) {
            if (rule.isVisibleInExplanatoryTool()) {
              if (writeElse) {
                actionBlock += "else ";
              } else {
                writeElse = true;
              }
              actionBlock += "if (ruleName.equals(\"" + rule.getName() + 
                		"\")) {\n";
              actionBlock += "text = RuleDescriptions."
                      + action.getName().toUpperCase() + "_"
                      + rule.getName().toUpperCase() + ";\n";
              
            }
          }
        }
      }

      
      ClassName ruleCategories = ClassName.get("simse.util", "RuleCategories");

      MethodSpec refreshDescriptionArea = MethodSpec.methodBuilder("refreshDescriptionArea")
    		  .addModifiers(Modifier.PRIVATE)
    		  .addParameter(String.class, "ruleName")
    		  .beginControlFlow("if (ruleName != null)")
    		  .addStatement("$T text = $T.getRuleMapping(ruleName)", String.class, ruleCategories)
    		  .addStatement("descriptionArea.setText(text)")
    		  .addStatement("descriptionArea.positionCaret(0)")
    		  .endControlFlow()
    		  .build();
      
      MethodSpec getLog = MethodSpec.methodBuilder("getLog")
    		  .addModifiers(Modifier.PUBLIC)
    		  .returns(ParameterizedTypeName.get(arrayList, state))
    		  .addStatement("return this.log")
    		  .build();
      
      TypeSpec explanatoryTool = TypeSpec.classBuilder("ExplanatoryTool")
    		  .addModifiers(Modifier.PUBLIC)
    		  .superclass(stage)
    		  .addSuperinterface(ParameterizedTypeName.get(eventHandler, mouseEvent))
    		  .addField(ParameterizedTypeName.get(arrayList, state), "log", Modifier.PRIVATE)
    		  .addField(ParameterizedTypeName.get(arrayList, stage), "visibleGraphs", Modifier.PRIVATE)
    		  .addField(multipleTimelinesBrowser, "timelinesBrowser", Modifier.PRIVATE)
    		  .addField(button, "multipleTimelinesButton", Modifier.PRIVATE)
    		  .addField(ParameterizedTypeName.get(comboBox, string), "objectList", Modifier.PRIVATE)
    		  .addField(ParameterizedTypeName.get(listView, string), "attributeList", Modifier.PRIVATE)
    		  .addField(ParameterizedTypeName.get(listView, string), "actionList", Modifier.PRIVATE)
    		  .addField(button, "generateObjGraphButton", Modifier.PRIVATE)
    		  .addField(button, "generateActGraphButton", Modifier.PRIVATE)
    		  .addField(button, "generateCompGraphButton", Modifier.PRIVATE)
    		  .addField(ParameterizedTypeName.get(comboBox, string), "actionComboBox", Modifier.PRIVATE)
    		  .addField(ParameterizedTypeName.get(listView, string), "triggerRuleList", Modifier.PRIVATE)
    		  .addField(ParameterizedTypeName.get(listView, string), "destroyerRuleList", Modifier.PRIVATE)
    		  .addField(ParameterizedTypeName.get(listView, string), "intermediateRuleList", Modifier.PRIVATE)
    		  .addField(textArea, "descriptionArea", Modifier.PRIVATE)
    		  .addField(button, "closeButton", Modifier.PRIVATE)
    		  .addField(gridPane, "mainPane", Modifier.PRIVATE)
    		  .addField(branch, "branch", Modifier.PRIVATE)
    		  .addMethod(constructor)
    		  .addMethod(handle)
    		  .addMethod(update)
    		  .addMethod(refreshAttributeList)
    		  .addMethod(setUpToolTips)
    		  .addMethod(refreshButtons)
    		  .addMethod(refreshRuleLists)
    		  .addMethod(refreshDescriptionArea)
    		  .addMethod(getLog)
    		  .build();
      
      
      JavaFile javaFile = JavaFile.builder("simse.explanatorytool", explanatoryTool)
  		    .build();

    try {
    	FileWriter writer = new FileWriter(expToolFile);
		javaFile.writeTo(writer);
		
		writer.close();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
     
  }

  // copies the JFreeChart jars into the generated code directory
  private void copyJFreeChartJars() {
    try {
    // TODO: Libraries
      ZipInputStream zis = new ZipInputStream(ExplanatoryToolGenerator.class
          .getResourceAsStream("res/libraries.zip"));
      while (true) {
        ZipEntry ze = zis.getNextEntry();
        if (ze == null) {
          break;
        }
        new File(options.getCodeGenerationDestinationDirectory() + "\\lib\\" +
            ze.getName()).createNewFile();
        byte[] buffer = new byte[1024];
        int len = 1024;
        BufferedOutputStream out = new BufferedOutputStream(
            new FileOutputStream(
            		options.getCodeGenerationDestinationDirectory() + "\\lib\\" + 
                ze.getName()));
        while ((len = zis.read(buffer, 0, len)) >= 0) {
          out.write(buffer, 0, len);
        }
        out.close();
        zis.closeEntry();
      }
      zis.close();
    } catch (IOException ioe) {
      System.out.println("IOE");
      ioe.printStackTrace();
    }
  }
}