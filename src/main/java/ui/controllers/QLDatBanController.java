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
import ui.AlertCus;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.text.NumberFormat;
import java.util.Locale;

public class QLDatBanController {

    //danh sách
    @FXML private VBox danhSachDatTruoc;
    @FXML private VBox danhSachDaNhan;
    @FXML private FlowPane foodList;

    //thông tin chi tiết
    @FXML private Label lblMaHoaDon;
    @FXML private Label lblHoTen;
    @FXML private Label lblSDT;
    @FXML private Label lblBan;
    @FXML private TextField txtSoLuongKhach;
    @FXML private ComboBox<String> eventCombo;

    //bảng đơn hàng
    @FXML private TableView<ChiTietHoaDon> orderTable;
    @FXML private TableColumn<ChiTietHoaDon, Integer> colSTT;
    @FXML private TableColumn<ChiTietHoaDon, String> colSanPham;
    @FXML private TableColumn<ChiTietHoaDon, Integer> colSoLuong;
    @FXML private TableColumn<ChiTietHoaDon, Double> colGia;
    @FXML private TableColumn<ChiTietHoaDon, Double> colTong;
    @FXML private TableColumn<ChiTietHoaDon, Void> colXoa;

    //tìm kiếm
    @FXML private TextField searchField;
    @FXML private Button btnSearch;

    //nút
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

    // danh sách món toàn bộ món để tìm kiếm/hiển thị
    private List<Mon> dsMonToanBo = new ArrayList<>();

    private final NumberFormat nf = NumberFormat.getInstance(new Locale("vi","VN"));

    @FXML
    public void initialize() {
        System.out.println("QLDatBanController initialized");

        if (!ketNoiDatabase()) {
//            hienThiThongBaoLoi("Không thể kết nối database. Vui lòng kiểm tra kết nối.");
            AlertCus.show("Thông Báo", "Không thể kết nối database.");
            return;
        }

        khoiTaoComboBox();
        ganSuKienChoNut();
        khoiTaoTableView();
        taiDanhSachDatTruoc();
        taiDanhSachDaNhan();

        //tao chuc nang chon mon
        khoiTaoChonMon();

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

    private void khoiTaoTableView() {
        //tạo các column cho TableView
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
            return new SimpleDoubleProperty(mon != null ? mon.getGiaBanTaiLucLapHD(hoaDonSelected) : 0).asObject();
        });

        colTong.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(cellData.getValue().getThanhTien()).asObject());

        // Column xóa - thêm nút xóa (giữ icon thùng rác)
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
                if (empty) setGraphic(null);
                else setGraphic(btnXoa);
            }
        });

        //gán dữ liệu cho TableView
        orderTable.setItems(chiTietHoaDonData);
    }

     //hàm xử lý khi nhấn nút xóa sẽ giảm 1 số lượng nếu >1
     //nếu số lượng =1 sẽ xóa hẳn

    private void xoaChiTietHoaDon(ChiTietHoaDon chiTiet) {
        if (chiTiet == null) return;

        int current = chiTiet.getSoLuong();
        if (current > 1) {
            chiTiet.setSoLuong(current - 1);
            double gia = chiTiet.getMon() != null ? chiTiet.getMon().getGiaBanTaiLucLapHD(hoaDonSelected) : 0;
            chiTiet.setThanhTien(gia * (current - 1));
            AlertCus.show("Thông Báo", "Đã giảm 1 số lượng (" + chiTiet.getMon().getTenMon() + ").");
        } else {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Xác nhận xóa món");
            confirm.setHeaderText("Bạn có chắc muốn xóa món này khỏi đơn hàng?");
            confirm.setContentText("Món: " + (chiTiet.getMon() != null ? chiTiet.getMon().getTenMon() : ""));
            Optional<ButtonType> res = confirm.showAndWait();
            if (res.isPresent() && res.get() == ButtonType.OK) {
                chiTietHoaDonData.remove(chiTiet);
                AlertCus.show("Thông Báo", "Đã xóa món khỏi đơn hàng");
            }
        }

        capNhatBangDonHang(); // refresh TableView
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

        if (maHD == null || maHD.trim().isEmpty()) {
            return;
        }

        try {
            List<ChiTietHoaDon> dsChiTiet = chiTietHDDAO.getByMaHD(maHD);
            if (dsChiTiet != null && !dsChiTiet.isEmpty()) {
                chiTietHoaDonData.addAll(dsChiTiet);
                System.out.println("Đã tải " + dsChiTiet.size() + " chi tiết hóa đơn");
            } else {
                System.out.println("Không có chi tiết hóa đơn cho mã: " + maHD);
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi tải chi tiết hóa đơn: " + e.getMessage());
            e.printStackTrace();
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

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận hủy");
        confirm.setHeaderText("Bạn có chắc chắn muốn hủy đặt bàn này?");
        confirm.setContentText("Hóa đơn: " + hoaDonSelected.getMaHD());

        Optional<ButtonType> res = confirm.showAndWait();
        if (res.isPresent() && res.get() == ButtonType.OK) {
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

    // --- CHỌN MÓN / HIỂN THỊ MÓN ---
    private void khoiTaoChonMon() {
        // load dsMon từ DAO
        try {
            dsMonToanBo = monDAO.getAll();
        } catch (Exception ex) {
            dsMonToanBo = new ArrayList<>();
            ex.printStackTrace();
        }

        // Hiển thị toàn bộ
        hienThiDanhSachMon(dsMonToanBo);
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
            Image img = new Image(getClass().getResourceAsStream("/IMG/avatar.png"));
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

        // sự kiện: click vào toàn bộ card cũng thêm món; hoặc click nút +
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

        // kiểm tra món đã có trong chiTietHoaDonData chưa
        ChiTietHoaDon found = null;
        for (ChiTietHoaDon ct : chiTietHoaDonData) {
            if (ct.getMon() != null && m.getMaMon().equals(ct.getMon().getMaMon())) {
                found = ct;
                break;
            }
        }

        if (found != null) {
            // tăng số lượng, cập nhật thanhTien trên object UI
            int sl = found.getSoLuong() + 1;
            found.setSoLuong(sl);
            double gia = found.getMon() != null ? found.getMon().getGiaBanTaiLucLapHD(hoaDonSelected) : 0;
            found.setThanhTien(gia * sl);

            AlertCus.show("Thông Báo", "Đã tăng số lượng cho món " + m.getTenMon());
        } else {
            // tạo ChiTietHoaDon mới và thêm vào ObservableList (UI)
            ChiTietHoaDon ct = new ChiTietHoaDon(hoaDonSelected, m, 1);
            chiTietHoaDonData.add(ct);
            AlertCus.show("Thông Báo", "Đã thêm món: " + m.getTenMon());
        }

        capNhatBangDonHang(); // refresh TableView và tính tổng
    }


    private void capNhatBangDonHang() {
        // cập nhật thanhTien cho từng chi tiết (phòng trường hợp giá thay đổi)
        for (ChiTietHoaDon ct : chiTietHoaDonData) {
            if (ct.getMon() != null) {
                ct.setThanhTien(ct.getMon().getGiaBanTaiLucLapHD(hoaDonSelected) * ct.getSoLuong());
            }
        }

        orderTable.refresh();

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
        orderTable.refresh();

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
    //dùng khi load lại UI và DB
    public void refreshData() {
        try {
            if (!ketNoiDatabase()) {
                hienThiThongBaoLoi("Không thể kết nối database khi refresh");
                return;
            }
            taiDanhSachDatTruoc();
            taiDanhSachDaNhan();
            // reload món
            khoiTaoChonMon();
            resetForm();
            hienThiThongBao("✅ Đã làm mới dữ liệu");
        } catch (Exception e) {
            System.err.println("Lỗi khi refresh data: " + e.getMessage());
            hienThiThongBao("Lỗi khi làm mới dữ liệu");
        }
    }
}
