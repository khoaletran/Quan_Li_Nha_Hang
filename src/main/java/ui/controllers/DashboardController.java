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
    @FXML private Label lblKhuVuc;
    @FXML private Label lblIn;
    @FXML private Label lblOut;
    @FXML private Label lblVip;

    private NhanVien nv;

    @FXML
    public void initialize() {
        if (avatarImage != null && avatarClip != null) {
            avatarImage.setClip(avatarClip);
        }

        // Load thống kê khi mở dashboard
        taiThongKeDashboard();
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
            List<HoaDon> danhSach = HoaDonDAO.getAllNgayHomNay();

            if (danhSach == null || danhSach.isEmpty()) {
                lblTongDonDangDoi.setText("0");
                lblTongDonDaNhan.setText("0");
                lblTongDonDaThanhToan.setText("0");
                lblDoanhThu.setText("0đ");
                lblSoKhach.setText("0");
                lblKhuVuc.setText("0");
                return;
            }

           int tongKhachHang = 0;
            double tongDoanhThu = 0;
            Set<String> tapKhachHang = new HashSet<>();
            Set<String> tapKhuVuc = new HashSet<>();

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

                // Đếm khách hàng duy nhất
                if (hd.getKhachHang() != null && hd.getKhachHang().getMaKhachHang() != null) {
                    tapKhachHang.add(hd.getKhachHang().getMaKhachHang());
                }

                // Đếm khu vực duy nhất
                if (hd.getBan() != null && hd.getBan().getKhuVuc() != null) {
                    String tenKV = hd.getBan().getKhuVuc().getTenKhuVuc();
                    if (tenKV != null) tapKhuVuc.add(tenKV);
                }

                // Đếm theo trạng thái
                int tt = hd.getTrangthai();
                if (tt == 0) {
                    donCho++;
                } else if (tt == 1) {
                    donDangDung++;
                } else if (tt == 2) {
                    donHoanThanh++;
                }

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

            // ✅ Cập nhật hiển thị
            lblTongDonDangDoi.setText(String.valueOf("Số đơn đang đợi: "+donCho));
            lblTongDonDaNhan.setText(String.valueOf("Số đơn đang dùng: "+donDangDung));
            lblTongDonDaThanhToan.setText(String.valueOf("Số đơn đã thanh toán: "+donHoanThanh));
            lblIn.setText(String.valueOf("Khu vực IN: "+in));
            lblOut.setText(String.valueOf("Khu vực Out: "+out));
            lblVip.setText(String.valueOf("Khu vực Vip: "+vip));
            lblDoanhThu.setText(String.format("%,.0f đ", tongDoanhThu));
            lblSoKhach.setText(String.valueOf(tongKhachHang));
//            lblKhuVuc.setText(String.valueOf(tapKhuVuc.size()));


            // 💬 In ra log cho dễ kiểm tra (hoặc có thể hiển thị lên UI)
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
            lblKhuVuc.setText("-");
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
}
