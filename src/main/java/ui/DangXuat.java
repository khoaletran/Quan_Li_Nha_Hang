package ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class DangXuat {

    public static void showDialog() { // <-- static để gọi thẳng như ui.DangXuat.showDialog()
        try {
            FXMLLoader loader = new FXMLLoader(DangXuat.class.getResource("/FXML/DangXuat.fxml"));
            Parent root = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setScene(new Scene(root));
            dialogStage.setTitle("Đăng xuất");
            dialogStage.initStyle(StageStyle.UNDECORATED);
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setResizable(false);
            dialogStage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
