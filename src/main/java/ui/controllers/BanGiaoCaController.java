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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.geometry.Pos;
import ui.AlertCus;
import ui.ConfirmCus;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class BanGiaoCaController {
    @FXML
    private VBox vboxHoaDon;
    @FXML
    private TextField txtCaLam, txtTGVC, txtsLHD, txtSoTienMat, txtSoTienCK, txtTongTien,searchField;
    @FXML
    private TextArea taMoTa;
    @FXML
    private Label lblTienMat, lblCKhoan, lblsoHD, lblDThu,searchIcon;
    @FXML
    private Button btnKetCa;

    private HoaDonDAO hoaDonDAO = new HoaDonDAO();
    private NhanVien nhanVien;
    private LocalDateTime thoiGianVaoCa;
    private double heThongTienMat = 0;
    private double heThongTienCK = 0;
    private double heThongTongTien = 0;
    private double heThongTienCoc = 0;
    private double tienCocCK = 0;
    private double tienCocTM = 0;



    @FXML
    public void initialize() {

        System.out.println("Initializing BanGiaoCaController");

        btnKetCa.setOnAction(e -> KiemTraTruocKetCa());

        // Click icon tìm kiếm
        searchIcon.setOnMouseClicked(e -> thucHienTimKiem());

        // Nhấn Enter để tìm
        searchField.setOnAction(e -> thucHienTimKiem());

        javafx.application.Platform.runLater(() -> {
            searchField.getScene().addEventFilter(javafx.scene.input.KeyEvent.KEY_PRESSED, event -> {
                if (event.isControlDown() && event.getCode() == javafx.scene.input.KeyCode.F) {
                    searchField.requestFocus();
                    searchField.selectAll();
                    event.consume();
                }
            });
        });
    }

    public void initData(NhanVien nv) {
        this.nhanVien = nv;
        loadHoaDonTrongCaLam();
    }

    private void loadHoaDonTrongCaLam() {
        int slHoaDon = 0;
        double tongTienMat = 0;
        double tongTienCK = 0;

        vboxHoaDon.getChildren().clear();

        if (nhanVien == null || thoiGianVaoCa == null) return;

//        List<HoaDon> danhSach = hoaDonDAO.getTheoMaNV(nhanVien.getMaNV());
        List<HoaDon> danhSach = hoaDonDAO.getAll();
        for (HoaDon hd : danhSach) {

            if (hd.getTrangthai() == 0 && hd.getTgLapHD().isAfter(thoiGianVaoCa) && hd.isKieuThanhToan() == true) {
                tienCocCK += hd.getCoc();
            }
            if (hd.getTrangthai() == 0 && hd.getTgLapHD().isAfter(thoiGianVaoCa) && hd.isKieuThanhToan() == false) {
                tienCocTM += hd.getCoc();
            }

            LocalDateTime tgCheckout = hd.getTgCheckOut();
            if (tgCheckout == null) continue;

            // Lọc theo ngày và giờ
            if (!tgCheckout.toLocalDate().equals(thoiGianVaoCa.toLocalDate())) continue;
            if (!tgCheckout.isAfter(thoiGianVaoCa)) continue;

            double tienHoaDonCuoiCung = hd.getTongTienSau();

            if (hd.isKieuThanhToan()) {
                tongTienCK += tienHoaDonCuoiCung; // Chuyển khoản
            } else {
                tongTienMat += tienHoaDonCuoiCung; // Tiền mặt
            }

            String maBan = hd.getBan().getMaBan();
            String prefix = maBan.substring(0, 2);

            String imgPath = "/IMG/ban/IN.png";

            switch (prefix) {
                case "BV":
                    imgPath = "/IMG/ban/vip.png";
                    break;
                case "BI":
                    imgPath = "/IMG/ban/IN.png";
                    break;
                case "BO":
                    imgPath = "/IMG/ban/out.png";
                    break;
            }
            ImageView imgBan = new ImageView(new Image(getClass().getResourceAsStream(imgPath)));
            imgBan.setFitWidth(60);
            imgBan.setFitHeight(60);
            imgBan.setPreserveRatio(true);


            Label lblMaHD = new Label(hd.getMaHD());
            lblMaHD.getStyleClass().add("invoice-id");

            Label lblSDT = new Label("SDT: " + hd.getKhachHang().getSdt());
            lblSDT.getStyleClass().add("invoice-phone");

            Label lblTenKH = new Label("Tên: " + hd.getKhachHang().getTenKhachHang());
            lblTenKH.getStyleClass().add("invoice-name");

            Label lblSoLuong = new Label("Số khách: " + hd.getSoLuong());
            lblTenKH.getStyleClass().add("invoice-name");

            VBox boxThongTin = new VBox(2, lblMaHD, lblSDT,lblTenKH,lblSoLuong);

            Label lblTongTien = new Label(NumberFormat
                    .getInstance(new Locale("vi", "VN"))
                    .format(tienHoaDonCuoiCung) + " đ");
            lblTongTien.getStyleClass().add("invoice-total");

            HBox left = new HBox(10, imgBan, boxThongTin);
            left.setAlignment(Pos.CENTER_LEFT);

            Pane spacer = new Pane();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            HBox card = new HBox(10, left, spacer, lblTongTien);
            card.setAlignment(Pos.CENTER_LEFT);
            card.getStyleClass().add("invoice-card");
            VBox.setMargin(card, new Insets(5, 0, 5, 0));

            vboxHoaDon.getChildren().add(card);
            slHoaDon++;
        }
        NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));
        lblsoHD.setText("Tiền cọc: " + nf.format(tienCocCK+tienCocTM) + " VND");
        txtsLHD.setText(String.valueOf(slHoaDon));
        lblTienMat.setText("Tiền mặt: " + nf.format(tongTienMat) + " VND");
        lblCKhoan.setText("Chuyển khoản: " + nf.format(tongTienCK) + " VND");
        lblDThu.setText("Doanh thu: " + nf.format(tongTienMat + tongTienCK +tienCocTM+tienCocCK) + " VND");
        heThongTienMat = tongTienMat + tienCocTM;
        heThongTienCK = tongTienCK + tienCocCK;
        heThongTongTien = tongTienMat + tongTienCK + tienCocTM + tienCocCK;
        heThongTienCoc = tienCocCK + tienCocTM;

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
    private void KiemTraTruocKetCa() {
        if (nhanVien == null || thoiGianVaoCa == null) return;

        PhieuKetCaDAO phieuKCDAO = new PhieuKetCaDAO();
        String maPhieu = tuSinhMaPhieuKC();

//        if (isEmpty(txtsLHD, "Số hóa đơn không được để trống!")) return;
        if (isEmpty(txtSoTienMat, "Số tiền mặt không được để trống!")) return;
        if (isEmpty(txtSoTienCK, "Số tiền chuyển khoản không được để trống!")) return;
        if (isEmpty(txtTongTien, "Tổng tiền không được để trống!")) return;
        if (!isNumeric(txtsLHD.getText())) {
            AlertCus.show("Thông báo lỗi", "Số hóa đơn phải là số!");
            return;
        }
        if (!isNumeric(txtSoTienMat.getText())) {
            AlertCus.show("Thông báo lỗi", "Số tiền mặt phải là số!");
            return;
        }
        if (!isNumeric(txtSoTienCK.getText())) {
            AlertCus.show("Thông báo lỗi", "Số tiền chuyển khoản phải là số!");
            return;
        }
        if (!isNumeric(txtTongTien.getText())) {
            AlertCus.show("Thông báo lỗi", "Số tổng tiền phải là số!");
            return;
        }
        boolean ca = txtCaLam.getText().equals("Ca sáng") ? false : true;
        int soHoaDon = Integer.parseInt(txtsLHD.getText());
        double tongTM = Double.parseDouble(txtSoTienMat.getText());
        double tongCK = Double.parseDouble(txtSoTienCK.getText());
        double chenhLech = heThongTienCoc;

        NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));

        double nvTienMat = tongTM;
        double nvTienCK = tongCK;
        double nvTongTien = Double.parseDouble(txtTongTien.getText());

        boolean lech =
                Double.compare(nvTienMat, heThongTienMat) != 0 ||
                        Double.compare(nvTienCK, heThongTienCK) != 0 ||
                        Double.compare(nvTongTien, heThongTongTien) != 0;

        String moTaNhanVien = taMoTa.getText().trim();
        String moTaCuoi = moTaNhanVien;

        if (lech) {
            String moTaHeThong =
                    "[Tiền trên hệ thống khi kết ca: tiền mặt = " + nf.format(heThongTienMat) + "đ"
                            + ", chuyển khoản = " + nf.format(heThongTienCK) + "đ"
                            + ", tổng = " + nf.format(heThongTongTien) + "đ]" +"\n Tin nhắn nhân viên :";

            if (!moTaNhanVien.isEmpty()) {
                moTaCuoi = moTaHeThong + "\n" + moTaNhanVien;
            } else {
                moTaCuoi = moTaHeThong;
            }
        }


        PhieuKetCa phieu = new PhieuKetCa(
                maPhieu,
                nhanVien,
                ca,
                soHoaDon,
                tongTM,
                tongCK,
                chenhLech,
                LocalDateTime.now(),
                thoiGianVaoCa,
                moTaCuoi
        );
        boolean answer = ConfirmCus.show("Xác nhận", "Xác nhận kết ca");
        if (answer) {
            boolean success = new PhieuKetCaDAO().insert(phieu);
            if (success) {
                AlertCus.show("Bàn giao ca", "Đã lưu báo cáo kết ca!");
                javafx.application.Platform.exit();
                dangXuat();
            } else {
                AlertCus.show("Bàn giao ca", "Lỗi lưu báo cáo kết ca!");
            }
        }
    }

    private boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) return false;
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isEmpty(TextField txt, String message) {
        if (txt.getText().trim().isEmpty()) {
            AlertCus.show("Thông báo lỗi", message);
            return true;
        }
        return false;
    }


    private String tuSinhMaPhieuKC() {
        int hour = thoiGianVaoCa.getHour();
        String ca = (hour < 12) ? "0" : "1";

        String datePart = thoiGianVaoCa.format(DateTimeFormatter.ofPattern("ddMMyy"));

        PhieuKetCaDAO phieuKCDAO = new PhieuKetCaDAO();
        String maHDCuoi = phieuKCDAO.getMaPhieuKCCuoiTheoNgay(ca, datePart);

        int so = 0;
        if (maHDCuoi != null) {
            String phanSo = maHDCuoi.substring(maHDCuoi.length() - 4);
            so = Integer.parseInt(phanSo);
        }

        return String.format("MP%s%s%04d", ca, datePart, so + 1);
    }

    private void loadHoaDonTrongCaLamTimKiem(String keyword) {
        int slHoaDon = 0;
        vboxHoaDon.getChildren().clear();

        if (nhanVien == null || thoiGianVaoCa == null) return;

        String kw = keyword.toLowerCase();

        List<HoaDon> danhSach = hoaDonDAO.getAll();
        for (HoaDon hd : danhSach) {

            if (hd.getTgCheckOut() == null) continue;
            if (!hd.getTgCheckOut().toLocalDate().equals(thoiGianVaoCa.toLocalDate())) continue;
            if (!hd.getTgCheckOut().isAfter(thoiGianVaoCa)) continue;

            boolean match =
                    hd.getMaHD().toLowerCase().contains(kw) ||
                            hd.getBan().getMaBan().toLowerCase().contains(kw) ||
                            hd.getKhachHang().getTenKhachHang().toLowerCase().contains(kw) ||
                            hd.getKhachHang().getSdt().startsWith(keyword);

            if (!kw.isEmpty() && !match) continue;

            double tien = hd.getTongTienSau();

            String imgPath = "/IMG/ban/IN.png";
            String prefix = hd.getBan().getMaBan().substring(0, 2);
            if (prefix.equals("BV")) imgPath = "/IMG/ban/vip.png";
            else if (prefix.equals("BO")) imgPath = "/IMG/ban/out.png";

            ImageView imgBan = new ImageView(new Image(getClass().getResourceAsStream(imgPath)));
            imgBan.setFitWidth(60);
            imgBan.setFitHeight(60);

            Label lblMaHD = new Label(hd.getMaHD());
            lblMaHD.getStyleClass().add("invoice-id");
            Label lblSDT = new Label("SDT: " + hd.getKhachHang().getSdt());
            lblSDT.getStyleClass().add("invoice-phone");
            Label lblTen = new Label("Tên: " + hd.getKhachHang().getTenKhachHang());
            lblTen.getStyleClass().add("invoice-name");

            VBox info = new VBox(2, lblMaHD, lblSDT, lblTen);

            Label lblTien = new Label(NumberFormat
                    .getInstance(new Locale("vi", "VN"))
                    .format(tien) + " đ");
            lblTien.getStyleClass().add("invoice-total");

            Pane spacer = new Pane();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            HBox card = new HBox(10, imgBan, info, spacer, lblTien);
            card.getStyleClass().add("invoice-card");
            card.setAlignment(Pos.CENTER_LEFT);
            VBox.setMargin(card, new Insets(5, 0, 5, 0));

            vboxHoaDon.getChildren().add(card);
            slHoaDon++;
        }

        txtsLHD.setText(String.valueOf(slHoaDon));
    }


    private void thucHienTimKiem() {
        String keyword = searchField.getText().trim();
        loadHoaDonTrongCaLamTimKiem(keyword);
    }



    public void dangXuat(){
        System.exit(0);
    }

}

