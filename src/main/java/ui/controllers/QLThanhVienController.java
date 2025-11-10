package ui.controllers;

import dao.KhachHangDAO;
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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class QLThanhVienController {

    @FXML private FlowPane menuFlow;
    @FXML private TextField txtTenNV, txtSDT, txtMatKhau,txtHangKH;
    @FXML private DatePicker txtNgayVaoLam;
    @FXML private ComboBox<String> comboHangKH;
    @FXML private RadioButton rdoNam, rdoNu, rdoConLam, rdoNghiLam;
    @FXML private Label lblMaNV;
    @FXML private Button btnThemNV, btnXacNhan, btnXoa;

    private final KhachHangDAO khDAO = new KhachHangDAO();
    private final ToggleGroup genderGroup = new ToggleGroup();
    private final ToggleGroup statusGroup = new ToggleGroup();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

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
        langNgheThayDoiMaNV();

        // load danh sách ban đầu
        loadNhanVienCards();
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
        lblMaNV.setText(kh.getMaKhachHang());
        txtTenNV.setText(kh.getTenKhachHang());
        txtSDT.setText(kh.getSdt());
        txtHangKH.setText(kh.getHangKhachHang().getMaHang());
        rdoNam.setSelected(kh.isGioiTinh());
        rdoNu.setSelected(!kh.isGioiTinh());
    }

    private void xoaTrangThongTin() {
        lblMaNV.setText(tuSinhMaNV(KhachHangDAO.maKHCuoi()));
        txtTenNV.clear();
        txtSDT.clear();
        txtHangKH.clear();
        rdoNam.setSelected(false);
        rdoNu.setSelected(false);
    }

    // =========================
    // LẮNG NGHE + XỬ LÝ NÚT
    // =========================
    private void langNgheThayDoiMaNV() {
        lblMaNV.textProperty().addListener((obs, oldValue, newValue) -> {
            boolean tonTai = KhachHangDAO.getAll()
                    .stream()
                    .anyMatch(nv -> nv.getMaKhachHang().equalsIgnoreCase(newValue.trim()));
            btnXacNhan.setText(tonTai ? "Lưu Thay Đổi" : "Thêm Khách Hàng");
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
    private String tuSinhMaNV(String maKH) {
        int so = Integer.parseInt(maKH.substring(2));
        return String.format("KH%04d", so + 1);
    }

    // =========================
    // THÊM / CẬP NHẬT / XÓA
    // =========================
    private void themNV() {
        KhachHang kh = taoKhachHangTuForm(tuSinhMaNV(KhachHangDAO.maKHCuoi()));
        if (KhachHangDAO.insert(kh)) {
            System.out.println("Thêm khách hàng thành công: " + kh.getMaKhachHang());
            loadNhanVienCards();
            xoaTrangThongTin();
        } else System.err.println("Thêm khách hàng thất bại!");
    }

    private void capNhatNV(String maKH) {
        KhachHang kh = taoKhachHangTuForm(maKH);
        if (KhachHangDAO.update(kh)) {
            System.out.println("Cập nhật khách hàng: " + maKH + " thành công!");
            loadNhanVienCards();
        } else System.err.println("Cập nhật thất bại cho mã: " + maKH);
    }

    private void xoaNV() {
        String maKH = lblMaNV.getText().trim();
        if (maKH.isEmpty()) {
            System.err.println("Vui lòng chọn khách hàng cần xóa!");
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
        String tenKH = txtTenNV.getText().trim();
        String sdt = txtSDT.getText().trim();
        boolean gioiTinh = rdoNam.isSelected();
        String hangKH = txtHangKH.getText().trim();

        KhachHang kh = new KhachHang();
        kh.setMaKhachHang(maKH);
        kh.setTenKhachHang(tenKH);
        kh.setSdt(sdt);
        kh.setGioiTinh(gioiTinh);
        return kh;
    }
}
