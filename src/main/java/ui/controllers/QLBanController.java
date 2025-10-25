package ui.controllers;

import dao.BanDAO;
import entity.Ban;
import entity.KhuVuc;
import entity.LoaiBan;
import javafx.fxml.FXML;
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

    private String imgUrlLoaiBan;
    private final BanDAO banDAO = new BanDAO();

    @FXML
    public void initialize() {
        loadAllBan();
    }

    private String imgLoaiBan(String urlLoaiBan) {
        if (urlLoaiBan.equals("Indoor")) {
            return "/IMG/ban/IN.png";
        } else if (urlLoaiBan.equals("Outdoor")) {
            return "/IMG/ban/out.png";
        } else if (urlLoaiBan.equals("VIP")) {
            return "/IMG/ban/vip.png";
        }
        return null;
    }

    private void loadAllBan() {
        flowPaneBan.getChildren().clear();
        List<Ban> dsBan = banDAO.getAll();

        for (Ban ban : dsBan) {
            VBox card = taoTheBan(ban);
            flowPaneBan.getChildren().add(card);
        }
    }

    private VBox taoTheBan(Ban ban) {
        VBox card = new VBox(5);
        card.getStyleClass().add("menu-item");

        // Hình ảnh bàn
        ImageView img = new ImageView(new Image(getClass().getResourceAsStream(imgLoaiBan(ban.getKhuVuc().getTenKhuVuc()))));
        img.setFitWidth(180);
        img.setFitHeight(120);
        img.setPreserveRatio(true);

        // Hộp thông tin bàn
        HBox infoBox = new HBox(20);
        infoBox.getStyleClass().add("item-info");

        // ========== Nhóm 1: Mã bàn ==========
        VBox group1 = new VBox(2);
        group1.getStyleClass().add("item-group");
        Label lbTitle1 = new Label("Mã bàn");
        lbTitle1.getStyleClass().add("item-title");
        Label lbDetail1 = new Label(ban.getMaBan());
        lbDetail1.getStyleClass().add("item-detail");
        group1.getChildren().addAll(lbTitle1, lbDetail1);

        // ========== Nhóm 2: Loại bàn ==========
        LoaiBan loai = ban.getLoaiBan();
        VBox group2 = new VBox(2);
        group2.getStyleClass().add("item-group");
        Label lbTitle2 = new Label("Loại bàn");
        lbTitle2.getStyleClass().add("item-title");
        Label lbDetail2 = new Label(loai.getTenLoaiBan() + " (" + loai.getSoLuong() + " người)");
        lbDetail2.getStyleClass().add("item-detail");
        group2.getChildren().addAll(lbTitle2, lbDetail2);

        // ========== Nhóm 3: Khu vực ==========
        KhuVuc kv = ban.getKhuVuc();
        VBox group3 = new VBox(2);
        group3.getStyleClass().add("item-group");
        Label lbTitle3 = new Label("Khu vực");
        lbTitle3.getStyleClass().add("item-title");
        Label lbDetail3 = new Label(kv.getTenKhuVuc());
        lbDetail3.getStyleClass().add("item-detail");
        group3.getChildren().addAll(lbTitle3, lbDetail3);

        // Gộp các nhóm vào infoBox
        infoBox.getChildren().addAll(group1, group2, group3);

        // Thêm ảnh + info vào thẻ
        card.getChildren().addAll(img, infoBox);
        // Sự kiện click
//        card.setOnMouseClicked(e -> {
//            System.out.println("Đã chọn bàn: " + ban.getMaBan());
//        });
        return card;
    }
}
