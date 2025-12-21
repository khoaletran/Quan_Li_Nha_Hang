package ui.controllers;

import connectDB.connectDB;
import dao.*;
import entity.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ui.AlertCus;
import ui.ConfirmCus;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.text.NumberFormat;
import java.util.Locale;

public class QLDatBanController {

    //danh sách
    @FXML private VBox danhSachDatTruoc, vboxChiTietDonHang;
    @FXML private VBox danhSachDaNhan;
    @FXML private FlowPane foodList;

    //thông tin chi tiết
    @FXML private Label lblMaHoaDon;
    @FXML private Label lblHoTen;
    @FXML private Label lblSDT;
    @FXML private Label lblBan;
    @FXML private TextField txtSoLuongKhach;
    @FXML private ComboBox<String> eventCombo;

    //tìm kiếm

    @FXML
    private DatePicker dpNgay;
    @FXML private ComboBox<Integer> cbGio;

    //nút
    @FXML private Button btnXacNhan;
    @FXML private Button btnHuyBan;

    // center
    @FXML private VBox paneDanhSach;   // VBox danh sách bàn
    @FXML private VBox paneMenu;       // VBox menu món

    @FXML private Button back;
    @FXML private TextField tfTimKiem;
    @FXML private ComboBox<LoaiMon> comboDanhMuc;
    @FXML private FlowPane flowMonAn;
    private final LoaiMonDAO loaiMonDAO = new LoaiMonDAO();

    // BIẾN TOÀN CỤC
    private final ChiTietHDDAO chiTietHDDAO = new ChiTietHDDAO();
    private final MonDAO monDAO = new MonDAO();

    private List<HoaDon> dsDatTruoc = new ArrayList<>();
    private List<HoaDon> dsDaNhan = new ArrayList<>();
    private HoaDon hoaDonSelected = null;
    private ObservableList<ChiTietHoaDon> chiTietHoaDonData = FXCollections.observableArrayList();

    // số lượng gốc khi load từ DB, khóa theo maMon
    private final Map<String, Integer> soLuongGocMap = new HashMap<>();

    // danh sách món toàn bộ món để tìm kiếm/hiển thị
    private List<Mon> dsMonToanBo = new ArrayList<>();

    // CACHE: card món + ảnh
    private final Map<String, VBox> menuCardCache = new HashMap<>();
    private final Map<String, Image> imageCache = new HashMap<>();

    private final NumberFormat nf = NumberFormat.getInstance(new Locale("vi","VN"));

    @FXML
    public void initialize() {
        System.out.println("QLDatBanController initialized");
        txtSoLuongKhach.setEditable(false);

        khoiTaoComboBox();
        ganSuKienChoNut();
        taiDanhSachDatTruoc();
        taiDanhSachDaNhan();
        khoiTaoChonMon();   // load ds món, combo loại, search, cache card
        resetForm();
        showDanhSachMode();
        if (back != null) {
            back.setOnAction(e -> showDanhSachMode());
        }
        initDatePicker();
        initComboGio();
    }

    private void khoiTaoComboBox() {
        if (eventCombo == null) return;
        List<SuKien> dsSuKien = SuKienDAO.getAll();
        eventCombo.getItems().clear();
        for (SuKien sk : dsSuKien) {
            eventCombo.getItems().add(sk.getTenSK());
        }

        eventCombo.setValue(null);
    }

    private void initDatePicker() {
        dpNgay.setValue(LocalDate.now());

        dpNgay.setOnAction(e -> locTheoNgayGio());
    }

//    private void initComboGio() {
//        cbGio.getItems().clear();
//
//        for (int h = 0; h <= 23; h++) {
//            cbGio.getItems().add(h);
//        }
//
//        cbGio.setPromptText("Giờ");
//
//        // Có thể bỏ nếu không muốn mặc định
//        cbGio.getSelectionModel().selectFirst();
//
//        cbGio.setOnAction(e -> locTheoNgayGio());
//    }
private void initComboGio() {
    cbGio.getItems().clear();

    for (int h = 0; h <= 23; h++) {
        cbGio.getItems().add(h);
    }

    cbGio.setPromptText("Giờ");

    // ===== Hiển thị: "Giờ: X" =====
    cbGio.setCellFactory(param -> new ListCell<>() {
        @Override
        protected void updateItem(Integer item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setText(null);
            } else {
                setText(item + " giờ");
            }
        }
    });

    cbGio.setButtonCell(new ListCell<>() {
        @Override
        protected void updateItem(Integer item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setText("Giờ");
            } else {
                setText(item + " giờ");
            }
        }
    });

    // Không chọn mặc định để cho phép lọc chỉ theo ngày
    cbGio.getSelectionModel().clearSelection();

    cbGio.setOnAction(e -> locTheoNgayGio());
}


    private void locTheoNgayGio() {

        LocalDate ngay = dpNgay.getValue();
        Integer gio = cbGio.getValue();

        // chưa chọn ngày thì không lọc
        if (ngay == null) {
            hienThiDanhSachDatTruoc();
            hienThiDanhSachDaNhan();
            return;
        }

        LocalDateTime start;
        LocalDateTime end;

        if (gio == null) {
            // ===== CHỈ CHỌN NGÀY =====
            start = ngay.atStartOfDay();          // 00:00:00
            end   = ngay.atTime(23, 59, 59);      // 23:59:59
        } else {
            // ===== CHỌN NGÀY + GIỜ =====
            start = ngay.atTime(gio, 0, 0);
            end   = ngay.atTime(gio, 59, 59);
        }

        // ===== LỌC ĐẶT TRƯỚC =====
        danhSachDatTruoc.getChildren().clear();
        for (HoaDon hd : dsDatTruoc) {
            LocalDateTime tg = hd.getTgLapHD();
            if (tg != null && !tg.isBefore(start) && !tg.isAfter(end)) {
                danhSachDatTruoc.getChildren().add(taoCardHoaDon(hd));
            }
        }

        // ===== LỌC ĐÃ NHẬN =====
        danhSachDaNhan.getChildren().clear();
        for (HoaDon hd : dsDaNhan) {
            LocalDateTime tg = hd.getTgLapHD();
            if (tg != null && !tg.isBefore(start) && !tg.isAfter(end)) {
                danhSachDaNhan.getChildren().add(taoCardHoaDon(hd));
            }
        }
    }

    private void ganSuKienChoNut() {
        if (btnXacNhan != null) btnXacNhan.setOnAction(e -> xacNhanDatBan());
        if (btnHuyBan != null) btnHuyBan.setOnAction(e -> huyDatBan());
    }
    //tải danh sách đặt trước / đã nhận
    private void taiDanhSachDatTruoc() {
        try {
            // CHỈ LẤY TRANGTHAI = 0 TỪ DB
            List<HoaDon> listHD = HoaDonDAO.getAllDatTruoc();
            dsDatTruoc.clear();
            if (listHD != null) {
                dsDatTruoc.addAll(listHD);
            }
            hienThiDanhSachDatTruoc();
        } catch (Exception ex) {
            System.err.println("Lỗi khi tải ds đặt trước: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void taiDanhSachDaNhan() {
        try {
            List<HoaDon> listHD = HoaDonDAO.getAllDaNhan();
            dsDaNhan.clear();
            if (listHD != null) {
                dsDaNhan.addAll(listHD);
            }
            hienThiDanhSachDaNhan();
        } catch (Exception ex) {
            System.err.println("Lỗi khi tải ds đã nhận: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void hienThiDanhSachDatTruoc() {
        if (danhSachDatTruoc == null) return;
        danhSachDatTruoc.getChildren().clear();
        if (dsDatTruoc.isEmpty()) {
            Label empty = new Label("Không có bàn nào đặt trước");
            empty.getStyleClass().add("empty-state");
            empty.setStyle("-fx-text-fill: #666; -fx-font-style: italic; -fx-padding: 20px;");
            danhSachDatTruoc.getChildren().add(empty);
            return;
        }
        for (HoaDon hd : dsDatTruoc) {
            HBox card = taoCardHoaDon(hd);
            danhSachDatTruoc.getChildren().add(card);
        }
    }

    private void hienThiDanhSachDaNhan() {
        if (danhSachDaNhan == null) return;
        danhSachDaNhan.getChildren().clear();
        if (dsDaNhan.isEmpty()) {
            Label empty = new Label("Không có bàn nào đã nhận");
            empty.getStyleClass().add("empty-state");
            empty.setStyle("-fx-text-fill: #666; -fx-font-style: italic; -fx-padding: 20px;");
            danhSachDaNhan.getChildren().add(empty);
            return;
        }
        for (HoaDon hd : dsDaNhan) {
            HBox card = taoCardHoaDon(hd);
            danhSachDaNhan.getChildren().add(card);
        }
    }

    private HBox taoCardHoaDon(HoaDon hd) {
        HBox card = new HBox(10);
        card.getStyleClass().add("invoice-card");
        card.setPadding(new Insets(8));
        card.setCursor(Cursor.HAND);
        card.setPrefHeight(80);
        card.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #dee2e6; -fx-border-radius: 8; -fx-background-radius: 8;");

        StackPane thumb = new StackPane();
        thumb.setStyle("-fx-background-radius: 8; -fx-overflow: hidden;");
        ImageView iv = new ImageView();
        iv.setFitWidth(80);
        iv.setFitHeight(60);
        iv.setPreserveRatio(true);
        try {
            Image img = new Image(getClass().getResourceAsStream("/IMG/ban/vip.png"));
            iv.setImage(img);
        } catch (Exception e) {
            thumb.setStyle("-fx-background-color: #e9ecef; -fx-background-radius: 8;");
            System.out.println("Không load được ảnh bàn: " + e.getMessage());
        }
        thumb.getChildren().add(iv);

        VBox info = new VBox(4);
        Label lblMa = new Label(hd.getMaHD());
        lblMa.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #333;");
        lblMa.setFont(Font.font(14));

        String sdt = "";
        if (hd.getKhachHang() != null && hd.getKhachHang().getSdt() != null) {
            sdt = hd.getKhachHang().getSdt();
        }
        Label lblPhone = new Label("SĐT: " + sdt);
        lblPhone.setStyle("-fx-text-fill: #666; -fx-font-size: 12px;");

        String tenKH = "";
        if (hd.getKhachHang() != null && hd.getKhachHang().getTenKhachHang() != null) {
            tenKH = hd.getKhachHang().getTenKhachHang();
        }
        Label lblTen = new Label("Tên: " + tenKH);
        lblTen.setStyle("-fx-text-fill: #666; -fx-font-size: 12px;");

        Label lblSoLuong = new Label("Số lượng: " + hd.getSoLuong());
        lblSoLuong.setStyle("-fx-text-fill: #666; -fx-font-size: 12px;");

        info.getChildren().addAll(lblMa, lblTen, lblPhone, lblSoLuong);
        HBox.setHgrow(info, Priority.ALWAYS);

        Label lblTrangThai = new Label(hd.getTrangthai() == 0 ? "Đặt trước" : "Đã nhận");
        lblTrangThai.setStyle(hd.getTrangthai() == 0 ?
                "-fx-text-fill: #e74c3c; -fx-font-weight: bold;" :
                "-fx-text-fill: #27ae60; -fx-font-weight: bold;");

        card.getChildren().addAll(thumb, info, lblTrangThai);

        card.setOnMouseClicked(e -> {
            clearSelectedStyles(danhSachDatTruoc);
            clearSelectedStyles(danhSachDaNhan);

            card.setStyle("-fx-background-color: #007bff; -fx-border-color: #0056b3; -fx-border-radius: 8; -fx-background-radius: 8;");

            for (javafx.scene.Node node : card.getChildren()) {
                if (node instanceof Label) {
                    ((Label) node).setStyle("-fx-text-fill: white;");
                }
            }
            hoaDonSelected = hd;
            hienThiThongTinChiTiet(hd);

            showMenuMode();
        });
        return card;
    }

    private void clearSelectedStyles(VBox box) {
        if (box == null) return;
        for (javafx.scene.Node node : box.getChildren()) {
            if (node instanceof HBox) {
                node.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #dee2e6; -fx-border-radius: 8; -fx-background-radius: 8;");
                for (javafx.scene.Node child : ((HBox) node).getChildren()) {
                    if (child instanceof Label) {
                        if (((Label) child).getText().contains("Đặt trước")) {
                            ((Label) child).setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                        } else if (((Label) child).getText().contains("Đã nhận")) {
                            ((Label) child).setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                        } else if (((Label) child).getText().startsWith("HD")) {
                            ((Label) child).setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #333;");
                        } else {
                            ((Label) child).setStyle("-fx-text-fill: #666; -fx-font-size: 12px;");
                        }
                    }
                }
            }
        }
    }

    private void hienThiThongTinChiTiet(HoaDon hd) {
        if (hd == null) return;
        System.out.println("Hiển thị chi tiết hóa đơn: " + hd.getMaHD());
        if (lblMaHoaDon != null) lblMaHoaDon.setText(hd.getMaHD());
        KhachHang kh = hd.getKhachHang();
        if (kh != null) {
            if (lblHoTen != null) lblHoTen.setText(kh.getTenKhachHang());
            if (lblSDT != null) lblSDT.setText(kh.getSdt());
        } else {
            if (lblHoTen != null) lblHoTen.setText("Chưa có thông tin");
            if (lblSDT != null) lblSDT.setText("Chưa có thông tin");
            System.out.println("Không có thông tin khách hàng");
        }
        if (hd.getBan() != null && lblBan != null) {
            lblBan.setText(hd.getBan().getMaBan());
        } else if (lblBan != null) {
            lblBan.setText("Chưa có thông tin");
        }

        if (eventCombo != null && hd.getSuKien() != null) {
            eventCombo.setValue(hd.getSuKien().getTenSK());
        } else if (eventCombo != null) {
            eventCombo.setValue(null);
        }

        if (txtSoLuongKhach != null) {
            txtSoLuongKhach.setText(String.valueOf(hd.getSoLuong()));
        }

        loadChiTietDonHang(hd.getMaHD());
    }

    private void loadChiTietDonHang(String maHD) {
        chiTietHoaDonData.clear();
        soLuongGocMap.clear();
        vboxChiTietDonHang.getChildren().clear();

        if (maHD == null || maHD.trim().isEmpty()) return;

        try {
            List<ChiTietHoaDon> dsChiTiet = chiTietHDDAO.getByMaHD(maHD);
            if (dsChiTiet != null && !dsChiTiet.isEmpty()) {

                for (ChiTietHoaDon ct : dsChiTiet) {
                    chiTietHoaDonData.add(ct);
                    if (ct.getMon() != null && ct.getMon().getMaMon() != null) {
                        soLuongGocMap.put(ct.getMon().getMaMon(), ct.getSoLuong());
                    }
                }
                capNhatUIChiTiet();
                System.out.println("Đã tải " + chiTietHoaDonData.size() + " chi tiết hóa đơn");
            } else {
                System.out.println("Không có chi tiết hóa đơn cho mã: " + maHD);
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi tải chi tiết hóa đơn: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void capNhatUIChiTiet() {
        vboxChiTietDonHang.getChildren().clear();
        int stt = 1;
        for (ChiTietHoaDon ct : chiTietHoaDonData) {
            HBox row = taoDongChiTiet(ct, stt++);
            vboxChiTietDonHang.getChildren().add(row);
        }
    }

    @FXML
    private void xacNhanDatBan() {
        if (hoaDonSelected == null) {
            AlertCus.show("Thông Báo", "Vui lòng chọn hóa đơn để xác nhận thay đổi");
            return;
        }
        try {
            System.out.println("Xác nhận cập nhật chi tiết hóa đơn: " + hoaDonSelected.getMaHD());
            // lấy danh sách chi tiết cũ từ DB
            List<ChiTietHoaDon> dsChiTietCu = chiTietHDDAO.getByMaHD(hoaDonSelected.getMaHD());
            // xóa những món không còn trong UI
            for (ChiTietHoaDon ctCu : dsChiTietCu) {
                boolean stillExists = false;
                for (ChiTietHoaDon ctUI : chiTietHoaDonData) {
                    if (ctUI.getMon() != null && ctCu.getMon() != null &&
                            ctUI.getMon().getMaMon().equals(ctCu.getMon().getMaMon())) {
                        stillExists = true;
                        break;
                    }
                }
                if (!stillExists) {
                    chiTietHDDAO.delete(ctCu.getHoaDon().getMaHD(), ctCu.getMon().getMaMon());
                }
            }
            boolean allOk = true;
            for (ChiTietHoaDon ct : chiTietHoaDonData) {
                boolean ok = chiTietHDDAO.update(ct);
                if (!ok) ok = chiTietHDDAO.insert(ct);
                if (!ok) allOk = false;
            }
            if (allOk) {
                AlertCus.show("Thông Báo", "Cập nhật chi tiết hóa đơn thành công");
                capNhatBangDonHang();
            } else {
                AlertCus.show("Thông Báo", "Có lỗi khi cập nhật chi tiết hóa đơn");
            }

        } catch (Exception e) {
            e.printStackTrace();
            AlertCus.show("Thông Báo", "Lỗi hệ thống: " + e.getMessage());
        }
    }

    private void huyDatBan() {
        if (hoaDonSelected == null) {
            AlertCus.show("Thông Báo", "Vui lòng chọn hóa đơn cần hủy");
            return;
        }
        if (hoaDonSelected.getTrangthai() != 0) {
            AlertCus.show("Thông Báo", "Chỉ có hóa đơn đang đặt trước mới được hủy");
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime tgCheckin = hoaDonSelected.getTgCheckIn(); // đúng getter của đại ca

        if (tgCheckin == null) {
            AlertCus.show("Thông Báo", "Hóa đơn chưa có tgCheckin.");
            return;
        }

        if (!now.isBefore(tgCheckin)) {
            AlertCus.show("Thông Báo", "Đã tới/qua tgCheckin. Không áp chính sách hủy này.");
            return;
        }

        long diffMinutes = java.time.Duration.between(now, tgCheckin).toMinutes(); // còn bao nhiêu phút
        int phanTram;
        if (diffMinutes < 12 * 60L) phanTram = 0;          // <12h: mất
        else if (diffMinutes < 18 * 60L) phanTram = 50;    // <18h: 50%
        else if (diffMinutes < 24 * 60L) phanTram = 70;    // <24h: 70%
        else phanTram = 100;                               // >=24h: 100%
        long diffHoursShow = diffMinutes / 60;
        boolean answer = ConfirmCus.show(
                "Xác nhận hủy đơn",
                "Bạn có chắc muốn hủy đơn đặt bàn này?\n" +
                        "Còn trước tgCheckin: " + diffHoursShow + " giờ\n" +
                        "Voucher quy đổi: " + phanTram + "% (1 lần dùng)"
        );
        if (!answer) return;
        try {
            hoaDonSelected.setTrangthai(3);
            boolean ok = HoaDonDAO.update(hoaDonSelected);
            if (!ok) {
                AlertCus.show("Thông Báo", "Hủy thất bại");
                return;
            }
            // 2) tạo voucher nếu có %
            if (phanTram > 0) {
                double tienCoc = hoaDonSelected.getCoc();
                double tienVoucherRaw = tienCoc * phanTram / 100.0;

                int tienVoucher = (int) (Math.floor(tienVoucherRaw / 10) * 10);

                boolean okV = KhuyenMaiDAO.insertVoucherHuyDatBan(
                        hoaDonSelected.getMaHD(),
                        tienVoucher,
                        LocalDate.now().plusDays(30)
                );

                if (!okV) {
                    AlertCus.show("Thông Báo",
                            "Hủy đặt bàn thành công.\n" +
                                    "Nhưng tạo voucher thất bại (lỗi DB hoặc trùng mã).");
                } else {
                    AlertCus.show("Thông Báo",
                            "Hủy đặt bàn thành công.\n" +
                                    "Đã tạo voucher 1 lần dùng (" + phanTram + "%), hạn 30 ngày.\n" +
                                    "Tên voucher = mã HĐ: " + hoaDonSelected.getMaHD());
                }
            } else {
                AlertCus.show("Thông Báo",
                        "Hủy đặt bàn thành công.\n" +
                                "Hủy sát giờ (<12h) nên không có voucher.");
            }

            dsDatTruoc.remove(hoaDonSelected);
            dsDaNhan.remove(hoaDonSelected);
            hienThiDanhSachDatTruoc();
            hienThiDanhSachDaNhan();
            resetForm();

        } catch (Exception e) {
            e.printStackTrace();
            AlertCus.show("Thông Báo", "Lỗi hệ thống: " + e.getMessage());
        }
    }

    private void khoiTaoChonMon() {
        try {
            dsMonToanBo = monDAO.getAll();
        } catch (Exception ex) {
            dsMonToanBo = new ArrayList<>();
            ex.printStackTrace();
        }
        // hiển thị danh sách món nhỏ (foodList) nếu có dùng
        hienThiDanhSachMon(dsMonToanBo);
        // build cache card cho menu center
        menuCardCache.clear();
        if (dsMonToanBo != null) {
            for (Mon m : dsMonToanBo) {
                if (m == null || m.getMaMon() == null) continue;
                VBox card = taoCardMon(m);   // tạo card 1 lần
                menuCardCache.put(m.getMaMon(), card);
            }
        }
        loadComboDanhMuc();
        // hiển thị toàn bộ món ban đầu
        locMonTheoTenVaLoai();
        if (tfTimKiem != null) {
            tfTimKiem.textProperty().addListener((obs, oldV, newV) -> locMonTheoTenVaLoai());
        }
    }

    private void hienThiDanhSachMon(List<Mon> danhSachMon) {
        if (foodList == null) return;
        foodList.getChildren().clear();

        if (danhSachMon == null || danhSachMon.isEmpty()) {
            Label empty = new Label("Không có món ăn");
            empty.setStyle("-fx-text-fill: #666; -fx-font-style: italic; -fx-padding: 20px;");
            foodList.getChildren().add(empty);
            return;
        }

        for (Mon m : danhSachMon) {
            VBox card = taoTheMon(m);
            foodList.getChildren().add(card);
        }
    }

    private VBox taoTheMon(Mon m) {
        VBox card = new VBox(6);
        card.getStyleClass().add("food-card");
        card.setPrefWidth(90);
        card.setPrefHeight(110);
        card.setPadding(new Insets(6));
        card.setCursor(Cursor.HAND);

        StackPane imageWrapper = new StackPane();
        imageWrapper.setPrefSize(60, 60);
        ImageView iv = new ImageView();
        iv.setFitWidth(60);
        iv.setFitHeight(60);
        iv.setPreserveRatio(true);
        try {
            Image img = getCachedImage("/IMG/food/restaurant.png");
            if (img != null) iv.setImage(img);
        } catch (Exception ex) {
            // bỏ qua nếu không load được ảnh
        }
        imageWrapper.getChildren().add(iv);

        Button btnAdd = new Button("+");
        btnAdd.setStyle("-fx-background-radius: 20; -fx-font-weight: bold;");
        StackPane.setAlignment(btnAdd, javafx.geometry.Pos.TOP_RIGHT);
        imageWrapper.getChildren().add(btnAdd);

        Label lblTen = new Label(m.getTenMon());
        lblTen.setWrapText(true);
        lblTen.setStyle("-fx-font-size: 12px; -fx-text-alignment: center;");

        Label lblGia = new Label(nf.format(m.getGiaBanTaiLucLapHD(hoaDonSelected)) + " VNĐ");
        lblGia.setStyle("-fx-font-size: 11px; -fx-text-fill: #666;");

        card.getChildren().addAll(imageWrapper, lblTen, lblGia);

        btnAdd.setOnAction(e -> themMonVaoDon(m));
        card.setOnMouseClicked(e -> themMonVaoDon(m));

        return card;
    }

    private void themMonVaoDon(Mon m) {
        if (m == null) return;
        if (hoaDonSelected == null) {
            AlertCus.show("Thông Báo", "Vui lòng chọn hóa đơn trước khi thêm món.");
            return;
        }
        int tonKho = m.getSoLuong();
        if (tonKho <= 0) {
            AlertCus.show("Thông Báo",
                    "Món \"" + m.getTenMon() + "\" đã hết hàng, không thể chọn.");
            return;
        }
        ChiTietHoaDon found = null;
        for (ChiTietHoaDon ct : chiTietHoaDonData) {
            if (ct.getMon() != null && m.getMaMon().equals(ct.getMon().getMaMon())) {
                found = ct;
                break;
            }
        }
        int soLuongDaChon = (found != null) ? found.getSoLuong() : 0;

        if (soLuongDaChon >= tonKho) {
            AlertCus.show("Thông Báo",
                    "Món \"" + m.getTenMon() + "\" chỉ còn " + tonKho + " phần.\nKhông thể chọn thêm.");
            return;
        }
        if (found != null) {
            int slMoi = soLuongDaChon + 1;
            found.setSoLuong(slMoi);
            double gia = found.getMon().getGiaBanTaiLucLapHD(hoaDonSelected);
            found.setThanhTien(gia * slMoi);
            AlertCus.show("Thông Báo", "Đã tăng số lượng cho món " + m.getTenMon());
        } else {
            ChiTietHoaDon ct = new ChiTietHoaDon(hoaDonSelected, m, 1);
            chiTietHoaDonData.add(ct);
            AlertCus.show("Thông Báo", "Đã thêm món: " + m.getTenMon());
        }
        capNhatBangDonHang();
        capNhatUIChiTiet();
    }

    private void capNhatBangDonHang() {
        for (ChiTietHoaDon ct : chiTietHoaDonData) {
            if (ct.getMon() != null) {
                ct.setThanhTien(ct.getMon().getGiaBanTaiLucLapHD(hoaDonSelected) * ct.getSoLuong());
            }
        }
        double tong = 0;
        for (ChiTietHoaDon ct : chiTietHoaDonData) tong += ct.getThanhTien();
        System.out.println("Tổng đơn hàng hiện tại: " + nf.format(tong) + " VNĐ");
    }

    private void resetForm() {
        hoaDonSelected = null;
        if (lblMaHoaDon != null) lblMaHoaDon.setText("");
        if (lblHoTen != null) lblHoTen.setText("");
        if (lblSDT != null) lblSDT.setText("");
        if (lblBan != null) lblBan.setText("");
        if (eventCombo != null) eventCombo.setValue(null);
        if (txtSoLuongKhach != null) txtSoLuongKhach.clear();
        chiTietHoaDonData.clear();
        vboxChiTietDonHang.getChildren().clear();
        clearSelectedStyles(danhSachDatTruoc);
        clearSelectedStyles(danhSachDaNhan);
    }

    private String formatCurrency(double amount) {
        Locale localeVN = new Locale("vi", "VN");
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(localeVN);
        DecimalFormat df = new DecimalFormat("#,###", symbols);
        return df.format(amount) + " đ";
    }

    private HBox taoDongChiTiet(ChiTietHoaDon ct, int stt) {
        Mon mon = ct.getMon();
        int soLuong = ct.getSoLuong();

        VBox vbox = new VBox(4);
        vbox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        HBox.setHgrow(vbox, javafx.scene.layout.Priority.ALWAYS);

        Label lblTen = new Label(mon.getTenMon());
        lblTen.getStyleClass().addAll("order-col", "product");
        lblTen.setWrapText(true);
        lblTen.setMaxWidth(Double.MAX_VALUE);
        lblTen.setStyle("-fx-font-weight: bold; -fx-font-size: 13.5px; -fx-text-fill: #333;");

        HBox hboxInfo = new HBox(10);
        hboxInfo.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        Label lblSoLuong = new Label(String.valueOf(soLuong));
        lblSoLuong.getStyleClass().addAll("order-col", "quantity", "lblSoLuongCT");
        lblSoLuong.setPrefWidth(30);
        lblSoLuong.setAlignment(javafx.geometry.Pos.CENTER);

        Label lblGia = new Label(formatCurrency(mon.getGiaBanTaiLucLapHD(ct.getHoaDon())));
        lblGia.getStyleClass().addAll("order-col", "price");
        lblGia.setPrefWidth(70);
        lblGia.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);

        Label lblTongTien = new Label(formatCurrency(ct.getThanhTien()));
        lblTongTien.getStyleClass().addAll("order-col", "total", "lblTongTienCT");
        lblTongTien.setPrefWidth(80);
        lblTongTien.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnMinus1 = new Button("-1");
        btnMinus1.getStyleClass().add("btn-minus");

        Button btnDeleteAll = new Button("✕");
        btnDeleteAll.getStyleClass().add("btn-delete");

        btnMinus1.setOnAction(e -> giamMotSoLuong(ct));
        btnDeleteAll.setOnAction(e -> xoaToanBoMon(ct));

        hboxInfo.getChildren().addAll(lblSoLuong, lblGia, lblTongTien, spacer, btnMinus1, btnDeleteAll);
        vbox.getChildren().addAll(lblTen, hboxInfo);

        HBox row = new HBox(vbox);
        row.getStyleClass().add("order-row");
        row.setSpacing(10);
        row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        return row;
    }

    private void giamMotSoLuong(ChiTietHoaDon ct) {
        if (ct == null) return;
        if (ct.getMon() == null) return;

        String maMon = ct.getMon().getMaMon();
        int current = ct.getSoLuong();

        int soLuongGoc = 0;
        if (hoaDonSelected != null && hoaDonSelected.getTrangthai() == 1) {
            soLuongGoc = soLuongGocMap.getOrDefault(maMon, 0);
        }

        if (hoaDonSelected != null && hoaDonSelected.getTrangthai() == 1 && current <= soLuongGoc) {
            AlertCus.show("Thông Báo", "Không thể giảm thêm. Đây là số lượng đã đặt trước.");
            return;
        }

        if (current <= 1) {
            if (soLuongGoc == 0) {
                chiTietHoaDonData.remove(ct);
            } else {
                ct.setSoLuong(soLuongGoc);
            }
        } else {
            ct.setSoLuong(current - 1);
        }

        double gia = ct.getMon().getGiaBanTaiLucLapHD(hoaDonSelected);
        ct.setThanhTien(gia * ct.getSoLuong());

        capNhatBangDonHang();
        capNhatUIChiTiet();
    }

    private void xoaToanBoMon(ChiTietHoaDon ct) {
        if (ct == null || ct.getMon() == null) return;

        String maMon = ct.getMon().getMaMon();
        int soLuongGoc = 0;
        if (hoaDonSelected != null && hoaDonSelected.getTrangthai() == 1) {
            soLuongGoc = soLuongGocMap.getOrDefault(maMon, 0);
        }

        if (hoaDonSelected != null && hoaDonSelected.getTrangthai() == 1 && soLuongGoc > 0) {
            AlertCus.show("Thông Báo", "Không thể xóa món đã đặt trước, chỉ được xóa món mới thêm.");
            return;
        }

        chiTietHoaDonData.remove(ct);
        capNhatBangDonHang();
        capNhatUIChiTiet();
    }

    private void showDanhSachMode() {
        if (paneDanhSach != null && paneMenu != null) {
            paneDanhSach.setVisible(true);
            paneDanhSach.setManaged(true);
            resetForm();
            paneMenu.setVisible(false);
            paneMenu.setManaged(false);
        }
    }

    private void showMenuMode() {
        if (paneDanhSach != null && paneMenu != null) {
            paneDanhSach.setVisible(false);
            paneDanhSach.setManaged(false);

            paneMenu.setVisible(true);
            paneMenu.setManaged(true);
        }
    }
    // ====== MENU CENTER: COMBO LOẠI + SEARCH + CARD MÓN ======

    private void loadComboDanhMuc() {
        if (comboDanhMuc == null) return;

        comboDanhMuc.getItems().clear();

        LoaiMon tatCa = new LoaiMon("ALL", "Tất cả món", "Tat ca");
        comboDanhMuc.getItems().add(tatCa);

        try {
            comboDanhMuc.getItems().addAll(loaiMonDAO.getAll());
        } catch (Exception e) {
            e.printStackTrace();
        }

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

        comboDanhMuc.getSelectionModel().selectFirst();

        comboDanhMuc.setOnAction(e -> locMonTheoTenVaLoai());
    }

    private void locMonTheoTenVaLoai() {
        if (flowMonAn == null) return;

        String keyword = (tfTimKiem != null)
                ? tfTimKiem.getText().trim().toLowerCase()
                : "";

        LoaiMon loaiChon = (comboDanhMuc != null)
                ? comboDanhMuc.getSelectionModel().getSelectedItem()
                : null;

        List<Mon> ketQua = new ArrayList<>();
        for (Mon m : dsMonToanBo) {
            if (m == null) continue;

            boolean matchText = keyword.isEmpty()
                    || (m.getTenMon() != null && m.getTenMon().toLowerCase().contains(keyword));

            boolean matchLoai = true;
            if (loaiChon != null && !"ALL".equals(loaiChon.getMaLoaiMon())) {
                LoaiMon loaiMon = m.getLoaiMon();
                matchLoai = (loaiMon != null
                        && loaiChon.getMaLoaiMon().equals(loaiMon.getMaLoaiMon()));
            }

            if (matchText && matchLoai) {
                ketQua.add(m);
            }
        }

        flowMonAn.getChildren().clear();
        if (ketQua.isEmpty()) {
            Label empty = new Label("Không có món phù hợp");
            empty.setStyle("-fx-text-fill: #666; -fx-font-style: italic; -fx-padding: 20px;");
            flowMonAn.getChildren().add(empty);
            return;
        }

        for (Mon m : ketQua) {
            VBox card = menuCardCache.get(m.getMaMon());
            if (card != null) {
                flowMonAn.getChildren().add(card);
            }
        }
    }

    private Image getCachedImage(String path) {
        if (path == null) return null;
        Image img = imageCache.get(path);
        if (img != null) return img;

        try {
            img = new Image(getClass().getResourceAsStream(path));
            imageCache.put(path, img);
            return img;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Card món ở center: ảnh lớn + tên + giá + nút "+"
     */
    private VBox taoCardMon(Mon m) {
        VBox card = new VBox(8);
        card.getStyleClass().add("menu-card");
        card.setAlignment(javafx.geometry.Pos.CENTER);
        card.setPrefWidth(160);
        card.setPrefHeight(180);
        card.setCursor(Cursor.HAND);

        StackPane imageWrapper = new StackPane();
        imageWrapper.setPrefSize(150, 110);

        ImageView imageView = new ImageView();
        imageView.setFitWidth(150);
        imageView.setFitHeight(110);
        imageView.setPreserveRatio(true);

        String file = (m.getHinhAnh() != null ? m.getHinhAnh().replaceFirst("^/", "") : "restaurant.png");
        String path = "/IMG/food/" + file;

        Image img = getCachedImage(path);
        if (img == null) {
            img = getCachedImage("/IMG/food/restaurant.png");
        }
        if (img != null) {
            imageView.setImage(img);
        }

        imageWrapper.getChildren().add(imageView);

        Button btnAdd = new Button("+");
        btnAdd.getStyleClass().add("add-icon");
        StackPane.setAlignment(btnAdd, javafx.geometry.Pos.TOP_RIGHT);
        imageWrapper.getChildren().add(btnAdd);

        Label lblTen = new Label(m.getTenMon());
        lblTen.getStyleClass().add("menu-item-name");
        lblTen.setWrapText(true);

        Label lblGia = new Label("SL: " + m.getSoLuong() + " - " + formatCurrency(m.getGiaBan()));
        lblGia.getStyleClass().add("menu-item-price");

        card.getChildren().addAll(imageWrapper, lblTen, lblGia);

        btnAdd.setOnAction(e -> themMonVaoDon(m));
        card.setOnMouseClicked(e -> themMonVaoDon(m));

        return card;
    }

}
