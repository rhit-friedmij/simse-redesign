/*
 * This class is responsible for generating all of the code for the destroyer
 * checker component in the Logic component of SimSE
 */

package simse.codegenerator.logicgenerator;

import simse.codegenerator.CodeGeneratorConstants;
import simse.codegenerator.CodeGeneratorUtils;

import simse.modelbuilder.objectbuilder.AttributeTypes;
import simse.modelbuilder.objectbuilder.SimSEObjectTypeTypes;
import simse.modelbuilder.actionbuilder.ActionType;
import simse.modelbuilder.actionbuilder.ActionTypeDestroyer;
import simse.modelbuilder.actionbuilder.ActionTypeParticipant;
import simse.modelbuilder.actionbuilder.ActionTypeParticipantAttributeConstraint;
import simse.modelbuilder.actionbuilder.ActionTypeParticipantConstraint;
import simse.modelbuilder.actionbuilder.ActionTypeParticipantDestroyer;
import simse.modelbuilder.actionbuilder.AttributeGuard;
import simse.modelbuilder.actionbuilder.AutonomousActionTypeDestroyer;
import simse.modelbuilder.actionbuilder.DefinedActionTypes;
import simse.modelbuilder.actionbuilder.RandomActionTypeDestroyer;
import simse.modelbuilder.actionbuilder.TimedActionTypeDestroyer;
import simse.modelbuilder.actionbuilder.UserActionTypeDestroyer;
import simse.modelbuilder.rulebuilder.Rule;

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
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

public class DestroyerCheckerGenerator implements CodeGeneratorConstants {
	private File directory; // directory to save generated code into
	private DefinedActionTypes actTypes;
	private FileWriter writer;
	private File destFile;
	private Vector<ActionTypeDestroyer> nonPrioritizedDestroyers;
	private Vector<ActionTypeDestroyer> prioritizedDestroyers;
	private Vector<ActionTypeDestroyer> allDestroyers;

	public DestroyerCheckerGenerator(DefinedActionTypes actTypes, File directory) {
		this.actTypes = actTypes;
		this.directory = directory;
		initializeDestroyerLists();
	}

	public void generate() {
		ClassName random = ClassName.get("java.util", "Random");
		ClassName vector = ClassName.get("java.util", "Vector");
		ClassName stage = ClassName.get("javafx.stage", "Stage");
		ClassName action = ClassName.get("simse.adts.actions", "Action");
		ClassName breakAction = ClassName.get("simse.adts.actions", "BreakAction");
		ClassName changePayRateAction = ClassName.get("simse.adts.actions", "ChangePayRateAction");
		ClassName correctCodeAction = ClassName.get("simse.adts.actions", "CorrectCodeAction");
		ClassName correctDesignAction = ClassName.get("simse.adts.actions", "CorrectDesignAction");
		ClassName correctRequirementsAction = ClassName.get("simse.adts.actions", "CorrectRequirementsAction");
		ClassName correctSystemTestPlanAction = ClassName.get("simse.adts.actions", "CorrectSystemTestPlanAction");
		ClassName createCodeAction = ClassName.get("simse.adts.actions", "CreateCodeAction");
		ClassName createDesignAction = ClassName.get("simse.adts.actions", "CreateDesignAction");
		ClassName createRequirementsAction = ClassName.get("simse.adts.actions", "CreateRequirementsAction");
		ClassName createSystemTestPlanAction = ClassName.get("simse.adts.actions", "CreateSystemTestPlanAction");
		ClassName fireAction = ClassName.get("simse.adts.actions", "FireAction");
		ClassName getSickAction = ClassName.get("simse.adts.actions", "GetSickAction");
		ClassName giveBonusAction = ClassName.get("simse.adts.actions", "GiveBonusAction");
		ClassName inspectCodeAction = ClassName.get("simse.adts.actions", "InspectCodeAction");
		ClassName integrateCodeAction = ClassName.get("simse.adts.actions", "IntegrateCodeAction");
		ClassName introduceNewRequirementsAction = ClassName.get("simse.adts.actions", "IntroduceNewRequirementsAction");
		ClassName purchaseToolAction = ClassName.get("simse.adts.actions", "PurchaseToolAction");
		ClassName quitAction = ClassName.get("simse.adts.actions", "QuitAction");
		ClassName reviewDesignAction = ClassName.get("simse.adts.actions", "ReviewDesignAction");
		ClassName reviewRequirementsAction = ClassName.get("simse.adts.actions", "ReviewRequirementsAction");
		ClassName reviewSystemTestPlanAction = ClassName.get("simse.adts.actions", "ReviewSystemTestPlanAction");
		ClassName suggestedDesignPhaseDurationAction = ClassName.get("simse.adts.actions", "SuggestedDesignPhaseDurationAction");
		ClassName suggestedImplIntegrationPhaseDurationAction = ClassName.get("simse.adts.actions", "SuggestedImplIntegrationPhaseDurationAction");
		ClassName suggestedRequirementsPhaseDurationAction = ClassName.get("simse.adts.actions", "SuggestedRequirementsPhaseDurationAction");
		ClassName suggestedTestingPhaseDurationAction = ClassName.get("simse.adts.actions", "SuggestedTestingPhaseDurationAction");
		ClassName systemTestAction = ClassName.get("simse.adts.actions", "SystemTestAction");
		ClassName artifact = ClassName.get("simse.adts.objects", "Artifact");
		ClassName automatedTestingTool = ClassName.get("simse.adts.objects", "AutomatedTestingTool");
		ClassName code = ClassName.get("simse.adts.objects", "Code");
		ClassName customer = ClassName.get("simse.adts.objects", "Customer");
		ClassName designDocument = ClassName.get("simse.adts.objects", "DesignDocument");
		ClassName designEnvironment = ClassName.get("simse.adts.objects", "DesignEnvironment");
		ClassName employee = ClassName.get("simse.adts.objects", "Employee");
		ClassName iDE = ClassName.get("simse.adts.objects", "IDE");
		ClassName project = ClassName.get("simse.adts.objects", "Project");
		ClassName requirementsCaptureTool = ClassName.get("simse.adts.objects", "RequirementsCaptureTool");
		ClassName requirementsDocument = ClassName.get("simse.adts.objects", "RequirementsDocument");
		ClassName sEProject = ClassName.get("simse.adts.objects", "SEProject");
		ClassName sSObject = ClassName.get("simse.adts.objects", "SSObject");
		ClassName softwareEngineer = ClassName.get("simse.adts.objects", "SoftwareEngineer");
		ClassName systemTestPlan = ClassName.get("simse.adts.objects", "SystemTestPlan");
		ClassName tool = ClassName.get("simse.adts.objects", "Tool");
		ClassName melloPanel = ClassName.get("simse.gui", "MelloPanel");
		ClassName state = ClassName.get("simse.state", "State");
		ClassName menuInput = ClassName.get("simse.logic", "MenuInputManager");
		ClassName trigCheck = ClassName.get("simse.logic", "TriggerChecker");
		ClassName destCheck = ClassName.get("simse.logic", "DestroyerChecker");
		ClassName ruleExecuter = ClassName.get("simse.logic", "RuleExecuter");
		ClassName miscUpdater = ClassName.get("simse.logic", "MiscUpdater");
		TypeName vectorOfActions = ParameterizedTypeName.get(vector, action);

		MethodSpec destroyerConstructor = MethodSpec.constructorBuilder()
				.addModifiers(Modifier.PUBLIC)
				.addParameter(state, "s")
				.addParameter(ruleExecuter, "r")
				.addParameter(trigCheck, "t")
				.addStatement("state = s")
				.addStatement("ruleExec = r")
				.addStatement("trigCheck = t")
				.addStatement("ranNumGen = new $T()", random)
				.addStatement("mello = $T.getInstance()", melloPanel)
				.build();
		
		 MethodSpec update = MethodSpec.methodBuilder("update")
					.addModifiers(Modifier.PUBLIC)
					.returns(void.class)
					.addParameter(boolean.class, "updateUserDestsOlnly")
					.addParameter(stage, "gui")
					.addStatement("$T actions = state.getActionStateRepository().getAllActions()", vectorOfActions)
					.beginControlFlow("for (int i = 0 i < actions.size() i++) {")
					.addStatement("$T tempAct = actions.elementAt(i)", action)
					.addCode(getDestroyerConditions().build())
					.endControlFlow()
					.addComment("update trigger checker:")
					.addStatement("trigCheck.update(true, gui)")
					.build();
		 
		 TypeSpec destroyer = TypeSpec.classBuilder("DestroyerChecker")
					.addModifiers(Modifier.PUBLIC)
					.addField(state, "state", Modifier.PRIVATE)
					.addMethod(destroyerConstructor)
					.addMethod(update)
					.build();
		  
		  JavaFile javaFile = JavaFile.builder("simse.logic", destroyer)
					.addFileComment("File generated by: simse.codegenerator.logicgenerator.DestroyerCheckerGenerator")
					.build();

		try {
			destFile = new File(directory, ("simse\\logic\\DestroyerChecker.java"));
			if (destFile.exists()) {
				destFile.delete(); // delete old version of file
			}
			
			System.out.println(javaFile.toString());
		    javaFile.writeTo(destFile);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, ("Error writing file " + destFile.getPath() + ": " + e.toString()),
					"File IO Error", JOptionPane.WARNING_MESSAGE);
		}
	}
	
	private CodeBlock.Builder getDestroyerConditions() {
		CodeBlock.Builder destroyerConditions = CodeBlock.builder();
		// go through each destroyer:
		for (int i = 0; i < allDestroyers.size(); i++) {
			ActionTypeDestroyer tempDest = allDestroyers.elementAt(i);
			ActionType tempAct = tempDest.getActionType();
			String actTypeName = CodeGeneratorUtils.getUpperCaseLeading(tempAct.getName()) + "Action";
	    	ClassName actName = ClassName.get("simse.adts.actions", actTypeName);
			/*
			 * if(i > 0) // not on first element { writer.write("else "); }
			 */
			writer.write("if((tempAct instanceof " + CodeGeneratorUtils.getUpperCaseLeading(tempAct.getName())
					+ "Action) && (state.getActionStateRepository().get"
					+ CodeGeneratorUtils.getUpperCaseLeading(tempAct.getName())
					+ "ActionStateRepository().getAllActions().contains(tempAct)))");
			writer.write(NEWLINE);
			writer.write(OPEN_BRACK);
			writer.write(NEWLINE);
			writer.write(CodeGeneratorUtils.getUpperCaseLeading(tempAct.getName()) + "Action "
					+ tempAct.getName().toLowerCase() + "TempAct = ("
					+ CodeGeneratorUtils.getUpperCaseLeading(tempAct.getName()) + "Action)tempAct;");
			writer.write(NEWLINE);

			if (tempDest instanceof TimedActionTypeDestroyer) { // timed destroyer
				writer.write("if(!updateUserDestsOnly)");
				writer.write(NEWLINE);
				writer.write(OPEN_BRACK);
				writer.write(NEWLINE);
				writer.write("if(" + tempAct.getName().toLowerCase() + "TempAct" + ".getTimeToLive() == 0)");
				writer.write(NEWLINE);
				writer.write(OPEN_BRACK);
				writer.write(NEWLINE);
				writer.write("Vector<SSObject> b = tempAct.getAllParticipants();");
				writer.write(NEWLINE);
				writer.write("for(int j=0; j<b.size(); j++)");
				writer.write(NEWLINE);
				writer.write(OPEN_BRACK);
				writer.write(NEWLINE);
				writer.write("SSObject c = b.elementAt(j);");
				writer.write(NEWLINE);
				writer.write("if(c instanceof Employee)");
				writer.write(NEWLINE);
				writer.write(OPEN_BRACK);
				writer.write(NEWLINE);
				if ((tempDest.getDestroyerText() != null) && (tempDest.getDestroyerText().length() > 0)) {
					writer.write("((Employee)c).setOverheadText(\"" + tempDest.getDestroyerText() + "\");");
					writer.write(NEWLINE);
				}
				writer.write(CLOSED_BRACK);
				writer.write(NEWLINE);
				writer.write("else if(c instanceof Customer)");
				writer.write(NEWLINE);
				writer.write(OPEN_BRACK);
				writer.write(NEWLINE);
				if ((tempDest.getDestroyerText() != null) && (tempDest.getDestroyerText().length() > 0)) {
					writer.write("((Customer)c).setOverheadText(\"" + tempDest.getDestroyerText() + "\");");
				}
				writer.write(NEWLINE);
				writer.write(CLOSED_BRACK);
				writer.write(NEWLINE);
				writer.write(CLOSED_BRACK);
				writer.write(NEWLINE);

				// execute all destroyer rules:
				Vector<Rule> destRules = tempAct.getAllDestroyerRules();
				for (int k = 0; k < destRules.size(); k++) {
					Rule dRule = destRules.elementAt(k);
					writer.write(
							"ruleExec.update(gui, RuleExecutor.UPDATE_ONE, \"" + dRule.getName() + "\", tempAct);");
					writer.write(NEWLINE);
				}
				writer.write("state.getActionStateRepository().get"
						+ CodeGeneratorUtils.getUpperCaseLeading(tempAct.getName())
						+ "ActionStateRepository().remove(" + tempAct.getName().toLowerCase() + "TempAct);");
				writer.write(NEWLINE);
				writer.write("trigCheck.update(true, gui);");
				writer.write(NEWLINE);
				writer.write("update(false, gui);");
				writer.write(NEWLINE);

				// game-ending:
				if (tempDest.isGameEndingDestroyer()) {
					writer.write("// stop game and give score:");
					writer.write(NEWLINE);
					writer.write(CodeGeneratorUtils.getUpperCaseLeading(tempAct.getName()) + "Action t111 = ("
							+ CodeGeneratorUtils.getUpperCaseLeading(tempAct.getName()) + "Action)tempAct;");
					writer.write(NEWLINE);
					// find the scoring attribute:
					ActionTypeParticipantDestroyer scoringPartDest = null;
					ActionTypeParticipantConstraint scoringPartConst = null;
					ActionTypeParticipantAttributeConstraint scoringAttConst = null;
					Vector<ActionTypeParticipantDestroyer> partDests = tempDest.getAllParticipantDestroyers();
					for (int j = 0; j < partDests.size(); j++) {
						ActionTypeParticipantDestroyer partDest = partDests.elementAt(j);
						Vector<ActionTypeParticipantConstraint> partConsts = partDest.getAllConstraints();
						for (int k = 0; k < partConsts.size(); k++) {
							ActionTypeParticipantConstraint partConst = partConsts.elementAt(k);
							ActionTypeParticipantAttributeConstraint[] attConsts = partConst
									.getAllAttributeConstraints();
							for (int m = 0; m < attConsts.length; m++) {
								if (attConsts[m].isScoringAttribute()) {
									scoringAttConst = attConsts[m];
									scoringPartConst = partConst;
									scoringPartDest = partDest;
									break;
								}
							}
						}
					}
					if ((scoringAttConst != null) && (scoringPartConst != null) && (scoringPartDest != null)) {
						writer.write(
								"if(t111.getAll" + scoringPartDest.getParticipant().getName() + "s().size() > 0)");
						writer.write(NEWLINE);
						writer.write(OPEN_BRACK);
						writer.write(NEWLINE);
						writer.write(CodeGeneratorUtils
								.getUpperCaseLeading(scoringPartConst.getSimSEObjectType().getName())
								+ " t = ("
								+ CodeGeneratorUtils.getUpperCaseLeading(
										scoringPartConst.getSimSEObjectType().getName())
								+ ")(t111.getAll" + scoringPartDest.getParticipant().getName()
								+ "s().elementAt(0));");
						writer.write(NEWLINE);
						if (scoringAttConst.getAttribute().getType() == AttributeTypes.INTEGER) {
							writer.write("int");
						} else if (scoringAttConst.getAttribute().getType() == AttributeTypes.DOUBLE) {
							writer.write("double");
						} else if (scoringAttConst.getAttribute().getType() == AttributeTypes.STRING) {
							writer.write("String");
						} else if (scoringAttConst.getAttribute().getType() == AttributeTypes.BOOLEAN) {
							writer.write("boolean");
						}
						writer.write(" v = t.get" + scoringAttConst.getAttribute().getName() + "();");
						writer.write(NEWLINE);
						writer.write("state.getClock().stop();");
						writer.write(NEWLINE);
						writer.write("state.setScore(v);");
						writer.write(NEWLINE);
						writer.write("((SimSEGUI)gui).update();");
						writer.write(NEWLINE);
						writer.write(
								"JOptionPane.showMessageDialog(null, (\"Your score is \" + v), \"Game over!\", JOptionPane.INFORMATION_MESSAGE);");
						writer.write(NEWLINE);
						writer.write(CLOSED_BRACK);
						writer.write(NEWLINE);
					}
				}

				writer.write(CLOSED_BRACK);
				writer.write(NEWLINE);
//		          writer.write("else");
//		          writer.write(NEWLINE);
//		          writer.write(OPEN_BRACK);
//		          writer.write(NEWLINE);
//		          writer.write("((" + getUpperCaseLeading(tempAct.getName())
//		              + "Action)tempAct).decrementTimeToLive();");
//		          writer.write(NEWLINE);
//		          writer.write(CLOSED_BRACK);
//		          writer.write(NEWLINE);
				writer.write(CLOSED_BRACK);
				writer.write(NEWLINE);
				writer.write(CLOSED_BRACK);
				writer.write(NEWLINE);
			} else { // random, user, or autonomous destroyer
				if ((tempDest instanceof RandomActionTypeDestroyer)
						|| (tempDest instanceof AutonomousActionTypeDestroyer)) {
					writer.write("if(!updateUserDestsOnly)");
					writer.write(NEWLINE);
					writer.write(OPEN_BRACK);
					writer.write(NEWLINE);
				}
				writer.write("boolean destroy = true;");
				writer.write(NEWLINE);

				Vector<ActionTypeParticipantDestroyer> destroyers = tempDest.getAllParticipantDestroyers();
				// go through each participant destroyer:
				for (int j = 0; j < destroyers.size(); j++) {
					ActionTypeParticipantDestroyer dest = destroyers.elementAt(j);
					ActionTypeParticipant part = dest.getParticipant();

					writer.write("Vector<" + SimSEObjectTypeTypes.getText(part.getSimSEObjectTypeType()) + "> "
							+ part.getName().toLowerCase() + "s = " + tempAct.getName().toLowerCase()
							+ "TempAct.getAll" + part.getName() + "s();");
					writer.write(NEWLINE);
					writer.write("for(int j=0; j<" + part.getName().toLowerCase() + "s.size(); j++)");
					writer.write(NEWLINE);
					writer.write(OPEN_BRACK);
					writer.write(NEWLINE);
					writer.write(SimSEObjectTypeTypes.getText(part.getSimSEObjectTypeType()) + " a = "
							+ part.getName().toLowerCase() + "s.elementAt(j);");
					writer.write(NEWLINE);
					// go through all participant constraints:
					Vector<ActionTypeParticipantConstraint> constraints = dest.getAllConstraints();
					for (int k = 0; k < constraints.size(); k++) {
						ActionTypeParticipantConstraint constraint = constraints.elementAt(k);
						String objTypeName = constraint.getSimSEObjectType().getName();
						if (k > 0) { // not on first element
							writer.write("else ");
						}
						writer.write(
								"if(a instanceof " + CodeGeneratorUtils.getUpperCaseLeading(objTypeName) + ")");
						writer.write(NEWLINE);
						writer.write(OPEN_BRACK);
						writer.write(NEWLINE);
						// go through all attribute constraints:
						ActionTypeParticipantAttributeConstraint[] attConstraints = constraint
								.getAllAttributeConstraints();
						int numAttConsts = 0;
						for (int m = 0; m < attConstraints.length; m++) {
							ActionTypeParticipantAttributeConstraint tempAttConst = attConstraints[m];
							if (tempAttConst.isConstrained()) {
								if (numAttConsts == 0) { // this is the first attribute that
															// we've come across that's
															// constrained
									writer.write("if(");
								} else {
									writer.write(" || ");
								}
								writer.write("(!(((" + CodeGeneratorUtils.getUpperCaseLeading(objTypeName)
										+ ")a).get" + tempAttConst.getAttribute().getName() + "()");
								if (tempAttConst.getAttribute().getType() == AttributeTypes.STRING) {
									writer.write(".equals(" + "\"" + tempAttConst.getValue().toString() + "\")");
								} else {
									if (tempAttConst.getGuard().equals(AttributeGuard.EQUALS)) {
										writer.write(" == ");
									} else {
										writer.write(" " + tempAttConst.getGuard() + " ");
									}
									writer.write(tempAttConst.getValue().toString());
								}
								writer.write("))");
								numAttConsts++;
							}
						}
						if (numAttConsts > 0) { // there is at least one constraint
							writer.write(")");
							writer.write(NEWLINE);
							writer.write(OPEN_BRACK);
							writer.write(NEWLINE);
							writer.write("destroy = false;");
							writer.write(NEWLINE);
							writer.write("break;");
							writer.write(NEWLINE);
							writer.write(CLOSED_BRACK);
							writer.write(NEWLINE);
						}
						writer.write(CLOSED_BRACK);
						writer.write(NEWLINE);
					}
					writer.write(CLOSED_BRACK);
					writer.write(NEWLINE);
				}
				writer.write("if(");
				if (tempDest instanceof RandomActionTypeDestroyer) {
					writer.write("(destroy) && ((ranNumGen.nextDouble() * 100.0) < "
							+ ((RandomActionTypeDestroyer) (tempDest)).getFrequency() + "))");
				} else { // user, action or autonomous
					writer.write("destroy)");
				}
				writer.write(NEWLINE);
				writer.write(OPEN_BRACK);
				writer.write(NEWLINE);
				writer.write("Vector<SSObject> b = tempAct.getAllParticipants();");
				writer.write(NEWLINE);
				writer.write("for(int j=0; j<b.size(); j++)");
				writer.write(NEWLINE);
				writer.write(OPEN_BRACK);
				writer.write(NEWLINE);
				writer.write("SSObject c = b.elementAt(j);");
				writer.write(NEWLINE);
				writer.write("if(c instanceof Employee)");
				writer.write(NEWLINE);
				writer.write(OPEN_BRACK);
				writer.write(NEWLINE);
				if ((tempDest instanceof AutonomousActionTypeDestroyer)
						|| (tempDest instanceof RandomActionTypeDestroyer)) {
					if ((tempDest.getDestroyerText() != null) && (tempDest.getDestroyerText().length() > 0)) {
						writer.write("((Employee)c).setOverheadText(\"" + tempDest.getDestroyerText() + "\");");
					}
					writer.write(NEWLINE);

					// For each user destroyer for this action, remove the menu item:
					for (int k = 0; k < allDestroyers.size(); k++) {
						ActionTypeDestroyer tempDest2 = allDestroyers.elementAt(k);
						if ((tempDest2 instanceof UserActionTypeDestroyer) && (tempDest2 != tempDest) && (tempDest2
								.getActionType().getName().equals(tempDest.getActionType().getName()))) {
							writer.write("((Employee) c).removeMenuItem(\""
									+ ((UserActionTypeDestroyer) tempDest2).getMenuText() + "\");");
							writer.write(NEWLINE);
						}
					}

					writer.write(CLOSED_BRACK);
					writer.write(NEWLINE);
					writer.write("else if(c instanceof Customer)");
					writer.write(NEWLINE);
					writer.write(OPEN_BRACK);
					writer.write(NEWLINE);
					if ((tempDest.getDestroyerText() != null) && (tempDest.getDestroyerText().length() > 0)) {
						writer.write("((Customer)c).setOverheadText(\"" + tempDest.getDestroyerText() + "\");");
					}
					writer.write(NEWLINE);
					writer.write(CLOSED_BRACK);
					writer.write(NEWLINE);
					writer.write(CLOSED_BRACK);
					writer.write(NEWLINE);

					// execute all destroyer rules:
					Vector<Rule> destRules = tempAct.getAllDestroyerRules();
					for (int k = 0; k < destRules.size(); k++) {
						Rule dRule = destRules.elementAt(k);
						writer.write("ruleExec.update(gui, RuleExecutor.UPDATE_ONE, \"" + dRule.getName()
								+ "\", tempAct);");
						writer.write(NEWLINE);
					}
					writer.write("state.getActionStateRepository().get"
							+ CodeGeneratorUtils.getUpperCaseLeading(tempAct.getName())
							+ "ActionStateRepository().remove(" + tempAct.getName().toLowerCase() + "TempAct);");
					writer.write(NEWLINE);
					writer.write("trigCheck.update(true, gui);");
					writer.write(NEWLINE);
					writer.write("update(false, gui);");
					writer.write(NEWLINE);

					// game-ending:
					if (tempDest.isGameEndingDestroyer()) {
						writer.write("// stop game and give score:");
						writer.write(NEWLINE);
						writer.write(CodeGeneratorUtils.getUpperCaseLeading(tempAct.getName()) + "Action t111 = ("
								+ CodeGeneratorUtils.getUpperCaseLeading(tempAct.getName()) + "Action)tempAct;");
						writer.write(NEWLINE);
						// find the scoring attribute:
						ActionTypeParticipantDestroyer scoringPartDest = null;
						ActionTypeParticipantConstraint scoringPartConst = null;
						ActionTypeParticipantAttributeConstraint scoringAttConst = null;
						Vector<ActionTypeParticipantDestroyer> partDests = tempDest.getAllParticipantDestroyers();
						for (int j = 0; j < partDests.size(); j++) {
							ActionTypeParticipantDestroyer partDest = partDests.elementAt(j);
							Vector<ActionTypeParticipantConstraint> partConsts = partDest.getAllConstraints();
							for (int k = 0; k < partConsts.size(); k++) {
								ActionTypeParticipantConstraint partConst = partConsts.elementAt(k);
								ActionTypeParticipantAttributeConstraint[] attConsts = partConst
										.getAllAttributeConstraints();
								for (int m = 0; m < attConsts.length; m++) {
									if (attConsts[m].isScoringAttribute()) {
										scoringAttConst = attConsts[m];
										scoringPartConst = partConst;
										scoringPartDest = partDest;
										break;
									}
								}
							}
						}
						if ((scoringAttConst != null) && (scoringPartConst != null) && (scoringPartDest != null)) {
							writer.write("if(t111.getAll" + scoringPartDest.getParticipant().getName()
									+ "s().size() > 0)");
							writer.write(NEWLINE);
							writer.write(OPEN_BRACK);
							writer.write(NEWLINE);
							writer.write(CodeGeneratorUtils
									.getUpperCaseLeading(scoringPartConst.getSimSEObjectType().getName())
									+ " t = ("
									+ CodeGeneratorUtils.getUpperCaseLeading(
											scoringPartConst.getSimSEObjectType().getName())
									+ ")(t111.getAll" + scoringPartDest.getParticipant().getName()
									+ "s().elementAt(0));");
							writer.write(NEWLINE);
							writer.write("if(t != null)");
							writer.write(NEWLINE);
							writer.write(OPEN_BRACK);
							writer.write(NEWLINE);
							if (scoringAttConst.getAttribute().getType() == AttributeTypes.INTEGER) {
								writer.write("int");
							} else if (scoringAttConst.getAttribute().getType() == AttributeTypes.DOUBLE) {
								writer.write("double");
							} else if (scoringAttConst.getAttribute().getType() == AttributeTypes.STRING) {
								writer.write("String");
							} else if (scoringAttConst.getAttribute().getType() == AttributeTypes.BOOLEAN) {
								writer.write("boolean");
							}
							writer.write(" v = t.get" + scoringAttConst.getAttribute().getName() + "();");
							writer.write(NEWLINE);
							writer.write("state.getClock().stop();");
							writer.write(NEWLINE);
							writer.write("state.setScore(v);");
							writer.write(NEWLINE);
							writer.write("((SimSEGUI)gui).update();");
							writer.write(NEWLINE);
							writer.write(
									"JOptionPane.showMessageDialog(null, (\"Your score is \" + v), \"Game over!\", JOptionPane.INFORMATION_MESSAGE);");
							writer.write(NEWLINE);
							writer.write(CLOSED_BRACK);
							writer.write(NEWLINE);
							writer.write(CLOSED_BRACK);
							writer.write(NEWLINE);
						}
					}

					writer.write(CLOSED_BRACK);
					writer.write(NEWLINE);
					writer.write(CLOSED_BRACK);
					writer.write(NEWLINE);
				} else { // user destroyer
					writer.write("((Employee)c).addMenuItem(\""
							+ ((UserActionTypeDestroyer) (tempDest)).getMenuText() + "\");");
					writer.write(NEWLINE);
					writer.write(CLOSED_BRACK);
					writer.write(NEWLINE);
					writer.write(CLOSED_BRACK);
					writer.write(NEWLINE);
					writer.write(CLOSED_BRACK);
					writer.write(NEWLINE);
				}
				writer.write(CLOSED_BRACK);
				writer.write(NEWLINE);
			}
		}
		return destroyerConditions;
	}

	/*
	 * gets the destroyers in prioritized order according to their priority
	 */
	private void initializeDestroyerLists() {
		// initialize lists:
		nonPrioritizedDestroyers = new Vector<ActionTypeDestroyer>();
		prioritizedDestroyers = new Vector<ActionTypeDestroyer>();
		Vector<ActionType> allActions = actTypes.getAllActionTypes();
		// go through all action types and get their destroyers:
		for (int i = 0; i < allActions.size(); i++) {
			ActionType tempAct = allActions.elementAt(i);
			Vector<ActionTypeDestroyer> dests = tempAct.getAllDestroyers();
			for (int j = 0; j < dests.size(); j++) {
				ActionTypeDestroyer tempDest = dests.elementAt(j);
				int priority = tempDest.getPriority();
				if (priority == -1) { // destroyer is not prioritized
					nonPrioritizedDestroyers.addElement(tempDest);
				} else { // priority >= 0
					if (prioritizedDestroyers.size() == 0) { // no elements have been
																// added yet to the
																// prioritized destroyer list
						prioritizedDestroyers.add(tempDest);
					} else {
						// find the correct position to insert the destroyer at:
						for (int k = 0; k < prioritizedDestroyers.size(); k++) {
							ActionTypeDestroyer tempA = prioritizedDestroyers.elementAt(k);
							if (priority <= tempA.getPriority()) {
								prioritizedDestroyers.insertElementAt(tempDest, k); // insert
																					// the
																					// destroyer
								break;
							} else if (k == (prioritizedDestroyers.size() - 1)) { // on the
																					// last
																					// element
								prioritizedDestroyers.add(tempDest); // add the destroyer to the
																		// end of the list
								break;
							}
						}
					}
				}
			}
		}
		// make it all into one:
		allDestroyers = new Vector<ActionTypeDestroyer>();
		for (int i = 0; i < prioritizedDestroyers.size(); i++) {
			allDestroyers.add(prioritizedDestroyers.elementAt(i));
		}
		for (int i = 0; i < nonPrioritizedDestroyers.size(); i++) {
			allDestroyers.add(nonPrioritizedDestroyers.elementAt(i));
		}
	}
}