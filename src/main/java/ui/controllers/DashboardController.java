package ui.controllers;

import dao.HoaDonDAO;
import entity.HoaDon;
import entity.NhanVien;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.IOException;
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

    // 🧮 Thống kê
    @FXML private Label lblTongDonDangDoi;
    @FXML private Label lblTongDonDaNhan;
    @FXML private Label lblTongDonDaThanhToan;
    @FXML private Label lblDoanhThu;
    @FXML private Label lblSoKhach;
    @FXML private Label lblIn;
    @FXML private Label lblOut;
    @FXML private Label lblVip;

//moi
@FXML private BarChart<String, Number> barChart;
    @FXML private LineChart<String, Number> lineChart;


    private NhanVien nv;

    @FXML
    public void initialize() {
        if (avatarImage != null && avatarClip != null) {
            avatarImage.setClip(avatarClip);
        }

        // Load thống kê khi mở dashboard
        taiThongKeDashboard();
        hienThiTop5MonAn();
        hienThiBieuDoLuongKhachTheoGio();
    }

    // ================= SETUP NHÂN VIÊN =================
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
    }

    // ================= HIỂN THỊ NHÂN VIÊN =================
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

    // ================= TẢI THỐNG KÊ DASHBOARD =================
    private void taiThongKeDashboard() {
        try {
            // Lấy danh sách hóa đơn trong ngày
            List<HoaDon> danhSach = HoaDonDAO.getAll();

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

           int tongKhachHang = 0;
            double tongDoanhThu = 0;

            //Đếm theo trạng thái
            int donCho = 0;      // trạng thái = 0
            int donDangDung = 0; // trạng thái = 1
            int donHoanThanh = 0;// trạng thái = 2

            //Khu vuc
            int in =0;
            int out =0;
            int vip =0;

            // Duyệt danh sách hóa đơn
            for (HoaDon hd : danhSach) {
                if (hd == null) continue;

                tongKhachHang+=hd.getSoLuong();
                tongDoanhThu += hd.getTongTienSau();

                // Đếm theo trạng thái
                int tt = hd.getTrangthai();
                if (tt == 0) {
                    donCho++;
                } else if (tt == 1) {
                    donDangDung++;
                } else if (tt == 2) {
                    donHoanThanh++;
                }
                //Đếm theo khu vực
                String maKV = hd.getBan().getKhuVuc().getMaKhuVuc();
                if(maKV.equals("KV0001")){
                    in++;
                }
                else if(maKV.equals("KV0002")){
                    out++;
                }
                else if(maKV.equals("KV0003")){
                    vip++;
                }
            }

            // Cập nhật hiển thị
            lblTongDonDangDoi.setText(String.valueOf("Số đơn đang đợi: "+donCho));
            lblTongDonDaNhan.setText(String.valueOf("Số đơn đang dùng: "+donDangDung));
            lblTongDonDaThanhToan.setText(String.valueOf("Số đơn đã thanh toán: "+donHoanThanh));
            lblIn.setText(String.valueOf("Khu vực In: "+in));
            lblOut.setText(String.valueOf("Khu vực Out: "+out));
            lblVip.setText(String.valueOf("Khu vực Vip: "+vip));
            lblDoanhThu.setText(String.format("%,.0f đ", tongDoanhThu));
            lblSoKhach.setText(String.valueOf(tongKhachHang));


            //In ra log cho dễ kiểm tra (hoặc có thể hiển thị lên UI)
            System.out.println("Đơn chờ: " + donCho);
            System.out.println("Đơn đang dùng: " + donDangDung);
            System.out.println("Đơn hoàn thành: " + donHoanThanh);

        } catch (Exception e) {
            System.err.println("[DashboardController] ❌ Lỗi tải thống kê: " + e.getMessage());
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

        // Đếm số lượng bán mỗi món
        Map<String, Integer> soLuongTheoMon = new HashMap<>();
        for (ChiTietHoaDon ct : dsChiTiet) {
            if (ct.getMon() == null) continue;
            String tenMon = ct.getMon().getTenMon();
            int soLuong = ct.getSoLuong();
            soLuongTheoMon.put(tenMon, soLuongTheoMon.getOrDefault(tenMon, 0) + soLuong);
        }

        // Sắp xếp giảm dần và lấy top 5
        List<Map.Entry<String, Integer>> top5 = soLuongTheoMon.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(5)
                .toList();

        // Hiển thị lên BarChart
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Top 5 món bán chạy");

        for (Map.Entry<String, Integer> entry : top5) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        barChart.getData().clear();
        barChart.getData().add(series);
    }
    private void hienThiBieuDoLuongKhachTheoGio() {
        List<HoaDon> danhSach = HoaDonDAO.getAll();
        if (danhSach == null || danhSach.isEmpty()) return;

        // Tạo map 0-23 giờ ban đầu, tất cả = 0 khách
        Map<Integer, Integer> khachTheoGio = new LinkedHashMap<>();
        for (int i = 0; i < 24; i++) {
            khachTheoGio.put(i, 0);
        }

        // Lặp qua danh sách hóa đơn trong ngày hiện tại
        for (HoaDon hd : danhSach) {
            if (hd == null || hd.getTgCheckIn() == null) continue;
//            if (!hd.getTgCheckIn().toLocalDate().equals(java.time.LocalDate.now())) continue;

            int gio = hd.getTgCheckIn().getHour();
            int soKhach = hd.getSoLuong();
            khachTheoGio.put(gio, khachTheoGio.get(gio) + soKhach);
        }

        // Tạo dữ liệu cho biểu đồ
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Lượng khách theo giờ");

        for (int i = 0; i < 24; i++) {
            series.getData().add(new XYChart.Data<>(i + "h", khachTheoGio.get(i)));
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
}
