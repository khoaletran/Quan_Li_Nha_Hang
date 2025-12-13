package ui.controllers;

import entity.NhanVien;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.function.Consumer;

public class MainController_NV {

    @FXML private StackPane mainContent;
    @FXML private SidebarController_NV sidebar_NVController;

    private NhanVien nhanVien;
    private LocalDateTime thoiGianVaoCa;

    @FXML private TopBarController topBarNVController;

    public SidebarController_NV getSidebarController() {
        return sidebar_NVController;
    }

    @FXML
    public void initialize() {
        javafx.application.Platform.runLater(() -> {
            Stage stage = (Stage) topBarNVController
                    .getRoot().getScene().getWindow();
            topBarNVController.bindStage(stage);
            topBarNVController.setTitle("CrabKing Restaurant");
        });

        topBarNVController.configureActionButton("Kết Ca",
                () -> setCenterContent("/FXML/BanGiaoCa.fxml"));

        if (sidebar_NVController != null) {
            sidebar_NVController.setMainController(this);
        }
        mainContent.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                addKeyboardShortcuts(newScene);
            }
        });
    }


    private void addKeyboardShortcuts(Scene scene) {
        scene.setOnKeyPressed(event -> {

            switch (event.getCode()) {

                // MENU CHÍNH NV
                case DIGIT1 -> sidebar_NVController.selectTab(1);
                case DIGIT2 -> sidebar_NVController.selectTab(2);
                case DIGIT3 -> sidebar_NVController.selectTab(3);
                case DIGIT4 -> sidebar_NVController.selectTab(4);
                case DIGIT5 -> sidebar_NVController.selectTab(5);
                case DIGIT6 -> sidebar_NVController.selectTab(6);

                // SUBMENU:
                case F1 -> sidebar_NVController.selectTab(21); // Đặt bàn
                case F2 -> sidebar_NVController.selectTab(22);
                case F3 -> sidebar_NVController.selectTab(23);
                case F4 -> sidebar_NVController.selectTab(24);
            }
        });
    }


    public void setNhanVien(NhanVien nhanVien) {
        this.nhanVien = nhanVien;
//        topBarNVController.setUserInfo(nhanVien.getTenNV(),"Nhân Viên");
        loadDefaultView();
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
                dashboardCtrl.setNhanVien(nhanVien);
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

    public void setCenterContent(String fxmlPath, Consumer<Object> controllerCallback) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent node = loader.load();

            Object controller = loader.getController();

            // Gọi callback trước
            if (controllerCallback != null) {
                controllerCallback.accept(controller);
            }

            // === GIỮ NGUYÊN LOGIC CŨ ===
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
                dashboardCtrl.setNhanVien(nhanVien);
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
