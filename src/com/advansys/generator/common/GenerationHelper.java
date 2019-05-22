package com.advansys.generator.common;

import java.io.*;
import java.util.Calendar;

/**
 * Created by elbaklna on 6/21/2017.
 */
public class GenerationHelper
{
   // get the simple class name (ex. get "LoadUnitGuideOperationDialog" from "com.dematic.laredoute.ui.client.loadunitguide.LoadUnitGuideOperationDialog")
   public static String getSimpleClassName(String className)
   {
      return className.substring(className.lastIndexOf('.') + 1);
   }

   /**
    * edit the first char from the variable name to be a lower case
    *
    * @param varName the variable name
    */
   public static String lowFirstChar(String varName)
   {
      return varName.substring(0, 1).toLowerCase() + varName.substring(1);
   }

   /**
    * edit the first char from the variable name to be a lower case
    *
    * @param varName the variable name
    */
   public static String upperFirstChar(String varName)
   {
      return varName.substring(0, 1).toUpperCase() + varName.substring(1);
   }

   public static void printLog(String out)
   {
      GenerationMain.getGeneratedFileWriter().println(out);
   }

   public static PrintWriter printLog(String out, PrintWriter generatedFileWriter)
   {
      generatedFileWriter.println(out);
      return generatedFileWriter;
   }

   public static PrintWriter createLogFile(String projectPath, String textFileName)
   {
      try
      {
         GenerationMain.setGeneratedFile(new File(projectPath + "/" + textFileName));
         GenerationMain.setGeneratedFileWriter(new PrintWriter(GenerationMain.getGeneratedFile(), "UTF-8"));
      }
      catch (IOException e)
      {
         // do something
         throw new RuntimeException(e);
      }
      return GenerationMain.getGeneratedFileWriter();
   }

   public static String parseJsonFile(BufferedReader br)
   {
      String jsonData = "";
      try
      {
         String line;
         while ((line = br.readLine()) != null)
         {
            jsonData += line + "\n";
         }
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
      finally
      {
         try
         {
            if (br != null)
               br.close();
         }
         catch (IOException ex)
         {
            ex.printStackTrace();
         }
      }
      return jsonData;
   }

   /**
    * print file copyrights
    *
    * @param out the print stream to be able to write in the file
    */
   public static void printFileCopyrightComment(PrintStream out)
   {
      int year = Calendar.getInstance().get(Calendar.YEAR);
      out.println("/**");
      out.println(" * Copyright (c) Advansys-ESC " + year
            + ". All rights reserved. Confidential.");
      out.println(" */");
   }

   /**
    * print author name in the file
    *
    * @param out the print stream to be able to write in the file
    */
   public static void printClassComment(PrintStream out, String userName)
   {
      out.println("/**");
      out.println(" * @author " + userName);
      out.println(" */");
   }

   // get the package name (ex. get "com.dematic.laredoute.ui.client.loadunitguide" from "com.dematic.laredoute.ui.client.loadunitguide.LoadUnitGuideOperationDialog")
   public static String getPackageName(String className)
   {
      return className.substring(0, className.lastIndexOf('.'));
   }
}
