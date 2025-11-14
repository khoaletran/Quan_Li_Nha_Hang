package ui.controllers;

import dao.ChiTietHDDAO;
import dao.HoaDonDAO;
import entity.ChiTietHoaDon;
import entity.HoaDon;

import entity.Mon;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.io.InputStream;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
    @FXML
    private ComboBox<String> comboNamMon, comboThangMon;
    @FXML
    private VBox vboxDishList;

    //  Biểu đồ
    @FXML
    private LineChart<Number, Number> lineChart;

    @FXML
    private DatePicker datePicker;

    @FXML
    private BarChart<String, Number> barChart;
    @FXML
    private DatePicker datePickerTuan;

    @FXML
    private Map<HoaDon, Double> mapHoaDon;
    private MainController_QL mainController;

    public void setMainController(MainController_QL controller) {
        this.mainController = controller;
    }



    private void loadBieuDoBarChart(LocalDate ngayChon) {
        // Xác định tuần chứa ngày được chọn
        LocalDate ngayDauTuan = ngayChon.with(DayOfWeek.MONDAY);
        LocalDate ngayCuoiTuan = ngayDauTuan.plusDays(6);

        Map<String, Integer> soLuongTheoThu = new LinkedHashMap<>();
        String[] thuList = {"Thứ 2", "Thứ 3", "Thứ 4", "Thứ 5", "Thứ 6", "Thứ 7", "Chủ nhật"};
        for (String thu : thuList) soLuongTheoThu.put(thu, 0);

        // Duyệt mapHoaDon để đếm số lượng đơn hàng
        for (Map.Entry<HoaDon, Double> entry : mapHoaDon.entrySet()) {
            HoaDon hd = entry.getKey();
            LocalDateTime tgCheckOut = hd.getTgCheckOut();

            if (tgCheckOut != null && hd.getTrangthai() == 2) {
                LocalDate ngay = tgCheckOut.toLocalDate();
                if (!ngay.isBefore(ngayDauTuan) && !ngay.isAfter(ngayCuoiTuan)) {
                    DayOfWeek dow = tgCheckOut.getDayOfWeek();
                    String tenThu = switch (dow) {
                        case MONDAY -> "Thứ 2";
                        case TUESDAY -> "Thứ 3";
                        case WEDNESDAY -> "Thứ 4";
                        case THURSDAY -> "Thứ 5";
                        case FRIDAY -> "Thứ 6";
                        case SATURDAY -> "Thứ 7";
                        case SUNDAY -> "Chủ nhật";
                    };
                    soLuongTheoThu.put(tenThu, soLuongTheoThu.get(tenThu) + 1);
                }
            }
        }

        // Tạo series cho BarChart
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName(String.format("Tuần %s - %s", ngayDauTuan, ngayCuoiTuan));

        for (String thu : thuList) {
            int soLuong = soLuongTheoThu.getOrDefault(thu, 0);
            XYChart.Data<String, Number> data = new XYChart.Data<>(thu, soLuong);
            series.getData().add(data);
        }

        // Cập nhật chart
        barChart.getData().clear();
        barChart.setLegendVisible(true);
        barChart.getData().add(series);

        // Cài tooltip
        Platform.runLater(() -> {
            for (XYChart.Data<String, Number> data : series.getData()) {
                Node node = data.getNode();
                if (node != null) {
                    Tooltip tooltip = new Tooltip(
                            String.format("%s: %d đơn", data.getXValue(), data.getYValue().intValue()));
                    Tooltip.install(node, tooltip);
                }
            }
        });
    }

    private void loadBieuDoLineChart(LocalDate ngayChon) {
        Map<Integer, Double> doanhThuTheoGio = new HashMap<>();

        // Tính doanh thu theo giờ
        for (Map.Entry<HoaDon, Double> entry : mapHoaDon.entrySet()) {
            HoaDon hd = entry.getKey();
            double tongTienSau = entry.getValue();

            LocalDateTime tgCheckOut = hd.getTgCheckOut();

            if (tgCheckOut != null && hd.getTrangthai() == 2 && tgCheckOut.toLocalDate().equals(ngayChon)) {
                int gio = tgCheckOut.getHour();
                doanhThuTheoGio.put(gio, doanhThuTheoGio.getOrDefault(gio, 0.0) + tongTienSau);
            }
        }

        // Chuẩn bị LineChart
        lineChart.getData().clear();
        lineChart.setCreateSymbols(true); // hiển thị symbol cho tất cả điểm

        // Set trục X 0-23
        NumberAxis xAxis = (NumberAxis) lineChart.getXAxis();
        xAxis.setAutoRanging(false);
        xAxis.setLowerBound(0);
        xAxis.setUpperBound(23);
        xAxis.setTickUnit(1);
        xAxis.setMinorTickCount(0);

        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName("Doanh thu ngày " + ngayChon);

        // Thêm dữ liệu cho 24 giờ
        for (int gio = 0; gio <= 23; gio++) {
            double value = doanhThuTheoGio.getOrDefault(gio, 0.0);
            XYChart.Data<Number, Number> data = new XYChart.Data<>(gio, value);
            series.getData().add(data);
        }

        lineChart.getData().add(series);

        // Cài tooltip sau khi chart render xong
        Platform.runLater(() -> {
            for (XYChart.Data<Number, Number> data : series.getData()) {
                Node node = data.getNode();
                if (node != null) {
                    Tooltip tooltip = new Tooltip(
                            String.format("Giờ: %d\nDoanh thu: %.2f triệu", data.getXValue().intValue(), data.getYValue().doubleValue()));
                    Tooltip.install(node, tooltip);
                }
            }
        });
    }


    private void loadThangNam() {
        int namHienTai = LocalDate.now().getYear();
        for (int i = 2020; i <= namHienTai; i++) {
            comboNamTK.getItems().add(String.valueOf(i));
            comboNamMon.getItems().add(String.valueOf(i));
        }
        comboThangMon.getItems().add("Tất cả");
        comboThangTK.getItems().add("Tất cả");
        for (int i = 1; i <= 12; i++) {
            comboThangMon.getItems().add(String.valueOf(i));
            comboThangTK.getItems().add(String.valueOf(i));
        }
    }

    private void loadMon() {
        String namString = comboNamMon.getValue();
        String thangString = comboThangMon.getValue();

        int nam = Integer.parseInt(namString);
        int thang = (thangString != null && !thangString.equals("Tất cả")) ? Integer.parseInt(thangString) : 0;

        // Tháng hiện tại
        LocalDate now = LocalDate.now();
        boolean isThangHienTai = (thang == now.getMonthValue() && nam == now.getYear());

        List<ChiTietHoaDon> dscthd = ChiTietHDDAO.getAllCTHDTheoThangNam(nam, thang);
        int thangTruoc = thang - 1;
        int namTruoc = nam;
        if (thangTruoc == 0) {
            thangTruoc = 12;
            namTruoc = nam - 1;
        }
        Map<String, Integer> mapThangTruoc = ChiTietHDDAO.getSoLuongTheoThangNam(namTruoc, thangTruoc);
        vboxDishList.setFillWidth(true);
        vboxDishList.getChildren().clear();

        for (ChiTietHoaDon cthd : dscthd) {
            Mon m = cthd.getMon();
            HBox hbox = new HBox(15);
            hbox.getStyleClass().add("dish-row");
            hbox.setAlignment(Pos.CENTER_LEFT);

            // Ảnh
            ImageView imageView = new ImageView();
            imageView.setFitHeight(50);
            imageView.setFitWidth(70);
            imageView.getStyleClass().add("food-image");
            imageView.setEffect(new DropShadow(4, Color.rgb(0, 0, 0, 0.15)));
            HBox.setMargin(imageView, new Insets(4, 4, 4, 4));
            String path = "/IMG/food/" + m.getHinhAnh();
            InputStream is = getClass().getResourceAsStream(path);
            if (is == null) is = getClass().getResourceAsStream("/IMG/food/restaurant.png");
            imageView.setImage(new Image(is));

            // Tên + số lượng
            VBox infoBox = new VBox(2);
            Label tenMon = new Label(m.getTenMon());
            Label soLuong = new Label("Số lượng bán trong tháng: " + cthd.getSoLuong());
            soLuong.getStyleClass().add("dish-sub");
            infoBox.getChildren().addAll(tenMon, soLuong);
            infoBox.setAlignment(Pos.CENTER_LEFT);

            Region region = new Region();
            HBox.setHgrow(region, Priority.ALWAYS);

            // ================== PHẦN TRẠNG THÁI ==================
            VBox statusBox = new VBox();
            Label status = new Label();

            if (isThangHienTai) {
                // 👉 Nếu là tháng hiện tại => dựa trên tồn kho
                if (cthd.getSoLuong() >= 1000 && m.getSoLuong() < 100) {
                    status.setText("🔥 Bán rất chạy - Cần nhập hàng ngay");
                    status.getStyleClass().add("dish-status-green");
                } else if (cthd.getSoLuong() >= 500 && m.getSoLuong() < 50) {
                    status.setText("⚠️ Cần nhập hàng gấp");
                            status.getStyleClass().add("dish-status-orange");
                } else if (cthd.getSoLuong() >= 100 && m.getSoLuong() < 100) {
                    status.setText("Nên nhập thêm hàng");
                    status.getStyleClass().add("dish-status-yellow");
                } else if (cthd.getSoLuong() >= 50) {
                    status.setText("Bán ổn định");
                    status.getStyleClass().add("dish-status-green");
                } else if (cthd.getSoLuong() < 50 && m.getSoLuong() > 100) {
                    status.setText("🛒 Cần khuyến mãi hoặc giảm giá");
                    status.getStyleClass().add("dish-status-yellow");
                } else {
                    status.setText("Ít bán");
                    status.getStyleClass().add("dish-status-red");
                }
            } else {
                // Nếu là tháng trước => đánh giá theo mức bán
                String text = trangThaiTheoSoLuong(cthd.getSoLuong());
                status.setText(text);

                // Gán style theo trạng thái
                if (text.contains("Best Seller")) {
                    status.getStyleClass().add("dish-status-red");       // đỏ nổi bật
                } else if (text.contains("Bán Rất Chạy")) {
                    status.getStyleClass().add("dish-status-orange");    // cam
                } else if (text.contains("Bán Ổn Định")) {
                    status.getStyleClass().add("dish-status-green");     // xanh lá
                } else if (text.contains("Cần Có Khuyến Mãi")) {
                    status.getStyleClass().add("dish-status-yellow");    // vàng
                } else { // Ít Người Mua
                    status.getStyleClass().add("dish-status-gray");      // xám
                }
            }

            statusBox.getChildren().add(status);

            // ================== PHẦN PHẦN TRĂM ==================
            HBox percentBox = new HBox();
            percentBox.setAlignment(Pos.CENTER);
            percentBox.getStyleClass().add("dish-inc");
            percentBox.setStyle("-fx-background-color: #4CAF50; -fx-background-radius: 0 8 8 0;");

            int soLuongThangTruoc = mapThangTruoc.getOrDefault(m.getMaMon(), 0);
            int soLuongHienTai = cthd.getSoLuong();
            double percent;
            if (soLuongThangTruoc != 0)
                percent = ((double) (soLuongHienTai - soLuongThangTruoc) / soLuongThangTruoc) * 100;
            else if (soLuongHienTai > 0)
                percent = 100;
            else
                percent = 0;

            Label percentLabel = new Label((percent >= 0 ? "↑" : "↓") + Math.abs(Math.round(percent)) + "%");
            percentLabel.getStyleClass().add("dish-row-label");
            percentBox.getChildren().add(percentLabel);

            hbox.getChildren().addAll(imageView, infoBox, region, statusBox, percentBox);
            hbox.setMaxWidth(Double.MAX_VALUE);


            vboxDishList.getChildren().add(hbox);

            hbox.setOnMouseClicked(event -> {
                if (mainController != null) {
                    mainController.setCenterContent("/FXML/QLMenu.fxml", m);
                }
            });

        }
    }

    private String trangThaiTheoSoLuong(int sl) {
        if (sl >= 1000) return "🔥 Best Seller";
        if (sl >= 500) return "Bán Rất Chạy";
        if (sl >= 200) return "Bán Ổn Định";
        if (sl >= 50) return "Cần Có Khuyến Mãi Hoặc Giảm Giá";
        return "Ít Người Mua";
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
        int in = 0, out = 0, vip = 0;
        double tongIn = 0, tongOut = 0, tongVip = 0;

        for(Map.Entry<HoaDon, Double> entry: mapHoaDon.entrySet()){
            HoaDon hd = entry.getKey();
            double tongTienSau = entry.getValue();

            LocalDate ngayLap = hd.getTgLapHD().toLocalDate();
            boolean matchNam = ngayLap.getYear() == nam;
            boolean matchThang = (thang==null) || (ngayLap.getMonthValue() == thang);
            boolean matchNgay = (ngay==null) || (ngayLap.getDayOfMonth()== ngay);
            if(matchNam && matchThang && matchNgay){
                tong+= tongTienSau;
                tongHoaDon++;
                String kv = hd.getBan().getKhuVuc().getTenKhuVuc();
                if (kv.equals("Indoor")) {
                    in++;
                    tongIn += tongTienSau;
                } else if (kv.equals("Outdoor")) {
                    out++;
                    tongOut += tongTienSau;
                } else {
                    vip++;
                    tongVip += tongTienSau;
                }
            }
        }


        double doanhThuHienTai = tong;
        double doanhThuTruoc = tinhDoanhThuKyTruoc(nam, thang, ngay, mapHoaDon);
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

    private double tinhDoanhThu(Integer nam, Integer thang, Integer ngay, Map<HoaDon, Double> mapHoaDon) {
        double tong = 0;
        for (Map.Entry<HoaDon, Double> entry : mapHoaDon.entrySet()) {
            HoaDon hd = entry.getKey();
            double tongTienSau = entry.getValue();

            LocalDate ngayLap = hd.getTgLapHD().toLocalDate();
            boolean matchNam = ngayLap.getYear() == nam;
            boolean matchThang = (thang == null) || (ngayLap.getMonthValue() == thang);
            boolean matchNgay = (ngay == null) || (ngayLap.getDayOfMonth() == ngay);

            if (matchNam && matchThang && matchNgay) {
                tong += tongTienSau;
            }
        }
        return tong;
    }

    private double tinhDoanhThuKyTruoc(Integer nam, Integer thang, Integer ngay, Map<HoaDon, Double> mapHoaDon) {
        if (ngay != null) {
            LocalDate current = LocalDate.of(nam, thang, ngay);
            LocalDate prev = current.minusDays(1);
            return tinhDoanhThu(prev.getYear(), prev.getMonthValue(), prev.getDayOfMonth(), mapHoaDon);
        } else if (thang != null) {
            YearMonth current = YearMonth.of(nam, thang);
            YearMonth prev = current.minusMonths(1);
            return tinhDoanhThu(prev.getYear(), prev.getMonthValue(), null, mapHoaDon);
        } else {
            return tinhDoanhThu(nam - 1, null, null, mapHoaDon);
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
    private void resetMon() {
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
        mapHoaDon = HoaDonDAO.getAllForThongKe();
        loadThangNam();

        reset(); // set mặc định ngày hiện tại
        resetMon();

        loadDoanhThu();
        loadMon();

        // Mặc định chọn ngày hôm nay
        datePicker.setValue(LocalDate.now());

        // Khi thay đổi ngày, reload biểu đồ
        datePicker.valueProperty().addListener((obs, oldDate, newDate) -> {
            if (newDate != null) {
                loadBieuDoLineChart(newDate);
            }
        });

        // Load lần đầu tiên
        loadBieuDoLineChart(datePicker.getValue());



        datePickerTuan.setValue(LocalDate.now());
        loadBieuDoBarChart(datePickerTuan.getValue());

        // Khi đổi ngày → tự cập nhật biểu đồ
        datePickerTuan.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) loadBieuDoBarChart(newVal);
        });

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

        comboThangMon.setOnAction(e -> loadMon());

        comboNamMon.setOnAction(e -> loadMon());
    }


}
