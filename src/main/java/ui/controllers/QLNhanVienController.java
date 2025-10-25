package ui.controllers;

import dao.NhanVienDAO;
import entity.NhanVien;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.util.List;

public class QLNhanVienController {

    @FXML private FlowPane menuFlow;
    @FXML private TextField txtTenNV, txtSDT, txtNgaySinh, txtNgayVaoLam;
    @FXML private ComboBox<String> comboChucVu;
    @FXML private RadioButton rdoNam, rdoNu, rdoConLam, rdoNghiLam;
    @FXML private Label lblMaNV;

    private final NhanVienDAO nvDAO = new NhanVienDAO();
    private ToggleGroup genderGroup = new ToggleGroup();
    private ToggleGroup statusGroup = new ToggleGroup();

    @FXML
    public void initialize() {
        rdoNam.setToggleGroup(genderGroup);
        rdoNu.setToggleGroup(genderGroup);
        rdoConLam.setToggleGroup(statusGroup);
        rdoNghiLam.setToggleGroup(statusGroup);

        comboChucVu.getItems().addAll("Nhân viên", "Quản lý");
        loadNhanVienCards();
    }

    // =========================
    // HIỂN THỊ DANH SÁCH NHÂN VIÊN
    // =========================
    private void loadNhanVienCards() {
        menuFlow.getChildren().clear();
        List<NhanVien> ds = nvDAO.getAll();

        for (NhanVien nv : ds) {
            VBox card = taoTheNhanVien(nv);
            menuFlow.getChildren().add(card);
        }
    }

    // Tạo 1 thẻ nhân viên (card)
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

        // Khi bấm vào card -> hiển thị chi tiết
        card.setOnMouseClicked(e -> hienThiThongTin(nv));

        return card;
    }

    // =========================
    // HIỂN THỊ THÔNG TIN NHÂN VIÊN
    // =========================
    private void hienThiThongTin(NhanVien nv) {
        lblMaNV.setText(nv.getMaNV());
        txtTenNV.setText(nv.getTenNV());
        txtSDT.setText(nv.getSdt());
        txtNgayVaoLam.setText(nv.getNgayVaoLam().toString());
        comboChucVu.setValue(nv.isQuanLi() ? "Quản lý" : "Nhân viên");

        if (nv.isGioiTinh()) rdoNam.setSelected(true);
        else rdoNu.setSelected(true);

        if (nv.isTrangThai()) rdoConLam.setSelected(true);
        else rdoNghiLam.setSelected(true);
    }
}
