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
import ui.AlertCus;
import ui.ConfirmCus;
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
    @FXML private ComboBox<SuKien> comboSuKien;
    @FXML private VBox vboxChiTietDonHang, vboxTienMat;
    @FXML private Label lbl_total, lblTienThua, lblCoc, lblConLai;
    @FXML private ToggleGroup paymentGroup;
    @FXML private RadioButton rdoTienMat, rdoChuyenKhoan;
    @FXML private Button back, btndatban, btnGoiY1, btnGoiY2, btnGoiY3, btnGoiY4, btnGoiY5, btnGoiY6;
    @FXML private TextField txtTienKhachDua, sdtKhach, tften, tfTimKiem;
    @FXML private TextField tf_ban, tftg, tfSLKhach, tfghichu;

    private boolean kieudatban;

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
    private SuKien sk;


    @FXML
    public void initialize() {
        loadComboDanhMuc();
        loadComboSuKien();
        loadDanhSachMon();
        xuLyHienThiTienMat();
        rdoChuyenKhoan.setSelected(true);
        back.setOnAction(e -> quayVeDatBan());

        tfTimKiem.textProperty().addListener((obs, oldVal, newVal) -> {
            locMonTheoTenVaLoai();
        });
        comboDanhMuc.setOnAction(e -> locMonTheoTenVaLoai());


        btndatban.setOnAction(e -> {
            if (rdoTienMat.isSelected()) {
                datBan();
            } else {
                double tongTien = parseCurrency(lblCoc.getText().trim());
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

    public void setSdtKhach(String sdt) {
        sdtKhach.setText(sdt);
    }

    public void setTen(String ten) {
        tften.setText(ten);
    }

    public void setKieudatban(boolean kieudatban) {
        this.kieudatban = kieudatban;
    }

    public void setThongTinBan(Ban ban) {
        this.banHienTai = ban;
        tf_ban.setText(ban.getMaBan());
        tf_ban.setEditable(false);
        System.out.println("Đang chọn bàn: " + ban);
    }

    private void locMonTheoTenVaLoai() {
        flowMonAn.getChildren().clear();

        String tuKhoa = tfTimKiem.getText().trim().toLowerCase();
        LoaiMon loaiDuocChon = comboDanhMuc.getValue();

        List<Mon> danhSach = monDAO.getAll();

        for (Mon mon : danhSach) {
            boolean hopTen = tuKhoa.isEmpty() || mon.getTenMon().toLowerCase().contains(tuKhoa);
            boolean hopLoai = (loaiDuocChon == null
                    || loaiDuocChon.getMaLoaiMon().equals("ALL")
                    || mon.getLoaiMon().getMaLoaiMon().equals(loaiDuocChon.getMaLoaiMon()));

            if (hopTen && hopLoai) {
                flowMonAn.getChildren().add(taoCardMon(mon));
            }
        }
    }

    @FXML
    private void quayVeDatBan() {
        if (mainController != null) {
            boolean xacNhan = ConfirmCus.show(
                    "Xác nhận hủy bàn đợi",
                    "Khách hàng không đặt nữa. Bạn có muốn xóa bàn chờ này không?"
            );

            if (xacNhan) {
                try {
                    Ban ban = banHienTai;
                    if (ban != null && ban.getMaBan().startsWith("W")) {
                        BanDAO banDAO = new BanDAO();
                        if (banDAO.delete(ban.getMaBan())) {
                            System.out.println("Đã xóa bàn đợi: " + ban.getMaBan());
                        } else {
                            System.err.println("Lỗi khi xóa bàn đợi khỏi DB!");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    ConfirmCus.show("Lỗi hệ thống", "Không thể xóa bàn đợi: " + e.getMessage());
                }

                mainController.setCenterContent("/FXML/DatBan.fxml");
            } else {
                System.out.println("Người dùng hủy thao tác quay lại, vẫn ở màn chọn món.");
            }
        }
    }

    private void loadComboDanhMuc() {
        comboDanhMuc.getItems().clear();

        LoaiMon tatCa = new LoaiMon("ALL", "Tất cả món","Tat ca");
        comboDanhMuc.getItems().add(tatCa);

        comboDanhMuc.getItems().addAll(loaiMonDAO.getAll());

        comboDanhMuc.setCellFactory(param -> new ListCell<>() {
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

        // Hiển thị giá trị được chọn
        comboDanhMuc.setButtonCell(new ListCell<>() {
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

        // Mặc định chọn "Tất cả món"
        comboDanhMuc.getSelectionModel().selectFirst();

        // Khi chọn loại → lọc lại danh sách
        comboDanhMuc.setOnAction(e -> locMonTheoTenVaLoai());
    }

    private void loadComboSuKien() {
        comboSuKien.getItems().clear();

        // Thêm "Không áp dụng"
        SuKien khongApDung = new SuKien("NONE", "Không áp dụng", "", 0);
        comboSuKien.getItems().add(khongApDung);

        comboSuKien.getItems().addAll(SuKienDAO.getAll());

        comboSuKien.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(SuKien item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getTenSK());
                }
            }
        });

        comboSuKien.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(SuKien item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("Chọn sự kiện");
                } else {
                    setText(item.getTenSK());
                }
            }
        });

        comboSuKien.getSelectionModel().selectFirst();

        comboSuKien.setOnAction(e -> {
            SuKien selected = comboSuKien.getValue();
            if (selected != null && !"NONE".equals(selected.getMaSK())) {
                sk = selected;
                System.out.println("Đã chọn sự kiện: " + sk.getTenSK() + " (+ " + sk.getGia() + " đ)");
            } else {
                sk = null;
                System.out.println("Không áp dụng sự kiện");
            }

            capNhatTongTien();
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
                loadTT();
            } else {
                updateMonSoLuong(mon, soLuong);
                loadTT();
            }
        });

        btnMinus.setOnAction(e -> {
            int soLuong = Integer.parseInt(lblSoLuong.getText());
            if (soLuong > 0) {
                soLuong--;
                lblSoLuong.setText(String.valueOf(soLuong));

                if (soLuong == 0) {
                    removeMonFromOrder(mon);
                    loadTT();

                } else {
                    updateMonSoLuong(mon, soLuong);
                    loadTT();
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

    private void loadTT(){
        capNhatTongTien();
        tinhCoc();
        xuLyHienThiTienMat();
        taoGoiYTienKhach();
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

        if (sk != null) {
            tongTien += sk.getGia();
        }

        lbl_total.setText(formatCurrency(tongTien));

        tinhCoc();
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
        double tongTien = parseCurrency(lblCoc.getText());;
        if (tongTien <= 0) return;

        double base = Math.round(tongTien / 1000.0) * 1000;
        double[] goiY;

        if (base < 1_000_000) {
            goiY = new double[]{
                    base,
                    Math.ceil(base / 10_000) * 10_000,
                    Math.ceil(base / 50_000) * 50_000,
                    Math.ceil(base / 100_000) * 100_000,
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
        double coc = parseCurrency(lblCoc.getText().trim());
        double tien = parseCurrency(txtTienKhachDua.getText().trim());
        lblTienThua.setText(formatCurrency(tien - coc));
    }

    private void tinhCoc(){
        Coc coc = CocDAO.getByKhuVucVaLoaiBan(banHienTai.getKhuVuc().getMaKhuVuc(),banHienTai.getLoaiBan().getMaLoaiBan());
        double tong = parseCurrency(lbl_total.getText().trim());
        double tienCoc = 0;
        if (coc.isLoaiCoc()) {
            tienCoc = tong * coc.getPhanTramCoc() / 100;
        } else{
            tienCoc = coc.getSoTienCoc();
        }
        lblCoc.setText(formatCurrency(tienCoc));
        lblConLai.setText(formatCurrency(tong-tienCoc));
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
            AlertCus.show("Thiếu thông tin", "Chưa chọn bàn hoặc món ăn!\nVui lòng kiểm tra lại trước khi đặt bàn.");
            return;
        }

        long phutCachNhau = java.time.Duration.between(now, thoiGianDat).toMinutes();
        boolean kieuDatBan = !(phutCachNhau >= 0 && phutCachNhau <= 15);

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
            AlertCus.show("Lỗi hệ thống", "Không thể lưu hóa đơn!\nVui lòng thử lại hoặc liên hệ quản trị viên.");
            return;
        }


        // ===== Thêm chi tiết hóa đơn =====
        boolean themCT = themChiTietHoaDon(hd);
        if (!themCT) {
            System.out.println("Lỗi khi thêm chi tiết hóa đơn!");
            AlertCus.show("Cảnh báo", "Một số món có thể chưa được lưu!\nVui lòng kiểm tra lại hóa đơn: " + hd.getMaHD());
        }


        // ===== Thông báo thành công =====
        System.out.println("Đặt bàn thành công: " + hd.getMaHD());
        AlertCus.show("Thành công", "Đặt bàn & tạo hóa đơn thành công!\nMã hóa đơn: " + hd.getMaHD() + "\nTổng tiền: " + lbl_total.getText());


        // ===== Làm mới giao diện =====
        vboxChiTietDonHang.getChildren().clear();
        chiTietMap.clear();
        soLuongMap.clear();
        lbl_total.setText("0 đ");
        BanDAO.update(banHienTai,true);
        quayVeDatBan();
    }


    private void datBanSauKhiXacNhan(String maHD) {
        System.out.println("Xác nhận thanh toán thành công → Tạo hóa đơn...");

        LocalDateTime now = LocalDateTime.now();

        if (banHienTai == null || soLuongMap.isEmpty()) {
            System.out.println("Chưa chọn bàn hoặc món!");
            AlertCus.show("Thiếu thông tin", "Chưa chọn bàn hoặc món ăn!\nVui lòng kiểm tra lại trước khi đặt bàn.");
            return;
        }

        long phutCachNhau = java.time.Duration.between(now, thoiGianDat).toMinutes();
        boolean kieuDatBan = !(phutCachNhau >= 0 && phutCachNhau <= 15);

        int trangThai = kieuDatBan ? 0 : 1;

        // ===== Tạo hóa đơn =====
        HoaDon hd = taoHoaDon(kieuDatBan, trangThai);
        if (hd == null) {
            System.out.println("Không tạo được hóa đơn!");
            AlertCus.show("Lỗi hệ thống", "Không thể tạo hóa đơn mới!\nVui lòng thử lại hoặc liên hệ quản trị viên.");
            return;
        }

        hd.setMaHD(maHD);

        HoaDonDAO hdDAO = new HoaDonDAO();
        boolean themHD = hdDAO.insert(hd);
        if (!themHD) {
            System.out.println("Lỗi khi thêm hóa đơn!");
            AlertCus.show("Lỗi hệ thống", "Không thể lưu hóa đơn!\nVui lòng thử lại hoặc liên hệ quản trị viên.");
            return;
        }

        boolean themCT = themChiTietHoaDon(hd);
        if (!themCT) {
            System.out.println("Lỗi khi thêm chi tiết hóa đơn!");
            AlertCus.show("Cảnh báo", "Một số món có thể chưa được lưu!\nVui lòng kiểm tra lại hóa đơn: " + hd.getMaHD());
        } else {
            System.out.println("Đặt bàn thành công: " + hd.getMaHD());
            AlertCus.show("Thành công", "Đặt bàn & tạo hóa đơn thành công!\nMã hóa đơn: " + hd.getMaHD());
        }

        String sdt = sdtKhach.getText().trim();
        KhachHang kh = new KhachHangDAO().findBySDT(sdt);

        vboxChiTietDonHang.getChildren().clear();
        chiTietMap.clear();
        soLuongMap.clear();
        lbl_total.setText("0 đ");
        BanDAO.update(banHienTai,true);
        quayVeDatBan();
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
        }



        if (khachHang == null) {
            khachHang = new KhachHang("KH0000", 0, true, sdt, "Khách lẻ", xetHang(0));
        }
        // ===== 3. Tính toán giá trị dẫn xuất =====

        KhuyenMai km = null;

        boolean kieuThanhToan = rdoChuyenKhoan.isSelected();

        // ===== 4. Khởi tạo đối tượng hóa đơn =====//
        HoaDon hd = new HoaDon();
        hd.setMaHD(maHD);
        hd.setKhachHang(khachHang);
        hd.setNhanVien(nhanVienHien);
        hd.setBan(banHienTai);
        if (banHienTai.getMaBan().startsWith("W")) {
            hd.setTgCheckIn(LocalDateTime.now());
        } else {
            hd.setTgCheckIn(LocalDateTime.now());
        }
        hd.setSoLuong(soLuongKhach);
        hd.setTgCheckOut(null);
        hd.setKhuyenMai(km);
        if (banHienTai.getMaBan().startsWith("W")) {
            hd.setTrangthai(0);
        } else {
            hd.setTrangthai(trangthai);
        }
        hd.setSuKien(sk);
        hd.setKieuThanhToan(kieuThanhToan);
        hd.setKieuDatBan(kieudatban);
        hd.setMoTa(tfghichu.getText().trim());

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