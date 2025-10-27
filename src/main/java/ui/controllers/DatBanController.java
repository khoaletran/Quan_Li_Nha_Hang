package ui.controllers;

import dao.BanDAO;
import dao.HoaDonDAO;
import entity.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class DatBanController {

    @FXML private DatePicker datePicker;
    @FXML private Spinner<Integer> hourSpinner;
    @FXML private Spinner<Integer> minuteSpinner;
    @FXML private TextField noteField;

    @FXML private ImageView starOut_01, starOut_02, starOut_03, starOut_04;
    @FXML private ImageView starIN_01, starIN_02, starIN_03, starIN_04;
    @FXML private ImageView starVIP_01, starVIP_02;

    @FXML private VBox tableOut_01, tableOut_02, tableOut_03, tableOut_04;
    @FXML private VBox tableIN_01, tableIN_02, tableIN_03, tableIN_04;
    @FXML private VBox tableVIP_01, tableVIP_02;

    private MainController_NV mainController;
    private NhanVien nv;

    private Timeline debounceTimer;
    private LocalDateTime lastSelectedTime;
    private int lastSoLuong = -1;

    @FXML
    public void initialize() {
        LocalTime defaultTime = getDefaultTimePlus5(); // 👉 gọi hàm riêng

        datePicker.setValue(LocalDate.now());

        SpinnerValueFactory<Integer> hourFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, defaultTime.getHour()) {
                    @Override
                    public void decrement(int steps) { setValue((getValue() - steps + 24) % 24); }
                    @Override
                    public void increment(int steps) { setValue((getValue() + steps) % 24); }
                };

        SpinnerValueFactory<Integer> minuteFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, defaultTime.getMinute()) {
                    @Override
                    public void decrement(int steps) {
                        int v = getValue() - steps;
                        if (v < 0) {
                            hourSpinner.decrement();
                            v += 60;
                        }
                        setValue(v % 60);
                    }
                    @Override
                    public void increment(int steps) {
                        int v = getValue() + steps;
                        if (v > 59) {
                            hourSpinner.increment();
                            v %= 60;
                        }
                        setValue(v);
                    }
                };

        hourSpinner.setValueFactory(hourFactory);
        minuteSpinner.setValueFactory(minuteFactory);

        ganSuKienChoBan();

        datePicker.setOnAction(e -> scheduleRefresh());
        noteField.textProperty().addListener((obs, o, n) -> scheduleRefresh());
        hourSpinner.valueProperty().addListener((obs, o, n) -> scheduleRefresh());
        minuteSpinner.valueProperty().addListener((obs, o, n) -> scheduleRefresh());

        locTheoRealTime();
    }

    private LocalTime getDefaultTimePlus5() {
        LocalDateTime now = LocalDateTime.now().plusMinutes(5);

        // Nếu giờ > 23:59 thì chuyển sang ngày mai
        if (now.getHour() >= 24) {
            now = now.minusHours(24);
            datePicker.setValue(LocalDate.now().plusDays(1));
        }

        return now.toLocalTime();
    }


    private void scheduleRefresh() {
        if (debounceTimer != null) debounceTimer.stop();
        debounceTimer = new Timeline(new KeyFrame(Duration.millis(300), e -> locTheoRealTime()));
        debounceTimer.play();
    }

    public void setNhanVien(NhanVien nv) { this.nv = nv; }
    public void setMainController(MainController_NV controller) { this.mainController = controller; }

    private boolean isThoiGianHopLe(LocalDate date, int hour, int minute) {
        if (date == null) return false;
        return !LocalDateTime.of(date, LocalTime.of(hour, minute)).isBefore(LocalDateTime.now());
    }

    private void locTheoRealTime() {
        LocalDate date = datePicker.getValue();
        int hour = hourSpinner.getValue();
        int minute = minuteSpinner.getValue();
        int soLuong;
        try { soLuong = Integer.parseInt(noteField.getText()); }
        catch (NumberFormatException e) { soLuong = 0; }

        LocalDateTime selectedTime = LocalDateTime.of(date, LocalTime.of(hour, minute));
        if (lastSelectedTime != null && selectedTime.equals(lastSelectedTime) && soLuong == lastSoLuong)
            return; // Không đổi => bỏ qua

        lastSelectedTime = selectedTime;
        lastSoLuong = soLuong;

        if (!isThoiGianHopLe(date, hour, minute)) {
            voHieuHoaTatCaBan();
            return;
        }

        List<HoaDon> dsHD = HoaDonDAO.getAll();

        capNhatHienThi(starOut_01, tableOut_01, "KV0001", "LB0001", 1, 2, dsHD);
        capNhatHienThi(starOut_02, tableOut_02, "KV0001", "LB0002", 3, 4, dsHD);
        capNhatHienThi(starOut_03, tableOut_03, "KV0001", "LB0003", 5, 8, dsHD);
        capNhatHienThi(starOut_04, tableOut_04, "KV0001", "LB0004", 8, 12, dsHD);

        capNhatHienThi(starIN_01, tableIN_01, "KV0002", "LB0001", 1, 2, dsHD);
        capNhatHienThi(starIN_02, tableIN_02, "KV0002", "LB0002", 3, 4, dsHD);
        capNhatHienThi(starIN_03, tableIN_03, "KV0002", "LB0003", 5, 8, dsHD);
        capNhatHienThi(starIN_04, tableIN_04, "KV0002", "LB0004", 8, 12, dsHD);

        capNhatHienThi(starVIP_01, tableVIP_01, "KV0003", "LB0004", 8, 12, dsHD);
        capNhatHienThi(starVIP_02, tableVIP_02, "KV0003", "LB0005", 12, 100, dsHD);
    }

    private void voHieuHoaTatCaBan() {
        VBox[] tables = {tableOut_01, tableOut_02, tableOut_03, tableOut_04,
                tableIN_01, tableIN_02, tableIN_03, tableIN_04,
                tableVIP_01, tableVIP_02};
        ImageView[] stars = {starOut_01, starOut_02, starOut_03, starOut_04,
                starIN_01, starIN_02, starIN_03, starIN_04,
                starVIP_01, starVIP_02};
        for (int i = 0; i < tables.length; i++) {
            tables[i].setDisable(true);
            stars[i].setOpacity(0.2);
            stars[i].setImage(new Image(getClass().getResourceAsStream("/IMG/icon/starwhite.png")));
        }
    }

    private void capNhatHienThi(ImageView star, VBox table, String maKV, String maLB, int minKhach, int maxKhach, List<HoaDon> dsHD) {
        int soLuong;
        try { soLuong = Integer.parseInt(noteField.getText()); }
        catch (NumberFormatException e) { soLuong = 0; }

        LocalDate date = datePicker.getValue();
        int hour = hourSpinner.getValue();
        int minute = minuteSpinner.getValue();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime selected = LocalDateTime.of(date, LocalTime.of(hour, minute));

        if (soLuong <= 0 || BanDAO.getBanTrong(maKV, maLB) == null || selected.isBefore(now)) {
            tatBan(star, table);
            return;
        }

        long phut = java.time.Duration.between(now, selected).toMinutes();
        boolean anLien = phut >= 0 && phut <= 15;
        int gioToiThieu = getThoiGianDatTruocToiThieu(maLB);
        if (!anLien && selected.isBefore(now.plusHours(gioToiThieu))) {
            tatBan(star, table);
            return;
        }

        if (soLuong > maxKhach) {
            tatBan(star, table);
            return;
        }

        boolean trungLich = dsHD.stream().anyMatch(hd ->
                hd.getTgCheckIn() != null && hd.getTgCheckIn().equals(selected));

        if (trungLich) {
            moBanSaoMo(star, table);
        } else if (soLuong >= minKhach && soLuong <= maxKhach) {
            moBanSaoSang(star, table);
        } else {
            moBanSaoTrang(star, table);
        }
    }

    private void tatBan(ImageView star, VBox table) {
        table.setDisable(true);
        star.setOpacity(0.2);
        star.setImage(new Image(getClass().getResourceAsStream("/IMG/icon/starwhite.png")));
    }

    private void moBanSaoMo(ImageView star, VBox table) {
        table.setDisable(true);
        star.setOpacity(0.4);
        star.setImage(new Image(getClass().getResourceAsStream("/IMG/icon/starwhite.png")));
    }

    private void moBanSaoTrang(ImageView star, VBox table) {
        table.setDisable(false);
        star.setOpacity(0.7);
        star.setImage(new Image(getClass().getResourceAsStream("/IMG/icon/starwhite.png")));
    }

    private void moBanSaoSang(ImageView star, VBox table) {
        table.setDisable(false);
        star.setOpacity(1.0);
        star.setImage(new Image(getClass().getResourceAsStream("/IMG/icon/star.png")));
    }

    private void ganSuKienChoBan() {
        tableOut_01.setOnMouseClicked(e -> chonBanNeuDuoc("KV0001", "LB0001", 2));
        tableOut_02.setOnMouseClicked(e -> chonBanNeuDuoc("KV0001", "LB0002", 4));
        tableOut_03.setOnMouseClicked(e -> chonBanNeuDuoc("KV0001", "LB0003", 8));
        tableOut_04.setOnMouseClicked(e -> chonBanNeuDuoc("KV0001", "LB0004", 12));
        tableIN_01.setOnMouseClicked(e -> chonBanNeuDuoc("KV0002", "LB0001", 2));
        tableIN_02.setOnMouseClicked(e -> chonBanNeuDuoc("KV0002", "LB0002", 4));
        tableIN_03.setOnMouseClicked(e -> chonBanNeuDuoc("KV0002", "LB0003", 8));
        tableIN_04.setOnMouseClicked(e -> chonBanNeuDuoc("KV0002", "LB0004", 12));
        tableVIP_01.setOnMouseClicked(e -> chonBanNeuDuoc("KV0003", "LB0004", 12));
        tableVIP_02.setOnMouseClicked(e -> chonBanNeuDuoc("KV0003", "LB0005", 100));
    }

    private int getThoiGianDatTruocToiThieu(String maLoaiBan) {
        switch (maLoaiBan) {
            case "LB0001": return 4;
            case "LB0002": return 4;
            case "LB0003": return 6;
            case "LB0004": return 8;
            case "LB0005": return 12;
            default: return 4;
        }
    }

    private void chonBanNeuDuoc(String maKV, String maLB, int sucChua) {
        int soLuong;
        try { soLuong = Integer.parseInt(noteField.getText()); }
        catch (NumberFormatException e) { soLuong = 0; }

        if (soLuong > sucChua || soLuong <= 0) return;

        LocalDate date = datePicker.getValue();
        int hour = hourSpinner.getValue();
        int minute = minuteSpinner.getValue();
        LocalDateTime selected = LocalDateTime.of(date, LocalTime.of(hour, minute));

        Ban ban = BanDAO.getBanTrong(maKV, maLB);
        if (ban == null) return;

        chonBan(ban, soLuong, date, hour, minute);
    }

    private void chonBan(Ban ban, int soLuong, LocalDate date, int hour, int minute) {
        if (mainController != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/ChonMon.fxml"));
                Parent node = loader.load();

                ChonMonController chonMonCtrl = loader.getController();
                chonMonCtrl.setMainController(mainController);
                chonMonCtrl.setThongTinBan(ban);
                chonMonCtrl.setNhanVien(nv);
                chonMonCtrl.setSoLuongKhach(soLuong);
                chonMonCtrl.setThoiGianDat(LocalDateTime.of(date, LocalTime.of(hour, minute)));

                mainController.getMainContent().getChildren().setAll(node);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
