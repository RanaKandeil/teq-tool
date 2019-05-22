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
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class PageOneController implements Initializable
{

   @FXML
   private Button button;

   @Override
   public void initialize(URL location, ResourceBundle resources)
   {
      // TODO Auto-generated method stub

   }

   @FXML
   public void handleButtonAction(ActionEvent event)
   {
      FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/pageTwo.fxml"));
      Parent root;
      try
      {
         root = (Parent) fxmlLoader.load();
         Stage stage = new Stage();
         stage.setScene(new Scene(root));
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
