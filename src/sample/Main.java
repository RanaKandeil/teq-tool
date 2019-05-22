package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Main extends Application
{

   @Override
   public void start(Stage primaryStage) throws Exception
   {
      boolean found = readFile();
      if (!found)
      {
         Parent root = FXMLLoader.load(getClass().getResource(
               "/fxml/StartView.fxml"));
         primaryStage.setScene(new Scene(root, 971, 826));
         Image applicationIcon = new Image(getClass().getResourceAsStream("/images/clock.png"));
         primaryStage.getIcons().add(applicationIcon);
         primaryStage.setTitle("TEQ Tool");
         primaryStage.show();
      }
      else
      {
         Parent root = FXMLLoader.load(getClass().getResource(
               "/fxml/DialogTypes.fxml"));

         primaryStage.setScene(new Scene(root, 971, 826));

         Image applicationIcon = new Image(getClass().getResourceAsStream("/images/clock.png"));
         primaryStage.getIcons().add(applicationIcon);
         primaryStage.setTitle("TEQ Tool");
         primaryStage.show();
      }
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

   public boolean readFile()
   {
      boolean found = FindFile("config.properties", new File("C:\\TEQ\\properties"));

      Properties prop = new Properties();
      InputStream input = null;

      try
      {
         String filename = "config.properties";
         if (!found)
         {
            System.out.println("Sorry, unable to find " + filename);
            return false;
         }
         // load a properties file from class path, inside static method
         input = new FileInputStream("C:/TEQ/properties/config.properties");
         prop.load(input);

         // get the property value and print it out
         System.out.println("Username: " + prop.getProperty(PropNames.UserName));
         System.out.println("Project Path: " + prop.getProperty(PropNames.ProjectPath));
         System.out.println("Project Name: " + prop.getProperty(PropNames.ProjectName));
         System.out.println("Repository Root: " + prop.getProperty(PropNames.RepositoryRoot));
         System.out.println("GWT Paths: " + prop.getProperty(PropNames.GWTPath));
         System.out.println("App Core Version: " + prop.getProperty(PropNames.AppCoreVersion));
         System.out.println("WMS Version: " + prop.getProperty(PropNames.WMSVersion));
         System.out.println("Core Version: " + prop.getProperty(PropNames.CORVersion));
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
      return true;
   }

   public static void main(String[] args)
   {
      launch(args);
   }
}