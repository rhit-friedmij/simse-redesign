package simse.codegenerator.guigenerator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

import javax.lang.model.element.Modifier;
import javax.swing.JOptionPane;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import simse.codegenerator.CodeGeneratorUtils;
import simse.modelbuilder.objectbuilder.Attribute;
import simse.modelbuilder.objectbuilder.AttributeTypes;
import simse.modelbuilder.objectbuilder.DefinedObjectTypes;
import simse.modelbuilder.objectbuilder.SimSEObjectType;
import simse.modelbuilder.objectbuilder.SimSEObjectTypeTypes;

public class InfoScreenGenerator {
	private DefinedObjectTypes objTypes;
	private File directory; // directory to save generated code into

	  public InfoScreenGenerator(DefinedObjectTypes objTypes, File directory) {
		this.objTypes = objTypes;
	    this.directory = directory;
	  }
	  
	  public void generate() {
		  generateArtifactInfoScreen();
		  generateEmployeeInfoScreen();
		  generateProjectInfoScreen();
		  generateCustomerInfoScreen();
		  generateToolInfoScreen();
	  }

	  private void generateArtifactInfoScreen() {
	    File aisFile = new File(directory, ("simse\\gui\\ArtifactInfoScreen.java"));
	    if (aisFile.exists()) {
	    	aisFile.delete(); // delete old version of file
	    }
	    try {
	      FileWriter writer = new FileWriter(aisFile);
	      
	      ClassName eventhandler = ClassName.get("javafx.event", "EventHandler");
	      ClassName pos = ClassName.get("javafx.geometry", "Pos");
	      ClassName scene = ClassName.get("javafx.scene", "Scene");
	      ClassName contextmenu = ClassName.get("javafx.scene.control", "ContextMenu");
	      ClassName label = ClassName.get("javafx.scene.control", "Label");
	      ClassName listview = ClassName.get("javafx.scene.control", "ListView");
	      ClassName imageview = ClassName.get("javafx.scene.image", "ImageView");
	      ClassName mouseevent = ClassName.get("javafx.scene.input", "MouseEvent");
	      ClassName stackpane = ClassName.get("javafx.scene.layout", "StackPane");
	      ClassName vbox = ClassName.get("javafx.scene.layout", "VBox");
	      ClassName font = ClassName.get("javafx.scene.text", "Font");
	      ClassName stage = ClassName.get("javafx.stage", "Stage");
	      ClassName artifact = ClassName.get("simse.adts.objects", "Artifact");
	      ClassName javafxhelpers = ClassName.get("simse.gui.util", "JavaFXHelpers");
	      ClassName logic = ClassName.get("simse.logic", "Logic");
	      ClassName state = ClassName.get("simse.state", "State");
	      ClassName simsegui = ClassName.get("simse.gui", "SimSEGUI");
	      ClassName string = ClassName.get(String.class);
	      TypeName mouseHandler = ParameterizedTypeName.get(eventhandler, mouseevent);
	      TypeName viewOfStrings = ParameterizedTypeName.get(listview, string);
	      
	      MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder()
	    		  .addModifiers(Modifier.PUBLIC)
	    		  .addParameter(state, "s")
	    		  .addParameter(simsegui, "gui")
	    		  .addParameter(logic, "l")
	    		  .addParameter(artifact, "artifact")
	    		  .addStatement("this.$N = s", "state")
	    		  .addStatement("this.$N = $N", "gui", "gui")
	    		  .addStatement("this.$N = l", "logic")
	    		  .addStatement("this.$N = $N", "artifact", "artifact")
	    		  .addStatement("this.$N = new $T()", "actions", contextmenu)
	    		  .addStatement("this.$N = new $T()", "mainPane", vbox)
	    		  .addStatement("String $NName = artifact.getKeyAsString()", "artifact")
	    		  .addStatement("this.setTitle(artifactName)")
	    		  .addStatement("$T imagePane = new $T()", stackpane, stackpane)
	    		  .addStatement("imagePane.setMinSize(110, 110)")
	    		  .addStatement("String imageUrl = TabPanel.getImage(this.artifact)")
	    		  .addStatement("$T img = $T.createImageView(imageUrl)", imageview, javafxhelpers)
	    		  .addStatement("img.setScaleX(2)")
	    		  .addStatement("img.setScaleY(2)")
	    		  .addStatement("imagePane.getChildren().add(img)")
	    		  .addStatement("$N = new $T<$T>()", "attributes", listview, string);
	      
	      	Vector<SimSEObjectType> objs = objTypes.getAllObjectTypes();
	      
			Vector<SimSEObjectType> types = new Vector<>();
			for (SimSEObjectType type : objs) {
				if (type.getType() == SimSEObjectTypeTypes.ARTIFACT) {
					types.add(type);
				}
			}
			
	      for (int i = 0; i < types.size(); i++) {
		      String atts = "";
		      if (i == 0) {
		    	  atts = atts.concat("if (artifact instanceof " + CodeGeneratorUtils.getUpperCaseLeading(types.get(i).getName()) + "){\n");
		      } else {
		    	  atts = atts.concat("else if (artifact instanceof " + CodeGeneratorUtils.getUpperCaseLeading(types.get(i).getName()) + "){\n");
		      }
		      for (Attribute att : types.get(i).getAllVisibleAttributes()) {
		    	  atts = atts.concat("attributes.getItems().add(\""+att.getName() + ": \" + " + getTypeAsToString(att)+
		    			  "(((" + CodeGeneratorUtils.getUpperCaseLeading(types.get(i).getName()) + ")artifact).get"+att.getName()+"()));\n");
		      }
		      
		      atts = atts.concat("}\n");
		      constructorBuilder.addCode(atts);
	      }

	    		  
	    		  
	    MethodSpec constructor = constructorBuilder.addStatement("$N.setPrefHeight($N.getItems().size()*25)", "attributes", "attributes")
	    		  .addStatement("$N.setMaxHeight(200)", "attributes")
	    		  .addStatement("String objTypeFull = $N.getClass().toString()", "artifact")
	    		  .addStatement("String[] objTypeArr = objTypeFull.split(\"\\\\.\")")
	    		  .addStatement("String objType = objTypeArr[objTypeArr.length - 1]")
	    		  .addStatement("String objTypeType = \"$T\"", artifact)
	    		  .addStatement("String title = artifactName + \" Attributes\"")
	    		  .addStatement("ObjectGraphPane objGraph = new ObjectGraphPane(title, $N.getLog(), objTypeType, objType, artifactName, $N.getBranch(), $N)", "gui", "gui", "gui")
	    		  .addStatement("$T name = new $T($N.getKeyAsString())", label, label, "artifact")
	    		  .addStatement("name.set$T(new Font(30))", font)
	    		  .addStatement("$N.getChildren().add(name)", "mainPane")
	    		  .addStatement("$N.getChildren().add(imagePane)", "mainPane")
	    		  .addStatement("$N.getChildren().add($N)", "mainPane", "attributes")
	    		  .addStatement("$N.getChildren().add(objGraph)", "mainPane")
	    		  .addStatement("$N.setAlignment($T.CENTER)", "mainPane", pos)
	    		  .addStatement("$T scene = new $T(mainPane, 500, 700)", scene, scene)
	    		  .addStatement("this.setScene(scene)")
	    		  .build();
	      
	      MethodSpec handle = MethodSpec.methodBuilder("handle")
	    		  .addAnnotation(Override.class)
	    		  .addModifiers(Modifier.PUBLIC)
	    		  .returns(void.class)
	    		  .addParameter(mouseevent, "e")
	    		  .addStatement("$N.show(mainPane, e.getScreenX(), e.getScreenY())", "actions")
	    		  .build();
	      
	      TypeSpec ais = TypeSpec.classBuilder("ArtifactInfoScreen")
	    		  .addModifiers(Modifier.PUBLIC)
	    		  .superclass(stage)
	    		  .addSuperinterface(mouseHandler)
	    		  .addField(contextmenu, "actions")
	    		  .addField(vbox, "mainPane")
	    		  .addField(simsegui, "gui")
	    		  .addField(logic, "logic")
	    		  .addField(state, "state")
	    		  .addField(artifact, "artifact")
	    		  .addField(viewOfStrings, "attributes")
	    		  .addMethod(constructor)
	    		  .addMethod(handle)
	    		  .build();
	      
	      JavaFile file = JavaFile.builder("", ais)
					 .build();
	      String toAppend = "package simse.gui;\n"
					+ "import simse.adts.objects.*;\n";
	      
	      writer.write(toAppend + file.toString());
	      
	      writer.close();
	    } catch (IOException e) {
	      JOptionPane.showMessageDialog(null, ("Error writing file "
	          + aisFile.getPath() + ": " + e.toString()), "File IO Error",
	          JOptionPane.WARNING_MESSAGE);
	    }
	  }
	  
	  private void generateEmployeeInfoScreen() {
		  File eisFile = new File(directory, ("simse\\gui\\EmployeeInfoScreen.java"));
		    if (eisFile.exists()) {
		    	eisFile.delete(); // delete old version of file
		    }
		    try {
		      FileWriter writer = new FileWriter(eisFile);
		      
		      ClassName actionevent = ClassName.get("javafx.event", "ActionEvent");
		      ClassName eventhandler = ClassName.get("javafx.event", "EventHandler");
		      ClassName pos = ClassName.get("javafx.geometry", "Pos");
		      ClassName scene = ClassName.get("javafx.scene", "Scene");
		      ClassName button = ClassName.get("javafx.scene.control", "Button");
		      ClassName contextmenu = ClassName.get("javafx.scene.control", "ContextMenu");
		      ClassName label = ClassName.get("javafx.scene.control", "Label");
		      ClassName listview = ClassName.get("javafx.scene.control", "ListView");
		      ClassName menuitem = ClassName.get("javafx.scene.control", "MenuItem");
		      ClassName imageview = ClassName.get("javafx.scene.image", "ImageView");
		      ClassName mouseevent = ClassName.get("javafx.scene.input", "MouseEvent");
		      ClassName stackpane = ClassName.get("javafx.scene.layout", "StackPane");
		      ClassName vbox = ClassName.get("javafx.scene.layout", "VBox");
		      ClassName font = ClassName.get("javafx.scene.text", "Font");
		      ClassName stage = ClassName.get("javafx.stage", "Stage");
		      ClassName employee = ClassName.get("simse.adts.objects", "Employee");
		      ClassName logic = ClassName.get("simse.logic", "Logic");
		      ClassName state = ClassName.get("simse.state", "State");
		      ClassName simsegui = ClassName.get("simse.gui", "SimSEGUI");
		      ClassName string = ClassName.get(String.class);
		      TypeName mouseHandler = ParameterizedTypeName.get(eventhandler, mouseevent);
		      TypeName viewOfStrings = ParameterizedTypeName.get(listview, string);
		      TypeName actionHandler = ParameterizedTypeName.get(eventhandler, actionevent);
		      
		      MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder()
		    		  .addModifiers(Modifier.PUBLIC)
		    		  .addParameter(state, "s")
		    		  .addParameter(simsegui, "gui")
		    		  .addParameter(logic, "l")
		    		  .addParameter(employee, "employee")
		    		  .addStatement("this.$N = s", "state")
		    		  .addStatement("this.$N = $N", "gui", "gui")
		    		  .addStatement("this.$N = l", "logic")
		    		  .addStatement("this.$N = new $T()", "mainPane", vbox)
		    		  .addStatement("this.$N = new $T()", "actions", contextmenu)
		    		  .addStatement("this.employee = employee")
		    		  .addStatement("String employeeName = employee.getKeyAsString()")
		    		  .addStatement("this.setTitle(employeeName)")
		    		  .addStatement("Vector<String> menuItems = employee.getMenu()")
		    		  .beginControlFlow("for (int i=0; i < menuItems.size(); i++)")
		    		  .addStatement("String item = menuItems.elementAt(i)")
		    		  .addStatement("$T tempItem = new $T(item)", menuitem, menuitem)
		    		  .addStatement("tempItem.setOnAction(menuItemEvent)")
		    		  .addStatement("$N.getItems().add(tempItem)", "actions")
		    		  .endControlFlow()
		    		  .addStatement("$T imagePane = new $T()", stackpane, stackpane)
		    		  .addStatement("imagePane.setMinSize(110, 110)")
		    		  .addStatement("String imageUrl = TabPanel.getImage(this.employee)")
		    		  .addStatement("$T img = employee.getCharacterModel().getDisplayedCharacter(true)", imageview)
		    		  .beginControlFlow("if(employee.getCharacterModel().getCharacterNum() < 8)")
		    		  .addStatement("img.setScaleX(4.5)")
		    		  .addStatement("img.setScaleY(4.5)")
		    		  .nextControlFlow("else ")
		    		  .addStatement("img.setScaleX(3)")
		    		  .addStatement("img.setScaleY(3)")
		    		  .endControlFlow()
		    		  .addStatement("imagePane.getChildren().add(img)")
		    		  .addStatement("$N = new $T(\"Assign $T to Tasks\")", "actionsButton", button, employee)
		    		  .addStatement("$N.addEventHandler($T.MOUSE_CLICKED, this)", "actionsButton", mouseevent)
		    		  .addStatement("$N = new $T<$T>()", "attributes", listview, string);
		    	      
		  	      	Vector<SimSEObjectType> objs = objTypes.getAllObjectTypes();
		  	      
		  			Vector<SimSEObjectType> types = new Vector<>();
		  			for (SimSEObjectType type : objs) {
		  				if (type.getType() == SimSEObjectTypeTypes.EMPLOYEE) {
		  					types.add(type);
		  				}
		  			}
		  			
		  	      for (int i = 0; i < types.size(); i++) {
		  		      String atts = "";
		  		      if (i == 0) {
		  		    	  atts = atts.concat("if (employee instanceof " + CodeGeneratorUtils.getUpperCaseLeading(types.get(i).getName()) + "){\n");
		  		      } else {
		  		    	  atts = atts.concat("else if (employee instanceof " + CodeGeneratorUtils.getUpperCaseLeading(types.get(i).getName()) + "){\n");
		  		      }
		  		      for (Attribute att : types.get(i).getAllVisibleAttributes()) {
		  		    	  atts = atts.concat("attributes.getItems().add(\""+att.getName() + ": \" + " + getTypeAsToString(att)+
		  		    			  "(((" + CodeGeneratorUtils.getUpperCaseLeading(types.get(i).getName()) + ")employee).get"+att.getName()+"()));\n");
		  		      }
		  		      
		  		      atts = atts.concat("}\n");
		  		      constructorBuilder.addCode(atts);
		  	      }

		  	    		  
		  	    		  
		  	    MethodSpec constructor = constructorBuilder.addStatement("$N.setPrefHeight($N.getItems().size()*25)", "attributes", "attributes")
			    		  .addStatement("$N.setMaxHeight(200)", "attributes")
		    		  .addStatement("String objTypeFull = employee.getClass().toString()")
		    		  .addStatement("String[] objTypeArr = objTypeFull.split(\"\\\\.\")")
		    		  .addStatement("String objType = objTypeArr[objTypeArr.length - 1]")
		    		  .addStatement("String objTypeType = \"$T\"", employee)
		    		  .addStatement("String title = employeeName + \" Attributes\"")
		    		  .addStatement("ObjectGraphPane objGraph = new ObjectGraphPane(title, $N.getLog(), objTypeType, objType, employeeName, $N.getBranch(), $N)", "gui", "gui", "gui")
		    		  .addStatement("$T name = new $T(employee.getKeyAsString())", label, label)
		    		  .addStatement("name.set$T(new Font(30))", font)
		    		  .addStatement("$N.getChildren().add(name)", "mainPane")
		    		  .addStatement("$N.getChildren().add(imagePane)", "mainPane")
		    		  .addStatement("$N.getChildren().add($N)", "mainPane", "actionsButton")
		    		  .addStatement("$N.getChildren().add($N)", "mainPane", "attributes")
		    		  .addStatement("$N.getChildren().add(objGraph)", "mainPane")
		    		  .addStatement("$N.setAlignment($T.CENTER)", "mainPane", pos)
		    		  .addStatement("$T scene = new $T(mainPane, 500, 700)", scene, scene)
		    		  .addStatement("this.setScene(scene)")
		    		  .build();
		      
		      MethodSpec pop = MethodSpec.methodBuilder("popupMenuActions")
		    		  .addModifiers(Modifier.PUBLIC)
		    		  .returns(void.class)
		    		  .addParameter(menuitem, "source")
		    		  .addStatement("$T item = ($T) source", menuitem, menuitem)
		    		  .addStatement("$N.getMenuInputManager().menuItemSelected(employee, item.getText(), $N)", "logic", "gui")
		    		  .addStatement("$N.getWorld().update()", "gui")
		    		  .build();
		      
		      MethodSpec handle = MethodSpec.methodBuilder("handle")
		    		  .addAnnotation(Override.class)
		    		  .addModifiers(Modifier.PUBLIC)
		    		  .returns(void.class)
		    		  .addParameter(mouseevent, "e")
		    		  .addStatement("$N.show(mainPane, e.getScreenX(), e.getScreenY())", "actions")
		    		  .build();
		      
		      TypeSpec anon = TypeSpec.anonymousClassBuilder("")
		    		  .addSuperinterface(actionHandler)
		    		  .addMethod(MethodSpec.methodBuilder("handle")
		    				  .addModifiers(Modifier.PUBLIC)
		    				  .returns(void.class)
		    				  .addParameter(actionevent, "event")
		    				  .addStatement("Object source = event.getSource()")
		    				  .beginControlFlow("if (source instanceof $T)", menuitem)
		    				  .addStatement("popupMenuActions(($T) source)", menuitem)
		    				  .endControlFlow()
		    				  .build())
		    		  .build();
		      
		      TypeSpec eis = TypeSpec.classBuilder("EmployeeInfoScreen")
		    		  .addModifiers(Modifier.PUBLIC)
		    		  .superclass(stage)
		    		  .addSuperinterface(mouseHandler)
		    		  .addField(contextmenu, "actions")
		    		  .addField(vbox, "mainPane")
		    		  .addField(simsegui, "gui")
		    		  .addField(logic, "logic")
		    		  .addField(state, "state")
		    		  .addField(employee, "employee")
		    		  .addField(button, "actionsButton")
		    		  .addField(viewOfStrings, "attributes")
		    		  .addField(FieldSpec.builder(actionHandler, "menuItemEvent", Modifier.PRIVATE)
		    				  .initializer("$L", anon)
		    				  .build())
		    		  .addMethod(constructor)
		    		  .addMethod(pop)
		    		  .addMethod(handle)
		    		  .build();
		      
		      JavaFile file = JavaFile.builder("", eis)
						 .build();
		      
		      String fileString = "package simse.gui;\n\nimport java.util.Vector;\nimport simse.adts.objects.*;\n"+file.toString();
		      
		      writer.write(fileString);
		      
		      writer.close();
		    } catch (IOException e) {
		      JOptionPane.showMessageDialog(null, ("Error writing file "
		          + eisFile.getPath() + ": " + e.toString()), "File IO Error",
		          JOptionPane.WARNING_MESSAGE);
		    }
	  }
	  
	  private void generateProjectInfoScreen() {
		    File pisFile = new File(directory, ("simse\\gui\\ProjectInfoScreen.java"));
		    if (pisFile.exists()) {
		    	pisFile.delete(); // delete old version of file
		    }
		    try {
		      FileWriter writer = new FileWriter(pisFile);
		      
		      ClassName eventhandler = ClassName.get("javafx.event", "EventHandler");
		      ClassName pos = ClassName.get("javafx.geometry", "Pos");
		      ClassName scene = ClassName.get("javafx.scene", "Scene");
		      ClassName contextmenu = ClassName.get("javafx.scene.control", "ContextMenu");
		      ClassName label = ClassName.get("javafx.scene.control", "Label");
		      ClassName listview = ClassName.get("javafx.scene.control", "ListView");
		      ClassName imageview = ClassName.get("javafx.scene.image", "ImageView");
		      ClassName mouseevent = ClassName.get("javafx.scene.input", "MouseEvent");
		      ClassName stackpane = ClassName.get("javafx.scene.layout", "StackPane");
		      ClassName vbox = ClassName.get("javafx.scene.layout", "VBox");
		      ClassName font = ClassName.get("javafx.scene.text", "Font");
		      ClassName stage = ClassName.get("javafx.stage", "Stage");
		      ClassName project = ClassName.get("simse.adts.objects", "Project");
		      ClassName javafxhelpers = ClassName.get("simse.gui.util", "JavaFXHelpers");
		      ClassName state = ClassName.get("simse.state", "State");
		      ClassName simsegui = ClassName.get("simse.gui", "SimSEGUI");
		      ClassName string = ClassName.get(String.class);
		      TypeName mouseHandler = ParameterizedTypeName.get(eventhandler, mouseevent);
		      TypeName viewOfStrings = ParameterizedTypeName.get(listview, string);
		      
		      MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder()
		    		  .addModifiers(Modifier.PUBLIC)
		    		  .addParameter(state, "s")
		    		  .addParameter(simsegui, "gui")
		    		  .addParameter(project, "project")
		    		  .addStatement("this.$N = s", "state")
		    		  .addStatement("this.$N = $N", "gui", "gui")
		    		  .addStatement("this.$N = $N", "project", "project")
		    		  .addStatement("this.$N = new $T()", "actions", contextmenu)
		    		  .addStatement("this.$N = new $T()", "mainPane", vbox)
		    		  .addStatement("String $NName = project.getKeyAsString()", "project")
		    		  .addStatement("this.setTitle(projectName)")
		    		  .addStatement("$T imagePane = new $T()", stackpane, stackpane)
		    		  .addStatement("imagePane.setMinSize(110, 110)")
		    		  .addStatement("String imageUrl = TabPanel.getImage(this.project)")
		    		  .addStatement("$T img = $T.createImageView(imageUrl)", imageview, javafxhelpers)
		    		  .addStatement("img.setScaleX(2)")
		    		  .addStatement("img.setScaleY(2)")
		    		  .addStatement("imagePane.getChildren().add(img)")
		    		  .addStatement("$N = new $T<$T>()", "attributes", listview, string);
		      
		      	Vector<SimSEObjectType> objs = objTypes.getAllObjectTypes();
		      
				Vector<SimSEObjectType> types = new Vector<>();
				for (SimSEObjectType type : objs) {
					if (type.getType() == SimSEObjectTypeTypes.PROJECT) {
						types.add(type);
					}
				}
				
		      for (int i = 0; i < types.size(); i++) {
			      String atts = "";
			      if (i == 0) {
			    	  atts = atts.concat("if (project instanceof " + CodeGeneratorUtils.getUpperCaseLeading(types.get(i).getName()) + "){\n");
			      } else {
			    	  atts = atts.concat("else if (project instanceof " + CodeGeneratorUtils.getUpperCaseLeading(types.get(i).getName()) + "){\n");
			      }
			      for (Attribute att : types.get(i).getAllVisibleAttributes()) {
			    	  atts = atts.concat("attributes.getItems().add(\""+att.getName() + ": \" + " + getTypeAsToString(att)+
			    			  "(((" + CodeGeneratorUtils.getUpperCaseLeading(types.get(i).getName()) + ")project).get"+att.getName()+"()));\n");
			      }
			      
			      atts = atts.concat("}\n");
			      constructorBuilder.addCode(atts);
		      }

		    		  
		    		  
		    MethodSpec constructor = constructorBuilder.addStatement("$N.setPrefHeight($N.getItems().size()*25)", "attributes", "attributes")
		    		  .addStatement("$N.setMaxHeight(200)", "attributes")
		    		  .addStatement("String objTypeFull = $N.getClass().toString()", "project")
		    		  .addStatement("String[] objTypeArr = objTypeFull.split(\"\\\\.\")")
		    		  .addStatement("String objType = objTypeArr[objTypeArr.length - 1]")
		    		  .addStatement("String objTypeType = \"$T\"", project)
		    		  .addStatement("String title = projectName + \" Attributes\"")
		    		  .addStatement("ObjectGraphPane objGraph = new ObjectGraphPane(title, $N.getLog(), objTypeType, objType, projectName, $N.getBranch(), $N)", "gui", "gui", "gui")
		    		  .addStatement("$T name = new $T($N.getKeyAsString())", label, label, "project")
		    		  .addStatement("name.set$T(new Font(30))", font)
		    		  .addStatement("$N.getChildren().add(name)", "mainPane")
		    		  .addStatement("$N.getChildren().add(imagePane)", "mainPane")
		    		  .addStatement("$N.getChildren().add($N)", "mainPane", "attributes")
		    		  .addStatement("$N.getChildren().add(objGraph)", "mainPane")
		    		  .addStatement("$N.setAlignment($T.CENTER)", "mainPane", pos)
		    		  .addStatement("$T scene = new $T(mainPane, 500, 700)", scene, scene)
		    		  .addStatement("this.setScene(scene)")
		    		  .build();
		      
		      MethodSpec handle = MethodSpec.methodBuilder("handle")
		    		  .addAnnotation(Override.class)
		    		  .addModifiers(Modifier.PUBLIC)
		    		  .returns(void.class)
		    		  .addParameter(mouseevent, "e")
		    		  .addStatement("$N.show(mainPane, e.getScreenX(), e.getScreenY())", "actions")
		    		  .build();
		      
		      TypeSpec pis = TypeSpec.classBuilder("ProjectInfoScreen")
		    		  .addModifiers(Modifier.PUBLIC)
		    		  .superclass(stage)
		    		  .addSuperinterface(mouseHandler)
		    		  .addField(contextmenu, "actions")
		    		  .addField(vbox, "mainPane")
		    		  .addField(simsegui, "gui")
		    		  .addField(state, "state")
		    		  .addField(project, "project")
		    		  .addField(viewOfStrings, "attributes")
		    		  .addMethod(constructor)
		    		  .addMethod(handle)
		    		  .build();
		      
		      JavaFile file = JavaFile.builder("", pis)
						 .build();
		      String toAppend = "package simse.gui;\n"
						+ "import simse.adts.objects.*;\n";
		      
		      writer.write(toAppend + file.toString());
		      
		      writer.close();
		    } catch (IOException e) {
		      JOptionPane.showMessageDialog(null, ("Error writing file "
		          + pisFile.getPath() + ": " + e.toString()), "File IO Error",
		          JOptionPane.WARNING_MESSAGE);
		    }
		  }
	  
	  private void generateCustomerInfoScreen() {
		    File cisFile = new File(directory, ("simse\\gui\\CustomerInfoScreen.java"));
		    if (cisFile.exists()) {
		    	cisFile.delete(); // delete old version of file
		    }
		    try {
		      FileWriter writer = new FileWriter(cisFile);
		      
		      ClassName eventhandler = ClassName.get("javafx.event", "EventHandler");
		      ClassName pos = ClassName.get("javafx.geometry", "Pos");
		      ClassName scene = ClassName.get("javafx.scene", "Scene");
		      ClassName contextmenu = ClassName.get("javafx.scene.control", "ContextMenu");
		      ClassName label = ClassName.get("javafx.scene.control", "Label");
		      ClassName listview = ClassName.get("javafx.scene.control", "ListView");
		      ClassName imageview = ClassName.get("javafx.scene.image", "ImageView");
		      ClassName mouseevent = ClassName.get("javafx.scene.input", "MouseEvent");
		      ClassName stackpane = ClassName.get("javafx.scene.layout", "StackPane");
		      ClassName vbox = ClassName.get("javafx.scene.layout", "VBox");
		      ClassName font = ClassName.get("javafx.scene.text", "Font");
		      ClassName stage = ClassName.get("javafx.stage", "Stage");
		      ClassName customer = ClassName.get("simse.adts.objects", "Customer");
		      ClassName javafxhelpers = ClassName.get("simse.gui.util", "JavaFXHelpers");
		      ClassName state = ClassName.get("simse.state", "State");
		      ClassName simsegui = ClassName.get("simse.gui", "SimSEGUI");
		      ClassName string = ClassName.get(String.class);
		      TypeName mouseHandler = ParameterizedTypeName.get(eventhandler, mouseevent);
		      TypeName viewOfStrings = ParameterizedTypeName.get(listview, string);
		      
		      MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder()
		    		  .addModifiers(Modifier.PUBLIC)
		    		  .addParameter(state, "s")
		    		  .addParameter(simsegui, "gui")
		    		  .addParameter(customer, "customer")
		    		  .addStatement("this.$N = s", "state")
		    		  .addStatement("this.$N = $N", "gui", "gui")
		    		  .addStatement("this.$N = $N", "customer", "customer")
		    		  .addStatement("this.$N = new $T()", "actions", contextmenu)
		    		  .addStatement("this.$N = new $T()", "mainPane", vbox)
		    		  .addStatement("String $NName = customer.getKeyAsString()", "customer")
		    		  .addStatement("this.setTitle(customerName)")
		    		  .addStatement("$T imagePane = new $T()", stackpane, stackpane)
		    		  .addStatement("imagePane.setMinSize(110, 110)")
		    		  .addStatement("String imageUrl = TabPanel.getImage(this.customer)")
		    		  .addStatement("$T img = $T.createImageView(imageUrl)", imageview, javafxhelpers)
		    		  .addStatement("img.setScaleX(2)")
		    		  .addStatement("img.setScaleY(2)")
		    		  .addStatement("imagePane.getChildren().add(img)")
		    		  .addStatement("$N = new $T<$T>()", "attributes", listview, string);
		      
		      	Vector<SimSEObjectType> objs = objTypes.getAllObjectTypes();
		      
				Vector<SimSEObjectType> types = new Vector<>();
				for (SimSEObjectType type : objs) {
					if (type.getType() == SimSEObjectTypeTypes.CUSTOMER) {
						types.add(type);
					}
				}
				
		      for (int i = 0; i < types.size(); i++) {
			      String atts = "";
			      if (i == 0) {
			    	  atts = atts.concat("if (customer instanceof " + CodeGeneratorUtils.getUpperCaseLeading(types.get(i).getName()) + "){\n");
			      } else {
			    	  atts = atts.concat("else if (customer instanceof " + CodeGeneratorUtils.getUpperCaseLeading(types.get(i).getName()) + "){\n");
			      }
			      for (Attribute att : types.get(i).getAllVisibleAttributes()) {
			    	  atts = atts.concat("attributes.getItems().add(\""+att.getName() + ": \" + " + getTypeAsToString(att)+
			    			  "(((" + CodeGeneratorUtils.getUpperCaseLeading(types.get(i).getName()) + ")customer).get"+att.getName()+"()));\n");
			      }
			      
			      atts = atts.concat("}\n");
			      constructorBuilder.addCode(atts);
		      }

		    		  
		    		  
		    MethodSpec constructor = constructorBuilder.addStatement("$N.setPrefHeight($N.getItems().size()*25)", "attributes", "attributes")
		    		  .addStatement("$N.setMaxHeight(200)", "attributes")
		    		  .addStatement("String objTypeFull = $N.getClass().toString()", "customer")
		    		  .addStatement("String[] objTypeArr = objTypeFull.split(\"\\\\.\")")
		    		  .addStatement("String objType = objTypeArr[objTypeArr.length - 1]")
		    		  .addStatement("String objTypeType = \"$T\"", customer)
		    		  .addStatement("String title = customerName + \" Attributes\"")
		    		  .addStatement("ObjectGraphPane objGraph = new ObjectGraphPane(title, $N.getLog(), objTypeType, objType, customerName, $N.getBranch(), $N)", "gui", "gui", "gui")
		    		  .addStatement("$T name = new $T($N.getKeyAsString())", label, label, "customer")
		    		  .addStatement("name.set$T(new Font(30))", font)
		    		  .addStatement("$N.getChildren().add(name)", "mainPane")
		    		  .addStatement("$N.getChildren().add(imagePane)", "mainPane")
		    		  .addStatement("$N.getChildren().add($N)", "mainPane", "attributes")
		    		  .addStatement("$N.getChildren().add(objGraph)", "mainPane")
		    		  .addStatement("$N.setAlignment($T.CENTER)", "mainPane", pos)
		    		  .addStatement("$T scene = new $T(mainPane, 500, 700)", scene, scene)
		    		  .addStatement("this.setScene(scene)")
		    		  .build();
		      
		      MethodSpec handle = MethodSpec.methodBuilder("handle")
		    		  .addAnnotation(Override.class)
		    		  .addModifiers(Modifier.PUBLIC)
		    		  .returns(void.class)
		    		  .addParameter(mouseevent, "e")
		    		  .addStatement("$N.show(mainPane, e.getScreenX(), e.getScreenY())", "actions")
		    		  .build();
		      
		      TypeSpec cis = TypeSpec.classBuilder("CustomerInfoScreen")
		    		  .addModifiers(Modifier.PUBLIC)
		    		  .superclass(stage)
		    		  .addSuperinterface(mouseHandler)
		    		  .addField(contextmenu, "actions")
		    		  .addField(vbox, "mainPane")
		    		  .addField(simsegui, "gui")
		    		  .addField(state, "state")
		    		  .addField(customer, "customer")
		    		  .addField(viewOfStrings, "attributes")
		    		  .addMethod(constructor)
		    		  .addMethod(handle)
		    		  .build();
		      
		      JavaFile file = JavaFile.builder("", cis)
						 .build();
		      String toAppend = "package simse.gui;\n"
						+ "import simse.adts.objects.*;\n";
		      
		      writer.write(toAppend + file.toString());
		      
		      writer.close();
		    } catch (IOException e) {
		      JOptionPane.showMessageDialog(null, ("Error writing file "
		          + cisFile.getPath() + ": " + e.toString()), "File IO Error",
		          JOptionPane.WARNING_MESSAGE);
		    }
	  }
	  
	  private void generateToolInfoScreen() {
		    File cisFile = new File(directory, ("simse\\gui\\ToolInfoScreen.java"));
		    if (cisFile.exists()) {
		    	cisFile.delete(); // delete old version of file
		    }
		    try {
		      FileWriter writer = new FileWriter(cisFile);
		      
		      ClassName eventhandler = ClassName.get("javafx.event", "EventHandler");
		      ClassName pos = ClassName.get("javafx.geometry", "Pos");
		      ClassName scene = ClassName.get("javafx.scene", "Scene");
		      ClassName contextmenu = ClassName.get("javafx.scene.control", "ContextMenu");
		      ClassName label = ClassName.get("javafx.scene.control", "Label");
		      ClassName listview = ClassName.get("javafx.scene.control", "ListView");
		      ClassName imageview = ClassName.get("javafx.scene.image", "ImageView");
		      ClassName mouseevent = ClassName.get("javafx.scene.input", "MouseEvent");
		      ClassName stackpane = ClassName.get("javafx.scene.layout", "StackPane");
		      ClassName vbox = ClassName.get("javafx.scene.layout", "VBox");
		      ClassName font = ClassName.get("javafx.scene.text", "Font");
		      ClassName stage = ClassName.get("javafx.stage", "Stage");
		      ClassName tool = ClassName.get("simse.adts.objects", "Tool");
		      ClassName javafxhelpers = ClassName.get("simse.gui.util", "JavaFXHelpers");
		      ClassName state = ClassName.get("simse.state", "State");
		      ClassName simsegui = ClassName.get("simse.gui", "SimSEGUI");
		      ClassName string = ClassName.get(String.class);
		      TypeName mouseHandler = ParameterizedTypeName.get(eventhandler, mouseevent);
		      TypeName viewOfStrings = ParameterizedTypeName.get(listview, string);
		      
		      MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder()
		    		  .addModifiers(Modifier.PUBLIC)
		    		  .addParameter(state, "s")
		    		  .addParameter(simsegui, "gui")
		    		  .addParameter(tool, "tool")
		    		  .addStatement("this.$N = s", "state")
		    		  .addStatement("this.$N = $N", "gui", "gui")
		    		  .addStatement("this.$N = $N", "tool", "tool")
		    		  .addStatement("this.$N = new $T()", "actions", contextmenu)
		    		  .addStatement("this.$N = new $T()", "mainPane", vbox)
		    		  .addStatement("String $NName = tool.getKeyAsString()", "tool")
		    		  .addStatement("this.setTitle(toolName)")
		    		  .addStatement("$T imagePane = new $T()", stackpane, stackpane)
		    		  .addStatement("imagePane.setMinSize(110, 110)")
		    		  .addStatement("String imageUrl = TabPanel.getImage(this.tool)")
		    		  .addStatement("$T img = $T.createImageView(imageUrl)", imageview, javafxhelpers)
		    		  .addStatement("img.setScaleX(2)")
		    		  .addStatement("img.setScaleY(2)")
		    		  .addStatement("imagePane.getChildren().add(img)")
		    		  .addStatement("$N = new $T<$T>()", "attributes", listview, string);
		      
		      	Vector<SimSEObjectType> objs = objTypes.getAllObjectTypes();
		      
				Vector<SimSEObjectType> types = new Vector<>();
				for (SimSEObjectType type : objs) {
					if (type.getType() == SimSEObjectTypeTypes.TOOL) {
						types.add(type);
					}
				}
				
		      for (int i = 0; i < types.size(); i++) {
			      String atts = "";
			      if (i == 0) {
			    	  atts = atts.concat("if (tool instanceof " + CodeGeneratorUtils.getUpperCaseLeading(types.get(i).getName()) + "){\n");
			      } else {
			    	  atts = atts.concat("else if (tool instanceof " + CodeGeneratorUtils.getUpperCaseLeading(types.get(i).getName()) + "){\n");
			      }
			      for (Attribute att : types.get(i).getAllVisibleAttributes()) {
			    	  atts = atts.concat("attributes.getItems().add(\""+att.getName() + ": \" + " + getTypeAsToString(att)+
			    			  "(((" + CodeGeneratorUtils.getUpperCaseLeading(types.get(i).getName()) + ")tool).get"+att.getName()+"()));\n");
			      }
			      
			      atts = atts.concat("}\n");
			      constructorBuilder.addCode(atts);
		      }

		    		  
		    		  
		    MethodSpec constructor = constructorBuilder.addStatement("$N.setPrefHeight($N.getItems().size()*25)", "attributes", "attributes")
		    		  .addStatement("$N.setMaxHeight(200)", "attributes")
		    		  .addStatement("String objTypeFull = $N.getClass().toString()", "tool")
		    		  .addStatement("String[] objTypeArr = objTypeFull.split(\"\\\\.\")")
		    		  .addStatement("String objType = objTypeArr[objTypeArr.length - 1]")
		    		  .addStatement("String objTypeType = \"$T\"", tool)
		    		  .addStatement("String title = toolName + \" Attributes\"")
		    		  .addStatement("ObjectGraphPane objGraph = new ObjectGraphPane(title, $N.getLog(), objTypeType, objType, toolName, $N.getBranch(), $N)", "gui", "gui", "gui")
		    		  .addStatement("$T name = new $T($N.getKeyAsString())", label, label, "tool")
		    		  .addStatement("name.set$T(new Font(30))", font)
		    		  .addStatement("$N.getChildren().add(name)", "mainPane")
		    		  .addStatement("$N.getChildren().add(imagePane)", "mainPane")
		    		  .addStatement("$N.getChildren().add($N)", "mainPane", "attributes")
		    		  .addStatement("$N.getChildren().add(objGraph)", "mainPane")
		    		  .addStatement("$N.setAlignment($T.CENTER)", "mainPane", pos)
		    		  .addStatement("$T scene = new $T(mainPane, 500, 700)", scene, scene)
		    		  .addStatement("this.setScene(scene)")
		    		  .build();
		      
		      MethodSpec handle = MethodSpec.methodBuilder("handle")
		    		  .addAnnotation(Override.class)
		    		  .addModifiers(Modifier.PUBLIC)
		    		  .returns(void.class)
		    		  .addParameter(mouseevent, "e")
		    		  .addStatement("$N.show(mainPane, e.getScreenX(), e.getScreenY())", "actions")
		    		  .build();
		      
		      TypeSpec cis = TypeSpec.classBuilder("ToolInfoScreen")
		    		  .addModifiers(Modifier.PUBLIC)
		    		  .superclass(stage)
		    		  .addSuperinterface(mouseHandler)
		    		  .addField(contextmenu, "actions")
		    		  .addField(vbox, "mainPane")
		    		  .addField(simsegui, "gui")
		    		  .addField(state, "state")
		    		  .addField(tool, "tool")
		    		  .addField(viewOfStrings, "attributes")
		    		  .addMethod(constructor)
		    		  .addMethod(handle)
		    		  .build();
		      
		      JavaFile file = JavaFile.builder("", cis)
						 .build();
		      String toAppend = "package simse.gui;\n"
						+ "import simse.adts.objects.*;\n";
		      
		      writer.write(toAppend + file.toString());
		      
		      writer.close();
		    } catch (IOException e) {
		      JOptionPane.showMessageDialog(null, ("Error writing file "
		          + cisFile.getPath() + ": " + e.toString()), "File IO Error",
		          JOptionPane.WARNING_MESSAGE);
		    }
	  }
	  
	  private String getTypeAsToString(Attribute att) {
	      if (att.getType() == AttributeTypes.INTEGER) {
	        return "Integer.toString";
	      } else if (att.getType() == AttributeTypes.DOUBLE) {
	        return "Double.toString";
	      } else if (att.getType() == AttributeTypes.BOOLEAN) {
	        return "Boolean.toString";
	      } else { //(att.getType() == AttributeTypes.STRING)
	        return "";
	      }
	  }
}
