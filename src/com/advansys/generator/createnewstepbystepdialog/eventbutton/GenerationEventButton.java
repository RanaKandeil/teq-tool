package com.advansys.generator.createnewstepbystepdialog.eventbutton;

import com.advansys.generator.common.GenerationHelper;
import com.advansys.generator.common.GenerationMain;
import org.json.JSONException;

import java.io.*;

/**
 * Created by elbaklna on 6/19/2017.
 */
public class GenerationEventButton
{
   private String basePackageDirectory;
   private boolean isGenerateEvent;
   private String eventClassName;

   public GenerationEventButton(String basePackageDirectory, boolean isGenerateEvent, String eventClassName)
   {
      this.basePackageDirectory = basePackageDirectory;
      this.isGenerateEvent = isGenerateEvent;
      this.eventClassName = eventClassName;
   }

   public StringBuilder createEventButtonClass(StringBuilder createEventButton)
   {
      try {
         File sourceFile = new File(basePackageDirectory + "/client/" + GenerationMain.getDialogName().toLowerCase() + "/event/"
                 + GenerationHelper.getSimpleClassName(eventClassName) + ".java");

         if (sourceFile.exists())
         {
            sourceFile.delete();
            GenerationMain.getLOGGER().info(eventClassName + " deleted successfully.");
            //return createEventButton;
         }
         ParseAndCreateEventButton parseAndCreateEventButton = new ParseAndCreateEventButton();

         InputStream inputStream = GenerationMain.class.getResourceAsStream("../json/EventButton.json");
         if(inputStream == null) {
            GenerationMain.getLOGGER().warning(eventClassName + "'s " + "JSON file is empty");
            return createEventButton;
         }

         BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
         parseAndCreateEventButton.createFiles(bufferedReader, sourceFile, GenerationMain.getStepViewMap(), GenerationMain.getProjectName(),
                 GenerationMain.getDialogName(), eventClassName,
                 GenerationMain.getUserName(), isGenerateEvent);
         GenerationMain.getLOGGER().info("Event button class " + sourceFile.getAbsolutePath() + " created");
         GenerationHelper.printLog("Event button class " + sourceFile.getAbsolutePath() + " created");
         createEventButton.append("\n event button class " + sourceFile.getAbsolutePath() + " created");
         return createEventButton;
      }
      catch (FileNotFoundException e){
         GenerationMain.getLOGGER().warning(eventClassName + "file was not found" + e.getMessage());
         return null;
      }
      catch (JSONException e){
         GenerationMain.getLOGGER().warning(eventClassName + "'s " + "JSON file is not found" + e.getMessage());
         return null;
      }
   }
}
