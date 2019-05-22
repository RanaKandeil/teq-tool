package com.advansys.generator.createnewstepbystepdialog.steps;

import com.advansys.generator.common.GenerationHelper;
import com.advansys.generator.common.GenerationMain;
import com.advansys.utils.FileFormatter;
import org.json.JSONException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by elbaklna on 6/19/2017.
 */
public class GenerationStep
{
   private String basePackageDirectory;
   private boolean isGenerateEvent;
   private String stepClassName;
   private File operationFile;
   private String pushEventClassName;
   private String pushEventClassNameWPN;

   public GenerationStep(String basePackageDirectory, boolean isGenerateEvent,
                         String stepClassName)
   {
      this.basePackageDirectory = basePackageDirectory;
      this.isGenerateEvent = isGenerateEvent;
      this.stepClassName = stepClassName;
   }

   public GenerationStep(File operationFile, String pushEventClassName,
                         String pushEventClassNameWPN)
   {
      this.operationFile = operationFile;
      this.pushEventClassName = pushEventClassName;
      this.pushEventClassNameWPN = pushEventClassNameWPN;
   }

   //create steps classes
   public StringBuilder createStepClasses(StringBuilder sb)
   {
      try {
         for (String stepName : GenerationMain.getStepViewMap().keySet())
         {
            createStepJavaClass(stepName, GenerationMain.getStepViewMap(), sb);
         }
         return sb;
      }
      catch (JSONException e){
         GenerationMain.getLOGGER().warning(stepClassName + "'s " + "JSON file is not found" + e.getMessage());
         return null;
      }

   }

   private StringBuilder createStepJavaClass(String stepName, LinkedHashMap<String, String> stepViewMap,
                                             StringBuilder createStepJavaClass)
   {
      try {
         File sourceFile = new File(basePackageDirectory + "/client/"
                 + GenerationMain.getDialogName().toLowerCase() + "/" + "steps/" + GenerationHelper.getSimpleClassName(stepName)
                 + ".java");

         if (sourceFile.exists())
         {
            sourceFile.delete();
            GenerationMain.getLOGGER().info(stepName + " deleted successfully.");
         }
            ParseAndCreateStep parseAndCreateStep = new ParseAndCreateStep();

            InputStream inputStream = GenerationMain.class.getResourceAsStream("../json/Step.json");
            if(inputStream == null) {
               GenerationMain.getLOGGER().warning(stepName + "'s " + "JSON file is empty");
               return createStepJavaClass;
            }

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            PrintStream out = parseAndCreateStep.createFiles(bufferedReader, sourceFile, stepViewMap, GenerationMain.getProjectName(),
                    GenerationMain.getDialogName(), stepClassName + stepName,
                    GenerationMain.getUserName(), isGenerateEvent);
            overrideGetInstructionKey(out, stepName);
            createOtherStepsSetters(stepName, stepViewMap, out);
            out.println("}");

            out.close();
            GenerationMain.getLOGGER().info("step class " + sourceFile.getAbsolutePath() + " created");
            GenerationHelper.printLog("step class " + sourceFile.getAbsolutePath() + " created");
            createStepJavaClass.append("\n step class " + sourceFile.getAbsolutePath() + " created");

         return createStepJavaClass;
      }
      catch (FileNotFoundException e){
         GenerationMain.getLOGGER().warning(stepName + "file was not found" + e.getMessage());
         return null;
      }
      catch (JSONException e){
         GenerationMain.getLOGGER().warning(stepName + "'s " + "JSON file is not found" + e.getMessage());
         return null;
      }

   }

   private void overrideGetInstructionKey(PrintStream out, String stepName)
   {
      out.println("   @Override");
      out.println("   public String getInstructionKey() {");
      out.println("      return \"" + GenerationHelper.lowFirstChar(stepName) + "\";");
      out.println("   }");
      out.println();
   }

   private void createOtherStepsSetters(String stepName, LinkedHashMap<String, String> stepViewMap, PrintStream out)
   {
      for (String otherStep : stepViewMap.keySet())
      {
         if (!stepName.equals(otherStep))
         {
            out.println("   public void set" + otherStep + "(" + otherStep + " " + GenerationHelper.lowFirstChar(otherStep) + ") {");
            out.println("      this." + GenerationHelper.lowFirstChar(otherStep) + " = " + GenerationHelper.lowFirstChar(otherStep) + ";");
            out.println("   }");
            out.println();
         }
      }
   }

   public StringBuilder modifyStepClass(String stepClassName, StringBuilder log)
   {
      findFile(stepClassName + ".java", new File(GenerationMain.getProjectPath()));
      if (operationFile != null)
      {
         processEventEdit(operationFile);
      }
      return log;
   }

   public void findFile(String name, File file)
   {
      File[] list = file.listFiles();
      if (list != null)
         for (File fil : list)
         {
            if (fil.isDirectory())
            {
               findFile(name, fil);
            }
            else if (name.equalsIgnoreCase(fil.getName()))
            {
               operationFile = fil;
            }
         }
   }

   public void processEventEdit(File file)
   {
      Path path = Paths.get(file.getPath());
      String s;
      int lineCount = 0;
      int importLineCount = 0;
      boolean importNotFound = true;
      List<String> lines = null;

      try
      {
         lines = Files.readAllLines(path, StandardCharsets.UTF_8);
      }
      catch (IOException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }

      for (int i = 0; i < lines.size(); i++)
      {
         s = lines.get(i);
         lineCount++;
         String methodStringPattern1 = "processEvent.*";

         Pattern methodPattern1 = Pattern.compile(methodStringPattern1);

         Matcher methodMatcher1 = methodPattern1.matcher(s);
         ;

         String importsStringPattern = "import .*";
         Pattern importsPattern = Pattern.compile(importsStringPattern);
         Matcher importsMatcher = importsPattern.matcher(s);

         if (importNotFound)
         {
            importLineCount++;
            if (importsMatcher.find())
            {
               importNotFound = false;
            }
         }

         if (methodMatcher1.find())
         {
            if (!importNotFound)
            {
               lines.add(importLineCount - 1, "import " + pushEventClassNameWPN + ";");
            }
            String contentToBeWritten = "      if (event instanceof " + GenerationHelper.upperFirstChar(pushEventClassName) + ") {}";
            checkForCurlyBraces(lines, lineCount, contentToBeWritten);
            break;
         }
      }

      try
      {
         Files.write(path, lines, StandardCharsets.UTF_8);
         FileFormatter.formatUnformattedClass(file);
      }
      catch (IOException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }

   private void checkForCurlyBraces(List<String> lines, int lineCount, String contentToBeWritten)
   {
      if (lines.get(lineCount).contains("{"))
      {
         lines.add(lineCount + 1, contentToBeWritten);
      }
      else
      {
         while (!lines.get(lineCount).contains("{"))
         {
            lineCount++;
         }
         lines.add(lineCount + 1, contentToBeWritten);
      }
   }
}