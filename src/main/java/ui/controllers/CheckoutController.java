package ui.controllers;

import com.google.zxing.*;
import dao.*;
import entity.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.*;
import ui.AlertCus;
import ui.HoaDonIn;
import ui.QRThanhToan;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class CheckoutController {

    @FXML private ToggleGroup paymentGroup;
    @FXML private RadioButton rdoChuyenKhoan, rdoTienMat;
    @FXML private TextField txtMaGG, txtTienKhachDua, searchField;
    @FXML private VBox vboxHoaDon, vboxMenu, vboxTienMat;
    @FXML private Button btnSearch, btnCamera, btnGoiY1, btnGoiY2, btnGoiY3, btnGoiY4, btnGoiY5, btnGoiY6, btnThanhToan;
    @FXML private Label lblmaHD, lbltenKH, lblsdtKH, lblsuKien, lblKhuVuc, lblTongTien, lblGiamGia, lblGiamGia1, lblThue, lblTongTT, lblTienThua, lblCoc, lblConLai;

    private HoaDon hdHienTai;

    private List<KhuyenMai> listKM = KhuyenMaiDAO.getAll();
    private List<HoaDon> allHoaDon = new ArrayList<>();


    @FXML
    public void initialize() {
        System.out.println("Initializing CheckoutController");
        loadAllHoaDon();
        xuLyHienThiTienMat();
        btnThanhToan.setOnAction(e -> xuLyThanhToan());

        txtMaGG.textProperty().addListener((obs, oldV, newV) -> updateThanhTien());

        Platform.runLater(() -> addShortcuts(searchField.getScene()));
        Tooltip tipFind = new Tooltip("Tìm kiếm hóa đơn (Ctrl + F)");
        Tooltip.install(searchField, tipFind);
        Tooltip tipCheck = new Tooltip("Check out khách hàng (Ctrl + B)");
        Tooltip.install(btnThanhToan, tipCheck);

        btnSearch.setOnAction(e -> timKiemHoaDon());

        searchField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                timKiemHoaDon();
            }
        });

    }

    private void addShortcuts(Scene scene){
        KeyCombination ctrlF = new KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN);
        scene.getAccelerators().put(ctrlF, () -> {
            searchField.requestFocus();
            searchField.selectAll();
        });
        KeyCombination ctrlB = new KeyCodeCombination(KeyCode.B, KeyCombination.CONTROL_DOWN);
        scene.getAccelerators().put(ctrlB, () -> xuLyThanhToan());
    }

    // ======== QUÉT MÃ QR GIẢM GIÁ ==========
    private boolean dangQuetQR = false;

    @FXML
    private void handleCameraButton() {
        if (dangQuetQR) return;
        dangQuetQR = true;

        new Thread(() -> {
            String maQR = QrCodeController.scanQRCodeWithPreview();
            Platform.runLater(() -> {
                dangQuetQR = false;
                if (maQR != null) {
                    txtMaGG.setText(maQR);
                    updateThanhTien();
                }
            });
        }).start();
    }

    // ======== TÍNH TOÁN GIẢM GIÁ & TỔNG TIỀN ==========
    private void updateThanhTien() {
        if (hdHienTai == null || lblmaHD.getText().isEmpty()) return;

        String code = txtMaGG.getText().trim();

        KhuyenMai found = null;
        if (!code.isEmpty()) {
            found = KhuyenMaiDAO.getByCode(code);
            if (!isKmConHieuLuc(found)) found = null;
        }
        hdHienTai.setKhuyenMai(found);

        lblGiamGia.setText(formatCurrency(hdHienTai.getTongTienKhuyenMai()));
        lblGiamGia1.setText("( Voucher: " + formatCurrency(hdHienTai.getTienMaKM())
                + " | Voucher Hạng: " + formatCurrency(hdHienTai.getTienHangKM()) + " )");

        if (rdoTienMat.isSelected()) taoGoiYTienKhach();

        double tongTien = hdHienTai.getTongTienTruoc();
        double thue = tongTien * 0.1;
        double tongTT = hdHienTai.getTongTienSau();
        double coc = hdHienTai.getCoc();
        double conLai = hdHienTai.getTongTienSau();

        lblTongTien.setText(formatCurrency(tongTien));
        lblThue.setText(formatCurrency(thue));
        lblTongTT.setText(formatCurrency(tongTT));
        lblCoc.setText(formatCurrency(coc));
        lblConLai.setText(formatCurrency(conLai));
    }




    // ======== HIỂN THỊ DANH SÁCH HÓA ĐƠN ==========
    public void loadAllHoaDon() {
        vboxHoaDon.getChildren().clear();

//        List<HoaDon> dsHoaDon = HoaDonDAO.getAll();
        allHoaDon = HoaDonDAO.getAll();
        List<KhachHang> dsKH = KhachHangDAO.getAll();
        List<KhuVuc> dsKV = KhuVucDAO.getAll();
        List<ChiTietHoaDon> dsCTAll = ChiTietHDDAO.getAll();

        // Gom tất cả chi tiết theo mã hóa đơn
        Map<String, List<ChiTietHoaDon>> mapCT = dsCTAll.stream()
                .collect(Collectors.groupingBy(ct -> ct.getHoaDon().getMaHD()));

        for (HoaDon hd : allHoaDon) {
            if (hd.getTrangthai() != 1) continue;

            HBox hbox = new HBox(15);
            hbox.setAlignment(Pos.CENTER);
            hbox.getStyleClass().add("invoice-card");

            ImageView imageView = new ImageView(new Image(
                    getClass().getResourceAsStream("/IMG/ban/IN.png")));
            // Ảnh cache static
            imageView.setFitWidth(100);
            imageView.setFitHeight(60);

            Label lblMaHD = new Label(hd.getMaHD());
            lblMaHD.getStyleClass().add("invoice-id");

            KhachHang kh = dsKH.stream()
                    .filter(k -> k.getMaKhachHang().equals(hd.getKhachHang().getMaKhachHang()))
                    .findFirst().orElse(null);

            String tenKH = kh != null ? kh.getTenKhachHang() : "Không rõ";
            String sdtKH = kh != null ? kh.getSdt() : "Không có";

            VBox info = new VBox(lblMaHD, new Label("SĐT: " + sdtKH), new Label("Bàn: " + hd.getBan().getMaBan()));
            Region region = new Region();
            HBox.setHgrow(region, Priority.ALWAYS);

            Button btnTime = new Button("🕒");
            btnTime.getStyleClass().add("time-btn");
            hbox.getChildren().addAll(imageView, info, region, btnTime);

            // Sự kiện click
            hbox.setOnMouseClicked(e -> {
                hdHienTai = hd;
                txtMaGG.clear();
                lblmaHD.setText(hd.getMaHD());
                lbltenKH.setText(tenKH);
                lblsdtKH.setText(sdtKH);
                lblsuKien.setText(hd.getSuKien() != null ? hd.getSuKien().getTenSK() : "Không có");

                KhuVuc kv = dsKV.stream()
                        .filter(k -> k.getMaKhuVuc().equals(hd.getBan().getKhuVuc().getMaKhuVuc()))
                        .findFirst().orElse(null);
                lblKhuVuc.setText(kv != null ? kv.getTenKhuVuc() : "?");

                List<ChiTietHoaDon> dsCT = mapCT.getOrDefault(hd.getMaHD(), new ArrayList<>());

                updateThanhTien();

                new Thread(() -> {
                    Platform.runLater(() -> {
                        vboxMenu.getChildren().clear();
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
                            Label lblPrice = new Label(formatCurrency(ct.getMon().getGiaBanTaiLucLapHD(hd)));
                            lblPrice.getStyleClass().add("col-price"); Label lblDiscount = new Label("0%");
                            lblDiscount.getStyleClass().add("col-discount");
                            Label lblTotal = new Label(formatCurrency(ct.getThanhTien()));
                            lblTotal.getStyleClass().add("col-total");
                            row.getChildren().addAll(lblSTT, lblName, lblQty, lblPrice, lblDiscount, lblTotal);
                            vboxMenu.getChildren().add(row);
                        }
                    });
                }).start();
            });

            vboxHoaDon.getChildren().add(hbox);
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
        double tongTien = parseCurrency(lblConLai.getText());
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
        double tong = parseCurrency(lblConLai.getText());
        double tienKD = parseCurrency(txtTienKhachDua.getText());
        double tienThua = tienKD - tong;

        // Nếu tiền thừa < 1000 hoặc âm → gán 0
        if (tienThua < 1000) {
            tienThua = 0;
        } else {
            // Làm tròn đến 1.000 gần nhất
            tienThua = Math.round(tienThua / 1000.0) * 1000;
        }

        lblTienThua.setText(formatCurrency(tienThua));
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
            AlertCus.show("Chưa chọn hóa đơn", "Vui lòng chọn hóa đơn trước khi thanh toán!");
            return;
        }

        KhachHang kh = hdHienTai.getKhachHang();
        boolean isTienMat = rdoTienMat.isSelected();
        double tongConLai = parseCurrency(lblConLai.getText().trim());


        if (isTienMat) {
            double tienKhach = parseCurrency(txtTienKhachDua.getText().trim());

            if (tienKhach < tongConLai) {
                AlertCus.show("Thiếu tiền", "Số tiền khách đưa chưa đủ để thanh toán!");
                return;
            }
            KhuyenMai km = hdHienTai.getKhuyenMai();
            if (km != null) {
                KhuyenMai fresh = KhuyenMaiDAO.getByID(km.getMaKM());
                if (!isKmConHieuLuc(fresh)) {
                    AlertCus.show("Voucher không hợp lệ", "Voucher đã hết hạn / chưa tới ngày / hoặc hết số lượng.");
                    return;
                }
                // giữ slot trước
                boolean ok = KhuyenMaiDAO.giamSoLuongAtomic(fresh.getMaKM());
                if (!ok) {
                    AlertCus.show("Voucher hết lượt", "Voucher vừa hết số lượng, không thể áp dụng.");
                    return;
                }
                hdHienTai.setKhuyenMai(fresh);
            }

            hdHienTai.setTrangthai(2);
            hdHienTai.setTgCheckOut(LocalDateTime.now());
            boolean okUpdate = HoaDonDAO.update(hdHienTai);
            if (!okUpdate) {
                if (hdHienTai.getKhuyenMai() != null) {
                    KhuyenMaiDAO.tangSoLuongAtomic(hdHienTai.getKhuyenMai().getMaKM());
                }
                AlertCus.show("Lỗi", "Không thể cập nhật hóa đơn. Vui lòng thử lại.");
                return;
            }

            // cộng điểm tích lũy
            congDiemTichLuy(kh, hdHienTai.getTongTienTruoc());

            // mở bàn
            BanDAO.update(hdHienTai.getBan(), false);

            // thông báo
            double tienThua = tienKhach - tongConLai;
            AlertCus.show("Thanh toán thành công",
                    "Khách đã thanh toán " + formatCurrency(tienKhach) +
                            "\nTiền thừa: " + formatCurrency(tienThua));

            HoaDonIn.previewHoaDon(hdHienTai);
            loadAllHoaDon();
            clearCheckoutInfo();
            vboxMenu.getChildren().clear();
            return;
        }

        QRThanhToan.hienThiQRPanel(tongConLai, hdHienTai.getMaHD(), () -> {

            // 1) Validate + giữ slot voucher (không đụng UI ở đây nếu callback không phải FX thread)
            KhuyenMai km = hdHienTai.getKhuyenMai();
            if (km != null) {
                KhuyenMai fresh = KhuyenMaiDAO.getByID(km.getMaKM());
                if (!isKmConHieuLuc(fresh)) {
                    Platform.runLater(() ->
                            AlertCus.show("Voucher không hợp lệ", "Voucher đã hết hạn / chưa tới ngày / hoặc hết số lượng.")
                    );
                    return;
                }

                boolean ok = KhuyenMaiDAO.giamSoLuongAtomic(fresh.getMaKM());
                if (!ok) {
                    Platform.runLater(() ->
                            AlertCus.show("Voucher hết lượt", "Voucher vừa hết số lượng, không thể áp dụng.")
                    );
                    return;
                }

                hdHienTai.setKhuyenMai(fresh);
            }

            // 2) Chốt hóa đơn sau khi giữ slot OK
            hdHienTai.setTrangthai(2);
            hdHienTai.setTgCheckOut(LocalDateTime.now());

            boolean updated = HoaDonDAO.update(hdHienTai);
            if (!updated) {
                if (hdHienTai.getKhuyenMai() != null) {
                    KhuyenMaiDAO.tangSoLuongAtomic(
                            hdHienTai.getKhuyenMai().getMaKM()
                    );
                }

                Platform.runLater(() ->
                        AlertCus.show("Lỗi", "Không thể cập nhật hóa đơn. Vui lòng thử lại.")
                );
                return;
            }

            congDiemTichLuy(kh, hdHienTai.getTongTienTruoc());
            BanDAO.update(hdHienTai.getBan(), false);

            Platform.runLater(() -> {
                AlertCus.show("Thanh toán thành công",
                        "Khách đã chuyển khoản đủ " + formatCurrency(tongConLai) +
                                "\nHóa đơn " + hdHienTai.getMaHD() + " đã hoàn tất.");

                HoaDonIn.previewHoaDon(hdHienTai);
                loadAllHoaDon();
                clearCheckoutInfo();
                vboxMenu.getChildren().clear();
            });
        });

    }


    private void congDiemTichLuy(KhachHang khachHang, double tongTien) {
        int diem = (int) (tongTien * 0.1 / 100 );
        if (khachHang == null) return;
        khachHang.setDiemTichLuy(khachHang.getDiemTichLuy() + diem);
        KhachHangDAO.update(khachHang);
    }

    private void giamSLMaKM(String maKM){
        if (maKM == null || maKM.isBlank()) return;
        KhuyenMai km = KhuyenMaiDAO.getByID(maKM);
        if (km == null) return;
        if (km.getSoLuong() <= 0) return;
        km.setSoLuong(km.getSoLuong() - 1);
        KhuyenMaiDAO.update(km);
    }

    private boolean isKmConHieuLuc(KhuyenMai km) {
        if (km == null) return false;

        if (km.getSoLuong() <= 0) return false;

        java.time.LocalDate today = java.time.LocalDate.now();

        java.time.LocalDate start = km.getNgayPhatHanh(); // LocalDate
        java.time.LocalDate end   = km.getNgayKetThuc();  // LocalDate

        if (start != null && today.isBefore(start)) return false; // chưa phát hành
        if (end != null && today.isAfter(end)) return false;      // hết hạn

        return true;
    }



    private void clearCheckoutInfo() {
        hdHienTai = null;

        lblmaHD.setText("");
        lbltenKH.setText("");
        lblsdtKH.setText("");
        lblsuKien.setText("");
        lblKhuVuc.setText("");

        lblTongTien.setText("0 đ");
        lblGiamGia.setText("0 đ");
        lblThue.setText("0 đ");
        lblTongTT.setText("0 đ");
        lblCoc.setText("0 đ");
        lblConLai.setText("0 đ");
        lblTienThua.setText("0 đ");

        txtMaGG.clear();
        txtTienKhachDua.clear();

        vboxMenu.getChildren().clear();
    }

    private void timKiemHoaDon() {
        String keyword = searchField.getText().trim().toLowerCase();

        if (keyword.isEmpty()) {
            loadAllHoaDon();
            return;
        }

        List<HoaDon> ketQua = allHoaDon.stream()
                .filter(hd -> {
                    String maHD = hd.getMaHD() != null ? hd.getMaHD().toLowerCase() : "";
                    String maBan = hd.getBan() != null ? hd.getBan().getMaBan().toLowerCase() : "";
                    String sdt   = (hd.getKhachHang() != null && hd.getKhachHang().getSdt() != null)
                            ? hd.getKhachHang().getSdt()
                            : "";

                    return maHD.startsWith(keyword)
                            || maBan.startsWith(keyword)
                            || sdt.startsWith(keyword);
                })
                .collect(Collectors.toList());

        hienThiDanhSachHoaDon(ketQua);
    }

    private void hienThiDanhSachHoaDon(List<HoaDon> dsHoaDon) {
        vboxHoaDon.getChildren().clear();

        List<KhachHang> dsKH = KhachHangDAO.getAll();
        List<KhuVuc> dsKV = KhuVucDAO.getAll();
        List<ChiTietHoaDon> dsCTAll = ChiTietHDDAO.getAll();

        Map<String, List<ChiTietHoaDon>> mapCT = dsCTAll.stream()
                .collect(Collectors.groupingBy(ct -> ct.getHoaDon().getMaHD()));

        for (HoaDon hd : dsHoaDon) {
            if (hd.getTrangthai() != 1) continue;

            HBox hbox = new HBox(15);
            hbox.setAlignment(Pos.CENTER);
            hbox.getStyleClass().add("invoice-card");

            ImageView imageView = new ImageView(new Image(
                    getClass().getResourceAsStream("/IMG/ban/IN.png")));
            // Ảnh cache static
            imageView.setFitWidth(100);
            imageView.setFitHeight(60);

            Label lblMaHD = new Label(hd.getMaHD());
            lblMaHD.getStyleClass().add("invoice-id");

            KhachHang kh = dsKH.stream()
                    .filter(k -> k.getMaKhachHang().equals(hd.getKhachHang().getMaKhachHang()))
                    .findFirst().orElse(null);

            String tenKH = kh != null ? kh.getTenKhachHang() : "Không rõ";
            String sdtKH = kh != null ? kh.getSdt() : "Không có";

            VBox info = new VBox(lblMaHD, new Label("SĐT: " + sdtKH), new Label("Bàn: " + hd.getBan().getMaBan()));
            Region region = new Region();
            HBox.setHgrow(region, Priority.ALWAYS);

            Button btnTime = new Button("🕒");
            btnTime.getStyleClass().add("time-btn");
            hbox.getChildren().addAll(imageView, info, region, btnTime);

            // Sự kiện click
            hbox.setOnMouseClicked(e -> {
                hdHienTai = hd;
                txtMaGG.clear();
                lblmaHD.setText(hd.getMaHD());
                lbltenKH.setText(tenKH);
                lblsdtKH.setText(sdtKH);
                lblsuKien.setText(hd.getSuKien() != null ? hd.getSuKien().getTenSK() : "Không có");

                KhuVuc kv = dsKV.stream()
                        .filter(k -> k.getMaKhuVuc().equals(hd.getBan().getKhuVuc().getMaKhuVuc()))
                        .findFirst().orElse(null);
                lblKhuVuc.setText(kv != null ? kv.getTenKhuVuc() : "?");

                List<ChiTietHoaDon> dsCT = mapCT.getOrDefault(hd.getMaHD(), new ArrayList<>());

                updateThanhTien();

                new Thread(() -> {
                    Platform.runLater(() -> {
                        vboxMenu.getChildren().clear();
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
                            Label lblPrice = new Label(formatCurrency(ct.getMon().getGiaBanTaiLucLapHD(hd)));
                            lblPrice.getStyleClass().add("col-price"); Label lblDiscount = new Label("0%");
                            lblDiscount.getStyleClass().add("col-discount");
                            Label lblTotal = new Label(formatCurrency(ct.getThanhTien()));
                            lblTotal.getStyleClass().add("col-total");
                            row.getChildren().addAll(lblSTT, lblName, lblQty, lblPrice, lblDiscount, lblTotal);
                            vboxMenu.getChildren().add(row);
                        }
                    });
                }).start();
            });
            vboxHoaDon.getChildren().add(hbox);
        }
    }



}
