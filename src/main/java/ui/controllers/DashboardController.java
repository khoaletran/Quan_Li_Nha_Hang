package ui.controllers;

import dao.HoaDonDAO;
import dao.MonDAO;
import entity.HoaDon;
import entity.Mon;
import entity.NhanVien;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

//import nối qua thống kê
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.Parent;

//import hiện dữ liệu khi hover
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.control.Tooltip;


import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import javafx.scene.chart.*;
import dao.ChiTietHDDAO;
import entity.ChiTietHoaDon;

public class DashboardController {

    @FXML private Label lblMaNV;
    @FXML private Label lblTenNV;
    @FXML private Label lblSDT;
    @FXML private Label lblChucVu;
    @FXML private Label lblGioiTinh;
    @FXML private Label lblNgayVaoLam;
    @FXML private ImageView avatarImage;
    @FXML private Circle avatarClip;
    @FXML private VBox box_not;

    //thống kê
    @FXML private Label lblTongDonDangDoi;
    @FXML private Label lblTongDonDaNhan;
    @FXML private Label lblTongDonDaThanhToan;
    @FXML private Label lblDoanhThu;
    @FXML private Label lblSoKhach;
    @FXML private Label lblIn;
    @FXML private Label lblOut;
    @FXML private Label lblVip;

    //biểu đồ
    @FXML private BarChart<String, Number> barChart;
    @FXML private LineChart<String, Number> lineChart;

    private NhanVien nv;

    @FXML
    public void initialize() {
        if (avatarImage != null && avatarClip != null) {
            avatarImage.setClip(avatarClip);
        }
        // tắt animation để node không bị tái tạo gây mất listener
        if (barChart != null) barChart.setAnimated(false);
        if (lineChart != null) lineChart.setAnimated(false);

        //load thống kê với biểu đồ
        taiThongKeDashboard();
        hienThiTop5MonAn();
        hienThiBieuDoLuongKhachTheoGio();
        // du liệu hover
        // chạy trong initialize()
        if (barChart != null) {
            barChart.getData().addListener((ListChangeListener<XYChart.Series<String, Number>>) change -> {
                while (change.next()) {
                    if (change.wasAdded()) Platform.runLater(this::attachBarTooltips);
                }
            });
        }

        if (lineChart != null) {
            lineChart.getData().addListener((ListChangeListener<XYChart.Series<String, Number>>) change -> {
                while (change.next()) {
                    if (change.wasAdded()) Platform.runLater(this::attachLinePointTooltips);
                }
            });
        }

    }

    //Nhân viên
    public void setMainController(Object controller) {
        if (controller instanceof MainController_NV nvCtrl) {
            this.nv = nvCtrl.getNhanVien();
        } else if (controller instanceof MainController_QL qlCtrl) {
            this.nv = qlCtrl.getNhanVien();
        }

        if (nv == null) {
            System.out.println("[DashboardController] ⚠️ NhanVien chưa được truyền vào!");
            return;
        }

        hienThiThongTinNhanVien();
    }

    public void setNhanVien(NhanVien nv) {
        this.nv = nv;
        hienThiThongTinNhanVien();

        kiemTraThongBaoCheckIn();
        batDongBoTimelineThongBao();
    }

    //hiển thị nhân viên
    private void hienThiThongTinNhanVien() {
        if (nv == null) return;

        lblMaNV.setText("Mã Nhân Viên: " + nv.getMaNV());
        lblTenNV.setText("Họ Và Tên: " + nv.getTenNV());
        lblSDT.setText("SĐT: " + nv.getSdt());
        lblChucVu.setText("Chức Vụ: " + (nv.isQuanLi() ? "Quản Lý" : "Nhân Viên"));
        lblGioiTinh.setText("Giới Tính: " + (nv.isGioiTinh() ? "Nam" : "Nữ"));

        if (nv.getNgayVaoLam() != null) {
            lblNgayVaoLam.setText("Ngày Vào Làm: " +
                    nv.getNgayVaoLam().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        } else {
            lblNgayVaoLam.setText("Ngày Vào Làm: -");
        }

        try {
            String imgPath = nv.isGioiTinh()
                    ? "/IMG/icon/man.png"
                    : "/IMG/icon/woman.png";
            avatarImage.setImage(new Image(getClass().getResourceAsStream(imgPath)));
        } catch (Exception e) {
            avatarImage.setImage(new Image(getClass().getResourceAsStream("/IMG/avatar.png")));
        }
    }

    //thống kê
//    private void taiThongKeDashboard() {
//        try {
//            List<HoaDon> danhSach = HoaDonDAO.getAll(); //cái này mai mốt sửa lại lấy hóa đơn trong ngày
//
//            if (danhSach == null || danhSach.isEmpty()) {
//                lblTongDonDangDoi.setText("0");
//                lblTongDonDaNhan.setText("0");
//                lblTongDonDaThanhToan.setText("0");
//                lblDoanhThu.setText("0đ");
//                lblSoKhach.setText("0");
//                lblIn.setText("0");
//                lblOut.setText("0");
//                lblVip.setText("0");
//                return;
//            }
//
//            //trạng thái
//            int donCho = 0;      // trạng thái = 0
//            int donDangDung = 0; // trạng thái = 1
//            int donHoanThanh = 0;// trạng thái = 2
//
//            //Khu vuc
//            int in =0;
//            int out =0;
//            int vip =0;
//
//            int tongKhachHang = 0;
//            double tongDoanhThu = 0;
//
//            for (HoaDon hd : danhSach) {
//                if (hd == null) continue;
//
//                //Đếm theo trạng thái
//                int tt = hd.getTrangthai();
//                if (tt == 0) {
//                    donCho++;
//                }
//                else if (tt == 1) {
//                    donDangDung++;
//                }
//                else if (tt == 2) {
//                    donHoanThanh++;
//                }
//                //Đếm theo khu vực
//                String maKV = hd.getBan().getKhuVuc().getMaKhuVuc();
//                if(maKV.equals("KV0001")){
//                    in++;
//                }
//                else if(maKV.equals("KV0002")){
//                    out++;
//                }
//                else if(maKV.equals("KV0003")){
//                    vip++;
//                }
//                //tổng khách hàng với doanh thu
//                tongKhachHang+=hd.getSoLuong();
//                tongDoanhThu+=hd.getTongTienSau();
//            }
//
//            //hiển thị
//            lblTongDonDangDoi.setText(String.valueOf("Số đơn đang đợi: "+donCho));
//            lblTongDonDaNhan.setText(String.valueOf("Số đơn đang dùng: "+donDangDung));
//            lblTongDonDaThanhToan.setText(String.valueOf("Số đơn đã thanh toán: "+donHoanThanh));
//            lblIn.setText(String.valueOf("Khu vực In: "+in));
//            lblOut.setText(String.valueOf("Khu vực Out: "+out));
//            lblVip.setText(String.valueOf("Khu vực Vip: "+vip));
//            lblDoanhThu.setText(String.format("%,.0f đ", tongDoanhThu));
//            lblSoKhach.setText(String.valueOf(tongKhachHang));
//
//
//            //In ra log cho dễ kiểm tra (hoặc có thể hiển thị lên UI)
////            System.out.println("Đơn chờ: " + donCho);
////            System.out.println("Đơn đang dùng: " + donDangDung);
////            System.out.println("Đơn hoàn thành: " + donHoanThanh);
//
//        } catch (Exception e) {
//            System.err.println("[DashboardController] Lỗi tải thống kê: " + e.getMessage());
//            lblTongDonDangDoi.setText("-");
//            lblTongDonDaNhan.setText("-");
//            lblTongDonDaThanhToan.setText("-");
//            lblDoanhThu.setText("-");
//            lblSoKhach.setText("-");
//            lblIn.setText("-");
//            lblOut.setText("-");
//            lblVip.setText("-");
//        }
//    }
    private void taiThongKeDashboard() {
        try {
            Map<HoaDon, Double> danhSach = HoaDonDAO.getAllForThongKe(); // Lấy dữ liệu đã tính sẵn từ DB

            if (danhSach == null || danhSach.isEmpty()) {
                lblTongDonDangDoi.setText("0");
                lblTongDonDaNhan.setText("0");
                lblTongDonDaThanhToan.setText("0");
                lblDoanhThu.setText("0đ");
                lblSoKhach.setText("0");
                lblIn.setText("0");
                lblOut.setText("0");
                lblVip.setText("0");
                return;
            }

            // Trạng thái
            int donCho = 0;
            int donDangDung = 0;
            int donHoanThanh = 0;

            // Khu vực
            int in = 0;
            int out = 0;
            int vip = 0;

            int tongKhachHang = 0;
            double tongDoanhThu = 0;

            for (Map.Entry<HoaDon, Double> entry : danhSach.entrySet()) {
                HoaDon hd = entry.getKey();
                double tongSau = entry.getValue();

                // Đếm trạng thái
                int tt = hd.getTrangthai();
                if (tt == 0) donCho++;
                else if (tt == 1) donDangDung++;
                else if (tt == 2) donHoanThanh++;

                // Đếm khu vực
                String maKV = hd.getBan().getKhuVuc().getMaKhuVuc();
                if (maKV.equals("KV0001")) in++;
                else if (maKV.equals("KV0002")) out++;
                else if (maKV.equals("KV0003")) vip++;

                // Tổng khách hàng & doanh thu
                tongKhachHang += hd.getSoLuong();
                tongDoanhThu += tongSau;
            }

            // Hiển thị
            lblTongDonDangDoi.setText("Số đơn đang đợi: " + donCho);
            lblTongDonDaNhan.setText("Số đơn đang dùng: " + donDangDung);
            lblTongDonDaThanhToan.setText("Số đơn đã thanh toán: " + donHoanThanh);
            lblIn.setText("Khu vực In: " + in);
            lblOut.setText("Khu vực Out: " + out);
            lblVip.setText("Khu vực Vip: " + vip);
            lblDoanhThu.setText(String.format("%,.0f đ", tongDoanhThu));
            lblSoKhach.setText(String.valueOf(tongKhachHang));

        } catch (Exception e) {
            System.err.println("[DashboardController] Lỗi tải thống kê: " + e.getMessage());
            lblTongDonDangDoi.setText("-");
            lblTongDonDaNhan.setText("-");
            lblTongDonDaThanhToan.setText("-");
            lblDoanhThu.setText("-");
            lblSoKhach.setText("-");
            lblIn.setText("-");
            lblOut.setText("-");
            lblVip.setText("-");
        }
    }

    private void hienThiTop5MonAn() {
        ChiTietHDDAO cthdDAO = new ChiTietHDDAO();
        List<ChiTietHoaDon> dsChiTiet = cthdDAO.getAll(); // Lấy toàn bộ chi tiết hóa đơn

        if (dsChiTiet == null || dsChiTiet.isEmpty()) return;

        //tạo map để chứa
        Map<String, Integer> soLuongTheoMon = new HashMap<>();
        for (ChiTietHoaDon ct : dsChiTiet) {
            if (ct.getMon() == null) continue;
            String tenMon = ct.getMon().getTenMon();
            int soLuong = ct.getSoLuong();
            soLuongTheoMon.put(tenMon, soLuongTheoMon.getOrDefault(tenMon, 0) + soLuong);
            // put vào map, getOrdefault là nếu món chưa có thì lấy mặc định là 0, có thì lấy tổng trước đó
        }

        // Sắp xếp giảm dần để lấy top 5
        List<Map.Entry<String, Integer>> top5 = soLuongTheoMon.entrySet().stream() //entry chuyền về cặp key value
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(5)
                .toList();

        // Hiển thị lên barchart, biểu đồ cột
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Top 5 món bán chạy");

        for (Map.Entry<String, Integer> entry : top5) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        barChart.getData().clear();
        barChart.getData().add(series);

        // dữ liệu biểu đồ hover

        // sau khi thêm dữ liệu vào barChart

// gắn tooltip ngay sau khi dữ liệu đã được thêm
        attachBarTooltips();

    }
    private void hienThiBieuDoLuongKhachTheoGio() {
        List<HoaDon> danhSach = HoaDonDAO.getAll(); // cái này sẽ lấy hd trong ngày
        if (danhSach == null || danhSach.isEmpty()) return;

        // Tạo map 0-23 giờ, khách = 0
        Map<Integer, Integer> khachTheoGio = new LinkedHashMap<>();
        for (int i = 0; i < 24; i++) {
            khachTheoGio.put(i, 0);
        }

        for (HoaDon hd : danhSach) {
            if (hd == null || hd.getTgCheckIn() == null) continue;
//            if (!hd.getTgCheckIn().toLocalDate().equals(java.time.LocalDate.now())) continue;

            int gio = hd.getTgCheckIn().getHour();
            int soKhach = hd.getSoLuong();
            khachTheoGio.put(gio, khachTheoGio.get(gio) + soKhach);
        }

        // Hiển thị lên biểu đồ đường
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Lượng khách theo giờ");

        for (int i = 0; i < 24; i++) {
            series.getData().add(new XYChart.Data<>(i + "", khachTheoGio.get(i)));
        }

        lineChart.getData().clear();
        lineChart.getData().add(series);
        lineChart.setAnimated(false);
        lineChart.setCreateSymbols(true);

        // Cấu hình trục Y tự động hiển thị hợp lý
        NumberAxis yAxis = (NumberAxis) lineChart.getYAxis();
        yAxis.setAutoRanging(true);
        yAxis.setForceZeroInRange(false);

        // Cấu hình trục X hiển thị rõ ràng
        CategoryAxis xAxis = (CategoryAxis) lineChart.getXAxis();
        xAxis.setTickLabelRotation(0); // để chữ nằm ngang

        //dữ liệu khi hover
        // gắn tooltip cho các điểm line
        attachLinePointTooltips();
    }

    //hiển thị dữ liệu khi hover biểu đồ

    // Gắn tooltip cho BarChart (các cột)
    private void attachBarTooltips() {
        if (barChart == null) return;

        // Đảm bảo chạy trên JavaFX thread sau khi node được dựng xong
        Platform.runLater(() -> {
            for (XYChart.Series<String, Number> series : barChart.getData()) {
                for (XYChart.Data<String, Number> data : series.getData()) {
                    if (data.getNode() != null) {
                        installBarTooltip(data);
                    } else {
                        data.nodeProperty().addListener((obs, oldNode, newNode) -> {
                            if (newNode != null) installBarTooltip(data);
                        });
                    }
                }
            }
        });
    }

    private void installBarTooltip(XYChart.Data<String, Number> data) {
        // debug: in ra thông tin để kiểm tra
        System.out.println("[DEBUG] installBarTooltip for: " + data.getXValue() + " = " + data.getYValue()
                + " nodeExists=" + (data.getNode() != null));

        Tooltip tooltip = new Tooltip();
        tooltip.setText(String.format("%s\nSố lượng: %s", data.getXValue(), data.getYValue()));

        // cập nhật tooltip nếu giá trị thay đổi
        data.YValueProperty().addListener((obs, oldV, newV) ->
                tooltip.setText(String.format("%s\nSố lượng: %s", data.getXValue(), newV))
        );

        // Nếu node đã có, gắn luôn; nếu chưa có, listener nodeProperty đã được thêm ở attachBarTooltips()
        Node node = data.getNode();
        if (node != null) {
            Tooltip.install(node, tooltip);
            // đảm bảo vùng hover dễ chạm
            node.setOnMouseEntered(e -> {
                node.getStyleClass().add("chart-bar-hover");
                // fallback: show tooltip tại vị trí con trỏ (hữu ích nếu Tooltip.install không hiện)
                try {
                    tooltip.show(node, e.getScreenX() + 10, e.getScreenY() + 10);
                } catch (IllegalStateException ex) { /* ignore */ }
            });
            node.setOnMouseMoved(e -> {
                // di chuyển tooltip theo chuột khi hover
                if (tooltip.isShowing()) {
                    tooltip.setAnchorX(e.getScreenX() + 10);
                    tooltip.setAnchorY(e.getScreenY() + 10);
                }
            });
            node.setOnMouseExited(e -> {
                node.getStyleClass().remove("chart-bar-hover");
                tooltip.hide();
            });
            // tăng "hit area" nếu cần (thay đổi size style nếu muốn)
            node.setStyle("-fx-padding: 4;");
        } else {
            // nếu node chưa có, lắng nghe khi node tạo
            data.nodeProperty().addListener((obs, oldNode, newNode) -> {
                if (newNode != null) {
                    installBarTooltip(data); // gọi lại chính nó khi node sẵn sàng
                }
            });
        }
    }

    // Gắn tooltip cho LineChart (các điểm)
    private void attachLinePointTooltips() {
        if (lineChart == null) return;

        Platform.runLater(() -> {
            for (XYChart.Series<String, Number> series : lineChart.getData()) {
                for (XYChart.Data<String, Number> data : series.getData()) {
                    if (data.getNode() != null) {
                        installLineTooltip(data, series.getName());
                    } else {
                        data.nodeProperty().addListener((obs, oldNode, newNode) -> {
                            if (newNode != null) installLineTooltip(data, series.getName());
                        });
                    }
                }
            }
        });
    }

    // Thay thế installLineTooltip hiện tại bằng version này
    private void installLineTooltip(XYChart.Data<String, Number> data, String seriesName) {
        System.out.println("[DEBUG] installLineTooltip for: " + seriesName + " - " + data.getXValue() + "=" + data.getYValue()
                + " nodeExists=" + (data.getNode() != null));

        String title = (seriesName == null ? "" : seriesName + "\n");
        Tooltip tooltip = new Tooltip(String.format("%sGiờ: %s\nLượng: %s", title, data.getXValue(), data.getYValue()));

        data.YValueProperty().addListener((obs, oldV, newV) ->
                tooltip.setText(String.format("%sGiờ: %s\nLượng: %s", title, data.getXValue(), newV))
        );

        Node node = data.getNode();
        if (node != null) {
            Tooltip.install(node, tooltip);

            node.setOnMouseEntered(e -> {
                try { tooltip.show(node, e.getScreenX() + 10, e.getScreenY() + 10); } catch (IllegalStateException ex) {}
            });
            node.setOnMouseMoved(e -> {
                if (tooltip.isShowing()) {
                    tooltip.setAnchorX(e.getScreenX() + 10);
                    tooltip.setAnchorY(e.getScreenY() + 10);
                }
            });
            node.setOnMouseExited(e -> tooltip.hide());

            // tăng vùng nhạy cho symbol điểm (nếu symbol quá nhỏ)
            node.setStyle("-fx-padding: 6;");
        } else {
            data.nodeProperty().addListener((obs, oldNode, newNode) -> {
                if (newNode != null) installLineTooltip(data, seriesName);
            });
        }
    }




    // ================= ĐỔI MẬT KHẨU =================
    @FXML
    private void showChangePassword() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/login.fxml"));
            Parent loginRoot = loader.load();

            LoginController loginController = loader.getController();
            loginController.setNhanVien(nv);
            loginController.showResetPane();

            Stage stage = new Stage();
            stage.setScene(new Scene(loginRoot));
            stage.setTitle("Đổi Mật Khẩu");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void batDongBoTimelineThongBao() {
        Timeline timeline = new Timeline(
                new KeyFrame(javafx.util.Duration.seconds(60), e -> kiemTraThongBaoCheckIn())
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }


    private void kiemTraThongBaoCheckIn() {
        if (box_not == null) return;
        if(nv != null && nv.isQuanLi()){
            MonDAO monAnDAO = new MonDAO();
            box_not.getChildren().clear();

            List<Mon> danhSachMon = monAnDAO.getAll();

            for (Mon monAn : danhSachMon) {
                int soLuong = monAn.getSoLuong();
                if(soLuong == 0){
                    box_not.getChildren().add(taoThongBaoMonAn("Món ăn đã hết",monAn,true));
                }else if(soLuong <= 10){
                    box_not.getChildren().add(taoThongBaoMonAn("Món ăn sắp hết",monAn,false));
                }
            }
        }else{
            HoaDonDAO hoaDonDAO = new HoaDonDAO();
            box_not.getChildren().clear();
            List<HoaDon> danhSachHoaDon = hoaDonDAO.getAllToday();
            LocalDateTime now = LocalDateTime.now();
            danhSachHoaDon.sort(Comparator.comparing(HoaDon::getTgCheckIn).reversed());

            for (HoaDon hd : danhSachHoaDon) {
                LocalDateTime tgCheckIn = hd.getTgCheckIn();
                if (tgCheckIn == null) continue;

                if (hd.getTrangthai() != 0) continue; // chỉ lấy hóa đơn đang chờ

                LocalDateTime checkInEarly = tgCheckIn.minusMinutes(15);
                LocalDateTime checkInLate  = tgCheckIn.plusMinutes(15);

                boolean chuaDenGio = false;
                boolean denGio = false;
                boolean quaGio = false;

                // 1) Chưa đến giờ check-in (trước checkInEarly)
                if (now.isBefore(checkInEarly)) {
                    chuaDenGio = true;
                }
                // 2) Đúng giờ check-in
                else if (!now.isBefore(checkInEarly) && !now.isAfter(checkInLate)) {
                    denGio = true;
                }
                // 3) Quá giờ check-in
                else if (now.isAfter(checkInLate)) {
                    quaGio = true;
                }

                if (chuaDenGio) {
                    box_not.getChildren().add(taoThongBaoHoaDon(hd, 0)); // 0 = chưa đến giờ
                } else if (denGio) {
                    box_not.getChildren().add(taoThongBaoHoaDon(hd, 1)); // 1 = đến giờ
                } else if (quaGio) {
                    box_not.getChildren().add(taoThongBaoHoaDon(hd, 2)); // 2 = quá giờ
                }
            }

        }
    }

    private Node taoThongBaoMonAn(String trangThaiMon, Mon mon, boolean hetMon) {

        HBox box = new HBox();
        box.getStyleClass().add("notif-item");
        box.setSpacing(10);
        box.setAlignment(Pos.CENTER_LEFT);

        // LEFT
        VBox left = new VBox();

        left.setSpacing(4);

        Label tonKho = new Label("Tồn kho: " + mon.getSoLuong());
        tonKho.getStyleClass().addAll("notif-sub","notif-stock");

        left.getChildren().addAll(tonKho);

        // RIGHT
        VBox right = new VBox();
        right.setSpacing(2);

        Label lbTenMon = new Label(mon.getTenMon());
        lbTenMon.getStyleClass().add("notif-sub-right-02");

        Label status = new Label(trangThaiMon);
        status.getStyleClass().add(hetMon ? "notif-status-red" : "notif-status-yellow");
        left.getStyleClass().addAll("notif-left",hetMon ? "notif-backgr-red" : "notif-backgr-yellow");
        right.getChildren().addAll(lbTenMon, status);

        box.getChildren().addAll(left, right);

        return box;
    }

    private Node taoThongBaoHoaDon(HoaDon hd, int trangThai) {
        HBox box = new HBox();
        box.getStyleClass().add("notif-item");
        box.setSpacing(10);
        box.setAlignment(Pos.CENTER_LEFT);

        VBox left = new VBox();
        left.setSpacing(4);

        Label lbTenBan = new Label("Mã bàn: "+hd.getBan().getMaBan());
        lbTenBan.getStyleClass().addAll("notif-sub");

        String time = hd.getTgCheckIn() != null
                ? hd.getTgCheckIn().toLocalTime().toString()
                : "--:--";

        Label lbCheckInTime = new Label("Thời gian: "+time);
        lbCheckInTime.getStyleClass().addAll("notif-sub");

        left.getChildren().addAll(lbTenBan, lbCheckInTime);

        VBox right = new VBox();
        right.setSpacing(4);

        Label tenKH = new Label("Tên khách: " + hd.getKhachHang().getTenKhachHang());
        tenKH.getStyleClass().add("notif-sub-right");

        Label sdt = new Label("SĐT: " + hd.getKhachHang().getSdt());
        sdt.getStyleClass().add("notif-sub-right");

        Label status = new Label();
        switch (trangThai) {
            case 0 -> {
                status.setText("Chưa đến giờ check-in");
                status.getStyleClass().add("notif-status-yellow");
                left.getStyleClass().addAll("notif-left", "notif-backgr-yellow");
            }
            case 1 -> {
                status.setText("Đã đến giờ hẹn");
                status.getStyleClass().add("notif-status-green");
                left.getStyleClass().addAll("notif-left", "notif-backgr-green");
            }
            case 2 -> {
                status.setText("Đã quá giờ hẹn");
                status.getStyleClass().add("notif-status-red");
                left.getStyleClass().addAll("notif-left", "notif-backgr-red");
            }
        }

        right.getChildren().addAll(tenKH, sdt, status);
        box.getChildren().addAll(left, right);

        return box;
    }



//    private Node taoThongBaoHoaDon(HoaDon hd, boolean denGio) {
//        HBox box = new HBox();
//        box.getStyleClass().add("notif-item");
//        box.setSpacing(10);
//        box.setAlignment(Pos.CENTER_LEFT);
//
//        // ==== LEFT COLUMN ====
//        VBox left = new VBox();
//        left.setSpacing(4);
//
//        Label lbTenBan = new Label("Mã bàn: "+hd.getBan().getMaBan());
//        lbTenBan.getStyleClass().addAll("notif-sub");
//
//        String time = hd.getTgCheckIn() != null
//                ? hd.getTgCheckIn().toLocalTime().toString()
//                : "--:--";
//
//        Label lbCheckInTime = new Label("Thời gian: "+time);
//        lbCheckInTime.getStyleClass().addAll("notif-sub");
//
//        left.getChildren().addAll(lbTenBan, lbCheckInTime);
//
//        // ==== RIGHT COLUMN ====
//        VBox right = new VBox();
//        right.setSpacing(4);
//
//        Label tenKH = new Label("tên khách : " + hd.getKhachHang().getTenKhachHang());
//        tenKH.getStyleClass().add("notif-sub-right");
//
//        Label sdt = new Label("SĐT : " + hd.getKhachHang().getSdt());
//        sdt.getStyleClass().add("notif-sub-right");
//
//        Label status = new Label(denGio ? "Đã đến giờ hẹn" : "Đã quá giờ hẹn");
//        status.getStyleClass().add(denGio ? "notif-status-green" : "notif-status-red");
//        left.getStyleClass().addAll("notif-left",denGio ? "notif-backgr-green" : "notif-backgr-red");
//
//        right.getChildren().addAll(tenKH, sdt, status);
//
//        // Add left-right vào item
//        box.getChildren().addAll(left, right);
//
//        return box;
//    }
//    //nối qua thống kê
//    @FXML private BorderPane rootPane; // nếu Dashboard.fxml có BorderPane chính
//    // hoặc các control khác: @FXML private BarChart<?,?> barChart; ...
//
//    @FXML
//    private void openThongKe(MouseEvent event) {
//        try {
//            // Load FXML Thống kê
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/ThongKe.fxml"));
//            Parent thongKeRoot = loader.load();
//
//            // Nếu bạn có một main BorderPane ở cấp cao hơn với fx:id="mainRoot",
//            // tìm nó qua scene lookup (phổ biến khi Dashboard nằm trong mainRoot center)
//            Node mainRootNode = rootPane.getScene().lookup("#mainRoot");
//            if (mainRootNode instanceof BorderPane) {
//                BorderPane mainRoot = (BorderPane) mainRootNode;
//                mainRoot.setCenter(thongKeRoot);
//            } else {
//                // fallback: thay root của scene (ít khuyến nghị nếu bạn có header/menu)
//                rootPane.getScene().setRoot(thongKeRoot);
//            }
//
//            // Nếu cần truyền dữ liệu vào ThongKeController:
//            // ThongKeController tk = loader.getController();
//            // tk.initData(someFilterOrModel);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//            // TODO: show Alert thông báo lỗi load FXML
//        }
//    }
}
