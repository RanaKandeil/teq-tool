package com.advansys.generator.createnewstepbystepdialog.services;

import com.advansys.generator.common.GenerationHelper;
import com.advansys.generator.common.GenerationMain;
import org.json.JSONException;

import java.io.*;

/**
 * Created by elbaklna on 6/19/2017.
 */
public class GenerationServiceFactory
{
   private String basePackageDirectory;
   private boolean isGenerateEvent;
   private String serviceFactoryClassName;

   public GenerationServiceFactory(String basePackageDirectory, boolean isGenerateEvent, String serviceFactoryClassName)
   {
      this.basePackageDirectory = basePackageDirectory;
      this.isGenerateEvent = isGenerateEvent;
      this.serviceFactoryClassName = serviceFactoryClassName;
   }

   // create dialog service factory
   public StringBuilder createServiceFactory(StringBuilder createServiceFactory)
   {
      try {
         File sourceFile = new File(basePackageDirectory + "/client/" + GenerationMain.getDialogName().toLowerCase() + "/service/"
                 + GenerationHelper.getSimpleClassName(serviceFactoryClassName) + ".java");

         if (sourceFile.exists())
         {
            sourceFile.delete();
            GenerationMain.getLOGGER().info(serviceFactoryClassName + " deleted successfully.");
         }
         ParseAndCreateServiceFactory parseAndCreateServiceFactory = new ParseAndCreateServiceFactory();

         InputStream inputStream = GenerationMain.class.getResourceAsStream("../json/ServiceFactory.json");
         if(inputStream == null) {
            GenerationMain.getLOGGER().warning(serviceFactoryClassName + "'s " + "JSON file is empty");
            return createServiceFactory;
         }

         BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
         parseAndCreateServiceFactory.createFiles(bufferedReader, sourceFile, GenerationMain.getStepViewMap(), GenerationMain.getProjectName(),
                 GenerationMain.getDialogName(), serviceFactoryClassName,
                 GenerationMain.getUserName(), isGenerateEvent);
         GenerationMain.getLOGGER().info("Service Dialog " + sourceFile.getAbsolutePath() + " created");
         GenerationHelper.printLog("Service Dialog " + sourceFile.getAbsolutePath() + " created");

         createServiceFactory.append("\n service factory class " + sourceFile.getAbsolutePath() + " created");
         return createServiceFactory;
      }
      catch (FileNotFoundException e){
         GenerationMain.getLOGGER().warning(serviceFactoryClassName + "file was not found" + e.getMessage());
         return null;
      }
      catch (JSONException e){
         GenerationMain.getLOGGER().warning(serviceFactoryClassName + "'s " + "JSON file is not found" + e.getMessage());
         return null;
      }
   }
}
