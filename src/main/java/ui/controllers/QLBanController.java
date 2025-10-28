package ui.controllers;

import dao.BanDAO;
import dao.KhuVucDAO;
import dao.LoaiBanDAO;
import entity.Ban;
import entity.KhuVuc;
import entity.LoaiBan;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;

public class QLBanController {

    @FXML private FlowPane flowPaneBan;
    @FXML private Label lblKhuVuc;
    @FXML private Label lblMaBan;
    @FXML private Label lblSoLuong;
    @FXML private ComboBox<String> comboDanhMuc;
    @FXML private ComboBox<String> comboKhuVuc;

    private final BanDAO banDAO = new BanDAO();
    private final LoaiBanDAO loaiBanDAO = new LoaiBanDAO();
    private final KhuVucDAO khuVucDAO = new KhuVucDAO();

    @FXML
    public void initialize() {
        loadAllBan();
        loadDanhMuc();
        loadKhuVuc();

        comboDanhMuc.setOnAction(event -> filterBan());
        comboKhuVuc.setOnAction(event -> filterBan());
    }

    // Load toàn bộ bàn
    private void loadAllBan() {
        flowPaneBan.getChildren().clear();
        List<Ban> dsBan = banDAO.getAll();
        for (Ban ban : dsBan) {
            VBox card = taoTheBan(ban);
            flowPaneBan.getChildren().add(card);
        }
    }

    // Tạo thẻ hiển thị bàn
    private VBox taoTheBan(Ban ban) {
        VBox card = new VBox();
        card.getStyleClass().add("menu-item");

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
}
