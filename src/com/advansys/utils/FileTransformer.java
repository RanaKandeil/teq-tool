package com.advansys.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.github.javaparser.ast.expr.Expression;
import org.apache.commons.io.FileUtils;

import com.advansys.defines.Defines;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.visitor.GenericVisitor;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FileTransformer
{
   public static void modifyMethodBody(File file, String methodName, List<String> contentToAdd, int modifyOption, File outputFile)
   {
      // creates an input stream for the file to be parsed
      // File file = new File("src/main/java/com/dematic/auc/javaparser");
      // for (String fileNames : file.list())
      // System.out.println(fileNames);
      try
      {
         FileInputStream in = new FileInputStream(file);

         // parse the file
         CompilationUnit cu = JavaParser.parse(in);

         // visit and print the methods names
         MethodVisitor methodModifier = new MethodVisitor(methodName, contentToAdd, modifyOption);
         methodModifier.visit(cu, null);

         PrintWriter out = new PrintWriter(outputFile);
         out.write(cu.toString());
         out.flush();
         out.close();
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   /**
    * Simple visitor implementation for visiting MethodDeclaration nodes.
    */
   private static class MethodVisitor extends VoidVisitorAdapter<Void>
   {
      String methodName = null;
      List<String> contentToAdd = null;
      int options;

      public MethodVisitor(String methodName, List<String> contentToAdd, int options)
      {
         this.methodName = methodName;
         this.contentToAdd = contentToAdd;
         this.options = options;
      }

      @Override
      public void visit(MethodDeclaration n, Void arg)
      {
         BlockStmt newMethodBody = new BlockStmt();
         /*
          * here you can access the attributes of the method. this method
			 * will be called for all methods in this CompilationUnit, including
			 * inner class methods
			 */
         if (n.getName().toString().equals(methodName))
         {
            Optional<BlockStmt> currentMethodBody = n.getBody();

            if (options == Defines.ADD_LINES_AFTER_BODY)
            {
               if (currentMethodBody.isPresent())
                  newMethodBody.addStatement(currentMethodBody.get());
               for (String line : contentToAdd)
                  newMethodBody.addStatement(line);
            }
            else if (options == Defines.ADD_LINES_BEFORE_BODY)
            {
               for (String line : contentToAdd)
                  newMethodBody.addStatement(line);
               if (currentMethodBody.isPresent())
                  newMethodBody.addStatement(currentMethodBody.get());
            }
            else
            {
               for (String line : contentToAdd)
                  newMethodBody.addStatement(line);
            }

            n.setBody(newMethodBody);

            // System.out.println(n.getBody().get());
         }
         super.visit(n, arg);
      }
   }

   public static void addStatementsToClass(File classFile, String contentToAdd, Integer positionFlag) {
      try {
         String classBody = FileUtils.readFileToString(classFile);
         if (positionFlag == Defines.ADD_LINES_AT_CLASS_START) {
            int index = classBody.indexOf('{');
            classBody = classBody.substring(0, index) + "{" + contentToAdd
                  + classBody.substring(index + 1, classBody.length());

         } else if (positionFlag == Defines.ADD_LINES_AT_CLASS_END) {
            int index = classBody.lastIndexOf('}');
            classBody = classBody.substring(0, index - 1) + contentToAdd
                  + classBody.substring(index, classBody.length());

         }
         PrintWriter out = new PrintWriter(classFile);
         out.write(classBody);
         out.flush();
         out.close();
         FileFormatter.formatUnformattedClass(classFile);
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   public static void main(String args[]) {
//		File file = new File(
//				"C:/Users/asharafs/Desktop/Teq/src/main/java/com/dematic/demo/ui/client/aci/AciOperationDialog.java");
//
//		String content = "public static final String META_DATA_CONSTANT_SUFFIX =\"_META_DATA\";	static class Mapper {public boolean needsDownCast;	public String dtoClassName;	public String mapperClassName;	}";
//
//		FileTransformer.addStatementsToClass(file, content, Defines.ADD_LINES_AT_CLASS_END);

      testMethodBodyChange();
   }

   public static void testMethodBodyChange() {
      File file = new File(
            "C:/Projects/Test/src/main/java/com/dematic/demo/ui/client/aci/AciOperationDialog.java");
      File outFile = new File("C:/Projects/Test/src/main/java/com/dematic/demo/ui/client/aci/AciOperationDialog.java");

      // String content = "super.registerForServerPushEvents(); String
      // terminalId = AppContext.getInstance().getTerminalId();
      // ServerPushManager serverPushManager =
      // AppContext.getInstance().getFrame().getServerPushManager();serverPushManager.addEventListener(AciGroupAssignedServerPushEvent.class.getName()
      // + \"_\" + terminalId, getRemoteEventListener());";
      List<String> content = new ArrayList<String>();
      content.add("String s=\"this is the first line\";");
      content.add("int i=5;");
      content.add(
            "if (event instanceof AciOrderCancelPushEvent) {   } else if (event instanceof ShowOrderDetailEvent) {     }");

      FileTransformer.modifyMethodBody(file, "registerForServerPushEvents", content, Defines.REPLACE_BODY, outFile);
   }
}