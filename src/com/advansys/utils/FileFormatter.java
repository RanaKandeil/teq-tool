package com.advansys.utils;

import org.apache.commons.io.FileUtils;
import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.TextEdit;

import java.io.File;
import java.io.PrintWriter;

public class FileFormatter
{
   /*
    * this function formats a non formated java class noting that if you're
    * using \ as an escape character it should be written as \\ in the file
    * otherwise an exception will be thrown eclipse jdt library has a bug as it
    * fails to format any text with @ charachter so we will convert any @ to a
    * comment before making the formatting and then revert it again
    */
   public static void formatUnformattedClass(File classFile)
   {
      try
      {
         String unformattedClassBody = FileUtils.readFileToString(classFile);
         unformattedClassBody = unformattedClassBody.replaceAll("@", "//@");
         System.out.println(unformattedClassBody);
         CodeFormatter codeFormatter = ToolFactory.createCodeFormatter(null);

         TextEdit textEdit = codeFormatter.format(CodeFormatter.K_COMPILATION_UNIT, unformattedClassBody, 0,
               unformattedClassBody.length(), 0, null);
         IDocument doc = new Document(unformattedClassBody);

         textEdit.apply(doc);

         PrintWriter out = new PrintWriter(classFile);
         String formattedClassBody = doc.get().replace("//@", "@");
         out.write(formattedClassBody);
         out.flush();
         out.close();
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   public static void formatClassText(String unformattedClassBody, File outputFile)
   {
      // String code = "public class TestFormatter{public static void
      // main(String[] args){System.out.println(\"Hello World 1\");}}";
      CodeFormatter codeFormatter = ToolFactory.createCodeFormatter(null);

      TextEdit textEdit = codeFormatter.format(CodeFormatter.K_COMPILATION_UNIT, unformattedClassBody, 0,
            unformattedClassBody.length(), 0, null);
      IDocument doc = new Document(unformattedClassBody);
      try
      {
         textEdit.apply(doc);
         // System.out.println(doc.get());

         PrintWriter out = new PrintWriter(outputFile);
         out.write(doc.get());
         out.flush();
         out.close();
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   public static void main(String args[])
   {
      File outFile = new File("C:/Users/asharafs/Desktop/formatted.java");
      // String code = "public class TestFormatter{public static void
      // main(String[] args){System.out.println(\"Hello World\");}}";
      // FileFormatter.formatClassText(code, outFile);
      FileFormatter.formatUnformattedClass(outFile);
   }
}