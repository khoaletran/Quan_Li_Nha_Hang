package ui.controllers;

import entity.NhanVien;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import javax.swing.*;
import java.io.IOException;

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
            mainController.setCenterContent("/FXML/HoTro.fxml");
            setSelected(btnHoTro);
        } else if (source == btnDangXuat) {
            ui.DangXuat.showDialog();
        }


    }

    // Làm sáng nút đang chọn
    private void setSelected(Button btn) {
        btn.getStyleClass().add("selectead");
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
