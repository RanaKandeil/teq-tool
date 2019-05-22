package controllers;

import com.advansys.generator.common.GenerationMain;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class PageTwoController implements Initializable
{

   @FXML
   private Button createNewDialogButton;

   @FXML
   private Button createNewStepButton;

   @FXML
   private Button createNewEventButton;

   @FXML
   private Button backButton;

   @Override
   public void initialize(URL location, ResourceBundle resources)
   {
      // TODO Auto-generated method stub
      createNewStepButton.setDisable(true);
      createNewEventButton.setDisable(true);
   }

   @FXML
   public void handleCreateNewButtonAction(ActionEvent event)
   {
      FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/pageThree.fxml"));
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

   @FXML
   public void handleNewStepButtonAction(ActionEvent event)
   {
      FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/pageSix.fxml"));
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

   @FXML
   public void handleNewEventButtonAction(ActionEvent event)
   {
      // TODO
      FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/pageFive.fxml"));
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

   public void settingButtonAction(ActionEvent event)
   {

      FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/StartView.fxml"));
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

   public void BackButtonAction(ActionEvent event)
   {

      FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/DialogTypes.fxml"));
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

   private void generalExceptionMessagePopUp(String message)
   {
      Alert alert = new Alert(Alert.AlertType.WARNING);
      alert.setTitle("Generation Exception ");
      alert.setHeaderText("File could not be generated");
      alert.setContentText(message + " ");
      alert.showAndWait();
   }
}
