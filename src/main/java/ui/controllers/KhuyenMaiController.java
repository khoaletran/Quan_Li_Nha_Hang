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

    // =========================
    // FXML COMPONENTS - DANH SÁCH
    // =========================
    @FXML private ScrollPane scrollList;
    @FXML private VBox vboxCenterScroll;
    @FXML private FlowPane foodList;

    // =========================
    // FXML COMPONENTS - FORM THÔNG TIN
    // =========================
    @FXML private TextField txtMaKM, txtTenKM, txtSoLuong, txtMaThayThe, txtPhanTram, txtTimKiem;
    @FXML private DatePicker dpNgayBatDau, dpNgayKetThuc, dpNgayLoc;
    @FXML private ComboBox<String> cbUudai, cbTrangThai, cbUuDaiTimKiem;

    // =========================
    // FXML COMPONENTS - NÚT CHỨC NĂNG
    // =========================
    @FXML private Button btnThem, btnSua, btnXoa, btnTimKiem, btnXoaTrang;

    // =========================
    // BIẾN TOÀN CỤC
    // =========================
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private KhuyenMai selectedKM = null;
    private final KhuyenMaiDAO kmDAO = new KhuyenMaiDAO();

    @FXML
    public void initialize() {
        System.out.println("KhuyenMaiController initialized");

        // Khởi tạo combobox
        khoiTaoComboBox();

        // Gán sự kiện cho các nút
        ganSuKienChoNut();

        // Load dữ liệu ban đầu
        taiDanhSachKhuyenMai();
    }

    // =========================
    // KHỞI TẠO
    // =========================
    private void khoiTaoComboBox() {
        // Combo ưu đãi trong form
        if (cbUudai != null) {
            cbUudai.getItems().addAll("Hóa đơn", "Món ăn");
        }

        // Combo ưu đãi trong tìm kiếm
        if (cbUuDaiTimKiem != null) {
            cbUuDaiTimKiem.getItems().clear();
            cbUuDaiTimKiem.getItems().addAll("Tất cả", "Hóa đơn", "Món ăn");
            cbUuDaiTimKiem.setValue("Tất cả");
        }

        // Combo trạng thái tìm kiếm
        if (cbTrangThai != null) {
            cbTrangThai.getItems().addAll("Tất cả", "Đang hoạt động", "Hết hạn", "Chưa bắt đầu");
        }
    }

    private void ganSuKienChoNut() {
        if (btnThem != null) btnThem.setOnAction(e -> xuLyThem());
        if (btnSua != null) btnSua.setOnAction(e -> xuLySua());
        if (btnXoa != null) btnXoa.setOnAction(e -> xuLyXoa());
        if (btnTimKiem != null) btnTimKiem.setOnAction(e -> xuLyTimKiem());
        if (btnXoaTrang != null) btnXoaTrang.setOnAction(e -> xoaTrangTimKiem());
    }

    // =========================
    // XỬ LÝ DANH SÁCH KHUYẾN MÃI
    // =========================
    private void taiDanhSachKhuyenMai() {
        try {
            List<KhuyenMai> danhSach = kmDAO.getAll();
            hienThiDanhSachKhuyenMai(danhSach);
            System.out.println("Đã tải " + danhSach.size() + " khuyến mãi");
        } catch (Exception e) {
            System.err.println("Lỗi khi tải danh sách khuyến mãi: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void hienThiDanhSachKhuyenMai(List<KhuyenMai> danhSach) {
        if (vboxCenterScroll == null) {
            System.out.println("vboxCenterScroll is NULL!");
            return;
        }

        vboxCenterScroll.getChildren().clear();

        for (KhuyenMai km : danhSach) {
            HBox theKhuyenMai = taoTheKhuyenMai(km);
            vboxCenterScroll.getChildren().add(theKhuyenMai);
        }
    }

    // =========================
    // TẠO THẺ KHUYẾN MÃI
    // =========================
    private HBox taoTheKhuyenMai(KhuyenMai km) {
        HBox the = new HBox(10);
        the.getStyleClass().add("order-card");
        the.setPadding(new Insets(8));
        the.setCursor(Cursor.HAND);
        the.setPrefHeight(80);

        // Ảnh đại diện
        StackPane khungAnh = new StackPane();
        khungAnh.setStyle("-fx-background-radius: 10 0 0 10; -fx-overflow: hidden;");
        ImageView anh = new ImageView(new Image(getClass().getResourceAsStream("/IMG/avatar.png")));
        anh.setFitWidth(60);
        anh.setFitHeight(60);
        anh.setPreserveRatio(false);
        khungAnh.getChildren().add(anh);

        // Thông tin khuyến mãi
        VBox thongTin = new VBox(4);
        Label lblMa = new Label(km.getMaKM());
        lblMa.getStyleClass().add("promo-id");
        lblMa.setFont(Font.font(14));

        HBox khungNgay = new HBox(12);
        Label lblNgayBD = new Label("Ngày bắt đầu: " + dinhDangNgay(km.getNgayPhatHanh()));
        Label lblNgayKT = new Label("Ngày kết thúc: " + dinhDangNgay(km.getNgayKetThuc()));
        lblNgayBD.getStyleClass().add("promo-date");
        lblNgayKT.getStyleClass().add("promo-date");

        khungNgay.getChildren().addAll(lblNgayBD, lblNgayKT);
        thongTin.getChildren().addAll(lblMa, khungNgay);
        HBox.setHgrow(thongTin, Priority.ALWAYS);

        // Indicator trạng thái theo thời gian
        StackPane indicator = new StackPane();
        indicator.setPrefWidth(40);
        String trangThai = xacDinhTrangThaiKhuyenMai(km);
        indicator.getStyleClass().add(trangThai);

        the.getChildren().addAll(khungAnh, thongTin, indicator);

        // Sự kiện click chọn
        the.setOnMouseClicked(e -> xuLyChonKhuyenMai(km, the));

        return the;
    }

    // =========================
    // XÁC ĐỊNH TRẠNG THÁI KHUYẾN MÃI THEO THỜI GIAN
    // =========================
    private String xacDinhTrangThaiKhuyenMai(KhuyenMai km) {
        LocalDate ngayHienTai = LocalDate.now();
        LocalDate ngayBatDau = km.getNgayPhatHanh();
        LocalDate ngayKetThuc = km.getNgayKetThuc();

        // Kiểm tra null
        if (ngayBatDau == null || ngayKetThuc == null) {
            return "order-status02"; // Đỏ - Lỗi dữ liệu
        }

        // Chưa đến hạn (chưa bắt đầu)
        if (ngayHienTai.isBefore(ngayBatDau)) {
            return "order-status03"; // Vàng - Chưa bắt đầu
        }
        // Đang hoạt động (trong khoảng thời gian)
        else if (!ngayHienTai.isBefore(ngayBatDau) && !ngayHienTai.isAfter(ngayKetThuc)) {
            return "order-status"; // Xanh - Đang hoạt động
        }
        // Hết hạn (đã qua ngày kết thúc)
        else {
            return "order-status02"; // Đỏ - Hết hạn
        }
    }

    private String dinhDangNgay(LocalDate ngay) {
        return ngay == null ? "" : dtf.format(ngay);
    }

    // =========================
    // XỬ LÝ CHỌN KHUYẾN MÃI
    // =========================
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

        // Set giá trị cho combobox ưu đãi
        if (cbUudai != null) {
            // Lấy giá trị ưu đãi từ database (giả sử có trường uuDai trong entity)
            // Nếu chưa có, bạn cần thêm trường này vào entity KhuyenMai
            // Ở đây tôi sẽ để mặc định là "Hóa đơn"
            cbUudai.setValue("Hóa đơn");
        }
    }

    private void danhDauTheDuocChon(HBox theDuocChon) {
        // Xóa highlight từ tất cả các thẻ
        vboxCenterScroll.getChildren().forEach(node ->
                node.getStyleClass().remove("selected-card")
        );

        // Thêm highlight cho thẻ được chọn
        if (!theDuocChon.getStyleClass().contains("selected-card")) {
            theDuocChon.getStyleClass().add("selected-card");
        }
    }

    // =========================
    // XỬ LÝ THÊM KHUYẾN MÃI
    // =========================
    private void xuLyThem() {
        try {
            KhuyenMai km = layThongTinTuForm();
            if (km == null) {
                return;
            }

            boolean thanhCong = kmDAO.insert(km);
            if (thanhCong) {
                hienThongBao(Alert.AlertType.INFORMATION, "Thành công", "Đã thêm khuyến mãi.");
                taiDanhSachKhuyenMai();
                xoaTrangForm();
            } else {
                hienThongBao(Alert.AlertType.ERROR, "Thất bại", "Thêm khuyến mãi thất bại.");
            }
        } catch (Exception ex) {
            hienThongBao(Alert.AlertType.ERROR, "Lỗi", ex.getMessage());
            ex.printStackTrace();
        }
    }

    // =========================
    // XỬ LÝ SỬA KHUYẾN MÃI
    // =========================
    private void xuLySua() {
        if (selectedKM == null) {
            hienThongBao(Alert.AlertType.WARNING, "Chưa chọn", "Vui lòng chọn khuyến mãi cần sửa.");
            return;
        }

        try {
            KhuyenMai km = layThongTinTuForm();
            if (km == null) {
                return;
            }

            boolean thanhCong = kmDAO.update(km);
            if (thanhCong) {
                hienThongBao(Alert.AlertType.INFORMATION, "Thành công", "Đã cập nhật khuyến mãi.");
                taiDanhSachKhuyenMai();
            } else {
                hienThongBao(Alert.AlertType.ERROR, "Thất bại", "Cập nhật thất bại.");
            }
        } catch (Exception ex) {
            hienThongBao(Alert.AlertType.ERROR, "Lỗi", ex.getMessage());
            ex.printStackTrace();
        }
    }

    // =========================
    // XỬ LÝ XÓA KHUYẾN MÃI
    // =========================
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
            boolean thanhCong = kmDAO.delete(selectedKM.getMaKM());
            if (thanhCong) {
                hienThongBao(Alert.AlertType.INFORMATION, "Đã xóa", "Xóa thành công.");
                selectedKM = null;
                taiDanhSachKhuyenMai();
                xoaTrangForm();
            } else {
                hienThongBao(Alert.AlertType.ERROR, "Lỗi", "Xóa thất bại.");
            }
        }
    }

    // =========================
    // XỬ LÝ TÌM KIẾM NÂNG CAO
    // =========================
    private void xuLyTimKiem() {
        String maTimKiem = txtTimKiem.getText().trim();
        String trangThai = cbTrangThai.getValue();
        String uuDai = cbUuDaiTimKiem.getValue();
        LocalDate ngayLoc = dpNgayLoc.getValue();

        // Nếu không có điều kiện tìm kiếm, load tất cả
        if (maTimKiem.isEmpty() &&
                (trangThai == null || trangThai.equals("Tất cả")) &&
                (uuDai == null || uuDai.equals("Tất cả"))) {
            taiDanhSachKhuyenMai();
            return;
        }

        List<KhuyenMai> ketQua = timKiemKhuyenMai(maTimKiem, trangThai, uuDai);
        hienThiKetQuaTimKiem(ketQua);
    }

    private List<KhuyenMai> timKiemKhuyenMai(String maTimKiem, String trangThai, String uuDai) {
        List<KhuyenMai> tatCaKM = kmDAO.getAll();
        List<KhuyenMai> ketQua = new ArrayList<>();

        for (KhuyenMai km : tatCaKM) {
            boolean khopMa = maTimKiem.isEmpty() ||
                    km.getMaKM().toLowerCase().contains(maTimKiem.toLowerCase()) ||
                    km.getTenKM().toLowerCase().contains(maTimKiem.toLowerCase());

            boolean khopTrangThai = kiemTraTrangThai(km, trangThai);
            boolean khopUuDai = kiemTraUuDai(km, uuDai);

            if (khopMa && khopTrangThai && khopUuDai) {
                ketQua.add(km);
            }
        }

        return ketQua;
    }

    private boolean kiemTraTrangThai(KhuyenMai km, String trangThai) {
        if (trangThai == null || trangThai.equals("Tất cả")) {
            return true;
        }

        switch (trangThai) {
            case "Đang hoạt động":
                return km.getSoLuong() > 0 &&
                        km.getNgayKetThuc() != null &&
                        km.getNgayPhatHanh().isBefore(LocalDate.now()) &&
                        km.getNgayKetThuc().isAfter(LocalDate.now());
            case "Hết hạn":
                return km.getNgayKetThuc() != null &&
                        km.getNgayKetThuc().isBefore(LocalDate.now());
            case "Chưa bắt đầu":
                return km.getNgayPhatHanh() != null &&
                        km.getNgayPhatHanh().isAfter(LocalDate.now());
            default:
                return true;
        }
    }

    private boolean kiemTraUuDai(KhuyenMai km, String uuDai) {
        if (uuDai == null || uuDai.equals("Tất cả")) {
            return true;
        }
        // true = Hóa đơn, false = Món ăn
        boolean isHoaDon = km.isUuDai();
        if (uuDai.equals("Hóa đơn")) return isHoaDon;
        if (uuDai.equals("Món ăn")) return !isHoaDon;
        return true;
    }


    private void hienThiKetQuaTimKiem(List<KhuyenMai> ketQua) {
        vboxCenterScroll.getChildren().clear();

        if (ketQua.isEmpty()) {
            hienThongBao(Alert.AlertType.INFORMATION, "Kết quả tìm kiếm",
                    "Không tìm thấy khuyến mãi phù hợp với điều kiện tìm kiếm.");
        } else {
            for (KhuyenMai km : ketQua) {
                HBox the = taoTheKhuyenMai(km);
                vboxCenterScroll.getChildren().add(the);
            }
        }
    }

    // =========================
    // XÓA TRẮNG BỘ LỌC
    // =========================
    private void xoaTrangTimKiem() {
        txtTimKiem.clear();
        dpNgayLoc.setValue(null);
        if (cbTrangThai != null) {
            cbTrangThai.setValue("Tất cả");
        }
        if (cbUuDaiTimKiem != null) { // Đảm bảo có dòng này
            cbUuDaiTimKiem.setValue("Tất cả");
        }
        taiDanhSachKhuyenMai();
    }

    // =========================
    // LẤY THÔNG TIN TỪ FORM
    // =========================
    private KhuyenMai layThongTinTuForm() {
        try {
            String ma = txtMaKM.getText().trim();
            String ten = txtTenKM.getText().trim();
            String soLuongStr = txtSoLuong.getText().trim();
            LocalDate ngayBatDau = dpNgayBatDau.getValue();
            LocalDate ngayKetThuc = dpNgayKetThuc.getValue();
            String maThayThe = txtMaThayThe.getText().trim();
            String phanTramStr = txtPhanTram.getText().replace("%", "").trim();
            String uuDai = cbUudai.getValue();
            boolean isHoaDon = "Hóa đơn".equalsIgnoreCase(uuDai);

            // Kiểm tra dữ liệu bắt buộc
            if (ma.isEmpty() || ten.isEmpty() || soLuongStr.isEmpty() ||
                    ngayBatDau == null || ngayKetThuc == null || phanTramStr.isEmpty() || uuDai == null) {
                hienThongBao(Alert.AlertType.WARNING, "Thiếu thông tin", "Vui lòng điền đầy đủ thông tin bắt buộc");
                return null;
            }

            // Kiểm tra ngày hợp lệ
            if (ngayBatDau.isAfter(ngayKetThuc)) {
                hienThongBao(Alert.AlertType.WARNING, "Ngày không hợp lệ", "Ngày bắt đầu phải trước ngày kết thúc");
                return null;
            }

            int soLuong = Integer.parseInt(soLuongStr);
            int phanTram = Integer.parseInt(phanTramStr);
            // TODO: Cần thêm trường loaiUuDai vào entity KhuyenMai
            // Hiện tại vẫn sử dụng entity cũ
            return new KhuyenMai(ma, ten, soLuong, ngayBatDau, ngayKetThuc, maThayThe, phanTram, isHoaDon);

        } catch (NumberFormatException e) {
            hienThongBao(Alert.AlertType.ERROR, "Lỗi định dạng số", "Số lượng và phần trăm phải là số nguyên");
            return null;
        } catch (Exception ex) {
            hienThongBao(Alert.AlertType.ERROR, "Lỗi dữ liệu", "Dữ liệu không hợp lệ: " + ex.getMessage());
            return null;
        }
    }

    // =========================
    // XÓA TRẮNG FORM
    // =========================
    private void xoaTrangForm() {
        txtMaKM.clear();
        txtTenKM.clear();
        txtSoLuong.clear();
        dpNgayBatDau.setValue(null);
        dpNgayKetThuc.setValue(null);
        txtMaThayThe.clear();
        txtPhanTram.clear();

        if (cbUudai != null) {
            cbUudai.getSelectionModel().clearSelection();
        }

        selectedKM = null;

        // Xóa highlight từ tất cả các thẻ
        if (vboxCenterScroll != null) {
            vboxCenterScroll.getChildren().forEach(node ->
                    node.getStyleClass().remove("selected-card")
            );
        }
    }

    // =========================
    // HIỂN THỊ THÔNG BÁO
    // =========================
    private void hienThongBao(Alert.AlertType loai, String tieuDe, String noiDung) {
        Alert thongBao = new Alert(loai);
        thongBao.setTitle(tieuDe);
        thongBao.setHeaderText(null);
        thongBao.setContentText(noiDung);
        thongBao.showAndWait();
    }
}