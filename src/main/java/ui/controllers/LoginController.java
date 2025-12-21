package ui.controllers;

import dao.NhanVienDAO;
import entity.NhanVien;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import ui.AlertCus;
import ui.AppConstants;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class LoginController {


    @FXML private VBox loginPane;
    @FXML private VBox forgotPane;
    @FXML private VBox resetPane;
    @FXML private Button closeBtn;
    @FXML private Button minimizeBtn;
    @FXML private Button loginBtn;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField newPassField;
    @FXML private PasswordField confirmPassField;

    private boolean DN = false;
    private NhanVien nhanvien;
    
    public TextField getUsernameField() {
        return usernameField;
    }
    // Hiệu ứng fade mượt
    private void switchPane(VBox hide, VBox show) {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(250), hide);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> {
            hide.setVisible(false);
            hide.setManaged(false);
            show.setVisible(true);
            show.setManaged(true);

            FadeTransition fadeIn = new FadeTransition(Duration.millis(250), show);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.play();
        });
        fadeOut.play();
    }

    // ===== Sự kiện chuyển màn hình =====

    @FXML
    private void close() {
        Stage stage = (Stage) closeBtn.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void minimize() {
        Stage stage = (Stage) minimizeBtn.getScene().getWindow();
        stage.setIconified(true);
    }



    @FXML
    private void showForgot() { switchPane(loginPane, forgotPane); }

    @FXML
    private void showLogin() {
        if (forgotPane.isVisible()) switchPane(forgotPane, loginPane);
        else switchPane(resetPane, loginPane);
    }

    @FXML
    private void showReset() { switchPane(forgotPane, resetPane); }

    public void setNhanVien(NhanVien nv){
        this.nhanvien = nv;
        if (nv != null) System.out.println("setNhanVien: " + nv.getMaNV());
    }


    @FXML
    private void login() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        List<NhanVien> listNV = NhanVienDAO.getAll();
        for (NhanVien nv : listNV) {
            if (nv.getMaNV().equals(username) && nv.getMatKhau().equals(password)) {
                if (!nv.isTrangThai()) {
                    AlertCus.show("Thông báo","Tài khoản không còn quyền truy cập!");
                    DN = true;
                    break;
                }
                if(nv.isQuanLi()) {
                    try {
                        // Đóng màn hình đăng nhập
                        Stage currentStage = (Stage) usernameField.getScene().getWindow();
                        currentStage.close();

                        // Mở giao diện chính (class MainNV)
                        ui.MainQL mainQL = new ui.MainQL();
                        Stage stage = new Stage();
                        mainQL.setNhanVienDangNhap(nv);
                        mainQL.show(stage);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    DN = true;
                    break;
                }
                else {
                    try {
                        Stage currentStage = (Stage) usernameField.getScene().getWindow();
                        currentStage.close();

                        ui.MainNV main = new ui.MainNV();
                        Stage stage = new Stage();
                        main.setNhanVienDangNhap(nv);
                        main.setThoiGianVaoCa(LocalDateTime.now());
                        main.show(stage);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    DN = true;
                    break;
                }
            }
        }

        if (username.equals(AppConstants.ADMIN) && password.equals(AppConstants.ADPASS)) {
            try {
                // Đóng màn hình đăng nhập
                Stage currentStage = (Stage) usernameField.getScene().getWindow();
                currentStage.close();

                // Mở giao diện chính (class MainNV)
                ui.MainQL mainQL = new ui.MainQL();
                Stage stage = new Stage();
                NhanVien nv = new NhanVien("NV0000","ADMIN","099999999",false,true, LocalDate.now(),true,AppConstants.ADPASS);
                mainQL.setNhanVienDangNhap(nv);
                mainQL.show(stage);

            } catch (Exception e) {
                e.printStackTrace();
            }
            DN = true;
        }

        if (!DN){
            AlertCus.show("Thông báo","Sai tên đăng nhập hoặc mật khẩu");
        }
    }
    public void showResetPane() {
        loginPane.setVisible(false);
        loginPane.setManaged(false);

        forgotPane.setVisible(false);
        forgotPane.setManaged(false);

        resetPane.setVisible(true);
        resetPane.setManaged(true);
    }

    @FXML
    private void resetDone() {
        String newPassword = newPassField.getText().trim();
        String confirmPassword = confirmPassField.getText().trim();

        // Kiểm tra mật khẩu không được trống
        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            AlertCus.show("Lỗi","Vui lòng nhập đầy đủ mật khẩu!");
            return;
        }
        // Kiểm tra mật khẩu khớp nhau
        if (!newPassword.equals(confirmPassword)) {
            AlertCus.show("Lỗi","Mật khẩu xác nhận không khớp!");
            return;
        }



        if (nhanvien.getMaNV() == null) {
            AlertCus.show("Lỗi","Không tìm thấy thông tin nhân viên!");
            return;
        }

        // Cập nhật mật khẩu mới xuống database
        boolean updateSuccess = NhanVienDAO.updateMatKhau(nhanvien.getMaNV(), newPassword);

        if (updateSuccess) {
            AlertCus.show("Thành công","Đổi mật khẩu thành công!");

            // Xóa dữ liệu trong các field
            newPassField.clear();
            confirmPassField.clear();

            // Quay lại màn hình đăng nhập
            switchPane(resetPane, loginPane);
        } else {
            AlertCus.show("Lỗi","Đổi mật khẩu thất bại!");
        }
    }

    // Phương thức hiển thị thông báo
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
