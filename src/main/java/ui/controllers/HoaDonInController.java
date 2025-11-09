package ui.controllers;

import dao.ChiTietHDDAO;
import entity.ChiTietHoaDon;
import entity.HoaDon;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class HoaDonInController {

    // ====== Các label trong FXML ======
    @FXML private Label lblGio;
    @FXML private Label lblNgay;
    @FXML private Label lblBan;
    @FXML private Label lblMaHD;
    @FXML private Label lblThuNgan;

    @FXML private Label lblTongTien;
    @FXML private Label lblGiamGia;
    @FXML private Label lblThue;
    @FXML private Label lblTongTT;
    @FXML private Label lblCoc;
    @FXML private Label lblConLai;
    @FXML private Label lblThanhToan;

    @FXML private VBox vboxChiTiet;

    // ====== Format tiền tệ ======
    private final DecimalFormat df = new DecimalFormat("#,###", new DecimalFormatSymbols(new Locale("vi", "VN")));

    @FXML
    public void initialize() {
        // Không cần làm gì thêm khi khởi tạo
    }

    /**
     * Gán dữ liệu hóa đơn vào giao diện in
     */
    public void setData(HoaDon hd) {
        if (hd == null) return;

        // ===== Thông tin cơ bản =====
        lblMaHD.setText(hd.getMaHD());
        if (hd.getBan() != null)
            lblBan.setText(hd.getBan().getMaBan());
        if (hd.getNhanVien() != null)
            lblThuNgan.setText(hd.getNhanVien().getTenNV());

        if (hd.getTgCheckOut() != null) {
            lblGio.setText(hd.getTgCheckOut().format(DateTimeFormatter.ofPattern("HH:mm")));
            lblNgay.setText(hd.getTgCheckOut().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        }

        // ===== Lấy danh sách chi tiết hóa đơn =====
        ChiTietHDDAO ctDAO = new ChiTietHDDAO();
        List<ChiTietHoaDon> dsCT = ctDAO.getByMaHD(hd.getMaHD());
        vboxChiTiet.getChildren().clear();

        // ===== Hiển thị từng món =====
        for (ChiTietHoaDon ct : dsCT) {
            VBox itemBox = new VBox(0);

            // Dòng 1: Tên món
            Label tenMon = new Label(ct.getMon().getTenMon());
            tenMon.setStyle("-fx-font-weight: bold; -fx-font-size: 11;");

            // Dòng 2: Mã món - Giá - SL - Thành tiền
            HBox dongChiTiet = new HBox(10);
            dongChiTiet.setStyle("-fx-alignment: CENTER_LEFT;");

            Label lblMaMon = new Label(ct.getMon().getMaMon());
            Label lblGia = new Label(df.format(ct.getMon().getGiaBanTaiLucLapHD(hd)));
            Label lblSL = new Label(String.valueOf(ct.getSoLuong()));
            Label lblThanhTien = new Label(df.format(ct.getThanhTien()));

            lblMaMon.setPrefWidth(60);
            lblGia.setPrefWidth(60);
            lblSL.setPrefWidth(30);
            lblThanhTien.setPrefWidth(70);

            dongChiTiet.getChildren().addAll(lblMaMon, lblGia, lblSL, lblThanhTien);

            // Thêm vào VBox món
            itemBox.getChildren().addAll(tenMon, dongChiTiet);
            vboxChiTiet.getChildren().add(itemBox);
        }



        // ===== Tính toán tổng =====
        double tong = dsCT.stream().mapToDouble(ChiTietHoaDon::getThanhTien).sum();
        double giamGia = hd.getTongTienKhuyenMai();
        double thue = hd.getThue();
        double tongSauThue = hd.getTongTienSau();
        double coc = hd.getCoc();
        double conLai = tongSauThue - coc;

        // ===== Gán dữ liệu =====
        lblTongTien.setText(df.format(tong));
        lblGiamGia.setText(df.format(giamGia));
        lblThue.setText(df.format(thue));
        lblTongTT.setText(df.format(tongSauThue));
        lblCoc.setText(df.format(coc));
        lblConLai.setText(df.format(conLai));

        lblThanhToan.setText(
                hd.isKieuThanhToan()
                        ? "Thanh toán: Thẻ/QR"
                        : "Thanh toán: Tiền mặt"
        );
    }
}
