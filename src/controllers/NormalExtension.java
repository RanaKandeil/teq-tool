package controllers;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

public class NormalExtension implements Initializable {

	@FXML
	TextField ProjectPathTextField;
	@FXML
	TextField ProjectName;
	@FXML
	TextField UserName;
	@FXML
	TextField DialogClassName;
	@FXML
	TextField DialogPackageName;
	@FXML
	TextField RepoRoot;
	@FXML
	TextField GwtRoot;
	@FXML
	TextField AppCoreVersion;
	@FXML
	TextField WmsVersion;
	@FXML
	TextField CorVersion;
	
	@FXML
	Label requiredProjectPath;
	@FXML
	Label requiredProjectName;
	@FXML
	Label requiredUserName;
	@FXML
	Label requiredDialogClassName;
	@FXML
	Label requiredDialogPackageName;
	@FXML
	Label requiredRepoRoot;
	@FXML
	Label requiredGWTpath;
	@FXML
	Label requiredAppCoreVersion;
	@FXML
	Label requiredwmsVersion;
	@FXML
	Label requiredCoreVersion;
	
	 private FadeTransition fadeOut1 = new FadeTransition(
	         Duration.millis(3000));

	 private FadeTransition fadeOut2 = new FadeTransition(
	         Duration.millis(3000));

     private FadeTransition fadeOut3 = new FadeTransition(
	         Duration.millis(3000));

     private FadeTransition fadeOut4 = new FadeTransition(
	         Duration.millis(3000));

     private FadeTransition fadeOut5 = new FadeTransition(
	         Duration.millis(3000));

	 private FadeTransition fadeOut6 = new FadeTransition(
	         Duration.millis(3000));
	 
	 private FadeTransition fadeOut7 = new FadeTransition(
	         Duration.millis(3000));
	 
	 private FadeTransition fadeOut8 = new FadeTransition(
	         Duration.millis(3000));
	 
	 private FadeTransition fadeOut9 = new FadeTransition(
	         Duration.millis(3000));
	 
	 private FadeTransition fadeOut10 = new FadeTransition(
	         Duration.millis(3000));
	 
	   
	   
	
	public void initialize(URL arg0, ResourceBundle arg1) {
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

	      fadeOut5.setFromValue(1.0);
	      fadeOut5.setToValue(0.0);
	      fadeOut5.setCycleCount(1);
	      fadeOut5.setAutoReverse(true);

	      fadeOut5.play();

	      fadeOut6.setFromValue(1.0);
	      fadeOut6.setToValue(0.0);
	      fadeOut6.setCycleCount(1);
	      fadeOut6.setAutoReverse(true);

	      fadeOut6.play();
	      
	      fadeOut7.setFromValue(1.0);
	      fadeOut7.setToValue(0.0);
	      fadeOut7.setCycleCount(1);
	      fadeOut7.setAutoReverse(true);

	      fadeOut7.play();
	      
	      fadeOut8.setFromValue(1.0);
	      fadeOut8.setToValue(0.0);
	      fadeOut8.setCycleCount(1);
	      fadeOut8.setAutoReverse(true);
	      
	      fadeOut8.play();
	      
	      fadeOut9.setFromValue(1.0);
	      fadeOut9.setToValue(0.0);
	      fadeOut9.setCycleCount(1);
	      fadeOut9.setAutoReverse(true);
	      
	      fadeOut9.play();
	      
	      fadeOut10.setFromValue(1.0);
	      fadeOut10.setToValue(0.0);
	      fadeOut10.setCycleCount(1);
	      fadeOut10.setAutoReverse(true);
	      
	      fadeOut10.play();
	}
	
 @FXML
   private void ProjectPathBrowseButtonAction()
	  {
	      DirectoryChooser chooser = new DirectoryChooser();
	      chooser.setTitle("Choose Path");
	      File selectedDirectory = chooser.showDialog(new Stage());
	      if (selectedDirectory == null)
	         ProjectPathTextField.setText(ProjectPathTextField.getText());
	      else
	         ProjectPathTextField.setText(selectedDirectory.getAbsolutePath());
	   }
 
 @FXML
  private void repoRootbrowseButtonAction()
     {
          DirectoryChooser chooser = new DirectoryChooser();
          chooser.setTitle("Choose Path");
          File selectedDirectory = chooser.showDialog(new Stage());
          if (selectedDirectory == null)
             RepoRoot.setText(RepoRoot.getText());
          else
             RepoRoot.setText(selectedDirectory.getAbsolutePath());
    }
 
 
 public void backButtonAction(ActionEvent event)
 {
         FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
              "/fxml/OVDialog.fxml"));
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
 
 public void continueButtonAction(ActionEvent event){
	 Validate();
 }
 
 private boolean Validate() {

	 int count = 0;
     if (!DialogClassName.getText().isEmpty())
        requiredDialogClassName.setVisible(false);
     else
     {
        requiredDialogClassName.setVisible(true);
        fadeOut1.setNode(requiredDialogClassName);
        fadeOut1.playFromStart();

        count++;
     }
     if (!DialogPackageName.getText().isEmpty())
         requiredDialogPackageName.setVisible(false);
      else
      {
         requiredDialogPackageName.setVisible(true);
         fadeOut2.setNode(requiredDialogPackageName);
         fadeOut2.playFromStart();

         count++;
      }
     if (!RepoRoot.getText().isEmpty())
         requiredRepoRoot.setVisible(true);
      else
      {
         requiredRepoRoot.setVisible(true);
         fadeOut3.setNode(requiredRepoRoot);
         fadeOut3.playFromStart();
         count++;
      }
      if (!GwtRoot.getText().isEmpty())
         requiredGWTpath.setVisible(false);
      else
      {
         requiredGWTpath.setVisible(true);
         fadeOut4.setNode(requiredGWTpath);
         fadeOut4.playFromStart();
         count++;
      }
      if (!AppCoreVersion.getText().isEmpty())
         requiredAppCoreVersion.setVisible(false);
      else
      {
         requiredAppCoreVersion.setVisible(true);
         fadeOut5.setNode(requiredAppCoreVersion);
         fadeOut5.playFromStart();
         count++;
      }
      if (!WmsVersion.getText().isEmpty())
         requiredwmsVersion.setVisible(false);
      else
      {
         requiredwmsVersion.setVisible(true);
         fadeOut6.setNode(requiredwmsVersion);
         fadeOut6.playFromStart();
         count++;
      }
      if (!CorVersion.getText().isEmpty())
         requiredCoreVersion.setVisible(false);
      else
      {
         requiredCoreVersion.setVisible(true);
         fadeOut7.setNode(requiredCoreVersion);
         fadeOut7.playFromStart();
         count++;
      }
      if(!ProjectPathTextField.getText().isEmpty())
    	  requiredProjectPath.setVisible(false);
      else{
    	  requiredProjectPath.setVisible(true);
    	  fadeOut8.setNode(requiredProjectPath);
    	  fadeOut8.playFromStart();
    	  count++;
      }
      if(!ProjectName.getText().isEmpty())
    	  requiredProjectName.setVisible(false);
      else{
    	  requiredProjectName.setVisible(true);
    	  fadeOut9.setNode(requiredProjectName);
    	  fadeOut9.playFromStart();
    	  count++;
      }
      if(!UserName.getText().isEmpty())
    	  requiredUserName.setVisible(false);
      else{
    	  requiredUserName.setVisible(true);
    	  fadeOut10.setNode(requiredUserName);
    	  fadeOut10.playFromStart();
    	  count++;
      }

      if (count > 0)
         return false;
      return true;
}

}
