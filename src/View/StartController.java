package View;

import Model.Model;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;


public class StartController {
    Stage ds;
    Model m;
    ViewController vc;
@FXML
    Label lbl_docs,lbl_index,lbl_cache,lbl_time;

    public void set(Stage sc, Model _m, ViewController _vc,long t) {
        this.ds = sc;
        vc=_vc;
        m=_m;
      String [] info=  m.sizes(vc.txt_posting.getText(),vc.check_stem.isSelected());
      lbl_docs.setText("467767");
      lbl_index.setText(info[0]);
      lbl_cache.setText(info[1]);
      Long x=t;
      lbl_time.setText(x.toString());
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

