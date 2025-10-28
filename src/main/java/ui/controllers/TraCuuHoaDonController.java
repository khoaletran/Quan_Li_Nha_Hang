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
import entity.HoaDon;
import entity.KhachHang;
import entity.ChiTietHoaDon;
import entity.Mon;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class TraCuuHoaDonController {

    // FXML - danh sách hóa đơn
    @FXML private VBox vbox_center_scroll;

    // FXML - bộ lọc và tìm kiếm
    @FXML private TextField txtMaBan;
    @FXML private DatePicker dpThoiGian;
    @FXML private TextField txtSDT;
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

    // FXML - bảng chi tiết hóa đơn
    @FXML private TableView<ChiTietHoaDon> product_table;
    @FXML private TableColumn<ChiTietHoaDon, String> colSanPham;
    @FXML private TableColumn<ChiTietHoaDon, String> colSoLuong;
    @FXML private TableColumn<ChiTietHoaDon, String> colGia;
    @FXML private TableColumn<ChiTietHoaDon, String> colTong;

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
        System.out.println("TraCuuHoaDonController initialized");

        if (!ketNoiDatabase()) {
            hienThiThongBaoLoi("Không thể kết nối database. Vui lòng kiểm tra kết nối.");
            return;
        }

        khoiTaoComboBox();
        khoiTaoDatePicker();
        ganSuKienChoNut();
        khoiTaoTableView();
        taiDanhSachHoaDon();
        resetForm();
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
        if (cboKhuVuc != null) {
            cboKhuVuc.getItems().clear();
            cboKhuVuc.getItems().addAll("Tất cả", "Indoor", "Outdoor", "VIP");
            cboKhuVuc.setValue("Tất cả");
        }
    }

    private void khoiTaoDatePicker() {
        if (dpThoiGian != null) {
            dpThoiGian.setPromptText("dd/MM/yyyy");
        }
    }

    private void ganSuKienChoNut() {
        if (btnTimKiem != null) btnTimKiem.setOnAction(e -> timKiemHoaDon());
        if (btnXoaTrang != null) btnXoaTrang.setOnAction(e -> xoaTrangBoLoc());
        if (confirm_btn != null) confirm_btn.setOnAction(e -> inHoaDon());
    }

    private void khoiTaoTableView() {
        if (colSanPham != null) {
            colSanPham.setCellValueFactory(cell -> {
                Mon m = cell.getValue().getMon();
                String ten = (m != null && m.getTenMon() != null) ? m.getTenMon() : "Không xác định";
                return new javafx.beans.property.SimpleStringProperty(ten);
            });
        }

        if (colSoLuong != null) {
            colSoLuong.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(
                    String.valueOf(cell.getValue().getSoLuong())));
        }

        if (colGia != null) {
            colGia.setCellValueFactory(cell -> {
                Mon m = cell.getValue().getMon();
                double g = (m != null) ? m.getGiaBanTaiLucLapHD(hoaDonSelected) : 0;
                return new javafx.beans.property.SimpleStringProperty(String.format("%,.0f VNĐ", g));
            });
        }

        if (colTong != null) {
            colTong.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(
                    String.format("%,.0f VNĐ", cell.getValue().getThanhTien())));
        }

        if (product_table != null) product_table.setItems(chiTietHoaDonData);
    }

    // TẢI & HIỂN THỊ DANH SÁCH HÓA ĐƠN
    private void taiDanhSachHoaDon() {
        try {
            List<HoaDon> listHD = hoaDonDAO.getAll();
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
            default: return "Không xác định";
        }
    }

    private String getTrangThaiStyle(int trangThai) {
        switch (trangThai) {
            case 0: return "-fx-text-fill: #e74c3c; -fx-font-weight: bold; -fx-font-size: 11px;";
            case 1: return "-fx-text-fill: #f39c12; -fx-font-weight: bold; -fx-font-size: 11px;";
            case 2: return "-fx-text-fill: #27ae60; -fx-font-weight: bold; -fx-font-size: 11px;";
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
        chiTietHoaDonData.clear();
        if (maHD == null || maHD.trim().isEmpty()) return;
        try {
            List<ChiTietHoaDon> dsChiTiet = chiTietHDDAO.getByMaHD(maHD);
            if (dsChiTiet != null && !dsChiTiet.isEmpty()) {
                chiTietHoaDonData.addAll(dsChiTiet);
                System.out.println("✅ Đã tải " + dsChiTiet.size() + " chi tiết hóa đơn");
                for (ChiTietHoaDon ct : dsChiTiet) {
                    System.out.println("   - " + (ct.getMon() != null ? ct.getMon().getTenMon() : "null") +
                            " x " + ct.getSoLuong() + " = " + ct.getThanhTien());
                }
            } else {
                System.out.println("Không có chi tiết hóa đơn cho mã: " + maHD);
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi tải chi tiết hóa đơn: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // TÌM KIẾM
    @FXML
    private void timKiemHoaDon() {
        String maBan = txtMaBan != null ? txtMaBan.getText().trim() : "";
        String sdt = txtSDT != null ? txtSDT.getText().trim() : "";
        String khuVuc = cboKhuVuc != null && cboKhuVuc.getValue() != null ? cboKhuVuc.getValue() : "Tất cả";
        LocalDate ngay = dpThoiGian != null ? dpThoiGian.getValue() : null;

        try {
            List<HoaDon> allHD = hoaDonDAO.getAll();
            List<HoaDon> ketQua = new java.util.ArrayList<>();

            for (HoaDon hd : allHD) {
                boolean match = true;

                // mã bàn
                if (!maBan.isEmpty()) {
                    if (hd.getBan() == null || hd.getBan().getMaBan() == null ||
                            !hd.getBan().getMaBan().toLowerCase().contains(maBan.toLowerCase())) {
                        match = false;
                    }
                }

                // sdt
                if (!sdt.isEmpty()) {
                    String sdtHD = null;
                    if (hd.getKhachHang() != null && hd.getKhachHang().getSdt() != null) {
                        sdtHD = hd.getKhachHang().getSdt();
                    } else {
                        try {
                            String maKH = null;
                            try {
                                Method m = hd.getClass().getMethod("getMaKH");
                                Object obj = m.invoke(hd);
                                if (obj != null) maKH = obj.toString();
                            } catch (NoSuchMethodException nsme) { maKH = null; }
                            if (maKH != null && !maKH.isEmpty()) {
                                KhachHang kh = khachHangDAO.getById(maKH);
                                if (kh != null) sdtHD = kh.getSdt();
                            }
                        } catch (Exception ex) { /* ignore */ }
                    }

                    if (sdtHD == null || !sdtHD.contains(sdt)) match = false;
                }

                // khu vực
                if (!khuVuc.equals("Tất cả")) {
                    if (hd.getBan() == null || hd.getBan().getKhuVuc() == null ||
                            hd.getBan().getKhuVuc().getTenKhuVuc() == null ||
                            !hd.getBan().getKhuVuc().getTenKhuVuc().equals(khuVuc)) {
                        match = false;
                    }
                }

                // ngày
                if (ngay != null) {
                    try {
                        Object tgObj = null;
                        Method m = null;
                        try { m = hd.getClass().getMethod("getTgCheckin"); }
                        catch (NoSuchMethodException nsme) {
                            try { m = hd.getClass().getMethod("getTgCheckout"); }
                            catch (NoSuchMethodException ex2) { m = null; }
                        }
                        if (m != null) tgObj = m.invoke(hd);

                        LocalDate ngayHD = null;
                        if (tgObj == null) ngayHD = null;
                        else if (tgObj instanceof LocalDate) ngayHD = (LocalDate) tgObj;
                        else if (tgObj instanceof LocalDateTime) ngayHD = ((LocalDateTime) tgObj).toLocalDate();
                        else if (tgObj instanceof Date) ngayHD = ((Date) tgObj).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                        else {
                            String s = tgObj.toString();
                            try { ngayHD = LocalDate.parse(s, dtf); }
                            catch (Exception ex) {
                                try { ngayHD = LocalDate.parse(s); } catch (Exception ex2) { ngayHD = null; }
                            }
                        }

                        if (ngayHD == null || !ngayHD.equals(ngay)) match = false;
                    } catch (Exception ex) {
                        System.out.println("Không thể lấy ngày từ HoaDon bằng reflection: " + ex.getMessage());
                        match = false;
                    }
                }

                if (match) ketQua.add(hd);
            }

            dsHoaDon.clear();
            dsHoaDon.addAll(ketQua);
            hienThiDanhSachHoaDon();

            if (ketQua.isEmpty()) hienThiThongBao("Không tìm thấy hóa đơn nào phù hợp với điều kiện tìm kiếm");
            else hienThiThongBao("Tìm thấy " + ketQua.size() + " hóa đơn phù hợp");

        } catch (Exception e) {
            e.printStackTrace();
            hienThiThongBaoLoi("Lỗi khi tìm kiếm hóa đơn: " + e.getMessage());
        }
    }

    // XÓA TRẮNG BỘ LỌC / IN / RESET
    @FXML
    private void xoaTrangBoLoc() {
        if (txtMaBan != null) txtMaBan.clear();
        if (dpThoiGian != null) dpThoiGian.setValue(null);
        if (txtSDT != null) txtSDT.clear();
        if (cboKhuVuc != null) cboKhuVuc.setValue("Tất cả");

        taiDanhSachHoaDon();
        resetForm();
    }

    @FXML
    private void inHoaDon() {
        if (hoaDonSelected == null) {
            hienThiThongBao("Vui lòng chọn hóa đơn cần in");
            return;
        }
        try {
            hienThiThongBao("Đang in hóa đơn: " + hoaDonSelected.getMaHD() + "\nChức năng in đang được phát triển...");
        } catch (Exception e) {
            e.printStackTrace();
            hienThiThongBaoLoi("Lỗi khi in hóa đơn: " + e.getMessage());
        }
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
    private void hienThiThongBao(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void hienThiThongBaoLoi(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Lỗi");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    //Refresh
    public void refreshData() {
        try {
            if (!ketNoiDatabase()) {
                hienThiThongBaoLoi("Không thể kết nối database khi refresh");
                return;
            }
            taiDanhSachHoaDon();
            resetForm();
            hienThiThongBao("✅ Đã làm mới dữ liệu");
        } catch (Exception e) {
            System.err.println("❌ Lỗi khi refresh data: " + e.getMessage());
            hienThiThongBao("❌ Lỗi khi làm mới dữ liệu");
        }
    }
}
