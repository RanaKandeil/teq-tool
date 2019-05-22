package com.advansys.generator.common;

import org.json.JSONException;

import java.io.FileNotFoundException;
import java.util.LinkedHashMap;

/**
 * Created by elbaklna on 6/19/2017.
 */
public interface GenerationInterface
{
   void createNewPushEvent(String projectPath, String projectName,
                           String userName, String dialogName,
                           String stepClassName, String eventClassName,
                           LinkedHashMap<String, String> attributes,
                           GenerationType generationType) throws FileNotFoundException, JSONException;

   StringBuilder createNewStepByStepDialog(String projectPath, String projectName,
                                           String dialogName, LinkedHashMap<String, String> stepViewMap, String userName, boolean isGeneratedEvent,
                                           GenerationType generationType) throws FileNotFoundException,
         RuntimeException, JSONException;

   StringBuilder createPackages(StringBuilder createPackages, GenerationType type);

   void preparePackageNames(GenerationType type);
}
