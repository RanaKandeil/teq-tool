package controllers;

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
import sample.PropNames;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Properties;
import java.util.ResourceBundle;

public class NewStepController implements Initializable
{

   @FXML
   private Button backBtn, ContinueButton;

   @FXML
   private TextField ProjectPathTextField;

   @FXML
   private TextField ProjectName;

   @FXML
   private TextField UserName;

   @FXML
   private TextField DialogClassName;

   @FXML
   private TextField DialogPackageName;

   @FXML
   private TextField StepsPackageName;

   @FXML
   private TextField ViewsPackageName;

   @FXML
   private TextField ServiceClientSide;

   @FXML
   private TextField ServiceServerSide;

   @FXML
   private TextField RepoRoot;

   @FXML
   private TextField GwtRoot;

   @FXML
   private TextField AppCoreVersion;

   @FXML
   private TextField WmsVersion;

   @FXML
   private TextField CorVersion;

   @FXML
   private Label requiredDialogClassName;

   @FXML
   private Label requiredDialogPackageName;

   @FXML
   private Label requiredStepsPackageName;

   @FXML
   private Label requiredViewsPackageName;

   @FXML
   private Label requiredServiceClientSide;

   @FXML
   private Label requiredServiceServerSide;

   @FXML
   private Label requiredRepoRoot;

   @FXML
   private Label requiredGWTpath;

   @FXML
   private Label requiredAppCoreVersion;

   @FXML
   private Label requiredwmsVersion;

   @FXML
   private Label requiredCoreVersion;

   static HashMap<String, String> valuess = new HashMap<String, String>();

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

   private FadeTransition fadeOut11 = new FadeTransition(
         Duration.millis(3000));

   @Override
   public void initialize(URL location, ResourceBundle resources)
   {
      // TODO Auto-generated method stub
      Properties prop = new Properties();
      InputStream input = null;

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

      fadeOut11.setFromValue(1.0);
      fadeOut11.setToValue(0.0);
      fadeOut11.setCycleCount(1);
      fadeOut11.setAutoReverse(true);

      fadeOut11.play();

      try
      {
         input = new FileInputStream("C:/TEQ/properties/config.properties");
         // load a properties file from class path, inside static method
         prop.load(input);

         // get the property value and print it out
         ProjectName.setText(prop.getProperty(PropNames.ProjectName));
         ProjectPathTextField.setText(prop
               .getProperty(PropNames.ProjectPath));
         UserName.setText(prop.getProperty(PropNames.UserName));

         GwtRoot.setText(prop.getProperty(PropNames.GWTPath));
         AppCoreVersion.setText(prop.getProperty(PropNames.AppCoreVersion));
         WmsVersion.setText(prop.getProperty(PropNames.WMSVersion));
         CorVersion.setText(prop.getProperty(PropNames.CORVersion));
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

   @FXML
   private void browseButtonAction()
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

   @FXML
   private void gwtPathbrowseButtonAction()
   {
      DirectoryChooser chooser = new DirectoryChooser();
      chooser.setTitle("Choose Path");
      File selectedDirectory = chooser.showDialog(new Stage());
      if (selectedDirectory == null)
         GwtRoot.setText(GwtRoot.getText());
      else
         GwtRoot.setText(selectedDirectory.getAbsolutePath());
   }

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

   public void continueButtonAction(ActionEvent event)
   {

      if (validate())
      {
         FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
               "/fxml/DynamicPage.fxml"));
         Parent root;

         String dialogPackageName = DialogPackageName.getText();
         String slashedDialogPackageName = dialogPackageName.replaceAll("\\.", "\\/");
         String[] resultDialogPackageName = dialogPackageName.split("\\.", 12);
         String searchWordInDialogPachageName = resultDialogPackageName[3];
         String serviceServerSide = ServiceServerSide.getText();
         String slashedServiceServertSidePackageName = serviceServerSide.replaceAll("\\.", "\\/");
         String serviceClientSidePackageName = ServiceClientSide.getText();
         String slashedServiceClienttSidePackageName = serviceClientSidePackageName.replaceAll("\\.", "\\/");
         String mvnRootString = RepoRoot.getText();
         String gwtRoot = GwtRoot.getText();
         String wmsVersion = WmsVersion.getText();
         String appCoreVersion = AppCoreVersion.getText();
         String dialogName = DialogClassName.getText();
         try
         {
            root = (Parent) fxmlLoader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            DynamicPage controller = fxmlLoader.<DynamicPage>getController();
            controller.loadClass(searchWordInDialogPachageName, slashedDialogPackageName, slashedServiceClienttSidePackageName, slashedServiceServertSidePackageName,
                  mvnRootString, gwtRoot, appCoreVersion, wmsVersion, dialogName);
            setValues();
            controller.setMapData(valuess);

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

   private boolean validate()
   {
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

      if (!StepsPackageName.getText().isEmpty())
         requiredStepsPackageName.setVisible(false);
      else
      {
         requiredStepsPackageName.setVisible(true);
         fadeOut3.setNode(requiredStepsPackageName);
         fadeOut3.playFromStart();

         count++;
      }
      if (!ViewsPackageName.getText().isEmpty())
         requiredViewsPackageName.setVisible(false);
      else
      {
         requiredViewsPackageName.setVisible(true);
         fadeOut4.setNode(requiredViewsPackageName);
         fadeOut4.playFromStart();

         count++;
      }
      if (!ServiceClientSide.getText().isEmpty())
         requiredServiceClientSide.setVisible(false);
      else
      {
         requiredServiceClientSide.setVisible(true);
         fadeOut5.setNode(requiredServiceClientSide);
         fadeOut5.playFromStart();

         count++;
      }
      if (!ServiceServerSide.getText().isEmpty())
         requiredServiceServerSide.setVisible(false);
      else
      {
         requiredServiceServerSide.setVisible(true);
         fadeOut6.setNode(requiredServiceServerSide);
         fadeOut6.playFromStart();

         count++;
      }
      if (!RepoRoot.getText().isEmpty())
         requiredRepoRoot.setVisible(true);
      else
      {
         requiredRepoRoot.setVisible(true);
         fadeOut7.setNode(requiredRepoRoot);
         fadeOut7.playFromStart();
         count++;
      }
      if (!GwtRoot.getText().isEmpty())
         requiredGWTpath.setVisible(false);
      else
      {
         requiredGWTpath.setVisible(true);
         fadeOut8.setNode(requiredGWTpath);
         fadeOut8.playFromStart();
         count++;
      }
      if (!AppCoreVersion.getText().isEmpty())
         requiredAppCoreVersion.setVisible(false);
      else
      {
         requiredAppCoreVersion.setVisible(true);
         fadeOut9.setNode(requiredAppCoreVersion);
         fadeOut9.playFromStart();
         count++;
      }
      if (!WmsVersion.getText().isEmpty())
         requiredwmsVersion.setVisible(false);
      else
      {
         requiredwmsVersion.setVisible(true);
         fadeOut10.setNode(requiredwmsVersion);
         fadeOut10.playFromStart();
         count++;
      }
      if (!CorVersion.getText().isEmpty())
         requiredCoreVersion.setVisible(false);
      else
      {
         requiredCoreVersion.setVisible(true);
         fadeOut11.setNode(requiredCoreVersion);
         fadeOut11.playFromStart();
         count++;
      }

      if (count > 0)
         return false;
      return true;
   }

   public HashMap<String, String> setValues()
   {
      String projectName = ProjectName.getText();
      String userName = UserName.getText();
      String projectPath = ProjectPathTextField.getText();
      String serviceClientSide = ServiceClientSide.getText();
      String serviceServerSide = ServiceServerSide.getText();
      String stepsPackageName = StepsPackageName.getText();
      String viewsPackageName = ViewsPackageName.getText();
      String dialogClassName = DialogClassName.getText();
      String dialogPackageName = DialogPackageName.getText();
      // String mvnRoot = RepoRoot.getText();
      String gwtRoot = GwtRoot.getText();
      String appCoreVersion = AppCoreVersion.getText();
      String wmsVersion = WmsVersion.getText();
      String dialogName = DialogClassName.getText();
      valuess.put("ProjectName", projectName);
      valuess.put("UserName", userName);
      valuess.put("projectPath", projectPath);
      valuess.put("serviceClientSide", serviceClientSide);
      valuess.put("serviceServerSide", serviceServerSide);
      valuess.put("stepsPackageName", stepsPackageName);
      valuess.put("viewsPackageName", viewsPackageName);
      valuess.put("dialogClassName", dialogClassName);
      valuess.put("dialogPackageName", dialogPackageName);
      // valuess.put("mavenRoot", mvnRoot);
      valuess.put("gwtRoot", gwtRoot);
      valuess.put("appCoreVersion", appCoreVersion);
      valuess.put("wmsVersion", wmsVersion);

      return valuess;
   }
}
