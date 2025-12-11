package ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ui.controllers.DangXuatController;

public class DangXuat {

    public static void showDialog(Stage ownerStage) {
        try {
            FXMLLoader loader = new FXMLLoader(DangXuat.class.getResource("/FXML/DangXuat.fxml"));
            Parent root = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setScene(new Scene(root));
            dialogStage.setTitle("Đăng xuất");
            dialogStage.initStyle(StageStyle.UNDECORATED);
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initOwner(ownerStage);
            dialogStage.setResizable(false);

            // truyền stage cho controller
            DangXuatController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setOwnerStage(ownerStage);

            dialogStage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
