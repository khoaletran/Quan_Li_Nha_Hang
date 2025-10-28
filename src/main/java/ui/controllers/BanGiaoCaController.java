package ui.controllers;

import dao.HoaDonDAO;
import entity.HoaDon;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.geometry.Pos;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
public class BanGiaoCaController {
    @FXML private VBox vboxHoaDon;
    @FXML private TextField txtCaLam,txtTGVC,txtsLHD,txtSoTienMat,txtSoTienCK,txtTongTien;
    @FXML private TextArea taMoTa;

    private HoaDonDAO hoaDonDAO = new HoaDonDAO();

    @FXML
    public void initialize() {
        loadTatCaHoaDon();
    }

    private void loadTatCaHoaDon() {
        vboxHoaDon.getChildren().clear();

        List<HoaDon> danhSach = hoaDonDAO.getAll();
        for (HoaDon hd : danhSach) {

            Circle circle = new Circle(20);
            circle.setStyle("-fx-fill: #d8d8d8;");

            // Label mã HD
            Label lblMaHD = new Label(hd.getMaHD());
            lblMaHD.getStyleClass().add("invoice-id");

            // Label số điện thoại
            Label lblSDT = new Label("SDT: " + hd.getKhachHang().getSdt());
            lblSDT.getStyleClass().add("invoice-phone");

            // Box chứa thông tin
            VBox boxThongTin = new VBox(2, lblMaHD, lblSDT);

            // Thẻ HBox chính
            HBox card = new HBox(10, circle, boxThongTin);
            card.setAlignment(Pos.CENTER_LEFT);
            card.getStyleClass().add("invoice-card");
            VBox.setMargin(card, new Insets(5, 0, 5, 0));

            // Khi click thẻ — hiển thị thông tin chi tiết (tuỳ bạn thêm sau)
//            card.setOnMouseClicked(e -> hienThongTinChiTiet(hd));

            vboxHoaDon.getChildren().add(card);
        }
    }

    public void setThoiGianVaoCa(LocalDateTime thoiGian) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        txtTGVC.setText(thoiGian.format(formatter));
    }

}
