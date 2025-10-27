package ui.controllers;

import connectDB.connectDB;
import dao.HoaDonDAO;
import dao.KhachHangDAO;
import dao.ChiTietHDDAO;
import dao.MonDAO;
import entity.HoaDon;
import entity.KhachHang;
import entity.ChiTietHoaDon;
import entity.Mon;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

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

    // FXML - bảng đơn hàng
    @FXML private TableView<ChiTietHoaDon> orderTable;
    @FXML private TableColumn<ChiTietHoaDon, Integer> colSTT;
    @FXML private TableColumn<ChiTietHoaDon, String> colSanPham;
    @FXML private TableColumn<ChiTietHoaDon, Integer> colSoLuong;
    @FXML private TableColumn<ChiTietHoaDon, Double> colGia;
    @FXML private TableColumn<ChiTietHoaDon, Double> colTong;
    @FXML private TableColumn<ChiTietHoaDon, Void> colXoa;

    // FXML - tìm kiếm
    @FXML private TextField searchField;
    @FXML private Button btnSearch;

    // FXML - nút hành động
    @FXML private Button btnXacNhan;
    @FXML private Button btnHuyBan;

    // BIẾN TOÀN CỤC
    private final HoaDonDAO hoaDonDAO = new HoaDonDAO();
    private final KhachHangDAO khachHangDAO = new KhachHangDAO();
    private final ChiTietHDDAO chiTietHDDAO = new ChiTietHDDAO();
    private final MonDAO monDAO = new MonDAO();

    private List<HoaDon> dsDatTruoc = new ArrayList<>();
    private List<HoaDon> dsDaNhan = new ArrayList<>();
    private HoaDon hoaDonSelected = null;
    private ObservableList<ChiTietHoaDon> chiTietHoaDonData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        System.out.println("QLDatBanController initialized");

        if (!ketNoiDatabase()) {
            hienThiThongBaoLoi("Không thể kết nối database. Vui lòng kiểm tra kết nối.");
            return;
        }

        khoiTaoComboBox();
        ganSuKienChoNut();
        khoiTaoTableView();
        taiDanhSachDatTruoc();
        taiDanhSachDaNhan();

        resetForm();
    }

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

    private void khoiTaoComboBox() {
        if (eventCombo != null) {
            eventCombo.getItems().clear();
            eventCombo.getItems().addAll("Sinh Nhật", "Họp Mặt", "Tiệc Cưới");
            eventCombo.setValue(null);
        }
    }

    private void ganSuKienChoNut() {
        if (btnXacNhan != null) btnXacNhan.setOnAction(e -> xacNhanDatBan());
        if (btnHuyBan != null) btnHuyBan.setOnAction(e -> huyDatBan());
        if (btnSearch != null && searchField != null) btnSearch.setOnAction(e -> timKiemMonAn(searchField.getText()));
    }

    private void khoiTaoTableView() {
        // Thiết lập các column cho TableView
        colSTT.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(chiTietHoaDonData.indexOf(cellData.getValue()) + 1).asObject());

        colSanPham.setCellValueFactory(cellData -> {
            Mon mon = cellData.getValue().getMon();
            return new SimpleStringProperty(mon != null ? mon.getTenMon() : "Không xác định");
        });

        colSoLuong.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getSoLuong()).asObject());

        colGia.setCellValueFactory(cellData -> {
            Mon mon = cellData.getValue().getMon();
            return new SimpleDoubleProperty(mon != null ? mon.getGiaBan() : 0).asObject();
        });

        colTong.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(cellData.getValue().getThanhTien()).asObject());

        // Column xóa - thêm nút xóa
        colXoa.setCellFactory(param -> new TableCell<ChiTietHoaDon, Void>() {
            private final Button btnXoa = new Button("🗑");

            {
                btnXoa.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-font-size: 10px;");
                btnXoa.setOnAction(event -> {
                    ChiTietHoaDon ct = getTableView().getItems().get(getIndex());
                    xoaChiTietHoaDon(ct);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btnXoa);
                }
            }
        });

        // Gán dữ liệu cho TableView
        orderTable.setItems(chiTietHoaDonData);
    }

    private void xoaChiTietHoaDon(ChiTietHoaDon chiTiet) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận xóa");
        confirm.setHeaderText("Bạn có chắc muốn xóa món này?");
        confirm.setContentText("Món: " + (chiTiet.getMon() != null ? chiTiet.getMon().getTenMon() : ""));

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            chiTietHoaDonData.remove(chiTiet);
            hienThiThongBao("Đã xóa món khỏi đơn hàng");
        }
    }

    private void taiDanhSachDatTruoc() {
        try {
            List<HoaDon> listHD = HoaDonDAO.getAll();
            dsDatTruoc.clear();
            if (listHD != null) {
                for (HoaDon hd : listHD) {
                    System.out.println("🔍 Hóa đơn: " + hd.getMaHD() +
                            ", Trạng thái: " + hd.getTrangthai() +
                            ", Kiểu đặt bàn: " + hd.isKieuDatBan() +
                            ", Khách hàng: " + (hd.getKhachHang() != null ? hd.getKhachHang().getTenKhachHang() : "null") +
                            ", soLuong=" + (hd.getSoLuong()) );
                    if (hd.getTrangthai() == 0 && hd.isKieuDatBan()) {
                        dsDatTruoc.add(hd);
                    }
                }
            }
            hienThiDanhSachDatTruoc();
        } catch (Exception ex) {
            System.err.println("Lỗi khi tải ds đặt trước: " + ex.getMessage());
            ex.printStackTrace();
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
            Image img = new Image(getClass().getResourceAsStream("/IMG/avatar.png"));
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

        System.out.println("📋 Hiển thị chi tiết hóa đơn: " + hd.getMaHD());

        if (lblMaHoaDon != null) lblMaHoaDon.setText(hd.getMaHD());

        KhachHang kh = hd.getKhachHang();
        if (kh != null) {
            if (lblHoTen != null) lblHoTen.setText(kh.getTenKhachHang());
            if (lblSDT != null) lblSDT.setText(kh.getSdt());
        } else {
            if (lblHoTen != null) lblHoTen.setText("Chưa có thông tin");
            if (lblSDT != null) lblSDT.setText("Chưa có thông tin");
            System.out.println("⚠️ Không có thông tin khách hàng");
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

        if (maHD == null || maHD.trim().isEmpty()) {
            return;
        }

        try {
            List<ChiTietHoaDon> dsChiTiet = chiTietHDDAO.getByMaHD(maHD);
            if (dsChiTiet != null && !dsChiTiet.isEmpty()) {
                chiTietHoaDonData.addAll(dsChiTiet);
                System.out.println("✅ Đã tải " + dsChiTiet.size() + " chi tiết hóa đơn");
            } else {
                System.out.println("ℹ️ Không có chi tiết hóa đơn cho mã: " + maHD);
            }
        } catch (Exception e) {
            System.err.println("❌ Lỗi khi tải chi tiết hóa đơn: " + e.getMessage());
            e.printStackTrace();
        }
    }

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

    private void resetForm() {
        hoaDonSelected = null;
        if (lblMaHoaDon != null) lblMaHoaDon.setText("");
        if (lblHoTen != null) lblHoTen.setText("");
        if (lblSDT != null) lblSDT.setText("");
        if (lblBan != null) lblBan.setText("");
        if (eventCombo != null) eventCombo.setValue(null);
        if (txtSoLuongKhach != null) txtSoLuongKhach.clear();
        if (foodList != null) foodList.getChildren().clear();

        chiTietHoaDonData.clear();

        clearSelectedStyles(danhSachDatTruoc);
        clearSelectedStyles(danhSachDaNhan);
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