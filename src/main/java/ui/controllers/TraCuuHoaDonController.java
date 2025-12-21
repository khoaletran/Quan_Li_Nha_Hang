/*
 * @ (#) TraCuuHoaDonController.java    1.1     10/27/2025
 *
 * Phiên bản tinh gọn — giữ nguyên tên phương thức tiếng Việt
 */

package ui.controllers;

import connectDB.connectDB;
import dao.HoaDonDAO;
import dao.KhachHangDAO;
import dao.ChiTietHDDAO;
import dao.KhuVucDAO;
import entity.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ui.AlertCus;
import ui.HoaDonIn;

import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class TraCuuHoaDonController {

    // FXML - danh sách hóa đơn
    @FXML private VBox vbox_center_scroll, vboxChiTietDonHang;

    // FXML - bộ lọc và tìm kiếm
    @FXML private TextField txtTimKiem;
    @FXML private DatePicker dpThoiGian;
    @FXML private ComboBox<String> cboTrangThai;
    @FXML private ComboBox<String> cboKhuVuc;
    @FXML private Button btnXoaTrang;
    @FXML private Button btnTimKiem;

    // FXML - thông tin chi tiết hóa đơn
    @FXML private TextField txtMaHoaDon;
    @FXML private TextField txtTenKH;
    @FXML private TextField txtSDTChiTiet;
    @FXML private TextField txtBan;
    @FXML private TextField txtSoLuong;
    @FXML private TextField txtSuKien;
    @FXML private TextField txtKhuVuc;
    @FXML private TextArea txtMoTa;

    // FXML - nút in hóa đơn
    @FXML private Button confirm_btn;

    // DAO + dữ liệu
    private final HoaDonDAO hoaDonDAO = new HoaDonDAO();
    private final KhachHangDAO khachHangDAO = new KhachHangDAO();
    private final ChiTietHDDAO chiTietHDDAO = new ChiTietHDDAO();

    private final ObservableList<HoaDon> dsHoaDon = FXCollections.observableArrayList();
    private final ObservableList<ChiTietHoaDon> chiTietHoaDonData = FXCollections.observableArrayList();
    private HoaDon hoaDonSelected = null;

    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    public void initialize() {
        if (!ketNoiDatabase()) {
            hienThiThongBaoLoi("Không thể kết nối database. Vui lòng kiểm tra kết nối.");
            return;
        }
        khoiTaoTrangThai();
        khoiTaoComboBox();
        khoiTaoDatePicker();
        ganSuKienChoNut();
        taiDanhSachHoaDon(null);
        resetForm();

        Tooltip tipFind = new Tooltip("Tìm kiếm nhanh (Ctrl + F)");
        Tooltip.install(btnTimKiem, tipFind);
        Tooltip tipClear = new Tooltip("Clear nhanh (Ctrl + L)");
        Tooltip.install(btnXoaTrang, tipClear);
        Tooltip tipPrint = new Tooltip("In nhanh hóa đơn(Ctrl + P)");
        Tooltip.install(confirm_btn, tipPrint);
        
    }
    private void addShortcuts(Scene scene){
        KeyCombination ctrlD = new KeyCodeCombination(KeyCode.D, KeyCombination.CONTROL_DOWN);
        KeyCombination ctrlF = new KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN);
        KeyCombination ctrlL = new KeyCodeCombination(KeyCode.L, KeyCombination.CONTROL_DOWN);
        scene.getAccelerators().put(ctrlL, () -> xoaTrangBoLoc());
        KeyCombination ctrlP = new KeyCodeCombination(KeyCode.P, KeyCombination.CONTROL_DOWN);
        scene.getAccelerators().put(ctrlP, () -> HoaDonIn.previewHoaDon(hoaDonSelected));
    }
    // KẾT NỐI DB
    private boolean ketNoiDatabase() {
        try {
            connectDB.getInstance().connect();
            System.out.println("Kết nối database thành công");
            return true;
        } catch (Exception e) {
            System.err.println("Lỗi kết nối database: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // KHỞI TẠO CONTROL
    private void khoiTaoComboBox() {
        if(cboKhuVuc == null) return;
        List<KhuVuc> dsKhuVuc = KhuVucDAO.getAll();
        cboKhuVuc.getItems().clear();
        for (KhuVuc kv : dsKhuVuc) {
            cboKhuVuc.getItems().add(kv.getTenKhuVuc());
        }
        cboKhuVuc.setValue(null);
    }

    private void khoiTaoDatePicker() {
        if (dpThoiGian != null) {
            dpThoiGian.setPromptText("dd/MM/yyyy");
        }
    }

    private void ganSuKienChoNut() {
        if (btnTimKiem != null) btnTimKiem.setOnAction(e -> locHoaDon());
        if (btnXoaTrang != null) btnXoaTrang.setOnAction(e -> xoaTrangBoLoc());
        if (confirm_btn != null) confirm_btn.setOnAction(e -> HoaDonIn.previewHoaDon(hoaDonSelected));
    }
    // TẢI & HIỂN THỊ DANH SÁCH HÓA ĐƠN
    private void taiDanhSachHoaDon(List<HoaDon> hoaDonSelected) {
        try {
            List<HoaDon> listHD = hoaDonSelected;
            dsHoaDon.clear();
            if (listHD != null) dsHoaDon.addAll(listHD);
            hienThiDanhSachHoaDon();
        } catch (Exception ex) {
            System.err.println("Lỗi khi tải danh sách hóa đơn: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void hienThiDanhSachHoaDon() {
        if (vbox_center_scroll == null) return;
        vbox_center_scroll.getChildren().clear();

        if (dsHoaDon.isEmpty()) {
            Label empty = new Label("Không có hóa đơn nào");
            empty.setStyle("-fx-text-fill: #666; -fx-font-style: italic; -fx-padding: 20px;");
            vbox_center_scroll.getChildren().add(empty);
            return;
        }

        for (HoaDon hd : dsHoaDon) {
            HBox card = taoCardHoaDon(hd);
            vbox_center_scroll.getChildren().add(card);
        }
    }

    private HBox taoCardHoaDon(HoaDon hd) {
        HBox card = new HBox(10);
        card.setPadding(new Insets(8));
        card.setCursor(Cursor.HAND);
        card.setPrefHeight(80);
        card.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #dee2e6; -fx-border-radius: 8; -fx-background-radius: 8;");

        // thumb
        StackPane thumb = new StackPane();
        thumb.setPrefSize(60, 60);
        ImageView iv = new ImageView();
        iv.setFitWidth(60);
        iv.setFitHeight(60);
        iv.setPreserveRatio(false);
        try {
            Image img = new Image(getClass().getResourceAsStream("/IMG/avatar.png"));
            iv.setImage(img);
            thumb.getChildren().add(iv);
        } catch (Exception e) {
            thumb.setStyle("-fx-background-color: #e9ecef; -fx-alignment: center;");
            Label lb = new Label("HD");
            lb.setStyle("-fx-text-fill: #666; -fx-font-weight: bold;");
            thumb.getChildren().add(lb);
        }

        // info
        VBox info = new VBox(2);
        Label lblMa = new Label(hd.getMaHD());
        lblMa.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #333;");
        String tenKH = Optional.ofNullable(hd.getKhachHang()).map(KhachHang::getTenKhachHang).orElse("Không có");
        Label lblTen = new Label("Tên: " + tenKH);
        lblTen.setStyle("-fx-text-fill: #666; -fx-font-size: 12px;");
        String sdt = Optional.ofNullable(hd.getKhachHang()).map(KhachHang::getSdt).orElse("Không có");
        Label lblPhone = new Label("SĐT: " + sdt);
        lblPhone.setStyle("-fx-text-fill: #666; -fx-font-size: 12px;");
        info.getChildren().addAll(lblMa, lblTen, lblPhone);
        HBox.setHgrow(info, javafx.scene.layout.Priority.ALWAYS);

        Label lblTrangThai = new Label(getTrangThaiText(hd.getTrangthai()));
        lblTrangThai.setStyle(getTrangThaiStyle(hd.getTrangthai()));

        card.getChildren().addAll(thumb, info, lblTrangThai);

        card.setOnMouseClicked(e -> {
            System.out.println("Click vào hóa đơn: " + hd.getMaHD());
            chiTietHoaDonData.clear();
            clearSelectedStyles();
            // style card được chọn
            card.setStyle("-fx-background-color: #007bff; -fx-border-color: #0056b3; -fx-border-radius: 8; -fx-background-radius: 8;");
            // đổi màu chữ
            for (javafx.scene.Node node : card.getChildren()) {
                if (node instanceof VBox) {
                    for (javafx.scene.Node child : ((VBox) node).getChildren()) {
                        if (child instanceof Label) {
                            ((Label) child).setStyle("-fx-text-fill: white;");
                        }
                    }
                } else if (node instanceof Label) {
                    ((Label) node).setStyle("-fx-text-fill: white;");
                }
            }

            hoaDonSelected = hd;
            hienThiThongTinChiTiet(hd);
        });

        return card;
    }

    private String getTrangThaiText(int trangThai) {
        switch (trangThai) {
            case 0: return "Đặt trước";
            case 1: return "Đang phục vụ";
            case 2: return "Đã thanh toán";
            case 3: return "Không nhận đơn";
            default: return "Không xác định";
        }
    }

    private String getTrangThaiStyle(int trangThai) {
        switch (trangThai) {
            case 0: return "-fx-text-fill: #e74c3c; -fx-font-weight: bold; -fx-font-size: 11px;";
            case 1: return "-fx-text-fill: #f39c12; -fx-font-weight: bold; -fx-font-size: 11px;";
            case 2: return "-fx-text-fill: #27ae60; -fx-font-weight: bold; -fx-font-size: 11px;";
            case 3: return "-fx-text-fill: red; -fx-font-weight: bold; -fx-font-size: 11px;";
            default: return "-fx-text-fill: #666; -fx-font-weight: bold; -fx-font-size: 11px;";
        }
    }

    private void clearSelectedStyles() {
        if (vbox_center_scroll == null) return;
        for (javafx.scene.Node node : vbox_center_scroll.getChildren()) {
            if (node instanceof HBox) {
                node.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #dee2e6; -fx-border-radius: 8; -fx-background-radius: 8;");
                for (javafx.scene.Node child : ((HBox) node).getChildren()) {
                    if (child instanceof VBox) {
                        for (javafx.scene.Node label : ((VBox) child).getChildren()) {
                            if (label instanceof Label) {
                                if (((Label) label).getText().startsWith("HD")) {
                                    ((Label) label).setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #333;");
                                } else {
                                    ((Label) label).setStyle("-fx-text-fill: #666; -fx-font-size: 12px;");
                                }
                            }
                        }
                    } else if (child instanceof Label) {
                        String text = ((Label) child).getText();
                        if (text.contains("Đặt trước")) {
                            ((Label) child).setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold; -fx-font-size: 11px;");
                        } else if (text.contains("Đang phục vụ")) {
                            ((Label) child).setStyle("-fx-text-fill: #f39c12; -fx-font-weight: bold; -fx-font-size: 11px;");
                        } else if (text.contains("Đã thanh toán")) {
                            ((Label) child).setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold; -fx-font-size: 11px;");
                        } else if (text.contains("Không nhận đơn")) {
                            ((Label) child).setStyle("-fx-text-fill: #d62c01ff; -fx-font-weight: bold; -fx-font-size: 11px;");
                        }
                    }
                }
            }
        }
    }

    // HIỂN THỊ CHI TIẾT HÓA ĐƠN
    private void hienThiThongTinChiTiet(HoaDon hd) {
        if (hd == null) return;
        if (txtMaHoaDon != null) txtMaHoaDon.setText(hd.getMaHD());

        KhachHang kh = hd.getKhachHang();
        if (kh == null) {
            String maKH = null;
            try {
                Method m = hd.getClass().getMethod("getMaKH");
                Object obj = m.invoke(hd);
                if (obj != null) maKH = obj.toString();
            } catch (Exception ignored) {}
            if (maKH != null && !maKH.trim().isEmpty()) {
                try { kh = khachHangDAO.getById(maKH); } catch (Exception ex) { System.out.println("Lỗi lấy KH: " + ex.getMessage()); }
            }
        }

        if (kh != null) {
            if (txtTenKH != null) txtTenKH.setText(kh.getTenKhachHang() != null ? kh.getTenKhachHang() : "—");
            if (txtSDTChiTiet != null) txtSDTChiTiet.setText(kh.getSdt() != null ? kh.getSdt() : "—");
        } else {
            if (txtTenKH != null) txtTenKH.setText("—");
            if (txtSDTChiTiet != null) txtSDTChiTiet.setText("—");
        }

        if (hd.getBan() != null) {
            if (txtBan != null) txtBan.setText(hd.getBan().getMaBan() != null ? hd.getBan().getMaBan() : "—");
            if (txtKhuVuc != null) {
                String tenKhuVuc = "—";
                if (hd.getBan().getKhuVuc() != null && hd.getBan().getKhuVuc().getTenKhuVuc() != null) {
                    tenKhuVuc = hd.getBan().getKhuVuc().getTenKhuVuc();
                }
                txtKhuVuc.setText(tenKhuVuc);
            }
        } else {
            if (txtBan != null) txtBan.setText("—");
            if (txtKhuVuc != null) txtKhuVuc.setText("—");
        }

        if (txtSuKien != null) {
            try {
                txtSuKien.setText(hd.getSuKien() != null && hd.getSuKien().getTenSK() != null ? hd.getSuKien().getTenSK() : "Không có");
            } catch (Exception ex) {
                txtSuKien.setText("Không có");
            }
        }

        if (txtSoLuong != null) {
            try { txtSoLuong.setText(String.valueOf(hd.getSoLuong())); }
            catch (Exception ex) { txtSoLuong.setText("0"); }
        }

        if (txtMoTa != null) {
            try {
                Method m = null;
                try { m = hd.getClass().getMethod("getGhiChu"); } catch (NoSuchMethodException nsme) { m = null; }
                if (m != null) {
                    Object val = m.invoke(hd);
                    txtMoTa.setText(val != null ? val.toString() : "");
                } else txtMoTa.setText("");
            } catch (Exception ex) {
                txtMoTa.setText("");
            }
        }

        loadChiTietDonHang(hd.getMaHD());
    }

    private void loadChiTietDonHang(String maHD) {
        vboxChiTietDonHang.getChildren().clear();
        if (maHD == null || maHD.trim().isEmpty()) return;
        try {
            List<ChiTietHoaDon> dsChiTiet = chiTietHDDAO.getByMaHD(maHD);
            if (dsChiTiet != null && !dsChiTiet.isEmpty()) {
                for (ChiTietHoaDon ct : dsChiTiet) {
                    HBox dong = taoDongChiTiet(ct);
                    vboxChiTietDonHang.getChildren().add(dong);
                }
            } else {
                System.out.println("Không có chi tiết hóa đơn cho mã: " + maHD);
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi tải chi tiết hóa đơn: " + e.getMessage());
            e.printStackTrace();
        }
    }



    // XÓA TRẮNG BỘ LỌC / IN / RESET
    @FXML
    private void xoaTrangBoLoc() {
        if (txtTimKiem != null) txtTimKiem.clear();
        if (dpThoiGian != null) dpThoiGian.setValue(null);
        if (cboTrangThai != null) cboTrangThai.setValue("Tất cả");
        if (cboKhuVuc != null) cboKhuVuc.setValue("Tất cả");

        taiDanhSachHoaDon(null);
        resetForm();
    }

    private void resetForm() {
        hoaDonSelected = null;
        if (txtMaHoaDon != null) txtMaHoaDon.setText("");
        if (txtTenKH != null) txtTenKH.setText("");
        if (txtSDTChiTiet != null) txtSDTChiTiet.setText("");
        if (txtBan != null) txtBan.setText("");
        if (txtSoLuong != null) txtSoLuong.setText("");
        if (txtSuKien != null) txtSuKien.setText("");
        if (txtKhuVuc != null) txtKhuVuc.setText("");
        if (txtMoTa != null) txtMoTa.setText("");

        chiTietHoaDonData.clear();
        clearSelectedStyles();
    }
    // HỘP THOẠI
    private void hienThiThongBaoLoi(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Lỗi");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private String formatCurrency(double amount) {
        Locale localeVN = new Locale("vi", "VN");
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(localeVN);

        DecimalFormat df = new DecimalFormat("#,###", symbols);

        return df.format(amount) + " đ";
    }

    private HBox taoDongChiTiet(ChiTietHoaDon ct) {
        VBox vbox = new VBox(4); // chứa tên và hàng thông tin
        vbox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        HBox.setHgrow(vbox, javafx.scene.layout.Priority.ALWAYS);

        // ===== Tên món (nằm trên) =====
        Label lblTen = new Label(ct.getMon().getTenMon());
        lblTen.getStyleClass().addAll("order-col", "product");
        lblTen.setWrapText(true);
        lblTen.setMaxWidth(Double.MAX_VALUE);
        lblTen.setStyle("-fx-font-weight: bold; -fx-font-size: 13.5px; -fx-text-fill: #333;");

        // ===== Hàng dưới: SL – Giá – Tổng tiền – Nút =====
        HBox hboxInfo = new HBox(10);
        hboxInfo.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        Label lblSoLuong = new Label(String.valueOf(ct.getSoLuong()));
        lblSoLuong.getStyleClass().addAll("order-col", "quantity", "lblSoLuongCT");
        lblSoLuong.setPrefWidth(30);
        lblSoLuong.setAlignment(javafx.geometry.Pos.CENTER);

        Label lblGia = new Label(formatCurrency(ct.getMon().getGiaBanTaiLucLapHD(ct.getHoaDon())));
        lblGia.getStyleClass().addAll("order-col", "price");
        lblGia.setPrefWidth(70);
        lblGia.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);

        Label lblTongTien = new Label(formatCurrency(ct.getThanhTien()));
        lblTongTien.getStyleClass().addAll("order-col", "total", "lblTongTienCT");
        lblTongTien.setPrefWidth(80);
        lblTongTien.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);

        hboxInfo.getChildren().addAll(lblSoLuong, lblGia, lblTongTien);
        vbox.getChildren().addAll(lblTen, hboxInfo);

        HBox row = new HBox(vbox);
        row.getStyleClass().add("order-row");
        row.setSpacing(10);
        row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        return row;
    }

    private void khoiTaoTrangThai() {
        cboTrangThai.getItems().clear();
        cboTrangThai.getItems().addAll(
                "Đặt trước",
                "Đang dùng",
                "Đã thanh toán",
                "Không nhận đơn"
        );
        cboTrangThai.setValue(null);
    }


    private void locHoaDon() {
        String keyword = (txtTimKiem.getText() != null)
                ? txtTimKiem.getText().trim()
                : "";

        String trangThaiTxt = (cboTrangThai != null) ? cboTrangThai.getValue() : null;
        LocalDate ngay = (dpThoiGian != null) ? dpThoiGian.getValue() : null;
        String khuVuc = (cboKhuVuc != null) ? cboKhuVuc.getValue() : null;

        Integer trangThai = null;
        if (trangThaiTxt != null && !"Tất cả".equalsIgnoreCase(trangThaiTxt)) {
            switch (trangThaiTxt) {
                case "Đặt trước":     trangThai = 0; break;
                case "Đang dùng":     trangThai = 1; break;
                case "Đã thanh toán": trangThai = 2; break;
                case "Không nhận đơn":trangThai = 3; break;
            }
        }

        List<HoaDon> list = hoaDonDAO.searchHoaDon(keyword, trangThai, ngay, khuVuc);

        dsHoaDon.setAll(list);
        hienThiDanhSachHoaDon();
    }


}
