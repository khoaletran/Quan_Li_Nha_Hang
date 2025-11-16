package ui;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
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

public class SplashApp extends Application{
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
        root.setStyle("-fx-background-color: white; -fx-padding: 30; -fx-alignment: center;");

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

        timeline.setOnFinished(e -> {
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

        timeline.play();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
