package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.*;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class DynamicPage implements Initializable
{
   StringBuffer fileContent = new StringBuffer();
   StringBuffer fileImports = new StringBuffer();

   static ArrayList<URL> jars = new ArrayList<>();
   static ArrayList<URL> getAllJars = new ArrayList<>();
   static ArrayList<String> classes = new ArrayList<>();
   static ArrayList<String> stepsClasses = new ArrayList<>();
   static ArrayList<String> viewsClasses = new ArrayList<>();
   static ArrayList<URL> orgJar = new ArrayList<>();

   static ArrayList<String> serviceServerSideClasses = new ArrayList<>();
   static ArrayList<String> serviceClientSideClasses = new ArrayList<>();
   static ArrayList<String> allClasses = new ArrayList<>();
   static ArrayList<String> operationDialogClass = new ArrayList<>();

   static ArrayList<String> classname = new ArrayList<>();

   static String rcpsuffixString = "eventservice";
   static String jarName = "";
   static String allJarName = "";
   static String classAnnotation = "@OverrideDefaultImplementation";

   private HashMap<String, String> mapValues;
   Set<String> returnTypeSet = new HashSet<String>();
   static URLClassLoader classLoader;
   static CheckBoxTreeItem<String> rootItem = new CheckBoxTreeItem<>("All Found Classes ");

   @FXML
   private TreeView<String> root;
   @FXML
   private Pane pane;

   @Override
   public void initialize(URL arg0, ResourceBundle arg1)
   {
      // TODO Auto-generated method stub

   }

   public void backButtonAction(ActionEvent event)
   {
      clearAll();
      FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
            "/fxml/pageSix.fxml"));
      Parent root;
      try
      {
         root = (Parent) fxmlLoader.load();
         Stage stage = new Stage();
         stage.setScene(new Scene(root));
         Image applicationIcon = new Image(getClass().getResourceAsStream("/images/clock.png"));
         stage.getIcons().add(applicationIcon);
         stage.setTitle("TEQ Tool");
         stage.show();

         (((Node) event.getSource())).getScene().getWindow().hide();
      }
      catch (IOException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }

   public StringBuilder generateClasses(StringBuilder Sbbb) throws Exception
   {
      String mavenRoot = mapValues.get("mavenRoot");
      String gwtRootString = mapValues.get("gwtRoot");
      String wmsVersionString = mapValues.get("wmsVersion");
      String appCoreVersionString = mapValues.get("appCoreVersion");
      String dialogName = mapValues.get("dialogClassName");
      File[] files = new File(mavenRoot).listFiles();
      File[] files2 = new File(mavenRoot + "\\org\\aspectj\\aspectjrt\\1.8.8").listFiles();

      String shortdialogName = dialogName.replace("OperationDialog", "");

      getAllJarsToGenerateClasses(files, rcpsuffixString, appCoreVersionString, wmsVersionString, mavenRoot);
      //load GWT servlet jar
      String projectName = mapValues.get("ProjectName");
      String userName = mapValues.get("UserName");
      String projectPath = mapValues.get("projectPath");
      projectPath = projectPath.replace("\\", "\\\\");

      classLoader = new URLClassLoader(getAllJars.toArray(new URL[0]));
      StringBuilder sb = new StringBuilder();

      ObservableSet<CheckBoxTreeItem<?>> checkedItems = FXCollections.observableSet();
      findCheckedItems((CheckBoxTreeItem<?>) root.getRoot(), checkedItems);
      String servicePackageName = new String();
      String serviceImports = new String();
      for (Object x : checkedItems)
      {
         classname.add(x.toString());
      }

      for (String className : classname)
      {
         fileContent = new StringBuffer();
         fileImports = new StringBuffer();
         if (className.contains("View Classes") || className.contains("Step Classes") || className.contains("All Found Classes ") || className.contains("All Classes") || className.contains("Operation Dialog Class") || className.contains("Service Server Side Classes") || className.contains("Service Client Side Classes"))
         {
            continue;
         }

         if (className.contains("View.class"))
         {
            ArrayList<String> xmlList = new ArrayList<>();
            String content = "<!DOCTYPE ui:UiBinder SYSTEM \"http://dl.google.com/gwt/DTD/xhtml.ent\">\n" +
                  "<ui:UiBinder xmlns:ui=\"urn:ui:com.google.gwt.uibinder\"\n" +
                  "xmlns:d=\"urn:import:com.dematic.wms.app.base.ui.client.scomponent\"\n" +
                  "xmlns:s=\"urn:import:com.dematic.wms.app.base.ui.client.dialog.steps\">\n" +
                  "\n" +
                  "<d:SAttributePanel expandX=\"100%\" expandY=\"100%\" fontSize=\"20\">\n" +
                  "\n" +
                  "</d:SAttributePanel>\n" +
                  "\n" +
                  "</ui:UiBinder>\n";
            BufferedWriter bw = null;
            FileWriter fw = null;

            xmlList.add(className);
            for (String xmlClass : xmlList)
            {

               className = className.replace("TreeItem [ value: ", "").replace(" ]", "").replace(".class", "").replace("/", ".").replaceAll("\\s+", "");
               xmlClass = className.replace(".", "\\\\");
               String lastWord = xmlClass.substring(xmlClass.lastIndexOf("\\") + 1);
               String removedWord = xmlClass.substring(0, xmlClass.lastIndexOf("\\\\")) + "";
               projectName = projectName.substring(0, 1).toUpperCase() + projectName.substring(1);
               String projectNameClassName = projectName.substring(0, 1).toUpperCase() + projectName.substring(1);

               String output = projectName.substring(0, 1).toLowerCase() + projectName.substring(1);
               String[] packageSplitName = removedWord.split("\\\\.");
               String replacedWordInpackageName = packageSplitName[2];
               String imports = removedWord.replace(replacedWordInpackageName, output);

               File file = new File(projectPath + "\\\\" + "src\\\\main\\\\java\\\\" + imports + "\\\\" + projectNameClassName + lastWord + ".ui.xml");

               file.getParentFile().mkdirs();
               file.createNewFile();
               fw = new FileWriter(file);
               bw = new BufferedWriter(fw);
               bw.write(content);
               xmlClass = xmlClass.replace("\\\\", "\\");

               Sbbb.append("\n" + xmlClass + ".ui.xml Is Created ");
            }

            try
            {

               if (bw != null)
                  bw.close();

               if (fw != null)
                  fw.close();
            }
            catch (IOException ex)
            {

               ex.printStackTrace();
            }
         }

         sb.setLength(0);
         sb.append(className.replace("TreeItem [ value: ", "").replace(" ]", "").replace(".class", "").replace("/", ".").replaceAll("\\s+", ""));
         String folers = className.replace("TreeItem [ value: ", "").replace(" ]", "").replace(".class", "").replace("/", ".").replaceAll("\\s+", "").replace(".", "\\\\");

         String lastWord = folers.substring(folers.lastIndexOf("\\") + 1);
         String removedWord = folers.substring(0, folers.lastIndexOf("\\\\")) + "";
         String projectNameClassName = projectName.substring(0, 1).toUpperCase() + projectName.substring(1);

         String output2 = projectName.substring(0, 1).toLowerCase() + projectName.substring(1);
         String[] packageSplitName2 = removedWord.split("\\\\.");
         String replacedWordInpackageName2 = packageSplitName2[2];
         String imports2 = removedWord.replace(replacedWordInpackageName2, output2);

         File file = new File(projectPath + "\\\\" + "src\\\\main\\\\java\\\\" + imports2 + "\\\\" + projectNameClassName + lastWord + ".java");
         file.getParentFile().mkdirs();
         file.createNewFile();

         String classNamee = sb.toString().replace(".", "\\");
         classNamee = classNamee.substring(classNamee.lastIndexOf("\\") + 1);

         Sbbb.append("\n" + sb.toString().replace(".", "\\") + ".java Is Created Done");

         PrintWriter writer = new PrintWriter(file, "UTF-8");
         Boolean b = false;
         try
         {
            returnTypeSet.clear();
            Boolean service = false;
            Class<?> cls = Class.forName(sb.toString(), false, classLoader);
            if (className.contains("Service") && !className.contains("Async") && !className.contains("Impl"))
            {

               servicePackageName = cls.getPackage().getName();

               String[] packageSplitName = servicePackageName.split("\\.");
               String replacedWordInpackageName = packageSplitName[2];
               String output = projectName.substring(0, 1).toLowerCase() + projectName.substring(1);

               serviceImports = servicePackageName.replace(replacedWordInpackageName, output);

               fileImports.append("package " + serviceImports + ";" + "\n");
               fileImports.append("import " + servicePackageName + "." + classNamee + ";" + "\n");
               fileImports.append("import com.google.gwt.user.client.rpc.RemoteServiceRelativePath ;" + "\n");

               fileContent.append("\n" + "/**");
               fileContent.append("\n" + " * @author " + userName);
               fileContent.append("\n" + " */");
               fileContent.append("\n" + "@RemoteServiceRelativePath " + "(\"" + projectNameClassName + classNamee + ".gwtrpc\")");

               fileContent.append("\n" + "public interface " + projectNameClassName + classNamee + " extends " + classNamee + "{");

               service = true;
            }

            if (className.contains("Impl.class"))
            {

               String packageName = cls.getPackage().getName();

               String[] packageSplitName = packageName.split("\\.");
               String replacedWordInpackageName = packageSplitName[2];
               String output = projectName.substring(0, 1).toLowerCase() + projectName.substring(1);
               String imports = packageName.replace(replacedWordInpackageName, output);

               String serviceNewPackageName = imports.substring(0, imports.lastIndexOf(".")) + "";

               serviceNewPackageName = serviceNewPackageName.substring(0, serviceNewPackageName.lastIndexOf(".")) + "";
               serviceNewPackageName = serviceNewPackageName.substring(0, serviceNewPackageName.lastIndexOf(".")) + "";
               serviceNewPackageName = serviceNewPackageName.substring(0, serviceNewPackageName.lastIndexOf(".")) + "";

               serviceNewPackageName = serviceNewPackageName + ".shared.service." + projectNameClassName + shortdialogName + "Service";

               fileImports.append("package " + imports + ";" + "\n");
               fileImports.append("import " + packageName + "." + classNamee + ";" + "\n");
               fileImports.append("import " + serviceNewPackageName + ";" + "\n");

               fileImports.append("import javax.enterprise.inject.Specializes ;" + "\n");
               fileContent.append("\n" + "/**");
               fileContent.append("\n" + " * @author " + userName);
               fileContent.append("\n" + " */");
               fileContent.append("\n" + "@Specializes");

               fileContent.append("\n" + "public class " + projectNameClassName + classNamee + " extends " + classNamee
                     + " implements " + projectNameClassName + shortdialogName + "Service" + "{");

               service = true;
            }
            if (className.contains("Async"))
            {

               String packageName = cls.getPackage().getName();

               String[] packageSplitName = packageName.split("\\.");
               String replacedWordInpackageName = packageSplitName[2];
               String output = projectName.substring(0, 1).toLowerCase() + projectName.substring(1);

               String imports = packageName.replace(replacedWordInpackageName, output);
               fileImports.append("package " + imports + ";" + "\n");
               fileImports.append("import " + packageName + "." + classNamee + ";" + "\n");
               fileContent.append("\n" + "/**");
               fileContent.append("\n" + " * @author " + userName);
               fileContent.append("\n" + " */");
               fileContent.append("\n" + "public interface " + projectNameClassName + classNamee + " extends " + classNamee + "{");

               service = true;
            }

            if (cls.getDeclaredMethods().length == 0)
            {
               String packageName = cls.getPackage().getName();

               String[] packageSplitName = packageName.split("\\.");
               String replacedWordInpackageName = packageSplitName[2];
               String imports = packageName.replace(replacedWordInpackageName, projectName);

               fileImports.append("package " + imports + ";" + "\n");
               fileImports.append("import " + packageName + "." + classNamee + ";" + "\n");
               fileImports.append("import com.dematic.wms.app.base.ui.shared.common.OverrideDefaultImplementation ;" + "\n");

               fileContent.append("\n" + "/**");
               fileContent.append("\n" + " * @author " + userName);
               fileContent.append("\n" + " */");
               fileContent.append("\n" + classAnnotation);

               fileContent.append("\n" + "public class " + projectNameClassName + classNamee + " extends " + classNamee + "{");
            }

            if (service == false)
            {

               for (Method method : cls.getDeclaredMethods())
               {
                  if (method.toString().startsWith("void") || method.toString().contains("static ") || method.toString().contains("abstract ") || method.toString().contains("private "))
                  {
                     continue;
                  }
                  if (method.getName().contains("$"))
                  {
                     continue;
                  }

                  if (method.getName().contains("getView"))
                  {
                     continue;
                  }

                  String packageName = cls.getPackage().getName();
//
                  String classname = "public class " + projectNameClassName + classNamee + " extends " + classNamee;
                  String abstractClassName = "public abstract class " + projectNameClassName + classNamee + " extends " + classNamee;

                  String methodName = method.getName();

                  String returnType = method.getReturnType().getTypeName();
                  method.getGenericReturnType();
                  returnTypeSet.add(returnType);
                  returnTypeSet.add(method.getReturnType().getName());
                  String finalRetutnType = returnType.substring(returnType.lastIndexOf(".") + 1);
                  String[] packageSplitName = packageName.split("\\.");
                  String replacedWordInpackageName = packageSplitName[2];
                  String output = projectName.substring(0, 1).toLowerCase() + projectName.substring(1);
                  String imports = packageName.replace(replacedWordInpackageName, output);
                  ArrayList<MethodParam> params = new ArrayList<>();

                  Class<?>[] param = method.getParameterTypes();
                  boolean isParamEmpty = true;
                  String returnTypes = new String();
                  int index = 0;
                  ArrayList<String> args = new ArrayList<>();
                  for (Class<?> c : param)
                  {
                     index++;
                     MethodParam methodParam = new MethodParam();
                     isParamEmpty = false;
                     String paramTypeName = (c.isArray() ? c.getComponentType() : c).getName();
                     returnTypeSet.add(paramTypeName);
                     paramTypeName = paramTypeName.substring(paramTypeName.lastIndexOf(".") + 1);
                     methodParam.setType(paramTypeName);
                     methodParam.setName("arg" + index);
                     args.add(methodParam.getName());
                     params.add(methodParam);
                  }
                  int x = 1;

                  if (params.size() >= 1)
                  {

                     if (!b)
                     {
                        fileImports.append("package " + imports + ";" + "\n");
                        fileImports.append("import " + packageName + "." + classNamee + ";" + "\n");
                        fileImports.append("import com.dematic.wms.app.base.ui.shared.common.OverrideDefaultImplementation ;" + "\n");

                        fileContent.append("\n" + "/**");
                        fileContent.append("\n" + " * @author " + userName);
                        fileContent.append("\n" + " */");
                        fileContent.append("\n" + classAnnotation);
                        if (Modifier.isAbstract(cls.getModifiers()))
                        {
                           fileContent.append("\n" + abstractClassName + "{");
                        }
                        else
                        {
                           fileContent.append("\n" + classname + "{");
                        }
                        b = true;
                     }
                     fileContent.append("\n" + "@Override");
                     if (method.toString().contains("abstract"))
                     {
                        if (finalRetutnType == "void")
                        {
                           fileContent.append("\n" + "public " + finalRetutnType + " " + methodName + "( " + params.toString().replace("[", "").replace("]", "") + ") { " + methodName + "(" + args.toString().replace("[", "").replace("]", "") + "" + ");" + "\n" + " }");
                        }
                        else
                        {
                           fileContent.append("\n" + "public " + finalRetutnType + " " + methodName + "( " + params.toString().replace("[", "").replace("]", "") + ") {" + "\n" + " return " + methodName + "(" + args.toString().replace("[", "").replace("]", "") + "" + "); " + "\n" + " }");
                        }
                     }
                     else
                     {
                        if (finalRetutnType == "void")
                        {
                           fileContent.append("\n" + "public " + finalRetutnType + " " + methodName + "( " + params.toString().replace("[", "").replace("]", "") + ") { super." + methodName + "(" + args.toString().replace("[", "").replace("]", "") + "" + ");" + "\n" + " }");
                        }
                        else
                        {
                           fileContent.append("\n" + "public " + finalRetutnType + " " + methodName + "( " + params.toString().replace("[", "").replace("]", "") + ") {" + "\n" + " return " + "super." + methodName + "(" + args.toString().replace("[", "").replace("]", "") + "" + "); " + "\n" + " }");
                        }
                     }
                  }
                  else
                  {
                     if (isParamEmpty)
                     {
                        if (!b)
                        {
                           fileImports.append("package " + imports + ";" + "\n");
                           fileImports.append("import com.dematic.wms.app.base.ui.shared.common.OverrideDefaultImplementation ;" + "\n");
                           fileImports.append("import " + packageName + "." + classNamee + ";");

                           fileContent.append("\n" + "/**");
                           fileContent.append("\n" + " * @author " + userName);
                           fileContent.append("\n" + " */");
                           fileContent.append("\n" + classAnnotation);
                           if (Modifier.isAbstract(cls.getModifiers()))
                           {
                              fileContent.append("\n" + abstractClassName + "{");
                           }
                           else
                           {
                              fileContent.append("\n" + classname + "{");
                           }
                           b = true;
                        }
                        fileContent.append("\n" + "@Override");
                        if (finalRetutnType == "void")
                        {
                           fileContent.append("\n" + "public " + finalRetutnType + " " + methodName + "( " + params.toString().replace("[", "").replace("]", "") + ") { super." + methodName + "(" + args.toString().replace("[", "").replace("]", "") + "" + ");" + "\n" + " }");
                        }
                        else
                        {
                           fileContent.append("\n" + "public " + finalRetutnType + " " + methodName + "( " + params.toString().replace("[", "").replace("]", "") + ") {" + "\n" + " return " + "super." + methodName + "(" + args.toString().replace("[", "").replace("]", "") + "" + "); " + "\n" + " }");
                        }
                     }
                  }
               }
            }
            for (String imports : returnTypeSet)
            {
               if (!imports.contains("void"))
               {
                  if (imports.contains("int") || imports.contains("boolean") || imports.contains("String"))
                  {
                     continue;
                  }
                  fileImports.append("\n" + "import " + imports + ";", 0, imports.length() + 9);
               }
            }
            fileContent.append("\n" + "}");
            writer.write((fileImports).append(fileContent).toString());
         }
         catch (ClassNotFoundException e)
         {
            e.printStackTrace();
         }
         finally
         {
            writer.close();
         }
      }
      return Sbbb;
   }

   public void generate(ActionEvent event)
   {

      FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
            "/fxml/lastPageLog.fxml"));
      Parent root;
      try
      {
         root = (Parent) fxmlLoader.load();
         Stage stage = new Stage();
         stage.setScene(new Scene(root));
         // stage.resizableProperty().setValue(Boolean.FALSE);
         LastPageLogController controller = fxmlLoader
               .<LastPageLogController>getController();
         StringBuilder sbsx = generateClasses(new StringBuilder());
         controller.initData(sbsx);

         Image applicationIcon = new Image(getClass().getResourceAsStream("/images/clock.png"));

         stage.getIcons().add(applicationIcon);
         stage.setTitle("TEQ Tool");
         stage.show();

         (((Node) event.getSource())).getScene().getWindow().hide();
      }
      catch (IOException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   public void loadClass(String searchWordInDialogPachageName, String slashedDialogPackageName, String slashedServiceClienttSidePackageName, String slashedServiceServertSidePackageName
         , String mvnRootString, String gwtRootString, String appCoreVersionString, String wmsVersionString, String dialogName) throws IOException
   {
      clearAll();
      String shortdialogName = dialogName.replace("OperationDialog", "");
      File[] files = new File(mvnRootString).listFiles();
      getSpecificJarsToLoadClasses(files, appCoreVersionString, wmsVersionString, searchWordInDialogPachageName);
      ZipFile file = null;
      String[] finalJarName = jarName.split(",");

      CheckBoxTreeItem<String> c1 = new CheckBoxTreeItem<>("All Classes");
      CheckBoxTreeItem<String> c2 = new CheckBoxTreeItem<>("Step Classes");
      CheckBoxTreeItem<String> c3 = new CheckBoxTreeItem<>("View Classes");
      CheckBoxTreeItem<String> c6 = new CheckBoxTreeItem<>("Operation Dialog Class");

      CheckBoxTreeItem<String> c4 = new CheckBoxTreeItem<>("Service Server Side Classes");
      classes.clear();
      for (String jarName : finalJarName)
      {
         file = new ZipFile(jarName);
         classes = getClassesNames(file);

         for (String className : classes)
         {
            if (className.contains(slashedDialogPackageName) && className.contains(".class") && !className.contains("$") && className.contains(dialogName))
            {
               c6.getChildren().addAll(new CheckBoxTreeItem<>(className));
               c1.getChildren().addAll(new CheckBoxTreeItem<>(className));
               operationDialogClass.add(className);
            }

            if (className.contains(slashedDialogPackageName) && className.contains(".class") && !className.contains("$") && className.contains("Step") && !className.contains("Event"))
            {
               c2.getChildren().add(new CheckBoxTreeItem<>(className));
               c1.getChildren().addAll(new CheckBoxTreeItem<>(className));

               stepsClasses.add(className);
            }
            if (className.contains(slashedDialogPackageName) && className.contains(".class") && !className.contains("$") && className.contains("View"))
            {
               c3.getChildren().addAll(new CheckBoxTreeItem<>(className));
               c1.getChildren().addAll(new CheckBoxTreeItem<>(className));

               viewsClasses.add(className);
            }

            if (className.contains(slashedServiceClienttSidePackageName + "/" + shortdialogName + "Service.class") || className.contains(slashedServiceClienttSidePackageName + "/" + shortdialogName + "ServiceAsync.class"))
            {
               c4.getChildren().addAll(new CheckBoxTreeItem<>(className));
               c1.getChildren().addAll(new CheckBoxTreeItem<>(className));
               serviceClientSideClasses.add(className);
            }
            if (className.contains(slashedServiceServertSidePackageName + "/" + shortdialogName + "ServiceImpl.class"))
            {
               c1.getChildren().addAll(new CheckBoxTreeItem<>(className));
               c4.getChildren().addAll(new CheckBoxTreeItem<>(className));

               serviceServerSideClasses.add(className);
            }
         }
      }
      c4.setSelected(true);

      rootItem.getChildren().addAll(c1, c2, c3, c4, c6);

      rootItem.setExpanded(true);
      root.setCellFactory(CheckBoxTreeCell.<String>forTreeView());
      root.setRoot(rootItem);
      root.setShowRoot(true);
   }

   private void findCheckedItems(CheckBoxTreeItem<?> item, ObservableSet<CheckBoxTreeItem<?>> checkedItems)
   {
      if (item.isSelected())
      {
         checkedItems.add(item);
      }
      for (TreeItem<?> child : item.getChildren())
      {
         findCheckedItems((CheckBoxTreeItem<?>) child, checkedItems);
      }
   }

   public static ArrayList getJars(File[] files, String suffix, String searchWordInDialogPachageName)
   {
      for (File file : files)
      {
         if (file.isDirectory())
         {
            getJars(file.listFiles(), suffix, searchWordInDialogPachageName); // Calls same method again.
         }
         else
         {
            if (file.getName().endsWith(suffix + ".jar") && file.getName().contains(searchWordInDialogPachageName))
            {
               try
               {
                  jars.add(file.toURI().toURL());
               }
               catch (MalformedURLException e)
               {
                  e.printStackTrace();
               }
            }
         }
      }
      return jars;
   }

   public static ArrayList getAllJars(File[] files, String suffix)
   {
      for (File file : files)
      {
         if (file.isDirectory())
         {
            getAllJars(file.listFiles(), suffix); // Calls same method again.
         }
         else
         {
            if (suffix == rcpsuffixString)
            {
               if (file.getName().startsWith(suffix) && file.getName().endsWith(".jar") && !file.getName().contains("sources"))
               {
                  try
                  {
                     getAllJars.add(file.toURI().toURL());
                  }
                  catch (MalformedURLException e)
                  {
                     e.printStackTrace();
                  }
               }
            }
            else if (file.getName().endsWith(suffix + ".jar"))
            {
               try
               {
                  getAllJars.add(file.toURI().toURL());
               }
               catch (MalformedURLException e)
               {
                  e.printStackTrace();
               }
            }
         }
      }
      return getAllJars;
   }

   public static ArrayList<String> getClassesNames(ZipFile file) throws IOException
   {
      final Set<ZipEntry> files = new HashSet<ZipEntry>(Collections.list(file.entries()));
      classes.clear();
      for (ZipEntry e : files)
      {
         classes.add(e.getName());
      }
      return classes;
   }

   public void setMapData(HashMap<String, String> newMapValues)
   {
      this.mapValues = new HashMap<String, String>();
      this.mapValues = newMapValues;
   }

   public void clearAll()
   {
      rootItem.getChildren().clear();
      jars.clear();
      allClasses.clear();
      viewsClasses.clear();
      stepsClasses.clear();
      getAllJars.clear();
   }

   public static void getSpecificJarsToLoadClasses(File[] file, String appCoreVersion, String wmsVersion, String searchWordInDialogPachageName) throws IOException
   {
//        jars.clear();
      getJars(file, wmsVersion, searchWordInDialogPachageName);
      getJars(file, appCoreVersion, searchWordInDialogPachageName);
//        getJars(file, rcpsuffix, searchWordInDialogPachageName);
      //load GWT servlet jar

      jarName = jars.toString();
      jarName = jarName.replaceAll("file:/", "");
      jarName = jarName.replaceAll("\\[", "");
      jarName = jarName.replaceAll("\\]", "");
      jarName = jarName.replaceAll(" ", "");
   }

   public static void getAllJarsToGenerateClasses(File[] file, String rcpsuffix, String appCoreVersion, String wmsVersion, String mavenRoot) throws IOException
   {
//        getAllJars.clear();
        getAllJars(file, wmsVersion);
        getAllJars(file, appCoreVersion);
        getAllJars(file, rcpsuffix);
        //load GWT servlet jar
        file = new File(mavenRoot + "\\org\\aspectj\\aspectjrt").listFiles();
        getAllJars(file, "");
        file = new File(mavenRoot + "\\org\\apache\\geronimo\\specs\\geronimo-ejb_3.1_spec").listFiles();
        getAllJars(file, "");
        file = new File(mavenRoot + "\\org\\slf4j\\slf4j-api").listFiles();
        getAllJars(file, "");
        file = new File(mavenRoot + "\\com\\googlecode\\gwtmosaic\\gwt-mosaic").listFiles();
        getAllJars(file, "");
        file = new File(mavenRoot + "\\com\\google\\gwt\\gwt-user").listFiles();
        getAllJars(file, "");
        allJarName = getAllJars.toString();
        allJarName = allJarName.replaceAll("file:/", "");
        allJarName = allJarName.replaceAll("\\[", "");
        allJarName = allJarName.replaceAll("\\]", "");
        allJarName = allJarName.replaceAll(" ", "");


    }

    public ArrayList<URL> getAllJarsToGenerateClasses2(File[] file, String rcpsuffix, String appCoreVersion, String wmsVersion, String mavenRoot) throws IOException {
//      getAllJars.clear();
      getAllJars(file, wmsVersion);
      getAllJars(file, appCoreVersion);
      getAllJars(file, rcpsuffix);
      //load GWT servlet jar
      file = new File(mavenRoot + "\\org\\aspectj\\aspectjrt").listFiles();
      getAllJars(file, "");
      file = new File(mavenRoot + "\\org\\apache\\geronimo\\specs\\geronimo-ejb_3.1_spec").listFiles();
      getAllJars(file, "");
      file = new File(mavenRoot + "\\org\\slf4j\\slf4j-api").listFiles();
      getAllJars(file, "");
      file = new File(mavenRoot + "\\com\\googlecode\\gwtmosaic\\gwt-mosaic").listFiles();
      getAllJars(file, "");
      file = new File(mavenRoot + "\\com\\google\\gwt\\gwt-user").listFiles();
      getAllJars(file, "");
      allJarName = getAllJars.toString();
      allJarName = allJarName.replaceAll("file:/", "");
      allJarName = allJarName.replaceAll("\\[", "");
      allJarName = allJarName.replaceAll("\\]", "");
      allJarName = allJarName.replaceAll(" ", "");
      return getAllJars;

  }


}
