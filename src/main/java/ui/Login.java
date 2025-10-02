package ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Login extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Login.class.getResource("/FXML/hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 500);

        // load CSS đúng folder
        scene.getStylesheets().add(Login.class.getResource("/CSS/login.css").toExternalForm());


        stage.setTitle("CrabKing - Đăng nhập");
        stage.setScene(scene);
        stage.show();
    }

}
