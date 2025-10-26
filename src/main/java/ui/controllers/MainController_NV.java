package ui.controllers;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import java.io.IOException;

public class MainController_NV {

    @FXML private StackPane mainContent;
    @FXML private SidebarController_NV sidebar_NVController;

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

    private void loadDefaultView() {
        setCenterContent("/FXML/DashBoard.fxml");
    }


    public void setCenterContent(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/ChonMon.fxml"));
            Parent node = loader.load();

            Object controller = loader.getController();
            if (controller instanceof ChonMonController chonMonCtrl) {
                chonMonCtrl.setMainController(this);
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
