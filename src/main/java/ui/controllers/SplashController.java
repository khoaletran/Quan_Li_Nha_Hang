package ui.controllers;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class SplashController {

    @FXML
    private Rectangle progressFill;   // thanh load màu xanh

    @FXML
    private Rectangle rightPanel;     // panel cam đậm bên phải

    private Runnable onFinished;      // callback do Login.java set

    public void setOnFinished(Runnable onFinished) {
        this.onFinished = onFinished;
    }

    public void playAnimation() {
        double tProgressEnd = 1.8;
        double tPanelEnd    = 2.6;
        double tFinish      = 2.85;

        double barMaxWidth   = 400;
        double panelMaxWidth = 1000; // full ngang

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(progressFill.widthProperty(), 0.0),
                        new KeyValue(rightPanel.widthProperty(), 0.0)
                ),

                new KeyFrame(Duration.seconds(tProgressEnd),
                        new KeyValue(progressFill.widthProperty(), barMaxWidth),
                        new KeyValue(rightPanel.widthProperty(), 0.0)
                ),

                new KeyFrame(Duration.seconds(tPanelEnd),
                        new KeyValue(rightPanel.widthProperty(), panelMaxWidth)
                ),

                new KeyFrame(Duration.seconds(tFinish))
        );

        timeline.setOnFinished(e -> {
            if (onFinished != null) {
                onFinished.run();
            }
        });

        timeline.play();
    }

}
