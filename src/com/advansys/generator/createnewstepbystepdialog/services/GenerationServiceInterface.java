package com.advansys.generator.createnewstepbystepdialog.services;

import com.advansys.generator.common.GenerationHelper;
import com.advansys.generator.common.GenerationMain;
import org.json.JSONException;

import java.io.*;

/**
 * Created by elbaklna on 6/19/2017.
 */
public class GenerationServiceInterface
{
   private String basePackageDirectory;
   private boolean isGenerateEvent;
   private String serviceClassName;

   public GenerationServiceInterface(String basePackageDirectory, boolean isGenerateEvent, String serviceClassName)
   {
      this.basePackageDirectory = basePackageDirectory;
      this.isGenerateEvent = isGenerateEvent;
      this.serviceClassName = serviceClassName;
   }

   // create dialog service
   public StringBuilder createServiceInterface(StringBuilder createServiceInterface)
   {
      try {
         File sourceFile = new File(basePackageDirectory + "/client/" + GenerationMain.getDialogName().toLowerCase() + "/service/"
                 + GenerationHelper.getSimpleClassName(serviceClassName) + ".java");

         if (sourceFile.exists())
         {
            sourceFile.delete();
            GenerationMain.getLOGGER().info(serviceClassName + " deleted successfully.");
         }
         ParseAndCreateServiceInterface parseAndCreateServiceInterface = new ParseAndCreateServiceInterface();

         InputStream inputStream = GenerationMain.class.getResourceAsStream("../json/ServiceInterface.json");
         if(inputStream == null) {
            GenerationMain.getLOGGER().warning(serviceClassName + "'s " + "JSON file is empty");
            return createServiceInterface;
         }

         BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

         parseAndCreateServiceInterface.createFiles(bufferedReader, sourceFile, GenerationMain.getStepViewMap(), GenerationMain.getProjectName(),
                 GenerationMain.getDialogName(), serviceClassName,
                 GenerationMain.getUserName(), isGenerateEvent);
         GenerationMain.getLOGGER().info("ServiceAsync Dialog " + sourceFile.getAbsolutePath() + " created");
         GenerationHelper.printLog("ServiceAsync Dialog " + sourceFile.getAbsolutePath() + " created");

         createServiceInterface.append(" \n service interface " + sourceFile.getAbsolutePath() + " created");
         return createServiceInterface;
      }
      catch (FileNotFoundException e){
         GenerationMain.getLOGGER().warning(serviceClassName + "file was not found" + e.getMessage());
         return null;
      }
      catch (JSONException e){
         GenerationMain.getLOGGER().warning(serviceClassName + "'s " + "JSON file is not found" + e.getMessage());
         return null;
      }
   }
}
