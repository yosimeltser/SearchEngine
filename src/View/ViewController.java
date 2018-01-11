package View;

import Model.ExpandQuery;

import Model.Load;
import Model.Model;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class ViewController {
    Stage primaryStage;
    Model m;
    Load load;
    boolean pressed;
    @FXML
    public Button run_query;
    public TextField txt_query, query_path, save_path;
    public CheckBox check_stem, ckc_expend, ckc_summerize;

    public void setStage(Stage other) {
        this.primaryStage = other;
        m = new Model();
        pressed = false;
    }

    public void closeProgram() {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setTitle("Exit");
        a.setHeaderText("Exit");
        a.setContentText("Are you sure you want to exit the program?");
        Optional<ButtonType> result = a.showAndWait();
        if (result.get() == ButtonType.OK) {
            primaryStage.close();
        }
    }

    public void query_start() {
        if (pressed) {
            long startTime = System.currentTimeMillis();
            if (ckc_summerize.isSelected()) {
                openSummer(txt_query.getText());
            } else if (ckc_expend.isSelected()) {
                ExpandQuery ex = new ExpandQuery(txt_query.getText());
                ex.expand();
            } else if (query_path != null) {
                if (query_path.getText().equals("")) {
                    if (txt_query.getText().equals("")) {
                        showError("Please insert a query");
                        return;
                    } else {
                        Alert alert = showInfo("Please wait for the retrieval");
                        m.findDocs(txt_query.getText(), check_stem.isSelected(), 0);
                        alert.close();
                    }
                } else {
                    Alert alert = showInfo("Please wait for the retrieval");
                    m.queryChooser(query_path.getText(), check_stem.isSelected());
                    alert.close();
                }

                long endTime = System.currentTimeMillis();
                long total = (startTime - endTime) / 1000;
                //show file
                File show = new File("C:\\trec\\showFile.txt");
                Desktop desktop = Desktop.getDesktop();
                if (show.exists()) {
                    try {
                        desktop.open(show);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Running Time");
                alert.setHeaderText(null);
                alert.setContentText("The total running time is: " + total + " seconds");
                alert.showAndWait();
                alert.close();
            }
        }
    }

    public void reset() {
        txt_query = null;
        query_path = null;
        File f = new File(save_path.getText());
        pressed = false;
        m.reset(f.getPath());
        showInfo("Results file were deleted");
        System.gc();
    }

    public void save_results() {
        FileChooser fileChooser = new FileChooser();
        //Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (.txt)", ".txt");
        fileChooser.getExtensionFilters().add(extFilter);
        //Show save file dialog
        File file = fileChooser.showSaveDialog(primaryStage);
        if (file != null) {
            save_path.setText(file.getPath());
            m.save(file.getPath());
        }
    }

    public void choose_query() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Queries File");
        File f = fileChooser.showOpenDialog(primaryStage);
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("TXT", "*.txt"));
        if (f != null)
            query_path.setText(f.getPath());
    }

    public void load() {
        pressed = true;
        load = new Load(check_stem.isSelected());
        showInfo("Loading was Successful");
    }

    public void openSummer(String q) {
        try {
            Stage stage = new Stage();
            stage.setTitle("Summarize");
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent root = fxmlLoader.load(getClass().getResource("Sentences.fxml").openStream());
            Scene scene = new Scene(root, 550, 300);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL); //Lock the window until it closes
            SentencesController st = fxmlLoader.getController();
            st.set(stage, q);
            stage.show();
        } catch (Exception e) {

        }
    }

    private Alert showError(String data) {
        Alert error = new Alert(Alert.AlertType.ERROR);
        error.setTitle("Error");
        error.setContentText(data);
        error.show();
        return error;
    }

    private Alert showInfo(String data) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information Dialog");
        alert.setHeaderText(null);
        alert.setContentText(data);
        alert.show();
        return alert;
    }


}