package ui.controllers;

import entity.NhanVien;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import java.io.IOException;
import java.time.LocalDateTime;

public class MainController_NV {

    @FXML private StackPane mainContent;
    @FXML private SidebarController_NV sidebar_NVController;

    private NhanVien nhanVien;
    private LocalDateTime thoiGianVaoCa;




    public SidebarController_NV getSidebarController() {
        return sidebar_NVController;
    }

    @FXML
    public void initialize() {
        if (sidebar_NVController != null) {
            sidebar_NVController.setMainController(this);
        }
        loadDefaultView();
    }

    public void setNhanVien(NhanVien nhanVien) {
        this.nhanVien = nhanVien;
    }

    public NhanVien getNhanVien() {return nhanVien;}

    private void loadDefaultView() {
        setCenterContent("/FXML/DashBoard.fxml");
    }

    public StackPane getMainContent() {
        return mainContent;
    }

    public void setThoiGianVaoCa(LocalDateTime thoiGianVaoCa) {
        this.thoiGianVaoCa = thoiGianVaoCa;
    }

    public LocalDateTime getThoiGianVaoCa() {
        return thoiGianVaoCa;
    }


    public void setCenterContent(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent node = loader.load();

            Object controller = loader.getController();

            if (controller instanceof ChonMonController chonMonCtrl) {
                chonMonCtrl.setMainController(this);
                chonMonCtrl.setNhanVien(nhanVien);
            }

            if (controller instanceof DatBanController datBanCtrl) {
                datBanCtrl.setMainController(this);
                datBanCtrl.setNhanVien(nhanVien);
            }

            if (controller instanceof DashboardController dashboardCtrl) {
                dashboardCtrl.setMainController(this);
            }

            if (controller instanceof BanGiaoCaController banGiaoCaCtrl) {
                banGiaoCaCtrl.setThoiGianVaoCa(thoiGianVaoCa);
            }

            if (controller instanceof BanGiaoCaController banGiaoCaCtrl) {
                banGiaoCaCtrl.initData(nhanVien);
                banGiaoCaCtrl.setThoiGianVaoCa(thoiGianVaoCa);
            }


            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), node);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);

            mainContent.getChildren().setAll(node);
            fadeIn.play();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
