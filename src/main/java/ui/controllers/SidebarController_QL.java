package ui.controllers;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

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

    private MainController_QL mainController;
    private Button currentSelected = null;

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
            mainController.setCenterContent("/FXML/HoTro.fxml");
            setSelected(btnHoTro);
        } else if (source == btnDangXuat) {
            mainController.setCenterContent("/FXML/DangNhap.fxml");
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
