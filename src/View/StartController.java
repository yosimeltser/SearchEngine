package View;

import Model.Model;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;


public class StartController {
    Stage ds;
    Model m;
    @FXML
    public TextField txt_corpus, txt_posting;
    public CheckBox check_stem;

    public void set(Stage sc, Model _m) {
        this.ds = sc;
        m=_m;
    }
    //tells is the user choose
    public boolean getStemOrNot() {
        return check_stem.isSelected();
    }
//NOTICE that the corpus & the file of the stopwords need to be at the same directory!
    public void choose_corpus() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory =
                directoryChooser.showDialog(ds);

        if (selectedDirectory == null) {
            txt_corpus.setText("No Directory selected");
        } else {
            txt_corpus.setText(selectedDirectory.getAbsolutePath());
        }
    }

    public void close_win() {
      //  System.out.println(txt_corpus.getText());
        m.start(txt_corpus.getText(),txt_posting.getText(),check_stem.isSelected());
        ds.close();
    }
// gives u the path where to save the the posting & dictionary.
    //need to check that the return argument isn't null
    public void choose_to_save() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory =
                directoryChooser.showDialog(ds);

        if (selectedDirectory == null) {
            txt_posting.setText("No Directory selected");
        } else {
            txt_posting.setText(selectedDirectory.getAbsolutePath());
        }
    }

}

