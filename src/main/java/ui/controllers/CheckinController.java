package ui.controllers;

import dao.ChiTietHDDAO;
import dao.HoaDonDAO;
import dao.KhuVucDAO;
import dao.ThoiGianDoiBanDAO;
import entity.ChiTietHoaDon;
import entity.HoaDon;
import entity.KhuVuc;
import entity.ThoiGianDoiBan;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import ui.AlertCus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class CheckinController {

    @FXML
    private VBox vboxDatTruoc; // VBox cho danh sách bàn đặt trước
    @FXML
    private VBox vboxCho;      // VBox cho danh sách bàn chờ
    @FXML
    private Label lblMaHD, lblTenKH, lblSDT, lblSoLuong, lblSuKien, lblKhuVuc, lblBan;
    @FXML
    private GridPane gridChiTietHD; //grid
    @FXML
    private TextField txtMaHD, txtSDT;
    @FXML
    private DatePicker dpThoiGian;
    @FXML
    private ComboBox<String> cboKhuVuc;

    // 🔹 Biến toàn cục lưu danh sách hóa đơn
    private List<HoaDon> dsHoaDon;

    // 🔹 Biến lưu item đang chọn
    private HBox lastSelected = null;

    @FXML
    public void initialize() {
        loadDanhSach();
        loadComboKhuVuc();
        setupFilterEvents();
    }
    //a
    private void loadComboKhuVuc(){
        cboKhuVuc.getItems().clear();
        cboKhuVuc.getItems().add("Tất cả");
        for(KhuVuc khuVuc: KhuVucDAO.getAll()){
            cboKhuVuc.getItems().add(khuVuc.getTenKhuVuc());
        }
        cboKhuVuc.getSelectionModel().selectFirst();
    }
    private void loadDanhSach() {
        // 1️⃣ Load thời gian đợi bàn 1 lần
        int thoiGianDatTruoc = 0; // cho kieuDatBan = 1
        int thoiGianCho = 0;      // cho kieuDatBan = 0
        try {
            ThoiGianDoiBan tgDatTruoc = ThoiGianDoiBanDAO.getLatestByLoai(true); // đặt trước
            ThoiGianDoiBan tgCho = ThoiGianDoiBanDAO.getLatestByLoai(false);       // chờ
            if (tgDatTruoc != null) thoiGianDatTruoc = tgDatTruoc.getThoiGian();
            if (tgCho != null) thoiGianCho = tgCho.getThoiGian();
        } catch (Exception e) {
            System.err.println("Lỗi load thời gian đợi bàn: " + e.getMessage());
        }

        // 2️⃣ Load danh sách hóa đơn hôm nay
        dsHoaDon = HoaDonDAO.getAllNgayHomNay(); // đã tối ưu: chỉ set ID và tên, không gọi DAO phụ

        // 3️⃣ Xóa vbox trước khi add item
        vboxDatTruoc.getChildren().clear();
        vboxCho.getChildren().clear();

        // 4️⃣ Tạo UI items
        for (HoaDon hd : dsHoaDon) {
            if (hd.getTrangthai() != 0) continue; // chỉ lấy trạng thái 0

            int thoiGian = (hd.isKieuDatBan() == true) ? thoiGianDatTruoc : thoiGianCho;

            HBox item = createBookingItem(hd, thoiGian);

            if (hd.isKieuDatBan() == true) { // đặt trước
                vboxDatTruoc.getChildren().add(item);
            } else { // chờ
                vboxCho.getChildren().add(item);
            }
        }
    }


    private HBox createBookingItem(HoaDon hd, int thoiGianCho) {
        HBox hbox = new HBox(10);
        hbox.getStyleClass().add("booking-item");

        // 🔹 1️⃣ Hình ảnh bàn theo loại
        String imgPath = "/IMG/ban/IN"; // mặc định
        if (hd.getBan() != null && hd.getBan().getMaBan() != null) {
            String tenKhuVuc = hd.getBan().getKhuVuc().getTenKhuVuc();
            if (tenKhuVuc.equals("Indoor")) imgPath = "/IMG/ban/IN.png";
            else if (tenKhuVuc.equals("Outdoor")) imgPath = "/IMG/ban/out.png";
            else if (tenKhuVuc.equals("VIP")) imgPath = "/IMG/ban/vip.png";
        }
        ImageView img = new ImageView(new Image(getClass().getResourceAsStream(imgPath)));
        img.setFitWidth(70);
        img.setFitHeight(60);

        img.setPreserveRatio(false);
        // 🔹 Bo góc
        Rectangle clip = new Rectangle(93, 80);
        clip.setArcWidth(15);   // bán kính bo góc ngang
        clip.setArcHeight(15);  // bán kính bo góc dọc
        img.setClip(clip);

// 🔹 Margin xung quanh (10px ví dụ)
        img.getStyleClass().add("booking-image");

        // 🔹 2️⃣ Info khách hàng
        VBox info = new VBox();
        info.setStyle("-fx-alignment: CENTER_LEFT;");
        info.getStyleClass().add("booking-info");
        Label lblId = new Label(hd.getMaHD());
        lblId.getStyleClass().add("booking-id");
        Label lblPhone = new Label(hd.getKhachHang() != null ? hd.getKhachHang().getSdt() : "-");
        lblPhone.getStyleClass().add("booking-phone");
        info.getChildren().addAll(lblId, lblPhone);

        // 🔹 3️⃣ Thời gian đặt
        VBox dateBox = new VBox();
        dateBox.getStyleClass().add("booking-date");
        String timeStr = (hd.getTgCheckIn() != null)
                ? hd.getTgCheckIn().toLocalTime() + " - " + hd.getTgCheckIn().toLocalDate()
                : "-";
        Label lblDate = new Label(timeStr);
        dateBox.getChildren().add(lblDate);

        // 🔹 4️⃣ Thời gian còn lại
        VBox remainingBox = new VBox();
        remainingBox.setStyle("-fx-alignment: CENTER;");
        remainingBox.getStyleClass().add("booking-remaining");
        Label lblRemaining = new Label();
        remainingBox.getChildren().add(lblRemaining);

        // 🔹 5️⃣ Đếm ngược
        if (hd.getTgCheckIn() != null) {
            LocalDateTime checkInTime = hd.getTgCheckIn();
            long totalSeconds = thoiGianCho * 60;

            javafx.animation.Timeline timeline = new javafx.animation.Timeline(
                    new javafx.animation.KeyFrame(javafx.util.Duration.seconds(1), e -> {
                        LocalDateTime now = LocalDateTime.now();
                        if (now.isBefore(checkInTime)) {
                            lblRemaining.setText("-- : -- : --");
                            remainingBox.setStyle("-fx-background-color: #00C8B3;");
                        } else {
                            long secondsLeft = totalSeconds - Duration.between(checkInTime, now).getSeconds();
                            if (secondsLeft > 0) {
                                long h = secondsLeft / 3600;
                                long m = (secondsLeft % 3600) / 60;
                                long s = secondsLeft % 60;
                                lblRemaining.setText(String.format("%02d:%02d:%02d", h, m, s));
                                remainingBox.setStyle("-fx-background-color: #00C853;");
                            } else {
                                lblRemaining.setText("00:00:00");
                                remainingBox.setStyle("-fx-background-color: #FF3B30;");
                            }
                        }
                    })
            );
            timeline.setCycleCount(javafx.animation.Animation.INDEFINITE);
            timeline.play();
        } else {
            lblRemaining.setText("-");
        }

        hbox.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(info, Priority.ALWAYS);
        remainingBox.setPrefWidth(100);
        remainingBox.setAlignment(Pos.CENTER);
        hbox.getChildren().addAll(img, info, dateBox, remainingBox);

        hbox.setOnMouseClicked(e -> {
            loadThongTinHoaDon(hd);
            highlightSelected(hbox);
        });

        return hbox;
    }



    private void highlightSelected(HBox selected) {
        if (lastSelected != null) lastSelected.setStyle("");
        selected.setStyle("-fx-background-color: #FFE0B2; -fx-background-radius: 10;");
        lastSelected = selected;
    }

    @FXML
    private void checkin() {
        String maHD = lblMaHD.getText();
        if (maHD == null || maHD.isEmpty()) {
            AlertCus.show("Thông báo", "Chưa chọn hóa đơn để check-in!");
            return;
        }

        HoaDon hd = HoaDonDAO.getByID(maHD);
        if (hd == null) {
            AlertCus.show("Lỗi", "Không tìm thấy hóa đơn: " + maHD);
            return;
        }

        LocalDateTime tgDat = hd.getTgCheckIn();
        if (tgDat == null) {
            AlertCus.show("Thông báo", "Hóa đơn chưa có thời gian đặt bàn!");
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        ThoiGianDoiBan tg = ThoiGianDoiBanDAO.getLatestByLoai(hd.isKieuDatBan());
        int thoiGianCho = (tg != null) ? tg.getThoiGian() : 0;
        LocalDateTime tgChoPhep = tgDat.plusMinutes(thoiGianCho);

        if (now.isBefore(tgDat)) {
            AlertCus.show("Thông báo", "Chưa tới giờ check-in!\nGiờ đặt: " + tgDat.toLocalTime());
            return;
        }

        if (now.isAfter(tgChoPhep)) {
            AlertCus.show("Thông báo", "Đã quá hạn check-in!\nHạn cuối: " + tgChoPhep.toLocalTime());

            hd.setTrangthai(3);
            HoaDonDAO.update(hd);
            loadDanhSach();
            return;
        }

        hd.setTrangthai(1);
        hd.setTgCheckIn(now);

        boolean ok = HoaDonDAO.update(hd);
        if (ok) {
            AlertCus.show("Thành công", "Check-in thành công cho hóa đơn " + maHD + "!");
            loadDanhSach();
            clearThongTin();
        } else {
            AlertCus.show("Lỗi", "Không thể cập nhật trạng thái check-in!");
        }
    }

    private void clearThongTin() {
        lblMaHD.setText("");
        lblTenKH.setText("");
        lblSDT.setText("");
        lblSoLuong.setText("");
        lblSuKien.setText("");
        lblKhuVuc.setText("");
        // Chỉ xóa các node từ dòng thứ 2 (row >= 1)
        gridChiTietHD.getChildren().removeIf(node -> {
            Integer row = GridPane.getRowIndex(node);
            return row != null && row >= 1;
        });


    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void loadThongTinHoaDon(HoaDon hd) {
        lblMaHD.setText(hd.getMaHD());
        lblTenKH.setText(hd.getKhachHang() != null ? hd.getKhachHang().getTenKhachHang() : "-");
        lblSDT.setText(hd.getKhachHang() != null ? hd.getKhachHang().getSdt() : "-");
        lblSoLuong.setText(String.valueOf(hd.getSoLuong()));
        lblSuKien.setText(hd.getSuKien() != null ? hd.getSuKien().getTenSK() : "-");
        lblKhuVuc.setText(hd.getBan().getKhuVuc() != null ? hd.getBan().getKhuVuc().getTenKhuVuc() : "-");

        List<ChiTietHoaDon> chiTietList = ChiTietHDDAO.getByMaHD(hd.getMaHD());
        gridChiTietHD.getChildren().removeIf(node -> GridPane.getRowIndex(node) != null && GridPane.getRowIndex(node) > 0);

        int row = 1;
        for (ChiTietHoaDon cthd : chiTietList) {
            Label lblMon = new Label(cthd.getMon().getTenMon());
            Label lblSL = new Label(String.valueOf(cthd.getSoLuong()));
            Label lblGia = new Label(String.format("%,.0fđ", cthd.getMon().getGiaBan()));
            Label lblTong = new Label(String.format("%,.0fđ", cthd.getThanhTien()));

            gridChiTietHD.add(lblMon, 0, row);
            gridChiTietHD.add(lblSL, 1, row);
            gridChiTietHD.add(lblGia, 2, row);
            gridChiTietHD.add(lblTong, 3, row);
            row++;
        }
    }

    private void setupFilterEvents() {
        clearThongTin();
        if (txtMaHD != null) addAutoSearch(txtMaHD);
        if (cboKhuVuc != null) addAutoSearch(cboKhuVuc); // ComboBox
        if (txtSDT != null) addAutoSearch(txtSDT);
        if (dpThoiGian != null) addAutoSearch(dpThoiGian);
    }

    private void addAutoSearch(TextField field) {
        field.textProperty().addListener((obs, oldVal, newVal) -> filterDanhSach());
    }

    private <T> void addAutoSearch(ComboBox<T> cbo) {
        cbo.valueProperty().addListener((obs, oldVal, newVal) -> filterDanhSach());
    }

    private void addAutoSearch(DatePicker picker) {
        picker.valueProperty().addListener((obs, oldVal, newVal) -> filterDanhSach());
    }

    private void filterDanhSach() {
        String maHD = txtMaHD != null ? txtMaHD.getText().trim().toLowerCase() : "";
        String sdt = txtSDT != null ? txtSDT.getText().trim().toLowerCase() : "";
        String ngay = dpThoiGian != null && dpThoiGian.getValue() != null
                ? dpThoiGian.getValue().toString()
                : "";
        Object khuVuc = cboKhuVuc != null ? cboKhuVuc.getValue() : null;

        vboxDatTruoc.getChildren().clear();
        vboxCho.getChildren().clear();

        // Lấy thời gian đợi bàn 1 lần
        int thoiGianDatTruoc = 0; // kieuDatBan = 1
        int thoiGianCho = 0;      // kieuDatBan = 0
        try {
            ThoiGianDoiBan tgDatTruoc = ThoiGianDoiBanDAO.getLatestByLoai(true);
            ThoiGianDoiBan tgCho = ThoiGianDoiBanDAO.getLatestByLoai(false);
            if (tgDatTruoc != null) thoiGianDatTruoc = tgDatTruoc.getThoiGian();
            if (tgCho != null) thoiGianCho = tgCho.getThoiGian();
        } catch (Exception e) {
            System.err.println("Lỗi load thời gian đợi bàn: " + e.getMessage());
        }

        for (HoaDon hd : dsHoaDon) {
            if (hd.getTrangthai() != 0) continue;

            boolean match = true;
            if (!maHD.isEmpty() && !hd.getMaHD().toLowerCase().contains(maHD)) match = false;
            if (!sdt.isEmpty() && (hd.getKhachHang() == null ||
                    !hd.getKhachHang().getSdt().toLowerCase().contains(sdt))) match = false;
            if (!ngay.isEmpty() && hd.getTgCheckIn() != null &&
                    !hd.getTgCheckIn().toLocalDate().toString().equals(ngay)) match = false;
            if (hd.getBan() != null && khuVuc != null && !khuVuc.toString().equals("Tất cả")) {
                String tenKhuVuc = hd.getBan().getKhuVuc() != null ? hd.getBan().getKhuVuc().getTenKhuVuc() : "";
                if (!khuVuc.toString().equals(tenKhuVuc)) match = false;
            }

            if (match) {
                // Chọn thời gian đợi bàn theo loại
                int thoiGian = (hd.isKieuDatBan()) ? thoiGianDatTruoc : thoiGianCho;
                HBox item = createBookingItem(hd, thoiGian);

                if (hd.isKieuDatBan()) { // đặt trước
                    vboxDatTruoc.getChildren().add(item);
                } else { // chờ
                    vboxCho.getChildren().add(item);
                }
            }
        }
    }
    @FXML
    private void xoaTrang(){
        txtSDT.setText("");
        txtMaHD.setText("");
        cboKhuVuc.getSelectionModel().selectFirst();
    }

}
