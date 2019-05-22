package com.advansys.generator.createnewstepbystepdialog.services;

import com.advansys.generator.common.GenerationHelper;
import com.advansys.generator.common.GenerationMain;
import org.json.JSONException;

import java.io.*;

/**
 * Created by elbaklna on 6/19/2017.
 */
public class GenerationServiceImpl
{
   private String basePackageDirectory;
   private boolean isGenerateEvent;
   private String serviceImplClassName;

   public GenerationServiceImpl(String basePackageDirectory, boolean isGenerateEvent, String serviceImplClassName)
   {
      this.basePackageDirectory = basePackageDirectory;
      this.isGenerateEvent = isGenerateEvent;
      this.serviceImplClassName = serviceImplClassName;
   }

   // create dialog service impl
   public StringBuilder createServiceImpl(StringBuilder createServiceImpl)
   {
      try {
         File sourceFile = new File(basePackageDirectory + "/server/" + GenerationMain.getDialogName().toLowerCase() + "/service/"
                 + GenerationHelper.getSimpleClassName(serviceImplClassName) + ".java");

         if (sourceFile.exists())
         {
            sourceFile.delete();
            GenerationMain.getLOGGER().info(serviceImplClassName + " deleted successfully.");
         }
         ParseAndCreateServiceImpl parseAndCreateServiceImpl = new ParseAndCreateServiceImpl();

         InputStream inputStream = GenerationMain.class.getResourceAsStream("../json/ServiceImpl.json");
         if(inputStream == null) {
            GenerationMain.getLOGGER().warning(serviceImplClassName + "'s " + "JSON file is empty");
            return createServiceImpl;
         }

         BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

         parseAndCreateServiceImpl.createFiles(bufferedReader, sourceFile, GenerationMain.getStepViewMap(), GenerationMain.getProjectName(),
                 GenerationMain.getDialogName(), serviceImplClassName,
                 GenerationMain.getUserName(), isGenerateEvent);
         GenerationMain.getLOGGER().info("Service Dialog " + sourceFile.getAbsolutePath() + " created");
         GenerationHelper.printLog("Service Dialog " + sourceFile.getAbsolutePath() + " created");
         createServiceImpl.append("\n service impl class " + sourceFile.getAbsolutePath() + " created");
         return createServiceImpl;
      }
      catch (FileNotFoundException e){
         GenerationMain.getLOGGER().warning(serviceImplClassName + "file was not found" + e.getMessage());
         return null;
      }
      catch (JSONException e){
         GenerationMain.getLOGGER().warning(serviceImplClassName + "'s " + "JSON file is not found" + e.getMessage());
         return null;
      }
   }
}
