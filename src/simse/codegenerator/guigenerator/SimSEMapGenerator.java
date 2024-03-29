/*
 * This class is responsible for generating all of the code for the SimSEMap
 * class in the GUI
 */

package simse.codegenerator.guigenerator;

import simse.codegenerator.CodeGeneratorConstants;
import simse.codegenerator.CodeGeneratorUtils;

import simse.modelbuilder.mapeditor.MapData;
import simse.modelbuilder.mapeditor.TileData;
import simse.modelbuilder.mapeditor.UserData;
import simse.modelbuilder.objectbuilder.AttributeTypes;
import simse.modelbuilder.objectbuilder.DefinedObjectTypes;
import simse.modelbuilder.objectbuilder.SimSEObjectType;
import simse.modelbuilder.objectbuilder.SimSEObjectTypeTypes;
import simse.modelbuilder.startstatebuilder.InstantiatedAttribute;
import simse.modelbuilder.startstatebuilder.SimSEObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.lang.model.element.Modifier;
import javax.swing.JOptionPane;

import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

public class SimSEMapGenerator implements CodeGeneratorConstants {
  private File directory; // directory to save generated code into
  private DefinedObjectTypes objTypes; // holds all of the defined object types
                                       // from an sso file
  private Hashtable<SimSEObject, String> objsToImages; // maps SimSEObjects
																												// (keys) to pathname
																												// (String) of image
																												// file (values)
  private Hashtable<SimSEObject, Vector<Integer>> objsToXYLocs; // maps
  
  private ArrayList<String> impTypes;
																																// SimSEObjects
																																// (keys) to
																																// XYLocations
																																// (Vectors) of
																																// employees
																																// (values)
  private ArrayList<UserData> userDatas; // array list of UserDatas

  public SimSEMapGenerator(DefinedObjectTypes objTypes, Hashtable<SimSEObject, 
  		String> objsToImages, ArrayList<UserData> userDatas, 
  		File directory) {
    this.objTypes = objTypes;
    this.objsToImages = objsToImages;
    this.userDatas = userDatas;
    objsToXYLocs = new Hashtable<SimSEObject, Vector<Integer>>();
    this.directory = directory;
    this.impTypes = new ArrayList<>();
  }

  public void generate() {
    // generate file:
    File ssmFile = new File(directory, ("simse\\gui\\SimSEMap.java"));
    if (ssmFile.exists()) {
      ssmFile.delete(); // delete old version of file
    }
    try {
    	ClassName arraylist = ClassName.get("java.util", "ArrayList");    
    	ClassName image = ClassName.get("javafx.scene.image", "Image");
    	ClassName pane = ClassName.get("javafx.scene.layout", "Pane");
    	ClassName employee = ClassName.get("simse.adts.objects", "Employee");
    	ClassName logic = ClassName.get("simse.logic", "Logic");
    	ClassName state = ClassName.get("simse.state", "State");
    	ClassName displayedemployee = ClassName.get("simse.gui", "DisplayedEmployee");
    	
      FileWriter writer = new FileWriter(ssmFile);      
      // constructor:
      String usrData = "";
      usrData = usrData.concat("for(int i=0; i<sopUsers.size(); i++)\n{\n");
      usrData = usrData.concat("DisplayedEmployee user = sopUsers.get(i);\n");
      usrData = usrData.concat("user.setXYLocations(getXYCoordinates(user.getEmployee())[0], getXYCoordinates(user.getEmployee())[1]);\n");
      usrData = usrData.concat("String url = getImage(user.getEmployee());\n");
      usrData = usrData.concat("if (url != null) {\n");
      usrData = usrData.concat("user.setUserIcon(url);\n");

      // go through all user datas:
      for (int i = 0; i < userDatas.size(); i++) {
        UserData tmpUser = userDatas.get(i);
        if (i > 0) {
        	usrData = usrData.concat("else ");
        }
        usrData = usrData.concat("if((user.getEmployee() instanceof " + 
        		CodeGeneratorUtils.getUpperCaseLeading(
        				tmpUser.getSimSEObject().getSimSEObjectType().getName()) + 
        				") && (((" + 
        				CodeGeneratorUtils.getUpperCaseLeading(
        						tmpUser.getSimSEObject().getSimSEObjectType().getName()) + 
        						")user.getEmployee()).get");
        SimSEObjectType objType = tmpUser.getSimSEObject().getSimSEObjectType();
        usrData = usrData.concat(CodeGeneratorUtils.getUpperCaseLeading(
        		objType.getKey().getName()) + "()");

        if ((tmpUser.getSimSEObject().getKey() != null)
            && (tmpUser.getSimSEObject().getKey().isInstantiated())) { // key
																																			 // is
																																			 // instantiated
          Object keyAttVal = tmpUser.getSimSEObject().getKey().getValue();
          if (objType.getKey().getType() == AttributeTypes.STRING) { // string
                                                                   	 // attribute
        	  usrData = usrData.concat(".equals(\"" + keyAttVal.toString() + "\")))");
          } else { // non-string attribute
        	  usrData = usrData.concat(" == " + keyAttVal.toString() + "))");
          }

          // x y locations:
          Vector<Integer> xys = new Vector<Integer>();
          xys.add(new Integer(tmpUser.getXLocation()));
          xys.add(new Integer(tmpUser.getYLocation()));
          objsToXYLocs.put(tmpUser.getSimSEObject(), xys);

          usrData = usrData.concat("\n{\n");
          usrData = usrData.concat("user.setDisplayed(" + tmpUser.isDisplayed() + ");\n");
          usrData = usrData.concat("user.setActivated(" + tmpUser.isActivated() + ");\n}\n");
        } else {
          JOptionPane.showMessageDialog(null, "Generator exception: " + 
          		objType.getName() + " " + 
          		(SimSEObjectTypeTypes.getText(
          				tmpUser.getSimSEObject().getSimSEObjectType().getType())) + 
          				" object has no key attribute value.");
        }
      }
      usrData = usrData.concat("}\n}\n\n");
      
      MethodSpec constructor = MethodSpec.constructorBuilder()
    		  .addModifiers(Modifier.PUBLIC)
    		  .addParameter(state, "s")
    		  .addParameter(logic, "l")
    		  .addStatement("$N = s", "state")
    		  .addStatement("$N = l", "logic")
    		  .addStatement("$N = new ArrayList<DisplayedEmployee>()", "sopUsers")
    		  .addStatement("// get all of the employees from the state:")
    		  .addStatement("Vector<Employee> allEmps = $N.getEmployeeStateRepository().getAll()", "state")
    		  .beginControlFlow("for(int i=0; i<allEmps.size(); i++)")
    		  .addStatement("$T tempEmp = allEmps.elementAt(i)", employee)
    		  .addStatement("DisplayedEmployee tmpUser = new DisplayedEmployee(tempEmp, null, false, false, -1, -1)")
    		  .addStatement("$N.add(tmpUser)", "sopUsers")
    		  .endControlFlow()
    		  .addCode(usrData)
    		  .build();
      
      String gi = "";
      // go through all object types:
      Vector<SimSEObjectType> types = objTypes.getAllObjectTypes();
      // Make a vector of only the employee types:
      Vector<SimSEObjectType> empTypes = new Vector<SimSEObjectType>();
      for (int i = 0; i < types.size(); i++) {
        SimSEObjectType temp = types.elementAt(i);
        if (temp.getType() == SimSEObjectTypeTypes.EMPLOYEE) {
          empTypes.add(temp);
        }
      }
      // go through all employee types:
      for (int i = 0; i < empTypes.size(); i++) {
        SimSEObjectType tempType = empTypes.elementAt(i);
        if (!this.impTypes.contains(CodeGeneratorUtils.getUpperCaseLeading(tempType.getName()))) {
        	this.impTypes.add(CodeGeneratorUtils.getUpperCaseLeading(tempType.getName()));
        }
        if (i > 0) { // not on first element
        	gi = gi.concat("else ");
        }
        gi = gi.concat("if(e instanceof "
            + CodeGeneratorUtils.getUpperCaseLeading(tempType.getName()) + ")\n{\n");
        gi = gi.concat(CodeGeneratorUtils.getUpperCaseLeading(tempType.getName()) 
        		+ " p = (" + 
        		CodeGeneratorUtils.getUpperCaseLeading(tempType.getName()) + ")e;\n");

        /*
         * go through all of the Employee created objects (and objects created
         * by create objects rules):
         */
        Enumeration<SimSEObject> createdObjects = objsToImages.keys();
        boolean putElse = false;
        for (int k = 0; k < objsToImages.size(); k++) {
          SimSEObject obj = createdObjects.nextElement();
          if (obj.getSimSEObjectType().getName().equals(tempType.getName())) {
            boolean allAttValuesInit = true; // whether or not all this object's
                                             // attribute values are initialized
            Vector<InstantiatedAttribute> atts = obj.getAllAttributes();
            if (atts.size() < obj.getSimSEObjectType().getAllAttributes()
                .size()) { // not all atts instantiated
              allAttValuesInit = false;
            } else {
              for (int m = 0; m < atts.size(); m++) {
                InstantiatedAttribute att = atts.elementAt(m);
                if (att.isInstantiated() == false) { // not instantiated
                  allAttValuesInit = false;
                  break;
                }
              }
            }
            if (allAttValuesInit) {
              if (putElse) {
            	  gi = gi.concat("else ");
              } else {
                putElse = true;
              }
              gi = gi.concat("if(p.get" + 
              		CodeGeneratorUtils.getUpperCaseLeading(
              				obj.getKey().getAttribute().getName()) + 
              				"()");
              if (obj.getKey().getAttribute().getType() == 
              	AttributeTypes.STRING) { // string att
            	  gi = gi.concat(".equals(\"" + obj.getKey().getValue().toString() +
                		"\"))");
              } else { // integer, double, or boolean att
            	  gi = gi.concat(" == " + obj.getKey().getValue().toString() + ")");
              }
              gi = gi.concat("\n{\n");
              if (((objsToImages.get(obj)) != null)
                  && (((String) objsToImages.get(obj)).length() > 0)) {
                String imagePath = (iconsDirectory + ((String) objsToImages
                    .get(obj)));
                gi = gi.concat("return \"src" + imagePath + "\";\n");
              }
              gi = gi.concat("}\n");
            }
          }
        }
        gi = gi.concat("}\n");
      }

      // getImage function:
      MethodSpec getImage = MethodSpec.methodBuilder("getImage")
    		  .addModifiers(Modifier.PROTECTED)
    		  .returns(String.class)
    		  .addParameter(employee, "e")
    		  .addCode(gi)
    		  .addStatement("return null")
    		  .build();

      

      // getXYCoordinates function:
   // go through all employee types:
      String xys="";
      for (int i = 0; i < empTypes.size(); i++) {
        SimSEObjectType tempType = empTypes.elementAt(i);
        if (!this.impTypes.contains(CodeGeneratorUtils.getUpperCaseLeading(tempType.getName()))) {
        	this.impTypes.add(CodeGeneratorUtils.getUpperCaseLeading(tempType.getName()));
        }
        if (i > 0) { // not on first element
        	xys=xys.concat("else ");
        }
        xys=xys.concat("if(emp instanceof "
            + CodeGeneratorUtils.getUpperCaseLeading(tempType.getName()) + ")\n{\n");
        xys=xys.concat(CodeGeneratorUtils.getUpperCaseLeading(tempType.getName()) 
        		+ " p = (" + CodeGeneratorUtils.getUpperCaseLeading(
        				tempType.getName()) + ")emp;\n{\n");

        // go through all of the Employees:
        Enumeration<SimSEObject> employees = objsToXYLocs.keys();
        boolean putElse = false;
        for (int k = 0; k < objsToXYLocs.size(); k++) {
          SimSEObject obj = employees.nextElement();
          if (obj.getSimSEObjectType().getName().equals(tempType.getName())) { // matching
                                                                             	 // type
            if (putElse) {
            	xys=xys.concat("else ");
            } else {
              putElse = true;
            }
            xys=xys.concat("if(p.get" + 
            		CodeGeneratorUtils.getUpperCaseLeading(
            				obj.getKey().getAttribute().getName()) + "()");
            if (obj.getKey().isInstantiated()) {
              if (obj.getKey().getAttribute().getType() == 
              	AttributeTypes.STRING) { // string att
            	  xys=xys.concat(".equals(\"" + obj.getKey().getValue().toString()
                    + "\"))");
              } else { // integer, double, or boolean att
            	  xys=xys.concat(" == " + obj.getKey().getValue().toString() + ")");
              }
            }
            xys=xys.concat("\n{\n");
            if ((objsToXYLocs.get(obj) != null)
                && (objsToXYLocs.get(obj).size() >= 2)) {
              int x = objsToXYLocs.get(obj).elementAt(0).intValue();
              int y = objsToXYLocs.get(obj).elementAt(1).intValue();
              xys=xys.concat("xys[0] = " + x + ";\n");
              xys=xys.concat("xys[1] = " + y + ";\n");
            }
            xys=xys.concat("}\n");
          }
        }
        xys=xys.concat("}\n}\n");
      }
      
      MethodSpec getXYCoordinates = MethodSpec.methodBuilder("getXYCoordinates")
    		  .addModifiers(Modifier.PROTECTED)
    		  .returns(int[].class)
    		  .addParameter(employee, "emp")
    		  .addStatement("int[] xys = {-1, -1}")
    		  .addCode(xys)
    		  .addStatement("return xys")
    		  .build();

      // getSopUsers function
      MethodSpec getSopUsers = MethodSpec.methodBuilder("getSopUsers")
    		  .addModifiers(Modifier.PUBLIC)
    		  .returns(ArrayList.class)
    		  .addStatement("return $N", "sopUsers")
    		  .build();

      // TileData class:      
      MethodSpec c2 = MethodSpec.constructorBuilder()
    		  .addModifiers(Modifier.PUBLIC)
    		  .addParameter(int.class, "b")
    		  .addParameter(int.class, "f")
    		  .addStatement("$N=b", "baseKey")
    		  .addStatement("$N=f", "fringeKey")
    		  .build();
      
      MethodSpec setBase = MethodSpec.methodBuilder("setBase")
    		  .addModifiers(Modifier.PUBLIC)
    		  .returns(void.class)
    		  .addParameter(int.class, "b")
    		  .addStatement("$N=b", "baseKey")
    		  .build();
      
      MethodSpec setFringe = MethodSpec.methodBuilder("setFringe")
    		  .addModifiers(Modifier.PUBLIC)
    		  .returns(void.class)
    		  .addParameter(int.class, "f")
    		  .addStatement("$N=f", "fringeKey")
    		  .build();
      
      MethodSpec getBase = MethodSpec.methodBuilder("getBase")
    		  .addModifiers(Modifier.PUBLIC)
    		  .returns(image)
    		  .addStatement("return MapData.getImage($N)", "baseKey")
    		  .build();

      MethodSpec getFringe = MethodSpec.methodBuilder("getFringe")
    		  .addModifiers(Modifier.PUBLIC)
    		  .returns(image)
    		  .addStatement("return MapData.getImage($N)", "fringeKey")
    		  .build();
      
      TypeSpec tileData = TypeSpec.classBuilder("TileData")
    		  .addModifiers(Modifier.PROTECTED)
    		  .addField(int.class, "baseKey")
    		  .addField(int.class, "fringeKey")
    		  .addMethod(c2)
    		  .addMethod(setBase)
    		  .addMethod(setFringe)
    		  .addMethod(getBase)
    		  .addMethod(getFringe)
    		  .build();
      
      TypeName listOfDisplayed = ParameterizedTypeName.get(arraylist, displayedemployee);
      
      ClassName td = ClassName.get("", "TileData");
      ArrayTypeName a = ArrayTypeName.of(td);
      ArrayTypeName a2 = ArrayTypeName.of(a);
      
      TypeSpec simSEMap = TypeSpec.classBuilder("SimSEMap")
    		  .superclass(pane)
    		  .addModifiers(Modifier.PUBLIC)
    		  .addField(state, "state", Modifier.PROTECTED)
    		  .addField(logic, "logic", Modifier.PROTECTED)
    		  .addField(String.class, "sopFile", Modifier.PROTECTED)
    		  .addField(listOfDisplayed, "sopUsers")
    		  .addField(int.class, "ssObjCount")
    		  .addMethod(constructor)
    		  .addMethod(getImage)
    		  .addMethod(getXYCoordinates)
    		  .addMethod(getSopUsers)
    		  .addType(tileData)
    		  .build();
      
      JavaFile file = JavaFile.builder("", simSEMap)
    		    .build();
      
      String fileString = "package simse.gui;\n\nimport java.util.Vector;\n"; 
	  for (String type : this.impTypes) {
    	  fileString = fileString + "import simse.adts.objects." + type + ";\n";
      }
      fileString = fileString + file.toString();
      
      writer
      .write("/* File generated by: simse.codegenerator.guigenerator.SimSEMapGenerator */\n");
      writer.write(fileString);

      writer.close();
    } catch (IOException e) {
      JOptionPane.showMessageDialog(null, ("Error writing file "
          + ssmFile.getPath() + ": " + e.toString()), "File IO Error",
          JOptionPane.WARNING_MESSAGE);
    }
  }
}