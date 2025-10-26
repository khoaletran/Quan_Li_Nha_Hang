package ui.controllers;

import dao.HoaDonDAO;
import dao.LoaiMonDAO;
import dao.KhachHangDAO;
import dao.MonDAO;
import entity.Ban;
import entity.KhachHang;
import entity.LoaiMon;
import entity.Mon;
import entity.NhanVien;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.text.DecimalFormat; // ADDED
import java.text.DecimalFormatSymbols; // ADDED
import java.util.HashMap;
import java.util.List;
import java.util.Locale; // ADDED
import java.util.Map;

public class ChonMonController {

    @FXML private FlowPane flowMonAn;
    @FXML private ComboBox<LoaiMon> comboDanhMuc;
    @FXML private VBox vboxChiTietDonHang, vboxTienMat;
    @FXML private Label lbl_total, lbl_thue, lbl_total_PT, lblTienThua;
    @FXML private ToggleGroup paymentGroup;
    @FXML private RadioButton rdoTienMat, rdoChuyenKhoan;
    @FXML private Button back, btnGoiY1, btnGoiY2, btnGoiY3, btnGoiY4, btnGoiY5, btnGoiY6;
    @FXML private TextField txtTienKhachDua, sdtKhach;
    @FXML private TextField tf_ban;


    private ui.controllers.MainController_NV mainController;

    private final Map<String, HBox> chiTietMap = new HashMap<>();
    private final Map<String, Integer> soLuongMap = new HashMap<>();

    private final MonDAO monDAO = new MonDAO();
    private final LoaiMonDAO loaiMonDAO = new LoaiMonDAO();

    private Ban banHienTai = null;
    private NhanVien nhanVienHien;



    @FXML
    public void initialize() {
        loadComboDanhMuc();
        loadDanhSachMon();
        xuLyHienThiTienMat();
        rdoChuyenKhoan.setSelected(true);
        back.setOnAction(e -> quayVeDatBan());
    }

    public void setMainController(ui.controllers.MainController_NV controller) {
        this.mainController = controller;
        setNhanVien(mainController.getNhanVien());
    }

    public void setNhanVien(NhanVien nhanVien) {
        this.nhanVienHien = nhanVien;
    }


    public void setThongTinBan(Ban ban) {
        this.banHienTai = ban;
        tf_ban.setText(ban.getMaBan());
        tf_ban.setEditable(false);
        System.out.println("Đang chọn bàn: " + ban);
    }


    @FXML
    private void quayVeDatBan() {
        if (mainController != null) {
            mainController.setCenterContent("/FXML/DatBan.fxml");
        }
    }


    private void loadComboDanhMuc() {
        comboDanhMuc.getItems().clear();
        comboDanhMuc.getItems().addAll(loaiMonDAO.getAll());

        comboDanhMuc.setCellFactory(param -> new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(LoaiMon item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getTenLoaiMon());
                }
            }
        });

        comboDanhMuc.setButtonCell(new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(LoaiMon item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("Chọn loại món");
                } else {
                    setText(item.getTenLoaiMon());
                }
            }
        });
    }

    private void loadDanhSachMon() {
        flowMonAn.getChildren().clear();
        List<Mon> danhSach = monDAO.getAll();
        System.out.println(danhSach.size());

        for (Mon mon : danhSach) {
            VBox card = taoCardMon(mon);
            flowMonAn.getChildren().add(card);
        }
    }

    private VBox taoCardMon(Mon mon){
        VBox card = new VBox();
        card.getStyleClass().add("menu-card");
        card.setSpacing(8);
        card.setAlignment(javafx.geometry.Pos.CENTER);

        ImageView imageView = new ImageView();
        try {
            imageView.setImage(new Image(getClass().getResourceAsStream("/IMG/food" + mon.getHinhAnh())));
        } catch (Exception e) {
            imageView.setImage(new Image(getClass().getResourceAsStream("/IMG/food/restaurant.png")));
        }
        imageView.setFitWidth(150);
        imageView.setFitHeight(110);
        imageView.getStyleClass().add("food-image");

        Label lblTen = new Label(mon.getTenMon());
        lblTen.getStyleClass().add("menu-item-name");

        Label lblGia = new Label(formatCurrency(mon.giaBan()));
        lblGia.getStyleClass().add("menu-item-price");

        Label lblSoLuong = new Label("0");
        lblSoLuong.getStyleClass().add("qty-label");

        Button btnMinus = new Button("-");
        btnMinus.getStyleClass().add("qty-btn_minus");

        Button btnPlus = new Button("+");
        btnPlus.getStyleClass().add("qty-btn_plus");

        HBox soLuongBox = new HBox(btnMinus, lblSoLuong, btnPlus);
        soLuongBox.getStyleClass().add("quantity-controls");

        // ===== SỰ KIỆN =====
        btnPlus.setOnAction(e -> {
            int soLuong = Integer.parseInt(lblSoLuong.getText());
            soLuong++;
            lblSoLuong.setText(String.valueOf(soLuong));

            if (soLuong == 1) {
                addMonToOrder(mon);
                capNhatTongTien();
                xuLyHienThiTienMat();
            } else {
                updateMonSoLuong(mon, soLuong);
                capNhatTongTien();
                xuLyHienThiTienMat();
            }
        });

        btnMinus.setOnAction(e -> {
            int soLuong = Integer.parseInt(lblSoLuong.getText());
            if (soLuong > 0) {
                soLuong--;
                lblSoLuong.setText(String.valueOf(soLuong));

                if (soLuong == 0) {
                    removeMonFromOrder(mon);
                    capNhatTongTien();
                    xuLyHienThiTienMat();
                } else {
                    updateMonSoLuong(mon, soLuong);
                    capNhatTongTien();
                    xuLyHienThiTienMat();
                }
            }
        });

        card.getChildren().addAll(imageView, lblTen, lblGia, soLuongBox);
        return card;
    }

    private void addMonToOrder(Mon mon) {
        String maMon = mon.getMaMon();
        if (chiTietMap.containsKey(maMon)) return;

        HBox dong = taoDongChiTiet(mon, 1);
        chiTietMap.put(maMon, dong);
        soLuongMap.put(maMon, 1);
        vboxChiTietDonHang.getChildren().add(dong);
    }


    private void removeMonFromOrder(Mon mon) {
        String maMon = mon.getMaMon();
        HBox dong = chiTietMap.remove(maMon);
        soLuongMap.remove(maMon);
        if (dong != null) vboxChiTietDonHang.getChildren().remove(dong);
    }


    private void updateMonSoLuong(Mon mon, int soLuong) {
        String maMon = mon.getMaMon();
        soLuongMap.put(maMon, soLuong);

        HBox dong = chiTietMap.get(maMon);
        if (dong != null) {
            Label lblSL = (Label) dong.lookup(".lblSoLuongCT");
            Label lblTongTien = (Label) dong.lookup(".lblTongTienCT");

            if (lblSL != null && lblTongTien != null) {
                lblSL.setText(String.valueOf(soLuong));
                lblTongTien.setText(formatCurrency(mon.giaBan() * soLuong));
            }
        }
    }

    private HBox taoDongChiTiet(Mon mon, int soLuong) {
        HBox row = new HBox();
        row.getStyleClass().add("order-row");
        row.setSpacing(10);
        row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        Label lblTen = new Label(mon.getTenMon());
        lblTen.getStyleClass().addAll("order-col", "product");
        lblTen.setPrefWidth(100);

        Label lblSoLuong = new Label(String.valueOf(soLuong));
        lblSoLuong.getStyleClass().addAll("order-col", "quantity", "lblSoLuongCT");
        lblSoLuong.setPrefWidth(60);

        // CHANGED
        Label lblGia = new Label(formatCurrency(mon.giaBan()));
        lblGia.getStyleClass().addAll("order-col", "price");
        lblGia.setPrefWidth(70);

        Label lblTongTien = new Label(formatCurrency(mon.giaBan() * soLuong));
        lblTongTien.getStyleClass().addAll("order-col", "total", "lblTongTienCT");
        lblTongTien.setPrefWidth(80);

        Region space1 = new Region();
        Region space2 = new Region();
        Region space3 = new Region();
        HBox.setHgrow(space1, javafx.scene.layout.Priority.ALWAYS);
        HBox.setHgrow(space2, javafx.scene.layout.Priority.ALWAYS);
        HBox.setHgrow(space3, javafx.scene.layout.Priority.ALWAYS);

        row.getChildren().addAll(lblTen, space1, lblSoLuong, space2, lblGia, space3, lblTongTien);
        return row;
    }

    private void capNhatTongTien() {
        double tongTien = 0;
        List<Mon> ds = monDAO.getAll();

        for (Map.Entry<String, Integer> entry : soLuongMap.entrySet()) {
            String maMon = entry.getKey();
            int soLuong = entry.getValue();

            Mon mon = ds.stream()
                    .filter(m -> m.getMaMon().equals(maMon))
                    .findFirst()
                    .orElse(null);

            if (mon != null) {
                tongTien += mon.giaBan() * soLuong;
            }
        }

        double thue = tongTien * 0.1;
        double tienPT = tongTien + thue;

        lbl_total.setText(formatCurrency(tongTien));
        lbl_thue.setText(formatCurrency(thue));
        lbl_total_PT.setText(formatCurrency(tienPT));
    }



    private String formatCurrency(double amount) {
        Locale localeVN = new Locale("vi", "VN");
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(localeVN);

        DecimalFormat df = new DecimalFormat("#,###", symbols);

        return df.format(amount) + " đ";
    }

    private double parseCurrency(String text) {
        if (text == null || text.isBlank()) return 0;
        String clean = text.replaceAll("[^\\d]", "");
        if (clean.isEmpty()) return 0;
        try {
            return Double.parseDouble(clean);
        } catch (NumberFormatException e) {
            return 0;
        }
    }


    private void xuLyHienThiTienMat() {
        paymentGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            boolean isTienMat = newToggle == rdoTienMat;

            vboxTienMat.setVisible(isTienMat);
            vboxTienMat.setManaged(isTienMat);

            Button[] nutGoiY = {btnGoiY1, btnGoiY2, btnGoiY3, btnGoiY4, btnGoiY5, btnGoiY6};
            for (Button b : nutGoiY) {
                b.setDisable(!isTienMat);
            }

            if (isTienMat) {
                taoGoiYTienKhach();
            }
        });
    }


    private void taoGoiYTienKhach() {
        double tongTien = parseCurrency(lbl_total_PT.getText());;
        if (tongTien <= 0) return;

        double base = Math.round(tongTien / 1000.0) * 1000;
        double[] goiY;

        if (base < 1_000_000) {
            goiY = new double[]{
                    base,
                    Math.ceil(base / 10_000) * 10_000,     // ví dụ: 370.000
                    Math.ceil(base / 50_000) * 50_000,     // 400.000
                    Math.ceil(base / 100_000) * 100_000,   // 400.000 hoặc 500.000
                    500_000,
                    1_000_000
            };
        }
        // Nếu từ 1–5 triệu
        else if (base < 5_000_000) {
            goiY = new double[]{
                    base,
                    Math.ceil(base / 50_000) * 50_000,
                    Math.ceil(base / 100_000) * 100_000,
                    Math.ceil(base / 500_000) * 500_000,
                    5_000_000,
                    10_000_000
            };
        }

        else {
            goiY = new double[]{
                    base,
                    Math.ceil(base / 100_000) * 100_000,
                    Math.ceil(base / 500_000) * 500_000,
                    Math.ceil(base / 1_000_000) * 1_000_000,
                    base + 2_000_000,
                    base + 5_000_000
            };
        }

        Button[] nut = {btnGoiY1, btnGoiY2, btnGoiY3, btnGoiY4, btnGoiY5, btnGoiY6};
        for (int i = 0; i < nut.length; i++) {
            if (i < goiY.length) {
                double val = goiY[i];
                nut[i].setText(formatCurrency(val));
                nut[i].setVisible(true);
                nut[i].setOnAction(e -> {
                    txtTienKhachDua.setText(formatCurrency(val));
                    tinhTienThua();
                });
            } else {
                nut[i].setVisible(false);
            }
        }
    }

    private void tinhTienThua(){
        double tong = parseCurrency(lbl_total_PT.getText().trim());
        double tienKD = parseCurrency(txtTienKhachDua.getText().trim());
        lblTienThua.setText(formatCurrency(tienKD - tong));
    }

    private void luuHoaDon(){
        KhachHangDAO khachHangDAO = new KhachHangDAO();
        HoaDonDAO hoaDonDAO = new HoaDonDAO();
        List<KhachHang> dsKhachHang = khachHangDAO.getAll();
        String maHD = "HD52510250006";
        String maKH = "KH0000";

        //check maKH trong hệ thống
        for (KhachHang kh : dsKhachHang) {
            if (kh.getSdt().equals(sdtKhach.getText())) {
                maKH = kh.getMaKhachHang();
                break;
            }
        }
        //hết
    }


}