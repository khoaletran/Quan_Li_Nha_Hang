package ui.controllers;

import dao.*;
import entity.*;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import ui.AlertCus;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class CheckinController {

    // root của màn checkin (gán fx:id="root" trong FXML)
    @FXML
    private BorderPane root;

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
    @FXML
    private Button btnCheckIn, btnXoaTrang;

    private List<HoaDon> dsHoaDon;

    private HBox lastSelected = null;

    // ====== TIMELINE TỰ ĐỘNG VÀ COUNTDOWN ======
    private static Timeline autoRefresh;     // CHỈ 1 timer cho tất cả controller
    private static boolean autoRefreshStarted = false;
    private final List<Timeline> countdownTimelines = new ArrayList<>();

    // cache thời gian đợi bàn (phút)
    private int thoiGianDatTruoc = 0; // kieuDatBan = 1
    private int thoiGianCho = 0;      // kieuDatBan = 0

    @FXML
    public void initialize() {
        System.out.println("Initializing CheckinController");

        loadThoiGianDoiBan();
        loadDanhSach();
        loadComboKhuVuc();
        setupFilterEvents();

        // autoRefresh chỉ tạo 1 lần duy nhất
        if (!autoRefreshStarted) {
            autoRefresh = new Timeline(
                    new KeyFrame(Duration.seconds(30), e -> autoAssignWaitlistToFreeTable())
            );
            autoRefresh.setCycleCount(Animation.INDEFINITE);
            autoRefresh.play();
            autoRefreshStarted = true;
            System.out.println("autoRefresh started");
        } else {
            System.out.println("autoRefresh already running");
        }

        Platform.runLater(() -> {
            if (txtSDT != null && txtSDT.getScene() != null) {
                addShortcuts(txtSDT.getScene());
            }

            if (root != null) {
                root.sceneProperty().addListener((obs, oldScene, newScene) -> {
                    if (newScene == null) {
                        dispose();
                    }
                });
            }
        });

        Tooltip tipFind = new Tooltip("Tìm kiếm số điện thoại (Ctrl + F)");
        Tooltip.install(txtSDT, tipFind);
        Tooltip tipCheck = new Tooltip("Check in khách hàng (Ctrl + B)");
        Tooltip.install(btnCheckIn, tipCheck);
        Tooltip tipClear = new Tooltip("Clear thông tin (Ctrl + L)");
        Tooltip.install(btnXoaTrang, tipClear);
    }


    /**
     * Dừng autoRefresh + tất cả countdown khi màn này bị remove.
     * Gọi tự động qua listener sceneProperty ở trên.
     */
    private void dispose() {
        System.out.println("CheckinController dispose() called");

        clearCountdownTimelines();  // dừng tất cả countdown từng tạo cho màn hình này

        // Nếu muốn dừng luôn autoRefresh khi không ở màn checkin nữa thì:
        if (autoRefresh != null) {
            autoRefresh.stop();
            autoRefreshStarted = false;
            System.out.println("autoRefresh stopped in dispose()");
        }
    }

    private void addShortcuts(Scene scene) {
        KeyCombination ctrlF = new KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN);
        scene.getAccelerators().put(ctrlF, () -> {
            txtSDT.requestFocus();
            txtSDT.selectAll();
        });
        KeyCombination ctrlB = new KeyCodeCombination(KeyCode.B, KeyCombination.CONTROL_DOWN);
        scene.getAccelerators().put(ctrlB, this::checkin);
        KeyCombination ctrlL = new KeyCodeCombination(KeyCode.L, KeyCombination.CONTROL_DOWN);
        scene.getAccelerators().put(ctrlL, this::xoaTrang);
    }

    // =====================================================================
    //                     LOAD THỜI GIAN ĐỢI BÀN (CACHE)
    // =====================================================================
    private void loadThoiGianDoiBan() {
        thoiGianDatTruoc = 0;
        thoiGianCho = 0;
        try {
            ThoiGianDoiBan tgDatTruoc = ThoiGianDoiBanDAO.getLatestByLoai(true);   // đặt trước
            ThoiGianDoiBan tgCho = ThoiGianDoiBanDAO.getLatestByLoai(false);       // chờ
            if (tgDatTruoc != null) thoiGianDatTruoc = tgDatTruoc.getThoiGian();
            if (tgCho != null) thoiGianCho = tgCho.getThoiGian();
        } catch (Exception e) {
            System.err.println("Lỗi load thời gian đợi bàn: " + e.getMessage());
        }
    }

    private void loadComboKhuVuc() {
        cboKhuVuc.getItems().clear();
        cboKhuVuc.getItems().add("Tất cả");
        for (KhuVuc khuVuc : KhuVucDAO.getAll()) {
            cboKhuVuc.getItems().add(khuVuc.getTenKhuVuc());
        }
        cboKhuVuc.getSelectionModel().selectFirst();
    }

    // =====================================================================
    //                          LOAD DANH SÁCH
    // =====================================================================
    private void loadDanhSach() {
        // lấy danh sách hóa đơn hôm nay 1 lần
        dsHoaDon = HoaDonDAO.getAllNgayHomNay();

        // xóa UI + stop toàn bộ countdown cũ
        clearBookingItems();

        if (dsHoaDon == null) return;

        for (HoaDon hd : dsHoaDon) {
            if (hd.getTrangthai() != 0) continue; // chỉ lấy trạng thái 0

            int thoiGian = hd.isKieuDatBan() ? thoiGianDatTruoc : thoiGianCho;

            HBox item = createBookingItem(hd, thoiGian);

            if (hd.isKieuDatBan()) { // đặt trước
                vboxDatTruoc.getChildren().add(item);
            } else { // chờ
                vboxCho.getChildren().add(item);
            }
        }
    }

    /**
     * Xóa toàn bộ HBox trong vbox + dừng countdown cũ.
     */
    private void clearBookingItems() {
        vboxDatTruoc.getChildren().clear();
        vboxCho.getChildren().clear();
        clearCountdownTimelines();
    }

    private void clearCountdownTimelines() {
        for (Timeline t : countdownTimelines) {
            t.stop();
        }
        countdownTimelines.clear();
    }

    // =====================================================================
    //                     TẠO ITEM ĐẶT BÀN + COUNTDOWN
    // =====================================================================
    private HBox createBookingItem(HoaDon hd, int thoiGianChoPhut) {
        HBox hbox = new HBox(10);
        hbox.getStyleClass().add("booking-item");
        hbox.setUserData(hd);

        String imgPath = "/IMG/ban/IN.png"; // mặc định
        if (hd.getBan() != null && hd.getBan().getMaBan() != null && hd.getBan().getKhuVuc() != null) {
            String tenKhuVuc = hd.getBan().getKhuVuc().getTenKhuVuc();
            if ("Indoor".equals(tenKhuVuc)) imgPath = "/IMG/ban/IN.png";
            else if ("Outdoor".equals(tenKhuVuc)) imgPath = "/IMG/ban/out.png";
            else if ("VIP".equals(tenKhuVuc)) imgPath = "/IMG/ban/vip.png";
        }
        ImageView img = new ImageView(new Image(getClass().getResourceAsStream(imgPath)));
        img.setFitWidth(80);
        img.setFitHeight(70);
        img.setPreserveRatio(false);

        Rectangle clip = new Rectangle(93, 80);
        clip.setArcWidth(15);
        clip.setArcHeight(15);
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

        // COUNTDOWN
        if (hd.getTgCheckIn() != null && thoiGianChoPhut > 0) {
            LocalDateTime checkInTime = hd.getTgCheckIn();
            long totalSeconds = thoiGianChoPhut * 60L;

            Timeline timeline = new Timeline(
                    new KeyFrame(Duration.seconds(1), e -> {
                        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
                        if (now.isBefore(checkInTime)) {
                            lblRemaining.setText("-- : -- : --");
                            remainingBox.setStyle("-fx-background-color: #00C8B3;");
                        } else {
                            long diff = ChronoUnit.SECONDS.between(checkInTime, now);
                            long secondsLeft = totalSeconds - diff;
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
                                if (banhethan != null) {
                                    BanDAO.update(banhethan, false);
                                }
                            }
                        }
                    })
            );
            timeline.setCycleCount(Animation.INDEFINITE);
            timeline.play();

            countdownTimelines.add(timeline);
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

    // =====================================================================
    //                              CHECKIN
    // =====================================================================
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
        int thoiGianChoPhut = (tg != null) ? tg.getThoiGian() : 0;
        LocalDateTime tgChoPhep = tgDat.plusMinutes(thoiGianChoPhut);

        if (now.isBefore(tgDat)) {
            AlertCus.show("Thông báo", "Chưa tới giờ check-in!\nGiờ đặt: " + tgDat.toLocalTime());
            // showAlert(Alert.AlertType.INFORMATION, "Thông báo",
            //         "Chưa tới giờ check-in!\nGiờ đặt: " + tgDat.toLocalTime());
            return;
        }

        if (now.isAfter(tgChoPhep)) {
            AlertCus.show("Thông báo", "Đã quá hạn check-in!\nHạn cuối: " + tgChoPhep.toLocalTime());
            // showAlert(Alert.AlertType.INFORMATION, "Thông báo",
            //         "Đã quá hạn check-in!\nHạn cuối: " + tgChoPhep.toLocalTime());
            hd.setTrangthai(3);
            BanDAO.update(hd.getBan(), false);
            HoaDonDAO.update(hd);
            loadDanhSach();
            return;
        }

        hd.setTrangthai(1);
        hd.setTgCheckIn(now);

        boolean ok = HoaDonDAO.update(hd);
        if (ok) {
            AlertCus.show("Thành công", "Check-in thành công cho hóa đơn " + maHD + "!");
            // showAlert(Alert.AlertType.INFORMATION, "Thành công",
            //         "Check-in thành công cho hóa đơn " + maHD + "!");
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
        lblBan.setText("");
        lblKhuVuc.setText("");

        // xóa các dòng detail (row >= 1)
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
        lblBan.setText(hd.getBan()!= null ? hd.getBan().getMaBan() : "-");
        lblKhuVuc.setText(hd.getBan().getKhuVuc() != null ? hd.getBan().getKhuVuc().getTenKhuVuc() : "-");

        List<ChiTietHoaDon> chiTietList = ChiTietHDDAO.getAllByMaHD(hd.getMaHD());
        gridChiTietHD.getChildren().removeIf(node -> {
            Integer row = GridPane.getRowIndex(node);
            return row != null && row > 0;
        });

        int row = 1;
        for (ChiTietHoaDon cthd : chiTietList) {
            Label lblMon = new Label(cthd.getMon().getTenMon());
            Label lblSL = new Label(String.valueOf(cthd.getSoLuong()));
            Label lblGia = new Label(String.format("%,.0fđ", cthd.getMon().getGiaBanTaiLucLapHD(hd)));
            Label lblTong = new Label(String.format("%,.0fđ", cthd.getThanhTien()));

            gridChiTietHD.add(lblMon, 0, row);
            gridChiTietHD.add(lblSL, 1, row);
            gridChiTietHD.add(lblGia, 2, row);
            gridChiTietHD.add(lblTong, 3, row);
            row++;
        }
    }

    // =====================================================================
    //                         FILTER DANH SÁCH
    // =====================================================================
    private void setupFilterEvents() {
        clearThongTin();
        if (txtMaHD != null) addAutoSearch(txtMaHD);
        if (cboKhuVuc != null) addAutoSearch(cboKhuVuc);
        if (txtSDT != null) addAutoSearch(txtSDT);
        if (dpThoiGian != null) addAutoSearch(dpThoiGian);
    }

    @FXML
    private void xoaTrang() {
        clearThongTin();
        if (txtMaHD != null) txtMaHD.clear();
        if (txtSDT != null) txtSDT.clear();
        if (dpThoiGian != null) dpThoiGian.setValue(null);
        if (cboKhuVuc != null) cboKhuVuc.getSelectionModel().selectFirst();
        // load lại toàn bộ
        loadDanhSach();
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
        if (dsHoaDon == null) return;

        String maHD = txtMaHD != null ? txtMaHD.getText().trim().toLowerCase() : "";
        String sdt = txtSDT != null ? txtSDT.getText().trim().toLowerCase() : "";
        String ngay = (dpThoiGian != null && dpThoiGian.getValue() != null)
                ? dpThoiGian.getValue().toString()
                : "";
        Object khuVuc = cboKhuVuc != null ? cboKhuVuc.getValue() : null;

        // clear UI + stop countdown cũ
        clearBookingItems();

        for (HoaDon hd : dsHoaDon) {
            if (hd.getTrangthai() != 0) continue;

            boolean match = true;
            if (!maHD.isEmpty() && !hd.getMaHD().toLowerCase().contains(maHD)) match = false;
            if (!sdt.isEmpty()) {
                String phone = (hd.getKhachHang() != null && hd.getKhachHang().getSdt() != null)
                        ? hd.getKhachHang().getSdt().toLowerCase()
                        : "";
                if (!phone.contains(sdt)) match = false;
            }
            if (!ngay.isEmpty() && hd.getTgCheckIn() != null &&
                    !hd.getTgCheckIn().toLocalDate().toString().equals(ngay)) match = false;
            if (hd.getBan() != null && khuVuc != null && !"Tất cả".equals(khuVuc.toString())) {
                String tenKhuVuc = hd.getBan().getKhuVuc() != null
                        ? hd.getBan().getKhuVuc().getTenKhuVuc()
                        : "";
                if (!khuVuc.toString().equals(tenKhuVuc)) match = false;
            }

            if (match) {
                int thoiGian = hd.isKieuDatBan() ? thoiGianDatTruoc : thoiGianCho;
                HBox item = createBookingItem(hd, thoiGian);

                if (hd.isKieuDatBan()) {
                    vboxDatTruoc.getChildren().add(item);
                } else {
                    vboxCho.getChildren().add(item);
                }
            }
        }
    }

    // =====================================================================
    //                 AUTO GÁN BÀN THẬT CHO HÓA ĐƠN CHỜ
    // =====================================================================
    private void autoAssignWaitlistToFreeTable() {
        try {
            // Lấy danh sách bàn trống
            List<Ban> dsBanTrong = BanDAO.getAllTrong();

            // Lấy danh sách hóa đơn đang chờ
            List<HoaDon> dsWaitlist = HoaDonDAO.getAllWaitlistCho();

            for (HoaDon hdWait : dsWaitlist) {
                Ban banCho = hdWait.getBan();
                if (banCho == null || banCho.getKhuVuc() == null || banCho.getLoaiBan() == null)
                    continue;

                int soLuongKhach = hdWait.getSoLuong();
                String maKV = banCho.getKhuVuc().getMaKhuVuc();

                Ban banPhuHop = dsBanTrong.stream()
                        .filter(b -> b.getKhuVuc().getMaKhuVuc().equals(maKV)
                                && b.getLoaiBan().getSoLuong() >= soLuongKhach)
                        .findFirst()
                        .orElse(null);

                if (banPhuHop != null) {
                    hdWait.setBan(banPhuHop);
                    BanDAO.update(banPhuHop, true);
                    hdWait.setTgCheckIn(LocalDateTime.now());
                    hdWait.setTrangthai(0); // vẫn trạng thái chờ

                    boolean ok = HoaDonDAO.update(hdWait);

                    if (ok) {
                        boolean xoaBanTam = BanDAO.delete(banCho.getMaBan());
                        System.out.println("Đã gán bàn thật " + banPhuHop.getMaBan() +
                                " cho hóa đơn chờ " + hdWait.getMaHD() +
                                (xoaBanTam ? " và xóa bàn tạm " + banCho.getMaBan() : " (KHÔNG XÓA ĐƯỢC bàn tạm)"));
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

    public void selectHoaDonByMaHD(String maHD) {
        Platform.runLater(() -> {
            if (dsHoaDon == null || dsHoaDon.isEmpty()) return;

            for (Node node : vboxDatTruoc.getChildren()) {
                if (node instanceof HBox hbox) {
                    HoaDon hd = (HoaDon) hbox.getUserData();
                    if (hd != null && hd.getMaHD().equals(maHD)) {
                        loadThongTinHoaDon(hd);
                        highlightSelected(hbox);
                        return;
                    }
                }
            }

            for (Node node : vboxCho.getChildren()) {
                if (node instanceof HBox hbox) {
                    HoaDon hd = (HoaDon) hbox.getUserData();
                    if (hd != null && hd.getMaHD().equals(maHD)) {
                        loadThongTinHoaDon(hd);
                        highlightSelected(hbox);
                        return;
                    }
                }
            }
        });
    }
}
