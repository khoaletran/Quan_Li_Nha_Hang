package ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ui.controllers.ConfirmController;
import javafx.scene.paint.Color;

public class ConfirmCus {

    public static boolean show(String title, String message) {
        try {
            FXMLLoader loader = new FXMLLoader(ConfirmCus.class.getResource("/FXML/Confirm.fxml"));
            Parent root = loader.load();

            ConfirmController controller = loader.getController();
            controller.setConfirmData(title, message);

            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);

            Stage stage = new Stage();
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);

            stage.showAndWait();

            return controller.getResult();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
