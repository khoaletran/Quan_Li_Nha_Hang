package ui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class AlertController {

    @FXML private Label lblTitle;
    @FXML private Label lblMessage;
    @FXML private Button btnConfirm;

    public void setAlertData(String title, String message) {
        lblTitle.setText(title != null ? title : "Thông báo");
        lblMessage.setText(message);

        btnConfirm.setOnAction(e -> {
            Stage stage = (Stage) btnConfirm.getScene().getWindow();
            stage.close();
        });
    }
}
