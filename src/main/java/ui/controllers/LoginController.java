package ui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button minimizeBtn, maximizeBtn, closeBtn, loginBtn;

    @FXML
    private Hyperlink forgotLink;

    private double xOffset = 0;
    private double yOffset = 0;

    // ====== Các hàm điều khiển cửa sổ ======
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


    // ====== Xử lý đăng nhập ======
    @FXML
    private void login() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Thiếu thông tin", "Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu.");
            return;
        }

        // Giả lập kiểm tra đăng nhập (sau này thay bằng DAO)
        if (username.equals("admin") && password.equals("123")) {
            showAlert(Alert.AlertType.INFORMATION, "Đăng nhập thành công", "Chào mừng " + username + " đến với CrabKing!");
            // TODO: Chuyển scene sang Trang Chủ Quản Lý
        } else {
            showAlert(Alert.AlertType.ERROR, "Đăng nhập thất bại", "Tên đăng nhập hoặc mật khẩu không đúng.");
        }
    }

    @FXML
    private void forgotPassword() {
        showAlert(Alert.AlertType.INFORMATION, "Quên mật khẩu", "Liên hệ quản lý hệ thống để được cấp lại mật khẩu.");
    }

    // ====== Hàm tiện ích ======
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
