package ui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class XacNhanXoaController {

    @FXML private Label lblNoiDung;
    @FXML private Button btnCancel, btnConfirm;

    private boolean confirmed = false; // true nếu người dùng bấm "Xóa"

    @FXML
    public void initialize() {
        btnCancel.setOnAction(e -> ((Stage) btnCancel.getScene().getWindow()).close());
        btnConfirm.setOnAction(e -> {
            confirmed = true;
            ((Stage) btnConfirm.getScene().getWindow()).close();
        });
    }

    public void setNoiDung(String noiDung) {
        lblNoiDung.setText(noiDung);
    }

    public boolean isConfirmed() {
        return confirmed;
    }


}
