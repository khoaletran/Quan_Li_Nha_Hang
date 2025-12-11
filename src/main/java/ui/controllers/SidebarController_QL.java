package ui.controllers;

import entity.NhanVien;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class SidebarController_QL {

    // ===== Các nút chính =====
    @FXML private Button btnDashboard;
    @FXML private Button btnQLMenu;
    @FXML private Button btnQLBan;
    @FXML private Button btnQLNhanVien;
    @FXML private Button btnQLKhuyenMai;
    @FXML private Button btnQLChinhSach;
    @FXML private Button btnThongKe;
    @FXML private Button btnHoTro;
    @FXML private Button btnDangXuat;
    @FXML private Label lblTenNV;
    @FXML private Label lblChucVu;
    @FXML private ImageView avatarImage;

    private MainController_QL mainController;
    private Button currentSelected = null;

    @FXML
    public void initialize() {
        javafx.scene.shape.Circle clip = new javafx.scene.shape.Circle(55, 55, 55);
        avatarImage.setClip(clip);
    }
    public void selectTab(int index) {
        // Xóa highlight nút cũ
        clearSelected();

        // Chọn nút tương ứng
        switch (index) {
            case 1 -> setSelected(btnDashboard);
            case 2 -> setSelected(btnQLMenu);
            case 3 -> setSelected(btnQLBan);
            case 4 -> setSelected(btnQLNhanVien);
            case 5 -> setSelected(btnQLKhuyenMai);
            case 6 -> setSelected(btnQLChinhSach);
            case 7 -> setSelected(btnThongKe);
            case 8 -> setSelected(btnHoTro);
        }

        // Load nội dung
        openTab(index);
    }

    public void openTab(int index) {
        switch (index) {
            case 1 -> mainController.setCenterContent("/FXML/DashBoard.fxml");
            case 2 -> mainController.setCenterContent("/FXML/QLMenu.fxml");
            case 3 -> mainController.setCenterContent("/FXML/QLBan.fxml");
            case 4 -> mainController.setCenterContent("/FXML/QLNhanVien.fxml");
            case 5 -> mainController.setCenterContent("/FXML/KhuyenMai.fxml");
            case 6 -> mainController.setCenterContent("/FXML/ChinhSach.fxml");
            case 7 -> mainController.setCenterContent("/FXML/ThongKe.fxml");
            case 8 -> mainController.setCenterContent("/FXML/HoTroQL.fxml");
        }
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


    // Nhận tham chiếu từ MainController
    public void setMainController(MainController_QL controller) {
        this.mainController = controller;
    }

    // Xử lý khi bấm menu
    @FXML
    private void handleMenuAction(javafx.event.ActionEvent event) {
        if (mainController == null) return;

        Object source = event.getSource();
        clearSelected();

        if (source == btnDashboard) {
            mainController.setCenterContent("/FXML/DashBoard.fxml");
            setSelected(btnDashboard);
        } else if (source == btnQLMenu) {
            mainController.setCenterContent("/FXML/QLMenu.fxml");
            setSelected(btnQLMenu);
        } else if (source == btnQLBan) {
            mainController.setCenterContent("/FXML/QLBan.fxml");
            setSelected(btnQLBan);
        } else if (source == btnQLNhanVien) {
            mainController.setCenterContent("/FXML/QLNhanVien.fxml");
            setSelected(btnQLNhanVien);
        } else if (source == btnQLKhuyenMai) {
            mainController.setCenterContent("/FXML/KhuyenMai.fxml");
            setSelected(btnQLKhuyenMai);
        } else if (source == btnQLChinhSach) {
            mainController.setCenterContent("/FXML/ChinhSach.fxml");
            setSelected(btnQLChinhSach);
        } else if (source == btnThongKe) {
            mainController.setCenterContent("/FXML/ThongKe.fxml");
            setSelected(btnThongKe);
        } else if (source == btnHoTro) {
            mainController.setCenterContent("/FXML/HoTroQL.fxml");
            setSelected(btnHoTro);
        } else if (source == btnDangXuat) {
            Stage stage = (Stage) btnDangXuat.getScene().getWindow();
            ui.DangXuat.showDialog(stage);
        }


    }

    // Làm sáng nút đang chọn
    private void setSelected(Button btn) {
        btn.getStyleClass().add("selected");
        currentSelected = btn;
    }

    // Xóa sáng nút cũ
    private void clearSelected() {
        if (currentSelected != null) {
            currentSelected.getStyleClass().remove("selected");
        }
        currentSelected = null;
    }
}
