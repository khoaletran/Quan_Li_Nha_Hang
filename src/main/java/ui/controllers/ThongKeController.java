package ui.controllers;

import dao.HoaDonDAO;
import entity.HoaDon;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;



import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

public class ThongKeController {
    @FXML
    private ComboBox<String> comboNgayTK;
    @FXML
    private ComboBox<String> comboThangTK;
    @FXML
    private ComboBox<String> comboNamTK;

    @FXML
    private Label lblDoanhThu;
    @FXML
    private Label lblTongHoaDon;
    @FXML
    private Label lblTieuDeSoSanh;
    @FXML
    private Label lblDoanhThuSoVoiXTruoc;
    @FXML
    private Label lblTiLe;
    @FXML
    private Label lblKhuVucIn, lblKhuVucOut, lblKhuVucVip;

//    Thống kê MÓN
    @FXML ComboBox<String> comboNamMon, comboThangMon;

    private List<HoaDon> dsHoaDon;

    private void loadThangNam(){
        int namHienTai = LocalDate.now().getYear();
        for(int i=2020; i<= namHienTai;i++){
            comboNamTK.getItems().add(String.valueOf(i));
            comboNamMon.getItems().add(String.valueOf(i));
        }
        comboThangMon.getItems().add("Tất cả");
        comboThangTK.getItems().add("Tất cả");
        for(int i=1; i<=12;i++){
            comboThangMon.getItems().add(String.valueOf(i));
            comboThangTK.getItems().add(String.valueOf(i));
        }
    }

    private void loadDoanhThu() {
        String namStr = comboNamTK.getValue();
        String thangStr = comboThangTK.getValue();
        String ngayStr = comboNgayTK.getValue();
        System.out.println("namStr=" + namStr + ", thangStr=" + thangStr + ", ngayStr=" + ngayStr);

        int nam = Integer.parseInt(namStr);
        Integer thang = (thangStr != null && !thangStr.equals("Tất cả")) ? Integer.parseInt(thangStr) : null;
        Integer ngay = (ngayStr != null && !ngayStr.equals("Tất cả")) ? Integer.parseInt(ngayStr) : null;


        if (ngay != null) {
            lblTieuDeSoSanh.setText("DOANH THU So Với Ngày Trước");
        } else if (thang != null) {
            lblTieuDeSoSanh.setText("DOANH THU So Với Tháng Trước");
        } else {
            lblTieuDeSoSanh.setText("DOANH THU So Với Năm Trước");
        }
        double tong = 0;
        int tongHoaDon = 0;
        int in =0, out=0, vip=0;
        double tongIn = 0, tongOut = 0, tongVip = 0;
        for (HoaDon hd : dsHoaDon) {
            LocalDate ngayLap = hd.getTgLapHD().toLocalDate();
            boolean matchNam = ngayLap.getYear() == nam;
            boolean matchThang = (thang == null) || (ngayLap.getMonthValue() == thang);
            boolean matchNgay = (ngay == null) || (ngayLap.getDayOfMonth() == ngay);
            if (matchNam && matchThang && matchNgay) {
                double temp=hd.getTongTienSau();
                tong += temp;
                tongHoaDon++;
                String kv = hd.getBan().getKhuVuc().getTenKhuVuc();
                if (kv.equals("Indoor")) {
                    in++;
                    tongIn += temp;
                } else if (kv.equals("Outdoor")) {
                    out++;
                    tongOut += temp;
                } else {
                    vip++;
                    tongVip += hd.getTongTienSau();
                }
            }
        }

        double doanhThuHienTai = tong;
        double doanhThuTruoc = tinhDoanhThuKyTruoc(nam, thang, ngay);
        double chenhlech = doanhThuHienTai - doanhThuTruoc;
        double tile = (doanhThuTruoc == 0) ? 0 : (chenhlech / doanhThuTruoc) * 100;

        lblDoanhThuSoVoiXTruoc.setText(String.format("%,.0f VNĐ ", chenhlech));

// Tùy chọn: đổi màu trực quan
        if (chenhlech >= 0) {
            lblDoanhThuSoVoiXTruoc.setStyle("-fx-text-fill: green;");
            lblTiLe.setStyle("-fx-text-fill: green;");
        } else {
            lblDoanhThuSoVoiXTruoc.setStyle("-fx-text-fill: red;");
            lblTiLe.setStyle("-fx-text-fill: red;");
        }
        lblDoanhThu.setText(String.format("%,.0f VNĐ", tong));
        lblTongHoaDon.setText(tongHoaDon + "");
        lblTiLe.setText(String.format("(%.1f%%)", tile));
        lblKhuVucIn.setText(String.format("IN: %.1f tr VNĐ (%d hd)", tongIn / 1_000_000.0, in));
        lblKhuVucOut.setText(String.format("OUT: %.1f tr VNĐ (%d hd)", tongOut / 1_000_000.0, out));
        lblKhuVucVip.setText(String.format("VIP: %.1f tr VNĐ (%d hd)", tongVip / 1_000_000.0, vip));

    }

    private double tinhDoanhThu(Integer nam, Integer thang, Integer ngay) {
        double tong = 0;
        for (HoaDon hd : dsHoaDon) {
            LocalDate ngayLap = hd.getTgLapHD().toLocalDate();
            boolean matchNam = ngayLap.getYear() == nam;
            boolean matchThang = (thang == null) || (ngayLap.getMonthValue() == thang);
            boolean matchNgay = (ngay == null) || (ngayLap.getDayOfMonth() == ngay);
            if (matchNam && matchThang && matchNgay) {
                tong += hd.getTongTienSau();
            }
        }
        return tong;
    }

    private double tinhDoanhThuKyTruoc(Integer nam, Integer thang, Integer ngay) {
        if (ngay != null) { // lọc theo ngày
            LocalDate current = LocalDate.of(nam, thang, ngay);
            LocalDate prev = current.minusDays(1);
            return tinhDoanhThu(prev.getYear(), prev.getMonthValue(), prev.getDayOfMonth());
        } else if (thang != null) { // lọc theo tháng
            YearMonth current = YearMonth.of(nam, thang);
            YearMonth prev = current.minusMonths(1);
            return tinhDoanhThu(prev.getYear(), prev.getMonthValue(), null);
        } else { // lọc theo năm
            return tinhDoanhThu(nam - 1, null, null);
        }
    }


    private void updateComboNgay(int nam, int thang) {
        comboNgayTK.getItems().clear();
        int soNgay = YearMonth.of(nam, thang).lengthOfMonth();
        comboNgayTK.getItems().add("Tất cả");
        for (int i = 1; i <= soNgay; i++) {
            comboNgayTK.getItems().add(String.valueOf(i));
        }
    }

    private void refreshNgay() {
        String namStr = comboNamTK.getSelectionModel().getSelectedItem();
        String thangStr = comboThangTK.getSelectionModel().getSelectedItem();
        if (namStr == null || thangStr.equals("Tất cả")) return;
        int nam = Integer.parseInt(namStr);
        int thang = Integer.parseInt(thangStr);
        updateComboNgay(nam, thang);
    }

    @FXML
    private void resetMon(){
        comboNamMon.getSelectionModel().select(String.valueOf(LocalDate.now().getYear()));
        comboThangMon.getSelectionModel().select(String.valueOf(LocalDate.now().getMonthValue()));
    }

    @FXML
    private void reset() {
        comboNamTK.getSelectionModel().select(String.valueOf(LocalDate.now().getYear()));
        comboThangTK.getSelectionModel().select(String.valueOf(LocalDate.now().getMonthValue()));
        updateComboNgay(LocalDate.now().getYear(), LocalDate.now().getMonthValue());
        comboNgayTK.getSelectionModel().select(String.valueOf(LocalDate.now().getDayOfMonth()));
        loadDoanhThu();
    }

    private boolean isUpdating = false;

    @FXML
    public void initialize() {
        dsHoaDon = HoaDonDAO.getAllNgayHomNay();

        loadThangNam();

        reset(); // set mặc định ngày hiện tại
        resetMon();
        comboNamTK.setOnAction(e -> {
            isUpdating = true; // bắt đầu update programmatically

            comboThangTK.getSelectionModel().selectFirst();
            comboNgayTK.getSelectionModel().selectFirst();
            comboNgayTK.setDisable(true); // tắt ngày khi chưa chọn tháng
            loadDoanhThu();

            isUpdating = false; // kết thúc update
        });

        comboThangTK.setOnAction(e -> {
            if (isUpdating) return; // bỏ qua nếu đang programmatic

            String thangStr = comboThangTK.getSelectionModel().getSelectedItem();
            comboNgayTK.setOnAction(null); // vẫn giữ logic reset ngày

            if (thangStr.equals("Tất cả")) {
                comboNgayTK.getItems().clear();
                comboNgayTK.getItems().add("Tất cả");
                comboNgayTK.getSelectionModel().selectFirst();
                comboNgayTK.setDisable(true); // vẫn disable ngày
            } else {
                refreshNgay();
                comboNgayTK.setDisable(false); // enable ngày
                comboNgayTK.getSelectionModel().selectFirst();
            }

            comboNgayTK.setOnAction(ev -> {
                if (!comboNgayTK.isDisabled()) {
                    loadDoanhThu();
                }
            });

            loadDoanhThu();
        });

        comboNgayTK.setOnAction(e -> {
            if (!comboNgayTK.isDisabled()) {
                loadDoanhThu();
            }
        });
    }


}
