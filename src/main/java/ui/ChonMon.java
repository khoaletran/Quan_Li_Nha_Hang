package ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ChonMon extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/ChonMon.fxml"));
        Scene scene = new Scene(loader.load(), 1400, 600); // Tăng kích thước để không bị tràn
        scene.getStylesheets().add(getClass().getResource("/CSS/ChonMon.css").toExternalForm());
        stage.setTitle(AppConstants.APP_TITLE + " - Chọn Món");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}