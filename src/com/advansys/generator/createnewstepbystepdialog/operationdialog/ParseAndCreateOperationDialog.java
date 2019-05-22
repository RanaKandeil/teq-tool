package com.advansys.generator.createnewstepbystepdialog.operationdialog;

import com.advansys.generator.common.GenerationHelper;
import com.advansys.generator.common.ParseAndCreateFilesMain;
import com.advansys.utils.FileFormatter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by elbaklna on 6/14/2017.
 */
public class ParseAndCreateOperationDialog extends ParseAndCreateFilesMain
{
   @Override
   public PrintStream createFiles(BufferedReader jsonFile, File sourceFile, LinkedHashMap<String, String> stepViewMap, String projectName, String dialogName, String packageNameWithClassName, String userName, boolean isGenerateEvent) throws FileNotFoundException, JSONException
   {
      String jsonData = "";
      jsonData = GenerationHelper.parseJsonFile(jsonFile);
      PrintStream out = new PrintStream(new FileOutputStream(sourceFile));

      GenerationHelper.printFileCopyrightComment(out);

      out.println("package " + GenerationHelper.getPackageName(packageNameWithClassName) + ";");
      out.println();

      JSONObject obj = new JSONObject(jsonData);
      if (projectName.contains("\\"))
      {
         projectName = projectName.replace("\\", ".");
      }
         //write imports
         writeImports(out, obj, projectName, dialogName, GenerationHelper.getSimpleClassName(packageNameWithClassName), stepViewMap, isGenerateEvent,
                 "");
         out.println();

      GenerationHelper.printClassComment(out, userName);
      out.println();

      //write Class Tags ,Name , Extends and implements
      writeClassData(out, obj, packageNameWithClassName, null, dialogName, "");

      writeVariables(out, obj, dialogName);

      //check on the className
      createSteps(out, obj, stepViewMap);

      out.println("}");

     FileFormatter.formatUnformattedClass(sourceFile);
      return out;
   }

   @Override
   public void writeClassData(PrintStream out, JSONObject obj, String packageNameWithClassName, String viewName, String dialogName, String eventNameWithoutTags) throws JSONException
   {
      if (obj.getString("classTags") != null && !obj.getString("classTags").equals("") && !obj.getString("classTags").isEmpty())
      {
         String replaced = obj.getString("classTags");
         if (replaced.contains("className.gwtrpc"))
         {
            out.println(replaced.replace("className", GenerationHelper.getSimpleClassName(packageNameWithClassName)));
         }
         else
         {
            out.println(obj.getString("classTags"));
         }
      }
      String classData;
      classData = "public class " + GenerationHelper.getSimpleClassName(packageNameWithClassName) + "";

      classData += classExtendsObject(obj, viewName);
      classData += " {";
      out.println(classData);
   }

   public void createSteps(PrintStream out, JSONObject obj, LinkedHashMap<String, String> stepViewMap)
   {
      out.println();
      out.println("   @Override");
      out.println("   protected Step createSteps() {");
      out.println("      setWidth(1200 + \"px\");");
      out.println("      setHeight(900 + \"px\");");
      out.println();
      List<String> alreadyCreatedViews = new ArrayList<>();
      for (Map.Entry stepViewEntry : stepViewMap.entrySet())
      {
         out.println("      " + stepViewEntry.getKey().toString() + " "
               + GenerationHelper.lowFirstChar(stepViewEntry.getKey().toString()) + " = GWT.create("
               + stepViewEntry.getKey().toString() + ".class);");

         if (!alreadyCreatedViews.contains(stepViewEntry.getValue().toString()))
         {
            out.println("      "
                  + stepViewEntry.getValue().toString()
                  + " "
                  + GenerationHelper.lowFirstChar(stepViewEntry.getValue().toString()) + " = GWT.create("
                  + stepViewEntry.getValue().toString() + ".class);");
         }
         out.println("      "
               + GenerationHelper.lowFirstChar(stepViewEntry.getKey().toString())
               + ".setView("
               + GenerationHelper.lowFirstChar(stepViewEntry.getValue().toString()) + ");");
         out.println();
         alreadyCreatedViews.add(stepViewEntry.getValue().toString());
      }
      assignStepsToOtherSteps(out, stepViewMap);
      out.println("      return "
            + GenerationHelper.lowFirstChar(stepViewMap.keySet().toArray()[0].toString()) + ";");
      out.println("   }");
   }

   public void assignStepsToOtherSteps(PrintStream out, LinkedHashMap<String, String> stepViewMap)
   {
      for (String stepNameKey : stepViewMap.keySet())
      {
         for (String otherStep : stepViewMap.keySet())
         {
            if (!stepNameKey.equals(otherStep))
            {
               out.println("      "
                     + GenerationHelper.lowFirstChar(stepNameKey)
                     + ".set" + otherStep + "("
                     + GenerationHelper.lowFirstChar(otherStep) + ");");
            }
         }
      }
   }
}
