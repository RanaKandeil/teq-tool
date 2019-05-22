package com.advansys.generator.createnewstepbystepdialog.views;

import com.advansys.generator.common.GenerationHelper;
import com.advansys.generator.common.GenerationMain;
import org.json.JSONException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by elbaklna on 6/19/2017.
 */
public class GenerationView
{

   private String basePackageDirectory;
   private boolean isGenerateEvent;
   private String viewClassName;

   public GenerationView(String basePackageDirectory, boolean isGenerateEvent, String viewClassName)
   {
      this.basePackageDirectory = basePackageDirectory;
      this.isGenerateEvent = isGenerateEvent;
      this.viewClassName = viewClassName;
   }

   // create views classes with their xml files
   public StringBuilder createViewClasses(StringBuilder sb)
   {
      try {
         List<String> alreadyCreatedViews = new ArrayList<>();
         for (String viewName : GenerationMain.getStepViewMap().values())
         {
            if (!alreadyCreatedViews.contains(viewName))
            {
               createViewJavaClass(viewName, sb);
               createViewXMLFile(viewName, sb);
            }
            alreadyCreatedViews.add(viewName);
         }
         return sb;
      }
      catch (JSONException e){
         GenerationMain.getLOGGER().warning(viewClassName + "'s " + "JSON file is not found" + e.getMessage());
         return null;
      }

   }

   // create view xml file
   private StringBuilder createViewXMLFile(String viewName, StringBuilder createViewXMLFile)
   {
      try {
         File sourceFileUiXml = new File(basePackageDirectory + "/client/"
                 + GenerationMain.getDialogName().toLowerCase() + "/" + "views/" + viewName
                 + ".ui.xml");

         if (sourceFileUiXml.exists())
         {
            sourceFileUiXml.delete();
            GenerationMain.getLOGGER().info(viewClassName + " deleted successfully.");
            // return createStepByStepDialog;
         }
            PrintStream out = new PrintStream(new FileOutputStream(
                    sourceFileUiXml));
            out.println("<!DOCTYPE ui:UiBinder SYSTEM \"http://dl.google.com/gwt/DTD/xhtml.ent\">");
            out.println("<ui:UiBinder xmlns:ui=\"urn:ui:com.google.gwt.uibinder\"");
            out.println("xmlns:d=\"urn:import:com.dematic.wms.app.base.ui.client.scomponent\"");
            out.println("xmlns:s=\"urn:import:com.dematic.wms.app.base.ui.client.dialog.steps\">");
            out.println();
            out.println("<d:SAttributePanel expandX=\"100%\" expandY=\"100%\" fontSize=\"20\">");
            if (isGenerateEvent)
            {
               out.println();
               List<String> keySet = new ArrayList<String>(GenerationMain.getStepViewMap().keySet());
               if (GenerationMain.getStepViewMap().get(keySet.get(0)).equals(viewName))
               {
                  if (GenerationMain.getProjectName().contains("\\"))
                  {
                     GenerationMain.setProjectName(GenerationMain.getProjectName().replace("\\", "."));
                  }
                  out.println("<d:SAttributePanel title=\"actions\" expandX=\"20%\" expandY=\"100%\">\n" +
                          "    \t\t<s:StepEventButton eventClassName=\"com.dematic." + GenerationMain.getProjectName().toLowerCase() + ".ui.client."
                          + GenerationMain.getDialogName().toLowerCase() + ".event.SwitchToOtherStepEvent\"\n" +
                          "            height=\"80px\" width=\"300px\" enabled=\"false\"/>\n" +
                          "</d:SAttributePanel>\n");
               }
            }
            out.println();
            out.println("</d:SAttributePanel>");
            out.println();
            out.println("</ui:UiBinder>");
            out.close();
            GenerationMain.getLOGGER().info("view xml file " + sourceFileUiXml.getAbsolutePath() + " created");
            GenerationHelper.printLog("view xml file " + sourceFileUiXml.getAbsolutePath() + " created");
            createViewXMLFile.append(" \n view xml file " + sourceFileUiXml.getAbsolutePath() + " created");
         return createViewXMLFile;
      }
      catch (FileNotFoundException e){
         GenerationMain.getLOGGER().warning(viewName + " view xml file was not found" + e.getMessage());
         return null;
      }

   }

   // create view class
   private StringBuilder createViewJavaClass(String viewName, StringBuilder createViewJavaClass)
   {
      try {
         File sourceFile = new File(basePackageDirectory + "/client/"
                 + GenerationMain.getDialogName().toLowerCase() + "/" + "views/" + viewName + ".java");

         sourceFile.delete();
         GenerationMain.getLOGGER().info(viewName + " deleted successfully.");
         if (sourceFile.exists())
         {
            System.out.println(viewName + " already exists");
            //         return;
         }
         else
         {
            ParseAndCreateView parseAndCreateView = new ParseAndCreateView();

            InputStream inputStream = GenerationMain.class.getResourceAsStream("../json/view.json");
            if(inputStream == null) {
               GenerationMain.getLOGGER().warning(viewName + "'s " + "JSON file is empty");
               return createViewJavaClass;
            }

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            PrintStream out = parseAndCreateView.createFiles(bufferedReader, sourceFile, GenerationMain.getStepViewMap(), GenerationMain.getProjectName(),
                    GenerationMain.getDialogName(), viewClassName + viewName,
                    GenerationMain.getUserName(), isGenerateEvent);
            out.println("}");
            GenerationMain.getLOGGER().info("View Dialog " + sourceFile.getAbsolutePath() + " created");
            GenerationHelper.printLog("View Dialog " + sourceFile.getAbsolutePath() + " created");

            out.close();
            GenerationMain.getLOGGER().info("view class " + sourceFile.getAbsolutePath() + " created");
            GenerationHelper.printLog("view class " + sourceFile.getAbsolutePath() + " created");
            createViewJavaClass.append(" \n view class " + sourceFile.getAbsolutePath() + " created");
         }
         return createViewJavaClass;
      }
      catch (FileNotFoundException e){
         GenerationMain.getLOGGER().warning(viewName + "file was not found" + e.getMessage());
         return null;
      }
      catch (JSONException e){
         GenerationMain.getLOGGER().warning(viewName + "'s " + "JSON file is not found" + e.getMessage());
         return null;
      }

   }
}