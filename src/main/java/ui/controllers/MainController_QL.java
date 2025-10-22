package ui.controllers;

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

    public SidebarController_QL getsidebar_QLController() {
        return sidebar_QLController ;
    }


    @FXML
    public void initialize() {
        if (sidebar_QLController != null) {
            sidebar_QLController.setMainController(this);
        }
        // Hiển thị Dashboard mặc định
        setCenterContent("/FXML/DashBoard.fxml");
    }

    public void setCenterContent(String fxmlPath) {
        try {
            Node node = FXMLLoader.load(getClass().getResource(fxmlPath));
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
