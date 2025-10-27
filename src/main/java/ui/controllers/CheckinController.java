package ui.controllers;

import dao.ChiTietHDDAO;
import dao.HoaDonDAO;
import dao.ThoiGianDoiBanDAO;
import entity.ChiTietHoaDon;
import entity.HoaDon;
import entity.ThoiGianDoiBan;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
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
    private GridPane gridChiTietHD;
    @FXML
    private TextField txtMaHD, txtMaBan, txtSDT;
    @FXML
    private DatePicker dpThoiGian;

    // 🔹 Biến toàn cục lưu danh sách hóa đơn
    private List<HoaDon> dsHoaDon;

    // 🔹 Biến lưu item đang chọn
    private HBox lastSelected = null;

    @FXML
    public void initialize() {
        loadDanhSach();
        setupFilterEvents();
    }

    private void loadDanhSach() {
        dsHoaDon = HoaDonDAO.getAll(); // Gán vào biến toàn cục
        vboxDatTruoc.getChildren().clear();
        vboxCho.getChildren().clear();

        for (HoaDon hd : dsHoaDon) {
            if (hd.getTrangthai() != 0) continue; // chỉ lấy trạng thái 0

            HBox item = createBookingItem(hd);

            if (hd.isKieuDatBan()) {
                vboxDatTruoc.getChildren().add(item);
            } else {
                vboxCho.getChildren().add(item);
            }
        }
    }

    private HBox createBookingItem(HoaDon hd) {
        HBox hbox = new HBox(10);
        hbox.getStyleClass().add("booking-item");

        // 1️⃣ Hình ảnh bàn
        ImageView img = new ImageView(new Image(getClass().getResourceAsStream("/IMG/Rounded rectangle.png")));
        img.setPreserveRatio(true);
        img.getStyleClass().add("booking-image");

        // 2️⃣ Info khách hàng
        VBox info = new VBox();
        info.setStyle("-fx-alignment: CENTER_LEFT;");
        info.getStyleClass().add("booking-info");
        Label lblId = new Label(hd.getMaHD());
        lblId.getStyleClass().add("booking-id");
        Label lblPhone = new Label(hd.getKhachHang() != null ? hd.getKhachHang().getSdt() : "-");
        lblPhone.getStyleClass().add("booking-phone");
        info.getChildren().addAll(lblId, lblPhone);

        // 3️⃣ Thời gian đặt
        VBox dateBox = new VBox();
        dateBox.getStyleClass().add("booking-date");
        String timeStr = (hd.getTgCheckIn() != null)
                ? hd.getTgCheckIn().toLocalTime() + " - " + hd.getTgCheckIn().toLocalDate()
                : "-";
        Label lblDate = new Label(timeStr);
        dateBox.getChildren().add(lblDate);

        // 4️⃣ Thời gian còn lại
        VBox remainingBox = new VBox();
        remainingBox.setStyle("-fx-alignment: CENTER;");
        remainingBox.getStyleClass().add("booking-remaining");
        Label lblRemaining = new Label();
        remainingBox.getChildren().add(lblRemaining);

        // 5️⃣ Thời gian đợi bàn từ DB
        int thoiGianCho = 0; // phút
        ThoiGianDoiBan tg = ThoiGianDoiBanDAO.getLatestByLoai(hd.isKieuDatBan());
        if (tg != null) thoiGianCho = tg.getThoiGian();

        // 6️⃣ Đếm ngược
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
        remainingBox.setPrefWidth(80);

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
            showAlert(Alert.AlertType.WARNING, "Thông báo", "Chưa chọn hóa đơn để check-in!");
            return;
        }

        HoaDon hd = HoaDonDAO.getByID(maHD);
        if (hd == null) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không tìm thấy hóa đơn: " + maHD);
            return;
        }

        LocalDateTime tgDat = hd.getTgCheckIn();
        if (tgDat == null) {
            showAlert(Alert.AlertType.WARNING, "Thông báo", "Hóa đơn chưa có thời gian đặt bàn!");
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        ThoiGianDoiBan tg = ThoiGianDoiBanDAO.getLatestByLoai(hd.isKieuDatBan());
        int thoiGianCho = (tg != null) ? tg.getThoiGian() : 0;
        LocalDateTime tgChoPhep = tgDat.plusMinutes(thoiGianCho);

        if (now.isBefore(tgDat)) {
            showAlert(Alert.AlertType.INFORMATION, "Thông báo", "Chưa tới giờ check-in!\nGiờ đặt: " + tgDat.toLocalTime());
            return;
        }

        if (now.isAfter(tgChoPhep)) {
            showAlert(Alert.AlertType.INFORMATION, "Thông báo", "Đã quá hạn check-in!\nHạn cuối: " + tgChoPhep.toLocalTime());
            return;
        }

        hd.setTrangthai(1);
        hd.setTgCheckIn(now);

        boolean ok = HoaDonDAO.update(hd);
        if (ok) {
            showAlert(Alert.AlertType.INFORMATION, "Thành công", "Check-in thành công cho hóa đơn " + maHD + "!");
            loadDanhSach();
            clearThongTin();
        } else {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể cập nhật trạng thái check-in!");
        }
    }

    private void clearThongTin() {
        lblMaHD.setText("");
        lblTenKH.setText("");
        lblSDT.setText("");
        lblSoLuong.setText("");
        lblSuKien.setText("");
        lblKhuVuc.setText("");
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
        if (txtMaHD != null) addAutoSearch(txtMaHD);
        if (txtMaBan != null) addAutoSearch(txtMaBan);
        if (txtSDT != null) addAutoSearch(txtSDT);
        if (dpThoiGian != null) addAutoSearch(dpThoiGian);
    }

    private void addAutoSearch(TextField field) {
        field.textProperty().addListener((obs, oldVal, newVal) -> filterDanhSach());
    }

    private void addAutoSearch(DatePicker picker) {
        picker.valueProperty().addListener((obs, oldVal, newVal) -> filterDanhSach());
    }

    private void filterDanhSach() {
        String maHD = txtMaHD != null ? txtMaHD.getText().trim().toLowerCase() : "";
        String maBan = txtMaBan != null ? txtMaBan.getText().trim().toLowerCase() : "";
        String sdt = txtSDT != null ? txtSDT.getText().trim().toLowerCase() : "";
        String ngay = dpThoiGian != null && dpThoiGian.getValue() != null
                ? dpThoiGian.getValue().toString()
                : "";

        vboxDatTruoc.getChildren().clear();
        vboxCho.getChildren().clear();

        for (HoaDon hd : dsHoaDon) {
            if (hd.getTrangthai() != 0) continue;

            boolean match = true;
            if (!maHD.isEmpty() && !hd.getMaHD().toLowerCase().contains(maHD)) match = false;
            if (!maBan.isEmpty() && !hd.getBan().getMaBan().toLowerCase().contains(maBan)) match = false;
            if (!sdt.isEmpty() && (hd.getKhachHang() == null ||
                    !hd.getKhachHang().getSdt().toLowerCase().contains(sdt))) match = false;
            if (!ngay.isEmpty() && hd.getTgCheckIn() != null &&
                    !hd.getTgCheckIn().toLocalDate().toString().equals(ngay)) match = false;

            if (match) {
                HBox item = createBookingItem(hd);
                if (hd.isKieuDatBan()) {
                    vboxDatTruoc.getChildren().add(item);
                } else {
                    vboxCho.getChildren().add(item);
                }
            }
        }
    }
}
