package ui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

public class DashboardController {

    @FXML
    private BorderPane rootPane; // BorderPane gốc của dashboard

    @FXML
    private void showChangePassword() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/login.fxml"));
            Parent loginRoot = loader.load();

            ui.controllers.LoginController loginController = loader.getController();
            loginController.showResetPane();

            Stage stage = new Stage();
            stage.setScene(new Scene(loginRoot));
            stage.setTitle("Đổi Mật Khẩu");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
