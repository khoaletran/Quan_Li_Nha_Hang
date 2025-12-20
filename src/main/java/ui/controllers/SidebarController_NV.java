package ui.controllers;

import entity.NhanVien;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class SidebarController_NV {

    @FXML private VBox subMenuDatBan;

    // Các nút chính
    @FXML private Button btnDashboard;
    @FXML private Button btnQuanLiDatBan;
    @FXML private Button btnDatBan;
    @FXML private Button btnCheckIn;
    @FXML private Button btnCheckOut;
    @FXML private Button btnCapNhatDonBan;
    @FXML private Button btnQuanLiThanhVien;
    @FXML private Button btnTraCuu;
    @FXML private Button btnHoTro;
    @FXML private Button btnKetCa;
    @FXML private Label lblTenNV;
    @FXML private Label lblChucVu;
    @FXML private ImageView avatarImage;

    private MainController_NV mainController;
    private Button currentMainSelected = null;
    private Button currentSubSelected = null;
    private boolean isSubMenuVisible = false; // trạng thái submenu

    @FXML
    public void initialize() {
        javafx.scene.shape.Circle clip = new javafx.scene.shape.Circle(55, 55, 55);
        avatarImage.setClip(clip);
        // TOOLTIP SHORTCUT
        btnDashboard.setTooltip(new Tooltip("Phím tắt: 1"));
        btnQuanLiDatBan.setTooltip(new Tooltip("Phím tắt: 2"));
        btnQuanLiThanhVien.setTooltip(new Tooltip("Phím tắt: 3"));
        btnTraCuu.setTooltip(new Tooltip("Phím tắt: 4"));
        btnHoTro.setTooltip(new Tooltip("Phím tắt: 5"));
        btnKetCa.setTooltip(new Tooltip("Phím tắt: ESC"));
        btnDatBan.setTooltip(new Tooltip("Phím tắt: F1"));
        btnCheckIn.setTooltip(new Tooltip("Phím tắt: F2"));
        btnCheckOut.setTooltip(new Tooltip("Phím tắt: F3"));
        btnCapNhatDonBan.setTooltip(new Tooltip("Phím tắt: F4"));
        btnKetCa.setTooltip(new Tooltip("Phím tắt: Esc"));
        Platform.runLater(() -> {
            Scene scene = avatarImage.getScene();
            if (scene == null) return;
            scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
                if (event.getCode() == KeyCode.ESCAPE) {
                    selectTab(6); // Bàn giao ca
                    event.consume(); // chặn lan nếu cần
                }
            });
        });
    }

    public void setThongTinNhanVien(NhanVien nv) {
        lblTenNV.setText(nv.getTenNV());
        lblChucVu.setText(nv.isQuanLi() ? "Quản Lí" : "Nhân Viên");

        Image img = nv.isGioiTinh()
                ? new Image(getClass().getResourceAsStream("/IMG/icon/man.png"))
                : new Image(getClass().getResourceAsStream("/IMG/icon/woman.png"));

        avatarImage.setImage(img);
    }

    public void setMainController(MainController_NV controller) {
        this.mainController = controller;
    }

    // =================== TOGGLE / SHOW / HIDE SUBMENU ===================
    @FXML
    private void toggleSubMenu() {
        boolean isVisible = subMenuDatBan.isVisible();
        subMenuDatBan.setVisible(!isVisible);
        subMenuDatBan.setManaged(!isVisible);

        FadeTransition ft = new FadeTransition(Duration.millis(200), subMenuDatBan);
        ft.setFromValue(isVisible ? 1.0 : 0.0);
        ft.setToValue(isVisible ? 0.0 : 1.0);
        ft.play();

        if (!isVisible) setMainSelected(btnQuanLiDatBan);
    }

    // Xử lý các nút
    @FXML
    private void handleMenuAction(javafx.event.ActionEvent event) {
        if (mainController == null) return;
        Object source = event.getSource();

        boolean isSubItem = (source == btnDatBan || source == btnCheckIn || source == btnCheckOut || source == btnCapNhatDonBan);


        if (!isSubItem && source != btnQuanLiDatBan && subMenuDatBan.isVisible()) {
            hideSubMenu();
            clearAllSelected();
        }

        // ===================== ĐIỀU HƯỚNG =====================
        if (source == btnDashboard) {
            mainController.setCenterContent("/FXML/DashBoard.fxml");
            setMainSelected(btnDashboard);

        } else if (source == btnDatBan) {
            mainController.setCenterContent("/FXML/DatBan.fxml");
            setSubSelected(btnDatBan, btnQuanLiDatBan);

        } else if (source == btnCheckIn) {
            mainController.setCenterContent("/FXML/CheckIn.fxml");
            setSubSelected(btnCheckIn, btnQuanLiDatBan);

        } else if (source == btnCheckOut) {
            mainController.setCenterContent("/FXML/CheckOut.fxml");
            setSubSelected(btnCheckOut, btnQuanLiDatBan);

        } else if (source == btnCapNhatDonBan) {
            mainController.setCenterContent("/FXML/QLDatBan.fxml");
            setSubSelected(btnCapNhatDonBan, btnQuanLiDatBan);

        } else if (source == btnQuanLiThanhVien) {
            mainController.setCenterContent("/FXML/QLThanhVien.fxml");
            setMainSelected(btnQuanLiThanhVien);

        } else if (source == btnTraCuu) {
            mainController.setCenterContent("/FXML/TraCuuHoaDon.fxml");
            setMainSelected(btnTraCuu);

        } else if (source == btnHoTro) {
            mainController.setCenterContent("/FXML/HoTroNV.fxml");
            setMainSelected(btnHoTro);

        } else if (source == btnKetCa) {
            mainController.setCenterContent("/FXML/BanGiaoCa.fxml");
            setMainSelected(btnKetCa);
        }
    }

    private void toggleSubMenuKeyboard() {
        toggleSubMenu(); // reuse logic giống chuột
    }

    private void showSubMenu() {
        if (!subMenuDatBan.isVisible()) {
            subMenuDatBan.setVisible(true);
            subMenuDatBan.setManaged(true);

            FadeTransition ft = new FadeTransition(Duration.millis(200), subMenuDatBan);
            ft.setFromValue(0.0);
            ft.setToValue(1.0);
            ft.play();
        }
        isSubMenuVisible = true;
    }

    private void hideSubMenu() {
        if (subMenuDatBan.isVisible()) {
            FadeTransition ft = new FadeTransition(Duration.millis(200), subMenuDatBan);
            ft.setFromValue(1.0);
            ft.setToValue(0.0);
            ft.setOnFinished(e -> {
                subMenuDatBan.setVisible(false);
                subMenuDatBan.setManaged(false);
            });
            ft.play();
        }
        isSubMenuVisible = false;
        clearAllSelected();
    }

    // =================== SELECT TAB CHO PHÍM TẮT ===================
    public void selectTab(int index) {

        switch (index) {
            // MAIN TABS
            case 1 -> {
                hideSubMenu();
                mainController.setCenterContent("/FXML/DashBoard.fxml");
                setMainSelected(btnDashboard);
            }
            case 2 -> toggleSubMenuKeyboard(); // toggle submenu như click
            case 3 -> {
                hideSubMenu();
                mainController.setCenterContent("/FXML/QLThanhVien.fxml");
                setMainSelected(btnQuanLiThanhVien);
            }
            case 4 -> {
                hideSubMenu();
                mainController.setCenterContent("/FXML/TraCuuHoaDon.fxml");
                setMainSelected(btnTraCuu);
            }
            case 5 -> {
                hideSubMenu();
                mainController.setCenterContent("/FXML/HoTroNV.fxml");
                setMainSelected(btnHoTro);
            }
            case 6 -> {
                hideSubMenu();
                mainController.setCenterContent("/FXML/BanGiaoCa.fxml");
                setMainSelected(btnKetCa);
            }

            // SUB MENU
            case 21 -> {
                showSubMenu();
                setSubSelected(btnDatBan, btnQuanLiDatBan);
                mainController.setCenterContent("/FXML/DatBan.fxml");
            }
            case 22 -> {
                showSubMenu();
                setSubSelected(btnCheckIn, btnQuanLiDatBan);
                mainController.setCenterContent("/FXML/CheckIn.fxml");
            }
            case 23 -> {
                showSubMenu();
                setSubSelected(btnCheckOut, btnQuanLiDatBan);
                mainController.setCenterContent("/FXML/CheckOut.fxml");
            }
            case 24 -> {
                showSubMenu();
                setSubSelected(btnCapNhatDonBan, btnQuanLiDatBan);
                mainController.setCenterContent("/FXML/QLDatBan.fxml");
            }
        }
    }

    // =================== SELECT / CLEAR ===================
    private void setMainSelected(Button btn) {
        clearAllSelected();
        btn.getStyleClass().add("selected");
        currentMainSelected = btn;
    }

    private void setSubSelected(Button child, Button parent) {
        clearAllSelected();
        parent.getStyleClass().add("selected");
        child.getStyleClass().add("selected");
        currentMainSelected = parent;
        currentSubSelected = child;
    }

    private void clearAllSelected() {
        if (currentMainSelected != null) currentMainSelected.getStyleClass().remove("selected");
        if (currentSubSelected != null) currentSubSelected.getStyleClass().remove("selected");
        currentMainSelected = null;
        currentSubSelected = null;
    }

}
