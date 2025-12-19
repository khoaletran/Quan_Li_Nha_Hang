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
                "1. Vì sao tôi không chỉnh sửa được một số chức năng?",
                "• Tài khoản chưa được cấp quyền quản lý\n" +
                        "• Bạn cần đăng nhập bằng tài khoản nhân viên\n" +
                        "• Thử đăng xuất và đăng nhập lại"
        ));

        faqList.add(new FAQItem(
                "2. Có thể phân quyền chi tiết cho từng nhân viên không?",
                "• Hiện tại hệ thống phân quyền theo vai trò\n" +
                        "• Nhân viên chỉ thao tác nghiệp vụ\n" +
                        "• Quản lý có toàn quyền cấu hình hệ thống"
        ));

        faqList.add(new FAQItem(
                "3. Doanh thu hiển thị không khớp thực tế thì do đâu?",
                "• Hóa đơn chưa được thanh toán\n" +
                        "• Đang lọc sai thời gian thống kê\n" +
                        "• Có hóa đơn bị hủy hoặc hoàn tiền\n" +
                        "→ Kiểm tra lại bộ lọc ngày/tháng"
        ));

        faqList.add(new FAQItem(
                "4. Vì sao không thể xuất báo cáo?",
                "• Chưa chọn khoảng thời gian thống kê\n" +
                        "• Không có dữ liệu trong khoảng thời gian đó\n"
        ));

        faqList.add(new FAQItem(
                "5. Vì sao không thể xóa món ăn trong menu?",
                "• Món đã phát sinh đơn hàng\n" +
                        "• Món đang được áp dụng trong khuyến mãi\n" +
                        "• Món đang được sử dụng trong báo cáo thống kê"
        ));

        faqList.add(new FAQItem(
                "6. Khi thay đổi chính sách, đơn cũ có bị ảnh hưởng không?",
                "• Không ảnh hưởng đơn đã tạo\n" +
                        "• Chỉ áp dụng cho các đơn phát sinh sau thời điểm thay đổi"
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
                "🍽️", "Quản Lý Menu", "Quy trình thêm xóa sửa món", "#e74c3c",
                "menu"
        ));

        helpCardList.add(new HelpCard(
                "🍽️", "Quản Lý Bàn", "Quy trình thêm xóa sửa bàn", "#e74c3c",
                "ban"
        ));

        helpCardList.add(new HelpCard(
                "📦", "Chính Sách", "Quản lý chính sách nhà hàng", "#f39c12",
                "chinhsach"
        ));

        helpCardList.add(new HelpCard(
                "💰", "Báo Cáo Thống Kê", "Xuất báo cáo doanh thu", "#16a085",
                "thongke"
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

private TitledPane taoTitledPaneFAQ(HoTroControllerQL.FAQItem faq) {
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

    private VBox taoHelpCard(HoTroControllerQL.HelpCard card) {

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
        // TODO: Implement detailed guide opening
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Hướng dẫn chi tiết");
        alert.setHeaderText("Hướng dẫn: " + card.getTitle());

        // Nội dung hướng dẫn chi tiết cho từng module
        String content = "";
        switch (card.getTag()) {
            case "dashboard":
                content = "Dashboard cung cấp:\n" +
                        "- Thông tin nhân viên đăng nhập\n" +
                        "- Thông báo các đơn đặt bàn, check in\n" +
                        "- Thống kê số đơn\n" +
                        "- Thống kê khu vực\n" +
                        "- Thống kê doanh thu và số khách\n" +
                        "- Biểu đồ top 5 món bán chạy\n" +
                        "- Biểu đồ lượng khách theo giờ\n";
                break;
            case "khuyenmai":
                content = "Quản lý khuyến mãi:\n" +
                        "1. Trang hiện danh sách các khuyến mãi\n" +
                        "   - Có 3 loại khuyến mãi: Chưa tới hạn màu vàng, hết hạn màu đỏ và đang trong hạn màu xanh\n" +
                        "2. Thêm khuyến mãi\n" +
                        "   - Bước 1: Nhập các thông tin khuyến mãi vào form bên phải\n" +
                        "   - Bước 2: Nhấn nút thêm\n" +
                        "3. Khi nhấn vào một khuyến mãi\n" +
                        "   - Bước 1: Thông tin khuyến mãi sẽ hiển thị bên phải\n" +
                        "   - Bước 2: Có thể nhập thông tin mới và nhấn nút sửa\n" +
                        "   - Bước 3: Có thể nhấn nút xóa khuyến mãi\n" +
                        "   - Bước 4: Có thể nhấn nút in QR cho khuyến mãi\n" +
                        "4. Tìm kiếm khuyến mãi\n" +
                        "   - Bước 1: Nhập thông tin khuyến mãi cần tìm ở bộ lọc bên dưới\n" +
                        "   - Bước 2: Nhấn nút tìm kiếm\n" +
                        "   - Bước 3: Nhấn nút xóa trắng để làm mới bộ lọc";
                break;
            case "nhanvien":
                content = "Quản lý nhân viên:\n" +
                        "1. Trang hiển thị danh sách nhân viên\n" +
                        "2. Thêm nhân viên\n" +
                        "   - Bước 1: Nhấn nút dấu + cột bên góc phải trang\n" +
                        "   - Bước 2: Nhập thông tin nhân viên vào form bên phải\n" +
                        "   - Bước 3: Nhấn nút xác nhận\n" +
                        "3. Khi nhấn vào một nhân viên\n" +
                        "   - Bước 1: Nhập thông tin mới cần sửa vào formn\n" +
                        "   - Bước 2: Nhấn nút lưu thay đổi\n" +
                        "   - Bước 3: Có thể nhấn nút xóa nhân viên\n" +
                        "4. Tìm kiếm nhân viên\n" +
                        "   - Bước 1: Nhập thông tin nhân viên cần tìm vào ô tìm kiếm ở trên\n" +
                        "   - Bước 2: Nhấn nút tìm\n";
                break;
            case "menu":
                content = "Quản lý menu:\n" +
                        "1. Trang hiển thị danh sách món ăn\n" +
                        "2. Thêm món mới\n" +
                        "   - Bước 1: Nhấn nút dấu + cột bên góc phải trang\n" +
                        "   - Bước 2: Nhập thông tin món vào form bên phải\n" +
                        "   - Bước 3: Nhấn nút thêm mới\n" +
                        "3. Khi nhấn vào một món ăn\n" +
                        "   - Bước 1: Nhập thông tin mới cần sửa vào formn\n" +
                        "   - Bước 2: Nhấn nút lưu thay đổi\n" +
                        "   - Bước 3: Có thể nhấn nút xóa món ăn\n" +
                        "4. Tìm kiếm món ăn\n" +
                        "   - Bước 1: Nhập thông tin món cần tìm vào ô tìm kiếm ở trên\n" +
                        "   - Bước 2: Nhấn nút tìm\n" +
                        "   - Bước 3: Lọc các loại món ăn ở ô combobox trên thanh tìm kiếm\n";
                break;
            case "ban":
                content = "Quản lý bàn:\n" +
                        "1. Trang hiển thị danh sách các bàn của nhà hàng\n" +
                        "2. Thêm bàn mới\n" +
                        "   - Bước 1: Nhấn nút dấu + cột bên góc phải trang\n" +
                        "   - Bước 2: Form thông tin sẽ hiện lên và nhập thông tin bàn vào form\n" +
                        "   - Bước 3: Nhấn nút thêm hoặc hủy (nếu không muốn thêm nữa)\n" +
                        "3. Tìm kiếm bàn\n" +
                        "   - Bước 1: Nhập thông tin bàn cần tìm vào ô tìm kiếm ở trên\n" +
                        "   - Bước 2: Nhấn nút tìm\n" +
                        "   - Bước 3: Lọc các loại bàn hoặc khu vực ở ô combobox trên thanh tìm kiếm\n";
                break;
            case "chinhsach":
                content = "Quản lý chính sách:\n" +
                        "1. Cài đặt thời gian đợi bàn\n" +
                        "   - Bước 1: Nhập thời gian bàn đặt trước hoặc bàn đợi\n" +
                        "   - Bước 2: Nhấn xác nhận\n" +
                        "2. Cập nhật tiền cọc\n" +
                        "   - Bước 1: Chọn loại bàn và khu vực ở bên phải\n" +
                        "   - Bước 2: Thông tin loại bàn đã chọn sẽ hiện trên form thông tin bên trái\n" +
                        "   - Bước 3: Nhập các thông tin cần sửa và nhấn xác nhận nếu muốn sửa\n" +
                        "   - Bước 4: Có thể nhấn nút xóa để xóa cọc\n" +
                        "   - Bước 5: Có thể nhấn nút xóa trắng để xóa dữ liệu trong form\n" +
                        "3. Cập nhật phần trăm lời cho món\n" +
                        "   - Bước 1: Chọn món ăn ở bên phải\n" +
                        "   - Bước 2: Thông tin món ăn đã chọn sẽ hiện trên form thông tin bên trái\n" +
                        "   - Bước 3: Nhập các thông tin cần sửa và nhấn xác nhận nếu muốn sửa\n" +
                        "   - Bước 4: Có thể nhấn nút xóa trắng để xóa dữ liệu trong form\n";
                break;
            case "thongke":
                content = "Báo cáo thống kê:\n\n" +
                        "1. Trang hiển thị danh sách món ăn ở góc trái trên\n" +
                        "   - Bước 1: Hiển thị các món ăn và phân trăm bán ra so với tháng, năm trước\n" +
                        "   - Bước 2: Có thể thay đổi thời gian để so sánh ở 2 combobox bên trên\n" +
                        "   - Bước 3: Nhập thông tin để tìm món ăn ở ô tìm kiếm bên trên\n" +
                        "   - Bước 4: Có thể nhấn nút reset để quay lại thời gian hiện tại và hiển thị tất cả món\n" +
                        "2. Trang hiển thị thông tin thống kê đối với tổng hóa đơn, doanh thu, doanh thu so với tháng trước, khu vực\n" +
                        "   - Bước 1: Có thể lọc thống kê theo ngày tháng năm ở các combobox bên trên\n" +
                        "   - Bước 2: Có thể nhấn nút reset để quay lại ngày hiện tại\n" +
                        "3. Phía dưới là 2 biểu đồ thống kê doanh thu theo giờ và số lượng đơn theo ngày\n" +
                        "   - Bước 1: Có thể điều chỉnh thời gian của 2 biểu đồ bằng ô combobox ở trên\n";
                break;
            case "caidat":
                content = "Vấn đề tài khoản:\n" +
                        "1. Có thể thay đổi mật khẩu bằng cách nhấn nút đổi mật khẩu ở trang dashboard\n" +
                        "2. Khi đăng nhập nếu quên mật khẩu thì nhấn nút quên mật khẩu để thay đổi\n";
                break;
            case "phimtat":
                content = "Chính sách:\n" +
                        "- Ctrl F: tìm kiếm món ăn\n" +
                        "\n" +
                        "Khuyến Mãi\n" +
                        "- Ctrl F: Tìm kiếm khuyến mãi\n" +
                        "\n" +
                        "QL Bàn:\n" +
                        "- Ctrl F: Tìm kiếm bàn\n" +
                        "- Ctrl N: Thêm bàn mới\n" +
                        "\n" +
                        "QL Menu:\n" +
                        "- Ctrl F: Tìm kiếm món ăn\n" +
                        "- Ctrl N: Thêm món mới\n" +
                        "\n" +
                        "QL Nhân Viên:\n" +
                        "- Ctrl F: Tìm kiếm nhân viên\n" +
                        "- Ctrl N: Thêm nhân viên mới\n" +
                        "\n" +
                        "Thống kê:\n" +
                        "- Ctrl F: Tìm món ăn\n" +
                        "\n" +
                        "Chuyển Trang QL:\n" +
                        "Phím 1: Dashboard\n" +
                        "Phím 2: QL Menu\n" +
                        "Phím 3: QL Bàn\n" +
                        "Phím 4: QL Nhân viên\n" +
                        "Phím 5: Khuyến mãi\n" +
                        "Phím 6: Chính sách\n" +
                        "Phím 7: Thống kê\n" +
                        "Phím 8: Hỗ trợ\n" +
                        "\n";
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