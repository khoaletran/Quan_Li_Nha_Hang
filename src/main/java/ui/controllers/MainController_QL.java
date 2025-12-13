package ui.controllers;

import entity.Mon;
import entity.NhanVien;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class MainController_QL {

    @FXML private StackPane mainContent;
    @FXML private SidebarController_QL sidebar_QLController;

    private NhanVien nhanVien;
    @FXML private ui.controllers.TopBarController topBarQlController;

    public SidebarController_QL getsidebar_QLController() {
        return sidebar_QLController ;
    }

    @FXML
    public void initialize() {
        javafx.application.Platform.runLater(() -> {
            // Lấy stage hiện tại từ top bar
            Stage stage = (Stage) topBarQlController
                    .getRoot().getScene().getWindow();

            // Bind cho top bar (kéo, nút thu nhỏ/phóng to,...)
            topBarQlController.bindStage(stage);
            topBarQlController.setTitle("CrabKing Restaurant");

            // CẤU HÌNH NÚT ĐĂNG XUẤT: truyền stage vào dialog
            topBarQlController.configureActionButton("Đăng Xuất",
                    () -> ui.DangXuat.showDialog(stage));
        });

        if (sidebar_QLController != null) {
            sidebar_QLController.setMainController(this);
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
                case DIGIT1 -> sidebar_QLController.selectTab(1);
                case DIGIT2 -> sidebar_QLController.selectTab(2);
                case DIGIT3 -> sidebar_QLController.selectTab(3);
                case DIGIT4 -> sidebar_QLController.selectTab(4);
                case DIGIT5 -> sidebar_QLController.selectTab(5);
                case DIGIT6 -> sidebar_QLController.selectTab(6);
                case DIGIT7 -> sidebar_QLController.selectTab(7);
                case DIGIT8 -> sidebar_QLController.selectTab(8);
            }
        });
    }


    public void setNhanVien(NhanVien nhanVien) {
        this.nhanVien = nhanVien;
//        topBarQlController.setUserInfo(nhanVien.getTenNV(),"Quản Lí");
        setCenterContent("/FXML/DashBoard.fxml");
    }

    public NhanVien getNhanVien() {
        return nhanVien;
    }

    public void setCenterContent(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Node node = loader.load();

            Object controller = loader.getController();
            if (controller instanceof DashboardController dashboardController) {
                dashboardController.setMainController(this);
                dashboardController.setNhanVien(nhanVien);
            } else if (controller instanceof ThongKeController thongKeController) {
                thongKeController.setMainController(this); // truyền MainController_QL
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



    public void setCenterContent(String fxmlPath, Mon mon) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            sidebar_QLController.selectTab(3);
            // Lấy controller của QLMenu
            QLMenuController controller = loader.getController();
            if (mon != null) {
                controller.setSearchKeyword(mon.getTenMon());
            }

            mainContent.getChildren().setAll(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void openQLMenuWithMon(Mon mon) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/FXML/QLMenu.fxml")
            );

            Parent root = loader.load();

            QLMenuController controller = loader.getController();
            controller.setSelectedMon(mon);
            controller.setMainController(this);

            if (mon != null) {
                controller.setSearchKeyword(mon.getTenMon());
            }

            sidebar_QLController.selectTab(2);

            mainContent.getChildren().setAll(root);

            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), root);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.play();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
