package controllers;

import com.advansys.generator.common.GenerationMain;
import com.advansys.generator.common.GenerationType;
import com.dematic.wms.app.base.control.dd.GenData;
import javafx.animation.FadeTransition;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.json.JSONException;
import sample.PropNames;

import java.io.*;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Properties;
import java.util.ResourceBundle;

public class PageThreeController implements Initializable
{

   @FXML
   private TextField projectPath;

   @FXML
   private TextField projectName;

   @FXML
   private TextField dialogName;

   @FXML
   private Button addRowImage;

   @FXML
   private TextField userName;

   @FXML
   private Button browseButton;

   @FXML
   private Button createNewButton;

   @FXML
   private TableView<StepAndView> stepTableView;

   @FXML
   private TableColumn<StepAndView, String> stepColumn;

   @FXML
   private TableColumn<StepAndView, String> viewColumn;

   @FXML
   private TableColumn<StepAndView, Button> deleteColumn;

   @FXML
   private TextField step;

   @FXML
   private TextField view;

   @FXML
   private Button addRow;

   @FXML
   private Button removeRow;

   @FXML
   private Button backButton;

   @FXML
   private Label requiredProjectPath;

   @FXML
   private Label requiredProjectName;

   @FXML
   private Label requiredDialogName;

   @FXML
   private Label requiredUserName;

   @FXML
   private ComboBox<String> alreadyExistingView;

   @FXML
   private CheckBox chooseFromExistingView;

   @FXML
   private CheckBox generateEventButton;

   public StringBuilder sb;

   @FXML
   Stage stage;

   private FadeTransition fadeOut1 = new FadeTransition(
         Duration.millis(3000));

   private FadeTransition fadeOut2 = new FadeTransition(
         Duration.millis(3000));

   private FadeTransition fadeOut3 = new FadeTransition(
         Duration.millis(3000));

   private FadeTransition fadeOut4 = new FadeTransition(
         Duration.millis(3000));

   @Override
   public void initialize(URL location, ResourceBundle resources)
   {

      view.setDisable(false);
      alreadyExistingView.setDisable(true);

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

      Properties prop = new Properties();
      InputStream input = null;

      try
      {

         String filename = "config.properties";
         //			input = getClass().getClassLoader().getResourceAsStream(filename);
         //			if (input == null) {
         //				System.out.println("Sorry, unable to find " + filename);
         //
         //			}
         input = new FileInputStream("C:/TEQ/properties/config.properties");
         // load a properties file from class path, inside static method
         prop.load(input);

         // get the property value and print it out
         projectName.setText(prop.getProperty(PropNames.ProjectName));
         projectPath.setText(prop.getProperty(PropNames.ProjectPath));
         userName.setText(prop.getProperty(PropNames.UserName));
      }
      catch (Exception ex)
      {
         // TODO
         generalExceptionMessagePopUp(ex.getMessage());
         GenerationMain.getLOGGER().warning(ex.getMessage());
      }
      finally
      {
         if (input != null)
         {
            try
            {

               input.close();
            }
            catch (Exception e)
            {
               // TODO
               generalExceptionMessagePopUp(e.getMessage());
               GenerationMain.getLOGGER().warning(e.getMessage());
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
         projectPath.setText(projectPath.getText());
      else
         projectPath.setText(selectedDirectory.getAbsolutePath());
   }

   @FXML
   private void createNewButtonAction(ActionEvent event)
   {

      LinkedHashMap<String, String> stepViewMap = new LinkedHashMap<>();
      ObservableList<StepAndView> observableList = stepTableView.getItems();
      if (validate())
      {
         if (observableList.size() > 0)
         {
            for (StepAndView list : observableList)
            {
               stepViewMap.put(list.getStep(), list.getView());
            }
            // Method to be called to generate
            try
            {
               File sourceFile = new File(System.getProperty("user.home") + "/AppData/Local/teqtool");
               if (!sourceFile.exists()) {
                  sourceFile.mkdirs();
               }

               runLMBTool(userName.getText(), projectPath.getText(),
                     projectName.getText(), dialogName.getText(),
                     stepViewMap, generateEventButton.isSelected());
            }
            catch (Exception ex)
            {
               // TODO
               generalExceptionMessagePopUp(ex.getMessage());
               GenerationMain.getLOGGER().warning(ex.getMessage());
            }
         }
         else
         {
            showTableEmptyPopUp();
         }
         showCreationPopUp(event);
      }
   }

   @FXML
   private void addRowAction()
   {
      if (!step.getText().isEmpty()
            && (!view.getText().isEmpty() || alreadyExistingView.getValue() != null))
      {
         if (alreadyExistingView.getValue() != null
               && !alreadyExistingView.getValue().isEmpty())
         {
            stepTableView.getItems().add(
                  new StepAndView(step.getText(), alreadyExistingView
                        .getValue()));
         }
         else
         {
            stepTableView.getItems().add(
                  new StepAndView(step.getText(), view.getText()));
            alreadyExistingView.getItems().add(view.getText());
         }
         step.clear();
         view.clear();
         alreadyExistingView.setValue("");
      }
      else
      {
         Alert alert = new Alert(AlertType.ERROR);
         alert.setTitle("No Data Added");
         alert.setHeaderText("No StepAndView Added");
         alert.setContentText("Please Enter a step and view .");
         alert.showAndWait();
      }
   }

   @FXML
   private void removeRowAction()
   {
      int selectedIndex = stepTableView.getSelectionModel()
            .getSelectedIndex();
      if (selectedIndex >= 0)
      {
         stepTableView.getItems().remove(selectedIndex);
      }
      else if (stepTableView.getItems().isEmpty())
      {
         showTableEmptyPopUp();
      }
      else
      {
         // Nothing selected.
         Alert alert = new Alert(AlertType.WARNING);
         alert.setTitle("No Selection");
         alert.setHeaderText("No StepAndView Selected");
         alert.setContentText("Please select a step and view in the table.");
         alert.showAndWait();
      }
   }

   private void runLMBTool(String userNameText, String projectPathText,
                           String projectNameText, String dialogNameText,
                           LinkedHashMap<String, String> stepViewMap, boolean isGenerateEvent)
   {
      try {
         GenerationMain generation = new GenerationMain();
         sb = generation.createNewStepByStepDialog(projectPathText,
                 projectNameText, dialogNameText, stepViewMap, userNameText,
                 isGenerateEvent, GenerationType.STEP_BY_STEP);
      }
      catch (JSONException e){
         GenerationMain.getLOGGER().warning("JSON file is not found" + e.getMessage());
      }

   }

   private void showTableEmptyPopUp()
   {
      Alert alert = new Alert(AlertType.WARNING);
      alert.setTitle("No Step or view ");
      alert.setHeaderText("No Step or view to remove");
      alert.setContentText("Table is Empty , No data to remove !");
      alert.showAndWait();
   }

   private void generalExceptionMessagePopUp(String message)
   {
      Alert alert = new Alert(AlertType.WARNING);
      alert.setTitle("Generation Exception ");
      alert.setHeaderText("File could not be generated");
      alert.setContentText(message + " ");
      alert.showAndWait();
   }

   private boolean validate()
   {
      int count = 0;
      if (!projectPath.getText().isEmpty())
         requiredProjectPath.setVisible(false);
      else
      {
         requiredProjectPath.setVisible(true);
         fadeOut1.setNode(requiredProjectPath);
         fadeOut1.playFromStart();

         count++;
      }

      if (!projectName.getText().isEmpty())
         requiredProjectName.setVisible(false);
      else
      {
         requiredProjectName.setVisible(true);
         fadeOut2.setNode(requiredProjectName);
         fadeOut2.playFromStart();

         count++;
      }

      if (!dialogName.getText().isEmpty())
         requiredDialogName.setVisible(false);
      else
      {
         requiredDialogName.setVisible(true);
         fadeOut3.setNode(requiredDialogName);
         fadeOut3.playFromStart();

         count++;
      }
      if (!userName.getText().isEmpty())
         requiredUserName.setVisible(false);
      else
      {
         requiredUserName.setVisible(true);
         fadeOut4.setNode(requiredUserName);
         fadeOut4.playFromStart();

         count++;
      }

      if (count > 0)
         return false;
      return true;
   }

   private void showCreationPopUp(ActionEvent event)
   {
      FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
            "/fxml/pageFour.fxml"));
      Parent root;
      try
      {
         root = (Parent) fxmlLoader.load();
         Stage stage = new Stage();
         stage.setScene(new Scene(root));
         PageFourController controller = fxmlLoader
               .<PageFourController>getController();
         controller.initData(projectPath.getText(), sb);
         (((Node) event.getSource())).getScene().getWindow().hide();

         Image applicationIcon = new Image(getClass().getResourceAsStream("/images/clock.png"));
         stage.getIcons().add(applicationIcon);
         stage.setTitle("TEQ Tool");
         stage.show();
      }
      catch (Exception e)
      {
         // TODO
         generalExceptionMessagePopUp(e.getMessage());
         GenerationMain.getLOGGER().warning(e.getMessage());
      }
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
      catch (Exception e)
      {
         // TODO
         generalExceptionMessagePopUp(e.getMessage());
         GenerationMain.getLOGGER().warning(e.getMessage());
      }
   }

   @FXML
   private void onChooseFromExistingViewEvent()
   {
      if (chooseFromExistingView.isSelected())
      {
         view.setDisable(true);
         alreadyExistingView.setDisable(false);
      }
      else
      {
         view.setDisable(false);
         alreadyExistingView.setDisable(true);
      }
   }
}
