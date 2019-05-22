package com.advansys.generator.createnewstepbystepdialog.services;

import com.advansys.generator.common.GenerationHelper;
import com.advansys.generator.common.GenerationMain;
import org.json.JSONException;

import java.io.*;

/**
 * Created by elbaklna on 6/19/2017.
 */
public class GenerationServiceAsync
{
   private String basePackageDirectory;
   private boolean isGenerateEvent;
   private String serviceAsyncClassName;

   public GenerationServiceAsync(String basePackageDirectory, boolean isGenerateEvent, String serviceAsyncClassName)
   {
      this.basePackageDirectory = basePackageDirectory;
      this.isGenerateEvent = isGenerateEvent;
      this.serviceAsyncClassName = serviceAsyncClassName;
   }

   // create dialog service async
   public StringBuilder createServiceAsyncInterface(StringBuilder createServiceAsyncInterface)
   {
      try {
         File sourceFile = new File(basePackageDirectory + "/client/" + GenerationMain.getDialogName().toLowerCase() + "/service/"
                 + GenerationHelper.getSimpleClassName(serviceAsyncClassName) + ".java");

         if (sourceFile.exists())
         {
            sourceFile.delete();
            GenerationMain.getLOGGER().info(serviceAsyncClassName + " deleted successfully.");
         }
         ParseAndCreateServiceAsync parseAndCreateServiceAsync = new ParseAndCreateServiceAsync();
         InputStream inputStream = GenerationMain.class.getResourceAsStream("../json/ServiceAsync.json");
         if(inputStream == null) {
            GenerationMain.getLOGGER().warning(serviceAsyncClassName + "'s " + "JSON file is empty");
            return createServiceAsyncInterface;
         }

         BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

         parseAndCreateServiceAsync.createFiles(bufferedReader, sourceFile, GenerationMain.getStepViewMap(), GenerationMain.getProjectName(),
                 GenerationMain.getDialogName(), serviceAsyncClassName,
                 GenerationMain.getUserName(), isGenerateEvent);
         GenerationMain.getLOGGER().info("Service Dialog " + sourceFile.getAbsolutePath() + " created");
         GenerationHelper.printLog("Service Dialog " + sourceFile.getAbsolutePath() + " created");

         createServiceAsyncInterface.append("\n service async interface " + sourceFile.getAbsolutePath() + " created");
         return createServiceAsyncInterface;
      }
      catch (FileNotFoundException e){
         GenerationMain.getLOGGER().warning(serviceAsyncClassName + "file was not found" + e.getMessage());
         return null;
      }
      catch (JSONException e){
         GenerationMain.getLOGGER().warning(serviceAsyncClassName + "'s " + "JSON file is not found" + e.getMessage());
         return null;
      }



   }
}
