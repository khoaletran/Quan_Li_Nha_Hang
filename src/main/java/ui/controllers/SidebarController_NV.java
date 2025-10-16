package ui.controllers;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class SidebarController_NV {

    @FXML
    private VBox subMenuDatBan;

    private boolean isSubMenuVisible = false;

    @FXML
    private void toggleSubMenu() {
        if (isSubMenuVisible) {
            collapse(subMenuDatBan);
        } else {
            expand(subMenuDatBan);
        }
        isSubMenuVisible = !isSubMenuVisible;
    }

    private void expand(VBox box) {
        box.setManaged(true);
        box.setVisible(true);

        box.setOpacity(0);
        double expandedHeight = box.getChildren().size() * 42; // cao mỗi nút ~40px

        box.setPrefHeight(0);

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(box.prefHeightProperty(), 0),
                        new KeyValue(box.opacityProperty(), 0)
                ),
                new KeyFrame(Duration.millis(250),
                        new KeyValue(box.prefHeightProperty(), expandedHeight),
                        new KeyValue(box.opacityProperty(), 1)
                )
        );
        timeline.play();
    }

    private void collapse(VBox box) {
        double currentHeight = box.getHeight();

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(box.prefHeightProperty(), currentHeight),
                        new KeyValue(box.opacityProperty(), 1)
                ),
                new KeyFrame(Duration.millis(250),
                        new KeyValue(box.prefHeightProperty(), 0),
                        new KeyValue(box.opacityProperty(), 0)
                )
        );

        timeline.setOnFinished(e -> {
            box.setVisible(false);
            box.setManaged(false);
        });

        timeline.play();
    }
}
