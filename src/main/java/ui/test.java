package ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import static ui.AppConstants.APP_LOGO;

public class test extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(test.class.getResource("/FXML/QLMenu.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), AppConstants.WINDOW_WIDTH, AppConstants.WINDOW_HEIGHT);

        // Áp dụng CSS
        scene.getStylesheets().add(test.class.getResource("/CSS/QLMenu.css").toExternalForm());

        stage.setTitle(AppConstants.APP_TITLE + " - Chọn Món");
        stage.getIcons().add(APP_LOGO);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
