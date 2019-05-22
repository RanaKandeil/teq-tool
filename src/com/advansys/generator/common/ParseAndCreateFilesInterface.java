package com.advansys.generator.common;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.LinkedHashMap;

/**
 * Created by elbaklna on 6/14/2017.
 */
public interface ParseAndCreateFilesInterface
{
   PrintStream createFiles(BufferedReader jsonFile, File sourceFile, LinkedHashMap<String, String> stepViewMap,
                           String projectName, String dialogName, String packageNameWithClassName,
                           String userName, boolean isGenerateEvent) throws FileNotFoundException, JSONException;

   void writeImports(PrintStream out, JSONObject obj, String projectName, String dialogName,
                     String className, LinkedHashMap<String, String> stepViewMap, boolean isGenerateEvent, String eventNameWithoutTags) throws JSONException;

   void writeClassData(PrintStream out, JSONObject obj, String packageNameWithClassName, String viewName,
                       String dialogName, String eventNameWithoutTags) throws JSONException;

   void writeVariables(PrintStream out, JSONObject obj, String dialogName) throws JSONException;

   void writeMethods(PrintStream out, JSONObject obj, String projectName, String dialogName, String packageNameWithClassName,
                     LinkedHashMap<String, String> stepViewMap, boolean isGenerateEvent) throws JSONException;

   void declareOtherSteps(String stepName, LinkedHashMap<String, String> stepViewMap, PrintStream out);

   String classExtendsObject(JSONObject obj, String viewName) throws JSONException;

   PrintStream writeAttrGetterAndSetter(LinkedHashMap<String, String> attributes, PrintStream out);
}