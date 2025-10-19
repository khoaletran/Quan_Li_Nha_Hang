package ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class test extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/CheckOut.fxml"));
        Scene scene = new Scene(loader.load(), AppConstants.WINDOW_WIDTH, AppConstants.WINDOW_HEIGHT);
        scene.getStylesheets().add(getClass().getResource("/CSS/checkout.css").toExternalForm());
        stage.setTitle(AppConstants.APP_TITLE + " - Trang chủ Quản lí");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
