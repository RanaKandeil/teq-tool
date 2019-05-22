package com.advansys.generator.createnewpushevent;

import com.advansys.generator.common.GenerationHelper;
import com.advansys.generator.common.ParseAndCreateFilesMain;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.LinkedHashMap;

/**
 * Created by elbaklna on 6/28/2017.
 */
public class ParseAndCreateEventWPN extends ParseAndCreateFilesMain
{

   public StringBuilder createEvent(String eventClassName, LinkedHashMap<String, String> attributes, StringBuilder log, String eventDirectory, String projectName, String userName, String eventNameWithoutTags, String dialogName, String eventClassNameWPN)
         throws FileNotFoundException, JSONException
   {
      File sourceFile = new File(eventDirectory + "/"
            + dialogName.toLowerCase() + "/boundary/" + "events/"
            + GenerationHelper.getSimpleClassName(eventClassName) + ".java");

      if (sourceFile.exists())
      {
         System.out.println(eventClassName + " is already exist");
         return log.append(eventClassName + " is already exist");
      }
      else
      {

         InputStream inputStream = getClass().getResourceAsStream("/com/advansys/generator/json/Event.json");
         BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

         PrintStream out = createJavaClass(bufferedReader, sourceFile, eventClassName, eventClassNameWPN, projectName, userName, dialogName, eventNameWithoutTags);
         out = writeAttrGetterAndSetter(attributes, out);

         out.println("}");

         return log.append("Event Dialog " + sourceFile.getAbsolutePath()
               + " created");
      }
   }

   public PrintStream createJavaClass(BufferedReader bufferedReader, File sourceFile, String className, String classNameWithPackge, String projectName, String userName, String dialogName, String eventNameWithoutTags)
         throws FileNotFoundException, JSONException
   {
      String jsonData = GenerationHelper.parseJsonFile(bufferedReader);
      JSONObject obj = new JSONObject(jsonData);

      PrintStream out = new PrintStream(new FileOutputStream(sourceFile));
      GenerationHelper.printFileCopyrightComment(out);

      out.println("package " + GenerationHelper.getPackageName(classNameWithPackge) + ";");

      if (projectName.contains("\\"))
      {
         projectName = projectName.replace("\\", ".");
      }
      //write imports
      writeImports(out, obj, projectName, dialogName,
            GenerationHelper.getSimpleClassName(className), null, false, eventNameWithoutTags);
      out.println();

      GenerationHelper.printClassComment(out, userName);
      out.println();

      writeClassData(out, obj, className, null, dialogName, eventNameWithoutTags);

      return out;
   }

   @Override
   public void writeClassData(PrintStream out, JSONObject obj, String packageNameWithClassName, String viewName, String dialogName, String eventNameWithoutTags) throws JSONException
   {
      String classData;
      classData = "public class " + GenerationHelper.getSimpleClassName(packageNameWithClassName) + "";

      classExtendsObject(obj, viewName);
      classData += " {";
      out.println(classData);
   }
}
