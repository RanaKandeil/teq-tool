package com.advansys.generator.createnewstepbystepdialog.services;

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
public class ParseAndCreateServiceAsync extends ParseAndCreateFilesMain
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
      writeClassData(out, obj, packageNameWithClassName, null, dialogName, "");

      writeVariables(out, obj, dialogName);
      out.println("}");
      FileFormatter.formatUnformattedClass(sourceFile);
      return out;
   }
   
   

   @Override
   public void writeClassData(PrintStream out, JSONObject obj, String packageNameWithClassName, String viewName, String dialogName, String eventNameWithoutTags) throws JSONException
   {
      String classData;
      classData = "public interface " + GenerationHelper.getSimpleClassName(packageNameWithClassName) + "";
      classData += " {";
      out.println(classData);
   }
}
