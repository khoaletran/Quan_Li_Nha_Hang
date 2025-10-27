package ui.controllers;

import dao.HoaDonDAO;
import dao.KhachHangDAO;
import entity.HoaDon;
import entity.KhachHang;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import javax.swing.*;
import java.time.LocalDate;
import java.util.List;

public class CheckoutController {

    @FXML
    private ToggleGroup paymentGroup;
    @FXML private Label lblmaHD,lbltenKH,lblsdtKH,lblSoLuong,lblsuKien,lblKhuVuc,lblTongTien,lblGiamGia,lblThue,lblTongTT;
    @FXML private Label lbldsHD,lbldsSdt;
    @FXML private RadioButton rdoChuyenKhoan,rdoTienMat;
    @FXML private TextField txtMaGG,txtTienKhachDua;
    @FXML private VBox vboxHoaDon;

    @FXML
    public void initialize() {
        loadAllHoaDon();
    }

//    public void loadAllHoaDon() {
//        vboxHoaDon.getChildren().clear();
//        HoaDonDAO hoaDonDAO = new HoaDonDAO();
//        KhachHangDAO khachHangDAO = new KhachHangDAO();
//
//        List<HoaDon> dsHoaDon = hoaDonDAO.getAll();
//        for (HoaDon hd : dsHoaDon) {
//            HBox hbox = new HBox(15);
//            hbox.setAlignment(javafx.geometry.Pos.CENTER);
//            hbox.getStyleClass().add("invoice-card");
//
//
//            ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream("/IMG/ban/IN.png")));
//            imageView.setFitWidth(100);
//            imageView.setFitHeight(60);
//            imageView.setPreserveRatio(true);
//
//            Label lblMaHD = new Label(hd.getMaHD());
//            lblMaHD.getStyleClass().add("invoice-id");
//
//            String sdTKHTrenDB = "Không có";
//            List<KhachHang> dsKhachHang = khachHangDAO.getAll();
//            for (KhachHang kh : dsKhachHang) {
//                if (kh.getMaKhachHang().equals(hd.getKhachHang().getMaKhachHang())) {
//                     sdTKHTrenDB = kh.getSdt();
//                     break;
//                }
//            }
//
//            Label lblSDT = new Label("SĐT: " + (sdTKHTrenDB));
//            lblSDT.getStyleClass().add("invoice-phone");
//
//            AnchorPane anchorPane = new AnchorPane();
//            anchorPane.setPrefHeight(10);
//            anchorPane.setPrefWidth(200);
//
//            VBox vboxInfo = new VBox(lblMaHD, anchorPane, lblSDT);
//            Region region = new Region();
//            HBox.setHgrow(region, javafx.scene.layout.Priority.ALWAYS);
//            Button btnTime = new Button("🕒");
//            btnTime.getStyleClass().add("time-btn");
//            hbox.getChildren().addAll(imageView, vboxInfo, region, btnTime);
//
//            vboxHoaDon.getChildren().add(hbox);
//        }
//    }

    public void loadAllHoaDon() {
        vboxHoaDon.getChildren().clear();

        HoaDonDAO hoaDonDAO = new HoaDonDAO();
        KhachHangDAO khachHangDAO = new KhachHangDAO();

        List<HoaDon> dsHoaDon = hoaDonDAO.getAll();

        for (HoaDon hd : dsHoaDon) {
            HBox hbox = new HBox(15);
            hbox.setAlignment(javafx.geometry.Pos.CENTER);
            hbox.getStyleClass().add("invoice-card");

            // Ảnh hóa đơn
            ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream("/IMG/ban/IN.png")));
            imageView.setFitWidth(100);
            imageView.setFitHeight(60);
            imageView.setPreserveRatio(true);

            // Mã hóa đơn
            Label lblMaHD = new Label(hd.getMaHD());
            lblMaHD.getStyleClass().add("invoice-id");

            // === Lấy thông tin khách hàng ===
            KhachHang khachHang = khachHangDAO.getById(hd.getKhachHang().getMaKhachHang());
            String tenKH = (khachHang != null) ? khachHang.getTenKhachHang() : "Không rõ";
            String sdtKH = (khachHang != null) ? khachHang.getSdt() : "Không có";

            Label lblSDT = new Label("SĐT: " + sdtKH);
            lblSDT.getStyleClass().add("invoice-phone");

            AnchorPane anchorPane = new AnchorPane();
            anchorPane.setPrefHeight(10);
            anchorPane.setPrefWidth(200);

            VBox vboxInfo = new VBox(lblMaHD, anchorPane, lblSDT);

            Region region = new Region();
            HBox.setHgrow(region, javafx.scene.layout.Priority.ALWAYS);

            Button btnTime = new Button("🕒");
            btnTime.getStyleClass().add("time-btn");

            hbox.getChildren().addAll(imageView, vboxInfo, region, btnTime);

            // ======== SỰ KIỆN CLICK ==========
            hbox.setOnMouseClicked(e -> {
                lblmaHD.setText(hd.getMaHD());
                lbltenKH.setText(tenKH);
                lblsdtKH.setText(sdtKH);

                lblsuKien.setText(hd.getSuKien() != null ? hd.getSuKien().getTenSK() : "Không có");
                lblThue.setText(String.format("%.0f%%", hd.getThue() * 100));

                // Nếu bạn có thêm getter cho các giá trị bên dưới thì bật lại:
                // lblSoLuong.setText(String.valueOf(hd.getSoLuongMon()));
                // lblKhuVuc.setText(hd.getKhuVuc() != null ? hd.getKhuVuc().getTenKhuVuc() : "Không rõ");
                // lblTongTien.setText(String.format("%,.0f đ", hd.getTongTien()));
                // lblGiamGia.setText(String.format("-%,.0f đ", hd.getGiamGia()));
                // lblTongTT.setText(String.format("%,.0f đ", hd.getTongThanhToan()));
            });
            // =================================

            vboxHoaDon.getChildren().add(hbox);
        }
    }


}
