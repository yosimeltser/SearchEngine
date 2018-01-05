package View;
import Model.Searcher;
import Model.Load;
import Model.Model;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
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
    @FXML
    public Button btn_start,run_query;
    public TextField txt_corpus, txt_posting,txt_query;
    public CheckBox check_stem;

    public void setStage(Stage other) {
        this.primaryStage = other;
        m = new Model();
        btn_start.setDisable(true);


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
    public void query_start(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information Dialog");
        alert.setHeaderText(null);
        alert.setContentText("Please wait for the retrieval");
        alert.show();
        m.findDocs(txt_query.getText(),check_stem.isSelected());
        alert.close();
    }
    public void load_start() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information Dialog");
        alert.setHeaderText(null);
        alert.setContentText("Please wait for the process to end");
        alert.show();
        long time = m.start(txt_corpus.getText(), txt_posting.getText(), check_stem.isSelected());
        alert.close();
        try {
            Stage stage = new Stage();
            stage.setTitle("Summarize");
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent root = fxmlLoader.load(getClass().getResource("Start.fxml").openStream());
            Scene scene = new Scene(root, 550, 300);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL); //Lock the window until it closes
            StartController st = fxmlLoader.getController();
            st.set(stage, m, this, time);
            stage.show();
        } catch (Exception e) {

        }
    }


    public void reset() {
        m.reset(this.txt_posting.getText());
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information Dialog");
        alert.setHeaderText(null);
        alert.setContentText("Cache, Dictionary & PostingList deleted");
        alert.show();
    }

//    public void show_cache() throws IOException {
//
//        String path = txt_posting.getText();
//        if (path.equals("") || path.equals("No Directory selected")) {
//            path = "";
//        } else {
//            path = path + "//";
//        }
//        File cache = new File(path + "Cache.txt");
//        Desktop desktop = Desktop.getDesktop();
//        if (cache.exists()) desktop.open(cache);
//
//    }

    public void choose_query() throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Queries File");
        fileChooser.showOpenDialog(primaryStage);
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("TXT", "*.txt"));
    }

    //NOTICE that the corpus & the file of the stopwords need to be at the same directory!
    public void choose_corpus() {

        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory =
                directoryChooser.showDialog(null);

        if (selectedDirectory == null) {
            txt_corpus.setText("No Directory selected");
        } else {
            txt_corpus.setText(selectedDirectory.getAbsolutePath());
            btn_start.setDisable(false);
        }
    }

    // gives u the path where to save the the posting & dictionary.
    //need to check that the return argument isn't null
//    public void choose_to_save() {
//        DirectoryChooser directoryChooser = new DirectoryChooser();
//        File selectedDirectory =
//                directoryChooser.showDialog(null);
//
//        if (selectedDirectory == null) {
//            txt_posting.setText("No Directory selected");
//        } else {
//            txt_posting.setText(selectedDirectory.getAbsolutePath());
//        }
//    }

    public void load() {
        String path="";
        if (txt_posting!=null) {
            path = txt_posting.getText();
        }
        load = new Load(path,check_stem.isSelected());
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information Dialog");
        alert.setHeaderText(null);
        alert.setContentText("Loading was Successful");
        alert.show();
    }

}

