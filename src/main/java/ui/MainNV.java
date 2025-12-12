package ui;

import entity.NhanVien;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import static ui.AppConstants.APP_LOGO;

import javafx.stage.StageStyle;
import ui.controllers.MainController_NV;
import ui.controllers.SidebarController_NV;

import java.time.LocalDateTime;

public class MainNV {

    private NhanVien nvDangNhap;
    private LocalDateTime thoiGianVaoCa;

    public void setThoiGianVaoCa(LocalDateTime thoiGianVaoCa) {
        this.thoiGianVaoCa = thoiGianVaoCa;
    }

    public void setNhanVienDangNhap(NhanVien nv) {
        this.nvDangNhap = nv;
    }

    public void show(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/MainNhanVien.fxml"));
        BorderPane root = loader.load();

        MainController_NV mainController = loader.getController();
        mainController.setNhanVien(nvDangNhap);
        mainController.setThoiGianVaoCa(thoiGianVaoCa);

        SidebarController_NV sidebarController = mainController.getSidebarController();
        sidebarController.setThongTinNhanVien(nvDangNhap);

        Scene scene = new Scene(root, AppConstants.WINDOW_WIDTH, AppConstants.WINDOW_HEIGHT);
        stage.setTitle(AppConstants.APP_TITLE + " - Nhân Viên");
        stage.getIcons().add(APP_LOGO);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }
}
