/*
 * @ (#) TraCuuHoaDonController.java    1.1     10/27/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.text.Font;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Callback;
import javafx.scene.control.DatePicker;

import java.util.List;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.lang.reflect.Method;
import java.util.Date;

public class TraCuuHoaDonController {

    // FXML - danh sách hóa đơn
    @FXML private VBox vbox_center_scroll; // VBox chứa danh sách hóa đơn

    // FXML - bộ lọc và tìm kiếm
    @FXML private TextField txtMaBan; // TextField mã bàn
    @FXML private DatePicker dpThoiGian; // DatePicker thay cho txtThoiGian
    @FXML private TextField txtSDT; // TextField số điện thoại
    @FXML private ComboBox<String> cboKhuVuc; // ComboBox khu vực
    @FXML private Button btnXoaTrang; // Button xóa trắng
    @FXML private Button btnTimKiem; // Button tìm kiếm

    // FXML - thông tin chi tiết hóa đơn
    @FXML private TextField txtMaHoaDon; // Mã hóa đơn
    @FXML private TextField txtTenKH; // Tên khách hàng
    @FXML private TextField txtSDTChiTiet; // SĐT chi tiết
    @FXML private TextField txtBan; // Bàn
    @FXML private TextField txtSoLuong; // Số lượng
    @FXML private TextField txtSuKien; // Sự kiện
    @FXML private TextField txtKhuVuc; // Khu vực
    @FXML private TextArea txtMoTa; // Mô tả

    // FXML - bảng chi tiết hóa đơn
    @FXML private TableView<ChiTietHoaDon> product_table; // TableView sản phẩm
    @FXML private TableColumn<ChiTietHoaDon, String> colSanPham; // Cột sản phẩm
    @FXML private TableColumn<ChiTietHoaDon, String> colSoLuong; // Cột số lượng
    @FXML private TableColumn<ChiTietHoaDon, String> colGia; // Cột giá
    @FXML private TableColumn<ChiTietHoaDon, String> colTong; // Cột tổng

    // FXML - nút in hóa đơn
    @FXML private Button confirm_btn; // Button in hóa đơn

    // BIẾN TOÀN CỤC
    private final HoaDonDAO hoaDonDAO = new HoaDonDAO();
    private final KhachHangDAO khachHangDAO = new KhachHangDAO();
    private final ChiTietHDDAO chiTietHDDAO = new ChiTietHDDAO();

    private List<HoaDon> dsHoaDon = new java.util.ArrayList<>();
    private HoaDon hoaDonSelected = null;
    private ObservableList<ChiTietHoaDon> chiTietHoaDonData = FXCollections.observableArrayList();

    // Format ngày (hiển thị nếu cần)
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

    private void khoiTaoComboBox() {
        if (cboKhuVuc != null) {
            cboKhuVuc.getItems().clear();
            cboKhuVuc.getItems().addAll("Tất cả", "Trong nhà", "Ngoài trời", "Khu VIP");
            cboKhuVuc.setValue("Tất cả");
        }
    }

    private void khoiTaoDatePicker() {
        if (dpThoiGian != null) {
            dpThoiGian.setPromptText("dd/MM/yyyy");
            // Nếu cần convert giữa String <-> LocalDate có thể set converter ở đây
        }
    }

    private void ganSuKienChoNut() {
        if (btnTimKiem != null) btnTimKiem.setOnAction(e -> timKiemHoaDon());
        if (btnXoaTrang != null) btnXoaTrang.setOnAction(e -> xoaTrangBoLoc());
        if (confirm_btn != null) confirm_btn.setOnAction(e -> inHoaDon());
    }

    private void khoiTaoTableView() {
        // Kiểm tra null trước khi khởi tạo
        if (colSanPham != null) {
            colSanPham.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ChiTietHoaDon, String>, javafx.beans.value.ObservableValue<String>>() {
                @Override
                public javafx.beans.value.ObservableValue<String> call(TableColumn.CellDataFeatures<ChiTietHoaDon, String> param) {
                    Mon mon = param.getValue().getMon();
                    String tenMon = mon != null ? mon.getTenMon() : "Không xác định";
                    return new javafx.beans.property.SimpleStringProperty(tenMon);
                }
            });
        }

        if (colSoLuong != null) {
            colSoLuong.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ChiTietHoaDon, String>, javafx.beans.value.ObservableValue<String>>() {
                @Override
                public javafx.beans.value.ObservableValue<String> call(TableColumn.CellDataFeatures<ChiTietHoaDon, String> param) {
                    int soLuong = param.getValue().getSoLuong();
                    return new javafx.beans.property.SimpleStringProperty(String.valueOf(soLuong));
                }
            });
        }

        if (colGia != null) {
            colGia.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ChiTietHoaDon, String>, javafx.beans.value.ObservableValue<String>>() {
                @Override
                public javafx.beans.value.ObservableValue<String> call(TableColumn.CellDataFeatures<ChiTietHoaDon, String> param) {
                    Mon mon = param.getValue().getMon();
                    double gia = mon != null ? mon.getGiaBan() : 0;
                    return new javafx.beans.property.SimpleStringProperty(String.format("%,.0f VNĐ", gia));
                }
            });
        }

        if (colTong != null) {
            colTong.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ChiTietHoaDon, String>, javafx.beans.value.ObservableValue<String>>() {
                @Override
                public javafx.beans.value.ObservableValue<String> call(TableColumn.CellDataFeatures<ChiTietHoaDon, String> param) {
                    double thanhTien = param.getValue().getThanhTien();
                    return new javafx.beans.property.SimpleStringProperty(String.format("%,.0f VNĐ", thanhTien));
                }
            });
        }

        if (product_table != null) {
            product_table.setItems(chiTietHoaDonData);
        }
    }

    private void taiDanhSachHoaDon() {
        try {
            List<HoaDon> listHD = HoaDonDAO.getAll();
            dsHoaDon.clear();
            if (listHD != null) {
                for (HoaDon hd : listHD) {
                    System.out.println("🔍 Hóa đơn: " + hd.getMaHD() +
                            ", Trạng thái: " + hd.getTrangthai() +
                            ", Khách hàng: " + (hd.getKhachHang() != null ? hd.getKhachHang().getTenKhachHang() : "null") +
                            ", Bàn: " + (hd.getBan() != null ? hd.getBan().getMaBan() : "null"));
                    // Hiển thị tất cả hóa đơn
                    dsHoaDon.add(hd);
                }
            }
            System.out.println("✅ Đã tải " + dsHoaDon.size() + " hóa đơn");
            hienThiDanhSachHoaDon();
        } catch (Exception ex) {
            System.err.println("Lỗi khi tải danh sách hóa đơn: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void hienThiDanhSachHoaDon() {
        if (vbox_center_scroll == null) {
            System.err.println("❌ vbox_center_scroll is null");
            return;
        }

        vbox_center_scroll.getChildren().clear();
        System.out.println("🔄 Hiển thị " + dsHoaDon.size() + " hóa đơn");

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
        card.getStyleClass().add("order-card");
        card.setPadding(new Insets(8));
        card.setCursor(Cursor.HAND);
        card.setPrefHeight(80);
        card.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #dee2e6; -fx-border-radius: 8; -fx-background-radius: 8;");

        // Hình ảnh
        StackPane thumb = new StackPane();
        thumb.setStyle("-fx-background-radius: 10 0 0 10; -fx-overflow: hidden;");
        ImageView iv = new ImageView();
        iv.setFitWidth(60);
        iv.setFitHeight(60);
        iv.setPreserveRatio(false);
        try {
            Image img = new Image(getClass().getResourceAsStream("/IMG/avatar.png"));
            iv.setImage(img);
        } catch (Exception e) {
            System.out.println("Không load được ảnh: " + e.getMessage());
            // Tạo hình ảnh mặc định
            thumb.setStyle("-fx-background-color: #e9ecef; -fx-background-radius: 10 0 0 10;");
            Label defaultLabel = new Label("HD");
            defaultLabel.setStyle("-fx-text-fill: #666; -fx-font-weight: bold;");
            thumb.getChildren().add(defaultLabel);
        }
        if (thumb.getChildren().isEmpty()) {
            thumb.getChildren().add(iv);
        }

        // Thông tin
        VBox info = new VBox(2);
        Label lblMa = new Label(hd.getMaHD());
        lblMa.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #333;");

        String sdt = "Không có";
        if (hd.getKhachHang() != null && hd.getKhachHang().getSdt() != null) {
            sdt = hd.getKhachHang().getSdt();
        }
        Label lblPhone = new Label("SĐT: " + sdt);
        lblPhone.setStyle("-fx-text-fill: #666; -fx-font-size: 12px;");

        // Hiển thị thêm thông tin
        String tenKH = "Không có";
        if (hd.getKhachHang() != null && hd.getKhachHang().getTenKhachHang() != null) {
            tenKH = hd.getKhachHang().getTenKhachHang();
        }
        Label lblTen = new Label("Tên: " + tenKH);
        lblTen.setStyle("-fx-text-fill: #666; -fx-font-size: 12px;");

        info.getChildren().addAll(lblMa, lblTen, lblPhone);
        HBox.setHgrow(info, javafx.scene.layout.Priority.ALWAYS);

        // Trạng thái
        String trangThaiText = getTrangThaiText(hd.getTrangthai());
        String trangThaiStyle = getTrangThaiStyle(hd.getTrangthai());
        Label lblTrangThai = new Label(trangThaiText);
        lblTrangThai.setStyle(trangThaiStyle);

        card.getChildren().addAll(thumb, info, lblTrangThai);

        // Sự kiện click
        card.setOnMouseClicked(e -> {
            System.out.println("🎯 Click vào hóa đơn: " + hd.getMaHD());
            clearSelectedStyles();

            // Đánh dấu card được chọn
            card.setStyle("-fx-background-color: #007bff; -fx-border-color: #0056b3; -fx-border-radius: 8; -fx-background-radius: 8;");

            // Đổi màu chữ trong card
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
                                // Reset style cho các label thông tin
                                if (((Label) label).getText().startsWith("HD")) {
                                    ((Label) label).setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #333;");
                                } else {
                                    ((Label) label).setStyle("-fx-text-fill: #666; -fx-font-size: 12px;");
                                }
                            }
                        }
                    } else if (child instanceof Label) {
                        // Reset style cho label trạng thái
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

    private void hienThiThongTinChiTiet(HoaDon hd) {
        if (hd == null) return;

        System.out.println("📋 Hiển thị chi tiết hóa đơn: " + hd.getMaHD());

        // Hiển thị thông tin cơ bản
        if (txtMaHoaDon != null) txtMaHoaDon.setText(hd.getMaHD());

        KhachHang kh = hd.getKhachHang();

        // Nếu hoa don chưa có thông tin KhachHang, thử tải từ DB bằng maKH (dùng getById instance)
        if (kh == null) {
            try {
                // Thử lấy maKH nếu entity HoaDon có getter tương ứng
                String maKH = null;
                try {
                    Method m = hd.getClass().getMethod("getMaKH");
                    Object obj = m.invoke(hd);
                    if (obj != null) maKH = obj.toString();
                } catch (NoSuchMethodException nsme) {
                    // entity không có getMaKH, bỏ qua
                }

                if (maKH != null && !maKH.trim().isEmpty()) {
                    kh = khachHangDAO.getById(maKH);
                } else {
                    // nếu chưa có maKH nhưng hd.getKhachHang() null, không làm gì
                }
            } catch (Exception ex) {
                System.out.println("Không tải được KhachHang từ DAO: " + ex.getMessage());
            }
        }

        if (kh != null) {
            if (txtTenKH != null) txtTenKH.setText(kh.getTenKhachHang() != null ? kh.getTenKhachHang() : "—");
            if (txtSDTChiTiet != null) txtSDTChiTiet.setText(kh.getSdt() != null ? kh.getSdt() : "—");
        } else {
            if (txtTenKH != null) txtTenKH.setText("—");
            if (txtSDTChiTiet != null) txtSDTChiTiet.setText("—");
        }

        // Thông tin bàn và khu vực
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

        // Thông tin sự kiện
        if (txtSuKien != null) {
            try {
                txtSuKien.setText(hd.getSuKien() != null && hd.getSuKien().getTenSK() != null ?
                        hd.getSuKien().getTenSK() : "Không có");
            } catch (Exception ex) {
                txtSuKien.setText("Không có");
            }
        }

        // Số lượng khách
        if (txtSoLuong != null) {
            try {
                txtSoLuong.setText(String.valueOf(hd.getSoLuong()));
            } catch (Exception ex) {
                txtSoLuong.setText("0");
            }
        }

        // Mô tả/ghi chú
        if (txtMoTa != null) {
            try {
                // Nếu entity có phương thức getGhiChu()
                Method m = null;
                try {
                    m = hd.getClass().getMethod("getGhiChu");
                } catch (NoSuchMethodException nsme) {
                    m = null;
                }
                if (m != null) {
                    Object val = m.invoke(hd);
                    txtMoTa.setText(val != null ? val.toString() : "");
                } else {
                    txtMoTa.setText("");
                }
            } catch (Exception ex) {
                txtMoTa.setText("");
            }
        }

        // Tải chi tiết hóa đơn
        loadChiTietDonHang(hd.getMaHD());
    }

    private void loadChiTietDonHang(String maHD) {
        chiTietHoaDonData.clear();

        if (maHD == null || maHD.trim().isEmpty()) {
            return;
        }

        try {
            List<ChiTietHoaDon> dsChiTiet = chiTietHDDAO.getByMaHD(maHD);
            if (dsChiTiet != null && !dsChiTiet.isEmpty()) {
                chiTietHoaDonData.addAll(dsChiTiet);
                System.out.println("✅ Đã tải " + dsChiTiet.size() + " chi tiết hóa đơn");

                // Debug: in ra chi tiết
                for (ChiTietHoaDon ct : dsChiTiet) {
                    System.out.println("   - " + (ct.getMon() != null ? ct.getMon().getTenMon() : "null") +
                            " x " + ct.getSoLuong() + " = " + ct.getThanhTien());
                }
            } else {
                System.out.println("ℹ️ Không có chi tiết hóa đơn cho mã: " + maHD);
            }
        } catch (Exception e) {
            System.err.println("❌ Lỗi khi tải chi tiết hóa đơn: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void timKiemHoaDon() {
        String maBan = txtMaBan != null ? txtMaBan.getText().trim() : "";
        String sdt = txtSDT != null ? txtSDT.getText().trim() : "";
        String khuVuc = cboKhuVuc != null && cboKhuVuc.getValue() != null ? cboKhuVuc.getValue() : "Tất cả";

        LocalDate ngay = null;
        if (dpThoiGian != null) {
            ngay = dpThoiGian.getValue(); // null nếu chưa chọn
        }

        System.out.println("🔍 Tìm kiếm với: Mã bàn=" + maBan + ", SDT=" + sdt + ", Khu vực=" + khuVuc + ", Ngày=" + ngay);

        try {
            List<HoaDon> allHD = HoaDonDAO.getAll();
            List<HoaDon> ketQua = new java.util.ArrayList<>();

            for (HoaDon hd : allHD) {
                boolean match = true;

                // Lọc theo mã bàn
                if (!maBan.isEmpty()) {
                    if (hd.getBan() == null || hd.getBan().getMaBan() == null ||
                            !hd.getBan().getMaBan().toLowerCase().contains(maBan.toLowerCase())) {
                        match = false;
                    }
                }

                // Lọc theo số điện thoại
                if (!sdt.isEmpty()) {
                    String sdtHD = null;
                    if (hd.getKhachHang() != null && hd.getKhachHang().getSdt() != null) {
                        sdtHD = hd.getKhachHang().getSdt();
                    } else {
                        // nếu hd chưa có khachHang, thử lấy maKH và truy vấn dao
                        try {
                            String maKH = null;
                            try {
                                Method m = hd.getClass().getMethod("getMaKH");
                                Object obj = m.invoke(hd);
                                if (obj != null) maKH = obj.toString();
                            } catch (NoSuchMethodException nsme) {
                                maKH = null;
                            }
                            if (maKH != null && !maKH.isEmpty()) {
                                KhachHang kh = khachHangDAO.getById(maKH);
                                if (kh != null) sdtHD = kh.getSdt();
                            }
                        } catch (Exception ex) {
                            // ignore
                        }
                    }

                    if (sdtHD == null || !sdtHD.contains(sdt)) {
                        match = false;
                    }
                }

                // Lọc theo khu vực
                if (!khuVuc.equals("Tất cả")) {
                    if (hd.getBan() == null || hd.getBan().getKhuVuc() == null ||
                            hd.getBan().getKhuVuc().getTenKhuVuc() == null ||
                            !hd.getBan().getKhuVuc().getTenKhuVuc().equals(khuVuc)) {
                        match = false;
                    }
                }

                // Lọc theo ngày (sử dụng reflection để đọc trường ngày của HoaDon - hỗ trợ LocalDate, LocalDateTime, java.util.Date, String)
                if (ngay != null) {
                    try {
                        Object tgObj = null;
                        Method m = null;
                        try {
                            m = hd.getClass().getMethod("getTgCheckin");
                        } catch (NoSuchMethodException nsme) {
                            try {
                                m = hd.getClass().getMethod("getTgCheckout");
                            } catch (NoSuchMethodException ex2) {
                                m = null;
                            }
                        }
                        if (m != null) {
                            tgObj = m.invoke(hd);
                        }

                        LocalDate ngayHD = null;
                        if (tgObj == null) {
                            // không có thông tin thời gian -> coi như không match
                            ngayHD = null;
                        } else if (tgObj instanceof LocalDate) {
                            ngayHD = (LocalDate) tgObj;
                        } else if (tgObj instanceof LocalDateTime) {
                            ngayHD = ((LocalDateTime) tgObj).toLocalDate();
                        } else if (tgObj instanceof Date) {
                            ngayHD = ((Date) tgObj).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                        } else {
                            // nếu là String, thử parse bằng dtf hoặc ISO
                            String s = tgObj.toString();
                            try {
                                ngayHD = LocalDate.parse(s, dtf);
                            } catch (Exception ex) {
                                try {
                                    ngayHD = LocalDate.parse(s); // ISO fallback
                                } catch (Exception ex2) {
                                    ngayHD = null;
                                }
                            }
                        }

                        if (ngayHD == null || !ngayHD.equals(ngay)) {
                            match = false;
                        }
                    } catch (Exception ex) {
                        // nếu không thể đọc trường ngày, bỏ qua bộ lọc ngày (hoặc bạn có thể set match=false)
                        System.out.println("Không thể lấy ngày từ HoaDon bằng reflection: " + ex.getMessage());
                        match = false;
                    }
                }

                if (match) {
                    ketQua.add(hd);
                }
            }

            dsHoaDon.clear();
            dsHoaDon.addAll(ketQua);
            hienThiDanhSachHoaDon();

            if (ketQua.isEmpty()) {
                hienThiThongBao("Không tìm thấy hóa đơn nào phù hợp với điều kiện tìm kiếm");
            } else {
                hienThiThongBao("Tìm thấy " + ketQua.size() + " hóa đơn phù hợp");
            }

        } catch (Exception e) {
            e.printStackTrace();
            hienThiThongBaoLoi("Lỗi khi tìm kiếm hóa đơn: " + e.getMessage());
        }
    }

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
