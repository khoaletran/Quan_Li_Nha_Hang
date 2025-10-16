package ui.controllers;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

public class SidebarController_NV {

    @FXML
    private VBox subMenuDatBan;

    private boolean isSubMenuVisible = false;

    @FXML
    private void toggleSubMenu() {
        isSubMenuVisible = !isSubMenuVisible;
        subMenuDatBan.setVisible(isSubMenuVisible);
        subMenuDatBan.setManaged(isSubMenuVisible);
    }
}