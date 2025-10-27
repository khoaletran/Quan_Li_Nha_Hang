package ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ui.controllers.QRThanhToanController;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class QRThanhToan {

    // ===== Thông tin cố định của tài khoản nhận tiền =====
    private static final String BANK_ID = "970422";           // MB Bank
    private static final String ACCOUNT_NO = "292979798888";  // Số tài khoản
    private static final String ACCOUNT_NAME = "TRAN LE KHOA";

    // ===== Sinh URL QR =====
    public static String taoURLQR(double soTien, String noiDung) {
        try {
            String encodedInfo = URLEncoder.encode(noiDung, "UTF-8");
            String encodedName = URLEncoder.encode(ACCOUNT_NAME, "UTF-8");

            return "https://img.vietqr.io/image/" + BANK_ID + "-" + ACCOUNT_NO + "-compact2.png"
                    + "?amount=" + soTien
                    + "&addInfo=" + encodedInfo
                    + "&accountName=" + encodedName;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    // ===== Tạo đối tượng ảnh QR =====
    public static Image taoImageQR(double soTien, String noiDung) {
        String url = taoURLQR(soTien, noiDung);
        if (url == null) return null;
        return new Image(url, true);
    }

    // ===== HIỂN THỊ POPUP FXML =====
    public static void hienThiQRPanel(double tongTien, String maHD, Runnable onPaymentConfirmed) {
        try {
            FXMLLoader loader = new FXMLLoader(QRThanhToan.class.getResource("/FXML/QRThanhToan.fxml"));
            Parent root = loader.load();

            QRThanhToanController controller = loader.getController();
            controller.setThongTin(tongTien, maHD);


            controller.setOnPaymentConfirmed(() -> {
                if (onPaymentConfirmed != null) {
                    onPaymentConfirmed.run();
                }
            });

            Stage stage = new Stage();
            stage.setTitle("Thanh Toán Qua QR");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
