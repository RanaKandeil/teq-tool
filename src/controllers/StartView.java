package controllers;

import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import sample.PropNames;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.*;
import java.net.URL;
import java.util.HashSet;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Logger;

//import org.w3c.dom.Node;

public class StartView implements Initializable
{

   @FXML
   private TextField usernametextfield;

   @FXML
   private TextField projectpathtextfield;

   @FXML
   private TextField projectnametextfield;

   @FXML
   private TextField reporoottextfield;

   @FXML
   private Label requiredUserName;

   @FXML
   private Label requiredProjectPath;

   @FXML
   private Label requiredProjectName;

   @FXML
   private Label requiredRepoRoot;

   private FadeTransition fadeOut1 = new FadeTransition(Duration.millis(3000));

   private FadeTransition fadeOut2 = new FadeTransition(Duration.millis(3000));

   private FadeTransition fadeOut3 = new FadeTransition(Duration.millis(3000));

   private FadeTransition fadeOut4 = new FadeTransition(Duration.millis(3000));

   String pomFileName = "pom.xml";
   String repoPath = "";
   String pomPath = "";
   private static File operationFile;
   final static String parentPath = "/project/parent";
   final static String parentGroupIdNodeXPath = "/project/parent/groupId";
   final static String parentVersionNodeXPath = "/project/parent/version";
   final static String parentArtifactidNodeXPath = "/project/parent/artifactId";
   final static String appVersionPath = "/project/properties/app.version";
   final static String gwtVersionPath = "/project/properties/gwtVersion";
   final static String gwtPatchesVersionPath = "/project/properties/gwtPatchesVersion";
   final static String gwtdependencyNodeXPath = "/project/build/pluginManagement/plugins/plugin/dependencies/dependency[groupId[contains(., 'com.google.gwt')] ]";
   final static String modulePath = "/project/modules";
   private static final Logger LOGGER = Logger.getLogger(StartView.class
         .getName());
   static String version = "";
   static String appVersion = "";
   static String gwtPath = "";

   public void initialize(URL arg0, ResourceBundle arg1)
   {
      // TODO Auto-generated method stub

      fadeOut1.setFromValue(1.0);
      fadeOut1.setToValue(0.0);
      fadeOut1.setCycleCount(1);
      fadeOut1.setAutoReverse(true);

      fadeOut1.play();

      fadeOut2.setFromValue(1.0);
      fadeOut2.setToValue(0.0);
      fadeOut2.setCycleCount(1);
      fadeOut2.setAutoReverse(true);

      fadeOut2.play();

      fadeOut3.setFromValue(1.0);
      fadeOut3.setToValue(0.0);
      fadeOut3.setCycleCount(1);
      fadeOut3.setAutoReverse(true);

      fadeOut3.play();

      fadeOut4.setFromValue(1.0);
      fadeOut4.setToValue(0.0);
      fadeOut4.setCycleCount(1);
      fadeOut4.setAutoReverse(true);
      fadeOut4.play();

      if (FindFile("config.properties", new File("C:\\TEQ\\properties")))
      {
         Properties prop = new Properties();
         InputStream input = null;

         try
         {

            String filename = "config.properties";

            input = new FileInputStream(
                  "C:/TEQ/properties/config.properties");
            // load a properties file from class path, inside static method
            prop.load(input);

            // get the property value and print it out
            projectnametextfield.setText(prop
                  .getProperty(PropNames.ProjectName));
            projectpathtextfield.setText(prop
                  .getProperty(PropNames.ProjectPath));
            usernametextfield.setText(prop.getProperty(PropNames.UserName));
            reporoottextfield.setText(prop
                  .getProperty(PropNames.RepositoryRoot));
         }
         catch (IOException ex)
         {
            ex.printStackTrace();
         }
         finally
         {
            if (input != null)
            {
               try
               {

                  input.close();
               }
               catch (IOException e)
               {
                  e.printStackTrace();
               }
            }
         }
      }
   }

   @FXML
   private void browseButtonAction()
   {
      DirectoryChooser chooser = new DirectoryChooser();
      chooser.setTitle("Choose Path");
      File selectedDirectory = chooser.showDialog(new Stage());
      if (selectedDirectory == null)
         projectpathtextfield.setText(projectpathtextfield.getText());
      else
         projectpathtextfield.setText(selectedDirectory.getAbsolutePath());
   }

   @FXML
   private void RepoRootbrowseButtonAction()
   {
      DirectoryChooser chooser = new DirectoryChooser();
      chooser.setTitle("Choose Path");
      File selectedDirectory = chooser.showDialog(new Stage());
      if (selectedDirectory == null)
         reporoottextfield.setText(reporoottextfield.getText());
      else
         reporoottextfield.setText(selectedDirectory.getAbsolutePath());
   }

   @FXML
   public void backButtonAction(ActionEvent event)
   {
      FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
            "/fxml/pageTwo.fxml"));
      Parent root;
      try
      {
         root = (Parent) fxmlLoader.load();
         Stage stage = new Stage();
         stage.setScene(new Scene(root));
         Image applicationIcon = new Image(getClass().getResourceAsStream(
               "/images/clock.png"));
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

   @FXML
   public void handleNextButtonAction(ActionEvent event)
   {
      // TODO
      if (validate())
      {
         FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
               "/fxml/DialogTypes.fxml"));
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
      repoPath = reporoottextfield.getText();
      pomPath = projectpathtextfield.getText() + "\\" + pomFileName;
      pomParser(pomFileName, pomPath, repoPath);
      // the parent pom file path
      String parentPom = parseChild(pomPath, repoPath);
      parseParent(parentPom, repoPath);
      createFile(usernametextfield.getText(), projectpathtextfield.getText(),
            projectnametextfield.getText(), reporoottextfield.getText(),
            projectpathtextfield.getText(), gwtPath, appVersion, appVersion, version);
   }

   public void handleHomePageButton(ActionEvent event)
   {
      FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
            "/fxml/DialogTypes.fxml"));
      Parent root;
      try
      {
         root = (Parent) fxmlLoader.load();
         Stage stage = new Stage();
         stage.setScene(new Scene(root));
         // stage.resizableProperty().setValue(Boolean.FALSE);
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

   private boolean validate()
   {
      int count = 0;
      if (!usernametextfield.getText().isEmpty())
         requiredUserName.setVisible(false);
      else
      {
         requiredUserName.setVisible(true);
         fadeOut1.setNode(requiredUserName);
         fadeOut1.playFromStart();
         count++;
      }

      if (!projectpathtextfield.getText().isEmpty())
         requiredProjectPath.setVisible(false);
      else
      {
         requiredProjectPath.setVisible(true);
         fadeOut2.setNode(requiredProjectPath);
         fadeOut2.playFromStart();
         count++;
      }

      if (!projectnametextfield.getText().isEmpty())
         requiredProjectName.setVisible(false);
      else
      {
         requiredProjectName.setVisible(true);
         fadeOut3.setNode(requiredProjectName);
         fadeOut3.playFromStart();
         count++;
      }

      if (!reporoottextfield.getText().isEmpty())
         requiredRepoRoot.setVisible(false);
      else
      {
         requiredRepoRoot.setVisible(true);
         fadeOut4.setNode(requiredRepoRoot);
         fadeOut4.playFromStart();
         count++;
      }

      if (count > 0)
         return false;
      return true;
   }

   public void settingButtonAction(ActionEvent event)
   {

      FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
            "/fxml/StartView.fxml"));
      Parent root;
      try
      {
         root = (Parent) fxmlLoader.load();
         Stage stage = new Stage();
         stage.setScene(new Scene(root));
         stage.show();
         (((Node) event.getSource())).getScene().getWindow().hide();
      }
      catch (IOException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }

   public void createFile(String name, String projectPath, String projectName,
                          String repositoryRoot, String pomPath, String gwtPath, String appVersion, String corVersion, String wmsVersion)
   {
      Properties prop = new Properties();
      OutputStream output = null;

      try
      {
         new File("C:/TEQ/properties").mkdirs();
         File file = new File("C:/TEQ/properties/config.properties");

         output = new FileOutputStream(file);
         PropNames propNames = new PropNames();
         // set the properties value
         prop.setProperty(propNames.UserName, name);
         prop.setProperty(propNames.ProjectPath, projectPath);
         prop.setProperty(propNames.ProjectName, projectName);
         prop.setProperty(propNames.RepositoryRoot, repositoryRoot);
         prop.setProperty(propNames.GWTPath, gwtPath);
         prop.setProperty(propNames.AppCoreVersion, appVersion);
         prop.setProperty(propNames.GWTPath, gwtPath);
         prop.setProperty(propNames.WMSVersion, wmsVersion);
         prop.setProperty(propNames.CORVersion, corVersion);

         // save properties to project root folder
         prop.store(output, null);
      }
      catch (IOException io)
      {
         io.printStackTrace();
      }
      finally
      {
         if (output != null)
         {
            try
            {
               output.close();
            }
            catch (IOException e)
            {
               e.printStackTrace();
            }
         }
      }
   }

   public void EditFile(String key, String value) throws IOException
   {
      FileInputStream in = new FileInputStream(
            "C:/TEQ/properties/config.properties");
      Properties props = new Properties();
      props.load(in);
      in.close();

      FileOutputStream out = new FileOutputStream(
            "C:/TEQ/properties/config.properties");
      props.setProperty(key, value);
      props.store(out, null);
      out.close();
   }

   public boolean FindFile(String filename, File dir)
   {

      File[] matchingFiles = dir.listFiles();

      if (matchingFiles != null)
      {

         for (File currfile : matchingFiles)
         {

            if (currfile.isDirectory())
            {
               FindFile(filename, currfile);
            }

            else if (filename.equalsIgnoreCase(currfile.getName()))
            {
               return true;
            }
         }
      }
      return false;
   }

   private static void pomParser(String pomFileName, String pomPath,
                                 String repoPath)
   {
      findPomFile(pomFileName, new File(pomPath));
      if (operationFile != null)
      {
         parsePomFile(operationFile, repoPath);
      }
      else
      {
         new RuntimeException("Pom file not found");
      }
   }

   private static void parsePomFile(File operationFile, String repoPath)
   {
      // TODO parse the pom file and save the important data to static
      // variables, reset operationFile variable, then find the parent pom
      // file based on saved data
   }

   private static void findPomFile(String name, File file)
   {
      File[] list = file.listFiles();
      if (list != null)
         for (File fil : list)
         {
            if (fil.isDirectory())
            {
               findPomFile(name, fil);
            }
            else if (name.equalsIgnoreCase(fil.getName()))
            {
               operationFile = fil;
            }
         }
   }

   public static String parseChild(String pomLocation, String repoLocation)
   {

      // Simply make java logger print log info in single line, delete it if
      // you want
      System.setProperty("java.util.logging.SimpleFormatter.format",
            "%1$tF %1$tT %4$s %2$s %5$s%6$s%n");
      Set<String> jars = new HashSet();
      String parentFilePath = "";
      try
      {

         File pomFile = new File(pomLocation);
         DocumentBuilderFactory dbFactory = DocumentBuilderFactory
               .newInstance();
         dbFactory.setIgnoringComments(true);
         DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
         Document doc = dBuilder.parse(pomFile);
         XPathFactory xPathfactory = XPathFactory.newInstance();
         XPath xpath = xPathfactory.newXPath();

         // very Important, normalize xml representation
         doc.getDocumentElement().normalize();
         // check parent exist
         XPathExpression parentExp = xpath.compile(parentPath);

         org.w3c.dom.Node parentNode = (org.w3c.dom.Node) parentExp
               .evaluate(doc, XPathConstants.NODE);
         if (parentNode == null)
         {
            return pomLocation;
         }
         else
         {
            // Get group Id
            XPathExpression groupIdExp = xpath
                  .compile(parentGroupIdNodeXPath);

            org.w3c.dom.Node groupIdNode = (org.w3c.dom.Node) groupIdExp
                  .evaluate(doc, XPathConstants.NODE);
            if (groupIdNode == null)
            {
               LOGGER.severe("Wrong xml path for project.version tag, please check the dcdVersionNodeXPath variable in PomParser class");
            }
            String groupId = groupIdNode.getTextContent();
            groupId = groupId.replace(".", "\\");
            groupId = groupId + "\\";

            LOGGER.info("WMS GroupId : " + groupId);
            // Get PROJECT version
            XPathExpression versionExp = xpath
                  .compile(parentVersionNodeXPath);

            org.w3c.dom.Node versionNode = (org.w3c.dom.Node) versionExp
                  .evaluate(doc, XPathConstants.NODE);
            if (versionNode == null)
            {
               LOGGER.severe("Wrong xml path for project.version tag, please check the dcdVersionNodeXPath variable in PomParser class");
            }
            version = versionNode.getTextContent();
            LOGGER.info("WMS Parent version : " + version);
            // Get PROJECT artifactId
            XPathExpression artifactIdExp = xpath
                  .compile(parentArtifactidNodeXPath);
            org.w3c.dom.Node artifactNode = (org.w3c.dom.Node) artifactIdExp
                  .evaluate(doc, XPathConstants.NODE);
            if (artifactNode == null)
            {
               LOGGER.severe("Wrong xml path for project.artifactId tag, please check the dcdVersionNodeXPath variable in PomParser class");
            }
            String artifactId = artifactNode.getTextContent();
            LOGGER.info("WMS Parent artifactId : " + artifactId);
            LOGGER.info("WMS Parent path : " + repoLocation
                  + "\\com\\dematic\\wms\\" + artifactId + "\\" + version);
            String parentFileName = artifactId + "-" + version + ".pom";
            parentFilePath = repoLocation + "\\" + groupId + artifactId
                  + "\\" + version + "\\" + parentFileName;
            return parseChild(parentFilePath, repoLocation);
         }
      }
      catch (ParserConfigurationException e)
      {
         e.printStackTrace();
      }
      catch (SAXException e)
      {
         e.printStackTrace();
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
      catch (XPathExpressionException e)
      {
         e.printStackTrace();
      }

      // return parseChild(parentFilePath, repoLocation);
      return parentFilePath;
   }

   public static void parseParent(String pomLocation, String repoLocation)
   {

      // Simply make java logger print log info in single line, delete it if
      // you want
      System.setProperty("java.util.logging.SimpleFormatter.format",
            "%1$tF %1$tT %4$s %2$s %5$s%6$s%n");

      try
      {

         File pomFile = new File(pomLocation);
         DocumentBuilderFactory dbFactory = DocumentBuilderFactory
               .newInstance();
         dbFactory.setIgnoringComments(true);
         DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
         Document doc = dBuilder.parse(pomFile);
         XPathFactory xPathfactory = XPathFactory.newInstance();
         XPath xpath = xPathfactory.newXPath();

         // very Important, normalize xml representation
         doc.getDocumentElement().normalize();

         // Get PROJECT version
         XPathExpression appVersionExp = xpath.compile(appVersionPath);
         org.w3c.dom.Node appVersionNode = (org.w3c.dom.Node) appVersionExp
               .evaluate(doc, XPathConstants.NODE);
         if (appVersionNode == null)
         {
            LOGGER.severe("Wrong xml path for project.version tag, please check the dcdVersionNodeXPath variable in PomParser class");
         }
         appVersion = appVersionNode.getTextContent();
         LOGGER.info("WMS project version : " + appVersion);
         // Get GWT version
         XPathExpression gwtVersionExp = xpath.compile(gwtVersionPath);
         org.w3c.dom.Node gwtVersionNode = (org.w3c.dom.Node) gwtVersionExp
               .evaluate(doc, XPathConstants.NODE);
         if (gwtVersionNode == null)
         {
            LOGGER.severe("Wrong xml path for project.artifactId tag, please check the dcdVersionNodeXPath variable in PomParser class");
         }
         String gwtVersion = gwtVersionNode.getTextContent();
         LOGGER.info("WMS gwt version : " + gwtVersion);

         // Get GWT patches version
         XPathExpression gwtPatchesVersionExp = xpath
               .compile(gwtPatchesVersionPath);
         org.w3c.dom.Node gwtPatchesVersionNode = (org.w3c.dom.Node) gwtPatchesVersionExp
               .evaluate(doc, XPathConstants.NODE);
         if (gwtPatchesVersionNode == null)
         {
            LOGGER.severe("Wrong xml path for project.artifactId tag, please check the dcdVersionNodeXPath variable in PomParser class");
         }
         String gwtPatchesVersion = gwtPatchesVersionNode.getTextContent();
         LOGGER.info("WMS gwt patches version : " + gwtPatchesVersion);

         // Extract all gwt dependencies
         XPathExpression expr = xpath.compile(gwtdependencyNodeXPath);
         NodeList nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);

         for (int i = 0; i < nl.getLength(); i++)
         {

            // Get all node inside dependency node(groupId, artifact,
            // etc...)
            NodeList dcl = nl.item(i).getChildNodes();

            // Extract artifact from dependency node
            for (int j = 0; j < dcl.getLength(); j++)
            {// artifactId
               // Get jar name
               if ("artifactId".equals(dcl.item(j).getNodeName()))
               {

                  gwtPath = gwtPath + repoLocation
                        + "\\com\\google\\gwt\\"
                        + dcl.item(j).getTextContent() + "\\"
                        + gwtVersion + "-" + "patch" + "-"
                        + gwtPatchesVersion + ";";
               }
            }
         }
         // Extract all modules
         XPathExpression exprmod = xpath.compile(modulePath);
         NodeList modl = (NodeList) exprmod.evaluate(doc,
               XPathConstants.NODESET);

         StringBuilder moduleName = new StringBuilder();

         for (int i = 0; i < modl.getLength(); i++)
         {

            // Get all node inside dependency node(groupId, artifact,
            // etc...)
            NodeList modcl = modl.item(i).getChildNodes();

            // Clear moduleName

            moduleName.setLength(0);

            // Extract artifact and version from dependency node
            for (int j = 0; j < modcl.getLength(); j++)
            {// artifactId
               // Get jar name
               if ("module".equals(modcl.item(j).getNodeName()))
               {
                  moduleName.append(modcl.item(j).getTextContent());
               }
            }
         }
      }
      catch (ParserConfigurationException e)
      {
         e.printStackTrace();
      }
      catch (SAXException e)
      {
         e.printStackTrace();
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
      catch (XPathExpressionException e)
      {
         e.printStackTrace();
      }
   }
}