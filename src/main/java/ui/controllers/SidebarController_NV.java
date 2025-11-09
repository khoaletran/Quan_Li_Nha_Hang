package ui.controllers;

import entity.NhanVien;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
    private Button currentMainSelected = null;  // menu lớn
    private Button currentSubSelected = null;

    @FXML
    public void initialize() {
        javafx.scene.shape.Circle clip = new javafx.scene.shape.Circle(55, 55, 55);
        avatarImage.setClip(clip);
    }

    public void setThongTinNhanVien(NhanVien nv) {
        lblTenNV.setText(nv.getTenNV());
        lblChucVu.setText(nv.isQuanLi() ? "Quản Lí" : "Nhân Viên");

        Image img;
        if (nv.isGioiTinh()) {
            img = new Image(getClass().getResourceAsStream("/IMG/icon/man.png"));
        } else {
            img = new Image(getClass().getResourceAsStream("/IMG/icon/woman.png"));
        }

        avatarImage.setImage(img);
    }

    public void setMainController(MainController_NV controller) {
        this.mainController = controller;
    }

    // Ẩn/hiện submenu
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
            mainController.setCenterContent("/FXML/HoTro.fxml");
            setMainSelected(btnHoTro);

        } else if (source == btnKetCa) {
            mainController.setCenterContent("/FXML/BanGiaoCa.fxml");
            setMainSelected(btnKetCa);
        }
    }

    // Ẩn submenu với hiệu ứng
    private void hideSubMenu() {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(200), subMenuDatBan);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(e -> {
            subMenuDatBan.setVisible(false);
            subMenuDatBan.setManaged(false);
        });
        fadeOut.play();
    }

    // 🟢 Chọn menu chính
    private void setMainSelected(Button btn) {
        clearAllSelected();
        btn.getStyleClass().add("selected");
        currentMainSelected = btn;
    }

    // 🟢 Chọn menu con (và giữ cha)
    private void setSubSelected(Button child, Button parent) {
        clearAllSelected();
        parent.getStyleClass().add("selected");
        child.getStyleClass().add("selected");
        currentMainSelected = parent;
        currentSubSelected = child;
    }

    // 🧹 Xóa hết selected cũ (cha + con)
    private void clearAllSelected() {
        if (currentMainSelected != null) currentMainSelected.getStyleClass().remove("selected");
        if (currentSubSelected != null) currentSubSelected.getStyleClass().remove("selected");
        currentMainSelected = null;
        currentSubSelected = null;
    }
}
