package ui.controllers;

import dao.BanDAO;
import dao.HoaDonDAO;
import entity.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class DatBanController {

    @FXML
    private DatePicker datePicker;
    @FXML
    private Spinner<Integer> hourSpinner;
    @FXML
    private Spinner<Integer> minuteSpinner;
    @FXML
    private TextField noteField;

    // ImageView bàn OUT
    @FXML
    private ImageView starOut_01;
    @FXML
    private ImageView starOut_02;
    @FXML
    private ImageView starOut_03;
    @FXML
    private ImageView starOut_04;

    // ImageView bàn IN
    @FXML
    private ImageView starIN_01;
    @FXML
    private ImageView starIN_02;
    @FXML
    private ImageView starIN_03;
    @FXML
    private ImageView starIN_04;

    // ImageView bàn VIP
    @FXML
    private ImageView starVIP_01;
    @FXML
    private ImageView starVIP_02;

    @FXML
    private VBox tableOut_01, tableOut_02, tableOut_03, tableOut_04;

    @FXML
    private VBox tableIN_01, tableIN_02, tableIN_03, tableIN_04;

    @FXML
    private VBox tableVIP_01, tableVIP_02;


    private ui.controllers.MainController_NV mainController;

    private HoaDonDAO hoaDonDAO = new HoaDonDAO();

    private NhanVien nv;


    @FXML
    public void initialize() {
        datePicker.setValue(LocalDate.now()); // mặc định là hôm nay
        hourSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 12));
        minuteSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0));
        ganSuKienChoBan();

        noteField.textProperty().addListener((obs, oldV, newV) -> locTheoRealTime());
        datePicker.valueProperty().addListener((obs, oldV, newV) -> locTheoRealTime());
        hourSpinner.valueProperty().addListener((obs, oldV, newV) -> locTheoRealTime());
        minuteSpinner.valueProperty().addListener((obs, oldV, newV) -> locTheoRealTime());
    }

    void setNhanVien(NhanVien nv) {
        this.nv = nv;
    }

    public void setMainController(ui.controllers.MainController_NV controller) {
        this.mainController = controller;
    }

    private void locTheoRealTime() {
        capNhatHienThi(starOut_01, 2, 3);
        capNhatHienThi(starOut_02, 3, 4);
        capNhatHienThi(starOut_03, 5, 8);
        capNhatHienThi(starOut_04, 8, 12);
        capNhatHienThi(starIN_01, 2, 3);
        capNhatHienThi(starIN_02, 3, 4);
        capNhatHienThi(starIN_03, 5, 8);
        capNhatHienThi(starIN_04, 8, 12);
        capNhatHienThi(starVIP_01, 8, 12);
        capNhatHienThi(starVIP_02, 12, 100);
    }

    private void hienThiNgoiSao(ImageView star, boolean b) {
        String imgPath = b ? "/IMG/icon/star.png" : "/IMG/icon/starwhite.png";
        star.setImage(new Image(getClass().getResourceAsStream(imgPath)));
    }

    private void capNhatHienThi(ImageView star, int minKhach, int maxKhach) {
        // Mặc định chưa chọn → star trắng
        boolean duocChon = false;

        // Lấy số lượng nhập
        int soLuong = 0;
        try {
            soLuong = Integer.parseInt(noteField.getText());
        } catch (NumberFormatException e) {
            soLuong = 0;
        }

        // Lấy thời gian từ datePicker + Spinner
        LocalDate date = datePicker.getValue();
        int hour = hourSpinner.getValue();
        int minute = minuteSpinner.getValue();

        boolean duocChonTheoThoiGian = true;
        if (date != null && hourSpinner.getValue() != null && minuteSpinner.getValue() != null) {
            LocalDateTime selectedDateTime = LocalDateTime.of(date, LocalTime.of(hour, minute));
            List<HoaDon> dsHD = hoaDonDAO.getAll();
            duocChonTheoThoiGian = dsHD.stream().noneMatch(hd -> {
                LocalDateTime tg = hd.getTgCheckIn();
                return tg != null && tg.equals(selectedDateTime);
            });
        }

        // Chỉ xét bật star nếu người dùng nhập số lượng
        if (soLuong > 0) {
            boolean duocChonTheoSoLuong = soLuong >= minKhach && soLuong <= maxKhach;
            duocChon = duocChonTheoThoiGian && duocChonTheoSoLuong;
        }

        hienThiNgoiSao(star, duocChon);
    }

    private void ganSuKienChoBan() {

        // OUT
        tableOut_01.setOnMouseClicked(e -> chonBan(BanDAO.getBanTrong("KV0001", "LB0001")));
        tableOut_02.setOnMouseClicked(e -> chonBan(BanDAO.getBanTrong("KV0001", "LB0002")));
        tableOut_03.setOnMouseClicked(e -> chonBan(BanDAO.getBanTrong("KV0001", "LB0003")));
        tableOut_04.setOnMouseClicked(e -> chonBan(BanDAO.getBanTrong("KV0001", "LB0004")));

        // IN
        tableIN_01.setOnMouseClicked(e -> chonBan(BanDAO.getBanTrong("KV0002", "LB0001")));
        tableIN_02.setOnMouseClicked(e -> chonBan(BanDAO.getBanTrong("KV0002", "LB0002")));
        tableIN_03.setOnMouseClicked(e -> chonBan(BanDAO.getBanTrong("KV0002", "LB0003")));
        tableIN_04.setOnMouseClicked(e -> chonBan(BanDAO.getBanTrong("KV0002", "LB0004")));

        // VIP
        tableVIP_01.setOnMouseClicked(e -> chonBan(BanDAO.getBanTrong("KV0003", "LB0004")));
        tableVIP_02.setOnMouseClicked(e -> chonBan(BanDAO.getBanTrong("KV0003", "LB0005")));
    }


    private void chonBan(Ban ban) {
        if (ban == null) {
            System.out.println("Không tìm thấy bàn trống phù hợp!");
            return;
        }

        if (mainController != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/ChonMon.fxml"));
                Parent node = loader.load();

                ChonMonController chonMonCtrl = loader.getController();
                chonMonCtrl.setMainController(mainController);
                chonMonCtrl.setThongTinBan(ban);
                chonMonCtrl.setNhanVien(nv);

                LocalDate date = datePicker.getValue();
                int hour = hourSpinner.getValue();
                int minute = minuteSpinner.getValue();
                LocalDateTime thoiGian = LocalDateTime.of(date, LocalTime.of(hour, minute));
                chonMonCtrl.setThoiGianDat(thoiGian);

                int soLuong = 0;
                try {
                    soLuong = Integer.parseInt(noteField.getText());
                } catch (NumberFormatException e) {
                    soLuong = 0;
                }
                chonMonCtrl.setSoLuongKhach(soLuong);

                mainController.getMainContent().getChildren().setAll(node);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }




}