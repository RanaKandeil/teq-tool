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
public class ParseAndCreatePushEventWPN extends ParseAndCreateFilesMain
{

   // Create push event class
   public StringBuilder createPushEvent(String pushEventClassName, LinkedHashMap<String, String> attributes, StringBuilder log, String basePackageDirectory, String projectName, String userName, String eventNameWithoutTags, String dialogName, String pushEventClassNameWPN)
         throws FileNotFoundException, JSONException
   {
      File sourceFile = new File(basePackageDirectory + "/client/"
            + dialogName.toLowerCase() + "/" + "service/" + "event/"
            + GenerationHelper.getSimpleClassName(pushEventClassName) + ".java");

      if (sourceFile.exists())
      {
         System.out.println(pushEventClassName + " is already exist");
         return log.append(pushEventClassName + " is already exist");
      }

      InputStream inputStream = getClass().getResourceAsStream("/com/advansys/generator/json/PushEvent.json");
      BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
      PrintStream out = createJavaClass(bufferedReader, sourceFile, pushEventClassName, pushEventClassNameWPN, projectName, userName, dialogName, eventNameWithoutTags);
      out.println("private static final long serialVersionUID = 1L;");

      out.println("   public " + GenerationHelper.upperFirstChar(pushEventClassName) + "() {\n      super();\n   }");

      out.print("   public " + GenerationHelper.upperFirstChar(pushEventClassName) + "(");
      int lenght = 0;
      for (Map.Entry attr : attributes.entrySet())
      {
         if (lenght == attributes.size() - 1)
            out.print(attr.getValue().toString() + " " + GenerationHelper.lowFirstChar(attr.getKey().toString()) + ")");
         else
            out.print(attr.getValue().toString() + " " + GenerationHelper.lowFirstChar(attr.getKey().toString()) + ",");
         lenght++;
      }
      out.print(" {");
      out.println("\n      super(terminalId);");

      for (Map.Entry attr : attributes.entrySet())
      {
         if (attr.getKey().toString().equals("terminalId"))
         {
            lenght++;
            continue;
         }
         out.println("      this." + GenerationHelper.lowFirstChar(attr.getKey().toString()) + " = " +
               GenerationHelper.lowFirstChar(attr.getKey().toString()) + ";");
      }
      out.println("   }");
      out.println();
      attributes.remove("terminalId");
      out = writeAttrGetterAndSetter(attributes, out);
      attributes.put("terminalId", "String");
      out.println("   @Override");

      out.println("   public String toString() {");
      out.print("      return \"" + pushEventClassName + "{\" + \"");
      lenght = 0;
      for (Map.Entry attr : attributes.entrySet())
      {
         if (lenght == attributes.size() - 1)
            out.print(GenerationHelper.lowFirstChar(attr.getKey().toString()) + "=\" + " + GenerationHelper.lowFirstChar(attr.getKey().toString()) + " + \'}\';");
         else
            out.print(GenerationHelper.lowFirstChar(attr.getKey().toString()) + "=\" + " + GenerationHelper.lowFirstChar(attr.getKey().toString()) + " + \", ");
         lenght++;
      }
      out.println("\n   }");
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
      classData += " {";
      out.println(classData);
   }
}
