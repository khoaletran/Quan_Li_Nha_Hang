package ui.controllers;

import entity.Mon;
import entity.NhanVien;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.io.IOException;

public class MainController_QL {

    @FXML private StackPane mainContent;
    @FXML private SidebarController_QL sidebar_QLController;

    private NhanVien nhanVien;

    public SidebarController_QL getsidebar_QLController() {
        return sidebar_QLController ;
    }

    @FXML
    public void initialize() {
        if (sidebar_QLController != null) {
            sidebar_QLController.setMainController(this);
        }
    }

    public void setNhanVien(NhanVien nhanVien) {
        this.nhanVien = nhanVien;

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

}
