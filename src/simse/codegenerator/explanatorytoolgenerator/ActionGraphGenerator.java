/*
 * This class is responsible for generating all of the code for the ActionGraph
 * class in the explanatory tool
 */

package simse.codegenerator.explanatorytoolgenerator;

import simse.codegenerator.CodeGeneratorConstants;
import simse.codegenerator.CodeGeneratorUtils;
import simse.modelbuilder.actionbuilder.ActionType;
import simse.modelbuilder.actionbuilder.DefinedActionTypes;
import simse.modelbuilder.ModelOptions;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

import javax.lang.model.element.Modifier;
import javax.swing.JOptionPane;

import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

public class ActionGraphGenerator implements CodeGeneratorConstants {
  private File directory; // directory to save generated code into
  private DefinedActionTypes actTypes;
  private ModelOptions options;

  public ActionGraphGenerator(DefinedActionTypes actTypes, File directory, 
  		ModelOptions options) {
    this.actTypes = actTypes;
    this.directory = directory;
    this.options = options;
  }

  public void generate() {
    File actGraphFile = new File(directory,
        ("simse\\explanatorytool\\ActionGraph.java"));
    if (actGraphFile.exists()) {
      actGraphFile.delete(); // delete old version of file
    }
      
      ClassName stage = ClassName.get("javafx.stage", "Stage");
      ClassName state = ClassName.get("simse.state", "State");
      ClassName chartMouseListener = ClassName.get("org.jfree.chart.fx.interaction", "ChartMouseListenerFX");
      ClassName arrayList = ClassName.get("java.util", "ArrayList");
      ClassName jFreeChart = ClassName.get("org.jfree.chart", "JFreeChart");
      ClassName chartViewer = ClassName.get("org.jfree.chart.fx", "ChartViewer");
      ClassName menuItem = ClassName.get("javafx.scene.control", "MenuItem");
      ClassName separatorMenuItem = ClassName.get("javafx.scene.control", "SeparatorMenuItem");
      ClassName xySeriesCollection = ClassName.get("org.jfree.data.xy", "XYSeriesCollection");
      ClassName branch = ClassName.get("simse.explanatorytool", "Branch");
      ClassName xySeries = ClassName.get("org.jfree.data.xy", "XYSeries");
      ClassName xyDataset = ClassName.get("org.jfree.data.xy", "XYDataset");
      ClassName xyPlot = ClassName.get("org.jfree.chart.plot", "XYPlot");
      ClassName chartMouseEventFX = ClassName.get("org.jfree.chart.fx.interaction", "ChartMouseEventFX");
      ClassName hashTable = ClassName.get("java.util", "Hashtable");
      ClassName integer = ClassName.get("java.lang", "Integer");
      ClassName action = ClassName.get("simse.adts.actions", "Action");
      ClassName scene = ClassName.get("javafx.scene", "Scene");
      ClassName colorAWT = ClassName.get("java.awt", "Color");
      ClassName rectangleInsets = ClassName.get("org.jfree.chart.ui", "RectangleInsets");
      ClassName valueAxis = ClassName.get("org.jfree.chart.axis", "ValueAxis");
      ClassName numberAxis = ClassName.get("org.jfree.chart.axis", "NumberAxis");
      ClassName xyLineAndShapeRenderer = ClassName.get("org.jfree.chart.renderer.xy", "XYLineAndShapeRenderer");
      ClassName chartFactory = ClassName.get("org.jfree.chart", "ChartFactory");
      ClassName plotOrientation = ClassName.get("org.jfree.chart.plot", "PlotOrientation");
      ClassName javaFXHelpers = ClassName.get("simse.gui.util", "JavaFXHelpers");
      ClassName color = ClassName.get("javafx.scene.paint", "Color");
  	  ClassName mouseEvent = ClassName.get("javafx.scene.input", "MouseEvent");
  	  ClassName mouseButton = ClassName.get("javafx.scene.input", "MouseButton");
  	  ClassName chartEntity = ClassName.get("org.jfree.chart.entity", "ChartEntity");
  	  ClassName xyItemEntity = ClassName.get("org.jfree.chart.entity", "XYItemEntity");
  	  ClassName actionInfoWindow = ClassName.get("simse.explanatorytool", "ActionInfoWindow");
  	 ClassName abstractXYItemLabelGenerator = ClassName.get("org.jfree.chart.labels", "AbstractXYItemLabelGenerator");
     ClassName xyToolTipGenerator = ClassName.get("org.jfree.chart.labels", "XYToolTipGenerator");
      ArrayTypeName stringArray = ArrayTypeName.of(String.class);
      
     
      
      MethodSpec toolTipConstructor = MethodSpec.constructorBuilder()
    		  .addStatement("super()")
    		  .build();
      
      MethodSpec generateToolTip = MethodSpec.methodBuilder("generateToolTip")
    		  .returns(String.class)
    		  .addParameter(xyDataset, "dataset")
    		  .addParameter(int.class, "series")
    		  .addParameter(int.class, "item")
    		  .addStatement("return new String(dataset.getSeriesKey(series) + \": click for Action info\")")
    		  .build();
      
      TypeSpec actionGraphToolTipGenerator = TypeSpec.classBuilder("ActionGraphToolTipGenerator")
    		  .superclass(abstractXYItemLabelGenerator)
    		  .addSuperinterface(xyToolTipGenerator)
    		  .addField(FieldSpec.builder(long.class, "serialVersionUID")
    				  .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
    				  .initializer("1L").build())
    		  .addMethod(toolTipConstructor)
    		  .addMethod(generateToolTip)
    		  .build();
      

      String actionFields = "";
      
      // generate an index and an indices array list for each type of action:
      Vector<ActionType> actions = actTypes.getAllActionTypes();
      for (int i = 0; i < actions.size(); i++) {
        ActionType act = actions.get(i);
        if (act.isVisibleInExplanatoryTool()) {
          String lCaseName = act.getName().toLowerCase();
//          writer.write("private int " + lCaseName
//                  + "Index = 1; // index to be used for labeling multiple actions of the same type");
//          writer.write(NEWLINE);
          actionFields += "private int " + lCaseName
                  + "Index = 1; // index to be used for labeling multiple actions of the same type\n";
//          writer.write("private ArrayList<Integer> " + lCaseName
//                  + "Indices = new ArrayList<Integer>(); // an ArrayList to map indices for "
//                  + act.getName() + " Action labels to action ids");
//          writer.write(NEWLINE);
          actionFields += "private ArrayList<Integer> " + lCaseName
                  + "Indices = new ArrayList<Integer>(); // an ArrayList to map indices for "
                  + act.getName() + " Action labels to action ids\n";
        }
      }
      
      String dummyEntriesBlock = "";
      
      for (int i = 0; i < actions.size(); i++) {
        ActionType act = actions.get(i);
        if (act.isVisibleInExplanatoryTool()) {
//          writer.write(act.getName().toLowerCase()
//              + "Indices.add(0, new Integer(-1));");
//          writer.write(NEWLINE);
          dummyEntriesBlock += act.getName().toLowerCase()
                  + "Indices.add(0, new Integer(-1));\n";
        }
      }
      
      String actionBlock = "";
      
      // go through each action and generate code for it:
      boolean writeElse = false;
      for (int i = 0; i < actions.size(); i++) {
        ActionType act = actions.get(i);
        String uCaseName = 
        	CodeGeneratorUtils.getUpperCaseLeading(act.getName());
        String lCaseName = act.getName().toLowerCase();
        if (act.isVisibleInExplanatoryTool()) {
          if (writeElse) {
//            writer.write("else ");
            actionBlock += "else ";
          } else {
            writeElse = true;
          }
//          writer.write("if (actionNames[i].equals(\"" + uCaseName + "\")) {");
//          writer.write(NEWLINE);
          actionBlock += "if (actionNames[i].equals(\"" + uCaseName + "\"))\n";
//          writer.write("// go through the " + uCaseName
//              + "ActionStateRepository for each clock tick:");
//          writer.write(NEWLINE);
          actionBlock += "// go through the " + uCaseName
                  + "ActionStateRepository for each clock tick:\n";
//          writer.write("for (int j = 0; j < log.size(); j++) {");
//          writer.write(NEWLINE);
          actionBlock += "for (int j = 0; j < log.size(); j++)\n";
//          writer.write("State state = log.get(j);");
//          writer.write(NEWLINE);
          actionBlock += "State state = log.get(j);\n";
//          writer.write("Vector<" + uCaseName + "Action> " + lCaseName
//              + "Actions = state.getActionStateRepository().get" + uCaseName
//              + "ActionStateRepository().getAllActions();");
//          writer.write(NEWLINE);
//          writer.write(NEWLINE);
          actionBlock += "Vector<" + uCaseName + "Action> " + lCaseName
                  + "Actions = state.getActionStateRepository().get" + uCaseName
                  + "ActionStateRepository().getAllActions();\n\n";
//          writer.write("// go through each " + uCaseName + "Action:");
//          writer.write(NEWLINE);
          actionBlock += "// go through each " + uCaseName + "Action:\n";
//          writer.write("for (int k = 0; k < " + lCaseName
//              + "Actions.size(); k++) {");
//          writer.write(NEWLINE);
          actionBlock += "for (int k = 0; k < " + lCaseName
                  + "Actions.size(); k++)\n";
//          writer.write(uCaseName + "Action action = " + lCaseName + "Actions.get(k);");
//          writer.write(NEWLINE);
//          writer.write(NEWLINE);
          actionBlock += uCaseName + "Action action = " + lCaseName + "Actions.get(k);\n\n";
//          writer.write("// update series:");
//          writer.write(NEWLINE);
          actionBlock += "// update series:\n";
//          writer.write("updateSeries(action, j);");
//          writer.write(NEWLINE);
          actionBlock += "updateSeries(action, j);\n";
//          writer.write(CLOSED_BRACK);
//          writer.write(NEWLINE);
          actionBlock += "}\n";
//          writer.write(CLOSED_BRACK);
//          writer.write(NEWLINE);
          actionBlock += "}\n";
//          writer.write(CLOSED_BRACK);
//          writer.write(NEWLINE);
          actionBlock += "}\n";
        }
      }
      

      
      MethodSpec setChartColors = MethodSpec.methodBuilder("setChartColors")
    		  .addStatement("chartViewer.backgroundProperty().set($T.createBackgroundColor($T.WHITE))", javaFXHelpers, color)
    		  .build();
      

      
      MethodSpec createChart = MethodSpec.methodBuilder("createChart")
    		  .returns(jFreeChart)
    		  .addParameter(xyDataset, "dataset")
    		  .addStatement("// create the chart")
    		  .addStatement("$T chart = $T.createXYLineChart(\"Action Graph\", \"Clock Ticks\", null, dataset, $T.VERTICAL, true, true, false)", jFreeChart, chartFactory, plotOrientation)
    		  .addStatement("$T plot = ($T) chart.getPlot()", xyPlot, xyPlot)
    		  .addStatement("plot.getRenderer().setDefaultToolTipGenerator(new ActionGraphToolTipGenerator())")
    		  .addStatement("plot.setBackgroundPaint(new $T(0xFF, 0xFF, 0xCC))", colorAWT)
    		  .addStatement("plot.setAxisOffset(new $T(5.0, 5.0, 5.0, 5.0))", rectangleInsets)
    		  .addStatement("plot.setDomainGridlinePaint($T.BLACK)", colorAWT)
    		  .addStatement("plot.setRangeGridlinePaint($T.BLACK)", colorAWT)
    		  .addStatement("$T rangeAxis = plot.getRangeAxis()", valueAxis)
    		  .addStatement("rangeAxis.setTickLabelsVisible(false)")
    		  .addStatement("rangeAxis.setTickMarksVisible(false)")
    		  .addStatement("rangeAxis.setAxisLineVisible(false)")
    		  .addStatement("rangeAxis.setStandardTickUnits($T.createIntegerTickUnits())", numberAxis)
    		  .addStatement("plot.getDomainAxis().setStandardTickUnits($T.createIntegerTickUnits())", numberAxis)
    		  .addStatement("$T renderer = ($T) plot.getRenderer()", xyLineAndShapeRenderer, xyLineAndShapeRenderer)
    		  .addStatement("renderer.setDefaultShapesVisible(true)")
    		  .addStatement("renderer.setDefaultShapesFilled(true)")
    		  .addStatement("// change the auto tick unit selection to integer units only")
    		  .addStatement("$T domainAxis = ($T) plot.getDomainAxis()", numberAxis, numberAxis)
    		  .addStatement("domainAxis.setStandardTickUnits($T.createIntegerTickUnits())", numberAxis)
    		  .addStatement("return chart")
    		  .build();
      
      
      
      MethodSpec createDataset = MethodSpec.methodBuilder("createDataset")
    		  .addStatement("// add a dummy entry for index 0")
    		  .addStatement("indices.add(0, \"Action\")")
    		  .addStatement("// go through each action")
    		  .beginControlFlow("for (int i = 0; i < actionNames.length; i++)")
    		  .addCode(actionBlock)
    		  .endControlFlow()
    		  .addStatement("return dataset")
    		  .build();
      
      
      
      MethodSpec constructor = MethodSpec.constructorBuilder()
    		  .addStatement("super()")
    		  .addStatement("$T title = \"Action Graph\"", String.class)
    		  .beginControlFlow("if (branch.getName() != null)")
    		  .addStatement("title = title.concat(\" - \" + branch.getName())")
    		  .endControlFlow()
    		  .addStatement("setTitle(title)")
    		  .addStatement("this.log = log")
    		  .addStatement("this.actionNames = actionNames")
    		  .addStatement("lastRightClickedX = 0")
    		  .addCode(dummyEntriesBlock)
    		  .addStatement("dataset = new $T()", xySeriesCollection)
    		  .addStatement("$T xydataset = $N()", xyDataset, createDataset)
    		  .addStatement("chart = $N(xydataset)", createChart)
    		  .addStatement("chartViewer = new $T(chart, true)", chartViewer)
    		  .addStatement("$N()", setChartColors)
    		  .addStatement("chartViewer.addChartMouseListener(this)")
    		  .addStatement("chartViewer.setPrefSize(500, 270)")
    		  .addStatement("setScene(new $T(chartViewer))", scene)
    		  .addStatement("newBranchItem = new $T(\"Start new branch from here\")", menuItem)
    		  .addStatement("newBranchItem.setOnAction(menuEvent)")
    		  .addStatement("separator = new $T()", separatorMenuItem)
    		  .beginControlFlow("if (showChart)")
    		  .addStatement("show()")
    		  .endControlFlow()
    		  .build();
      
      String ifStatementActionBlock = "";

      // go through each action and generate code for it:
      boolean writeElse2 = false;
      for (int i = 0; i < actions.size(); i++) {
        ActionType act = actions.get(i);
        String uCaseName = 
        	CodeGeneratorUtils.getUpperCaseLeading(act.getName());
        String lCaseName = act.getName().toLowerCase();
        if (act.isVisibleInExplanatoryTool()) {
          if (writeElse2) {
//            writer.write("else ");
            ifStatementActionBlock += "else ";
          } else {
            writeElse2 = true;
          }
//          writer.write("if (action instanceof " + uCaseName + "Action) {");
//          writer.write(NEWLINE);
          ifStatementActionBlock += "if (action instanceof \" + uCaseName + \"Action) {\n";
//          writer.write("newSeriesName = \"" + uCaseName + "Action-\" + "
//              + lCaseName + "Index;");
//          writer.write(NEWLINE);
          ifStatementActionBlock += "newSeriesName = \"" + uCaseName + "Action-\" + "
                  + lCaseName + "Index;\n";
//          writer.write("newSeries = new XYSeries(newSeriesName);");
//          writer.write(NEWLINE);
          ifStatementActionBlock += "newSeries = new XYSeries(newSeriesName);\n";
//          writer.write(lCaseName + "Indices.add(" + lCaseName
//              + "Index, new Integer(action.getId()));");
//          writer.write(NEWLINE);
          ifStatementActionBlock += lCaseName + "Indices.add(" + lCaseName
                  + "Index, new Integer(action.getId()));\n";
//          writer.write(lCaseName + "Index++;");
//          writer.write(NEWLINE);
          ifStatementActionBlock += lCaseName + "Index++;\n";
          ifStatementActionBlock += "}\n";
        }
      }

      String elseStatementCodeBlock = "";
      
      // go through each action and generate code for it:
      boolean writeElse3 = false;
      for (int i = 0; i < actions.size(); i++) {
        ActionType act = actions.get(i);
        String uCaseName = 
        	CodeGeneratorUtils.getUpperCaseLeading(act.getName());
        String lCaseName = act.getName().toLowerCase();
        if (act.isVisibleInExplanatoryTool()) {
          if (writeElse3) {
//            writer.write("else ");
            elseStatementCodeBlock += "else ";
          } else {
            writeElse3 = true;
          }
//          writer.write("if (action instanceof " + uCaseName + "Action) {");
//          writer.write(NEWLINE);
          elseStatementCodeBlock += "if (action instanceof " + uCaseName + "Action) {\n";
//          writer.write("index = " + lCaseName
//              + "Indices.indexOf(new Integer(action.getId()));");
//          writer.write(NEWLINE);
//          writer.write(NEWLINE);
          elseStatementCodeBlock += "index = " + lCaseName
                  + "Indices.indexOf(new Integer(action.getId()));\n\n";
//          writer.write("// add the data value to the series:");
//          writer.write(NEWLINE);
          elseStatementCodeBlock += "// add the data value to the series:\n"; 
//          writer.write("oldSeries.add(clockTick, indices.indexOf(\""
//              + uCaseName + "Action-\" + index));");
//          writer.write(NEWLINE);
          elseStatementCodeBlock += "oldSeries.add(clockTick, indices.indexOf(\""
                  + uCaseName + "Action-\" + index));\n";
//          writer.write(CLOSED_BRACK);
//          writer.write(NEWLINE);
          elseStatementCodeBlock += "}\n";
        }
      }

      
      MethodSpec updateSeries = MethodSpec.methodBuilder("updateSeries")
    		  .addParameter(action, "action")
    		  .addParameter(int.class, "clockTick")
    		  .beginControlFlow("if (!series.containsKey(action.getId()))")
    		  .addStatement("$T newSeries = null", xySeries)
    		  .addStatement("$T newSeriesName = \"\"", String.class)
    		  .addCode(ifStatementActionBlock)
    		  .addStatement("// add the data value to the series")
    		  .addStatement("newSeries.add(clockTick, actionIndex)")
    		  .addStatement("// add the series to the Hashtable")
    		  .addStatement("series.put(action.getId(), newSeries)")
    		  .addStatement("// add the index entry to the ArrayList")
    		  .addStatement("indices.add(actionIndex, newSeriesName)")
    		  .addStatement("dataset.addSeries(newSeries)")
    		  .addStatement("// update the index for the next new action")
    		  .addStatement("actionIndex++")
    		  .beginControlFlow("else ")
    		  .addStatement("$T oldSeries = series.get(action.getId())", xySeries)
    		  .addStatement("int index = 0")
    		  .addCode(elseStatementCodeBlock)
    		  .endControlFlow()
    		  .build();
      
      MethodSpec getIdOfActionWithSeriesName = MethodSpec.methodBuilder("getIdOfActionWithSeriesName")
    		  .addParameter(String.class, "seriesName")
    		  .returns(int.class)
    		  .addStatement("$T<$T> keys = series.keys()", Enumeration.class, Integer.class)
    		  .beginControlFlow("while (keys.hasMoreElements())")
    		  .addStatement("$T id = keys.nextElement()", Integer.class)
    		  .addStatement("$T xys = series.get(id)", xySeries)
    		  .beginControlFlow("if (xys.getKey().equals(seriesName))")
    		  .addStatement("return id.intValue()")
    		  .endControlFlow()
    		  .endControlFlow()
    		  .addStatement("return -1")
    		  .build();
      
      MethodSpec getXYPlot = MethodSpec.methodBuilder("getXYPlot")
    		  .returns(xyPlot)
    		  .addStatement("return chart.getXYPlot()")
    		  .build();
    	
    	String mouseReleasedBlock = "";
    	
    	if (options.getAllowBranchingOption()) {
//	    	writer.write("if (me.getButton() != MouseEvent.BUTTON1) { // not left-click");
//	    	writer.write(NEWLINE);
	    	mouseReleasedBlock += "if (me.getButton() != MouseEvent.BUTTON1) { // not left-click\n";
//	    	writer.write("XYPlot plot = chart.getXYPlot();");
//	    	writer.write(NEWLINE);
	    	mouseReleasedBlock += "XYPlot plot = chart.getXYPlot();\n";
//	    	writer.write("Range domainRange = plot.getDataRange(plot.getDomainAxis());");
//	    	writer.write(NEWLINE);
	    	mouseReleasedBlock += "Range domainRange = plot.getDataRange(plot.getDomainAxis());\n";
//	    	writer.write("if (domainRange != null) { // chart is not blank");
//	    	writer.write(NEWLINE);
	    	mouseReleasedBlock += "if (domainRange != null) { // chart is not blank\n";
//	    	writer.write("Point2D pt = chartPanel.translateScreenToJava2D(new Point(me.getX(), me.getY()));");
//	    	writer.write(NEWLINE);
	    	mouseReleasedBlock += "javafx.geometry.Point2D pt = chartViewer.localToScreen(event.getScreenX(), event.getScreenY());\n";
//	    	writer.write("ChartRenderingInfo info = this.chartPanel.getChartRenderingInfo();");
//	    	writer.write(NEWLINE);
	    	mouseReleasedBlock += "ChartRenderingInfo info = this.chartViewer.getRenderingInfo();\n";
//	    	writer.write("Rectangle2D dataArea = info.getPlotInfo().getDataArea();");
//	    	writer.write(NEWLINE);
	    	mouseReleasedBlock += "java.awt.geom.Rectangle2D dataArea = info.getPlotInfo().getDataArea();\n";
//	    	writer.write("NumberAxis domainAxis = (NumberAxis)plot.getDomainAxis();");
//	    	writer.write(NEWLINE);
	    	mouseReleasedBlock += "NumberAxis domainAxis = (NumberAxis) plot.getDomainAxis();\n";
//	    	writer.write("RectangleEdge domainAxisEdge = plot.getDomainAxisEdge();");
//	    	writer.write(NEWLINE);
	    	mouseReleasedBlock += "RectangleEdge domainAxisEdge = plot.getDomainAxisEdge();\n";
//	    	writer.write("double chartX = domainAxis.java2DToValue(pt.getX(), dataArea, domainAxisEdge);");
//	    	writer.write(NEWLINE);
	    	mouseReleasedBlock += "double chartX = domainAxis.java2DToValue(pt.getX(), dataArea, domainAxisEdge);\n";
//	    	writer.write("lastRightClickedX = (int)Math.rint(chartX);");
//	    	writer.write(NEWLINE);
	    	mouseReleasedBlock += "lastRightClickedX = (int)Math.rint(chartX);\n";
//	    	writer.write("if (domainRange != null && lastRightClickedX >= domainRange.getLowerBound() && lastRightClickedX <= domainRange.getUpperBound()) { // clicked within domain range");
//	    	writer.write(NEWLINE);
	    	mouseReleasedBlock += "if (domainRange != null && lastRightClickedX >= domainRange.getLowerBound() && lastRightClickedX <= domainRange.getUpperBound()) { // clicked within domain range\n";
//	    	writer.write("if (chartPanel.getPopupMenu().getComponentIndex(newBranchItem) == -1) { // no new branch item on menu currently");
//	    	writer.write(NEWLINE);
	    	mouseReleasedBlock += "if (chartViewer.getContextMenu().getItems().indexOf(newBranchItem) == -1) { // no new branch item on menu currently\n";
//	    	writer.write("chartPanel.getPopupMenu().add(separator);");
//	    	writer.write(NEWLINE);
	    	mouseReleasedBlock += "chartViewer.getContextMenu().getItems().add(separator);\n";
//	    	writer.write("chartPanel.getPopupMenu().add(newBranchItem);");
//	    	writer.write(NEWLINE);
	    	mouseReleasedBlock += "chartViewer.getContextMenu().getItems().add(newBranchItem)\n";
//	    	writer.write("chartPanel.getPopupMenu().pack();");
//	    	writer.write(NEWLINE);
//	    	writer.write(NEWLINE);
//	    	writer.write(CLOSED_BRACK);
	    	mouseReleasedBlock += "\n}\n";
//	    	writer.write(NEWLINE);
//	    	writer.write(CLOSED_BRACK);
//	    	writer.write(NEWLINE);
//	    	writer.write("else { // clicked outside of domain range");
//	    	writer.write(NEWLINE);
	    	mouseReleasedBlock += "else { // clicked outside of domain range";
//	    	writer.write("if (chartPanel.getPopupMenu().getComponentIndex(newBranchItem) >= 0) { // new branch item currently on menu");
//	    	writer.write(NEWLINE);
	    	mouseReleasedBlock += "if (chartViewer.getContextMenu().getItems().indexOf(newBranchItem) >= 0) { // new branch item currently on menu";
//	    	writer.write("chartPanel.getPopupMenu().remove(newBranchItem);");
//	    	writer.write(NEWLINE);
	    	mouseReleasedBlock += "chartViewer.getContextMenu().getItems().remove(newBranchItem);\n";
//	    	writer.write("if (chartPanel.getPopupMenu().getComponentIndex(separator) >= 0) { // has separator");
//	    	writer.write(NEWLINE);
	    	mouseReleasedBlock += "if (chartViewer.getContextMenu().getItems().indexOf(separator) >= 0) { // has separator\n";
//	    	writer.write("chartPanel.getPopupMenu().remove(separator);");
//	    	writer.write(NEWLINE);
	    	mouseReleasedBlock += "chartViewer.getContextMenu().getItems().remove(separator);\n";
//	    	writer.write(CLOSED_BRACK);
//	    	writer.write(NEWLINE);
	    	mouseReleasedBlock += "}\n";
//	    	writer.write(CLOSED_BRACK);
//	    	writer.write(NEWLINE);
	    	mouseReleasedBlock += "}\n";
//	    	writer.write(CLOSED_BRACK);
//	    	writer.write(NEWLINE);
	    	mouseReleasedBlock += "}\n";
//	    	writer.write(CLOSED_BRACK);
//	    	writer.write(NEWLINE);
	    	mouseReleasedBlock += "}\n";
//	    	writer.write(CLOSED_BRACK);
//	    	writer.write(NEWLINE);
	    	mouseReleasedBlock += "}\n";
    	}
    	
    	
        MethodSpec chartMouseClicked = MethodSpec.methodBuilder("chartMouseClicked")
      		  .addParameter(chartMouseEventFX, "me")
      		  .addStatement("$T event = me.getTrigger()", mouseEvent)
      		  .beginControlFlow("if (event.getButton() == $T.PRIMARY)", mouseButton)
      		  .addStatement("$T entity = ($T) me.getEntity()", chartEntity, chartEntity)
      		  .beginControlFlow("if ((entity != null) && (entity instanceof $T))", xyItemEntity)
      		  .addStatement("$T xyEntity = ($T) entity", xyItemEntity, xyItemEntity)
      		  .addStatement("// get the x-value of the action (clock tick)")
      		  .addStatement("int xVal = (int) xyEntity.getDataset().getXValue(xyEntity.getSeriesIndex(), xyEntity.getItem())")
      		  .addStatement("// get the y-value of the action (action index)")
      		  .addStatement("int yVal = (int) xyEntity.getDataset().getYValue(xyEntity.getSeriesIndex(), xyEntity.getItem())")
      		  .addStatement("// get the series name of the action")
      		  .addStatement("$T seriesName = indices.get(yVal)", String.class)
      		  .addStatement("// get the action id")
      		  .addStatement("int actionId = $N(seriesName)", getIdOfActionWithSeriesName)
      		  .beginControlFlow("if (actionId > -1)")
      		  .addStatement("$T action = log.get(xVal).getActionStateRepository().getActionWithId(actionId)", action)
      		  .beginControlFlow("if (action != null)")
      		  .addStatement("new $T(seriesName, action, xVal)", actionInfoWindow)
      		  .endControlFlow()
      		  .endControlFlow()
      		  .endControlFlow()
      		  .endControlFlow()
      		  .beginControlFlow("else ")
      		  .addCode(mouseReleasedBlock)
      		  .endControlFlow()
      		  .build();

    	
    	String updateActionCodeBlock = "";
    	// go through each action and generate code for it:
      writeElse = false;
      for (int i = 0; i < actions.size(); i++) {
        ActionType act = actions.get(i);
        String uCaseName = 
        	CodeGeneratorUtils.getUpperCaseLeading(act.getName());
        String lCaseName = act.getName().toLowerCase();
        if (act.isVisibleInExplanatoryTool()) {
          if (writeElse) {
//            writer.write("else ");
            updateActionCodeBlock += "else ";
          } else {
            writeElse = true;
          }
//          writer.write("if (actionNames[i].equals(\"" + uCaseName + "\")) {");
//          writer.write(NEWLINE);
          updateActionCodeBlock += "if (actionNames[i].equals(\"" + uCaseName + "\")) {\n";
//          writer.write("// get the " + uCaseName
//              + "ActionStateRepository for the last clock tick:");
//          writer.write(NEWLINE);
          updateActionCodeBlock += "// get the " + uCaseName
                  + "ActionStateRepository for the last clock tick:\n";
//          writer.write("State state = log.get(log.size() - 1);");
//          writer.write(NEWLINE);
          updateActionCodeBlock += "State state = log.get(log.size() - 1);\n";
//          writer.write("Vector<" + uCaseName + "Action> " + lCaseName
//              + "Actions = state.getActionStateRepository().get" + uCaseName
//              + "ActionStateRepository().getAllActions();");
//          writer.write(NEWLINE);
//          writer.write(NEWLINE);
          updateActionCodeBlock += "Vector<" + uCaseName + "Action> " + lCaseName
                  + "Actions = state.getActionStateRepository().get" + uCaseName
                  + "ActionStateRepository().getAllActions();\n\n";
//          writer.write("// go through each " + uCaseName + "Action:");
//          writer.write(NEWLINE);
          updateActionCodeBlock += "// go through each " + uCaseName + "Action:\n";
//          writer.write("for (int k = 0; k < " + lCaseName
//              + "Actions.size(); k++) {");
//          writer.write(NEWLINE);
          updateActionCodeBlock += "for (int k = 0; k < " + lCaseName
                  + "Actions.size(); k++) {\n";
//          writer.write(uCaseName + "Action action = " + lCaseName + 
//          		"Actions.get(k);");
//          writer.write(NEWLINE);
//          writer.write(NEWLINE);
          updateActionCodeBlock += uCaseName + "Action action = " + lCaseName + 
            		"Actions.get(k);\n\n";
//          writer.write("// update series:");
//          writer.write(NEWLINE);
          updateActionCodeBlock += "// update series:\n";
//          writer.write("updateSeries(action, (log.size() - 1));");
//          writer.write(NEWLINE);
          updateActionCodeBlock += "updateSeries(action, (log.size() - 1));\n";
//          writer.write(CLOSED_BRACK);
//          writer.write(NEWLINE);
//          writer.write(CLOSED_BRACK);
//          writer.write(NEWLINE);
          updateActionCodeBlock += "}\n}\n";
        }
      }
      
      MethodSpec update = MethodSpec.methodBuilder("update")
    		.beginControlFlow("if ((log.size() > 0) && (log.get(log.size() - 1) != null))")
    		.addStatement("// add a new end data point for each series:")
    		.addStatement("// go through each action:")
    		.beginControlFlow("for (int i = 0; i < actionNames.length; i++)")
    		.addCode(updateActionCodeBlock)
    		.endControlFlow()
    		.endControlFlow()
      		.build();
    		  
      
      TypeSpec actionGraph = TypeSpec.classBuilder("ActionGraph")
    		  .superclass(stage)
    		  .addSuperinterface(chartMouseListener)
    		  .addField(ParameterizedTypeName.get(arrayList, state), "log")
    		  .addField(stringArray, "actionNames")
    		  .addField(jFreeChart, "chart")
    		  .addField(chartViewer, "chartViewer")
    		  .addField(menuItem, "newBranchItem")
    		  .addField(separatorMenuItem, "separator")
    		  .addField(int.class, "lastRightClickedX")
    		  .addField(xySeriesCollection, "dataset")
    		  .addField(branch, "branch")
    		  .addField(ParameterizedTypeName.get(hashTable, integer, xySeries), "series")
    		  .addStaticBlock(CodeBlock.builder().add(actionFields).build())
    		  .addMethod(constructor)
    		  .addMethod(createDataset)
    		  .addMethod(setChartColors)
    		  .addMethod(createChart)
    		  .addMethod(updateSeries)
    		  .addMethod(chartMouseClicked)
    		  .addMethod(getIdOfActionWithSeriesName)
    		  .addMethod(update)
    		  .addMethod(getXYPlot)
    		  .addType(actionGraphToolTipGenerator)
    		  .build();
      
      JavaFile javaFile = JavaFile.builder("simse.explanatorytool", actionGraph)
    		    .build();

      try {
    	FileWriter writer = new FileWriter(actGraphFile);
		javaFile.writeTo(writer);
		
		writer.close();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
  }
}
