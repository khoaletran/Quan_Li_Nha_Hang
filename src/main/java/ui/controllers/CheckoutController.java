package ui.controllers;

import dao.ChiTietHDDAO;
import dao.KhuyenMaiDAO;
import dao.KhuVucDAO;
import dao.BanDAO;
import dao.HoaDonDAO;
import dao.KhachHangDAO;
import entity.*;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
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
    @FXML private VBox vboxHoaDon,vboxMenu;

    @FXML
    public void initialize() {
        loadAllHoaDon();
        txtMaGG.textProperty().addListener((obs, oldText, newText) -> {
            updateThanhTien();
        });
    }


    private void updateThanhTien() {
        if(lblmaHD.getText().isEmpty()) return; // chưa chọn hóa đơn

        double tienTruoc = Double.parseDouble(lblTongTien.getText().replaceAll("[^0-9]", ""));
        double tienThue = tienTruoc / 10; // 10% thuế
        double tienGG = 0;

        String maGiamG = txtMaGG.getText().trim();
        if(!maGiamG.isEmpty()) {
            KhuyenMai khuyenMai = new KhuyenMaiDAO().getByID(maGiamG);
            if(khuyenMai != null && maGiamG.equals(khuyenMai.getMaKM())) {
                tienGG = tienTruoc * khuyenMai.getPhanTRamGiamGia() / 100.0;
            }
        }

        lblGiamGia.setText(String.format("-%,.0f đ", tienGG));
        lblTongTT.setText(String.format("%,.0f đ", tienTruoc + tienThue - tienGG));
    }


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

                BanDAO banDAO = new BanDAO();
                KhuVucDAO khuVucDAO = new KhuVucDAO();
                KhuyenMaiDAO khuyenMaiDAO = new KhuyenMaiDAO();
                KhuVuc khuVuc = null;
                List<Ban> dsBan = banDAO.getAll();
                for (Ban bd : dsBan){
                    if(bd.getMaBan().equals(hd.getBan().getMaBan())){
                        khuVuc = khuVucDAO.getById(bd.getKhuVuc().getMaKhuVuc());
                        break;
                    }
                }
                lblSoLuong.setText(String.valueOf(hd.getSoLuong()));
                lblKhuVuc.setText(khuVuc.getTenKhuVuc());
                double tienTruoc = hd.getTongTienTruoc();
                double tienThue = hd.getTongTienTruoc()/10;
                lblThue.setText(String.format("%,.0f đ", tienThue));
                 lblTongTien.setText(String.format("%,.0f đ", tienTruoc));

                updateThanhTien();
                vboxMenu.getChildren().clear();
                vboxMenu.getStyleClass().add("menu-list");

                // Lấy danh sách chi tiết món ăn
                ChiTietHDDAO chiTietDAO = new ChiTietHDDAO();
                List<ChiTietHoaDon> dsCTHD = chiTietDAO.getByMaHD(hd.getMaHD());

                int stt = 1;
                for (ChiTietHoaDon cthd : dsCTHD) {
                    HBox hboxRow = new HBox(10);
                    hboxRow.getStyleClass().add("menu-row");

                    Label lblSTT = new Label(String.valueOf(stt++));
                    lblSTT.getStyleClass().add("col-stt");
                    Label lblName = new Label(cthd.getMon().getTenMon());
                    lblName.getStyleClass().add("col-name");
                    Label lblQty = new Label(String.valueOf(cthd.getSoLuong()));
                    lblQty.getStyleClass().add("col-qty");
                    Label lblPrice = new Label(String.format("%,.0f đ", cthd.getMon().getGiaBan()));
                    lblPrice.getStyleClass().add("col-price");
                    Label lblDiscount = new Label("0%");
                    lblDiscount.getStyleClass().add("col-discount");
                    Label lblTotal = new Label(String.format("%,.0f đ", cthd.getThanhTien()));
                    lblTotal.getStyleClass().add("col-total");

                    hboxRow.getChildren().addAll(lblSTT, lblName, lblQty, lblPrice, lblDiscount, lblTotal);
                    vboxMenu.getChildren().add(hboxRow);
                }
            });

            vboxHoaDon.getChildren().add(hbox);
        }
    }


}
