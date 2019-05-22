package com.advansys.generator.createnewstepbystepdialog.steps;

import com.advansys.generator.common.GenerationHelper;
import com.advansys.generator.common.ParseAndCreateFilesMain;
import com.advansys.utils.FileFormatter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.LinkedHashMap;

/**
 * Created by elbaklna on 6/14/2017.
 */
public class ParseAndCreateStep extends ParseAndCreateFilesMain
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

      writeClassData(out, obj, packageNameWithClassName, stepViewMap.get(GenerationHelper.getSimpleClassName(packageNameWithClassName)),
            dialogName, "");

      writeVariables(out, obj, dialogName);
      writeMethods(out, obj, projectName, dialogName, packageNameWithClassName, stepViewMap, isGenerateEvent);
      //FileFormatter.formatUnformattedClass(sourceFile);
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
}
