package ui.controllers;

import dao.KhachHangDAO;
import dao.PhieuKetCaDAO;
import entity.HangKhachHang;
import entity.KhachHang;
import entity.NhanVien;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.StringConverter;
import ui.AlertCus;
import ui.ConfirmCus;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class QLThanhVienController {

    @FXML private FlowPane menuFlow;
    @FXML private TextField txtTenNV, txtSDT, txtMatKhau,txtHangKH,txtDiemTL;
    @FXML private RadioButton rdoNam, rdoNu;
    @FXML private Label lblMaNV;
    @FXML private Button btnThemNV, btnXacNhan, btnXoa;

    private final KhachHangDAO khDAO = new KhachHangDAO();
    private final ToggleGroup genderGroup = new ToggleGroup();

    @FXML
    public void initialize() {
        // setup nhóm radio
        rdoNam.setToggleGroup(genderGroup);
        rdoNu.setToggleGroup(genderGroup);

        // sự kiện nút
        btnThemNV.setOnAction(e -> xoaTrangThongTin());
        btnXacNhan.setOnAction(e -> xuLyXacNhan());
//        btnXoa.setOnAction(e -> xoaNV());

        // lắng nghe mã NV để đổi nút động
        langNgheThayDoiMaKH();

        // load danh sách ban đầu
        loadNhanVienCards();

        txtDiemTL.setEditable(false);
    }

    // =========================
    // DANH SÁCH NHÂN VIÊN
    // =========================
    private void loadNhanVienCards() {
        menuFlow.getChildren().clear();
        List<KhachHang> ds = khDAO.getAll();

        for (KhachHang kh : ds) {
            VBox card = taoTheKhachHang(kh);
            menuFlow.getChildren().add(card);
        }
    }

    private VBox taoTheKhachHang(KhachHang kh) {
        VBox card = new VBox(5);
        card.getStyleClass().add("menu-card");

        ImageView img;
        if (kh.isGioiTinh()){
            img = new ImageView(new Image(getClass().getResourceAsStream("/IMG/icon/man.png")));
        } else{
            img = new ImageView(new Image(getClass().getResourceAsStream("/IMG/icon/woman.png")));
        }


        img.setFitWidth(120);
        img.setFitHeight(120);

        Label lblMa = new Label(kh.getMaKhachHang());
        lblMa.getStyleClass().add("menu-item-name");

        Label lblTen = new Label(kh.getTenKhachHang());
        lblTen.getStyleClass().add("menu-item-price");

        card.getChildren().addAll(img, lblMa, lblTen);
        card.setOnMouseClicked(e -> hienThiThongTin(kh));

        return card;
    }

    // =========================
    // FORM HIỂN THỊ / RESET
    // =========================
    private void hienThiThongTin(KhachHang kh) {
        int diemHang = kh.getHangKhachHang().getDiemHang();
        String hang;

        if (diemHang >= 0 && diemHang <= 199) {
            hang = "Đồng";
        } else if (diemHang >= 200 && diemHang <= 499) {
            hang = "Bạc";
        } else if (diemHang >= 500 && diemHang <= 999) {
            hang = "Vàng";
        } else if (diemHang >= 1000 && diemHang <= 1999) {
            hang = "Bạch kim";
        } else if (diemHang >= 2000) {
            hang = "Kim cương";
        } else {
            hang = "Đồng";
        }
        lblMaNV.setText(kh.getMaKhachHang());
        txtTenNV.setText(kh.getTenKhachHang());
        txtSDT.setText(kh.getSdt());
        txtHangKH.setText(hang);
        rdoNam.setSelected(kh.isGioiTinh());
        rdoNu.setSelected(!kh.isGioiTinh());
        txtDiemTL.setText(String.valueOf(kh.getDiemTichLuy()));
    }

    private void xoaTrangThongTin() {
        lblMaNV.setText(tuSinhMaKH(KhachHangDAO.getMaKHCuoi()));
        txtTenNV.clear();
        txtSDT.clear();
        txtHangKH.clear();
        rdoNam.setSelected(false);
        rdoNu.setSelected(false);
        txtDiemTL.clear();
    }

    // =========================
    // LẮNG NGHE + XỬ LÝ NÚT
    // =========================
    private void langNgheThayDoiMaKH() {
        lblMaNV.textProperty().addListener((obs, oldValue, newValue) -> {
            boolean tonTai = KhachHangDAO.getAll()
                    .stream()
                    .anyMatch(nv -> nv.getMaKhachHang().equalsIgnoreCase(newValue.trim()));
            btnXacNhan.setText(tonTai ? "Lưu Thay Đổi" : "Thêm Khách");
        });
    }

    private void xuLyXacNhan() {
        String maKH = lblMaNV.getText().trim();
        boolean tonTai = KhachHangDAO.getAll()
                .stream()
                .anyMatch(nv -> nv.getMaKhachHang().equalsIgnoreCase(maKH));

        if (tonTai) capNhatNV(maKH);
        else themNV();
    }

    // =========================
    // HÀM TỰ SINH MÃ
    // =========================
    private String tuSinhMaKH(String maKH) {
        int so = Integer.parseInt(maKH.substring(2));
        return String.format("KH%04d", so + 1);
    }

    // =========================
    // THÊM / CẬP NHẬT / XÓA
    // =========================
    private void themNV() {
        KhachHang kh = taoKhachHangTuForm(tuSinhMaKH(KhachHangDAO.getMaKHCuoi()));
        if (kh == null) return;
        boolean answer = ConfirmCus.show("Xác nhận", "Xác nhận thêm khách làm thành viên");
        if (answer) {
            boolean success = KhachHangDAO.insert(kh);
            if (success) {
                AlertCus.show("Thông báo", "Thêm khách hàng thành công: " + kh.getMaKhachHang());
                loadNhanVienCards();
                xoaTrangThongTin();
            } else {
                AlertCus.show("Thông báo", "Thêm khách hàng thất bại!: " + kh.getMaKhachHang());
            }
        }
    }

    private void capNhatNV(String maKH) {
        KhachHang kh = taoKhachHangTuForm(maKH);
        if (kh == null) return;
        boolean answer = ConfirmCus.show("Xác nhận", "Xác nhận cập nhật thông tin khách hàng");
        if (answer) {
            boolean success = KhachHangDAO.update(kh);
            if (success) {
                AlertCus.show("Thông báo", "Cập nhật khách hàng thành công: " + kh.getMaKhachHang());
                loadNhanVienCards();
            } else {
                AlertCus.show("Thông báo", "Cập nhật khách hàng thất bại!: " + kh.getMaKhachHang());
            }
        }
    }

    private void xoaNV() {
        String maKH = lblMaNV.getText().trim();
        if (maKH.isEmpty()) {
            AlertCus.show("Thông báo","Vui lòng chọn khách hàng cần xóa");
            return;
        }
        if (ui.XacNhanXoa.hienHopThoaiXacNhan("Xác nhận xóa", "Bạn có chắc muốn xóa khách hàng " + maKH + " không?")) {
            KhachHangDAO.delete(maKH);
            loadNhanVienCards();
            xoaTrangThongTin();
        }
    }

    // =========================
    // TẠO NHÂN VIÊN TỪ FORM
    // =========================
    private KhachHang taoKhachHangTuForm(String maKH) {
        if (!validTextField()) return null;
        String tenKH = txtTenNV.getText().trim();
        String sdt = txtSDT.getText().trim();
        boolean gioiTinh = rdoNam.isSelected();
        int diemTL = 0;
        if(txtDiemTL.getText().equals("") || txtDiemTL.getText() == null) {
            diemTL = 0;
        }else diemTL = Integer.parseInt(txtDiemTL.getText().trim());
        String maHang;
        int giamGia;
        String moTa;
        int diemHang;
        if (diemTL >= 0 && diemTL <= 199) {
            maHang = "HH0001";
            giamGia = 0;
            moTa = "Hạng Đồng - Mới tham gia";
            diemHang = 0;
        } else if (diemTL >= 200 && diemTL <= 499) {
            maHang = "HH0002";
            giamGia = 5;
            moTa = "Hạng Bạc - Khách thân thiết";
            diemHang = 200;
        } else if (diemTL >= 500 && diemTL <= 999) {
            maHang = "HH0003";
            giamGia = 10;
            moTa = "Hạng Vàng - Khách VIP nhỏ";
            diemHang = 500;
        } else if (diemTL >= 1000 && diemTL <= 1999) {
            maHang = "HH0004";
            giamGia = 15;
            moTa = "Hạng Bạch Kim - Khách VIP lớn";
            diemHang = 1000;
        } else if (diemTL >= 2000) {
            maHang = "HH0005";
            giamGia = 20;
            moTa = "Hạng Kim Cương - Khách siêu VIP";
            diemHang = 2000;
        } else {
            maHang = "HH0001";
            giamGia = 0;
            moTa = "Hạng Đồng - Mới tham gia";
            diemHang = 0;
        }

        HangKhachHang hangKH = new HangKhachHang(maHang,moTa,giamGia,diemHang);

        KhachHang kh = new KhachHang();
        kh.setMaKhachHang(maKH);
        kh.setTenKhachHang(tenKH);
        kh.setSdt(sdt);
        kh.setGioiTinh(gioiTinh);
        kh.setDiemTichLuy(diemTL);
        kh.setHangKhachHang(hangKH);
        return kh;
    }

    private boolean validTextField() {
        String ten = txtTenNV.getText().trim();
        String sdt = txtSDT.getText().trim();
        String diemTLText = txtDiemTL.getText().trim();

        if (ten.isEmpty()) {
            AlertCus.show("Thông báo", "Bạn chưa nhập tên khách hàng!");
            txtTenNV.requestFocus();
            return false;
        }
        if (sdt.isEmpty()) {
            AlertCus.show("Thông báo", "Bạn chưa nhập số điện thoại!");
            txtSDT.requestFocus();
            return false;
        }
        if (!sdt.matches("^0[3-9]\\d{8}$")) {
            AlertCus.show("Thông báo", "Số điện thoại không hợp lệ! Phải gồm 10 chữ số và bắt đầu bằng 03–09!");
            txtSDT.requestFocus();
            return false;
        }
        if (!rdoNam.isSelected() && !rdoNu.isSelected()) {
            AlertCus.show("Thông báo", "Vui lòng chọn giới tính!");
            return false;
        }
        if (diemTLText.isEmpty()) {
            AlertCus.show("Thông báo", "Bạn chưa nhập điểm tích lũy!");
            txtDiemTL.requestFocus();
            return false;
        }
        try {
            int diemTL = Integer.parseInt(diemTLText);
            if (diemTL < 0) {
                AlertCus.show("Thông báo", "Điểm tích lũy không được âm!");
                txtDiemTL.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            AlertCus.show("Thông báo", "Điểm tích lũy phải là số nguyên!");
            txtDiemTL.requestFocus();
            return false;
        }
        return true;
    }

}
