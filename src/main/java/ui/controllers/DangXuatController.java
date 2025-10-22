package ui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.application.Platform;

public class DangXuatController {

    @FXML private Button btnCancel;
    @FXML private Button btnConfirm;

    @FXML
    public void initialize() {
        btnCancel.setOnAction(e -> {
            Stage stage = (Stage) btnCancel.getScene().getWindow();
            stage.close();
        });

        btnConfirm.setOnAction(e -> {
            Platform.exit();
        });
    }
}
