package ui.controllers;

import dao.*;
import entity.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;

public class ChinhSachController {
    /**
     * Sinh mã mới dạng PREFIX + 4 chữ số
     *
     * @param latestId mã mới nhất hiện có, ví dụ "TD0005", hoặc null nếu chưa có
     * @param prefix   tiền tố, ví dụ "TD", "C"
     * @return mã mới, ví dụ "TD0006"
     */
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

    @FXML
    private TextField txtBanDatTruoc;
    @FXML
    private TextField txtBanDoi;
    @FXML
    private Button btnXacNhanBanDatTruoc;
    @FXML
    private Button btnXacNhanBanDoi;

    private final ThoiGianDoiBanDAO tgdbDAO = new ThoiGianDoiBanDAO();


    // ================= Controller Thời gian đặt bàn===================
    private void loadThoiGianDoiBan() {
        // Lấy bản ghi mới nhất của "Bàn đặt trước"
        ThoiGianDoiBan banDatTruoc = tgdbDAO.getLatestByLoai(true);
        if (banDatTruoc != null) {
            txtBanDatTruoc.setText(String.valueOf(banDatTruoc.getThoiGian()));
        }

        // Lấy bản ghi mới nhất của "Bàn đợi"
        ThoiGianDoiBan banDoi = tgdbDAO.getLatestByLoai(false);
        if (banDoi != null) {
            txtBanDoi.setText(String.valueOf(banDoi.getThoiGian()));
        }
    }

    // ========== SỰ KIỆN ONACTION ==========
    @FXML
    private void xacNhanBanDatTruoc() {
        themMoiThoiGian(true);
    }

    @FXML
    private void xacNhanBanDoi() {
        themMoiThoiGian(false);
    }

    private void themMoiThoiGian(boolean laBanDatTruoc) {
        try {
            TextField targetField = laBanDatTruoc ? txtBanDatTruoc : txtBanDoi;
            int thoiGian = Integer.parseInt(targetField.getText().trim());

            // Lấy bản ghi mới nhất
            ThoiGianDoiBan thoiGianDoiBan = tgdbDAO.getLatest();
            String maTGDBFinal = generateID(
                    thoiGianDoiBan != null ? thoiGianDoiBan.getMaTGDB() : null,
                    "TD"
            );


            // Tạo đối tượng entity
            ThoiGianDoiBan tgdb = new ThoiGianDoiBan();
            tgdb.setMaTGDB(maTGDBFinal);
            tgdb.setLoaiDatBan(laBanDatTruoc);
            tgdb.setThoiGian(thoiGian);

            // Thêm mới vào DB
            boolean ok = tgdbDAO.insert(tgdb);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Thông báo");
            alert.setHeaderText(null);
            alert.setContentText(ok ? "Đã thêm thời gian đợi bàn thành công!" : "Thêm thất bại!");
            alert.showAndWait();

        } catch (NumberFormatException ex) {
            showError("Vui lòng nhập số hợp lệ!");
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());
        }
    }
    // ================= Controller Thời gian đặt bàn===================


    //

    @FXML
    private ComboBox<String> cbKhuVuc;
    @FXML
    private ComboBox<String> cbLoaiBan;
    @FXML
    private RadioButton rbPhanTram;
    @FXML
    private RadioButton rbTien;
    @FXML
    private TextField txtGiaTriCoc;
    @FXML
    private Button btnXacNhanCoc;
    @FXML
    private VBox vboxCocList;

    private ToggleGroup groupCoc;
    private final CocDAO cocDAO = new CocDAO();
    private final KhuVucDAO khuVucDAO = new KhuVucDAO();
    private final LoaiBanDAO loaiBanDAO = new LoaiBanDAO();

    private Coc cocDangChon = null; // lưu cọc đang chọn để update

    private void loadDanhSachCoc() {
        vboxCocList.getChildren().clear();
        List<Coc> list = cocDAO.getAll();

        for (Coc coc : list) {
            HBox hbox = new HBox(10);
            hbox.setAlignment(Pos.CENTER_LEFT);
            hbox.getStyleClass().add("deposit-item-box");

            Label lblInfo = new Label("Loại Bàn: " + coc.getLoaiBan().getTenLoaiBan()
                    + "    Khu: " + coc.getKhuVuc().getTenKhuVuc());
            lblInfo.getStyleClass().add("deposit-info");

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            Label lblValue = new Label(coc.isLoaiCoc() ? coc.getPhanTramCoc() + "%" : formatTien(coc.getSoTienCoc()));
            lblValue.getStyleClass().add("deposit-value");
            lblValue.setPrefWidth(80);
            lblValue.setMinWidth(80);
            lblValue.setMaxWidth(80);
            lblValue.setAlignment(Pos.CENTER_RIGHT);

            hbox.getChildren().addAll(lblInfo, spacer, lblValue);
            vboxCocList.getChildren().add(hbox);

            // Event click chọn để điền GridPane
            hbox.setOnMouseClicked(e -> {
                cocDangChon = coc;
                cbKhuVuc.setValue(coc.getKhuVuc().getTenKhuVuc());
                cbLoaiBan.setValue(coc.getLoaiBan().getTenLoaiBan());
                if (coc.isLoaiCoc()) rbPhanTram.setSelected(true);
                else rbTien.setSelected(true);

                txtGiaTriCoc.setText(coc.isLoaiCoc() ? String.valueOf(coc.getPhanTramCoc())
                        : String.valueOf(coc.getSoTienCoc()));
            });
        }
    }

    @FXML
    private void xacNhan() {
        // Lấy giá trị từ form
        String tenKhuVuc = cbKhuVuc.getValue();
        String tenLoaiBan = cbLoaiBan.getValue();

        if (tenKhuVuc == null || tenLoaiBan == null) {
            showError("Vui lòng chọn khu vực và loại bàn!");
            return;
        }

        boolean loaiCoc = rbPhanTram.isSelected();
        double giaTri;

        try {
            giaTri = Double.parseDouble(txtGiaTriCoc.getText());
            if (giaTri < 0) {
                showError("Giá trị cọc không được âm!");
                return;
            }
            if (loaiCoc && giaTri > 100) {
                showError("Phần trăm cọc không thể lớn hơn 100!");
                return;
            }
        } catch (NumberFormatException e) {
            showError("Giá trị cọc không hợp lệ!");
            return;
        }

        // Lấy object KhuVuc và LoaiBan từ tên
        KhuVuc kv = khuVucDAO.getByName(tenKhuVuc);
        LoaiBan lb = loaiBanDAO.getByName(tenLoaiBan);

        if (kv == null || lb == null) {
            showError("Khu vực hoặc loại bàn không tồn tại!");
            return;
        }

        boolean ok;
        if (cocDangChon != null) {
            // --- Cập nhật cọc ---
            cocDangChon.setKhuVuc(kv);
            cocDangChon.setLoaiBan(lb);
            cocDangChon.setLoaiCoc(loaiCoc);
            if (loaiCoc) {
                cocDangChon.setPhanTramCoc((int) giaTri);
                cocDangChon.setSoTienCoc(0);
            } else {
                cocDangChon.setSoTienCoc(giaTri);
                cocDangChon.setPhanTramCoc(0);
            }

            ok = cocDAO.update(cocDangChon);
            if (!ok) {
                showError("Cập nhật cọc thất bại!");
                return;
            }
        } else {
            // --- Thêm mới cọc ---
            Coc newCoc = new Coc();

            // Sinh mã mới dựa vào getLatest() + prefix "C"
            Coc latest = cocDAO.getLatest();
            newCoc.setMaCoc(generateID(latest != null ? latest.getMaCoc() : null, "CO"));

            newCoc.setKhuVuc(kv);
            newCoc.setLoaiBan(lb);
            newCoc.setLoaiCoc(loaiCoc);
            if (loaiCoc) {
                newCoc.setPhanTramCoc((int) giaTri);
                newCoc.setSoTienCoc(0);
            } else {
                newCoc.setSoTienCoc(giaTri);
                newCoc.setPhanTramCoc(0);
            }

            ok = cocDAO.insert(newCoc);
            if (!ok) {
                showError("Thêm mới cọc thất bại!");
                return;
            }
        }

        // Reload danh sách và reset form
        loadDanhSachCoc();
        xoaTrang();
    }


    @FXML
    private void xoaTrang() {
        cocDangChon = null;         // Xóa cọc đang chọn
        cbKhuVuc.setValue(null);    // Reset ComboBox Khu vực
        cbLoaiBan.setValue(null);   // Reset ComboBox Loại bàn
        rbPhanTram.setSelected(true); // Chọn mặc định Phần Trăm
        txtGiaTriCoc.clear();       // Xóa TextField giá trị
    }


    // Hàm tiện ích format số tiền
    private String formatTien(double tien) {
        return String.format("%,.0fđ", tien);
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Lỗi nhập liệu");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    //    sự kiện 3
    @FXML
    private FlowPane foodList;
    @FXML
    private ComboBox<String> cbLoaiMon; // Loại món
    @FXML
    private TextField txtTenMon;        // Tên món
    @FXML
    private Button btnXacNhan;
    @FXML
    private TextField txtSearch; // fx:id cho TextField tìm kiếm
    @FXML
    private TextField txtGiaGoc;
    @FXML
    private TextField txtGiaTriLoi;
    @FXML
    private TextField txtGiaBan;
    @FXML
    private TextField txtTangPhanTram; // tăng %
    @FXML
    private TextField txtTen;

    private final LoaiMonDAO loaiMon = new LoaiMonDAO();

    private void loadFoodList() {
        foodList.getChildren().clear();
        String maLoaiMon = LoaiMonDAO.getMaLoaiMonByTen("Món khai vị");
        System.out.println(maLoaiMon);
        for (Mon mon : MonDAO.getAll()) {
            VBox card = createFoodCard(mon);
            foodList.getChildren().add(card);
        }
    }


    private VBox createFoodCard(Mon mon) {
        VBox vbox = new VBox(5);
        vbox.setAlignment(Pos.CENTER);
        vbox.getStyleClass().add("food-card");

        StackPane stack = new StackPane();

        // Kiểm tra ảnh món, nếu không có thì dùng ảnh mặc định
        String imagePath = "/IMG/food/" + (mon.getHinhAnh() != null && !mon.getHinhAnh().isEmpty() ? mon.getHinhAnh() : "avatar.png");
        InputStream is = getClass().getResourceAsStream(imagePath);
        if (is == null) {
            System.out.println("Không tìm thấy ảnh: " + imagePath + ", dùng ảnh mặc định.");
            is = getClass().getResourceAsStream("/IMG/food/restaurant.png");
        }

        Image img = new Image(is, 40, 40, true, true);
        ImageView imgView = new ImageView(img);
        imgView.setFitWidth(40);
        imgView.setFitHeight(40);
        imgView.getStyleClass().add("food-image");

        Button addBtn = new Button("+");
        addBtn.getStyleClass().add("add-icon");
        StackPane.setAlignment(addBtn, Pos.TOP_RIGHT);

        stack.getChildren().addAll(imgView, addBtn);
        vbox.getChildren().addAll(stack, new Label(mon.getTenMon()));
        // 🔹 Thêm sự kiện click
        vbox.setOnMouseClicked(e -> showMonDetails(mon));
        return vbox;
    }
    private Mon selectedMon = null;

    private void showMonDetails(Mon mon) {
        selectedMon = mon;
        // Tạm lưu handler hiện tại
        EventHandler<ActionEvent> handler = cbLoaiMon.getOnAction();
        cbLoaiMon.setOnAction(null); // tắt handler tạm thời

        // Set ComboBox loại món tương ứng
        if (mon.getLoaiMon() != null) {
            cbLoaiMon.getSelectionModel().select(mon.getLoaiMon().getTenLoaiMon());
        }

        // Bật lại handler
        cbLoaiMon.setOnAction(handler);
        // Hiển thị tên
        txtTen.setText(mon.getTenMon());

        // Hiển thị giá gốc
        txtGiaGoc.setText(String.valueOf(mon.getGiaGoc()));

        // Hiển thị phần trăm lời hiện tại
        int phanTram = mon.getPhanTramGiaBanHienTai();
        txtTangPhanTram.setText(String.valueOf(phanTram));

        // Hiển thị giá bán thực tế
        txtGiaBan.setText(String.format("%.0f", mon.getGiaBan()));

        // Giá trị lời = giá bán - giá gốc
        double loi = mon.getGiaBan() - mon.getGiaGoc();
        txtGiaTriLoi.setText(String.format("%.0f", loi));
    }

    @FXML
    private void search() {
        String keyword = txtSearch.getText().trim().toLowerCase(); // lấy từ khóa, loại khoảng trắng, chuyển thành thường
        foodList.getChildren().clear();

        for (Mon mon : MonDAO.getAll()) {
            if (mon.getTenMon().toLowerCase().contains(keyword)) {
                VBox card = createFoodCard(mon);
                foodList.getChildren().add(card);
            }
        }
    }

    private void setupLoaiMonEvent() {
        cbLoaiMon.setOnAction(e -> {
            selectedMon = null;
            String selectedLoai = cbLoaiMon.getSelectionModel().getSelectedItem();

            // Ẩn các field giá gốc, giá bán, giá lời
            txtGiaGoc.setVisible(false);
            txtTen.setVisible(false);
            txtGiaTriLoi.setVisible(false);
            txtGiaBan.setVisible(false);

            // TextField tăng % luôn hiển thị
            txtTangPhanTram.setVisible(true);

            // Load phần trăm lời từ DB
            if (selectedLoai != null && !selectedLoai.isEmpty()) {
                String maLoaiMon = LoaiMonDAO.getMaLoaiMonByTen(selectedLoai);
                if (maLoaiMon != null) {
                    PhanTramGiaBan ptgb = PhanTramGiaBanDAO.getLatestForLoaiMon(maLoaiMon);
                    txtTangPhanTram.setText(ptgb != null ? String.valueOf(ptgb.getPhanTramLoi()) : "");
                } else {
                    txtTangPhanTram.clear();
                }
            } else {
                txtTangPhanTram.clear();
            }

            // Lọc danh sách món theo loại
            foodList.getChildren().clear();
            for (Mon mon : MonDAO.getAll()) {
                if (mon.getLoaiMon() != null &&
                        selectedLoai.equals(mon.getLoaiMon().getTenLoaiMon())) {
                    VBox card = createFoodCard(mon);
                    foodList.getChildren().add(card);
                }
            }
        });
    }

    @FXML
    private void xacNhanPhanTramLoi() {
        String phanTramText = txtTangPhanTram.getText().trim();
        if (phanTramText.isEmpty()) {
            System.out.println("Chưa nhập phần trăm lời!");
            return;
        }

        int phanTram;
        try {
            phanTram = Integer.parseInt(phanTramText);
            if (phanTram < 0) {
                System.out.println("Phần trăm lời phải >= 0");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Phần trăm lời không hợp lệ");
            return;
        }

        // ✅ Nếu đã chọn món, cập nhật riêng cho món
        if (selectedMon != null) {
            PhanTramGiaBan pt = new PhanTramGiaBan();
            PhanTramGiaBan latestPG = PhanTramGiaBanDAO.getLatest();
            String maPGFinal = generateID(latestPG != null ? latestPG.getMaPTGB() : null, "PG");

            pt.setMaPTGB(maPGFinal);
            pt.setMon(selectedMon);
            pt.setLoaiMon(selectedMon.getLoaiMon());
            pt.setPhanTramLoi(phanTram);
            pt.setNgayApDung(LocalDate.now());

            boolean ok = PhanTramGiaBanDAO.insert(pt);
            if (ok) {
                System.out.println("Cập nhật % lời cho món " + selectedMon.getTenMon() + " thành công!");
                showMonDetails(selectedMon); // refresh hiển thị
            } else {
                System.out.println("Cập nhật thất bại!");
            }
            return;
        }

        // ✅ Nếu không chọn món, lấy loại món từ ComboBox
        String tenLoai = cbLoaiMon.getSelectionModel().getSelectedItem();
        if (tenLoai == null || tenLoai.isEmpty()) {
            System.out.println("Chưa chọn món hoặc loại món!");
            return;
        }

        String maLoaiMon = LoaiMonDAO.getMaLoaiMonByTen(tenLoai);
        if (maLoaiMon == null) {
            System.out.println("Không tìm thấy mã loại món");
            return;
        }

        PhanTramGiaBan pt = new PhanTramGiaBan();
        PhanTramGiaBan latestPG = PhanTramGiaBanDAO.getLatest();
        String maPGFinal = generateID(latestPG != null ? latestPG.getMaPTGB() : null, "PG");

        pt.setMaPTGB(maPGFinal);
        pt.setLoaiMon(new LoaiMon(maLoaiMon));
        pt.setMon(null);
        pt.setPhanTramLoi(phanTram);
        pt.setNgayApDung(LocalDate.now());

        boolean ok = PhanTramGiaBanDAO.insert(pt);
        if (ok) {
            System.out.println("Cập nhật % lời cho loại món " + tenLoai + " thành công!");
            txtTangPhanTram.setText(String.valueOf(phanTram));
        } else {
            System.out.println("Cập nhật thất bại!");
        }
    }


    @FXML
    private void resetFields() {
        // Reset ComboBox và TextField
        selectedMon = null;
        cbLoaiMon.getSelectionModel().clearSelection();
        txtSearch.clear();

        // Hiển thị tất cả field giá
        txtGiaGoc.setVisible(true);
        txtGiaTriLoi.setVisible(true);
        txtGiaBan.setVisible(true);
        txtTangPhanTram.setVisible(true); // vẫn hiển thị
        txtTen.setVisible(true);

        txtGiaGoc.clear();
        txtGiaTriLoi.clear();
        txtGiaBan.clear();
        txtTangPhanTram.clear();
        txtTen.clear();

        // Load lại tất cả món
        loadFoodList();
    }


    @FXML
    public void initialize() {
        // ToggleGroup
        groupCoc = new ToggleGroup();
        rbPhanTram.setToggleGroup(groupCoc);
        rbTien.setToggleGroup(groupCoc);

        // Load danh sách khu vực và loại bàn vào ComboBox
        cbKhuVuc.getItems().clear();
        for (KhuVuc kv : khuVucDAO.getAll()) {
            cbKhuVuc.getItems().add(kv.getTenKhuVuc());
        }

        cbLoaiBan.getItems().clear();
        for (LoaiBan lb : loaiBanDAO.getAll()) {
            cbLoaiBan.getItems().add(lb.getTenLoaiBan());
        }
        cbLoaiMon.getItems().clear();
        for (LoaiMon lm : LoaiMonDAO.getAll()) {
            cbLoaiMon.getItems().add(lm.getTenLoaiMon());
        }
        // Load danh sách cọc
        loadDanhSachCoc();
        loadThoiGianDoiBan();
        for (Mon mon : MonDAO.getAll()) {
            System.out.println(mon.toString());
        }
        loadFoodList();
        setupLoaiMonEvent();

    }


}
