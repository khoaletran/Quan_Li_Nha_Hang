package ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ui.controllers.XacNhanXoaController;

public class XacNhanXoa {

    public static boolean hienHopThoaiXacNhan(String tieuDe, String noiDung) {
        try {
            FXMLLoader loader = new FXMLLoader(XacNhanXoa.class.getResource("/FXML/XacNhanXoa.fxml"));
            Parent root = loader.load();

            XacNhanXoaController controller = loader.getController();
            controller.setNoiDung(noiDung);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle(tieuDe);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setResizable(false);
            stage.showAndWait();

            return controller.isConfirmed();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
