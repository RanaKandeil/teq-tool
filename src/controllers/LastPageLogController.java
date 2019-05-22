package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Created by galalme on 5/3/2017.
 */
public class LastPageLogController
{
   @FXML
   private TextArea lastLogArea;

   public void initData(StringBuilder x)

   {
      lastLogArea.setText(x.toString());
   }

   public void backButtonAction(ActionEvent event)
   {
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
}
