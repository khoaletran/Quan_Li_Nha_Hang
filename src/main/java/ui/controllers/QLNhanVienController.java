package ui.controllers;

import dao.NhanVienDAO;
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

import java.time.LocalDate;
import java.util.List;

public class QLNhanVienController {

    @FXML private FlowPane menuFlow;
    @FXML private TextField txtTenNV, txtSDT, txtNgayVaoLam, txtMatKhau;
    @FXML private ComboBox<String> comboChucVu;
    @FXML private RadioButton rdoNam, rdoNu, rdoConLam, rdoNghiLam;
    @FXML private Label lblMaNV;
    @FXML private Button btnThemNV, btnXacNhan, btnXoa;

    private final NhanVienDAO nvDAO = new NhanVienDAO();
    private final ToggleGroup genderGroup = new ToggleGroup();
    private final ToggleGroup statusGroup = new ToggleGroup();

    @FXML
    public void initialize() {
        // setup nhóm radio
        rdoNam.setToggleGroup(genderGroup);
        rdoNu.setToggleGroup(genderGroup);
        rdoConLam.setToggleGroup(statusGroup);
        rdoNghiLam.setToggleGroup(statusGroup);

        // combo chức vụ
        comboChucVu.getItems().addAll("Nhân viên", "Quản lý");

        // sự kiện nút
        btnThemNV.setOnAction(e -> xoaTrangThongTin());
        btnXacNhan.setOnAction(e -> xuLyXacNhan());
//        btnXoa.setOnAction(e -> xoaNV());

        // lắng nghe mã NV để đổi nút động
        langNgheThayDoiMaNV();

        // load danh sách ban đầu
        loadNhanVienCards();
    }

    // =========================
    // DANH SÁCH NHÂN VIÊN
    // =========================
    private void loadNhanVienCards() {
        menuFlow.getChildren().clear();
        List<NhanVien> ds = nvDAO.getAll();

        for (NhanVien nv : ds) {
            VBox card = taoTheNhanVien(nv);
            menuFlow.getChildren().add(card);
        }
    }

    private VBox taoTheNhanVien(NhanVien nv) {
        VBox card = new VBox(5);
        card.getStyleClass().add("menu-card");

        ImageView img = new ImageView(new Image(getClass().getResourceAsStream("/IMG/avatar.png")));
        img.setFitWidth(120);
        img.setFitHeight(120);

        Label lblMa = new Label(nv.getMaNV());
        lblMa.getStyleClass().add("menu-item-name");

        Label lblTen = new Label(nv.getTenNV());
        lblTen.getStyleClass().add("menu-item-price");

        card.getChildren().addAll(img, lblMa, lblTen);
        card.setOnMouseClicked(e -> hienThiThongTin(nv));

        return card;
    }

    // =========================
    // FORM HIỂN THỊ / RESET
    // =========================
    private void hienThiThongTin(NhanVien nv) {
        lblMaNV.setText(nv.getMaNV());
        txtTenNV.setText(nv.getTenNV());
        txtSDT.setText(nv.getSdt());
        txtNgayVaoLam.setText(nv.getNgayVaoLam().toString());
        comboChucVu.setValue(nv.isQuanLi() ? "Quản lý" : "Nhân viên");

        rdoNam.setSelected(nv.isGioiTinh());
        rdoNu.setSelected(!nv.isGioiTinh());
        rdoConLam.setSelected(nv.isTrangThai());
        rdoNghiLam.setSelected(!nv.isTrangThai());
    }

    private void xoaTrangThongTin() {
        lblMaNV.setText(tuSinhMaNV(NhanVienDAO.maNVCuoi()));
        txtTenNV.clear();
        txtSDT.clear();
        txtNgayVaoLam.clear();
        comboChucVu.setValue(null);
        rdoNam.setSelected(false);
        rdoNu.setSelected(false);
        rdoConLam.setSelected(false);
        rdoNghiLam.setSelected(false);
    }

    // =========================
    // LẮNG NGHE + XỬ LÝ NÚT
    // =========================
    private void langNgheThayDoiMaNV() {
        lblMaNV.textProperty().addListener((obs, oldValue, newValue) -> {
            boolean tonTai = NhanVienDAO.getAll()
                    .stream()
                    .anyMatch(nv -> nv.getMaNV().equalsIgnoreCase(newValue.trim()));
            btnXacNhan.setText(tonTai ? "Lưu Thay Đổi" : "Thêm Nhân Viên");
        });
    }

    private void xuLyXacNhan() {
        String maNV = lblMaNV.getText().trim();
        boolean tonTai = NhanVienDAO.getAll()
                .stream()
                .anyMatch(nv -> nv.getMaNV().equalsIgnoreCase(maNV));

        if (tonTai) capNhatNV(maNV);
        else themNV();
    }

    // =========================
    // HÀM TỰ SINH MÃ
    // =========================
    private String tuSinhMaNV(String maNV) {
        int so = Integer.parseInt(maNV.substring(2));
        return String.format("NV%04d", so + 1);
    }

    // =========================
    // THÊM / CẬP NHẬT / XÓA
    // =========================
    private void themNV() {
        NhanVien nv = taoNhanVienTuForm(tuSinhMaNV(NhanVienDAO.maNVCuoi()));
        if (NhanVienDAO.insert(nv)) {
            System.out.println("Thêm nhân viên thành công: " + nv.getMaNV());
            loadNhanVienCards();
            xoaTrangThongTin();
        } else System.err.println("Thêm nhân viên thất bại!");
    }

    private void capNhatNV(String maNV) {
        NhanVien nv = taoNhanVienTuForm(maNV);
        if (NhanVienDAO.update(nv)) {
            System.out.println("Cập nhật nhân viên: " + maNV + " thành công!");
            loadNhanVienCards();
        } else System.err.println("Cập nhật thất bại cho mã: " + maNV);
    }

    private void xoaNV() {
        String maNV = lblMaNV.getText().trim();
        if (maNV.isEmpty()) {
            System.err.println("Vui lòng chọn nhân viên cần xóa!");
            return;
        }
        if (ui.XacNhanXoa.hienHopThoaiXacNhan("Xác nhận xóa", "Bạn có chắc muốn xóa nhân viên " + maNV + " không?")) {
            NhanVienDAO.delete(maNV);
            loadNhanVienCards();
            xoaTrangThongTin();
        }
    }

    // =========================
    // TẠO NHÂN VIÊN TỪ FORM
    // =========================
    private NhanVien taoNhanVienTuForm(String maNV) {
        String tenNV = txtTenNV.getText().trim();
        String sdt = txtSDT.getText().trim();
        LocalDate ngayVaoLam = LocalDate.parse(txtNgayVaoLam.getText().trim());
        boolean gioiTinh = rdoNam.isSelected();
        boolean trangThai = rdoConLam.isSelected();
        boolean quanLi = "Quản lý".equalsIgnoreCase(comboChucVu.getValue());
        String matKhau = txtMatKhau.getText().isEmpty() ? "123" : txtMatKhau.getText();

        NhanVien nv = new NhanVien();
        nv.setMaNV(maNV);
        nv.setTenNV(tenNV);
        nv.setSdt(sdt);
        nv.setGioiTinh(gioiTinh);
        nv.setQuanLi(quanLi);
        nv.setNgayVaoLam(ngayVaoLam);
        nv.setTrangThai(trangThai);
        nv.setMatKhau(matKhau);
        return nv;
    }
}
