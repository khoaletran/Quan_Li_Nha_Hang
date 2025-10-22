package ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import static ui.AppConstants.APP_LOGO;

public class MainNV extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(Login.class.getResource("/FXML/MainNhanVien.fxml"));
        Scene scene = new Scene(loader.load(), AppConstants.WINDOW_WIDTH, AppConstants.WINDOW_HEIGHT);
        stage.setTitle(AppConstants.APP_TITLE + " - Nhân Viên");
        stage.getIcons().add(APP_LOGO);
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }
}
