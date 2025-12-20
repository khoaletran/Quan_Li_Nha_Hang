package ui.controllers;

import dao.LoaiMonDAO;
import dao.MonDAO;
import entity.LoaiMon;
import entity.Mon;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import ui.AlertCus;


import java.io.File;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
    // @FXML private Button btnXoa;
    @FXML
    private Button btnAdd;
    @FXML
    private TextField searchField;
    private MainController_QL mainController;
    private boolean isProgrammaticChange = false;
    private static final Map<String, Image> imageCache = new HashMap<>();
    private final Image fallbackImage =
        new Image(getClass().getResourceAsStream("/IMG/food/restaurant.png"),
                180, 180, true, true);

    private List<Mon> dsMon = new ArrayList<>();
    private List<LoaiMon> dsLoaiMon = new ArrayList<>();
    private Mon monDuocChonTuDashboard;
    private File selectedFile;
    DecimalFormatSymbols symbols =
        new DecimalFormatSymbols(new Locale("vi", "VN"));
    DecimalFormat df = new DecimalFormat("#,###", symbols);
    @FXML
    public void initialize() {
        dsMon = MonDAO.getAll();
        dsLoaiMon = LoaiMonDAO.getAll();
        loadComboDanhMuc();
        loadDanhSachMon(); // hiển thị tất cả
        cboLoaiMonFilter.setOnAction(e -> locMonTheoDanhMuc());
        // TextField tìm kiếm realtime
        searchField.textProperty().addListener((obs, oldText, newText) -> filterMon());
        // btnXoa.setDisable(true);
        txtGiaGoc.textProperty().addListener((obs, oldText, newText) -> {
            if (isProgrammaticChange) return;
            if (newText == null || newText.isEmpty()) return;

            String numeric = newText.replaceAll("\\.", "");
            if (numeric.isEmpty()) {
                isProgrammaticChange = true;
                txtGiaGoc.setText("");
                isProgrammaticChange = false;
                return;
            }

            try {
                String formatted = df.format(Long.parseLong(numeric));
                if (!formatted.equals(newText)) {
                    isProgrammaticChange = true;
                    txtGiaGoc.setText(formatted);
                    txtGiaGoc.positionCaret(formatted.length());
                    isProgrammaticChange = false;
                }
            } catch (NumberFormatException e) {
                isProgrammaticChange = true;
                txtGiaGoc.setText(oldText);
                isProgrammaticChange = false;
            }
        });


        // ===== THÊM PHÍM TẮT =====
        addShortcuts();
        Tooltip tipFind = new Tooltip("Tìm kiếm món ăn (Ctrl + F)");
        tipFind.getStyleClass().add("tooltip");
        Tooltip.install(searchField, tipFind);
        Tooltip tipNew = new Tooltip("Thêm món ăn mới (Ctrl + N)");
        tipNew.getStyleClass().add("tooltip");
        Tooltip.install(btnAdd, tipNew);

        Platform.runLater(() -> {
            if (monDuocChonTuDashboard != null) {
                loadChiTietMon(monDuocChonTuDashboard);
            }
        });

    }


//    private void addShortcuts(Scene scene) {
//        KeyCombination ctrlF = new KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN);
//        KeyCombination ctrlN = new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN);
//        scene.getAccelerators().put(ctrlF, () -> {
//            searchField.requestFocus();
//            searchField.selectAll();  // tự bôi đen text để nhập mới
//        });
//        scene.getAccelerators().put(ctrlN, () -> {
//            addMon();
//        });
//    }

    private void addShortcuts() {
        searchField.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene == null) return;

            KeyCombination ctrlF =
                    new KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN);
            KeyCombination ctrlN =
                    new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN);

            newScene.getAccelerators().put(ctrlF, () -> {
                searchField.requestFocus();
                searchField.selectAll();
            });

            newScene.getAccelerators().put(ctrlN, this::addMon);
        });
    }


    public void setMainController(MainController_QL mainController) {
        this.mainController = mainController;
    }

    public void setSelectedMon(Mon mon) {
        this.monDuocChonTuDashboard = mon;
    }

private Image getMenuImageCached(String fileName) {
    if (fileName == null || fileName.isBlank()) {
        return fallbackImage;
    }

    String path = "/IMG/food/" + fileName.replaceFirst("^/", "");

    return imageCache.computeIfAbsent(path, p -> {
        try {
            return new Image(
                    getClass().getResourceAsStream(p),
                    180, 180, true, true
            );
        } catch (Exception e) {
            return fallbackImage;
        }
    });
}


    // Hàm lọc món kết hợp tên + loại
    private void filterMon() {
        String keyword = searchField.getText().toLowerCase().trim();
        String selectedLoai = cboLoaiMonFilter.getSelectionModel().getSelectedItem();

        flowMonAn.getChildren().clear();

        for (Mon mon : dsMon) {
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
        for (LoaiMon lm : dsLoaiMon) {
            cboLoaiMonFilter.getItems().add(lm.getTenLoaiMon());
            cboLoaiMon.getItems().add(lm.getTenLoaiMon());
        }

        cboLoaiMonFilter.getSelectionModel().selectFirst();
    }

    private void loadDanhSachMon() {
        dsMon = MonDAO.getAll();
        flowMonAn.getChildren().clear();
        // preload ảnh
        for (Mon mon : dsMon) {
            getMenuImageCached(mon.getHinhAnh());
        }
        for (Mon mon : dsMon) {
            flowMonAn.getChildren().add(taoCardMon(mon, null));
        }
    }

    private void locMonTheoDanhMuc() {
        String selectedLoai = cboLoaiMonFilter.getSelectionModel().getSelectedItem();
        flowMonAn.getChildren().clear();
        searchField.setText("");

        if (selectedLoai == null || selectedLoai.equals("Tất cả")) {
            loadDanhSachMon();
            return;
        }

        for (Mon mon : dsMon) {
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
        card.setUserData(mon);
        card.setPrefSize(250, 250);
        card.setMaxSize(250, 250);
        card.setMinSize(250, 250);

        // ===== 2. Khung hình cố định =====
        StackPane imagePane = new StackPane();
        imagePane.setPrefSize(180, 180);

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
        imageView.setImage(getMenuImageCached(mon.getHinhAnh()));


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
        card.setOnMouseClicked(e -> {
            Platform.runLater(() -> loadChiTietMon(mon));
        });

        return card;
    }


    private void loadChiTietMon(Mon mon) {
        isProgrammaticChange = true;

        lblMaMon.setText(mon.getMaMon());
        txtTenMon.setText(mon.getTenMon());
        txtMoTa.setText(mon.getMoTa());
        txtGiaGoc.setText(df.format((long) mon.getGiaGoc()));
        txtSoLuong.setText(String.valueOf(mon.getSoLuong()));

        isProgrammaticChange = false;

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

        String newID = generateID(MonDAO.getLatestMaMon(), "MM");
        lblMaMon.setText(newID);
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
        // btnXoa.setDisable(true);

    }

    @FXML
    private void xacNhan() {
        String maMon = lblMaMon.getText().trim();
        String tenMon = txtTenMon.getText().trim();
        if (tenMon.isEmpty()) {
            AlertCus.show("Thông báo", "Tên món không được để trống!");
            txtTenMon.requestFocus();
            return;
        }

        String moTa = txtMoTa.getText().trim();
        String giaStr = txtGiaGoc.getText().trim();
        if (giaStr.isEmpty()) {
            AlertCus.show("Thông báo", "Giá gốc không được để trống!");
            txtGiaGoc.requestFocus();
            return;
        }
        giaStr = giaStr.replace(".", "");
        String soLuongStr = txtSoLuong.getText().trim();
        if (cboLoaiMon.getSelectionModel().getSelectedItem() == null) {
            AlertCus.show("Thông báo", "Vui lòng chọn loại món!");
            cboLoaiMon.requestFocus();
            return;
        }
        String maLoai = "";
        if (cboLoaiMon.getSelectionModel().getSelectedItem() != null) {
            maLoai = LoaiMonDAO.getMaLoaiMonByTen(cboLoaiMon.getSelectionModel().getSelectedItem());
        }
        LoaiMon loaiMon = new LoaiMon(maLoai);

        double giaGoc;
        int soLuong;
        try {
            giaGoc = Double.parseDouble(giaStr);
            if (giaGoc <= 0) {
                AlertCus.show("Thông báo", "Giá gốc phải lớn hơn 0!");
                txtGiaGoc.requestFocus();
                return;
            }
            soLuong = Integer.parseInt(soLuongStr);
            if (soLuong < 0) {
                AlertCus.show("Thông báo", "Số lượng không được âm!");
                txtSoLuong.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            AlertCus.show("Thông báo", "Giá hoặc số lượng không hợp lệ!");
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
        mon.setMaMon(maMon.isEmpty() ? generateID(MonDAO.getLatestMaMon(), "MM") : maMon);
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
                AlertCus.show("Thông báo", "Thêm món mới thành công!");
                // hiển thị ngay món mới trong FlowPane
                flowMonAn.getChildren().add(taoCardMon(mon, selectedFile)); // dùng selectedFile để load ảnh
                loadDanhSachMon();
            } else {
                AlertCus.show("Thông báo", "Thêm món thất bại!");
            }
        } else {
            success = MonDAO.update(mon);
            if (success) {
                AlertCus.show("Thông báo", "Cập nhật món thành công!");
                loadDanhSachMon(); // load lại danh sách để cập nhật
            } else {
                AlertCus.show("Thông báo", "Cập nhật thất bại!");
            }
        }

        resetFields();
        btnXacNhan.setText("Thêm mới");
        selectedFile = null;
    }

    // @FXML
    // private void xoaMon() {
    //     String maMon = lblMaMon.getText().trim();
    //     if (maMon.isEmpty()) {
    //         AlertCus.show("Thông báo", "Không có món nào để xóa!");
    //         return;
    //     }
    //     String tenMon = txtTenMon.getText();
    //     // === Hộp thoại xác nhận ===

    //     if(XacNhanXoa.hienHopThoaiXacNhan("Xác nhận xóa","Bạn có chắc chắn xóa " + tenMon)){
    //         boolean success = MonDAO.delete(maMon);

    //         if (success) {
    //             AlertCus.show("Thông báo", "Xóa món " + tenMon + " thành công!");
    //             dsMon = MonDAO.getAll();
    //             loadDanhSachMon();
    //         } else {
    //             AlertCus.show("Thông báo", "Xóa món thất bại!");
    //         }
    //     }


    //     resetFields();
    //     btnXacNhan.setText("Thêm mới");
    //     btnXoa.setDisable(true);
    // }

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




    private String formatCurrency(double amount) {
        Locale localeVN = new Locale("vi", "VN");
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(localeVN);
        DecimalFormat df = new DecimalFormat("#,###", symbols);
        return df.format(amount) + " đ";
    }


    public void setSearchKeyword(String keyword) {
        if (keyword == null || keyword.isBlank()) return;

        searchField.setText(keyword);      // điền tên món
        filterMon();                        // lọc danh sách theo keyword
    }

    public void selectMonByMaMon(String maMon) {
        Platform.runLater(() -> {
            if (maMon == null || maMon.isBlank()) return;

            for (javafx.scene.Node node : flowMonAn.getChildren()) {
                if (node instanceof VBox card) {
                    Object data = card.getUserData();
                    if (data instanceof Mon mon && maMon.equals(mon.getMaMon())) {
                        loadChiTietMon(mon);
//                        highlightCard(card);
                        card.requestFocus();
                        return;
                    }
                }
            }
        });
    }

}
