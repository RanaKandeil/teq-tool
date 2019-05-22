package com.advansys.generator.createnewstepbystepdialog.operationdialog;

import com.advansys.generator.common.GenerationHelper;
import com.advansys.generator.common.GenerationMain;
import com.advansys.utils.FileFormatter;
import com.advansys.utils.FileTransformer;
import org.json.JSONException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by elbaklna on 6/19/2017.
 */
public class GenerationOperationDialog
{

   private String basePackageDirectory;
   private boolean isGenerateEvent;
   private String dialogClassName;
   private File operationFile;
   private String pushEventClassName;
   private String pushEventClassNameWPN;

   public GenerationOperationDialog(String basePackageDirectory, boolean isGenerateEvent, String dialogClassName)
   {
      this.basePackageDirectory = basePackageDirectory;
      this.isGenerateEvent = isGenerateEvent;
      this.dialogClassName = dialogClassName;
   }

   public GenerationOperationDialog(File operationFile, String pushEventClassName, String pushEventClassNameWPN)
   {
      this.operationFile = operationFile;
      this.pushEventClassName = pushEventClassName;
      this.pushEventClassNameWPN = pushEventClassNameWPN;
   }

   // create step by step dialog with the relations between the steps and the views
   public StringBuilder createStepByStepDialog(StringBuilder createStepByStepDialog)
   {
      try {
         File sourceFile = new File(basePackageDirectory + "/client/"
                 + GenerationMain.getDialogName().toLowerCase() + "/"
                 + GenerationHelper.getSimpleClassName(dialogClassName) + ".java");
         GenerationMain.setOperationFile(sourceFile);

         if (sourceFile.exists())
         {
            sourceFile.delete();
            GenerationMain.getLOGGER().info(dialogClassName + " deleted successfully.");
            // return createStepByStepDialog;
         }

         ParseAndCreateOperationDialog parseAndCreateOperationDialog = new ParseAndCreateOperationDialog();

         InputStream inputStream = GenerationMain.class.getResourceAsStream("../json/OperationDialog.json");
         if(inputStream == null) {
            GenerationMain.getLOGGER().warning(dialogClassName + "'s " + "JSON file is empty");
            return createStepByStepDialog;
         }

         BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

         parseAndCreateOperationDialog.createFiles(bufferedReader, sourceFile, GenerationMain.getStepViewMap(), GenerationMain.getProjectName(),
                 GenerationMain.getDialogName(), dialogClassName,
                 GenerationMain.getUserName(), isGenerateEvent);
         GenerationMain.getLOGGER().info("step by step dialog class " + sourceFile.getAbsolutePath() + " created");
         GenerationHelper.printLog("step by step dialog class " + sourceFile.getAbsolutePath() + " created");

         createStepByStepDialog.append(" \n step by step dialog class " + sourceFile.getAbsolutePath() + " created");
         return createStepByStepDialog;
      }
      catch (FileNotFoundException e){
            GenerationMain.getLOGGER().warning(dialogClassName + "file was not found" + e.getMessage());
            return null;
         }
      catch (JSONException e){
            GenerationMain.getLOGGER().warning(dialogClassName + "'s " + "JSON file is not found" + e.getMessage());
            return null;
      }

   }

   public StringBuilder modifyStepByStepDialogClass(String pushEventClassName, StringBuilder log)
   {
      findFile(GenerationMain.getDialogName() + "OperationDialog.java", new File(GenerationMain.getProjectPath()));
      if (operationFile != null)
      {
         convertFileToString(operationFile, "getRemoteEventListener");
         convertFileToString(operationFile, "registerForServerPushEvents");
         convertFileToString(operationFile, "removeServerPushRegistration");
         convertFileToString(operationFile, "onFinalUnload");
         operationFile = null;
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

   @SuppressWarnings("null")
   public String convertFileToString(File file, String methodName)
   {
      int lineCount = 0;
      int importLineCount = 0;
      int classLineCounter = 0;
      boolean found = false;
      boolean importNotFound = true;
      boolean classNotFound = true;

      Path path = Paths.get(file.getPath());

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

      String s = new String();

      for (int i = 0; i < lines.size(); i++)
      {

         s = lines.get(i);

         lineCount++;
         String methodsStringPattern = "(" + methodName + ".*?\\))(.*)";
         String importsStringPattern = "import .*";
         String classStringPattern1 = "class.*" + GenerationMain.getDialogName() + "OperationDialog" + ".*";

         Pattern methodsPattern = Pattern.compile(methodsStringPattern);
         Pattern importsPattern = Pattern.compile(importsStringPattern);
         Pattern classPattern = Pattern.compile(classStringPattern1);

         Matcher methodsMatcher = methodsPattern.matcher(s);
         Matcher importsMatcher = importsPattern.matcher(s);
         Matcher classMatcher = classPattern.matcher(s);

         if (importNotFound)
         {
            importLineCount++;
            if (importsMatcher.find())
            {
               importNotFound = false;
            }
         }
         if (classNotFound)
         {
            classLineCounter++;
            if (classMatcher.find())
            {
               classNotFound = false;
            }
         }
         if (methodsMatcher.find())
         {

            found = true;
            break;
         }
      }
      if (!found)
      {
         if (methodName.equals("registerForServerPushEvents"))
         {

            FileTransformer
                  .addStatementsToClass(
                        file,
                        "   protected void registerForServerPushEvents() {"
                              + "\n"
                              + "      super.registerForServerPushEvents();"
                              + "\n"
                              + "      String terminalId = AppContext.getInstance().getTerminalId();"
                              + "\n"
                              + "      ServerPushManager serverPushManager =AppContext.getInstance().getFrame().getServerPushManager();"
                              + "\n"

                              + "      serverPushManager.addEventListener("
                              + GenerationHelper
                              .upperFirstChar(pushEventClassName)
                              + ".class.getName() + \"_\"+ terminalId,getRemoteEventListener());"
                              + "\n" + "   }\n", 2);

            if (!importNotFound)
            {
               lines.add(importLineCount - 1, "import "
                     + pushEventClassNameWPN + ";");
               lines.add(importLineCount - 1,
                     "import com.dematic.wms.app.base.ui.client.frame.AppContext;");
               lines.add(importLineCount - 1,
                     "import com.dematic.wms.app.base.ui.client.sp.ServerPushManager;");
            }
         }
         if (methodName.equals("getRemoteEventListener"))
         {

            FileTransformer
                  .addStatementsToClass(
                        file,
                        "   protected RemoteEventListener getRemoteEventListener() {"
                              + "\n"
                              + "      if (remoteEventListener == null) {"
                              + "\n"
                              + "         remoteEventListener = new RemoteEventListener() {"
                              + "\n"
                              + "            @Override"
                              + "\n"
                              + "            public void apply(Event anEvent) {"
                              + "\n"
                              + "               if (anEvent instanceof StepEvent) {"
                              + "\n"
                              + "                  addEvent((StepEvent) anEvent);"
                              + "\n" + "               }" + "\n"
                              + "           }" + "\n" + "         };"
                              + "\n" + "      }" + "\n"
                              + "      return remoteEventListener;"
                              + "\n" + "   }\n", 2);
            if (!classNotFound)
            {
               String contentToBeAdded = "protected RemoteEventListener remoteEventListener;";
               checkForCurlyBraces(lines, classLineCounter - 1,
                     contentToBeAdded);
            }
            if (!importNotFound)
            {
               lines.add(importLineCount - 1,
                     "import de.novanic.eventservice.client.event.listener.RemoteEventListener;");
               lines.add(importLineCount - 1,
                     "import de.novanic.eventservice.client.event.Event;");
               lines.add(importLineCount - 1,
                     "import com.dematic.wms.app.base.ui.shared.dialog.steps.common.StepEvent;");
            }
         }
         if (methodName.equals("removeServerPushRegistration"))
         {

            FileTransformer
                  .addStatementsToClass(
                        file,
                        "   public void removeServerPushRegistration() {"
                              + "\n"
                              + "      String terminalId = AppContext.getInstance().getTerminalId();"
                              + "\n"
                              + "      ServerPushManager serverPushManager =AppContext.getInstance().getFrame().getServerPushManager();"
                              + "\n"
                              + "      serverPushManager.removeEventListener("
                              + GenerationHelper
                              .upperFirstChar(pushEventClassName)
                              + ".class.getName() + \"_\"+ terminalId,getRemoteEventListener());"
                              + "\n" + "   }\n", 2);
         }
         if (methodName.equals("onFinalUnload"))
         {

            FileTransformer.addStatementsToClass(file,
                  "   protected void onFinalUnload() {" + "\n"
                        + "      removeServerPushRegistration();"
                        + "\n" + "   }", 2);
         }
      }
      else
      {
         if (methodName.equals("registerForServerPushEvents"))
         {
            int index = 0;
            boolean isFound = false;
            for (String line : lines)
            {
               if (line.contains("serverPushManager.addEventListener"))
               {
                  index = lines.indexOf(line);
                  isFound = true;
                  break;
               }
            }
            if (isFound)
            {
               if (!importNotFound)
               {
                  lines.add(importLineCount - 1, "import "
                        + pushEventClassNameWPN + ";");
               }
               lines.add(
                     index + 1,
                     "      serverPushManager.addEventListener("
                           + GenerationHelper
                           .upperFirstChar(pushEventClassName)
                           + ".class.getName() + \"_\" + terminalId, getRemoteEventListener());");
            }
         }
         if (methodName.equals("removeServerPushRegistration"))
         {
            int index = 0;
            boolean isFound = false;
            for (String line : lines)
            {
               if (line.contains("serverPushManager.removeEventListener"))
               {
                  index = lines.indexOf(line);
                  isFound = true;
                  break;
               }
            }
            if (isFound)
            {
               lines.add(
                     index,
                     "      serverPushManager.removeEventListener("
                           + GenerationHelper
                           .upperFirstChar(pushEventClassName)
                           + ".class.getName() +\"_\" + terminalId, getRemoteEventListener());");
            }
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
      return s;
   }

   private void checkForCurlyBraces(List<String> lines, int lineCount,
                                    String contentToBeWritten)
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
