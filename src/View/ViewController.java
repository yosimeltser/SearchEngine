package View;

import Model.Model;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class ViewController {
    Stage primaryStage;
    Model m;
    StartController st;
    //public Model md;

    public void setStage(Stage other) {
        this.primaryStage = other;
        m = new Model();
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

    public void load_start() {
        try {
            Stage stage = new Stage();
            stage.setTitle("Choose DataSet");
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent root = fxmlLoader.load(getClass().getResource("Start.fxml").openStream());
            Scene scene = new Scene(root, 550, 300);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL); //Lock the window until it closes
            StartController st = fxmlLoader.getController();
            st.set(stage, m);
            stage.show();


        } catch (Exception e) {

        }
    }

    //zohar!!
        public void reset(){
       m.reset(st.txt_posting.getText());
    }
    public void show_cache() throws IOException {
        File file = new File("src/resource/stopword.txt");
        Desktop desktop = Desktop.getDesktop();
        if (file.exists()) desktop.open(file);

    }
}

