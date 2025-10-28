package ui.controllers;

import dao.KhuyenMaiDAO;
import entity.KhuyenMai;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class KhuyenMaiController {

    // FXML - DANH SÁCH
    @FXML private ScrollPane scrollList;
    @FXML private VBox vboxCenterScroll;
    @FXML private FlowPane foodList;

    // FXML - FORM
    @FXML private TextField txtMaKM, txtTenKM, txtSoLuong, txtMaThayThe, txtPhanTram, txtTimKiem;
    @FXML private DatePicker dpNgayBatDau, dpNgayKetThuc;
    @FXML private ComboBox<String> cbUudai, cbTrangThai, cbUuDaiTimKiem;

    // FXML - NÚT
    @FXML private Button btnThem, btnSua, btnXoa, btnTimKiem, btnXoaTrang;

    // BIẾN
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private KhuyenMai selectedKM = null;
    private final KhuyenMaiDAO kmDAO = new KhuyenMaiDAO();

    @FXML
    public void initialize() {
        System.out.println("KhuyenMaiController initialized");
        khoiTaoComboBox();
        ganSuKienChoNut();
        taiDanhSachKhuyenMai();

        if (txtMaKM != null) {
            txtMaKM.setEditable(false);
            txtMaKM.setFocusTraversable(false);
            txtMaKM.setText(tuSinhMaKM(KhuyenMaiDAO.maKMCuoi()));
        }
    }

    // KHỞI TẠO
    private void khoiTaoComboBox() {
        if (cbUudai != null) cbUudai.getItems().setAll("Hóa đơn", "Món ăn");
        if (cbUuDaiTimKiem != null) {
            cbUuDaiTimKiem.getItems().setAll("Tất cả", "Hóa đơn", "Món ăn");
            cbUuDaiTimKiem.setValue("Tất cả");
        }
        if (cbTrangThai != null) cbTrangThai.getItems().setAll("Tất cả", "Đang hoạt động", "Hết hạn", "Chưa bắt đầu");
    }

    private void ganSuKienChoNut() {
        if (btnThem != null) btnThem.setOnAction(e -> xuLyThem());
        if (btnSua != null) btnSua.setOnAction(e -> xuLySua());
        if (btnXoa != null) btnXoa.setOnAction(e -> xuLyXoa());
        if (btnTimKiem != null) btnTimKiem.setOnAction(e -> xuLyTimKiem());
        if (btnXoaTrang != null) btnXoaTrang.setOnAction(e -> xoaTrangTimKiem());
    }

    // TẢI & HIỂN THỊ
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

    // TẠO THẺ
    private HBox taoTheKhuyenMai(KhuyenMai km) {
        HBox the = new HBox(10);
        the.getStyleClass().add("order-card");
        the.setPadding(new Insets(8));
        the.setCursor(Cursor.HAND);
        the.setPrefHeight(80);

        // Ảnh
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

        // Thông tin
        VBox thongTin = new VBox(4);
        Label lblMa = new Label(km.getMaKM());
        lblMa.setFont(Font.font(14));
        HBox khungNgay = new HBox(12);
        Label lblNgayBD = new Label("Bắt đầu: " + dinhDangNgay(km.getNgayPhatHanh()));
        Label lblNgayKT = new Label("Kết thúc: " + dinhDangNgay(km.getNgayKetThuc()));
        khungNgay.getChildren().addAll(lblNgayBD, lblNgayKT);
        thongTin.getChildren().addAll(lblMa, khungNgay);
        HBox.setHgrow(thongTin, Priority.ALWAYS);

        // Indicator trạng thái
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
        if (bd == null || kt == null) return "order-status02"; // lỗi dữ liệu/đỏ
        if (now.isBefore(bd)) return "order-status03"; // vàng - chưa bắt đầu
        if (!now.isBefore(bd) && !now.isAfter(kt)) return "order-status"; // xanh - đang hoạt động
        return "order-status02"; // đỏ - hết hạn
    }

    private String dinhDangNgay(LocalDate ngay) {
        return ngay == null ? "" : dtf.format(ngay);
    }

    // CHỌN KHUYẾN MÃI
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
        if (cbUudai != null) cbUudai.setValue(km.isUuDai() ? "Hóa đơn" : "Món ăn");
    }

    private void danhDauTheDuocChon(HBox theDuocChon) {
        if (vboxCenterScroll != null) {
            vboxCenterScroll.getChildren().forEach(n -> n.getStyleClass().remove("selected-card"));
        }
        if (!theDuocChon.getStyleClass().contains("selected-card")) theDuocChon.getStyleClass().add("selected-card");
    }

    // THÊM
    private void xuLyThem() {
        try {
            KhuyenMai km = layThongTinTuForm();
            if (km == null) return;
            boolean ok = kmDAO.insert(km);
            if (ok) {
                hienThongBao(Alert.AlertType.INFORMATION, "Thành công", "Đã thêm khuyến mãi.");
                taiDanhSachKhuyenMai();
                xoaTrangForm();
            } else {
                hienThongBao(Alert.AlertType.ERROR, "Thất bại", "Thêm khuyến mãi thất bại.");
                txtMaKM.setText(tuSinhMaKM(KhuyenMaiDAO.maKMCuoi()));
            }
        } catch (Exception ex) {
            hienThongBao(Alert.AlertType.ERROR, "Lỗi", ex.getMessage());
            ex.printStackTrace();
        }
    }

    // SỬA
    private void xuLySua() {
        if (selectedKM == null) {
            hienThongBao(Alert.AlertType.WARNING, "Chưa chọn", "Vui lòng chọn khuyến mãi cần sửa.");
            return;
        }
        try {
            KhuyenMai km = layThongTinTuForm();
            if (km == null) return;
            boolean ok = kmDAO.update(km);
            if (ok) {
                hienThongBao(Alert.AlertType.INFORMATION, "Thành công", "Đã cập nhật khuyến mãi.");
                taiDanhSachKhuyenMai();
                xoaTrangForm();
            } else {
                hienThongBao(Alert.AlertType.ERROR, "Thất bại", "Cập nhật thất bại.");
            }
        } catch (Exception ex) {
            hienThongBao(Alert.AlertType.ERROR, "Lỗi", ex.getMessage());
            ex.printStackTrace();
        }
    }

    // XÓA
    private void xuLyXoa() {
        if (selectedKM == null) {
            hienThongBao(Alert.AlertType.WARNING, "Chưa chọn", "Vui lòng chọn khuyến mãi cần xóa.");
            return;
        }
        Alert xacNhan = new Alert(Alert.AlertType.CONFIRMATION);
        xacNhan.setTitle("Xác nhận xóa");
        xacNhan.setContentText("Bạn có chắc muốn xóa khuyến mãi " + selectedKM.getMaKM() + "?");
        Optional<ButtonType> ketQua = xacNhan.showAndWait();
        if (ketQua.isPresent() && ketQua.get() == ButtonType.OK) {
            boolean ok = kmDAO.delete(selectedKM.getMaKM());
            if (ok) {
                hienThongBao(Alert.AlertType.INFORMATION, "Đã xóa", "Xóa thành công.");
                selectedKM = null;
                taiDanhSachKhuyenMai();
                xoaTrangForm();
            } else {
                hienThongBao(Alert.AlertType.ERROR, "Lỗi", "Xóa thất bại.");
            }
        }
    }

    // TÌM KIẾM
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
            boolean khopMa = q.isEmpty() ||
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
                        && ( !now.isBefore(km.getNgayPhatHanh()) && !now.isAfter(km.getNgayKetThuc()) );
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
        boolean isHoaDon = km.isUuDai();
        return uuDai.equals("Hóa đơn") ? isHoaDon : !isHoaDon;
    }

    private void hienThiKetQuaTimKiem(List<KhuyenMai> ketQua) {
        if (vboxCenterScroll == null) return;
        vboxCenterScroll.getChildren().clear();
        if (ketQua == null || ketQua.isEmpty()) {
            hienThongBao(Alert.AlertType.INFORMATION, "Kết quả tìm kiếm", "Không tìm thấy khuyến mãi phù hợp.");
            return;
        }
        for (KhuyenMai km : ketQua) vboxCenterScroll.getChildren().add(taoTheKhuyenMai(km));
    }

    // XÓA TRẮNG BỘ LỌC & FORM
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
            String phStr = txtPhanTram.getText().replace("%", "").trim();
            String uuDai = cbUudai != null ? cbUudai.getValue() : null;
            boolean isHoaDon = "Hóa đơn".equalsIgnoreCase(uuDai);

            if (ma.isEmpty() || ten.isEmpty() || soStr.isEmpty() || bd == null || kt == null || phStr.isEmpty() || uuDai == null) {
                hienThongBao(Alert.AlertType.WARNING, "Thiếu thông tin", "Vui lòng điền đầy đủ thông tin bắt buộc");
                return null;
            }
            if (bd.isAfter(kt)) {
                hienThongBao(Alert.AlertType.WARNING, "Ngày không hợp lệ", "Ngày bắt đầu phải trước ngày kết thúc");
                return null;
            }
            int soLuong = Integer.parseInt(soStr);
            int phanTram = Integer.parseInt(phStr);
            return new KhuyenMai(ma, ten, soLuong, bd, kt, maThayThe, phanTram, isHoaDon);
        } catch (NumberFormatException e) {
            hienThongBao(Alert.AlertType.ERROR, "Lỗi định dạng", "Số lượng và phần trăm phải là số nguyên");
            return null;
        } catch (Exception ex) {
            hienThongBao(Alert.AlertType.ERROR, "Lỗi dữ liệu", "Dữ liệu không hợp lệ: " + ex.getMessage());
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

    // TỰ SINH MÃ
    private String tuSinhMaKM(String maKM) {
        int so = Integer.parseInt(maKM.substring(2));
        return String.format("KM%04d", so + 1);
    }

    // HỘP THOẠI
    private void hienThongBao(Alert.AlertType loai, String tieuDe, String noiDung) {
        Alert a = new Alert(loai);
        a.setTitle(tieuDe);
        a.setHeaderText(null);
        a.setContentText(noiDung);
        a.showAndWait();
    }
}
