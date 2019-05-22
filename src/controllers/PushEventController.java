package controllers;

import com.advansys.generator.common.GenerationMain;
import com.advansys.generator.common.GenerationType;
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

public class PushEventController implements Initializable {
    @FXML
    private TextField projectPath;

    @FXML
    private TextField projectName;

    @FXML
    private TextField dialogClassName;

    @FXML
    private TextField stepClassName;

    @FXML
    private TextField pushEventClassName;

    @FXML
    private TextField userName;

    @FXML
    private Label requiredProjectPath;

    @FXML
    private Label requiredProjectName;

    @FXML
    private Label requiredUserName;

    @FXML
    private Label requiredDialogClassName;

    @FXML
    private Label requiredStepClassName;

    @FXML
    private Label requiredPushEventClassName;

    @FXML
    private Button backButton;

    @FXML
    private TableView<KeyAndValue> keyvalueTable;

    @FXML
    private TableColumn<KeyAndValue, String> keyColumn;

    @FXML
    private TableColumn<KeyAndValue, String> valueColumn;

    @FXML
    private TextField AttributeTextField;

    @FXML
    private ComboBox<String> value;

    @FXML
    private Button addRow;

    @FXML
    private Button removeRow;

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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
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
        try {
            input = new FileInputStream("C:/TEQ/properties/config.properties");
            // load a properties file from class path, inside static method
            prop.load(input);

            // get the property value and print it out
            projectName.setText(prop.getProperty(PropNames.ProjectName));
            projectPath.setText(prop.getProperty(PropNames.ProjectPath));
            userName.setText(prop.getProperty(PropNames.UserName));
        } catch (Exception ex) {
            // TODO
            generalExceptionMessagePopUp(ex.getMessage());
            GenerationMain.getLOGGER().warning(ex.getMessage());
        } finally {
            if (input != null) {
                try {

                    input.close();
                } catch (Exception e) {
                    // TODO
                    generalExceptionMessagePopUp(e.getMessage());
                    GenerationMain.getLOGGER().warning(e.getMessage());
                }
            }
        }
    }

    public void backButtonAction(ActionEvent event) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
                "/fxml/pageTwo.fxml"));
        Parent root;
        try {
            root = (Parent) fxmlLoader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            Image applicationIcon = new Image(getClass().getResourceAsStream("/images/clock.png"));
            stage.getIcons().add(applicationIcon);
            stage.setTitle("TEQ Tool");
            stage.show();
            (((Node) event.getSource())).getScene().getWindow().hide();
        } catch (Exception e) {
            // TODO
            generalExceptionMessagePopUp(e.getMessage());
            GenerationMain.getLOGGER().warning(e.getMessage());
        }
    }

    private boolean validate() {
        int count = 0;
        if (!projectPath.getText().isEmpty())
            requiredProjectPath.setVisible(false);
        else {
            requiredProjectPath.setVisible(true);
            fadeOut1.setNode(requiredProjectPath);
            fadeOut1.playFromStart();

            count++;
        }

        if (!projectName.getText().isEmpty())
            requiredProjectName.setVisible(false);
        else {
            requiredProjectName.setVisible(true);
            fadeOut2.setNode(requiredProjectName);
            fadeOut2.playFromStart();

            count++;
        }

        if (!userName.getText().isEmpty())
            requiredUserName.setVisible(false);
        else {
            requiredUserName.setVisible(true);
            fadeOut3.setNode(requiredUserName);
            fadeOut3.playFromStart();

            count++;
        }
        if (!dialogClassName.getText().isEmpty())
            requiredDialogClassName.setVisible(false);
        else {
            requiredDialogClassName.setVisible(true);
            fadeOut4.setNode(requiredDialogClassName);
            fadeOut4.playFromStart();

            count++;
        }
        if (!stepClassName.getText().isEmpty())
            requiredStepClassName.setVisible(false);
        else {
            requiredStepClassName.setVisible(true);
            fadeOut5.setNode(requiredStepClassName);
            fadeOut5.playFromStart();

            count++;
        }
        if (!pushEventClassName.getText().isEmpty())
            requiredPushEventClassName.setVisible(false);
        else {
            requiredPushEventClassName.setVisible(true);
            fadeOut6.setNode(requiredPushEventClassName);
            fadeOut6.playFromStart();

            count++;
        }

        if (count > 0)
            return false;
        return true;
    }

    @FXML
    public void createNewPushEvent(ActionEvent event) {
        LinkedHashMap<String, String> attributesMap = new LinkedHashMap<>();
        ObservableList<KeyAndValue> observableList = keyvalueTable.getItems();
        if (validate()) {
            if (observableList.size() > 0) {
                for (KeyAndValue list : observableList) {
                    attributesMap.put(list.getKey(), list.getValue());
                }
                // Method to be called to generate
                try {
                    runLMBTool(userName.getText(), projectPath.getText(),
                            projectName.getText(), dialogClassName.getText(), stepClassName.getText(), pushEventClassName.getText(),
                            attributesMap);
                } catch (Exception e) {
                    // TODO
                    generalExceptionMessagePopUp(e.getMessage());
                    GenerationMain.getLOGGER().warning(e.getMessage());
                }

                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
                        "/fxml/pageTwo.fxml"));
                Parent root;
                try {
                    root = (Parent) fxmlLoader.load();
                    Stage stage = new Stage();
                    stage.setScene(new Scene(root));

                    Image applicationIcon = new Image(getClass().getResourceAsStream("/images/clock.png"));
                    stage.getIcons().add(applicationIcon);
                    stage.setTitle("TEQ Tool");
                    stage.show();
                    (((Node) event.getSource())).getScene().getWindow().hide();
                } catch (Exception e) {
                    // TODO
                    generalExceptionMessagePopUp(e.getMessage());
                    GenerationMain.getLOGGER().warning(e.getMessage());
                }
            }
        }
    }

    private void runLMBTool(String userNameText, String projectPathText,
                            String projectNameText, String dialogNameText, String stepNameText, String pushEventNameText,
                            LinkedHashMap<String, String> attributesMap)
    {
        try {
            GenerationMain generation = new GenerationMain();
            generation.createNewPushEvent(projectPathText, projectNameText,
                    userNameText, dialogNameText, stepNameText, pushEventNameText, attributesMap, GenerationType.PUSH_EVENT);
        }
        catch (JSONException e){
            GenerationMain.getLOGGER().warning("JSON file is not found" + e.getMessage());
        }

    }

    @FXML
    private void browseButtonAction() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Choose Path");
        File selectedDirectory = chooser.showDialog(new Stage());
        if (selectedDirectory == null)
            projectPath.setText(projectPath.getText());
        else
            projectPath.setText(selectedDirectory.getAbsolutePath());
    }

    @FXML
    private void addRowAction() {

        if (AttributeTextField != null && value != null && !AttributeTextField.getText().isEmpty()
                && (!value.getValue().isEmpty())) {

            keyvalueTable.getItems().add(
                    new KeyAndValue(AttributeTextField.getText(), value.getValue()));

            AttributeTextField.clear();
        } else {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("No Data Added");
            alert.setHeaderText("No Attribute or Type Added");
            alert.setContentText("Please Enter an Attribute and Type .");
            alert.showAndWait();
        }
    }

    @FXML
    private void removeRowAction() {
        int selectedIndex = keyvalueTable.getSelectionModel()
                .getSelectedIndex();
        if (selectedIndex >= 0) {
            keyvalueTable.getItems().remove(selectedIndex);
        } else if (keyvalueTable.getItems().isEmpty()) {
            showTableEmptyPopUp();
        } else {
            // Nothing selected.
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText("No Key or Value Selected");
            alert.setContentText("Please select a key and value in the table.");
            alert.showAndWait();
        }
    }

    private void showTableEmptyPopUp() {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle("No key or value ");
        alert.setHeaderText("No key or value to remove");
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
}
