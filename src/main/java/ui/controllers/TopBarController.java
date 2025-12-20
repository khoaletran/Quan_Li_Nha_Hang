package ui.controllers;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

public class TopBarController {

    @FXML private HBox root;
    @FXML private ImageView imgLogo;
    @FXML private Label lblUserName;
    @FXML private Label lblUserRole;
    @FXML private TextField txtSearch;
    @FXML private Label lblClock;
    @FXML private Button btnAction;
    @FXML private Button btnMinimize;
    @FXML private Button btnMaximize;
    @FXML private Button btnClose;


    private Stage stage;
    private double offsetX;
    private double offsetY;

    private final DateTimeFormatter fmtClock =
            DateTimeFormatter.ofPattern("HH:mm | dd/MM/yyyy");

    private Consumer<String> searchHandler;
    private Runnable actionHandler;

    @FXML
    private void initialize() {
        initClock();
        initDragWindow();
        initSearch();
        initActionButton();
        initWindowButtons();
        
    }

    /* ========= API cho MainController ========= */

    public HBox getRoot() {
        return root;
    }

    public void bindStage(Stage stage) {
        this.stage = stage;
    }

    /** Đổi title cửa sổ (cho NV / Quản lý khác nhau) */
    public void setTitle(String title) {
        if (stage != null) {
            stage.setTitle(title);
        }
    }

    /** Version cũ: set mỗi tên */
    public void setUserInfo(String name) {
        lblUserName.setText(name);
    }

    /** Version đầy đủ: tên + chức vụ */
//    public void setUserInfo(String name, String role) {
//        lblUserName.setText(name);
//        lblUserRole.setText(role);
//    }

    /** Gán text + logic cho nút action bên phải (Kết ca, Đăng xuất, …) */
    public void configureActionButton(String text, Runnable handler) {
        btnAction.setText(text);
        this.actionHandler = handler;
    }

    /** Handler khi gõ vào ô search (để filter menu sidebar, chuyển màn, …) */
    public void setSearchHandler(Consumer<String> handler) {
        this.searchHandler = handler;
    }

    /* ========= Đồng hồ ========= */

    private void initClock() {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, e ->
                        lblClock.setText(LocalDateTime.now().format(fmtClock))),
                new KeyFrame(Duration.seconds(1))
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    /* ========= Search ========= */

    private void initSearch() {
        if (txtSearch == null) return;
        txtSearch.textProperty().addListener((obs, oldVal, newVal) -> {
            if (searchHandler != null) {
                searchHandler.accept(newVal.trim());
            }
        });
    }

    /* ========= Nút action ========= */

    private void initActionButton() {
        if (btnAction == null) return;
        btnAction.setOnAction(e -> {
            if (actionHandler != null) {
                actionHandler.run();
            }
        });
    }

    private void initWindowButtons() {
        if (btnMinimize != null) {
            
            btnMinimize.setOnAction(e -> {
                if (stage != null) {
                    stage.setIconified(true);   // Thu nhỏ xuống taskbar
                }
            });
        }

        if (btnMaximize != null) {
            btnMaximize.setOnAction(e -> {
                if (stage != null) {
                    // Toggle maximize / restore
                    stage.setMaximized(!stage.isMaximized());
                }
            });
        }

        if (btnClose != null) {
            btnClose.setOnAction(e -> {
                if (stage != null) {
                    stage.close();              // Đóng cửa sổ
                }
            });
        }
    }

    private void initDragWindow() {
        if (root == null) return;

        root.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> {
            if (stage == null) return;
            offsetX = e.getSceneX();
            offsetY = e.getSceneY();
        });

        root.addEventFilter(MouseEvent.MOUSE_DRAGGED, e -> {
            if (stage == null || stage.isMaximized()) return;
            stage.setX(e.getScreenX() - offsetX);
            stage.setY(e.getScreenY() - offsetY);
        });

        // Double-click để toggle maximize
        root.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
            if (stage == null) return;
            if (e.getClickCount() == 2) {
                stage.setMaximized(!stage.isMaximized());
            }
        });
    }

}

