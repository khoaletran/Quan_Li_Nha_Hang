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
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ui.AlertCus;
import ui.ConfirmCus;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
    @FXML private TextField searchField;
    @FXML private Button btnSearch;

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
    private final HoaDonDAO hoaDonDAO = new HoaDonDAO();
    private final KhachHangDAO khachHangDAO = new KhachHangDAO();
    private final ChiTietHDDAO chiTietHDDAO = new ChiTietHDDAO();
    private final MonDAO monDAO = new MonDAO();

    private List<HoaDon> dsDatTruoc = new ArrayList<>();
    private List<HoaDon> dsDaNhan = new ArrayList<>();
    private HoaDon hoaDonSelected = null;
    private ObservableList<ChiTietHoaDon> chiTietHoaDonData = FXCollections.observableArrayList();

    // số lượng gốc khi load từ DB, khóa theo maMon
    private final java.util.Map<String, Integer> soLuongGocMap = new java.util.HashMap<>();

    // danh sách món toàn bộ món để tìm kiếm/hiển thị
    private List<Mon> dsMonToanBo = new ArrayList<>();

    private final NumberFormat nf = NumberFormat.getInstance(new Locale("vi","VN"));

    @FXML
    public void initialize() {
        System.out.println("QLDatBanController initialized");

        if (!ketNoiDatabase()) {
            AlertCus.show("Thông Báo", "Không thể kết nối database.");
            return;
        }

        txtSoLuongKhach.setEditable(false);

        khoiTaoComboBox();
        ganSuKienChoNut();
        taiDanhSachDatTruoc();
        taiDanhSachDaNhan();

        khoiTaoChonMon();   // load ds món, combo loại, search

        resetForm();
        showDanhSachMode();

        if (back != null) {
            back.setOnAction(e -> showDanhSachMode());
        }
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
        if (eventCombo != null) {
            eventCombo.getItems().clear();
            eventCombo.getItems().addAll("Sinh Nhật", "Họp Mặt", "Tiệc Cưới");
            eventCombo.setValue(null);
        }
    }

    private void ganSuKienChoNut() {
        if (btnXacNhan != null) btnXacNhan.setOnAction(e -> xacNhanDatBan());
        if (btnHuyBan != null) btnHuyBan.setOnAction(e -> huyDatBan());
        if (btnSearch != null && searchField != null) {
            btnSearch.setOnAction(e -> timKiemMon());
            searchField.setOnAction(e -> timKiemMon()); // Enter -> tìm
        }
    }

    //tải danh sách đặt trước / đã nhận
    private void taiDanhSachDatTruoc() {
        try {
            List<HoaDon> listHD = HoaDonDAO.getAll();
            dsDatTruoc.clear();
            if (listHD != null) {
                for (HoaDon hd : listHD) {
                    if (hd.getTrangthai() == 0) {
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
                    if (hd.getTrangthai() == 1) {
                        dsDaNhan.add(hd);
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
                    // xóa khỏi DB
                    chiTietHDDAO.delete(ctCu.getHoaDon().getMaHD(), ctCu.getMon().getMaMon());
                }
            }

            // cập nhật / insert các món còn lại trên UI
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



    @FXML
    private void huyDatBan() {
        if (hoaDonSelected == null) {
            AlertCus.show("Thông Báo", "Vui lòng chọn hóa đơn cần hủy");
            return;
        }
        //Kiểm tra
        if (hoaDonSelected.getTrangthai() != 0) {
            AlertCus.show("Thông Báo", "Chỉ có hóa đơn đang đặt trước mới được hủy");
            return;
        }

//        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
//        confirm.setTitle("Xác nhận hủy");
//        confirm.setHeaderText("Bạn có chắc chắn muốn hủy đặt bàn này?");
//        confirm.setContentText("Hóa đơn: " + hoaDonSelected.getMaHD());
//
//        Optional<ButtonType> res = confirm.showAndWait(); res.isPresent() && res.get() == ButtonType.OK
        boolean answer = ConfirmCus.show("Xác nhận hủy đơn", "Bạn có chắc muốn hủy đơn đặt bàn này?");
        if (answer) {
            try {
                System.out.println("Hủy đặt bàn: " + hoaDonSelected.getMaHD());
                boolean ok = HoaDonDAO.delete(hoaDonSelected.getMaHD());
                if (ok) {;
                    AlertCus.show("Thông Báo", "Hủy đặt bàn thành công");
                    dsDatTruoc.remove(hoaDonSelected);
                    dsDaNhan.remove(hoaDonSelected);
                    hienThiDanhSachDatTruoc();
                    hienThiDanhSachDaNhan();
                    resetForm();
                } else {
                    AlertCus.show("Thông Báo", "Hủy thất bại");
                }
            } catch (Exception e) {
                e.printStackTrace();
                AlertCus.show("Thông Báo", "Lỗi hệ thống: " + e.getMessage());
            }
        }
    }
    private void khoiTaoChonMon() {
        try {
            dsMonToanBo = monDAO.getAll();
        } catch (Exception ex) {
            dsMonToanBo = new ArrayList<>();
            ex.printStackTrace();
        }

        hienThiDanhSachMon(dsMonToanBo);

        loadComboDanhMuc();
        loadDanhSachMon();
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
            Image img = new Image(getClass().getResourceAsStream("/IMG/food/restaurant.png"));
            iv.setImage(img);
        } catch (Exception ex) {
            // bỏ qua nếu không load được ảnh
        }
        imageWrapper.getChildren().add(iv);

        // nút thêm nhỏ góc trên
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
            int slMoi = soLuongDaChon + 1;      // chắc chắn <= tonKho
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
        // cập nhật thanhTien cho từng chi tiết (phòng trường hợp giá thay đổi)
        for (ChiTietHoaDon ct : chiTietHoaDonData) {
            if (ct.getMon() != null) {
                ct.setThanhTien(ct.getMon().getGiaBanTaiLucLapHD(hoaDonSelected) * ct.getSoLuong());
            }
        }

        // tính tổng và cập nhật vào HoaDon (nếu bạn lưu tongTienTruoc/tongTienSau)
        double tong = 0;
        for (ChiTietHoaDon ct : chiTietHoaDonData) tong += ct.getThanhTien();
        System.out.println("Tổng đơn hàng hiện tại: " + nf.format(tong) + " VNĐ");

    }

    private void timKiemMon() {
        String keyword = searchField != null ? searchField.getText().trim().toLowerCase() : "";
        if (keyword.isEmpty()) {
            hienThiDanhSachMon(dsMonToanBo);
            return;
        }

        List<Mon> ketQua = new ArrayList<>();
        for (Mon m : dsMonToanBo) {
            if (m.getTenMon() != null && m.getTenMon().toLowerCase().contains(keyword)) {
                ketQua.add(m);
            } else if (m.getMaMon() != null && m.getMaMon().toLowerCase().contains(keyword)) {
                ketQua.add(m);
            }
        }
        hienThiDanhSachMon(ketQua);
    }
    //dùng khi khởi tạo controller, sau khi hủy hóa đơn và khi phương thức resetData chạy
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

        // Tên món
        Label lblTen = new Label(mon.getTenMon());
        lblTen.getStyleClass().addAll("order-col", "product");
        lblTen.setWrapText(true);
        lblTen.setMaxWidth(Double.MAX_VALUE);
        lblTen.setStyle("-fx-font-weight: bold; -fx-font-size: 13.5px; -fx-text-fill: #333;");

        // Hàng dưới
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

        // sự kiện
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

        // không cho giảm dưới số lượng gốc khi hóa đơn đã nhận
        if (hoaDonSelected != null && hoaDonSelected.getTrangthai() == 1 && current <= soLuongGoc) {
            AlertCus.show("Thông Báo", "Không thể giảm thêm. Đây là số lượng đã đặt trước.");
            return;
        }

        if (current <= 1) {
            // nếu là món mới (gốc = 0) thì cho xóa hẳn bằng -1
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

        // nếu là món đã có từ trước và hóa đơn đã nhận -> không cho xóa
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

        // mục "Tất cả món"
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

    private void loadDanhSachMon() {
        locMonTheoTenVaLoai(); // keyword rỗng + "ALL" ⇒ hiển thị tất cả
    }

    /**
     * Lọc theo tên (tfTimKiem) + loại (comboDanhMuc) và vẽ card vào flowMonAn
     */
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
            flowMonAn.getChildren().add(taoCardMon(m));
        }
    }

    /**
     * Card món ở center: ảnh lớn + tên + giá + nút "+"
     * Nhấn card hoặc "+" đều gọi themMonVaoDon(m)
     */
    private VBox taoCardMon(Mon m) {
        VBox card = new VBox(8);
        card.getStyleClass().add("menu-card");
        card.setAlignment(javafx.geometry.Pos.CENTER);
        card.setPrefWidth(160);
        card.setPrefHeight(180);
        card.setCursor(Cursor.HAND);

        // ảnh
        StackPane imageWrapper = new StackPane();
        imageWrapper.setPrefSize(150, 110);

        ImageView imageView = new ImageView();
        imageView.setFitWidth(150);
        imageView.setFitHeight(110);
        imageView.setPreserveRatio(true);

        try {
            String file = (m.getHinhAnh() != null ? m.getHinhAnh().replaceFirst("^/", "") : "restaurant.png");
            String path = "/IMG/food/" + file;
            Image img = new Image(getClass().getResourceAsStream(path));
            imageView.setImage(img);
        } catch (Exception e) {
            try {
                Image img = new Image(getClass().getResourceAsStream("/IMG/food/restaurant.png"));
                imageView.setImage(img);
            } catch (Exception ignore) { }
        }

        imageWrapper.getChildren().add(imageView);

        // nút "+"
        Button btnAdd = new Button("+");
        btnAdd.getStyleClass().add("add-icon");
        StackPane.setAlignment(btnAdd, javafx.geometry.Pos.TOP_RIGHT);
        imageWrapper.getChildren().add(btnAdd);

        Label lblTen = new Label(m.getTenMon());
        lblTen.getStyleClass().add("menu-item-name");
        lblTen.setWrapText(true);

        Label lblGia = new Label(  "SL: " + m.getSoLuong() + " - " + formatCurrency( m.getGiaBan() ));
        lblGia.getStyleClass().add("menu-item-price");

        card.getChildren().addAll(imageWrapper, lblTen, lblGia);

        // sự kiện: giống chọn món cũ
        btnAdd.setOnAction(e -> themMonVaoDon(m));
        card.setOnMouseClicked(e -> themMonVaoDon(m));

        return card;
    }

}
