/*
 * This class is responsible for generating all of the code for the logic's
 * MiscUpdater component, which is responsible for doing various updating tasks
 * like clearing employee menus and overhead texts, and incrementing action
 * times
 */

package simse.codegenerator.logicgenerator;

import simse.codegenerator.CodeGeneratorConstants;
import simse.codegenerator.CodeGeneratorUtils;

import simse.modelbuilder.actionbuilder.ActionType;
import simse.modelbuilder.actionbuilder.ActionTypeDestroyer;
import simse.modelbuilder.actionbuilder.DefinedActionTypes;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.util.Vector;

import javax.lang.model.element.Modifier;
import javax.swing.JOptionPane;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

public class MiscUpdaterGenerator implements CodeGeneratorConstants {
  private File directory; // directory to generate into
  private File muFile; // file to generate
  private DefinedActionTypes actTypes;

  public MiscUpdaterGenerator(File directory, DefinedActionTypes actTypes) {
    this.directory = directory;
    this.actTypes = actTypes;
  }

  public void generate() {
	  ClassName vector = ClassName.get("java.util", "Vector");
	  ClassName changePayRateAction = ClassName.get("simse.adts.actions", "ChangePayRateAction");
	  ClassName fireAction = ClassName.get("simse.adts.actions", "FireAction");
	  ClassName giveBonusAction = ClassName.get("simse.adts.actions", "GiveBonusAction");
	  ClassName introduceNewRequirementsAction = ClassName.get("simse.adts.actions", "IntroduceNewRequirementsAction");
	  ClassName purchaseToolAction = ClassName.get("simse.adts.actions", "PurchaseToolAction");
	  ClassName quitAction = ClassName.get("simse.adts.actions", "QuitAction");
	  ClassName suggestedDesignPhaseDurationAction = ClassName.get("simse.adts.actions", "SuggestedDesignPhaseDurationAction");
	  ClassName suggestedImplIntegrationPhaseDurationAction = ClassName.get("simse.adts.actions", "SuggestedImplIntegrationPhaseDurationAction");
	  ClassName suggestedRequirementsPhaseDurationAction = ClassName.get("simse.adts.actions", "SuggestedRequirementsPhaseDurationAction");
	  ClassName suggestedTestingPhaseDurationAction = ClassName.get("simse.adts.actions", "SuggestedTestingPhaseDurationAction");
	  ClassName employee = ClassName.get("simse.adts.objects", "Employee");
	  ClassName action = ClassName.get("simse.adts.actions", "Action");
	  ClassName state = ClassName.get("simse.state", "State");
	  
	  MethodSpec updaterConstructor = MethodSpec.constructorBuilder()
				.addModifiers(Modifier.PUBLIC)
				.addParameter(state, "s")
				.addStatement("state = s")
				.build();
	  
	  Vector<ActionType> allActions = actTypes.getAllActionTypes();
      // make a vector w/ all action types that have timed destroyers:
      Vector<ActionType> timedActs = new Vector<ActionType>();
      for (int i = 0; i < allActions.size(); i++) {
    	  ActionType act = allActions.elementAt(i);
    	  if (act.hasDestroyerOfType(ActionTypeDestroyer.TIMED)) {
    		  timedActs.add(act);
    	  }
      }
      
      CodeBlock.Builder actsBuilder = CodeBlock.builder();
      // generate code for action types w/ timed destroyers:
      for (int i = 0; i < timedActs.size(); i++) {
    	  ActionType act = timedActs.elementAt(i);
    	  String actTypeName = CodeGeneratorUtils.getUpperCaseLeading(act.getName()) + "Action";
    	  ClassName actName = ClassName.get("simse.adts.actions", actTypeName);
    	  String varName = act.getName().toLowerCase() + "Actions";
    	  actsBuilder.addStatement("$T<$T> " + varName + " = state.getActionStateRepository().get" +
    			  actTypeName + "StateRepository().getAllActions()", vector, actName);
    	  actsBuilder.beginControlFlow("for (int i = 0; i < " + varName + ".size(); i++) {");
    	  actsBuilder.addStatement("$T act = " + varName + ".elementAt(i);", actName);
    	  actsBuilder.addStatement("act.decrementTimeToLive();");
    	  actsBuilder.endControlFlow();
      }
	  
	  
	  MethodSpec update = MethodSpec.methodBuilder("update")
				.addModifiers(Modifier.PUBLIC)
				.returns(void.class)
				.addComment("clear menus and overhead texts")
				.addStatement("$T<$T> employees = state.getEmployeeStateRepository().getAll()", vector, employee)
				.beginControlFlow("for (int i = 0; i < employees.size(); i++) ")
				.addStatement("employees.elementAt(i).clearOverheadText()")
				.addStatement("employees.elementAt(i).clearMenu()")
				.endControlFlow()
				.addCode("$L", "\n")
				.addComment("update actions' time elapsed")
				.addStatement("$T<$T> actions = state.getActionStateRepository().getAllActions()", vector, action)
				.beginControlFlow("for (int i = 0; i < actions.size(); i++) ")
				.addStatement("$T act = actions.elementAt(i)", action)
				.addStatement("act.incrementTimeElapsed()")
				.endControlFlow()
				.addCode("$L", "\n")
				.addComment("decrement time to live for actions w/ timed destroyers:")
				.addCode(actsBuilder.build())
				.addCode("$L", "\n")
				.addComment("update clock")
				.addStatement("state.getClock().incrementTime()")
				.build();
	  
	  TypeSpec updater = TypeSpec.classBuilder("MiscUpdater")
				.addModifiers(Modifier.PUBLIC)
				.addField(state, "state", Modifier.PRIVATE)
				.addMethod(updaterConstructor)
				.addMethod(update)
				.build();
	  
	  JavaFile javaFile = JavaFile.builder("simse.logic", updater)
				.addFileComment("File generated by: simse.codegenerator.logicgenerator.MiscUpdaterGenerator")
				.build();
	  
	  try {
	      muFile = new File(directory, ("simse\\logic\\MiscUpdater.java"));
	      if (muFile.exists()) {
	        muFile.delete(); // delete old version of file
	      }
	      
	      System.out.println(javaFile.toString());
	      javaFile.writeTo(muFile);
    } catch (IOException e) {
      JOptionPane.showMessageDialog(null, ("Error writing file " + muFile.getPath() + ": " + 
    		  e.toString()), "File IO Error", JOptionPane.WARNING_MESSAGE);
    }
  }
}