package controllers;

import com.advansys.generator.common.GenerationMain;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class PageFourController implements Initializable, Runnable
{
   @FXML
   private Button close;

   @FXML
   private TextField generatedPath;

   @FXML
   private TextArea logArea;

   private List<String> list;

   private String logPath = null;

   private boolean flag = false;

   @Override
   public void initialize(URL location, ResourceBundle resources)
   {
   }

   @FXML
   public void closeButtonAction(ActionEvent event)
   {
      Stage currentStage = (Stage) close.getScene().getWindow();
      currentStage.close();
      FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/pageTwo.fxml"));
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
      catch (Exception e)
      {
         // TODO
         generalExceptionMessagePopUp(e.getMessage());
         GenerationMain.getLOGGER().warning(e.getMessage());
      }
   }

   public void initData(String path, StringBuilder x)

   {
      logArea.setText(x.toString());
      generatedPath.setText(path);
      logPath = new String();
      logPath.equals(path.concat("\\generatedss_output.txt"));
   }

   private void loadAppConfigurationFile()
   {
      Task task = new Task<Void>()
      {
         @Override
         public Void call() throws InterruptedException
         {
            int max = 1000000;
            for (int i = 1; i <= max; i = i + 10)
            {
               if (isCancelled())
               {
                  break;
               }
               if (flag == false)
                  loadFile();
               for (int j = 0; j < list.size(); j++)
               {
                  String value = list.get(j);
                  if (value.equalsIgnoreCase("finished"))
                  {
                     flag = true;
                     break;
                  }
                  logArea.setText(logArea.getText() + value + "\n");
               }
            }
            return null;
         }
      };
      if (flag == false)
         new Thread(task).start();
   }

   public void loadFile()
   {
      list = new ArrayList<String>();
      File file = new File(logPath);
      BufferedReader reader = null;

      try
      {
         reader = new BufferedReader(new FileReader(file));
         String text = null;

         while ((text = reader.readLine()) != null)
         {
            list.add(text);
         }
      }
      catch (Exception e)
      {
         // TODO
         generalExceptionMessagePopUp(e.getMessage());
         GenerationMain.getLOGGER().warning(e.getMessage());
      }
      finally
      {
         try
         {
            if (reader != null)
            {
               reader.close();
            }
         }
         catch (Exception e)
         {
            // TODO
            generalExceptionMessagePopUp(e.getMessage());
            GenerationMain.getLOGGER().warning(e.getMessage());
         }
      }
   }

   private void generalExceptionMessagePopUp(String message)
   {
      Alert alert = new Alert(Alert.AlertType.WARNING);
      alert.setTitle("Generation Exception ");
      alert.setHeaderText("File could not be generated");
      alert.setContentText(message + " ");
      alert.showAndWait();
   }

   @Override
   public void run()
   {

      loadAppConfigurationFile();
   }

   public TextArea getLogArea()
   {
      return logArea;
   }
}
