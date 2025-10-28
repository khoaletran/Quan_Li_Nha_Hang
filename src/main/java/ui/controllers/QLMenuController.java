package ui.controllers;

import dao.LoaiMonDAO;
import dao.MonDAO;
import entity.LoaiMon;
import entity.Mon;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;

import java.io.File;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;

public class QLMenuController {

    @FXML
    private FlowPane flowMonAn;
    @FXML
    private ComboBox<String> cboLoaiMonFilter;
    @FXML
    private ComboBox<String> cboLoaiMon;

    @FXML
    private Label lblMaMon;
    @FXML
    private TextField txtTenMon, txtMoTa, txtGiaGoc, txtSoLuong;
    @FXML
    private ImageView imgMon;
    @FXML
    private Button btnXacNhan;
    @FXML
    private TextField searchField;

    private final MonDAO monDAO = new MonDAO();
    private File selectedFile;

    @FXML
    public void initialize() {
        loadComboDanhMuc();
        loadDanhSachMon(); // hiển thị tất cả
        cboLoaiMonFilter.setOnAction(e -> locMonTheoDanhMuc());
        // TextField tìm kiếm realtime
        searchField.textProperty().addListener((obs, oldText, newText) -> filterMon());
    }

    // Hàm lọc món kết hợp tên + loại
    private void filterMon() {
        String keyword = searchField.getText().toLowerCase().trim();
        String selectedLoai = cboLoaiMonFilter.getSelectionModel().getSelectedItem();

        flowMonAn.getChildren().clear();

        for (Mon mon : MonDAO.getAll()) {
            boolean matchName = mon.getTenMon().toLowerCase().contains(keyword);
            boolean matchLoai = selectedLoai == null
                    || selectedLoai.equals("Tất cả")
                    || (mon.getLoaiMon() != null && selectedLoai.equals(mon.getLoaiMon().getTenLoaiMon()));

            if (matchName && matchLoai) {
                flowMonAn.getChildren().add(taoCardMon(mon, null));
            }
        }
    }

    private void loadComboDanhMuc() {
        cboLoaiMonFilter.getItems().clear();
        cboLoaiMon.getItems().clear();

        cboLoaiMonFilter.getItems().add("Tất cả"); // filter xem tất cả
        for (LoaiMon lm : LoaiMonDAO.getAll()) {
            cboLoaiMonFilter.getItems().add(lm.getTenLoaiMon());
            cboLoaiMon.getItems().add(lm.getTenLoaiMon());
        }

        cboLoaiMonFilter.getSelectionModel().selectFirst();
    }

    private void loadDanhSachMon() {
        flowMonAn.getChildren().clear();
        List<Mon> danhSach = monDAO.getAll();
        for (Mon mon : danhSach) {
            flowMonAn.getChildren().add(taoCardMon(mon, null));
        }
    }

    private void locMonTheoDanhMuc() {
        String selectedLoai = cboLoaiMonFilter.getSelectionModel().getSelectedItem();
        flowMonAn.getChildren().clear();

        if (selectedLoai == null || selectedLoai.equals("Tất cả")) {
            loadDanhSachMon();
            return;
        }

        for (Mon mon : MonDAO.getAll()) {
            if (mon.getLoaiMon() != null &&
                    selectedLoai.equals(mon.getLoaiMon().getTenLoaiMon())) {
                flowMonAn.getChildren().add(taoCardMon(mon, null));
            }
        }
    }

    private VBox taoCardMon(Mon mon, File file) {
        // ===== 1. Card chính =====
        VBox card = new VBox();
        card.getStyleClass().add("menu-item");
        card.setPrefSize(250, 250);  // cố định chiều ngang và cao
        card.setMaxSize(250, 250);
        card.setMinSize(250, 250);

        // ===== 2. Khung hình cố định =====
        StackPane imagePane = new StackPane();
        imagePane.setPrefSize(180, 180); // khung hình cố định

        ImageView imageView = new ImageView();
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        imageView.setCache(true);
        imageView.setFitWidth(180);
        imageView.setFitHeight(180);

        // Clip để hình không tràn khung
        javafx.scene.shape.Rectangle clip = new javafx.scene.shape.Rectangle(180, 180);
        imageView.setClip(clip);

        // ===== 3. Load hình =====
        try {
            if (file != null && file.exists()) {
                imageView.setImage(new Image(file.toURI().toString()));
            } else {
                File localFile = new File("src/main/resources/IMG/food/" + mon.getHinhAnh());
                if (localFile.exists()) {
                    imageView.setImage(new Image(localFile.toURI().toString()));
                } else {
                    imageView.setImage(new Image(getClass().getResourceAsStream("/IMG/food/restaurant.png")));
                }
            }
        } catch (Exception e) {
            imageView.setImage(new Image(getClass().getResourceAsStream("/IMG/food/restaurant.png")));
        }


        imagePane.getChildren().add(imageView);

        // ===== 4. Info Box =====
        Label lblTen = new Label(mon.getTenMon());
        lblTen.getStyleClass().add("item-name");
        lblTen.setWrapText(true);

        Label lblGia = new Label(formatCurrency(mon.getGiaGoc()));
        lblGia.getStyleClass().add("item-price");
        lblGia.setWrapText(true);
        lblGia.setPrefWidth(90);

        Region space = new Region();
        HBox.setHgrow(space, Priority.ALWAYS);

        HBox infoBox = new HBox(lblTen, space, lblGia);
        infoBox.getStyleClass().add("item-info");

        // ===== 5. Thêm vào card =====
        card.getChildren().addAll(imagePane, infoBox);

        // ===== 6. Sự kiện click =====
        card.setOnMouseClicked(e -> loadChiTietMon(mon));

        return card;
    }


    private void loadChiTietMon(Mon mon) {
        lblMaMon.setText(mon.getMaMon());
        txtTenMon.setText(mon.getTenMon());
        txtMoTa.setText(mon.getMoTa());
        txtGiaGoc.setText(String.valueOf(mon.getGiaGoc()));
        txtSoLuong.setText(String.valueOf(mon.getSoLuong()));

        if (mon.getLoaiMon() != null) {
            cboLoaiMon.getSelectionModel().select(mon.getLoaiMon().getTenLoaiMon());
        } else {
            cboLoaiMon.getSelectionModel().clearSelection();
        }

        try {
            File localFile = new File("src/main/resources/IMG/food/" + mon.getHinhAnh());
            if (localFile.exists()) {
                imgMon.setImage(new Image(localFile.toURI().toString()));
            } else {
                imgMon.setImage(new Image(getClass().getResourceAsStream("/IMG/food/restaurant.png")));
            }
        } catch (Exception e) {
            imgMon.setImage(new Image(getClass().getResourceAsStream("/IMG/food/restaurant.png")));
        }


        // Khi load chi tiết món thì button sẽ đổi text
        btnXacNhan.setText("Xác nhận");
    }

    @FXML
    private void chonAnh() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn hình ảnh món");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Hình ảnh", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        File file = fileChooser.showOpenDialog(imgMon.getScene().getWindow());
        if (file != null) {
            selectedFile = file;
            imgMon.setImage(new Image(file.toURI().toString()));
            System.out.println("Đường dẫn ảnh: " + file.getAbsolutePath());
            try {
                File dest = new File("src/main/resources/IMG/food/" + file.getName());
                java.nio.file.Files.copy(file.toPath(), dest.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @FXML
    private void addMon() {
        // Khi thêm mới, reset tất cả fields
        resetFields();
        // Đổi text button thành "Thêm mới"
        btnXacNhan.setText("Thêm mới");
    }

    private void resetFields() {
        lblMaMon.setText("");
        txtTenMon.setText("");
        txtMoTa.setText("");
        txtGiaGoc.setText("");
        txtSoLuong.setText("");
        cboLoaiMon.getSelectionModel().clearSelection();

        try {
            imgMon.setImage(new Image(getClass().getResourceAsStream("/IMG/food/restaurant.png")));
        } catch (Exception e) {
            imgMon.setImage(null);
        }

        selectedFile = null;
    }

    @FXML
    private void xacNhan() {
        String maMon = lblMaMon.getText().trim();
        String tenMon = txtTenMon.getText().trim();
        String moTa = txtMoTa.getText().trim();
        String giaStr = txtGiaGoc.getText().trim();
        String soLuongStr = txtSoLuong.getText().trim();

        String maLoai = "";
        if (cboLoaiMon.getSelectionModel().getSelectedItem() != null) {
            maLoai = LoaiMonDAO.getMaLoaiMonByTen(cboLoaiMon.getSelectionModel().getSelectedItem());
        }
        LoaiMon loaiMon = new LoaiMon(maLoai);

        double giaGoc;
        int soLuong;
        try {
            giaGoc = Double.parseDouble(giaStr);
            soLuong = Integer.parseInt(soLuongStr);
        } catch (NumberFormatException e) {
            System.out.println("Giá hoặc số lượng không hợp lệ!");
            return;
        }

        // Xử lý ảnh
        String tenAnh = "restaurant.png"; // default
        if (selectedFile != null) {
            tenAnh = selectedFile.getName();
            // copy file vào resources
            try {
                File dest = new File("src/main/resources/IMG/food/" + tenAnh);
                java.nio.file.Files.copy(selectedFile.toPath(), dest.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else if (!maMon.isEmpty()) {
            Mon old = MonDAO.findByID(maMon);
            if (old != null && old.getHinhAnh() != null)
                tenAnh = old.getHinhAnh();
        }

        Mon mon = new Mon();
        mon.setMaMon(maMon.isEmpty() ? generateID(MonDAO.getLatestMaMon(), "MN") : maMon);
        mon.setTenMon(tenMon);
        mon.setMoTa(moTa);
        mon.setGiaGoc(giaGoc);
        mon.setSoLuong(soLuong);
        mon.setLoaiMon(loaiMon);
        mon.setHinhAnh(tenAnh);

        boolean success;
        if (btnXacNhan.getText().equals("Thêm mới")) {
            success = MonDAO.insert(mon);
            if (success) {
                System.out.println("Thêm món mới thành công!");
                // hiển thị ngay món mới trong FlowPane
                flowMonAn.getChildren().add(taoCardMon(mon, selectedFile)); // dùng selectedFile để load ảnh
            } else {
                System.out.println("Thêm món thất bại!");
            }
        } else {
            success = MonDAO.update(mon);
            if (success) {
                System.out.println("Cập nhật món thành công!");
                loadDanhSachMon(); // load lại danh sách để cập nhật
            } else {
                System.out.println("Cập nhật thất bại!");
            }
        }

        resetFields();
        btnXacNhan.setText("Thêm mới");
        selectedFile = null;
    }


    private String generateID(String latestId, String prefix) {
        if (latestId == null || latestId.isEmpty()) {
            return prefix + "0001";
        }
        try {
            int num = Integer.parseInt(latestId.substring(prefix.length())); // Lấy phần số
            num += 1;
            return prefix + String.format("%04d", num); // 4 chữ số, ví dụ "0006"
        } catch (NumberFormatException e) {
            e.printStackTrace();
            // fallback nếu dữ liệu trong DB bị sai
            return prefix + "0001";
        }
    }


    // ======== ĐỊNH DẠNG TIỀN ==========
    private double parseCurrency(String text) {
        if (text == null || text.isBlank()) return 0;
        String clean = text.replaceAll("[^\\d]", "");
        if (clean.isEmpty()) return 0;
        return Double.parseDouble(clean);
    }

    private String formatCurrency(double amount) {
        Locale localeVN = new Locale("vi", "VN");
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(localeVN);
        DecimalFormat df = new DecimalFormat("#,###", symbols);
        return df.format(amount) + " đ";
    }
}
