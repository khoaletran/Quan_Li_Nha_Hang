package ui.controllers;

import dao.ChiTietHDDAO;
import dao.HoaDonDAO;
import dao.PhieuKetCaDAO;
import entity.ChiTietHoaDon;
import entity.HoaDon;
import entity.NhanVien;
import entity.PhieuKetCa;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.geometry.Pos;
import ui.AlertCus;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class BanGiaoCaController {
    @FXML private VBox vboxHoaDon;
    @FXML private TextField txtCaLam,txtTGVC,txtsLHD,txtSoTienMat,txtSoTienCK,txtTongTien;
    @FXML private TextArea taMoTa;
    @FXML private Label lblTienMat,lblCKhoan,lblsoHD,lblDThu;
    @FXML private Button btnKetCa;

    private HoaDonDAO hoaDonDAO = new HoaDonDAO();
    private NhanVien nhanVien;
    private LocalDateTime thoiGianVaoCa;


    @FXML
    public void initialize() {
        btnKetCa.setOnAction(e -> ketCa());

    }

    public void initData(NhanVien nv) {
        this.nhanVien = nv;
        loadTatCaHoaDon();
    }

    private void loadTatCaHoaDon() {
        int slHoaDon = 0;
        double tongTienMat = 0;
        double tongTienCK = 0;
        double tienHoaDon = 0;

        vboxHoaDon.getChildren().clear();

        if (nhanVien == null || thoiGianVaoCa == null) return;

        List<HoaDon> danhSach = hoaDonDAO.getTheoMaNV(nhanVien.getMaNV());
        for (HoaDon hd : danhSach) {

            // Lọc theo trạng thái
            //if (hd.getTrangthai() == 0) continue;

            LocalDateTime tgCheckout = hd.getTgCheckOut();
            if (tgCheckout == null) continue;

            // Lọc theo ngày và giờ
            if (!tgCheckout.toLocalDate().equals(thoiGianVaoCa.toLocalDate())) continue;
            if (!tgCheckout.isAfter(thoiGianVaoCa)) continue;

            List<ChiTietHoaDon> dsCTHD = ChiTietHDDAO.getByMaHD(hd.getMaHD());

            for (ChiTietHoaDon ct : dsCTHD) {
                tienHoaDon += ct.getMon().getGiaGoc() * ct.getSoLuong();
            }

            if (hd.getKhuyenMai() != null) {
                double giamGia = hd.getKhuyenMai().getPhanTRamGiamGia();
                tienHoaDon = tienHoaDon * (1 - giamGia / 100.0);
            }
            tienHoaDon = tienHoaDon * 1.1;

            if (hd.isKieuThanhToan()) {
                tongTienCK += tienHoaDon; // Chuyển khoản
            } else {
                tongTienMat += tienHoaDon; // Tiền mặt
            }

            Circle circle = new Circle(20);
            circle.setStyle("-fx-fill: #d8d8d8;");

            Label lblMaHD = new Label(hd.getMaHD());
            lblMaHD.getStyleClass().add("invoice-id");

            Label lblSDT = new Label("SDT: " + hd.getKhachHang().getSdt());
            lblSDT.getStyleClass().add("invoice-phone");

            VBox boxThongTin = new VBox(2, lblMaHD, lblSDT);

            HBox card = new HBox(10, circle, boxThongTin);
            card.setAlignment(Pos.CENTER_LEFT);
            card.getStyleClass().add("invoice-card");
            VBox.setMargin(card, new Insets(5, 0, 5, 0));

            vboxHoaDon.getChildren().add(card);
            slHoaDon++;
        }
        NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));
        lblsoHD.setText("Số hóa đơn: " + slHoaDon);
        lblTienMat.setText("Tiền mặt: " + nf.format(tongTienMat) + " VND");
        lblCKhoan.setText("Chuyển khoản: " + nf.format(tongTienCK) + " VND");
        lblDThu.setText("Doanh thu: " + nf.format(tongTienMat + tongTienCK) + " VND");
    }


    private String xacDinhCaLam(LocalDateTime thoiGian) {
        int gio = thoiGian.getHour();
        int phut = thoiGian.getMinute();

        if (gio < 12 || (gio == 12 && phut == 0)) {
            return "Ca sáng";
        } else {
            return "Ca tối";
        }
    }

    public void setThoiGianVaoCa(LocalDateTime thoiGian) {
        this.thoiGianVaoCa = thoiGian;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        txtTGVC.setText(thoiGian.format(formatter));

        String caLam = xacDinhCaLam(thoiGian);
        txtCaLam.setText(caLam);
    }

    public void setNhanVien(NhanVien nhanVien) {
        this.nhanVien = nhanVien;
    }

    @FXML
    private void ketCa() {
        if (nhanVien == null || thoiGianVaoCa == null) return;

        PhieuKetCaDAO phieuKCDAO = new PhieuKetCaDAO();
        String maPhieu = phieuKCDAO.generateNewMaPhieu();

        boolean ca = txtCaLam.getText().equals("Ca sáng") ? false : true;
        int soHoaDon = Integer.parseInt(txtsLHD.getText());
        double tongTM = Double.parseDouble(txtSoTienMat.getText());
        double tongCK = Double.parseDouble(txtSoTienCK.getText());
        double chenhLech = Double.parseDouble(txtTongTien.getText()) - (tongTM+tongCK);

        PhieuKetCa phieu = new PhieuKetCa(
                maPhieu,
                nhanVien,
                ca,
                soHoaDon,
                tongTM,
                tongCK,
                chenhLech,
                LocalDateTime.now(),
                taMoTa.getText().trim()
        );

        boolean success = new PhieuKetCaDAO().insert(phieu);
        if (success) {
            AlertCus.show("Bàn giao ca","Đã lưu báo cáo kết ca!");
        } else {
            AlertCus.show("Bàn giao ca","Lỗi lưu báo cáo kết ca!");
        }
    }


}
