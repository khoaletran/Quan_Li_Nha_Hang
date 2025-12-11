package ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ui.controllers.LoginController;
import ui.controllers.SplashController;

import static ui.AppConstants.APP_LOGO;

public class Login extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle(AppConstants.APP_TITLE + " - Login");
        stage.getIcons().add(APP_LOGO);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setResizable(false);

        // 1. Load Splash
        FXMLLoader splashLoader =
                new FXMLLoader(Login.class.getResource("/FXML/SplashScreen.fxml"));
        Parent splashRoot = splashLoader.load();
        SplashController splashController = splashLoader.getController();

        Scene splashScene = new Scene(splashRoot, 1000, 600);
        stage.setScene(splashScene);
        stage.show();

        // 2. Khi Splash báo xong → load Login.fxml
        splashController.setOnFinished(() -> {
            try {
                FXMLLoader loginLoader =
                        new FXMLLoader(Login.class.getResource("/FXML/Login.fxml"));
                Parent loginRoot = loginLoader.load();

                Scene loginScene = new Scene(loginRoot, 1000, 600);
                loginScene.getStylesheets().add(
                        Login.class.getResource("/CSS/login.css").toExternalForm()
                );

                LoginController loginController = loginLoader.getController();

                stage.setScene(loginScene);
                stage.centerOnScreen();

                // focus vào ô username
                loginController.getUsernameField().requestFocus();

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        // 3. Bắt đầu animation Splash
        splashController.playAnimation();
    }

    public static void main(String[] args) {
        launch();
    }
}
