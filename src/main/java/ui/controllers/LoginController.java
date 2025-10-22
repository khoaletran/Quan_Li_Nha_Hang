package ui.controllers;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.swing.*;

public class LoginController {

    @FXML private VBox loginPane;
    @FXML private VBox forgotPane;
    @FXML private VBox resetPane;
    @FXML private Button closeBtn;
    @FXML private Button minimizeBtn;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    // Hiệu ứng fade mượt
    private void switchPane(VBox hide, VBox show) {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(250), hide);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> {
            hide.setVisible(false);
            hide.setManaged(false);
            show.setVisible(true);
            show.setManaged(true);

            FadeTransition fadeIn = new FadeTransition(Duration.millis(250), show);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.play();
        });
        fadeOut.play();
    }

    // ===== Sự kiện chuyển màn hình =====

    @FXML
    private void close() {
        Stage stage = (Stage) closeBtn.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void minimize() {
        Stage stage = (Stage) minimizeBtn.getScene().getWindow();
        stage.setIconified(true);
    }



    @FXML
    private void showForgot() { switchPane(loginPane, forgotPane); }

    @FXML
    private void showLogin() {
        if (forgotPane.isVisible()) switchPane(forgotPane, loginPane);
        else switchPane(resetPane, loginPane);
    }

    @FXML
    private void showReset() { switchPane(forgotPane, resetPane); }

    @FXML
    private void resetDone() { switchPane(resetPane, loginPane); }

    @FXML
    private void login() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.equals("NV0001") && password.equals("1")) {
            try {
                // Đóng màn hình đăng nhập
                Stage currentStage = (Stage) usernameField.getScene().getWindow();
                currentStage.close();

                // Mở giao diện chính (class MainNV)
                ui.MainQL mainQL = new ui.MainQL();
                Stage stage = new Stage();
                mainQL.start(stage);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if (username.equals("NV0002") && password.equals("1")) {
            try {
                // Đóng màn hình đăng nhập
                Stage currentStage = (Stage) usernameField.getScene().getWindow();
                currentStage.close();

                // Mở giao diện chính (class MainNV)
                ui.MainNV mainNV = new ui.MainNV();
                Stage stage = new Stage();
                mainNV.start(stage);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Đăng nhập thất bại");
            alert.setHeaderText(null);
            alert.setContentText("Sai mã nhân viên hoặc mật khẩu!");
            alert.showAndWait();
        }
    }


}
