package ui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class ConfirmController {

    @FXML private Label lblTitle, lblMessage;
    @FXML private Button btnYes, btnNo;

    private boolean result = false; // true = Yes, false = No

    public void setConfirmData(String title, String message) {
        lblTitle.setText(title);
        lblMessage.setText(message);
    }

    @FXML
    public void initialize() {
        btnYes.setOnAction(e -> {
            result = true;
            closeWindow();
        });

        btnNo.setOnAction(e -> {
            result = false;
            closeWindow();
        });
    }

    private void closeWindow() {
        Stage stage = (Stage) lblTitle.getScene().getWindow();
        stage.close();
    }

    public boolean getResult() {
        return result;
    }
}
