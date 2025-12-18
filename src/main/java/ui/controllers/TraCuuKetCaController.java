package ui.controllers;

import dao.PhieuKetCaDAO;
import entity.PhieuKetCa;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class TraCuuKetCaController implements Initializable {

    @FXML private VBox vbox_center_scroll;
    @FXML private TextField txtMaPhieu;
    @FXML private TextField txtTenNV;
    @FXML private TextField txtCaLam;
    @FXML private TextField txtSoDon;
    @FXML private TextField txtTgVaoCa;
    @FXML private TextField txtTgKetCa;
    @FXML private TextField txtTienMat;
    @FXML private TextField txtChuyenKhoan;
    @FXML private TextField txtTongTien;
    @FXML private TextField txtTienChenhLech;
    @FXML private TextArea taMoTa;

    private static final DateTimeFormatter DT_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private static final NumberFormat VND_FORMAT =
            NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));



    private final PhieuKetCaDAO phieuKetCaDAO = new PhieuKetCaDAO();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadDanhSachPhieuKetCa();
    }

    private void loadDanhSachPhieuKetCa() {
        vbox_center_scroll.getChildren().clear();

        List<PhieuKetCa> list = phieuKetCaDAO.getAllForTraCuu();

        for (PhieuKetCa p : list) {
            VBox orderCard = createOrderCard(p);
            vbox_center_scroll.getChildren().add(orderCard);
        }
    }

    private VBox createOrderCard(PhieuKetCa p) {

        VBox card = new VBox(10);
        card.getStyleClass().add("kc-card");

        // ===== Ảnh =====
        ImageView img = new ImageView(
                new Image(getClass().getResource("/IMG/avatar.png").toExternalForm())
        );
        img.setFitWidth(56);
        img.setFitHeight(56);
        img.getStyleClass().add("kc-card-image");

        // ===== Thông tin =====
        VBox infoBox = new VBox(4);

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        Label lblTime = new Label(
                p.getNgayKetCa() != null
                        ? "Thời gian: " +p.getTgLogIn().format(fmt)+" - "+p.getNgayKetCa().format(fmt)
                        : "Chưa kết ca"
        );
        lblTime.getStyleClass().add("kc-card-time");

        Label lblNhanVien = new Label("Nhân viên: "+p.getNhanVien().getTenNV());
        lblNhanVien.getStyleClass().add("kc-card-staff");

        Label lblTongTien = new Label(
                String.format("Tổng tiền: %,.0f ₫", p.getTienMat() + p.getTienCK())
        );
        lblTongTien.getStyleClass().add("kc-card-total");

        infoBox.getChildren().addAll(lblTime, lblNhanVien, lblTongTien);

        // ===== Layout ngang =====
        HBox row = new HBox(12, img, infoBox);
        row.setAlignment(Pos.CENTER_LEFT);

        card.getChildren().add(row);
        card.setOnMouseClicked(e -> hienThiPhieuKetCa(p));

        return card;
    }

    private void hienThiPhieuKetCa(PhieuKetCa p) {
        txtMaPhieu.setText(String.valueOf(p.getMaPhieu()));
        txtTenNV.setText(p.getNhanVien().getTenNV());
        txtCaLam.setText(p.isCa() ? "Ca tối" : "Ca sáng");
        txtSoDon.setText(String.valueOf(p.getSoHoaDon()));

        txtTgVaoCa.setText(
                p.getTgLogIn() != null ? p.getTgLogIn().format(DT_FORMAT) : ""
        );

        txtTgKetCa.setText(
                p.getNgayKetCa() != null ? p.getNgayKetCa().format(DT_FORMAT) : ""
        );

        txtTienMat.setText(VND_FORMAT.format(p.getTienMat()));
        txtChuyenKhoan.setText(VND_FORMAT.format(p.getTienCK()));

        double tongTien = p.getTienMat() + p.getTienCK();
        txtTongTien.setText(VND_FORMAT.format(tongTien));

        txtTienChenhLech.setText(VND_FORMAT.format(p.getTienChenhLech()));

        taMoTa.setText(p.getMoTa());
    }



}
