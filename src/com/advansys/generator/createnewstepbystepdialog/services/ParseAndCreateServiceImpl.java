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
public class ParseAndCreateServiceImpl extends ParseAndCreateFilesMain
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

      //HERE ONLY
      classData=classImplementsObject(obj, viewName, classData, dialogName, eventNameWithoutTags);

      classData += " {";
      out.println(classData);
   }

   public String classImplementsObject(JSONObject obj, String viewName, String classData, String dialogName, String eventNameWithoutTags) throws JSONException
   {
      if ( obj.getJSONObject("classImplements") != null )
      {
         int implemntsLength = obj.getJSONObject("classImplements").getJSONArray("classImplementsInterfaces").length();
         String implemnts = " implements ";
         for (int i = 0; i < implemntsLength; i++)
         {
            if (i == implemntsLength - 1)
            {
               String impReplace = obj.getJSONObject("classImplements").getJSONArray("classImplementsInterfaces").getString(i);
               if (impReplace.contains("dialogNameService"))
               {
                  implemnts += impReplace.replace("dialogName", dialogName);
               }
               else if (impReplace.contains("eventNameEvent"))
               {
                  implemnts += impReplace.replace("eventName", GenerationHelper.upperFirstChar(eventNameWithoutTags));
               }
               else if (impReplace.contains("eventNamePushEvent"))
               {
                  implemnts += impReplace.replace("eventName", GenerationHelper.upperFirstChar(eventNameWithoutTags));
               }
               else
               {
                  implemnts += obj.getJSONObject("classImplements").getJSONArray("classImplementsInterfaces").get(i);
               }
            }
            else
            {
               implemnts += obj.getJSONObject("classImplements").getJSONArray("classImplementsInterfaces").get(i) + ", ";
            }
         }
         classData += implemnts;
      }
      return classData;
   }
}
