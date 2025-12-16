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

public class HoTroControllerNV implements Initializable {

    @FXML private TextField txtTimKiem;
    @FXML private VBox faqContainer;
    @FXML private ScrollPane scrollPane;
    @FXML private GridPane helpCardsContainer;


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

        faqList.add(new FAQItem(
                "1. Cách xem thống kê doanh thu?",
                "• Dashboard hiển thị tự động doanh thu theo ngày\n" +
                        "• Biểu đồ cột: Top 5 món bán chạy\n" +
                        "• Biểu đồ đường: Lượng khách theo giờ\n" +
                        "• Nhấn vào trang thống kê để xem chi tiết"
        ));

        faqList.add(new FAQItem(
                "2. Xử lý thông báo hẹn giờ như thế nào?",
                "Màu xanh: Đã đến giờ hẹn - Chuẩn bị bàn\n" +
                        "Màu đỏ: Quá giờ hẹn - Liên hệ khách hàng\n" +
                        "Màu vàng: Sắp đến giờ hẹn - Nhắc nhở"
        ));

        faqList.add(new FAQItem(
                "3. Cách đổi mật khẩu tài khoản?",
                "1. Từ Dashboard, nhấn nút 'Đổi Mật Khẩu'\n" +
                        "2. Nhập mật khẩu cũ\n" +
                        "3. Nhập mật khẩu mới\n" +
                        "4. Xác nhận mật khẩu mới và nhấn 'Lưu'"
        ));

        faqList.add(new FAQItem(
                "4. Xử lý đơn đặt bàn và thanh toán?",
                "1. Chọn bàn, nhập thông tin khách hàng và thêm món vào đơn\n" +
                        "2. Xem tổng tiền và áp dụng khuyến mãi (nếu có)\n" +
                        "3. Xác nhận đơn hàng\n" +
                        "4. Chọn phương thức thanh toán (tiền mặt/Chuyển khoản)\n" +
                        "5. In hóa đơn và hoàn tất"
        ));
    }

    private void khoiTaoDuLieuHelpCards() {
        helpCardList.add(new HelpCard(
                "📊", "Dashboard", "Theo dõi thống kê & báo cáo", "#3498db",
                "dashboard"
        ));

        helpCardList.add(new HelpCard(
                "👥", "Quản Lý Thành Viên", "Quản lý khách hàng là thành viên", "#9b59b6",
                "khachhang"
        ));

        helpCardList.add(new HelpCard(
                "🍽️", "Đặt Bàn", "Quy trình đặt bàn & phục vụ", "#e74c3c",
                "datban"
        ));

        helpCardList.add(new HelpCard(
                "📦", "Hóa Đơn", "Quản lý hóa đơn", "#f39c12",
                "hoadon"
        ));

        helpCardList.add(new HelpCard(
                "⚙️", "Tài khoản", "Vấn đề tài khoản", "#34495e",
                "caidat"
        ));

        helpCardList.add(new HelpCard(
                "📱", "Phím tắt", "Hướng dẫn dùng phím tắt", "#1abc9c",
                "phimtat"
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

        pane.getStyleClass().add("faq-pane");
        // Content
        VBox content = new VBox(10);
        content.setPadding(new Insets(15));

        TextFlow textFlow = new TextFlow();
        for (String line : faq.getAnswer().split("\n")) {
            Text text = new Text(line + "\n");
            text.getStyleClass().add("faq-text");
            textFlow.getChildren().add(text);
        }

        content.getChildren().add(textFlow);
        pane.setContent(content);

        // Icon mở / đóng
        Label arrow = new Label("▶");
        arrow.getStyleClass().add("faq-arrow");
        pane.setGraphic(arrow);

        pane.expandedProperty().addListener((obs, oldVal, newVal) -> {
            arrow.setText(newVal ? "▼" : "▶");
        });

        return pane;
    }

    private VBox taoHelpCard(HelpCard card) {

        VBox cardBox = new VBox(15);
        cardBox.setAlignment(Pos.CENTER);
        cardBox.setMinWidth(250);
        cardBox.setPrefWidth(250);
        cardBox.setMaxWidth(Double.MAX_VALUE);
        cardBox.setPadding(new Insets(20));

        cardBox.getStyleClass().add("help-card");

        // Icon
        Label iconLabel = new Label(card.getIcon());
        iconLabel.getStyleClass().add("help-card-icon");

        // Title
        Label titleLabel = new Label(card.getTitle());
        titleLabel.getStyleClass().add("help-card-title");

        // Description
        Label descLabel = new Label(card.getDescription());
        descLabel.setWrapText(true);
        descLabel.setMaxWidth(200);
        descLabel.getStyleClass().add("help-card-desc");

        // Button
        Button actionBtn = new Button("Xem hướng dẫn");
        actionBtn.getStyleClass().add("help-card-button");

        // Màu động từ model
        actionBtn.setStyle("-fx-background-color: " + card.getColor() + ";");

        // Sự kiện
        actionBtn.setOnAction(e -> moHuongDanChiTiet(card));
        cardBox.setOnMouseClicked(e -> moHuongDanChiTiet(card));

        cardBox.getChildren().addAll(
                iconLabel,
                titleLabel,
                descLabel,
                actionBtn
        );

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

    private void moHuongDanChiTiet(HelpCard card) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Hướng dẫn chi tiết");
        alert.setHeaderText("Hướng dẫn: " + card.getTitle());

        // Nội dung hướng dẫn chi tiết cho từng module
        String content = "";
        switch (card.getTag()) {
            case "dashboard":
                content = "Dashboard cung cấp:\n\n" +
                        "- Thông tin nhân viên đăng nhập\n" +
                        "- Thông báo các đơn đặt bàn, check in\n" +
                        "- Thống kê số đơn\n" +
                        "- Thống kê khu vực\n" +
                        "- Thống kê doanh thu và số khách\n" +
                        "- Biểu đồ top 5 món bán chạy\n" +
                        "- Biểu đồ lượng khách theo giờ\n";
                break;
            case "khachhang":
                content = "Quản lý thành viên:\n\n" +
                        "1. Tự động thêm khách hàng mới khi đặt bàn\n" +
                        "2. Thêm khách hàng bằng tay:\n" +
                        "   - Bước 1: Nhấn nút + bên góc phải trên màn hình\n" +
                        "   - Bước 2: Nhập thông tin khách hàng\n" +
                        "   - Bước 3: Nhấn thêm\n" +
                        "3. Sửa khách hàng\n" +
                        "   - Bước 1: Nhấn vào khách hàng cần sửa\n" +
                        "   - Bước 2: Nhập thông tin mới vào form\n" +
                        "   - Bước 3: Nhấn xác nhận\n" +
                        "4. Tìm kiếm khách hàng\n" +
                        "   - Bước 1: Nhập sdt hoặc mã khách hàng vào ô tìm kiếm\n" +
                        "   - Bước 2: Nhấn tìm kiếm\n";
                break;
            case "datban":
                content = "Quản lý đặt bàn:\n\n" +
                        "1. Đặt bàn\n" +
                        "   - Bước 1: Nhập số lượng người và chọn thời gian đặt bàn\n" +
                        "   - Bước 2: Chọn loại bàn phù hợp\n" +
                        "   - Bước 3: Hệ thống tự chuyển sang trang chọn món\n" +
                        "   - Bước 4: Nhập thông tin khách hàng\n" +
                        "   - Bước 5: Chọn món ăn và số lượng\n" +
                        "   - Bước 6: Nhấn đặt bàn\n" +
                        "2. Check In\n" +
                        "   - Bước 1: Trang này hiển thị 2 danh sách đơn đặt trước và danh sách chờ\n" +
                        "   - Bước 2: Chọn đơn cần check in, xem thông tin đơn ở from bên phải\n" +
                        "   - Bước 3: Nhấn checkin\n" +
                        "   - Bước 4: Nhập các thông tin cần lọc để tìm kiếm đơn ở bên dưới trang\n" +
                        "3. Check Out\n" +
                        "   - Bước 1: Trang hiển thị danh sách các hóa đơn chưa check out\n" +
                        "   - Bước 2: Nhập thông tin tìm kiếm đơn vào ô tìm kiếm trên cùng\n" +
                        "   - Bước 3: Chọn một đơn cầnc check out\n" +
                        "   - Bước 4: Khi chọn đơn sẽ hiển thị danh sách các món ăn bên dưới và thông tin đơn hàng bên phải\n" +
                        "   - Bước 5: Chọn phương thức thanh toán chuyển khoản hay tiền mặt\n" +
                        "   - Bước 6: Thực hiện thanh toán và nhấn nút thanh toán\n" +
                        "4. Cập nhật đơn bàn\n" +
                        "   - Bước 1: Trang hiển thị danh sách các đơn đặt trước và đơn đã nhận\n" +
                        "   - Bước 2: Chọn một đơn sẽ hiển thị thông tin đơn bên phải\n" +
                        "   - Bước 3: Có thể thay đổi món ăn cho đơn, nhưng không thể xóa món của đơn đã nhận\n" +
                        "   - Bước 4: Chỉ có thể hủy được đơn đặt trước";
                break;
            case "hoadon":
                content = "Quản lý hóa đơn:\n" +
                        "1. Trang hiển thị danh sách các hoá đơn:\n" +
                        "   - Có 3 trạng thái: Đặt trước, đang phục vụ, đã thanh toán\n" +
                        "2. Khi nhấn vào đơn sẽ hiển thị thông tin đơn đó bên phải\n" +
                        "   - Bước 1: Nhấn nút in hóa đơn nếu muốn\n" +
                        "3. Tìm kiếm hóa đơn\n" +
                        "   - Bước 1: Nhập thông tin đơn cần tìm\n" +
                        "   - Bước 2: Nhấn nút tìm kiếm\n" +
                        "   - Bước 3: Có thể nhấn nút xóa trắng để nhập thông tin khác\n";
                break;
            case "caidat":
                content = "Vấn đề tài khoản:\n" +
                        "1. Có thể thay đổi mật khẩu bằng cách nhấn nút đổi mật khẩu ở trang dashboard\n" +
                        "2. Khi đăng nhập nếu quên mật khẩu thì nhấn nút quên mật khẩu để thay đổi\n";
                break;
            case "phimtat":
                content = "Check in:\n" +
                        "- Ctrl F : tìm số điện thoại\n" +
                        "- Ctrl B: Check in\n" +
                        "- Ctrl L: Clear thông tin\n" +
                        "\n" +
                        "Check out:\n" +
                        "- Ctrl F: tìm kiếm hóa đơn\n" +
                        "- Ctrl B: check out\n" +
                        "\n" +
                        "Chọn món:\n" +
                        "- Ctrl F: Tìm kiếm món ăn\n" +
                        "- Ctrl D: Điền sđt khách hàng\n" +
                        "- Ctrl B: Đặt bàn\n" +
                        "\n" +
                        "Đặt bàn:\n" +
                        "- Ctrl D: Nhập số lượng chỗ\n" +
                        "\n" +
                        "QL Thành Viên:\n" +
                        "- Ctrl F: Tìm kiếm thành viên\n" +
                        "- Ctrl N: Thêm thành viên mới\n" +
                        "\n" +
                        "Tra cứu hóa đơn:\n" +
                        "- Ctrl D: Nhập số điện thoại\n" +
                        "- Ctrl F: Tìm kiếm\n" +
                        "- Ctrl L: Xóa trắng\n" +
                        "- Ctrl P: In hóa đơn\n" +
                        "\n" +
                        "Chuyển Trang NV:\n" +
                        "Phím 1: Dashboard\n" +
                        "Phím 2: \n" +
                        "Phím 3: QL thành viên\n" +
                        "Phím 4: Tra cứu hóa đơn\n" +
                        "Phím 5: Hỗ trợ\n" +
                        "Phím 6: Bàn giao ca\n" +
                        "Phím F1: Đặt bàn\n" +
                        "Phím F2: Check in\n" +
                        "Phím F3: Check out\n" +
                        "Phím F4: QL Đặt bàn\n";
                break;
            default:
                content = "Hướng dẫn chi tiết cho " + card.getTitle() + " đang được cập nhật.";

        }
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

        // ===== Root =====
        VBox root = new VBox(15);
        root.setPadding(new Insets(25));
        root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("custom-dialog-root");

        // ===== Title =====
        Label lblTitle = new Label(title);
        lblTitle.getStyleClass().add("custom-dialog-title");

        // ===== Content =====
        Label lblContent = new Label(content);
        lblContent.setWrapText(true);
        lblContent.setMaxWidth(450);
        lblContent.getStyleClass().add("custom-dialog-content");

        // ===== ScrollPane =====
        ScrollPane scrollPane = new ScrollPane(lblContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefViewportHeight(300);
        scrollPane.getStyleClass().add("custom-dialog-scroll");

        // ===== Button =====
        Button btnClose = new Button("Đóng");
        btnClose.getStyleClass().add("custom-dialog-button");
        btnClose.setOnAction(e -> dialog.close());

        // ===== Add =====
        root.getChildren().addAll(lblTitle, scrollPane, btnClose);

        Scene scene = new Scene(root, 520, 450);
        scene.getStylesheets().add(
                getClass().getResource("/CSS/hotronv.css").toExternalForm()
        );
        dialog.setScene(scene);

        dialog.setResizable(false);
        dialog.showAndWait();
    }



}