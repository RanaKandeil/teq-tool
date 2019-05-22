package com.advansys.generator.common;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by elbaklna on 6/14/2017.
 */
public class ParseAndCreateFilesMain implements ParseAndCreateFilesInterface
{
   @Override
   public PrintStream createFiles(BufferedReader jsonFile, File sourceFile, LinkedHashMap<String, String> stepViewMap, String projectName, String dialogName, String packageNameWithClassName, String userName, boolean isGenerateEvent) throws FileNotFoundException, JSONException
   {
      return null;
   }

   @Override
   public void writeImports(PrintStream out, JSONObject obj, String projectName, String dialogName, String className, LinkedHashMap<String, String> stepViewMap, boolean isGenerateEvent, String eventNameWithoutTags) throws JSONException
   {
      if (obj.getJSONObject("imports") != null && obj.getJSONObject("imports").has("classImport"))
      {
         int importsLength = obj.getJSONObject("imports").getJSONArray("classImport").length();
         for (int i = 0; i < importsLength; i++)
         {
            System.out.println(obj.getJSONObject("imports").getJSONArray("classImport").getJSONObject(i).getString("importString"));
            String replacedString = obj.getJSONObject("imports").getJSONArray("classImport").getJSONObject(i)
                  .getString("importString");
            if (replacedString.contains("projectName"))
            {
               replacedString = replacedString.replace("projectName", projectName.toLowerCase());
               if (replacedString.contains("dialogName"))
               {
                  replacedString = replacedString.replace("dialogName", dialogName.toLowerCase());
                  if (replacedString.contains("serviceDialogName"))
                  {
                     replacedString = replacedString.replace("serviceDialogName", dialogName + "Service");
                  }
                  else if (replacedString.contains("eventNameEvent"))
                  {
                     replacedString = replacedString.replace("eventName", GenerationHelper.upperFirstChar(eventNameWithoutTags));
                  }
                  else if (replacedString.contains("eventNamePushEvent"))
                  {
                     replacedString = replacedString.replace("eventName", GenerationHelper.upperFirstChar(eventNameWithoutTags));
                  }
               }
            }
            out.println(replacedString);
         }
      }
      if (isGenerateEvent)
      {
         List<String> keySet = new ArrayList<String>(stepViewMap.keySet());
         if (keySet.get(0).equals(className))
         {
            out.println("import com.dematic." + projectName.toLowerCase() + ".ui.client." + dialogName.toLowerCase()
                  + ".event.SwitchToOtherStepEvent;");
         }
      }
   }

   @Override
   public void writeClassData(PrintStream out, JSONObject obj, String packageNameWithClassName, String viewName, String dialogName, String eventNameWithoutTags) throws JSONException
   {

   }

   @Override
   public void writeVariables(PrintStream out, JSONObject obj, String dialogName) throws JSONException
   {
      if (obj.getString("variables") != null && !obj.getString("variables").equals("null") && !obj.getString("variables")
            .isEmpty())
      {
         String var = obj.getString("variables");
         out.println(var.replace("dialogName", dialogName));
      }
   }

   @Override
   public void writeMethods(PrintStream out, JSONObject obj, String projectName, String dialogName, String packageNameWithClassName, LinkedHashMap<String, String> stepViewMap, boolean isGenerateEvent) throws JSONException
   {
      if (GenerationHelper.getSimpleClassName(packageNameWithClassName).contains("Step"))
      {
         declareOtherSteps(GenerationHelper.getSimpleClassName(packageNameWithClassName), stepViewMap, out);
      }
      if (obj.getJSONObject("methods") != null)
      {
         int methodsLength = obj.getJSONObject("methods").getJSONArray("methodCharacters").length();
         // String method = "";
         for (int i = 0; i < methodsLength; i++)
         {
            String replaced;
            if (obj.getJSONObject("methods").getJSONArray("methodCharacters").getJSONObject(i).getString("override") != null
                  && !obj.getJSONObject("methods").getJSONArray("methodCharacters").getJSONObject(i).getString("override")
                  .equals(""))
            {
               out.println(obj.getJSONObject("methods").getJSONArray("methodCharacters").getJSONObject(i).getString("override"));
            }

            if (obj.getJSONObject("methods").getJSONArray("methodCharacters").getJSONObject(i).getString("methodNameSuffix")
                  .contains("nameOfview"))
            {

               replaced = obj.getJSONObject("methods").getJSONArray("methodCharacters").getJSONObject(i).getString(
                     "methodNameSuffix");
               replaced = replaced.replace("nameOfview", GenerationHelper.getSimpleClassName(packageNameWithClassName));
            }
            else if (obj.getJSONObject("methods").getJSONArray("methodCharacters").getJSONObject(i).getString("methodNameSuffix")
                  .contains("constructor"))
            {
               replaced = obj.getJSONObject("methods").getJSONArray("methodCharacters").getJSONObject(i)
                     .getString("methodNameSuffix");
               replaced = replaced.replace("constructor", GenerationHelper.getSimpleClassName(packageNameWithClassName));
            }
            else
            {
               replaced = obj.getJSONObject("methods").getJSONArray("methodCharacters").getJSONObject(i)
                     .getString("methodNameSuffix");
            }
            String returned = obj.getJSONObject("methods").getJSONArray("methodCharacters").getJSONObject(i)
                  .getString("returnType");
            if (obj.getJSONObject("methods").getJSONArray("methodCharacters").getJSONObject(i).getString("returnType").contains(
                  "ServiceAsync"))
            {
               returned = returned.replace("dialogName", dialogName);
            }
            if (obj.getJSONObject("methods").getJSONArray("methodCharacters").getJSONObject(i).getString("returnType").contains(
                  "nameOfview"))
            {
               returned = returned.replace("nameOfview", GenerationHelper.getSimpleClassName(packageNameWithClassName));
            }
            out.println(obj.getJSONObject("methods").getJSONArray("methodCharacters").getJSONObject(i).getString("accessModifier")
                  + " "
                  + returned + " "
                  + replaced
                  + " "
                  + "{");

            if (obj.getJSONObject("methods").getJSONArray("methodCharacters").getJSONObject(i).getString("methodContent") != null
                  && !obj.getJSONObject("methods").getJSONArray("methodCharacters").getJSONObject(i).getString("methodContent")
                  .equals(""))
            {

               if (obj.getJSONObject("methods").getJSONArray("methodCharacters").getJSONObject(i)
                     .getString("methodContent").contains("dialogName"))
               {
                  out.println(obj.getJSONObject("methods").getJSONArray("methodCharacters").getJSONObject(i)
                        .getString("methodContent").replace("dialogName", dialogName));
               }
               else if (obj.getJSONObject("methods").getJSONArray("methodCharacters").getJSONObject(i)
                     .getString("methodContent").contains("nextStep"))
               {
                  if (isGenerateEvent)
                  {
                     List<String> keySet = new ArrayList<String>(stepViewMap.keySet());
                     if (keySet.get(0).equals(GenerationHelper.getSimpleClassName(packageNameWithClassName)))
                     {
                        if (keySet.size() > 1)
                        {
                           out.println(obj.getJSONObject("methods").getJSONArray("methodCharacters").getJSONObject(i)
                                 .getString("methodContent").replace("nextStep", GenerationHelper.lowFirstChar(keySet.get(1))));
                        }
                        else
                        {
                           out.println(obj.getJSONObject("methods").getJSONArray("methodCharacters").getJSONObject(i)
                                 .getString("methodContent").replace("nextStep", "this"));
                        }
                     }
                  }
               }
               else if (obj.getJSONObject("methods").getJSONArray("methodCharacters").getJSONObject(i)
                     .getString("methodContent").contains("eventButton"))
               {
                  if (isGenerateEvent)
                  {
                     List<String> keySet = new ArrayList<String>(stepViewMap.keySet());
                     if (keySet.get(0).equals(GenerationHelper.getSimpleClassName(packageNameWithClassName)))
                     {
                        out.println(obj.getJSONObject("methods").getJSONArray("methodCharacters").getJSONObject(i)
                              .getString("methodContent").replace("eventButton", ""));
                     }
                  }
               }
               else
               {
                  out.println(obj.getJSONObject("methods").getJSONArray("methodCharacters").getJSONObject(i)
                        .getString("methodContent"));
               }
            }
            if (obj.getJSONObject("methods").getJSONArray("methodCharacters").getJSONObject(i).getString("returnValue") != null
                  && !obj.getJSONObject("methods").getJSONArray("methodCharacters").getJSONObject(i).getString("returnValue")
                  .equals(""))
            {
               out.println(obj.getJSONObject("methods").getJSONArray("methodCharacters").getJSONObject(i).getString("returnValue"));
            }
            out.println("   }");
            out.println();
         }
      }
   }

   @Override
   public String classExtendsObject(JSONObject obj, String viewName) throws JSONException
   {
      String classData = "";
      if ( obj.getJSONObject("classExtends") != null && obj.getJSONObject("classExtends").has("classExtendsInterfaces"))
      {

         classData += " extends ";
         int extendsLength = obj.getJSONObject("classExtends").getJSONArray("classExtendsInterfaces").length();
         for (int i = 0; i < extendsLength; i++)
         {
            if (i == extendsLength - 1)
            {
               String replaced = obj.getJSONObject("classExtends").getJSONArray("classExtendsInterfaces").getString(i);

               if (replaced.contains("nameOfview"))
               {
                  classData += replaced.replace("nameOfview", viewName);
               }
               else
               {
                  classData += obj.getJSONObject("classExtends").getJSONArray("classExtendsInterfaces").get(i);
               }
            }
            else
            {
               classData += obj.getJSONObject("classExtends").getJSONArray("classExtendsInterfaces").get(i) + ", ";
            }
         }
      }
      return classData;
   }

   @Override
   public void declareOtherSteps(String stepName, LinkedHashMap<String, String> stepViewMap, PrintStream out)
   {
      for (String otherStep : stepViewMap.keySet())
      {
         if (!stepName.equals(otherStep))
         {
            out.println("   protected " + otherStep + " " + GenerationHelper.lowFirstChar(otherStep) + ";");
            out.println();
         }
      }
   }

   @Override
   public PrintStream writeAttrGetterAndSetter(LinkedHashMap<String, String> attributes, PrintStream out)
   {
      for (Map.Entry stepViewEntry : attributes.entrySet())
      {
         out.println("   private " + stepViewEntry.getValue().toString() + " " + GenerationHelper.lowFirstChar(stepViewEntry.getKey().toString())
               + ";");
         out.println();
         out.println("   public " + stepViewEntry.getValue().toString() + " get" + GenerationHelper.upperFirstChar(stepViewEntry.getKey().toString()
               + "(){"));
         out.println("      return " + GenerationHelper.lowFirstChar(stepViewEntry.getKey().toString()) + ";");
         out.println("   }");
         out.println();
         out.println("   public void set" + GenerationHelper.upperFirstChar(stepViewEntry.getKey().toString() + "(" +
               stepViewEntry.getValue().toString() + " " + GenerationHelper.lowFirstChar(stepViewEntry.getKey().toString()) + "){"));
         out.println("      this." + GenerationHelper.lowFirstChar(stepViewEntry.getKey().toString()) + " = " +
               GenerationHelper.lowFirstChar(stepViewEntry.getKey().toString()) + ";");
         out.println("   }");
         out.println();
      }
      return out;
   }
}
