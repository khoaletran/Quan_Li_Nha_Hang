package ui.controllers;

import entity.NhanVien;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class DashboardController {

    @FXML private Label lblMaNV;
    @FXML private Label lblTenNV;
    @FXML private Label lblSDT;
    @FXML private Label lblChucVu;
    @FXML private Label lblGioiTinh;
    @FXML private Label lblNgayVaoLam;
    @FXML private Circle circleTrangThai;

    @FXML private ImageView avatarImage;
    @FXML private Circle avatarClip;

    private NhanVien nv;

    @FXML
    public void initialize() {
        if (avatarImage != null && avatarClip != null) {
            avatarImage.setClip(avatarClip);
        }
    }

    /** Nhận controller cha (NV hoặc QL) */
    public void setMainController(Object controller) {
        if (controller instanceof MainController_NV nvCtrl) {
            this.nv = nvCtrl.getNhanVien();
        } else if (controller instanceof MainController_QL qlCtrl) {
            this.nv = qlCtrl.getNhanVien();
        }

        if (nv == null) {
            System.out.println("[DashboardController] ⚠️ NhanVien chưa được truyền vào!");
            return;
        }

        hienThiThongTinNhanVien();
    }

    public void setNhanVien(NhanVien nv) {
        this.nv = nv;
        hienThiThongTinNhanVien();
    }

    private void hienThiThongTinNhanVien() {
        if (nv == null) return;

        lblMaNV.setText("Mã Nhân Viên: " + nv.getMaNV());
        lblTenNV.setText("Họ Và Tên: " + nv.getTenNV());
        lblSDT.setText("SĐT: " + nv.getSdt());
        lblChucVu.setText("Chức Vụ: " + (nv.isQuanLi() ? "Quản Lý" : "Nhân Viên"));
        lblGioiTinh.setText("Giới Tính: " + (nv.isGioiTinh() ? "Nam" : "Nữ"));

        if (nv.getNgayVaoLam() != null) {
            lblNgayVaoLam.setText("Ngày Vào Làm: " +
                    nv.getNgayVaoLam().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        } else {
            lblNgayVaoLam.setText("Ngày Vào Làm: -");
        }


        // Avatar theo giới tính
        try {
            String imgPath = nv.isGioiTinh()
                    ? "/IMG/icon/man.png"
                    : "/IMG/icon/woman.png";
            avatarImage.setImage(new Image(getClass().getResourceAsStream(imgPath)));
        } catch (Exception e) {
            avatarImage.setImage(new Image(getClass().getResourceAsStream("/IMG/avatar.png")));
        }
    }

    @FXML
    private void showChangePassword() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/login.fxml"));
            Parent loginRoot = loader.load();

            LoginController loginController = loader.getController();
            loginController.setNhanVien(nv);
            loginController.showResetPane();

            Stage stage = new Stage();
            stage.setScene(new Scene(loginRoot));
            stage.setTitle("Đổi Mật Khẩu");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
