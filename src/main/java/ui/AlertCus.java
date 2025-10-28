package ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ui.controllers.AlertController;
import javafx.scene.paint.Color;

public class AlertCus {

    public static void show(String title, String message) {
        try {
            FXMLLoader loader = new FXMLLoader(AlertCus.class.getResource("/FXML/Alert.fxml"));
            Parent root = loader.load();

            // Gán dữ liệu cho controller
            AlertController controller = loader.getController();
            controller.setAlertData(title, message);

            // Tạo Scene & Stage
            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);

            Stage stage = new Stage();
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);

            stage.showAndWait();
        } catch (Exception e) {
            System.err.println("Lỗi hiển thị AlertCus: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
