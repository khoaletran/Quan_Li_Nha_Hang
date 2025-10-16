package ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import static ui.AppConstants.APP_LOGO;

public class Login extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(Login.class.getResource("/FXML/Login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 600);

        // Áp dụng CSS
        scene.getStylesheets().add(Login.class.getResource("/CSS/login.css").toExternalForm());

        stage.setTitle(AppConstants.APP_TITLE + " - Login");
        stage.getIcons().add(APP_LOGO);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
