package ui.controllers;

import com.google.zxing.*;
import dao.*;
import entity.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import ui.AlertCus;
import ui.HoaDonIn;
import ui.QRThanhToan;

import java.util.List;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class CheckoutController {

    @FXML private ToggleGroup paymentGroup;
    @FXML private RadioButton rdoChuyenKhoan, rdoTienMat;
    @FXML private TextField txtMaGG, txtTienKhachDua;
    @FXML private VBox vboxHoaDon, vboxMenu, vboxTienMat;
    @FXML private Button btnCamera, btnGoiY1, btnGoiY2, btnGoiY3, btnGoiY4, btnGoiY5, btnGoiY6, btnThanhToan;
    @FXML private Label lblmaHD, lbltenKH, lblsdtKH, lblSoLuong, lblsuKien, lblKhuVuc, lblTongTien, lblGiamGia, lblThue, lblTongTT, lblTienThua, lblCoc, lblConLai;

    private HoaDon hdHienTai;


    private List<KhuyenMai> listKM = KhuyenMaiDAO.getAll();

    @FXML
    public void initialize() {
        loadAllHoaDon();
        xuLyHienThiTienMat();
        btnThanhToan.setOnAction(e -> xuLyThanhToan());

        txtMaGG.setOnAction(e -> updateThanhTien());
    }

    // ======== QUÉT MÃ QR GIẢM GIÁ ==========
    @FXML
    private void handleCameraButton() {
        new Thread(() -> {
            String maQR = QrCodeController.scanQRCodeWithPreview();

            if (maQR != null) {
                javafx.application.Platform.runLater(() -> {
                    txtMaGG.setText(maQR);
                    updateThanhTien();
                });
            } else {
                javafx.application.Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Không nhận được mã");
                    alert.setHeaderText(null);
                    alert.setContentText("Không quét được mã QR. Vui lòng thử lại!");
                    alert.showAndWait();
                });
            }
        }).start();
    }

    // ======== TÍNH TOÁN GIẢM GIÁ & TỔNG TIỀN ==========
    private void updateThanhTien() {
        if (lblmaHD.getText().isEmpty()) return;

        double tienTruoc = parseCurrency(lblTongTien.getText());
        double tienThue = 0;
        double tienGG = 0;

        String maGiamG = txtMaGG.getText().trim();
        if (!maGiamG.isEmpty()) {
            for (KhuyenMai km : listKM) {
                if( maGiamG.equals(km.getMaKM()) || maGiamG.equals(km.getMaThayThe()) ){
                    hdHienTai.setKhuyenMai(km);
                    break;
                }
            }
        }

        lblGiamGia.setText(formatCurrency(hdHienTai.getTongTienKhuyenMai()));
        lblTongTT.setText(formatCurrency(tienTruoc + tienThue - tienGG ));

        if (rdoTienMat.isSelected()) taoGoiYTienKhach();


        lblThue.setText(formatCurrency(hdHienTai.getThue()));
        lblTongTT.setText(formatCurrency(hdHienTai.getTongTienSau()));
        lblCoc.setText(formatCurrency(hdHienTai.getCoc()));
        lblConLai.setText(formatCurrency(hdHienTai.getTongTienSau()-hdHienTai.getCoc()));
    }


    // ======== HIỂN THỊ DANH SÁCH HÓA ĐƠN ==========
    public void loadAllHoaDon() {
        vboxHoaDon.getChildren().clear();

        HoaDonDAO hoaDonDAO = new HoaDonDAO();
        KhachHangDAO khachHangDAO = new KhachHangDAO();

        List<HoaDon> dsHoaDon = hoaDonDAO.getAll();

        for (HoaDon hd : dsHoaDon) {
            if (hd.getTrangthai() == 1) {
                HBox hbox = new HBox(15);
                hbox.setAlignment(javafx.geometry.Pos.CENTER);
                hbox.getStyleClass().add("invoice-card");

                ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream("/IMG/ban/IN.png")));
                imageView.setFitWidth(100);
                imageView.setFitHeight(60);

                Label lblMaHD = new Label(hd.getMaHD());
                lblMaHD.getStyleClass().add("invoice-id");

                KhachHang kh = khachHangDAO.getById(hd.getKhachHang().getMaKhachHang());
                String tenKH = (kh != null) ? kh.getTenKhachHang() : "Không rõ";
                String sdtKH = (kh != null) ? kh.getSdt() : "Không có";

                Label lblSDT = new Label("SĐT: " + sdtKH);
                lblSDT.getStyleClass().add("invoice-phone");

                VBox vboxInfo = new VBox(lblMaHD, new AnchorPane(), lblSDT);
                Region region = new Region();
                HBox.setHgrow(region, javafx.scene.layout.Priority.ALWAYS);

                Button btnTime = new Button("🕒");
                btnTime.getStyleClass().add("time-btn");

                hbox.getChildren().addAll(imageView, vboxInfo, region, btnTime);

                // ======== SỰ KIỆN CLICK ==========
                hbox.setOnMouseClicked(e -> {
                    hdHienTai = hd;
                    lblmaHD.setText(hd.getMaHD());
                    lbltenKH.setText(tenKH);
                    lblsdtKH.setText(sdtKH);
                    lblsuKien.setText(hd.getSuKien() != null ? hd.getSuKien().getTenSK() : "Không có");

                    BanDAO banDAO = new BanDAO();
                    KhuVucDAO khuVucDAO = new KhuVucDAO();
                    KhuVuc kv = khuVucDAO.getById(hd.getBan().getKhuVuc().getMaKhuVuc());
                    lblKhuVuc.setText(kv != null ? kv.getTenKhuVuc() : "?");

                    txtMaGG.clear();
                    lblSoLuong.setText(String.valueOf(hd.getSoLuong()));
                    lblTongTien.setText(formatCurrency(hd.getTongTienTruoc()));
                    lblThue.setText(formatCurrency(hd.getThue()));
                    lblTongTT.setText(formatCurrency(hd.getTongTienSau()));
                    lblCoc.setText(formatCurrency(hd.getCoc()));
                    lblConLai.setText(formatCurrency(hd.getTongTienSau()-hd.getCoc()));


                    updateThanhTien();

                    // load chi tiết món
                    vboxMenu.getChildren().clear();
                    ChiTietHDDAO ctDAO = new ChiTietHDDAO();
                    List<ChiTietHoaDon> dsCT = ctDAO.getByMaHD(hd.getMaHD());

                    int stt = 1;
                    for (ChiTietHoaDon ct : dsCT) {
                        HBox row = new HBox(10);
                        row.getStyleClass().add("menu-row");

                        Label lblSTT = new Label(String.valueOf(stt++));
                        lblSTT.getStyleClass().add("col-stt");
                        Label lblName = new Label(ct.getMon().getTenMon());
                        lblName.getStyleClass().add("col-name");
                        Label lblQty = new Label(String.valueOf(ct.getSoLuong()));
                        lblQty.getStyleClass().add("col-qty");
                        Label lblPrice = new Label(formatCurrency(ct.getMon().getGiaBan()));
                        lblPrice.getStyleClass().add("col-price");
                        Label lblDiscount = new Label("0%");
                        lblDiscount.getStyleClass().add("col-discount");
                        Label lblTotal = new Label(formatCurrency(ct.getThanhTien()));
                        lblTotal.getStyleClass().add("col-total");

                        row.getChildren().addAll(lblSTT, lblName, lblQty, lblPrice, lblDiscount, lblTotal);
                        vboxMenu.getChildren().add(row);
                    }
                });

                vboxHoaDon.getChildren().add(hbox);
            }
        }
    }

    // ======== GỢI Ý TIỀN MẶT + TÍNH TIỀN THỪA ==========
    private void xuLyHienThiTienMat() {
        paymentGroup.selectedToggleProperty().addListener((obs, oldT, newT) -> {
            boolean isTienMat = newT == rdoTienMat;
            vboxTienMat.setVisible(isTienMat);

            Button[] nut = {btnGoiY1, btnGoiY2, btnGoiY3, btnGoiY4, btnGoiY5, btnGoiY6};
            for (Button b : nut) b.setDisable(!isTienMat);

            if (isTienMat) taoGoiYTienKhach();
        });
    }

    private void taoGoiYTienKhach() {
        double tongTien = parseCurrency(lblTongTT.getText());
        if (tongTien <= 0) return;

        double base = Math.round(tongTien / 1000.0) * 1000;
        double[] goiY;

        if (base < 1_000_000) {
            goiY = new double[]{base, Math.ceil(base / 10_000) * 10_000,
                    Math.ceil(base / 50_000) * 50_000,
                    Math.ceil(base / 100_000) * 100_000,
                    500_000, 1_000_000};
        } else if (base < 5_000_000) {
            goiY = new double[]{base, Math.ceil(base / 50_000) * 50_000,
                    Math.ceil(base / 100_000) * 100_000,
                    Math.ceil(base / 500_000) * 500_000,
                    5_000_000, 10_000_000};
        } else {
            goiY = new double[]{base,
                    Math.ceil(base / 100_000) * 100_000,
                    Math.ceil(base / 500_000) * 500_000,
                    Math.ceil(base / 1_000_000) * 1_000_000,
                    base + 2_000_000, base + 5_000_000};
        }

        Button[] nut = {btnGoiY1, btnGoiY2, btnGoiY3, btnGoiY4, btnGoiY5, btnGoiY6};
        for (int i = 0; i < nut.length; i++) {
            if (i < goiY.length) {
                double val = goiY[i];
                nut[i].setText(formatCurrency(val));
                nut[i].setVisible(true);
                nut[i].setOnAction(e -> {
                    txtTienKhachDua.setText(formatCurrency(val));
                    tinhTienThua();
                });
            } else nut[i].setVisible(false);
        }
    }

    private void tinhTienThua() {
        double tong = parseCurrency(lblTongTT.getText());
        double tienKD = parseCurrency(txtTienKhachDua.getText());
        lblTienThua.setText(formatCurrency(tienKD - tong));
    }

    // ======== ĐỊNH DẠNG TIỀN ==========
    private double parseCurrency(String text) {
        if (text == null || text.isBlank()) return 0;
        String clean = text.replaceAll("[^\\d]", "");
        if (clean.isEmpty()) return 0;
        return Double.parseDouble(clean);
    }

    private String formatCurrency(double amount) {
        Locale localeVN = new Locale("vi", "VN");
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(localeVN);
        DecimalFormat df = new DecimalFormat("#,###", symbols);
        return df.format(amount) + " đ";
    }


    private void xuLyThanhToan() {
        if (hdHienTai == null) {
            AlertCus.show("⚠️ Chưa chọn hóa đơn", "Vui lòng chọn hóa đơn trước khi thanh toán!");
            return;
        }

        KhachHang kh = hdHienTai.getKhachHang();
        boolean isTienMat = rdoTienMat.isSelected();
        double tongConLai = parseCurrency(lblConLai.getText().trim());

        // ===== 💵 THANH TOÁN TIỀN MẶT =====
        if (isTienMat) {
            double tienKhach = parseCurrency(txtTienKhachDua.getText().trim());

            if (tienKhach < tongConLai) {
                AlertCus.show("⚠️ Thiếu tiền", "Số tiền khách đưa chưa đủ để thanh toán!");
                return;
            }

            // cập nhật hóa đơn
            hdHienTai.setTrangthai(2);
            hdHienTai.setTgCheckOut(java.time.LocalDateTime.now());
            HoaDonDAO.update(hdHienTai);

            // cộng điểm tích lũy
            congDiemTichLuy(kh, hdHienTai.getTongTienTruoc());

            // mở bàn
            BanDAO.update(hdHienTai.getBan(), false);

            // thông báo
            double tienThua = tienKhach - tongConLai;
            AlertCus.show("✅ Thanh toán thành công",
                    "Khách đã thanh toán " + formatCurrency(tienKhach) +
                            "\nTiền thừa: " + formatCurrency(tienThua));

            HoaDonIn.inHoaDonNhanh(hdHienTai);
            loadAllHoaDon(); // làm mới danh sách
            return;
        }

        // ===== 💳 THANH TOÁN CHUYỂN KHOẢN =====
        QRThanhToan.hienThiQRPanel(tongConLai, hdHienTai.getMaHD(), () -> {
            hdHienTai.setTrangthai(2);
            hdHienTai.setTgCheckOut(java.time.LocalDateTime.now());
            HoaDonDAO.update(hdHienTai);

            congDiemTichLuy(kh, hdHienTai.getTongTienTruoc());
            BanDAO.update(hdHienTai.getBan(), false);

            javafx.application.Platform.runLater(() -> {
                AlertCus.show("✅ Thanh toán thành công",
                        "Khách đã chuyển khoản đủ " + formatCurrency(tongConLai) +
                                "\nHóa đơn " + hdHienTai.getMaHD() + " đã hoàn tất.");

                HoaDonIn.previewHoaDon(hdHienTai);
                loadAllHoaDon();
            });
        });
    }


    private void congDiemTichLuy(KhachHang khachHang, double tongTien) {
        int diem = (int) (tongTien * 0.1 / 100 );
        if (khachHang == null) return;
        khachHang.setDiemTichLuy(khachHang.getDiemTichLuy() + diem);
        KhachHangDAO.update(khachHang);
    }

}
