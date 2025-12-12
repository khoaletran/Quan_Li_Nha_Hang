package ui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class DangXuatController {

    @FXML private Button btnCancel;
    @FXML private Button btnConfirm;

    private Stage dialogStage;
    private Stage ownerStage;

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setOwnerStage(Stage ownerStage) {
        this.ownerStage = ownerStage;
    }

    @FXML
    public void initialize() {
        btnCancel.setOnAction(e -> {
            if (dialogStage != null) {
                dialogStage.close();
            } else {
                ((Stage) btnCancel.getScene().getWindow()).close();
            }
        });

        btnConfirm.setOnAction(e -> {
            // Đóng stage chính => nếu đây là stage cuối cùng, app tự thoát, KHÔNG cần Platform.exit()
            if (ownerStage != null) {
                ownerStage.close();
            }
            // Đóng dialog
            if (dialogStage != null) {
                dialogStage.close();
            }
        });
    }
}
