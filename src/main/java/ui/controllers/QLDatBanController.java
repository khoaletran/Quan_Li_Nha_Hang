package ui.controllers;

import connectDB.connectDB;
import dao.HoaDonDAO;
import dao.KhachHangDAO;
import entity.HoaDon;
import entity.KhachHang;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.geometry.Insets;
import javafx.scene.Cursor;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class QLDatBanController {

    // FXML - danh sách
    @FXML private VBox danhSachDatTruoc;
    @FXML private VBox danhSachDaNhan;
    @FXML private FlowPane foodList;

    // FXML - thông tin chi tiết
    @FXML private Label lblMaHoaDon;
    @FXML private Label lblHoTen;
    @FXML private Label lblSDT;
    @FXML private Label lblBan;
    @FXML private TextField txtSoLuongKhach;
    @FXML private ComboBox<String> eventCombo;

    // FXML - tìm kiếm
    @FXML private TextField searchField;
    @FXML private Button btnSearch;

    // FXML - nút hành động
    @FXML private Button btnXacNhan;
    @FXML private Button btnHuyBan;

    // BIẾN TOÀN CỤC
    private final HoaDonDAO hoaDonDAO = new HoaDonDAO();
    private final KhachHangDAO khachHangDAO = new KhachHangDAO();
    private List<HoaDon> dsDatTruoc = new ArrayList<>();
    private List<HoaDon> dsDaNhan = new ArrayList<>();
    private HoaDon hoaDonSelected = null;
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    @FXML
    public void initialize() {
        System.out.println("QLDatBanController initialized");

        // KẾT NỐI DATABASE TRƯỚC KHI THAO TÁC
        if (!ketNoiDatabase()) {
            hienThiThongBaoLoi("Không thể kết nối database. Vui lòng kiểm tra kết nối.");
            return;
        }

        khoiTaoComboBox();
        ganSuKienChoNut();
        taiDanhSachDatTruoc();
        taiDanhSachDaNhan();

        resetForm();
    }

    // PHƯƠNG THỨC KẾT NỐI DATABASE
    private boolean ketNoiDatabase() {
        try {
            connectDB.getInstance().connect();
            System.out.println("✅ Kết nối database thành công");
            return true;
        } catch (Exception e) {
            System.err.println("Lỗi kết nối database: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Khởi tạo combobox và giá trị mặc định
    private void khoiTaoComboBox() {
        if (eventCombo != null) {
            eventCombo.getItems().clear();
            eventCombo.getItems().addAll("Sinh Nhật", "Họp Mặt", "Tiệc Cưới");
            eventCombo.setValue(null);
        }
    }

    // Gán sự kiện cho các nút
    private void ganSuKienChoNut() {
        if (btnXacNhan != null) btnXacNhan.setOnAction(e -> xacNhanDatBan());
        if (btnHuyBan != null) btnHuyBan.setOnAction(e -> huyDatBan());
        if (btnSearch != null && searchField != null) btnSearch.setOnAction(e -> timKiemMonAn(searchField.getText()));
    }

    // Tải danh sách từ database - CHI TIẾT DEBUG
    private void taiDanhSachDatTruoc() {
        try {
            List<HoaDon> listHD = HoaDonDAO.getAll();
//            System.out.println("📊 Tổng số hóa đơn từ DB: " + (all != null ? all.size() : 0));

            dsDatTruoc.clear();
            if (listHD != null) {
                for (HoaDon hd : listHD) {
                    // DEBUG: In thông tin từng hóa đơn
                    System.out.println("🔍 Hóa đơn: " + hd.getMaHD() +
                            ", Trạng thái: " + hd.getTrangthai() +
                            ", Kiểu đặt bàn: " + hd.isKieuDatBan() +
                            ", Khách hàng: " + (hd.getKhachHang() != null ? hd.getKhachHang().getTenKhachHang() : "null"));

                    if (hd.getTrangthai() == 0 && hd.isKieuDatBan()) {
                        dsDatTruoc.add(hd);
//                        System.out.println("THÊM vào ds đặt trước: " + hd.getMaHD());
                    }
                }
            }
            hienThiDanhSachDatTruoc();
//            System.out.println("🎯 Số lượng đặt trước: " + dsDatTruoc.size());
        } catch (Exception ex) {
            System.err.println("Lỗi khi tải ds đặt trước: " + ex.getMessage());
            ex.printStackTrace();
//            hienThiDuLieuMauDatTruoc();
        }
    }

    private void taiDanhSachDaNhan() {
        try {
            List<HoaDon> listHD = HoaDonDAO.getAll();
            dsDaNhan.clear();
            if (listHD != null) {
                for (HoaDon hd : listHD) {
                    if (hd.getTrangthai() == 1 && hd.isKieuDatBan()) {
                        dsDaNhan.add(hd);
                        System.out.println("THÊM vào ds đã nhận: " + hd.getMaHD());
                    }
                }
            }
            hienThiDanhSachDaNhan();
//            System.out.println("🎯 Số lượng đã nhận: " + dsDaNhan.size());
        } catch (Exception ex) {
            System.err.println("❌ Lỗi khi tải ds đã nhận: " + ex.getMessage());
            ex.printStackTrace();
//            hienThiDuLieuMauDaNhan();
        }
    }

    // Hiển thị danh sách vào VBox
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

    // Tạo card cho mỗi hóa đơn
    private HBox taoCardHoaDon(HoaDon hd) {
        HBox card = new HBox(10);
        card.getStyleClass().add("invoice-card");
        card.setPadding(new Insets(8));
        card.setCursor(Cursor.HAND);
        card.setPrefHeight(80);
        card.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #dee2e6; -fx-border-radius: 8; -fx-background-radius: 8;");

        // Ảnh/thumbnail
        StackPane thumb = new StackPane();
        thumb.setStyle("-fx-background-radius: 8; -fx-overflow: hidden;");
        ImageView iv = new ImageView();
        iv.setFitWidth(80);
        iv.setFitHeight(60);
        iv.setPreserveRatio(true);
        try {
            Image img = new Image(getClass().getResourceAsStream("/IMG/avatar.png"));
            iv.setImage(img);
        } catch (Exception e) {
            // Nếu không load được ảnh, tạo background màu
            thumb.setStyle("-fx-background-color: #e9ecef; -fx-background-radius: 8;");
            System.out.println("Không load được ảnh bàn: " + e.getMessage());
        }
        thumb.getChildren().add(iv);

        // Thông tin chính
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

        info.getChildren().addAll(lblMa, lblTen, lblPhone);
        HBox.setHgrow(info, Priority.ALWAYS);

        // Trạng thái
        Label lblTrangThai = new Label(hd.getTrangthai() == 0 ? "Đặt trước" : "Đã nhận");
        lblTrangThai.setStyle(hd.getTrangthai() == 0 ?
                "-fx-text-fill: #e74c3c; -fx-font-weight: bold;" :
                "-fx-text-fill: #27ae60; -fx-font-weight: bold;");

        card.getChildren().addAll(thumb, info, lblTrangThai);

        // Sự kiện click chọn card
        card.setOnMouseClicked(e -> {
            clearSelectedStyles(danhSachDatTruoc);
            clearSelectedStyles(danhSachDaNhan);

            card.setStyle("-fx-background-color: #007bff; -fx-border-color: #0056b3; -fx-border-radius: 8; -fx-background-radius: 8;");

            // Đổi màu chữ khi selected
            for (javafx.scene.Node node : card.getChildren()) {
                if (node instanceof Label) {
                    ((Label) node).setStyle("-fx-text-fill: white;");
                }
            }

            hoaDonSelected = hd;
            hienThiThongTinChiTiet(hd);
        });

        return card;
    }

    private void clearSelectedStyles(VBox box) {
        if (box == null) return;
        for (javafx.scene.Node node : box.getChildren()) {
            if (node instanceof HBox) {
                node.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #dee2e6; -fx-border-radius: 8; -fx-background-radius: 8;");
                // Reset màu chữ
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

    // Hiển thị thông tin chi tiết khi chọn
    private void hienThiThongTinChiTiet(HoaDon hd) {
        if (hd == null) return;

        System.out.println("📋 Hiển thị chi tiết hóa đơn: " + hd.getMaHD());

        if (lblMaHoaDon != null) lblMaHoaDon.setText(hd.getMaHD());

        KhachHang kh = hd.getKhachHang();
        if (kh != null) {
            if (lblHoTen != null) lblHoTen.setText(kh.getTenKhachHang());
            if (lblSDT != null) lblSDT.setText(kh.getSdt());
//            System.out.println("👤 Khách hàng: " + kh.getTenKhachHang() + " - " + kh.getSdt());
        } else {
            if (lblHoTen != null) lblHoTen.setText("Chưa có thông tin");
            if (lblSDT != null) lblSDT.setText("Chưa có thông tin");
            System.out.println("⚠️ Không có thông tin khách hàng");
        }

        if (hd.getBan() != null && lblBan != null) {
            lblBan.setText(hd.getBan().getMaBan());
//            System.out.println("🍽 Bàn: " + hd.getBan().getMaBan());
        } else if (lblBan != null) {
            lblBan.setText("Chưa có thông tin");
//            System.out.println("⚠️ Không có thông tin bàn");
        }

        if (eventCombo != null && hd.getSuKien() != null) {
            eventCombo.setValue(hd.getSuKien().getTenSK());
        }

        // load chi tiết đơn hàng (nếu cần)
        loadChiTietDonHang(hd.getMaHD());
    }

    private void loadChiTietDonHang(String maHD) {
        // TODO: implement nếu có DAO chi tiết
        System.out.println("📦 Load chi tiết đơn hàng: " + maHD);
    }

    // Xác nhận đặt bàn -> chuyển trạng thái sang đã nhận
    @FXML
    private void xacNhanDatBan() {
        if (hoaDonSelected == null) {
            hienThiThongBao("Vui lòng chọn hóa đơn cần xác nhận");
            return;
        }

        try {
            System.out.println("🔄 Xác nhận đặt bàn: " + hoaDonSelected.getMaHD());
            hoaDonSelected.setTrangthai(1);
            boolean ok = HoaDonDAO.update(hoaDonSelected);
            if (ok) {
                hienThiThongBao("✅ Xác nhận đặt bàn thành công");
                // chuyển giữa danh sách
                dsDatTruoc.remove(hoaDonSelected);
                dsDaNhan.add(hoaDonSelected);
                hienThiDanhSachDatTruoc();
                hienThiDanhSachDaNhan();
                resetForm();
            } else {
                hienThiThongBao("❌ Xác nhận thất bại");
            }
        } catch (Exception e) {
            e.printStackTrace();
            hienThiThongBao("❌ Lỗi hệ thống: " + e.getMessage());
        }
    }

    // Hủy đặt bàn
    @FXML
    private void huyDatBan() {
        if (hoaDonSelected == null) {
            hienThiThongBao("Vui lòng chọn hóa đơn cần hủy");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận hủy");
        confirm.setHeaderText("Bạn có chắc chắn muốn hủy đặt bàn này?");
        confirm.setContentText("Hóa đơn: " + hoaDonSelected.getMaHD());

        Optional<ButtonType> res = confirm.showAndWait();
        if (res.isPresent() && res.get() == ButtonType.OK) {
            try {
                System.out.println("🗑️ Hủy đặt bàn: " + hoaDonSelected.getMaHD());
                boolean ok = HoaDonDAO.delete(hoaDonSelected.getMaHD());
                if (ok) {
                    hienThiThongBao("✅ Hủy đặt bàn thành công");
                    dsDatTruoc.remove(hoaDonSelected);
                    dsDaNhan.remove(hoaDonSelected);
                    hienThiDanhSachDatTruoc();
                    hienThiDanhSachDaNhan();
                    resetForm();
                } else {
                    hienThiThongBao("❌ Hủy thất bại");
                }
            } catch (Exception e) {
                e.printStackTrace();
                hienThiThongBao("❌ Lỗi hệ thống: " + e.getMessage());
            }
        }
    }

    // Tìm kiếm món ăn (hiển thị vào foodList)
    private void timKiemMonAn(String keyword) {
        if (foodList == null) return;
        foodList.getChildren().clear();

        if (keyword == null || keyword.trim().isEmpty()) {
            Label info = new Label("Nhập từ khóa để tìm món ăn");
            info.setStyle("-fx-text-fill: #666; -fx-font-style: italic; -fx-padding: 20px;");
            foodList.getChildren().add(info);
            return;
        }

        Label info = new Label("Chức năng tìm kiếm món ăn chưa được triển khai (từ khóa: " + keyword + ")");
        info.setStyle("-fx-text-fill: #666; -fx-font-style: italic; -fx-padding: 20px;");
        foodList.getChildren().add(info);
    }

    // Reset form chi tiết
    private void resetForm() {
        hoaDonSelected = null;
        if (lblMaHoaDon != null) lblMaHoaDon.setText("");
        if (lblHoTen != null) lblHoTen.setText("");
        if (lblSDT != null) lblSDT.setText("");
        if (lblBan != null) lblBan.setText("");
        if (eventCombo != null) eventCombo.setValue(null);
        if (txtSoLuongKhach != null) txtSoLuongKhach.clear();
        if (foodList != null) foodList.getChildren().clear();

        // xóa highlight
        clearSelectedStyles(danhSachDatTruoc);
        clearSelectedStyles(danhSachDaNhan);
    }

    // Hiển thị thông báo nhanh
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

    // Dữ liệu mẫu khi không có DB
    private void hienThiDuLieuMauDatTruoc() {
        try {
            dsDatTruoc.clear();
            HoaDon hd1 = new HoaDon("HD202510270001", 0, 0, 0, 0, 0, true, false, null, null, 1, null, null, null, null, null);
            HoaDon hd2 = new HoaDon("HD202510270002", 0, 0, 0, 0, 0, true, false, null, null, 1, null, null, null, null, null);
            dsDatTruoc.add(hd1);
            dsDatTruoc.add(hd2);
            hienThiDanhSachDatTruoc();
            System.out.println("📋 Đang sử dụng dữ liệu mẫu đặt trước");
        } catch (Exception e) {
            System.err.println("❌ Lỗi dữ liệu mẫu đặt trước: " + e.getMessage());
        }
    }

    private void hienThiDuLieuMauDaNhan() {
        try {
            dsDaNhan.clear();
            HoaDon hd = new HoaDon("HD202510270003", 0, 0, 0, 0, 0, true, false, null, null, 2, null, null, null, null, null);
            dsDaNhan.add(hd);
            hienThiDanhSachDaNhan();
            System.out.println("📋 Đang sử dụng dữ liệu mẫu đã nhận");
        } catch (Exception e) {
            System.err.println("❌ Lỗi dữ liệu mẫu đã nhận: " + e.getMessage());
        }
    }

    // Refresh danh sách (public để gọi từ bên ngoài)
    public void refreshData() {
        try {
            if (!ketNoiDatabase()) {
                hienThiThongBaoLoi("Không thể kết nối database khi refresh");
                return;
            }
            taiDanhSachDatTruoc();
            taiDanhSachDaNhan();
            resetForm();
            hienThiThongBao("✅ Đã làm mới dữ liệu");
        } catch (Exception e) {
            System.err.println("❌ Lỗi khi refresh data: " + e.getMessage());
            hienThiThongBao("❌ Lỗi khi làm mới dữ liệu");
        }
    }
}