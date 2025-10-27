package ui.controllers;

import dao.BanDAO;
import dao.HoaDonDAO;
import entity.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

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

    // OUT
    @FXML private ImageView starOut_01, starOut_02, starOut_03, starOut_04;
    // IN
    @FXML private ImageView starIN_01, starIN_02, starIN_03, starIN_04;
    // VIP
    @FXML private ImageView starVIP_01, starVIP_02;

    // Table nodes
    @FXML private VBox tableOut_01, tableOut_02, tableOut_03, tableOut_04;
    @FXML private VBox tableIN_01, tableIN_02, tableIN_03, tableIN_04;
    @FXML private VBox tableVIP_01, tableVIP_02;

    private ui.controllers.MainController_NV mainController;
    private final HoaDonDAO hoaDonDAO = new HoaDonDAO();
    private NhanVien nv;

    @FXML
    public void initialize() {
        datePicker.setValue(LocalDate.now());
        hourSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, LocalTime.now().getHour()));
        minuteSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, LocalTime.now().getMinute()));

        ganSuKienChoBan();

        noteField.textProperty().addListener((obs, o, n) -> locTheoRealTime());
        datePicker.valueProperty().addListener((obs, o, n) -> locTheoRealTime());
        hourSpinner.valueProperty().addListener((obs, o, n) -> locTheoRealTime());
        minuteSpinner.valueProperty().addListener((obs, o, n) -> locTheoRealTime());

        locTheoRealTime();
    }

    void setNhanVien(NhanVien nv) { this.nv = nv; }
    public void setMainController(ui.controllers.MainController_NV controller) { this.mainController = controller; }


    private boolean isThoiGianHopLe(LocalDate date, int hour, int minute) {
        if (date == null) return false;
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime selected = LocalDateTime.of(date, LocalTime.of(hour, minute));
        return !selected.isBefore(now);
    }

    private void capNhatHienThi(ImageView star, VBox table, String maKV, String maLB, int minKhach, int maxKhach) {
        int soLuong;
        try { soLuong = Integer.parseInt(noteField.getText()); }
        catch (NumberFormatException e) { soLuong = 0; }

        LocalDate date = datePicker.getValue();
        int hour = hourSpinner.getValue();
        int minute = minuteSpinner.getValue();

        boolean hopLe = isThoiGianHopLe(date, hour, minute);
        Ban banTrong = BanDAO.getBanTrong(maKV, maLB);

        // Nếu không còn bàn trống → tắt bàn luôn
        if (banTrong == null) {
            table.setDisable(true);
            star.setOpacity(0.2);
            star.setImage(new Image(getClass().getResourceAsStream("/IMG/icon/starwhite.png")));
            return;
        } else {
            table.setDisable(false); // còn bàn thì bật lại
        }

        // Nếu ngày giờ sai hoặc số lượng vượt mức
        if (!hopLe || soLuong > maxKhach) {
            star.setOpacity(0.3);
            star.setImage(new Image(getClass().getResourceAsStream("/IMG/icon/starwhite.png")));
            return;
        }

        // Kiểm tra trùng thời gian hóa đơn
        List<HoaDon> dsHD = hoaDonDAO.getAll();
        LocalDateTime selectedDateTime = LocalDateTime.of(date, LocalTime.of(hour, minute));
        boolean trungLich = dsHD.stream().anyMatch(hd -> {
            LocalDateTime tg = hd.getTgCheckIn();
            return tg != null && tg.equals(selectedDateTime);
        });

        boolean duocChon = soLuong >= minKhach && soLuong <= maxKhach && !trungLich;
        star.setOpacity(duocChon ? 1.0 : 0.3);
        hienThiNgoiSao(star, duocChon);
    }

    private void hienThiNgoiSao(ImageView star, boolean duocChon) {
        String imgPath = duocChon ? "/IMG/icon/star.png" : "/IMG/icon/starwhite.png";
        star.setImage(new Image(getClass().getResourceAsStream(imgPath)));
    }

    private void locTheoRealTime() {
        capNhatHienThi(starOut_01, tableOut_01, "KV0001", "LB0001", 1, 2);
        capNhatHienThi(starOut_02, tableOut_02, "KV0001", "LB0002", 3, 4);
        capNhatHienThi(starOut_03, tableOut_03, "KV0001", "LB0003", 5, 8);
        capNhatHienThi(starOut_04, tableOut_04, "KV0001", "LB0004", 8, 12);

        capNhatHienThi(starIN_01, tableIN_01, "KV0002", "LB0001", 1, 2);
        capNhatHienThi(starIN_02, tableIN_02, "KV0002", "LB0002", 3, 4);
        capNhatHienThi(starIN_03, tableIN_03, "KV0002", "LB0003", 5, 8);
        capNhatHienThi(starIN_04, tableIN_04, "KV0002", "LB0004", 8, 12);

        capNhatHienThi(starVIP_01, tableVIP_01, "KV0003", "LB0004", 8, 12);
        capNhatHienThi(starVIP_02, tableVIP_02, "KV0003", "LB0005", 12, 100);
    }

    // ================== GẮN SỰ KIỆN ==================
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

    // ================== CHỌN BÀN ==================
    private void chonBanNeuDuoc(String maKV, String maLB, int sucChua) {
        int soLuong;
        try { soLuong = Integer.parseInt(noteField.getText()); }
        catch (NumberFormatException e) { soLuong = 0; }

        if (soLuong > sucChua) {
            System.out.println("⚠️ Số lượng khách vượt quá sức chứa bàn!");
            return;
        }

        LocalDate date = datePicker.getValue();
        int hour = hourSpinner.getValue();
        int minute = minuteSpinner.getValue();

        if (!isThoiGianHopLe(date, hour, minute)) {
            System.out.println("⚠️ Thời gian không hợp lệ (nhỏ hơn hiện tại)!");
            return;
        }

        Ban ban = BanDAO.getBanTrong(maKV, maLB);
        if (ban == null) {
            System.out.println("⚠️ Hết bàn trống cho khu vực này!");
            return;
        }

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
