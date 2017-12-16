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


    public void set(Stage sc, Model _m) {
        this.ds = sc;
        m=_m;
    }
//    //tells is the user choose
//    public boolean getStemOrNot() {
//        return check_stem.isSelected();
//    }
//
//
//    public void close_win() {
//      //  System.out.println(txt_corpus.getText());
//        ds.close();
//        m.start(txt_corpus.getText(),txt_posting.getText(),check_stem.isSelected());
//
//
//    }


}

