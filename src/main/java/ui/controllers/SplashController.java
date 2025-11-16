package ui.controllers;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.StageStyle;
import ui.AppConstants;

public class SplashController{
    @FXML
    private ProgressBar progressBar;

    private double progress = 0;

    public void initialize() {
        progressBar.setProgress(0);
        Timeline timeline = new Timeline();
        int totalFrames = 100;
        double durationSeconds = 3;
        double step = 1.0 / totalFrames;

        for (int i = 1; i <= totalFrames; i++) {
            double progress = i * step;
            KeyFrame keyFrame = new KeyFrame(
                    Duration.seconds(i * durationSeconds / totalFrames),
                    e -> progressBar.setProgress(progress)
            );
            timeline.getKeyFrames().add(keyFrame);
        }

        timeline.setCycleCount(1);

        // Khi timeline xong, mở login
        timeline.setOnFinished(e -> openLogin());

        timeline.play();
    }

    private void openLogin() {
        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/Login.fxml"));
                Scene scene = new Scene(loader.load(), 1000, 600);
                Stage stage = new Stage();
                stage.setScene(scene);
                stage.setTitle("CrabKing - Login");
                stage.show();

                // Đóng stage splash
                Stage splashStage = (Stage) progressBar.getScene().getWindow();
                splashStage.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    private void updateProgress() {
        progress += 0.01;
        progressBar.setProgress(progress);

        if (progress >= 1.0) {
            // Load xong → mở login
            Stage splashStage = (Stage) progressBar.getScene().getWindow();
            splashStage.close();

            Platform.runLater(() -> {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/Login.fxml"));
                    Scene scene = new Scene(loader.load(), 1000, 600);
                    scene.getStylesheets().add(getClass().getResource("/CSS/login.css").toExternalForm());

                    Stage loginStage = new Stage();
                    loginStage.setTitle(AppConstants.APP_TITLE + " - Login");
                    loginStage.getIcons().add(AppConstants.APP_LOGO);
                    loginStage.setScene(scene);
                    loginStage.initStyle(StageStyle.UNDECORATED);
                    loginStage.setResizable(false);
                    loginStage.show();

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        }
    }
}
