package ui.controllers;

import entity.NhanVien;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
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
        // ❌ Không gọi load Dashboard ở đây nữa
        // Vì lúc này chưa có nhân viên
    }

    public void setNhanVien(NhanVien nhanVien) {
        this.nhanVien = nhanVien;

        // ✅ Khi có nhân viên rồi mới load Dashboard
        setCenterContent("/FXML/DashBoard.fxml");
    }

    public NhanVien getNhanVien() {
        return nhanVien;
    }

    public void setCenterContent(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Node node = loader.load();

            // ✅ Truyền controller cha và nhân viên
            Object controller = loader.getController();
            if (controller instanceof DashboardController dashboardController) {
                dashboardController.setMainController(this);
                dashboardController.setNhanVien(nhanVien);
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
