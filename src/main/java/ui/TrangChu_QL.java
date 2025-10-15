package ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class TrangChu_QL extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(
                TrangChu_QL.class.getResource("/FXML/TrangChu_QL.fxml")
        );

        Scene scene = new Scene(fxmlLoader.load(), 1200, 700);

        scene.getStylesheets().add(
                TrangChu_QL.class.getResource("/CSS/TrangChu_QL.css").toExternalForm()
        );

        stage.setTitle("CrabKing - Trang Chủ Quản Lý");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
