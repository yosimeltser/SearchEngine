package View;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Screen extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Information Retrieval");
        FXMLLoader fxmlLoader = new FXMLLoader();
        Parent root = null;
        try {
            root = fxmlLoader.load(getClass().getResource("View.fxml").openStream());
        } catch (Exception e) {
         //   e.printStackTrace();
        }
        Scene scene = new Scene(root, 600, 500);
        primaryStage.setScene(scene);
        //--------------
        ViewController view = fxmlLoader.getController();
        view.setStage(primaryStage);
        primaryStage.setOnCloseRequest(e->{e.consume();
        view.closeProgram(); });

        //--------------
        primaryStage.show();

    }
}
