package ui.controllers;
import dao.KhuyenMaiDAO;
import entity.KhuyenMai;
import javafx.application.Platform;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import ui.AlertCus;
import ui.ConfirmCus;

import java.io.File;
import java.io.FileInputStream;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class KhuyenMaiController {

    // FXML - DANH SÁCH
    @FXML private VBox vboxCenterScroll;

    // FXML - FORM
    @FXML private TextField txtMaKM, txtTenKM, txtSoLuong, txtMaThayThe, txtPhanTram, txtTimKiem;
    @FXML private DatePicker dpNgayBatDau, dpNgayKetThuc;
    @FXML private ComboBox<String> cbUudai, cbTrangThai, cbUuDaiTimKiem;

    // FXML - NÚT
    @FXML private Button btnThem, btnSua, btnXoa, btnTimKiem, btnXoaTrang,btnInQR;

    // BIẾN CHUNG
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private KhuyenMai selectedKM = null;
    private final KhuyenMaiDAO kmDAO = new KhuyenMaiDAO();

    @FXML
    public void initialize() {
        System.out.println("KhuyenMaiController initialized");
        khoiTaoComboBox();
        ganSuKienChoNut();
        taiDanhSachKhuyenMai();

        Platform.runLater(() -> addShortcuts(txtTimKiem.getScene()));
        Tooltip tipFind = new Tooltip("Tìm kiếm Mã Khuyến Mãi (Ctrl + F)");
        tipFind.getStyleClass().add("tooltip");
        Tooltip.install(txtTimKiem, tipFind);

    }
    private void addShortcuts(Scene scene){
        KeyCombination ctrlF = new KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN);
        scene.getAccelerators().put(ctrlF, ()->{
            txtTimKiem.requestFocus();
            txtTimKiem.selectAll();
        });
        
    }

    // ========================== KHỞI TẠO ==========================
    private void khoiTaoComboBox() {
        if (cbUudai != null) cbUudai.getItems().setAll("Phần Trăm", "Tiền Mặt");
        if (cbUuDaiTimKiem != null) {
            cbUuDaiTimKiem.getItems().setAll("Tất cả", "Phần Trăm", "Tiền Mặt");
            cbUuDaiTimKiem.setValue("Tất cả");
        }
        if (cbTrangThai != null) cbTrangThai.getItems().setAll("Tất cả", "Đang hoạt động", "Hết hạn", "Chưa bắt đầu");
    }

    private void ganSuKienChoNut() {
        if(txtTimKiem!= null) txtTimKiem.setOnAction(e -> xuLyTimKiem());
        if (btnThem != null) btnThem.setOnAction(e -> xuLyThem());
        if (btnSua != null) btnSua.setOnAction(e -> xuLySua());
        if (btnXoa != null) btnXoa.setOnAction(e -> xuLyXoa());
        if (btnTimKiem != null) btnTimKiem.setOnAction(e -> xuLyTimKiem());
        if (btnXoaTrang != null) btnXoaTrang.setOnAction(e -> xoaTrangTimKiem());
        if (btnInQR != null) btnInQR.setOnAction(e -> inAnhQR());

    }

    // ========================== DANH SÁCH KHUYẾN MÃI ==========================
    private void taiDanhSachKhuyenMai() {
        try {
            List<KhuyenMai> danhSach = kmDAO.getAll();
            hienThiDanhSachKhuyenMai(danhSach);
            System.out.println("Đã tải " + (danhSach != null ? danhSach.size() : 0) + " khuyến mãi");
        } catch (Exception e) {
            System.err.println("Lỗi tải danh sách khuyến mãi: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void hienThiDanhSachKhuyenMai(List<KhuyenMai> danhSach) {
        if (vboxCenterScroll == null) return;
        vboxCenterScroll.getChildren().clear();
        if (danhSach == null || danhSach.isEmpty()) {
            Label empty = new Label("Không có khuyến mãi");
            empty.setStyle("-fx-text-fill:#666; -fx-padding:20px; -fx-font-style:italic;");
            vboxCenterScroll.getChildren().add(empty);
            return;
        }
        for (KhuyenMai km : danhSach) vboxCenterScroll.getChildren().add(taoTheKhuyenMai(km));
    }

    private HBox taoTheKhuyenMai(KhuyenMai km) {
        HBox the = new HBox(10);
        the.getStyleClass().add("order-card");
        the.setPadding(new Insets(8));
        the.setCursor(Cursor.HAND);
        the.setPrefHeight(80);

        StackPane khungAnh = new StackPane();
        khungAnh.setPrefSize(60, 60);
        ImageView anh;
        try {
            anh = new ImageView(new Image(getClass().getResourceAsStream("/IMG/avatar.png")));
        } catch (Exception ex) {
            anh = new ImageView();
        }
        anh.setFitWidth(60); anh.setFitHeight(60); anh.setPreserveRatio(false);
        khungAnh.getChildren().add(anh);

        VBox thongTin = new VBox(4);
        Label lblMa = new Label(km.getMaKM());
        lblMa.setFont(Font.font(14));
        HBox khungNgay = new HBox(12);
        Label lblNgayBD = new Label("Bắt đầu: " + dinhDangNgay(km.getNgayPhatHanh()));
        Label lblNgayKT = new Label("Kết thúc: " + dinhDangNgay(km.getNgayKetThuc()));
        khungNgay.getChildren().addAll(lblNgayBD, lblNgayKT);
        thongTin.getChildren().addAll(lblMa, khungNgay);
        HBox.setHgrow(thongTin, Priority.ALWAYS);

        StackPane indicator = new StackPane();
        indicator.setPrefWidth(40);
        indicator.getStyleClass().add(xacDinhTrangThaiKhuyenMai(km));

        the.getChildren().addAll(khungAnh, thongTin, indicator);
        the.setOnMouseClicked(e -> xuLyChonKhuyenMai(km, the));
        return the;
    }

    private String xacDinhTrangThaiKhuyenMai(KhuyenMai km) {
        LocalDate now = LocalDate.now();
        LocalDate bd = km.getNgayPhatHanh();
        LocalDate kt = km.getNgayKetThuc();
        if (bd == null || kt == null) return "order-status02";
        if (now.isBefore(bd)) return "order-status03";
        if (!now.isBefore(bd) && !now.isAfter(kt)) return "order-status";
        return "order-status02";
    }

    private String dinhDangNgay(LocalDate ngay) {
        return ngay == null ? "" : dtf.format(ngay);
    }

    private void xuLyChonKhuyenMai(KhuyenMai km, HBox the) {
        selectedKM = km;
        hienThiThongTinKhuyenMai(km);
        danhDauTheDuocChon(the);
    }

    private void hienThiThongTinKhuyenMai(KhuyenMai km) {
        if (km == null) return;
        txtMaKM.setText(km.getMaKM());
        txtTenKM.setText(km.getTenKM());
        txtSoLuong.setText(String.valueOf(km.getSoLuong()));
        dpNgayBatDau.setValue(km.getNgayPhatHanh());
        dpNgayKetThuc.setValue(km.getNgayKetThuc());
        txtMaThayThe.setText(km.getMaThayThe());
        txtPhanTram.setText(String.valueOf(km.getPhanTRamGiamGia()));
        if (cbUudai != null) cbUudai.setValue(km.isUuDai() ? "Tiền Mặt" : "Phần Trăm");
    }

    private void danhDauTheDuocChon(HBox theDuocChon) {
        if (vboxCenterScroll != null) {
            vboxCenterScroll.getChildren().forEach(n -> n.getStyleClass().remove("selected-card"));
        }
        if (!theDuocChon.getStyleClass().contains("selected-card")) theDuocChon.getStyleClass().add("selected-card");
    }

    // ========================== CRUD CƠ BẢN ==========================
    private void xuLyThem() {
        try {
            KhuyenMai km = layThongTinTuForm();
            if (km == null) return;
            boolean answer = ConfirmCus.show("Xác nhận", "Xác nhận thêm khuyến mãi mới");
            if (answer) {
                boolean ok = kmDAO.insert(km);
                if (ok) {
                    selectedKM = km;
                    QrCodeController.generateQRCodeKM(txtMaKM.getText().trim(),txtMaKM.getText().trim(),300);
                    AlertCus.show("Thông báo","Đã thêm khuyến mãi");
                    taiDanhSachKhuyenMai();
                    xoaTrangForm();
                } else {
                    AlertCus.show("Thông báo","Thêm khuyến mãi thất bại");
                    txtMaKM.setText(tuSinhMaKM(KhuyenMaiDAO.maKMCuoi()));
                }
            }
        } catch (Exception ex) {
            AlertCus.show("Thông báo","Thêm khuyến mãi thất bại");
            ex.printStackTrace();
        }
    }

    private void xuLySua() {
        if (selectedKM == null) {
            AlertCus.show("Thông báo","Vui lòng chọn khuyến mãi cần sửa.");
            return;
        }
        try {
            KhuyenMai km = layThongTinTuForm();
            if (km == null) return;
            boolean ok = kmDAO.update(km);
            if (ok) {
                AlertCus.show("Thông báo","Đã cập nhật khuyến mãi.");
                taiDanhSachKhuyenMai();
                xoaTrangForm();
            } else {
                AlertCus.show("Thông báo","Cập nhật thất bại.");
            }
        } catch (Exception ex) {
            AlertCus.show("Thông báo","Lỗi");
            ex.printStackTrace();
        }
    }

    private void xuLyXoa() {
        if (selectedKM == null) {
            AlertCus.show("Thông báo","Vui lòng chọn khuyến mãi cần xóa");
            return;
        }

        boolean answer = ConfirmCus.show("Xác nhận xóa",
                "Bạn có chắc muốn xóa khuyến mãi " + selectedKM.getMaKM() + "?");

        if (answer) {
            boolean ok = kmDAO.delete(selectedKM.getMaKM());
            if (ok) {
                AlertCus.show("Thông báo","Xóa thành công");
                selectedKM = null;
                taiDanhSachKhuyenMai();
                xoaTrangForm();
            } else {
                AlertCus.show("Thông báo","Xóa thất bại");
            }
        }
    }

    // ========================== TÌM KIẾM KHUYẾN MÃI ==========================
    private void xuLyTimKiem() {
        String q = txtTimKiem.getText().trim();
        String trangThai = cbTrangThai != null ? cbTrangThai.getValue() : null;
        String uuDai = cbUuDaiTimKiem != null ? cbUuDaiTimKiem.getValue() : null;

        if (q.isEmpty() && (trangThai == null || trangThai.equals("Tất cả")) && (uuDai == null || uuDai.equals("Tất cả"))) {
            taiDanhSachKhuyenMai();
            return;
        }
        List<KhuyenMai> ketQua = timKiemKhuyenMai(q, trangThai, uuDai);
        hienThiKetQuaTimKiem(ketQua);
    }

    private List<KhuyenMai> timKiemKhuyenMai(String q, String trangThai, String uuDai) {
        List<KhuyenMai> tatCa = kmDAO.getAll();
        List<KhuyenMai> ketQua = new ArrayList<>();
        if (tatCa == null) return ketQua;

        for (KhuyenMai km : tatCa) {
            boolean khopMa = q.isEmpty() || // trường hợp k nhập
                    km.getMaKM().toLowerCase().contains(q.toLowerCase()) ||
                    km.getTenKM().toLowerCase().contains(q.toLowerCase());

            boolean khopTrangThai = kiemTraTrangThai(km, trangThai);
            boolean khopUuDai = kiemTraUuDai(km, uuDai);
            if (khopMa && khopTrangThai && khopUuDai) ketQua.add(km);
        }
        return ketQua;
    }

    private boolean kiemTraTrangThai(KhuyenMai km, String trangThai) {
        if (trangThai == null || trangThai.equals("Tất cả")) return true;
        LocalDate now = LocalDate.now();
        switch (trangThai) {
            case "Đang hoạt động":
                return km.getSoLuong() > 0 && km.getNgayKetThuc() != null
                        && km.getNgayPhatHanh() != null
                        && (!now.isBefore(km.getNgayPhatHanh()) && !now.isAfter(km.getNgayKetThuc()));
            case "Hết hạn":
                return km.getNgayKetThuc() != null && km.getNgayKetThuc().isBefore(now);
            case "Chưa bắt đầu":
                return km.getNgayPhatHanh() != null && km.getNgayPhatHanh().isAfter(now);
            default:
                return true;
        }
    }

    private boolean kiemTraUuDai(KhuyenMai km, String uuDai) {
        if (uuDai == null || uuDai.equals("Tất cả")) return true;
        boolean loaiThanhToan = km.isUuDai();
        return uuDai.equals("Tiền Mặt") ? loaiThanhToan : !loaiThanhToan;
    }

    private void hienThiKetQuaTimKiem(List<KhuyenMai> ketQua) {
        if (vboxCenterScroll == null) return;
        vboxCenterScroll.getChildren().clear();
        if (ketQua == null || ketQua.isEmpty()) {
            AlertCus.show("Thông báo","Không tìm thấy khuyến mãi phù hợp.");
            return;
        }
        for (KhuyenMai km : ketQua) vboxCenterScroll.getChildren().add(taoTheKhuyenMai(km));
    }

    // ========================== TIỆN ÍCH ==========================
    private void xoaTrangTimKiem() {
        txtTimKiem.clear();
        if (cbTrangThai != null) cbTrangThai.setValue("Tất cả");
        if (cbUuDaiTimKiem != null) cbUuDaiTimKiem.setValue("Tất cả");
        taiDanhSachKhuyenMai();
        xoaTrangForm();
    }

    private KhuyenMai layThongTinTuForm() {
        try {
            String ma = txtMaKM.getText().trim();
            String ten = txtTenKM.getText().trim();
            String soStr = txtSoLuong.getText().trim();
            LocalDate bd = dpNgayBatDau.getValue();
            LocalDate kt = dpNgayKetThuc.getValue();
            String maThayThe = txtMaThayThe.getText().trim();
            String uuDai = cbUudai != null ? cbUudai.getValue() : null;

            // true = Tiền Mặt, false = Phần Trăm
            boolean loaiThanhToan = "Tiền Mặt".equalsIgnoreCase(uuDai);

            // Validate chung
            if (ma.isEmpty() || ten.isEmpty() || soStr.isEmpty()
                    || bd == null || kt == null || uuDai == null) {
                AlertCus.show("Thông báo", "Vui lòng điền đầy đủ thông tin");
                return null;
            }

            if (bd.isAfter(kt)) {
                AlertCus.show("Thông báo", "Ngày bắt đầu phải trước ngày kết thúc");
                return null;
            }

            int soLuong = Integer.parseInt(soStr);

            String giaTriStr = txtPhanTram.getText().replace("%", "").trim();
            if (giaTriStr.isEmpty()) {
                AlertCus.show("Thông báo", "Vui lòng nhập giá trị ưu đãi");
                return null;
            }

            int giaTri = Integer.parseInt(giaTriStr);

            // VALIDATE THEO LOẠI ƯU ĐÃI
            if (!loaiThanhToan) { // PHẦN TRĂM
                if (giaTri < 1 || giaTri > 100) {
                    AlertCus.show("Thông báo", "Phần trăm giảm phải từ 1 đến 100");
                    return null;
                }
            } else { // TIỀN MẶT
                if (giaTri < 0) {
                    AlertCus.show("Thông báo", "Tiền giảm không được âm");
                    return null;
                }
            }

            return new KhuyenMai(
                    ma,
                    ten,
                    soLuong,
                    bd,
                    kt,
                    maThayThe,
                    giaTri,       // % hoặc tiền
                    loaiThanhToan // true = tiền mặt
            );

        } catch (NumberFormatException e) {
            AlertCus.show("Thông báo", "Số lượng và giá trị ưu đãi phải là số");
            return null;
        } catch (Exception ex) {
            AlertCus.show("Thông báo", "Dữ liệu không hợp lệ: " + ex.getMessage());
            return null;
        }
    }


    private void xoaTrangForm() {
        txtMaKM.setText(tuSinhMaKM(KhuyenMaiDAO.maKMCuoi()));
        txtTenKM.clear();
        txtSoLuong.clear();
        dpNgayBatDau.setValue(null);
        dpNgayKetThuc.setValue(null);
        txtMaThayThe.clear();
        txtPhanTram.clear();
        if (cbUudai != null) cbUudai.getSelectionModel().clearSelection();
        selectedKM = null;
        if (vboxCenterScroll != null) vboxCenterScroll.getChildren().forEach(n -> n.getStyleClass().remove("selected-card"));
    }

    private String tuSinhMaKM(String maKM) {
        int so = Integer.parseInt(maKM.substring(2));
        return String.format("KM%04d", so + 1);
    }


    private void inAnhQR() {
        try {
            String maQR = txtMaKM.getText().trim();
            if (maQR.isEmpty()) {
                AlertCus.show("Lỗi", "Chưa có mã khuyến mãi!");
                return;
            }

            File file = new File("src/main/resources/IMG/qrcode/" + maQR + ".png");

            if (!file.exists()) {
                try {
                    QrCodeController.generateQRCodeKM(
                            maQR,
                            maQR,
                            300
                    );
                } catch (Exception ex) {
                    AlertCus.show("Lỗi", "Không thể tạo QR!");
                    ex.printStackTrace();
                    return;
                }
            }

            Image img = new Image(new FileInputStream(file));

            ImageView imageView = new ImageView(img);
            imageView.setFitWidth(300);
            imageView.setPreserveRatio(true);

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("In QR khuyến mãi");
            dialog.getDialogPane().setContent(imageView);
            dialog.getDialogPane().getButtonTypes()
                    .addAll(ButtonType.OK, ButtonType.CANCEL);

            dialog.showAndWait();

        } catch (Exception e) {
            AlertCus.show("Lỗi", "Không thể in ảnh QR!");
            e.printStackTrace();
        }
    }

}
