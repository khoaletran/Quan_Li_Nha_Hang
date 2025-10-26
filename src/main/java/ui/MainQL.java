package ui;

import entity.NhanVien;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ui.controllers.MainController_NV;
import ui.controllers.MainController_QL;
import ui.controllers.SidebarController_QL;

import static ui.AppConstants.APP_LOGO;

public class MainQL extends Application {

    private NhanVien nvDangNhap;

    public void setNhanVienDangNhap(NhanVien nv) {
        this.nvDangNhap = nv;
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(Login.class.getResource("/FXML/MainQuanLi.fxml"));
        BorderPane root = loader.load();

        MainController_QL mainController = loader.getController();

        mainController.setNhanVien(nvDangNhap);

        SidebarController_QL sidebarController = mainController.getsidebar_QLController();
        sidebarController.setThongTinNhanVien(nvDangNhap);

        Scene scene = new Scene(root, AppConstants.WINDOW_WIDTH, AppConstants.WINDOW_HEIGHT);
        stage.setTitle(AppConstants.APP_TITLE + " - Quản Lí");
        stage.getIcons().add(APP_LOGO);
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }
}
