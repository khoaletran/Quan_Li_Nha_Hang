package ui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import ui.QRThanhToan;

public class QRThanhToanController {

    @FXML private ImageView imgQR;
    @FXML private Button btnXacNhan;
    @FXML private Button btnHuy;

    private Runnable onPaymentConfirmed;

    private double tongTien;
    private String maHD;

    public void setThongTin(double tongTien, String maHD) {
        this.tongTien = tongTien;
        this.maHD = maHD;

        Image qrImage = QRThanhToan.taoImageQR(tongTien, "Thanh toan HD " + maHD);
        imgQR.setImage(qrImage);
    }

    public void setOnPaymentConfirmed(Runnable onPaymentConfirmed) {
        this.onPaymentConfirmed = onPaymentConfirmed;
    }

    @FXML
    public void initialize() {
        btnXacNhan.setOnAction(e -> {
            if (onPaymentConfirmed != null) {
                onPaymentConfirmed.run();
            }
            dongCuaSo();
        });

        btnHuy.setOnAction(e -> {
            System.out.println("Người dùng đã hủy giao dịch.");
            dongCuaSo();
        });
    }

    private void dongCuaSo() {
        Stage stage = (Stage) btnHuy.getScene().getWindow();
        stage.close();
    }
}
