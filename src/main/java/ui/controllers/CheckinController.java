package ui.controllers;

import dao.*;
import entity.*;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;

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

    private List<HoaDon> dsHoaDon;
    
    private HBox lastSelected = null;

    @FXML
    public void initialize() {
        loadDanhSach();
        loadComboKhuVuc();
        setupFilterEvents();

        javafx.animation.Timeline autoRefresh = new javafx.animation.Timeline(
                new javafx.animation.KeyFrame(javafx.util.Duration.seconds(30), e -> {
                    autoAssignWaitlistToFreeTable();
                })
        );
        autoRefresh.setCycleCount(javafx.animation.Animation.INDEFINITE);
        autoRefresh.play();
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
    private void  loadDanhSach() {

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


        dsHoaDon = HoaDonDAO.getAllNgayHomNay(); // đã tối ưu: chỉ set ID và tên, không gọi DAO phụ


        vboxDatTruoc.getChildren().clear();
        vboxCho.getChildren().clear();


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

        String imgPath = "/IMG/ban/IN"; // mặc định
        if (hd.getBan() != null && hd.getBan().getMaBan() != null) {
            String tenKhuVuc = hd.getBan().getKhuVuc().getTenKhuVuc();
            if (tenKhuVuc.equals("Indoor")) imgPath = "/IMG/ban/IN.png";
            else if (tenKhuVuc.equals("Outdoor")) imgPath = "/IMG/ban/out.png";
            else if (tenKhuVuc.equals("VIP")) imgPath = "/IMG/ban/vip.png";
        }
        ImageView img = new ImageView(new Image(getClass().getResourceAsStream(imgPath)));
        img.setFitWidth(80);
        img.setFitHeight(70);

        img.setPreserveRatio(false);

        Rectangle clip = new Rectangle(93, 80);
        clip.setArcWidth(15);   // bán kính bo góc ngang
        clip.setArcHeight(15);  // bán kính bo góc dọc
        img.setClip(clip);


        HBox.setMargin(img, new Insets(10));
        img.getStyleClass().add("booking-image");

        VBox info = new VBox();
        info.setStyle("-fx-alignment: CENTER_LEFT;");
        info.getStyleClass().add("booking-info");
        Label lblId = new Label(hd.getMaHD());
        lblId.getStyleClass().add("booking-id");
        Label lblPhone = new Label(hd.getKhachHang() != null ? hd.getKhachHang().getSdt() : "-");
        lblPhone.getStyleClass().add("booking-phone");
        info.getChildren().addAll(lblId, lblPhone);


        VBox dateBox = new VBox();
        dateBox.getStyleClass().add("booking-date");
        String timeStr = (hd.getTgCheckIn() != null)
                ? hd.getTgCheckIn().toLocalTime() + " - " + hd.getTgCheckIn().toLocalDate()
                : "-";
        Label lblDate = new Label(timeStr);
        dateBox.getChildren().add(lblDate);


        VBox remainingBox = new VBox();
        remainingBox.setStyle("-fx-alignment: CENTER;");
        remainingBox.getStyleClass().add("booking-remaining");
        Label lblRemaining = new Label();
        remainingBox.getChildren().add(lblRemaining);


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
                                Ban banhethan = hd.getBan();
                                BanDAO.update(banhethan, false);
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
            hd.setTrangthai(3);
            HoaDonDAO.update(hd);
            loadDanhSach();
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

        List<ChiTietHoaDon> chiTietList = ChiTietHDDAO.getAllByMaHD(hd.getMaHD());
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

    @FXML
    private void xoaTrang() {
        clearThongTin();
        txtMaHD.clear();
        txtSDT.clear();
        dpThoiGian.setValue(null);
        cboKhuVuc.getSelectionModel().selectFirst();
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

    private void autoAssignWaitlistToFreeTable() {
        try {
            // Lấy danh sách bàn trống thật
            List<Ban> dsBanTrong = BanDAO.getAllTrong();

            // Lấy danh sách hóa đơn đang chờ
            List<HoaDon> dsWaitlist = HoaDonDAO.getAllWaitlistCho();

            for (HoaDon hdWait : dsWaitlist) {
                Ban banCho = hdWait.getBan();
                if (banCho == null || banCho.getKhuVuc() == null || banCho.getLoaiBan() == null)
                    continue;

                int soLuongKhach = hdWait.getSoLuong();
                String maKV = banCho.getKhuVuc().getMaKhuVuc();

                // Tìm bàn thật cùng khu vực và đủ chỗ
                Ban banPhuHop = dsBanTrong.stream()
                        .filter(b -> b.getKhuVuc().getMaKhuVuc().equals(maKV)
                                && b.getLoaiBan().getSoLuong() >= soLuongKhach)
                        .findFirst()
                        .orElse(null);

                if (banPhuHop != null) {
                    // Cập nhật bàn mới cho hóa đơn
                    hdWait.setBan(banPhuHop);
                    boolean okban = BanDAO.update(banPhuHop,true);
                    hdWait.setTgCheckIn(LocalDateTime.now());
                    hdWait.setTrangthai(0); // vẫn là chờ, không chuyển sang phục vụ

                    // Update hóa đơn sang bàn thật
                    boolean ok = HoaDonDAO.update(hdWait);

                    if (ok) {
                        // Xóa bàn đợi (bàn tạm chờ) khỏi DB
                        boolean xoaBanTam = BanDAO.delete(banCho.getMaBan());
                        System.out.println("💡 Đã gán bàn thật " + banPhuHop.getMaBan() +
                                " cho hóa đơn chờ " + hdWait.getMaHD() +
                                (xoaBanTam ? " và xóa bàn tạm " + banCho.getMaBan() + " " : "  KHÔNG XÓA ĐƯỢC bàn tạm"));

                    } else {
                        System.err.println("Không thể cập nhật hóa đơn " + hdWait.getMaHD());
                    }
                }
            }

            System.out.println("Hoàn tất tự động gán bàn thật cho các hóa đơn chờ.");
            loadDanhSach();

        } catch (Exception e) {
            System.err.println("Lỗi khi auto gán bàn chờ: " + e.getMessage());
            e.printStackTrace();
        }
    }




}
