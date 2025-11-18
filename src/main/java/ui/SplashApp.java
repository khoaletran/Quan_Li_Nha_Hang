package ui;

import javafx.animation.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class SplashApp extends Application {
    @Override
    public void start(Stage splashStage) throws Exception {
        // Hình logo
        ImageView logo = new ImageView(new Image(getClass().getResourceAsStream("/IMG/logo_1.png")));
        logo.setFitWidth(300);
        logo.setPreserveRatio(true);

        // Thanh tiến trình
        ProgressBar progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(300);

        Label progressLabel = new Label("0%");

        VBox root = new VBox(20, logo, progressBar, progressLabel);
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #FF8C42, #FFD166); -fx-alignment: center;");

        Scene scene = new Scene(root, 1000, 600);
        splashStage.initStyle(StageStyle.UNDECORATED);
        splashStage.setScene(scene);
        splashStage.show();

        // Timeline giả lập load
        Timeline timeline = new Timeline();
        for (int i = 0; i <= 100; i++) {
            int percent = i;
            KeyFrame kf = new KeyFrame(Duration.millis(i * 30), e -> {
                progressBar.setProgress(percent / 100.0);
                progressLabel.setText(percent + "%");
            });
            timeline.getKeyFrames().add(kf);
        }

        timeline.setOnFinished(e -> playEndEffect(splashStage, root, logo));
        timeline.play();
    }

    // Hiệu ứng fade + scale
    private void playEndEffect(Stage splashStage, VBox root, ImageView logo) {
        // Fade root
        FadeTransition fade = new FadeTransition(Duration.seconds(1), root);
        fade.setFromValue(1.0);
        fade.setToValue(0.0);

        // Scale logo
        ScaleTransition scale = new ScaleTransition(Duration.seconds(1), logo);
        scale.setFromX(1.0);
        scale.setFromY(1.0);
        scale.setToX(1.5);
        scale.setToY(1.5);

        // Kết hợp
        ParallelTransition pt = new ParallelTransition(fade, scale);
        pt.setOnFinished(ev -> {
            splashStage.close();
            // Mở Login
            Platform.runLater(() -> {
                try {
                    Login loginApp = new Login();
                    Stage loginStage = new Stage();
                    loginApp.start(loginStage);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        });

        pt.play();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
