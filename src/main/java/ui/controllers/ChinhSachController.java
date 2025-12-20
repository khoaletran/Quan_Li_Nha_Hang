package ui.controllers;

import dao.*;
import entity.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.*;
import ui.AlertCus;

import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class ChinhSachController {
    @FXML
    private TextField txtBanDatTruoc;
    @FXML
    private TextField txtBanDoi;
    @FXML
    private Button btnXacNhanBanDatTruoc;
    @FXML
    private Button btnXacNhanBanDoi;
    DecimalFormatSymbols symbols =
            new DecimalFormatSymbols(new Locale("vi", "VN"));
    DecimalFormat df = new DecimalFormat("#,###", symbols);
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
            AlertCus.show("Thông báo", ok ? "Đã thêm thời gian đợi bàn thành công!" : "Thêm thất bại!");
    

        } catch (NumberFormatException ex) {
            AlertCus.show("Thông báo", "Vui lòng nhập số hợp lệ!");
    
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
                        : String.valueOf((long)coc.getSoTienCoc()));
            });
        }
    }

    @FXML
    private void xacNhan() {
        // Lấy giá trị từ form
        String tenKhuVuc = cbKhuVuc.getValue();
        String tenLoaiBan = cbLoaiBan.getValue();

        if (tenKhuVuc == null || tenLoaiBan == null) {
            AlertCus.show("Thông báo", "Vui lòng chọn khu vực và loại bàn!");
            return;
        }

        boolean loaiCoc = rbPhanTram.isSelected();
        double giaTri;

        try {
            String giaTriStr= txtGiaTriCoc.getText().replace(".", "");
            giaTri = Double.parseDouble(giaTriStr);
            if (giaTri < 0) {
                AlertCus.show("Thông báo", "Giá trị cọc không được âm!");
                return;
            }
            if (loaiCoc && giaTri > 100) {
                AlertCus.show("Thông báo", "Phần trăm cọc không thể lớn hơn 100!");
                return;
            }
        } catch (NumberFormatException e) {
            AlertCus.show("Thông báo", "Giá trị cọc không hợp lệ!");
            return;
        }

        // Lấy object KhuVuc và LoaiBan từ tên
        KhuVuc kv = khuVucDAO.getByName(tenKhuVuc);
        LoaiBan lb = loaiBanDAO.getByName(tenLoaiBan);

        if (kv == null || lb == null) {
            AlertCus.show("Thông báo", "Khu vực hoặc loại bàn không tồn tại!");
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
                AlertCus.show("Thông báo", "Cập nhật cọc thất bại!");
                return;
            }
            else{
                AlertCus.show("Thông báo", "Cập nhật cọc thành công!");
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
            Coc existed = cocDAO.getByKhuVucVaLoaiBan(kv.getMaKhuVuc(), lb.getMaLoaiBan());
            if (existed != null) {
                AlertCus.show("Thông báo", 
                    "Cọc cho Khu vực \"" + tenKhuVuc + "\" và Loại bàn \"" 
                    + tenLoaiBan + "\" đã tồn tại!");
                return;
            }
            ok = cocDAO.insert(newCoc);
            if (!ok) {
                AlertCus.show("Thông báo", "Thêm mới cọc thất bại!");
                return;
            }
            else{
                AlertCus.show("Thông báo", "Thêm mới cọc thành công!");
            }
        }

        // Reload danh sách và reset form
        loadDanhSachCoc();
        xoaTrang();
    }
    @FXML
    private void xoaCoc() {
        if (cocDangChon == null) {
            AlertCus.show("Thông báo", "Không có cọc nào để xóa!");
            return;
        }

        // ----- Hộp thoại xác nhận -----
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận");
        alert.setHeaderText("Bạn có chắc muốn xóa cọc này?");
        alert.setContentText("Hành động này không thể hoàn tác.");

        ButtonType yesBtn = new ButtonType("Xóa", ButtonBar.ButtonData.OK_DONE);
        ButtonType noBtn = new ButtonType("Hủy", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(yesBtn, noBtn);

        // Hiển thị & chờ người dùng chọn
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isEmpty() || result.get() == noBtn) {
            return; // Người dùng bấm Hủy
        }

        // ----- Thực hiện xóa -----
        boolean ok = cocDAO.delete(cocDangChon.getMaCoc());

        if (ok) {
            AlertCus.show("Thông báo", "Xóa thành công!");
            loadDanhSachCoc();
            xoaTrang();
        } else {
            AlertCus.show("Thông báo", "Xóa thất bại!");
        }
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

        stack.getChildren().addAll(imgView);
        vbox.getChildren().addAll(stack, new Label(mon.getTenMon()));

        // 🔹 Thêm sự kiện click
        vbox.setCursor(javafx.scene.Cursor.HAND);
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
        txtGiaGoc.setText(String.valueOf((long) mon.getGiaGoc()));

        // Hiển thị phần trăm lời hiện tại
        int phanTram = mon.getPhanTramGiaBanHienTai();
        txtTangPhanTram.setText(String.valueOf(phanTram));

        // Hiển thị giá bán thực tế
        txtGiaBan.setText(String.valueOf((long) mon.getGiaBan()));


        // Giá trị lời = giá bán - giá gốc
        double loi = mon.getGiaBan() - mon.getGiaGoc();
        txtGiaTriLoi.setText(String.valueOf((long) loi));
    }

    private void search() {
        String keyword = txtSearch.getText().trim().toLowerCase(); // lấy từ khóa, loại khoảng trắng, chuyển thành thường
        foodList.getChildren().clear();

        for (Mon mon : MonDAO.getAll()) {
            boolean matchName = mon.getTenMon().toLowerCase().contains(keyword);
            boolean matchMa = mon.getMaMon().toLowerCase().contains(keyword);
            if (matchMa || matchName) {
                VBox card = createFoodCard(mon);
                foodList.getChildren().add(card);
            }
        }
    }

    private void setupLoaiMonEvent() {
        cbLoaiMon.setOnAction(e -> {
            selectedMon = null;
            
            String selectedLoai = cbLoaiMon.getSelectionModel().getSelectedItem();
            if (selectedLoai == null) {
                txtTangPhanTram.clear();
                loadFoodList();   // DÙNG HÀM SẴN CÓ
                return;
            }
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
        AlertCus.show("Thông báo", "Chưa nhập phần trăm lời!");
        return;
    }

    int phanTram;
    try {
        phanTram = Integer.parseInt(phanTramText);
        if (phanTram < 0) {
            AlertCus.show("Thông báo", "Phần trăm lời phải >= 0");
            return;
        }
    } catch (NumberFormatException e) {
        AlertCus.show("Thông báo", "Phần trăm lời không hợp lệ");
        return;
    }

    // =========================================================
    // 1) CẬP NHẬT CHO MÓN
    // =========================================================
    if (selectedMon != null) {

        // --- CHẶN CẬP NHẬT TRONG NGÀY ---
        if (PhanTramGiaBanDAO.existsTodayForMon(selectedMon.getMaMon())) {
            AlertCus.show("Thông báo", "Hôm nay đã cập nhật phần trăm lời cho món này rồi!");
            resetFields();
            return;
        }

        PhanTramGiaBan latestPG = PhanTramGiaBanDAO.getLatest();
        String maPGFinal = generateID(latestPG != null ? latestPG.getMaPTGB() : null, "PG");

        PhanTramGiaBan pt = new PhanTramGiaBan();
        pt.setMaPTGB(maPGFinal);
        pt.setMon(selectedMon);
        pt.setLoaiMon(selectedMon.getLoaiMon());
        pt.setPhanTramLoi(phanTram);
        pt.setNgayApDung(LocalDateTime.now());

        boolean ok = PhanTramGiaBanDAO.insert(pt);

        if (ok) {
            AlertCus.show("Thông báo", 
                "Cập nhật % lời cho món " + selectedMon.getTenMon() + " thành công!");

            // UPDATE CACHE ĐÚNG LOGIC
            Mon.clearCachePTMon(selectedMon.getMaMon());
            Mon.updateCachePTMon(selectedMon.getMaMon(), phanTram);
        } else {
            AlertCus.show("Thông báo", "Cập nhật thất bại!");
        }

        resetFields();
        return;
    }

    // =========================================================
    // 2) CẬP NHẬT CHO LOẠI MÓN
    // =========================================================
    String tenLoai = cbLoaiMon.getSelectionModel().getSelectedItem();
    if (tenLoai == null || tenLoai.isEmpty()) {
        AlertCus.show("Thông báo", "Chưa chọn món hoặc loại món!");
        return;
    }

    String maLoaiMon = LoaiMonDAO.getMaLoaiMonByTen(tenLoai);
    if (maLoaiMon == null) {
        AlertCus.show("Thông báo", "Không tìm thấy mã loại món");
        return;
    }

    // --- CHẶN CẬP NHẬT TRONG NGÀY ---
    if (PhanTramGiaBanDAO.existsTodayForLoaiMon(maLoaiMon)) {
        AlertCus.show("Thông báo", "Hôm nay đã cập nhật phần trăm lời cho loại món này rồi!");
        resetFields();
        return;
    }

    PhanTramGiaBan latestPG = PhanTramGiaBanDAO.getLatest();
    String maPGFinal = generateID(latestPG != null ? latestPG.getMaPTGB() : null, "PG");

    PhanTramGiaBan pt = new PhanTramGiaBan();
    pt.setMaPTGB(maPGFinal);
    pt.setLoaiMon(new LoaiMon(maLoaiMon));
    pt.setMon(null);
    pt.setPhanTramLoi(phanTram);
    pt.setNgayApDung(LocalDateTime.now());

    boolean ok = PhanTramGiaBanDAO.insert(pt);

    if (ok) {
        AlertCus.show("Thông báo", 
            "Cập nhật % lời cho loại món " + tenLoai + " thành công!");

    

    } else {
        AlertCus.show("Thông báo", "Cập nhật thất bại!");
    }

    resetFields();
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
        System.out.println("Initializing ChinhSachController");
        // ToggleGroup
        groupCoc = new ToggleGroup();
        rbPhanTram.setToggleGroup(groupCoc);
        rbTien.setToggleGroup(groupCoc);
        txtGiaGoc.setMouseTransparent(true);
        txtGiaTriLoi.setMouseTransparent(true);
        txtGiaBan.setMouseTransparent(true);
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

        loadFoodList();
        setupLoaiMonEvent();
        
        txtTangPhanTram.textProperty().addListener((obs, oldText, newText) -> updateGiaTriTuongUng());

        addCurrencyFormat(txtGiaGoc, false);
        addCurrencyFormat(txtGiaBan, false);
        addCurrencyFormat(txtGiaTriLoi, false);
        addCurrencyFormat(txtGiaTriCoc, true); 


        txtSearch.textProperty().addListener((obs, oldText, newText)-> search());

        Platform.runLater(()-> addShortcuts(txtSearch.getScene()));
        Tooltip tipFind = new Tooltip("Tìm kiếm Món ăn (Ctrl + F)");
        tipFind.getStyleClass().add("tooltip");
        Tooltip.install(txtSearch, tipFind);

    }
    private void addShortcuts(Scene scene){
        KeyCombination ctrlF = new KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN);
        scene.getAccelerators().put(ctrlF, () -> {
            txtSearch.requestFocus();
            txtSearch.selectAll();
        });
    }
    private void updateGiaTriTuongUng(){
        String tangPhanTram = txtTangPhanTram.getText().trim();
        if(selectedMon==null|tangPhanTram==null|| tangPhanTram.isEmpty()) return;
        double phanTramTang = Double.parseDouble(tangPhanTram);
        double giaGoc = selectedMon.getGiaGoc();
        double giaLoi = giaGoc * phanTramTang/100;
        double giaBan = giaGoc + giaLoi;
        txtGiaTriLoi.setText(String.valueOf((long) giaLoi));
        txtGiaBan.setText(String.valueOf((long) giaBan));
    } 
    private void addCurrencyFormat(TextField tf, boolean skipIfPhanTram) {
    
        tf.textProperty().addListener((obs, oldText, newText) -> {
            if ((skipIfPhanTram && rbPhanTram.isSelected()) || newText == null || newText.isEmpty()) return;

            String numeric = newText.replaceAll("\\.", "");
            if (numeric.isEmpty()) {
                tf.setText("");
                return;
            }

            try {
                String formatted = df.format(Long.parseLong(numeric));
                if (!formatted.equals(newText)) {
                    tf.setText(formatted);
                    tf.positionCaret(formatted.length());
                }
            } catch (NumberFormatException e) {
                tf.setText(oldText);
            }
        });
    }

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
}
