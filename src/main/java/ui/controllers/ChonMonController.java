package ui.controllers;

import dao.*;
import entity.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import ui.QRThanhToan;

import java.text.DecimalFormat; // ADDED
import java.text.DecimalFormatSymbols; // ADDED
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static ui.QRThanhToan.hienThiQRPanel;

public class ChonMonController {

    @FXML private FlowPane flowMonAn;
    @FXML private ComboBox<LoaiMon> comboDanhMuc;
    @FXML private VBox vboxChiTietDonHang, vboxTienMat;
    @FXML private Label lbl_total, lbl_thue, lbl_total_PT, lblTienThua;
    @FXML private ToggleGroup paymentGroup;
    @FXML private RadioButton rdoTienMat, rdoChuyenKhoan;
    @FXML private Button back, btndatban, btnGoiY1, btnGoiY2, btnGoiY3, btnGoiY4, btnGoiY5, btnGoiY6;
    @FXML private TextField txtTienKhachDua, sdtKhach, tften;
    @FXML private TextField tf_ban, tftg, tfSLKhach;


    private ui.controllers.MainController_NV mainController;

    private final Map<String, HBox> chiTietMap = new HashMap<>();
    private final Map<String, Integer> soLuongMap = new HashMap<>();

    private final MonDAO monDAO = new MonDAO();
    private final LoaiMonDAO loaiMonDAO = new LoaiMonDAO();

    private Ban banHienTai = null;
    private NhanVien nhanVienHien;
    private LocalDateTime thoiGianDat;
    private int soLuongKhach;
    private Runnable onPaymentConfirmed;


    @FXML
    public void initialize() {
        loadComboDanhMuc();
        loadDanhSachMon();
        xuLyHienThiTienMat();
        rdoChuyenKhoan.setSelected(true);
        back.setOnAction(e -> quayVeDatBan());

        btndatban.setOnAction(e -> {
            if (rdoTienMat.isSelected()) {
                datBan();
            } else {
                double tongTien = parseCurrency(lbl_total_PT.getText().trim());
                String maHD = tuSinhMaHD();

                QRThanhToan.hienThiQRPanel(tongTien, maHD, () -> {
                    System.out.println("Thanh toán chuyển khoản thành công → Tạo hóa đơn...");
                    datBanSauKhiXacNhan(maHD);
                });
            }
        });


        sdtKhach.setOnKeyReleased(e -> timKhachHang());
    }


    public void setOnPaymentConfirmed(Runnable callback) {
        this.onPaymentConfirmed = callback;
    }


    public void setMainController(ui.controllers.MainController_NV controller) {
        this.mainController = controller;
        setNhanVien(mainController.getNhanVien());
    }

    public void setNhanVien(NhanVien nhanVien) {
        this.nhanVienHien = nhanVien;
    }

    public void setThoiGianDat(LocalDateTime thoiGianDat) {
        this.thoiGianDat = thoiGianDat;
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm | dd/MM/yyyy ");
        tftg.setText(thoiGianDat.format(fmt));

    }

    public void setSoLuongKhach(int soLuongKhach) {
        this.soLuongKhach = soLuongKhach;
        tfSLKhach.setText(String.valueOf(soLuongKhach));
    }

    public void setThongTinBan(Ban ban) {
        this.banHienTai = ban;
        tf_ban.setText(ban.getMaBan());
        tf_ban.setEditable(false);
        System.out.println("Đang chọn bàn: " + ban);
    }


    @FXML
    private void quayVeDatBan() {
        if (mainController != null) {
            mainController.setCenterContent("/FXML/DatBan.fxml");
        }
    }


    private void loadComboDanhMuc() {
        comboDanhMuc.getItems().clear();
        comboDanhMuc.getItems().addAll(loaiMonDAO.getAll());

        comboDanhMuc.setCellFactory(param -> new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(LoaiMon item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getTenLoaiMon());
                }
            }
        });

        comboDanhMuc.setButtonCell(new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(LoaiMon item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("Chọn loại món");
                } else {
                    setText(item.getTenLoaiMon());
                }
            }
        });
    }

    private void loadDanhSachMon() {
        flowMonAn.getChildren().clear();
        List<Mon> danhSach = monDAO.getAll();
        System.out.println(danhSach.size());

        for (Mon mon : danhSach) {
            VBox card = taoCardMon(mon);
            flowMonAn.getChildren().add(card);
        }
    }

    private VBox taoCardMon(Mon mon){
        VBox card = new VBox();
        card.getStyleClass().add("menu-card");
        card.setSpacing(8);
        card.setAlignment(javafx.geometry.Pos.CENTER);

        ImageView imageView = new ImageView();
        try {
            imageView.setImage(new Image(getClass().getResourceAsStream("/IMG/food" + mon.getHinhAnh())));
        } catch (Exception e) {
            imageView.setImage(new Image(getClass().getResourceAsStream("/IMG/food/restaurant.png")));
        }
        imageView.setFitWidth(150);
        imageView.setFitHeight(110);
        imageView.getStyleClass().add("food-image");

        Label lblTen = new Label(mon.getTenMon());
        lblTen.getStyleClass().add("menu-item-name");

        Label lblGia = new Label(formatCurrency(mon.getGiaBan()));
        lblGia.getStyleClass().add("menu-item-price");

        Label lblSoLuong = new Label("0");
        lblSoLuong.getStyleClass().add("qty-label");

        Button btnMinus = new Button("-");
        btnMinus.getStyleClass().add("qty-btn_minus");

        Button btnPlus = new Button("+");
        btnPlus.getStyleClass().add("qty-btn_plus");

        HBox soLuongBox = new HBox(btnMinus, lblSoLuong, btnPlus);
        soLuongBox.getStyleClass().add("quantity-controls");

        // ===== SỰ KIỆN =====
        btnPlus.setOnAction(e -> {
            int soLuong = Integer.parseInt(lblSoLuong.getText());
            soLuong++;
            lblSoLuong.setText(String.valueOf(soLuong));

            if (soLuong == 1) {
                addMonToOrder(mon);
                capNhatTongTien();
                xuLyHienThiTienMat();
            } else {
                updateMonSoLuong(mon, soLuong);
                capNhatTongTien();
                xuLyHienThiTienMat();
            }
        });

        btnMinus.setOnAction(e -> {
            int soLuong = Integer.parseInt(lblSoLuong.getText());
            if (soLuong > 0) {
                soLuong--;
                lblSoLuong.setText(String.valueOf(soLuong));

                if (soLuong == 0) {
                    removeMonFromOrder(mon);
                    capNhatTongTien();
                    xuLyHienThiTienMat();
                } else {
                    updateMonSoLuong(mon, soLuong);
                    capNhatTongTien();
                    xuLyHienThiTienMat();
                }
            }
        });

        card.getChildren().addAll(imageView, lblTen, lblGia, soLuongBox);
        return card;
    }

    private void addMonToOrder(Mon mon) {
        String maMon = mon.getMaMon();
        if (chiTietMap.containsKey(maMon)) return;

        HBox dong = taoDongChiTiet(mon, 1);
        chiTietMap.put(maMon, dong);
        soLuongMap.put(maMon, 1);
        vboxChiTietDonHang.getChildren().add(dong);
    }


    private void removeMonFromOrder(Mon mon) {
        String maMon = mon.getMaMon();
        HBox dong = chiTietMap.remove(maMon);
        soLuongMap.remove(maMon);
        if (dong != null) vboxChiTietDonHang.getChildren().remove(dong);
    }


    private void updateMonSoLuong(Mon mon, int soLuong) {
        String maMon = mon.getMaMon();
        soLuongMap.put(maMon, soLuong);

        HBox dong = chiTietMap.get(maMon);
        if (dong != null) {
            Label lblSL = (Label) dong.lookup(".lblSoLuongCT");
            Label lblTongTien = (Label) dong.lookup(".lblTongTienCT");

            if (lblSL != null && lblTongTien != null) {
                lblSL.setText(String.valueOf(soLuong));
                lblTongTien.setText(formatCurrency(mon.getGiaBan() * soLuong));
            }
        }
    }

    private HBox taoDongChiTiet(Mon mon, int soLuong) {
        HBox row = new HBox();
        row.getStyleClass().add("order-row");
        row.setSpacing(10);
        row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        Label lblTen = new Label(mon.getTenMon());
        lblTen.getStyleClass().addAll("order-col", "product");
        lblTen.setPrefWidth(100);

        Label lblSoLuong = new Label(String.valueOf(soLuong));
        lblSoLuong.getStyleClass().addAll("order-col", "quantity", "lblSoLuongCT");
        lblSoLuong.setPrefWidth(60);

        // CHANGED
        Label lblGia = new Label(formatCurrency(mon.getGiaBan()));
        lblGia.getStyleClass().addAll("order-col", "price");
        lblGia.setPrefWidth(70);

        Label lblTongTien = new Label(formatCurrency(mon.getGiaBan() * soLuong));
        lblTongTien.getStyleClass().addAll("order-col", "total", "lblTongTienCT");
        lblTongTien.setPrefWidth(80);

        Region space1 = new Region();
        Region space2 = new Region();
        Region space3 = new Region();
        HBox.setHgrow(space1, javafx.scene.layout.Priority.ALWAYS);
        HBox.setHgrow(space2, javafx.scene.layout.Priority.ALWAYS);
        HBox.setHgrow(space3, javafx.scene.layout.Priority.ALWAYS);

        row.getChildren().addAll(lblTen, space1, lblSoLuong, space2, lblGia, space3, lblTongTien);
        return row;
    }

    private void capNhatTongTien() {
        double tongTien = 0;
        List<Mon> ds = monDAO.getAll();

        for (Map.Entry<String, Integer> entry : soLuongMap.entrySet()) {
            String maMon = entry.getKey();
            int soLuong = entry.getValue();

            Mon mon = ds.stream()
                    .filter(m -> m.getMaMon().equals(maMon))
                    .findFirst()
                    .orElse(null);

            if (mon != null) {
                tongTien += mon.getGiaBan() * soLuong;
            }
        }

        double thue = tongTien * 0.1;
        double tienPT = tongTien + thue;

        lbl_total.setText(formatCurrency(tongTien));
        lbl_thue.setText(formatCurrency(thue));
        lbl_total_PT.setText(formatCurrency(tienPT));
    }



    private String formatCurrency(double amount) {
        Locale localeVN = new Locale("vi", "VN");
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(localeVN);

        DecimalFormat df = new DecimalFormat("#,###", symbols);

        return df.format(amount) + " đ";
    }

    private double parseCurrency(String text) {
        if (text == null || text.isBlank()) return 0;
        String clean = text.replaceAll("[^\\d]", "");
        if (clean.isEmpty()) return 0;
        try {
            return Double.parseDouble(clean);
        } catch (NumberFormatException e) {
            return 0;
        }
    }


    private void xuLyHienThiTienMat() {
        paymentGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            boolean isTienMat = newToggle == rdoTienMat;

            vboxTienMat.setVisible(isTienMat);
            vboxTienMat.setManaged(isTienMat);

            Button[] nutGoiY = {btnGoiY1, btnGoiY2, btnGoiY3, btnGoiY4, btnGoiY5, btnGoiY6};
            for (Button b : nutGoiY) {
                b.setDisable(!isTienMat);
            }

            if (isTienMat) {
                taoGoiYTienKhach();
            }
        });
    }


    private void taoGoiYTienKhach() {
        double tongTien = parseCurrency(lbl_total_PT.getText());;
        if (tongTien <= 0) return;

        double base = Math.round(tongTien / 1000.0) * 1000;
        double[] goiY;

        if (base < 1_000_000) {
            goiY = new double[]{
                    base,
                    Math.ceil(base / 10_000) * 10_000,     // ví dụ: 370.000
                    Math.ceil(base / 50_000) * 50_000,     // 400.000
                    Math.ceil(base / 100_000) * 100_000,   // 400.000 hoặc 500.000
                    500_000,
                    1_000_000
            };
        }
        // Nếu từ 1–5 triệu
        else if (base < 5_000_000) {
            goiY = new double[]{
                    base,
                    Math.ceil(base / 50_000) * 50_000,
                    Math.ceil(base / 100_000) * 100_000,
                    Math.ceil(base / 500_000) * 500_000,
                    5_000_000,
                    10_000_000
            };
        }

        else {
            goiY = new double[]{
                    base,
                    Math.ceil(base / 100_000) * 100_000,
                    Math.ceil(base / 500_000) * 500_000,
                    Math.ceil(base / 1_000_000) * 1_000_000,
                    base + 2_000_000,
                    base + 5_000_000
            };
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
            } else {
                nut[i].setVisible(false);
            }
        }
    }

    private void tinhTienThua(){
        double tong = parseCurrency(lbl_total_PT.getText().trim());
        double tienKD = parseCurrency(txtTienKhachDua.getText().trim());
        lblTienThua.setText(formatCurrency(tienKD - tong));
    }

    private String tuSinhMaHD() {
        int hour = java.time.LocalTime.now().getHour();
        String ca = (hour < 12) ? "0" : "1";

        String datePart = thoiGianDat.format(DateTimeFormatter.ofPattern("ddMMyy"));

        HoaDonDAO hoaDonDAO = new HoaDonDAO();
        String maHDCuoi = hoaDonDAO.getMaHDCuoiTheoNgay(ca, datePart);

        int so = 0;
        if (maHDCuoi != null) {
            String phanSo = maHDCuoi.substring(maHDCuoi.length() - 4);
            so = Integer.parseInt(phanSo);
        }

        return String.format("HD%s%s%04d", ca, datePart, so + 1);
    }



    private HangKhachHang xetHang(int diemTichLuy) {
        List<HangKhachHang> list = new HangKhachDAO().getAll();
        HangKhachHang hang = list.stream()
                .filter(h -> diemTichLuy >= h.getDiemHang())
                .max(Comparator.comparingInt(HangKhachHang::getDiemHang))
                .orElse(list.get(0));
        return hang;
    }

    private void datBan() {
        LocalDateTime now = LocalDateTime.now();

        // ===== Kiểm tra dữ liệu =====
        if (banHienTai == null || soLuongMap.isEmpty()) {
            System.out.println("⚠️ Chưa chọn bàn hoặc món!");
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Thiếu thông tin");
            alert.setHeaderText("Chưa chọn bàn hoặc món ăn!");
            alert.setContentText("Vui lòng kiểm tra lại trước khi đặt bàn.");
            alert.showAndWait();
            return;
        }

        boolean kieuDatBan = now.isBefore(thoiGianDat);
        int trangThai = kieuDatBan ? 0 : 1;

        // ===== Tạo hóa đơn =====
        HoaDon hd = taoHoaDon(kieuDatBan, trangThai);
        if (hd == null) {
            System.out.println("Không tạo được hóa đơn!");
            return;
        }

        // ===== Lưu hóa đơn =====
        HoaDonDAO hdDAO = new HoaDonDAO();
        boolean themHD = hdDAO.insert(hd);
        if (!themHD) {
            System.out.println("Lỗi khi thêm hóa đơn!");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi hệ thống");
            alert.setHeaderText("Không thể lưu hóa đơn!");
            alert.setContentText("Vui lòng thử lại hoặc liên hệ quản trị viên.");
            alert.showAndWait();
            return;
        }

        // ===== Thêm chi tiết hóa đơn =====
        boolean themCT = themChiTietHoaDon(hd);
        if (!themCT) {
            System.out.println("Lỗi khi thêm chi tiết hóa đơn!");
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Cảnh báo");
            alert.setHeaderText("Một số món có thể chưa được lưu!");
            alert.setContentText("Vui lòng kiểm tra lại hóa đơn: " + hd.getMaHD());
            alert.showAndWait();
        }

        // ===== Thông báo thành công =====
        System.out.println("Đặt bàn thành công: " + hd.getMaHD());
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thành công");
        alert.setHeaderText("Đặt bàn & tạo hóa đơn thành công!");
        alert.setContentText("Mã hóa đơn: " + hd.getMaHD() + "\nTổng tiền: " + lbl_total_PT.getText());
        alert.showAndWait();

        // ===== Làm mới giao diện =====
        vboxChiTietDonHang.getChildren().clear();
        chiTietMap.clear();
        soLuongMap.clear();
        lbl_total.setText("0 đ");
        lbl_thue.setText("0 đ");
        lbl_total_PT.setText("0 đ");
        quayVeDatBan();
    }


    private void datBanSauKhiXacNhan(String maHD) {
        System.out.println("Xác nhận thanh toán thành công → Tạo hóa đơn...");

        LocalDateTime now = LocalDateTime.now();

        if (banHienTai == null || soLuongMap.isEmpty()) {
            System.out.println("Chưa chọn bàn hoặc món!");
            return;
        }

        boolean kieuDatBan = now.isBefore(thoiGianDat);
        int trangThai = kieuDatBan ? 0 : 1;

        // ===== Tạo hóa đơn =====
        HoaDon hd = taoHoaDon(kieuDatBan, trangThai);
        if (hd == null) {
            System.out.println("Không tạo được hóa đơn!");
            return;
        }

        hd.setMaHD(maHD);

        HoaDonDAO hdDAO = new HoaDonDAO();
        boolean themHD = hdDAO.insert(hd);
        if (!themHD) {
            System.out.println("Lỗi khi thêm hóa đơn!");
            return;
        }

        boolean themCT = themChiTietHoaDon(hd);
        if (!themCT) {
            System.out.println("Lỗi khi thêm chi tiết hóa đơn!");
        } else {
            System.out.println("Đặt bàn thành công: " + hd.getMaHD());
        }

        String sdt = sdtKhach.getText().trim();
        KhachHang kh = new KhachHangDAO().findBySDT(sdt);
        if (kh != null) {
            congDiemTichLuy(kh, tinhDiem());
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thanh toán thành công");
        alert.setHeaderText("Đặt bàn & tạo hóa đơn thành công!");
        alert.setContentText("Mã hóa đơn: " + hd.getMaHD());
        alert.showAndWait();

        vboxChiTietDonHang.getChildren().clear();
        chiTietMap.clear();
        soLuongMap.clear();
        lbl_total.setText("0 đ");
        lbl_thue.setText("0 đ");
        lbl_total_PT.setText("0 đ");
        quayVeDatBan();
    }



    private void congDiemTichLuy(KhachHang khachHang, int diem) {
        if (khachHang == null) return;
        khachHang.setDiemTichLuy(khachHang.getDiemTichLuy() + diem);
        KhachHangDAO.update(khachHang);
    }

    private int tinhDiem() {
        double tongTien = parseCurrency(lbl_total_PT.getText().trim());
        return (int) (tongTien * 0.1 / 1000);
    }

    private Coc layCoc(Ban ban) {
        if (ban == null) return null;

        String maKV = ban.getKhuVuc().getMaKhuVuc();
        String maLB = ban.getLoaiBan().getMaLoaiBan();

        List<Coc> listCoc = new CocDAO().getAll();

        for (Coc coc : listCoc) {
            if (coc.getKhuVuc() != null && coc.getLoaiBan() != null) {
                if (coc.getKhuVuc().getMaKhuVuc().equals(maKV)
                        && coc.getLoaiBan().getMaLoaiBan().equals(maLB)) {
                    return coc;
                }
            }
        }

        return null;
    }

    private boolean themChiTietHoaDon(HoaDon hoaDon) {
        if (hoaDon == null) {
            System.out.println("Không có hóa đơn để thêm chi tiết!");
            return false;
        }

        String maHD = hoaDon.getMaHD();
        ChiTietHDDAO dao = new ChiTietHDDAO();
        List<Mon> dsMon = monDAO.getAll();

        boolean tatCaOK = true;

        for (Map.Entry<String, Integer> entry : soLuongMap.entrySet()) {
            String maMon = entry.getKey();
            int soLuong = entry.getValue();

            Mon mon = dsMon.stream()
                    .filter(m -> m.getMaMon().equals(maMon))
                    .findFirst()
                    .orElse(null);

            if (mon != null && soLuong > 0) {
                double thanhTien = mon.getGiaBan() * soLuong;
                ChiTietHoaDon ct = new ChiTietHoaDon(hoaDon, mon, soLuong);

                if (!dao.insert(ct)) {
                    tatCaOK = false;
                    System.err.println("Lỗi thêm chi tiết món: " + maMon);
                }
            }
        }

        return tatCaOK;
    }


    private HoaDon taoHoaDon(boolean kieudatban, int trangthai) {
        if (banHienTai == null || nhanVienHien == null) {
            System.out.println("Thiếu thông tin bàn hoặc nhân viên!");
            return null;
        }

        // ===== 1. Tạo mã hóa đơn =====
        String maHD = tuSinhMaHD();

        // ===== 2. Lấy thông tin khách hàng =====
        KhachHang khachHang = null;
        String sdt = sdtKhach.getText().trim();

        if (!sdt.isEmpty()) {
            khachHang = new KhachHangDAO().findBySDT(sdt);
            if (khachHang != null) {
                congDiemTichLuy(khachHang, tinhDiem());
            } else {
                System.out.println("Không tìm thấy khách hàng có SDT: " + sdt);
            }
        }

        if (khachHang == null) {
            khachHang = new KhachHang("KH0000", 0, true, sdt, "Khách lẻ", xetHang(0));
        }
        // ===== 3. Tính toán giá trị dẫn xuất =====
        double tongTienTruoc = parseCurrency(lbl_total_PT.getText().trim());
        double thue = tongTienTruoc * 0.1;
        double tongSauThue = tongTienTruoc + thue;

        KhuyenMai km = null; // tạm null
        double tongKM = 0;
        SuKien suKien = null;
        double tongSuKM = 0;
        if(suKien!=null){
            tongSuKM = suKien.getGia();
        }
        double tongtienSau = tongSauThue - tongKM + tongSuKM ;

        double tienCoc = 0;
        Coc coc = layCoc(banHienTai);
        if (coc.isLoaiCoc()) {
            tienCoc = tongtienSau * coc.getPhanTramCoc();
        } else{
            tienCoc = coc.getSoTienCoc();
        }

        boolean kieuThanhToan = rdoChuyenKhoan.isSelected();

        // ===== 4. Khởi tạo đối tượng hóa đơn =====//
        HoaDon hd = new HoaDon();
        hd.setMaHD(maHD);
        hd.setKhachHang(khachHang);
        hd.setNhanVien(nhanVienHien);
        hd.setBan(banHienTai);
        hd.setTgCheckIn(LocalDateTime.now());
        hd.setTgCheckOut(null);
        hd.setKhuyenMai(km);
        hd.setTrangthai(trangthai);
        hd.setSuKien(suKien);
        hd.setKieuThanhToan(kieuThanhToan);
        hd.setKieuDatBan(kieudatban);
        hd.setThue(thue);
        hd.setCoc(tienCoc);
        hd.setTongTienTruoc(tongTienTruoc);
        hd.setTongTienKhuyenMai(tongKM);
        hd.setTongTienSau(tongtienSau);

        System.out.println("Tạo hóa đơn thành công: " + hd.getMaHD());
        return hd;
    }

    private void timKhachHang() {
        String sdt = sdtKhach.getText().trim();
        if (sdt.isEmpty()) {
            tften.clear();
            return;
        }

        KhachHang kh = new KhachHangDAO().findBySDT(sdt);
        if (kh != null) {
            tften.setText(kh.getTenKhachHang());
        } else {
            tften.setText("Khách lẻ");
        }
    }


}