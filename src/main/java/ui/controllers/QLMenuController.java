package ui.controllers;

import dao.LoaiMonDAO;
import dao.MonDAO;
import entity.LoaiMon;
import entity.Mon;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.List;

public class QLMenuController {

    @FXML
    private FlowPane flowMonAn;
    @FXML private ComboBox<LoaiMon> comboDanhMuc;

    private final MonDAO monDAO = new MonDAO();
    private final LoaiMonDAO loaiMonDAO = new LoaiMonDAO();

    @FXML
    public void initialize() {
        loadDanhSachMon();
        loadComboDanhMuc();
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

        for (Mon mon : danhSach) {
            VBox card = taoCardMon(mon);
            flowMonAn.getChildren().add(card);
        }
    }

    private VBox taoCardMon(Mon mon) {
        VBox card = new VBox();
        card.getStyleClass().add("menu-item");

        // Ảnh món
        ImageView imageView = new ImageView();
        try {
            imageView.setImage(new Image(getClass().getResourceAsStream("/IMG/food" + mon.getHinhAnh())));
        } catch (Exception e) {
            imageView.setImage(new Image(getClass().getResourceAsStream("/IMG/food/restaurant.png")));
        }

        imageView.setFitWidth(180);
        imageView.setFitHeight(180);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        imageView.setCache(true);

        imageView.getStyleClass().add("menu-image");

        // Tên và giá
        Label lblTen = new Label(mon.getTenMon());
        lblTen.getStyleClass().add("item-name");

        Label lblGia = new Label(String.format("%.0f đ", mon.getGiaBan()));
        lblGia.getStyleClass().add("item-price");

        Region space = new Region();
        HBox.setHgrow(space, javafx.scene.layout.Priority.ALWAYS);

        HBox infoBox = new HBox(lblTen, space, lblGia);
        infoBox.getStyleClass().add("item-info");

        card.getChildren().addAll(imageView, infoBox);
        return card;
    }
}
