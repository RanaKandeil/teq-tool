package com.advansys.generator.createnewpushevent;

import com.advansys.generator.common.GenerationHelper;
import com.advansys.generator.common.ParseAndCreateFilesMain;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by elbaklna on 6/28/2017.
 */
public class ParseAndCreateEventMapperWPN extends ParseAndCreateFilesMain
{

   public StringBuilder createEventMapper(String eventMapperClassName, LinkedHashMap<String, String> attributes, StringBuilder log, String basePackageDirectory, String projectName, String userName, String dialogName, String eventMapperClassNameWPN, String eventNameWithTags, String pushEventClassName, String eventNameWithoutTags)
         throws FileNotFoundException, JSONException
   {
      File sourceFile = new File(basePackageDirectory + "/server/"
            + dialogName.toLowerCase() + "/mappers/"
            + GenerationHelper.getSimpleClassName(eventMapperClassName) + ".java");

      if (sourceFile.exists())
      {
         System.out.println(eventMapperClassName + " is already exist");
         return log.append(eventMapperClassName + " is already exist");
      }

      InputStream inputStream = getClass().getResourceAsStream("/com/advansys/generator/json/EventMapper.json");
      BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

      PrintStream out = createJavaClass(bufferedReader, sourceFile, eventMapperClassName, eventMapperClassNameWPN, projectName, userName, dialogName, eventNameWithoutTags);
      out.println("@Override");
      out.println("public " + pushEventClassName + " map(" + eventNameWithTags + " serverEvent){");
      out.print("return new " + pushEventClassName + "(");
      int lenght = 0;
      for (Map.Entry attr : attributes.entrySet())
      {
         if (lenght == attributes.size() - 1)
            out.print("serverEvent.get" + GenerationHelper.upperFirstChar(attr.getKey().toString()) + "());");
         else
            out.print("serverEvent.get" + GenerationHelper.upperFirstChar(attr.getKey().toString()) + "(), ");
         lenght++;
      }
      out.println("\n   }");
      out.println();
      out.println("   @Override");
      out.println("   public List<String> getDomains(" + eventNameWithTags + " serverEvent){");
      out.println("      String domainId = " + pushEventClassName + ".class.getName() + \"_\" + serverEvent.getTerminalId();");
      out.println("      return Arrays.asList(domainId);");
      out.println("   }");
      out.println("}");
      return log;
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
      if (obj.getJSONObject("classImplements") != null && obj.getJSONObject("classImplements").has("classImplementsInterfaces"))
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
      classData += " {";
      out.println(classData);
   }
}
