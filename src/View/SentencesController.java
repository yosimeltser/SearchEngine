package View;


import Model.SearchDoc;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.util.LinkedHashMap;
import java.util.Map;

public class SentencesController {
    Stage st;
    SearchDoc sd;
    String query;
    @FXML
    TextArea txtf_show;

    public void set(Stage s, String q) {
        sd = new SearchDoc();
        query = q;
        st = s;
        start();
    }

    public void start() {
        String f = "";
        // gets the sentences that were ranked
        LinkedHashMap<String, Double> res = sd.rank(query);
        int counter = 1;
        //prints the 5 most important sentences in the given doc
        if (res.size() > 0) {
            for (Map.Entry<String, Double> entry : res.entrySet()) {
                if (counter < 6) {
                    f += "Score: " + counter + System.lineSeparator() + entry.getKey() + System.lineSeparator();
                    counter++;
                }
            }
            txtf_show.setText(f);
        } else {
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setTitle("Error");
            error.setContentText("Incorrect Document ID");
            error.show();

        }
    }
}
