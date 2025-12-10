package ui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.geometry.Insets;
import javafx.scene.Node;

import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class HoTroControllerQL implements Initializable {

    @FXML private TextField txtTimKiem;
    @FXML private VBox faqContainer;

    @FXML private ScrollPane scrollPane;
    @FXML private GridPane helpCardsContainer;


    // Danh sách FAQ
    private List<FAQItem> faqList = new ArrayList<>();
    private List<HelpCard> helpCardList = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        khoiTaoDuLieuFAQ();
        khoiTaoDuLieuHelpCards();
        hienThiFAQ();
        hienThiHelpCards();
        setupTimKiem();
        setupScrollPaneStyle();

        // Tự điều chỉnh kích thước card theo GridPane width
        helpCardsContainer.widthProperty().addListener((obs, oldWidth, newWidth) -> {
            capNhatDoRongCard(newWidth.doubleValue());
        });


    }
    private void capNhatDoRongCard(double containerWidth) {

        int columns = 3;
        double hgap = helpCardsContainer.getHgap();

        double totalGap = (columns - 1) * hgap;

        double cardWidth = (containerWidth - totalGap) / columns - 5;

        for (Node node : helpCardsContainer.getChildren()) {
            if (node instanceof VBox card) {
                card.setPrefWidth(cardWidth);
            }
        }
    }


    private void khoiTaoDuLieuFAQ() {
        faqList.add(new FAQItem( //quan li
                "1. Làm thế nào để thêm khuyến mãi mới?",
                "Bước 1: Truy cập trang Khuyến Mãi\n" +
                        "Bước 2: Nhập thông tin khuyến mãi\n" +
                        "Bước 3: Chọn ngày bắt đầu/kết thúc và phần trăm giảm giá\n" +
                        "Bước 4: Nhấn nút THÊM'"
        ));

        faqList.add(new FAQItem( //ca 2
                "2. Cách xem thống kê doanh thu?",
                "• Dashboard hiển thị tự động doanh thu theo ngày\n" +
                        "• Biểu đồ cột: Top 5 món bán chạy\n" +
                        "• Biểu đồ đường: Lượng khách theo giờ\n" +
                        "• Nhấn vào trang thống kê để xem chi tiết"
        ));

        faqList.add(new FAQItem( //nhan vien
                "3. Xử lý thông báo hẹn giờ như thế nào?",
                "🟢 Màu xanh: Đã đến giờ hẹn - Chuẩn bị bàn\n" +
                        "🔴 Màu đỏ: Quá giờ hẹn - Liên hệ khách hàng\n" +
                        "🟡 Màu vàng: Sắp đến giờ hẹn - Nhắc nhở"
        ));

        faqList.add(new FAQItem( //ca 2
                "4. Cách đổi mật khẩu tài khoản?",
                "1. Từ Dashboard, nhấn nút 'Đổi Mật Khẩu'\n" +
                        "2. Nhập mật khẩu cũ\n" +
                        "3. Nhập mật khẩu mới\n" +
                        "4. Xác nhận mật khẩu mới và nhấn 'Lưu'"
        ));

        faqList.add(new FAQItem( //quan li
                "5. Quản lý món ăn trong menu?",
                "• Thêm món: Vào trang Quản Lý Menu > Dấu + ở góc phải trên > Nhấn nút Thêm mới\n" +
                        "• Sửa món: Chọn món cần sửa và nhập thông tin thay đổi > Nhấn nút Xác nhận\n" +
                        "• Xóa món: Chọn món > Nhấn nút Xóa (chỉ khi không có đơn hàng)\n"
        ));

        faqList.add(new FAQItem( //nhan vien
                "6. Xử lý đơn đặt bàn và thanh toán?",
                "1. Chọn bàn, nhập thông tin khách hàng và thêm món vào đơn\n" +
                        "2. Xem tổng tiền và áp dụng khuyến mãi (nếu có)\n" +
                        "3. Xác nhận đơn hàng\n" +
                        "4. Chọn phương thức thanh toán (tiền mặt/Chuyển khoản)\n" +
                        "5. In hóa đơn và hoàn tất"
        ));

        faqList.add(new FAQItem( //quản lí
                "7. Quản lý nhân viên và phân quyền?",
                "Chỉ Quản lý có quyền:\n" +
                        "• Thêm/Sửa/Xóa nhân viên\n" +
                        "• Phân quyền truy cập\n" +
                        "• Xem báo cáo toàn hệ thống\n"
        ));
    }

    private void khoiTaoDuLieuHelpCards() {
        helpCardList.add(new HelpCard(
                "📊", "Dashboard", "Theo dõi thống kê & báo cáo", "#3498db",
                "dashboard"
        ));

        helpCardList.add(new HelpCard(
                "🎯", "Khuyến Mãi", "Quản lý chương trình khuyến mãi", "#2ecc71",
                "khuyenmai"
        ));

        helpCardList.add(new HelpCard(
                "👥", "Quản Lý NV", "Quản lý nhân viên & phân quyền", "#9b59b6",
                "nhanvien"
        ));

        helpCardList.add(new HelpCard(
                "🍽️", "Đặt Bàn", "Quy trình đặt bàn & phục vụ", "#e74c3c",
                "datban"
        ));

        helpCardList.add(new HelpCard(
                "📦", "Hóa Đơn", "Quản lý hóa đơn", "#f39c12",
                "kho"
        ));

        helpCardList.add(new HelpCard(
                "💰", "Báo Cáo Thống Kê", "Xuất báo cáo doanh thu", "#16a085",
                "baocao"
        ));

        helpCardList.add(new HelpCard(
                "⚙️", "Tài khoản", "Vấn đề tài khoản", "#34495e",
                "caidat"
        ));

        helpCardList.add(new HelpCard(
                "📱", "Phím tắt", "Hướng dẫn dùng phím tắt", "#1abc9c",
                "mobile"
        ));
    }

    private void hienThiFAQ() {
        faqContainer.getChildren().clear();
        for (FAQItem faq : faqList) {
            faqContainer.getChildren().add(taoTitledPaneFAQ(faq));
        }
    }

    private void hienThiHelpCards() {
        helpCardsContainer.getChildren().clear();
//        for (HelpCard card : helpCardList) {
//            helpCardsContainer.getChildren().add(taoHelpCard(card));
//        }
        int columns = 3; // số card mỗi hàng
        int row = 0;
        int col = 0;

        for (HelpCard card : helpCardList) {
            VBox cardBox = taoHelpCard(card);

            helpCardsContainer.add(cardBox, col, row);

            col++;
            if (col >= columns) {
                col = 0;
                row++;
            }
        }
    }

    private TitledPane taoTitledPaneFAQ(FAQItem faq) {
        TitledPane pane = new TitledPane();
        pane.setText(faq.getQuestion());
        pane.setExpanded(false);
        pane.setAnimated(true);

        // Style cho title
        pane.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 10; -fx-border-color: #dfe6e9;");

        // Content
        VBox content = new VBox();
        content.setPadding(new Insets(15));
        content.setSpacing(10);

        TextFlow textFlow = new TextFlow();
        String[] lines = faq.getAnswer().split("\n");
        for (String line : lines) {
            Text text = new Text(line + "\n");
            text.setStyle("-fx-font-size: 14px; -fx-fill: #2c3e50;");
            textFlow.getChildren().add(text);
        }

        content.getChildren().add(textFlow);
        pane.setContent(content);

        // Thêm icon indicator
        pane.setGraphic(new Label("▶"));
        pane.expandedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                pane.setGraphic(new Label("▼"));
            } else {
                pane.setGraphic(new Label("▶"));
            }
        });

        return pane;
    }

    private VBox taoHelpCard(HelpCard card) {
        VBox cardBox = new VBox();
        cardBox.setAlignment(javafx.geometry.Pos.CENTER);
        cardBox.setSpacing(15);

        cardBox.setMinWidth(250);
        cardBox.setPrefWidth(250);      // mỗi card cùng độ rộng
        cardBox.setMaxWidth(Double.MAX_VALUE); // cho phép giãn khi FlowPane đủ chỗ


        cardBox.setPadding(new Insets(20));
        cardBox.setStyle(
                "-fx-background-color: #ffffff; " +
                        "-fx-background-radius: 15; " +
                        "-fx-padding: 20; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 10, 0, 0, 3); " +
                        "-fx-cursor: hand;"
        );

        // Icon
        Label iconLabel = new Label(card.getIcon());
        iconLabel.setStyle("-fx-font-size: 40px;");

        // Title
        Label titleLabel = new Label(card.getTitle());
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 18px; -fx-text-fill: #2c3e50;");

        // Description
        Label descLabel = new Label(card.getDescription());
        descLabel.setWrapText(true);
        descLabel.setMaxWidth(200);
        descLabel.setStyle(
                "-fx-font-size: 14px; " +
                        "-fx-text-fill: #7f8c8d; " +
                        "-fx-wrap-text: true; " +
                        "-fx-text-alignment: center;" +
                        "-fx-alignment: center"
        );

        // Button
        Button actionBtn = new Button("Xem hướng dẫn");
        actionBtn.setStyle(
                "-fx-background-color: " + card.getColor() + "; " +
                        "-fx-text-fill: white; " +
                        "-fx-background-radius: 20; " +
                        "-fx-padding: 8 20; " +
                        "-fx-font-weight: bold;"
        );

        // Gắn sự kiện click
        actionBtn.setOnAction(e -> moHuongDanChiTiet(card));
        cardBox.setOnMouseClicked(e -> moHuongDanChiTiet(card));

        // Hiệu ứng hover
        cardBox.setOnMouseEntered(e -> {
            cardBox.setStyle(
                    "-fx-background-color: #ffffff; " +
                            "-fx-background-radius: 15; " +
                            "-fx-padding: 20; " +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 15, 0, 0, 5); " +
                            "-fx-translate-y: -5; " +
                            "-fx-cursor: hand;"
            );
        });

        cardBox.setOnMouseExited(e -> {
            cardBox.setStyle(
                    "-fx-background-color: #ffffff; " +
                            "-fx-background-radius: 15; " +
                            "-fx-padding: 20; " +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 10, 0, 0, 3); " +
                            "-fx-translate-y: 0; " +
                            "-fx-cursor: hand;"
            );
        });
        //nut


        // Hover vào
        String baseColor = card.getColor(); // màu gốc từ card
        String hoverColor = "#2c3e50";      // màu hover bạn muốn
        actionBtn.setOnMouseEntered(e -> {
            actionBtn.setStyle(
                    "-fx-background-color: " + hoverColor + ";" +
                            "-fx-text-fill: white;" +
                            "-fx-background-radius: 20;" +
                            "-fx-padding: 8 20;" +
                            "-fx-font-weight: bold;"
            );
        });

// Hover ra
        actionBtn.setOnMouseExited(e -> {
            actionBtn.setStyle(
                    "-fx-background-color: " + baseColor + ";" +
                            "-fx-text-fill: white;" +
                            "-fx-background-radius: 20;" +
                            "-fx-padding: 8 20;" +
                            "-fx-font-weight: bold;"
            );
        });

        cardBox.getChildren().addAll(iconLabel, titleLabel, descLabel, actionBtn);
        return cardBox;
    }

    private void setupTimKiem() {
        txtTimKiem.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.trim().isEmpty()) {
                hienThiFAQ();
                hienThiHelpCards();
            } else {
                timKiemFAQ(newValue.trim().toLowerCase());
                timKiemHelpCards(newValue.trim().toLowerCase());
            }
        });

        // Sự kiện nhấn Enter
        txtTimKiem.setOnAction(e -> {
            String keyword = txtTimKiem.getText().trim().toLowerCase();
            if (!keyword.isEmpty()) {
                timKiemFAQ(keyword);
                timKiemHelpCards(keyword);
            }
        });
    }

    private void timKiemFAQ(String keyword) {
        faqContainer.getChildren().clear();

        List<FAQItem> ketQua = faqList.stream()
                .filter(faq -> faq.getQuestion().toLowerCase().contains(keyword) ||
                        faq.getAnswer().toLowerCase().contains(keyword))
                .collect(Collectors.toList());

        if (ketQua.isEmpty()) {
            Label noResult = new Label("Không tìm thấy kết quả nào cho \"" + keyword + "\"");
            noResult.setStyle("-fx-font-size: 16px; -fx-text-fill: #7f8c8d; -fx-padding: 20;");
            faqContainer.getChildren().add(noResult);
        } else {
            for (FAQItem faq : ketQua) {
                faqContainer.getChildren().add(taoTitledPaneFAQ(faq));
            }
        }
    }

    private void timKiemHelpCards(String keyword) {
        helpCardsContainer.getChildren().clear();

        List<HelpCard> ketQua = helpCardList.stream()
                .filter(card -> card.getTitle().toLowerCase().contains(keyword)
                        || card.getDescription().toLowerCase().contains(keyword)
                        || card.getTag().toLowerCase().contains(keyword))
                .collect(Collectors.toList());

        if (ketQua.isEmpty()) {
            hienThiHelpCards();
            return;
        }

        int col = 0, row = 0;
        for (HelpCard card : ketQua) {
            helpCardsContainer.add(taoHelpCard(card), col, row);
            col++;
            if (col >= 3) {
                col = 0;
                row++;
            }
        }
    }


    private void setupScrollPaneStyle() {
        // Tắt thanh scroll ngang
        scrollPane.setFitToWidth(true);

        // Style scrollbar dọc
        scrollPane.lookupAll(".scroll-bar").forEach(node -> {
            if (node instanceof ScrollBar) {
                ScrollBar scrollBar = (ScrollBar) node;
                if (scrollBar.getOrientation() == javafx.geometry.Orientation.VERTICAL) {
                    scrollBar.setStyle(
                            "-fx-background-color: transparent;" +
                                    "-fx-background-radius: 6;" +
                                    "-fx-pref-width: 12px;"
                    );
                }
            }
        });
    }

    @FXML
    private void onTimKiemClick() {
        String keyword = txtTimKiem.getText().trim().toLowerCase();
        if (!keyword.isEmpty()) {
            timKiemFAQ(keyword);
            timKiemHelpCards(keyword);
        }
    }

    @FXML
    private void onTaiTaiLieuPDF() {
        // TODO: Implement PDF download functionality
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Tải tài liệu");
        alert.setHeaderText("Tải tài liệu hướng dẫn PDF");
        alert.setContentText("Chức năng này đang được phát triển. Tài liệu sẽ được tải xuống sớm.");
        alert.showAndWait();
    }

    @FXML
    private void onLienHeHotline() {
        // TODO: Implement call functionality
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Liên hệ hỗ trợ");
        alert.setHeaderText("Hotline: 1900 1234");
        alert.setContentText("Vui lòng gọi số trên để được hỗ trợ trực tiếp.");
        alert.showAndWait();
    }

    private void moHuongDanChiTiet(HelpCard card) {
        // TODO: Implement detailed guide opening
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Hướng dẫn chi tiết");
        alert.setHeaderText("Hướng dẫn: " + card.getTitle());

        // Nội dung hướng dẫn chi tiết cho từng module
        String content = "";
        switch (card.getTag()) {
            case "dashboard":
                content = "Dashboard cung cấp:\n\n" +
                        "1. Thông tin nhân viên đăng nhập\n" +
                        "2. Thống kê đơn hàng theo trạng thái\n" +
                        "3. Thống kê khu vực\n" +
                        "4. Doanh thu và số khách\n" +
                        "5. Biểu đồ top 5 món bán chạy\n" +
                        "6. Biểu đồ lượng khách theo giờ\n" +
                        "7. Thông báo check-in và tồn kho";
                break;
            case "khuyenmai":
                content = "Quản lý khuyến mãi:\n\n" +
                        "1. Thêm khuyến mãi mới\n" +
                        "2. Xem danh sách khuyến mãi\n" +
                        "3. Lọc theo trạng thái và ưu đãi\n" +
                        "4. Sửa/Xóa khuyến mãi\n" +
                        "5. In mã QR cho khuyến mãi\n" +
                        "6. Chọn món áp dụng khuyến mãi";
                break;
            case "nhanvien":
                content = "Quản lý nhân viên:\n\n" +
                        "1. Chỉ quản lý có quyền truy cập\n" +
                        "2. Thêm/Sửa/Xóa nhân viên\n" +
                        "3. Phân quyền chức năng\n" +
                        "4. Xem lịch sử làm việc\n" +
                        "5. Quản lý tài khoản đăng nhập";
                break;
            default:
                content = "Hướng dẫn chi tiết cho " + card.getTitle() + " đang được cập nhật.";

        }

//        alert.setContentText(content);
//        alert.setWidth(400);
//        alert.setHeight(300);
//        alert.showAndWait();
        showCustomDialog("Hướng dẫn: " + card.getTitle(), content);
    }

    // Inner classes for data model
    private class FAQItem {
        private String question;
        private String answer;

        public FAQItem(String question, String answer) {
            this.question = question;
            this.answer = answer;
        }

        public String getQuestion() { return question; }
        public String getAnswer() { return answer; }
    }

    private class HelpCard {
        private String icon;
        private String title;
        private String description;
        private String color;
        private String tag;

        public HelpCard(String icon, String title, String description, String color, String tag) {
            this.icon = icon;
            this.title = title;
            this.description = description;
            this.color = color;
            this.tag = tag;
        }

        public String getIcon() { return icon; }
        public String getTitle() { return title; }
        public String getDescription() { return description; }
        public String getColor() { return color; }
        public String getTag() { return tag; }
    }

    // Phương thức để tích hợp với MainController (tương tự DashboardController)
    public void setMainController(Object controller) {
        // Tương tự như DashboardController, có thể nhận MainController_NV hoặc MainController_QL
        // Nếu cần thông tin nhân viên để tùy chỉnh hướng dẫn
    }

    // Phương thức để cập nhật dữ liệu real-time nếu cần
    public void refreshData() {
        // Có thể cập nhật thông tin mới nhất nếu cần
    }

    @FXML
    private void onExpandAllFAQ() {
        // Mở rộng tất cả FAQ
        for (Node node : faqContainer.getChildren()) {
            if (node instanceof TitledPane) {
                ((TitledPane) node).setExpanded(true);
            }
        }
    }

    @FXML
    private void onCollapseAllFAQ() {
        // Thu gọn tất cả FAQ
        for (Node node : faqContainer.getChildren()) {
            if (node instanceof TitledPane) {
                ((TitledPane) node).setExpanded(false);
            }
        }
    }

    private void showCustomDialog(String title, String content) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle(title);

        VBox box = new VBox(20);
        box.setPadding(new Insets(25));
        box.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 15;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 20, 0, 0, 5);"
        );

        Label lblTitle = new Label(title);
        lblTitle.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Label lblContent = new Label(content);
        lblContent.setWrapText(true);
        lblContent.setStyle("-fx-font-size: 15px; -fx-text-fill: #34495e;");
        lblContent.setMaxWidth(400);

        Button btnClose = new Button("Đóng");
        btnClose.setStyle(
                "-fx-background-color: #3498db;" +
                        "-fx-text-fill: white;" +
                        "-fx-background-radius: 18;" +
                        "-fx-padding: 8 20;" +
                        "-fx-font-size: 14px; -fx-font-weight: bold;"
        );
        btnClose.setOnAction(e -> dialog.close());

        box.getChildren().addAll(lblTitle, lblContent, btnClose);
        box.setAlignment(Pos.CENTER);

        Scene scene = new Scene(box);
        dialog.setScene(scene);
        dialog.setResizable(false);
        dialog.show();
    }


//    // =======================
//// Hộp thoại thông báo đẹp
//// =======================
//    private void hienThongBao(String tieuDe, String noiDung, String loai) {
//        Alert alert;
//
//        switch (loai.toLowerCase()) {
//            case "success":
//                alert = new Alert(Alert.AlertType.INFORMATION);
//                break;
//            case "warning":
//                alert = new Alert(Alert.AlertType.WARNING);
//                break;
//            case "error":
//                alert = new Alert(Alert.AlertType.ERROR);
//                break;
//            default:
//                alert = new Alert(Alert.AlertType.INFORMATION);
//        }
//
//        DialogPane dialogPane = alert.getDialogPane();
//
//        // Style tổng thể
//        dialogPane.setStyle(
//                "-fx-background-color: #ffffff;" +
//                        "-fx-border-radius: 15;" +
//                        "-fx-background-radius: 15;" +
//                        "-fx-padding: 20;"
//        );
//
//        // Style nội dung text
//        dialogPane.lookup(".header-panel").setStyle(
//                "-fx-background-color: transparent;" +
//                        "-fx-padding: 0 0 10 0;"
//        );
//
//        dialogPane.lookup(".content.label").setStyle(
//                "-fx-font-size: 14px;" +
//                        "-fx-text-fill: #2c3e50;"
//        );
//
//        // Style nút OK
//        Button btnOk = (Button) dialogPane.lookupButton(ButtonType.OK);
//        btnOk.setText("Đóng");
//        btnOk.setStyle(
//                "-fx-background-color: #3498db;" +
//                        "-fx-background-radius: 20;" +
//                        "-fx-text-fill: white;" +
//                        "-fx-font-weight: bold;" +
//                        "-fx-padding: 8 20;"
//        );
//
//        btnOk.setOnMouseEntered(e -> btnOk.setStyle(
//                "-fx-background-color: #2c3e50;" +
//                        "-fx-background-radius: 20;" +
//                        "-fx-text-fill: white;" +
//                        "-fx-font-weight: bold;" +
//                        "-fx-padding: 8 20;"
//        ));
//
//        btnOk.setOnMouseExited(e -> btnOk.setStyle(
//                "-fx-background-color: #3498db;" +
//                        "-fx-background-radius: 20;" +
//                        "-fx-text-fill: white;" +
//                        "-fx-font-weight: bold;" +
//                        "-fx-padding: 8 20;"
//        ));
//
//        // Gán tiêu đề – nội dung
//        alert.setTitle(tieuDe);
//        alert.setHeaderText(tieuDe);
//        alert.setContentText(noiDung);
//
//        alert.showAndWait();
//    }

}