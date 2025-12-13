package ui.controllers;

import dao.BanDAO;
import dao.KhuVucDAO;
import dao.LoaiBanDAO;
import entity.Ban;
import entity.KhuVuc;
import entity.LoaiBan;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ui.AlertCus;
import ui.ConfirmCus;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;

import java.awt.*;
import java.util.List;

public class QLBanController {

    @FXML private FlowPane flowPaneBan;
    @FXML private Label lblKhuVuc;
    @FXML private Label lblMaBan;
    @FXML private Label lblSoLuong, btnAddBan,lblSearchBtn,btnDeleteBan;
    @FXML private Button btnModalCancel, btnModalSave;
    @FXML private ComboBox<String> comboDanhMuc;
    @FXML private ComboBox<String> comboKhuVuc;
    @FXML private VBox overlayModal;
    @FXML private StackPane modalLayer;
    @FXML private StackPane rootPane;
    @FXML private Rectangle modalBg;
    @FXML private ComboBox comboModalLoaiBan, comboModalKhuVuc;
    @FXML private TextField txtModalMaBan,txtSearchTop;
    private List<Ban> dsBan;
    private boolean deleteMode = false;
    private final BanDAO banDAO = new BanDAO();
    private final LoaiBanDAO loaiBanDAO = new LoaiBanDAO();
    private final KhuVucDAO khuVucDAO = new KhuVucDAO();

    @FXML
    public void initialize() {
        loadAllBan();
        loadDanhMuc();
        loadKhuVuc();

        comboModalLoaiBan.getItems().addAll("A", "B", "C", "D", "E");
        comboModalKhuVuc.getItems().addAll("Indoor", "Outdoor", "VIP");

        comboDanhMuc.setOnAction(event -> filterBan());
        comboKhuVuc.setOnAction(event -> filterBan());
//        txtSearchTop.textProperty().addListener((obs, oldText, newText)-> filterBan());

        modalBg.widthProperty().bind(rootPane.widthProperty());
        modalBg.heightProperty().bind(rootPane.heightProperty());

        // Ẩn modal lúc đầu
        modalLayer.setVisible(false);
        modalLayer.setManaged(false);

        btnAddBan.setOnMouseClicked(e -> showModal());
        btnModalCancel.setOnAction(e -> hideModal());

        btnModalSave.setOnAction(e -> themBan());

        comboModalKhuVuc.setOnAction(event -> sinhMaBanMoi());

        txtSearchTop.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                addShortcuts(newScene);
            }
        });
        Tooltip tipFind = new Tooltip("Tìm kiếm bàn (Ctrl + F)");
        tipFind.getStyleClass().add("tooltip");
        Tooltip.install(txtSearchTop, tipFind);
        Tooltip tipNew = new Tooltip("Thêm bàn mới  (Ctrl + N)");
        tipNew.getStyleClass().add("tooltip");
        Tooltip.install(btnAddBan, tipNew);

        lblSearchBtn.setOnMouseClicked(e -> {
            String keyword = txtSearchTop.getText().trim();
            timKiemBan(keyword);
        });

        btnDeleteBan.setOnMouseClicked(e -> {
            deleteMode = !deleteMode;

            if (deleteMode) {
                btnDeleteBan.setStyle("-fx-text-fill: black; -fx-font-weight: bold;-fx-background-color: #c8c7c7");
//                AlertCus.show("Chế độ xóa", "ĐÃ bật chế độ xóa.\nHãy double click vào bàn để xóa!");
            } else {
                btnDeleteBan.setStyle("");
//                AlertCus.show("Chế độ xóa", "ĐÃ tắt chế độ xóa.");
            }
        });


        txtSearchTop.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                lblSearchBtn.fireEvent(new MouseEvent(
                        MouseEvent.MOUSE_CLICKED,
                        0, 0, 0, 0,
                        MouseButton.PRIMARY,
                        1,
                        false, false, false, false,
                        true, false, false, true,
                        false, false, null
                ));
            }
        });
    }

    // Load toàn bộ bàn
    private void loadAllBan() {
        flowPaneBan.getChildren().clear();
         dsBan = banDAO.getAll();
        for (Ban ban : dsBan) {
            VBox card = taoTheBan(ban);
            flowPaneBan.getChildren().add(card);
        }
    }

    // Tạo thẻ hiển thị bàn
    private VBox taoTheBan(Ban ban) {
        VBox card = new VBox();
        card.getStyleClass().add("menu-item");

        card.setOnMouseClicked(event -> {
            if (deleteMode && event.getClickCount() == 2) {
                xoaBan(ban);
            }
        });


        HBox infoBox = new HBox();
        infoBox.getStyleClass().add("item-info");

        VBox group1 = new VBox();
        group1.getStyleClass().add("item-group");
        Label lbTitle1 = new Label("Mã bàn");
        lbTitle1.getStyleClass().add("item-title");
        Label lbDetail1 = new Label(ban.getMaBan());
        lbDetail1.getStyleClass().add("item-detail");
        group1.getChildren().addAll(lbTitle1, lbDetail1);

        VBox group2 = new VBox();
        group2.getStyleClass().add("item-group");
        Label lbTitle2 = new Label("Loại bàn");
        lbTitle2.getStyleClass().add("item-title");
        Label lbDetail2 = new Label(ban.getLoaiBan().getTenLoaiBan() + " (" + ban.getLoaiBan().getSoLuong() + " người)");
        lbDetail2.getStyleClass().add("item-detail");
        group2.getChildren().addAll(lbTitle2, lbDetail2);

        VBox group3 = new VBox();
        group3.getStyleClass().add("item-group");
        Label lbTitle3 = new Label("Khu vực");
        lbTitle3.getStyleClass().add("item-title");
        Label lbDetail3 = new Label(ban.getKhuVuc().getTenKhuVuc());
        lbDetail3.getStyleClass().add("item-detail");
        group3.getChildren().addAll(lbTitle3, lbDetail3);

        infoBox.getChildren().addAll(group1, group2, group3);
        card.getChildren().addAll(infoBox);
        return card;
    }

    private void addShortcuts(Scene scene){
        KeyCombination ctrlF = new KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN);
        KeyCombination ctrlN = new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN);
        scene.getAccelerators().put(ctrlF, () -> {
            txtSearchTop.requestFocus();
            txtSearchTop.selectAll();  // tự bôi đen text để nhập mới
        });
        scene.getAccelerators().put(ctrlN, () -> showModal());
    }

    private void loadDanhMuc() {
        comboDanhMuc.getItems().clear();
        comboDanhMuc.getItems().add("Tất cả");

        List<LoaiBan> dsLoaiBan = loaiBanDAO.getAll();
        for (LoaiBan loai : dsLoaiBan) {
            comboDanhMuc.getItems().add("Loại bàn : " + loai.getTenLoaiBan());
        }

        comboDanhMuc.setValue("Tất cả");
    }

    private void loadKhuVuc() {
        comboKhuVuc.getItems().clear();
        comboKhuVuc.getItems().add("Tất cả");

        List<KhuVuc> dsKhuVuc = khuVucDAO.getAll();
        for (KhuVuc kv : dsKhuVuc) {
            comboKhuVuc.getItems().add("Khu vực : " + kv.getTenKhuVuc());
        }

        comboKhuVuc.setValue("Tất cả");
    }

    private void filterBan() {
        String selectedLoaiBan = comboDanhMuc.getValue();
        String selectedKhuVuc = comboKhuVuc.getValue();

        if ((selectedLoaiBan == null || selectedLoaiBan.equals("Tất cả")) &&
                (selectedKhuVuc == null || selectedKhuVuc.equals("Tất cả"))) {
            loadAllBan();
            return;
        }

        flowPaneBan.getChildren().clear();
        List<Ban> dsBan = banDAO.getAll();

        for (Ban ban : dsBan) {
            boolean match = true;

            // Lọc theo loại bàn
            if (selectedLoaiBan != null && !selectedLoaiBan.equals("Tất cả")) {
                String tenLoai = selectedLoaiBan.replace("Loại bàn : ", "").trim();
                if (!ban.getLoaiBan().getTenLoaiBan().equalsIgnoreCase(tenLoai)) {
                    match = false;
                }
            }

            // Lọc theo khu vực
            if (selectedKhuVuc != null && !selectedKhuVuc.equals("Tất cả")) {
                String tenKhuVuc = selectedKhuVuc.replace("Khu vực : ", "").trim();
                if (!ban.getKhuVuc().getTenKhuVuc().equalsIgnoreCase(tenKhuVuc)) {
                    match = false;
                }
            }

            if (match) {
                VBox card = taoTheBan(ban);
                flowPaneBan.getChildren().add(card);
            }
        }
    }

    // Hiển thị modal
    public void showModal() {
        modalLayer.setVisible(true);
        modalLayer.setManaged(true);
    }

    // Ẩn modal
    public void hideModal() {
        modalLayer.setVisible(false);
        modalLayer.setManaged(false);
    }

    private void sinhMaBanMoi() {
        Object selected = comboModalKhuVuc.getValue();
        if (selected == null) return;

        String khuVuc = selected.toString(); // ép kiểu sang String
        String prefix = switch (khuVuc) {
            case "Indoor" -> "BI";
            case "Outdoor" -> "BO";
            case "VIP" -> "BV";
            default -> "B";
        };

        String maBanCuoi = BanDAO.getMaBanCuoiTheoKhuVuc(khuVuc);

        int soMoi = 1;
        if (maBanCuoi != null && maBanCuoi.startsWith(prefix)) {
            try {
                String numberPart = maBanCuoi.substring(2);
                soMoi = Integer.parseInt(numberPart) + 1;
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        String maBanMoi = prefix + String.format("%04d", soMoi);
        txtModalMaBan.setText(maBanMoi);
    }

    @FXML
    private void themBan() {
        try {
            // Lấy mã bàn từ txtModalMaBan
            String maBan = txtModalMaBan.getText();
            if (maBan == null || maBan.isEmpty()) {
                AlertCus.show("Thông báo","Chưa chọn bàn và khu vực!");
                return;
            }

            Object selected02 = comboModalLoaiBan.getValue();
            if (selected02 == null) return;

            String loaiBanChon = selected02.toString();
            String maLoaiBan = switch (loaiBanChon) {
                case "A" -> "LB0001";
                case "B" -> "LB0002";
                case "C" -> "LB0003";
                case "D" -> "LB0004";
                case "E" -> "LB0005";
                default -> null;
            };
            String tenLoaiBan = switch (loaiBanChon) {
                case "A" -> "Loại A";
                case "B" -> "Loại B";
                case "C" -> "Loại C";
                case "D" -> "Loại D";
                case "E" -> "Loại E";
                default -> null;
            };
            int soLuong = switch (loaiBanChon) {
                case "A" -> 2;
                case "B" -> 4;
                case "C" -> 8;
                case "D" -> 12;
                case "E" -> 20;
                default -> 0;
            };
            if (maLoaiBan == null) return;

            LoaiBan loaiBan = new LoaiBan();
            loaiBan.setMaLoaiBan(maLoaiBan);
            loaiBan.setTenLoaiBan(tenLoaiBan);
            loaiBan.setSoLuong(soLuong);

            Object selected = comboModalKhuVuc.getValue();
            if (selected == null) return;

            String khuVucChon = selected.toString();
            String maKhuVuc = switch (khuVucChon) {
                case "Outdoor" -> "KV0001";
                case "Indoor" -> "KV0002";
                case "VIP" -> "KV0003";
                default -> null;
            };
            if (maKhuVuc == null) return;

            KhuVuc kv = new KhuVuc();
            kv.setMaKhuVuc(maKhuVuc);
            kv.setTenKhuVuc(khuVucChon);

            // Tạo đối tượng Ban mới, trạng thái mặc định là 0 (false)
            Ban banMoi = new Ban();
            banMoi.setMaBan(maBan);
            banMoi.setLoaiBan(loaiBan);
            banMoi.setKhuVuc(kv);
            banMoi.setTrangThai(false);

            // Gọi DAO insert
            boolean success = banDAO.insert(banMoi, false);
            if (success) {
                AlertCus.show("Thông báo","Thêm bàn thành công : " + maBan);
                loadAllBan();
                hideModal();
            } else {
                AlertCus.show("Thông báo","Thêm bàn thất bại!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void xoaBan(Ban ban) {
        boolean answer = ConfirmCus.show("Xác nhận", "Xác nhận xóa bàn : " + ban.getMaBan());
        if (answer) {
            boolean success = banDAO.delete(ban.getMaBan());
            if (success) {
                AlertCus.show("Thông báo", "Xóa bàn thành công : " + ban.getMaBan());
                loadAllBan();
                loadDanhMuc();
                loadKhuVuc();
            } else {
                AlertCus.show("Thông báo", "Xóa bàn thất bại!");
            }
        }
    }

    private void timKiemBan(String keyword) {
        keyword = keyword.toLowerCase();
        flowPaneBan.getChildren().clear();

        List<Ban> dsBan = banDAO.getAll();

        if (keyword.isEmpty()) {
            loadAllBan();
            return;
        }

        for (Ban ban : dsBan) {

            String maBan = ban.getMaBan().toLowerCase();
            String loaiBan = ban.getLoaiBan().getTenLoaiBan().toLowerCase();
            String khuVuc = ban.getKhuVuc().getTenKhuVuc().toLowerCase();

            // Lấy ký hiệu loại bàn (A/B/C/D/E)
            String kyHieuLoai = loaiBan.substring(loaiBan.length() - 1);

            boolean match =
                    maBan.contains(keyword)
                            || loaiBan.contains(keyword)
                            || kyHieuLoai.contains(keyword)
                            || khuVuc.contains(keyword);

            if (match) {
                flowPaneBan.getChildren().add(taoTheBan(ban));
            }
        }
    }

}

