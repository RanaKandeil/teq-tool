package controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class OVDialogController implements Initializable {

	@FXML
	Button NormalExtensionsButton;
	
	@FXML
	Button PreDefinedTasksButton;
	
	@FXML
	Button settingbtn;
	
	
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		
	}
	

	   public void NormalExtensionBtnAction(ActionEvent event)
	   {
	      FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
	            "/fxml/normalExtension.fxml"));
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
	   
	   public void PreDefinedTaskBtnAction(ActionEvent event)
	   {
	      FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
	            "/fxml/OVDialog.fxml"));
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
	   
	   public void backButtonAction(ActionEvent event)
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

}
