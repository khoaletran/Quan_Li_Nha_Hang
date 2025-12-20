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
                        "• Kiểm tra lại bộ lọc ngày/tháng"
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
        //moi
        actionBtn.setUserData(card.getColor());


        // Màu động từ model
//        actionBtn.setStyle("-fx-background-color: " + card.getColor() + ";");
        actionBtn.setStyle("-card-color: " + card.getColor() + ";");

        // Sự kiện
        actionBtn.setOnAction(e -> moHuongDanChiTiet(card));
//        cardBox.setOnMouseClicked(e -> moHuongDanChiTiet(card));

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
        showCustomDialog(card.getTitle(), taoNoiDungHuongDanVBox(card));
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
    private void showCustomDialog(String title, VBox contentBox) {

        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Hướng dẫn: " + title);

        VBox root = new VBox(15);
        root.setPadding(new Insets(25));
        root.setAlignment(Pos.CENTER);

        Label lblTitle = new Label("Hướng dẫn: " + title);
        lblTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        ScrollPane scrollPane = new ScrollPane(contentBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefViewportHeight(320);

        Button btnClose = new Button("Đóng");
        btnClose.getStyleClass().add("custom-dialog-button");
        btnClose.setOnAction(e -> dialog.close());

        root.getChildren().addAll(lblTitle, scrollPane, btnClose);

        Scene scene = new Scene(root, 550, 480);
        scene.getStylesheets().add(
                getClass().getResource("/CSS/hotronv.css").toExternalForm()
        );



        dialog.setScene(scene);
        dialog.setResizable(false);
        dialog.showAndWait();

    }

    private VBox taoNoiDungHuongDanVBox(HelpCard card) {

        VBox box = new VBox(6);
        box.setPadding(new Insets(10));
        box.setMaxWidth(480);

        switch (card.getTag()) {

            case "dashboard":
                box.getChildren().addAll(
                        tieuDeLbl("Dashboard cung cấp:"),

                        noiDungLbl("- Thông tin nhân viên đăng nhập"),
                        noiDungLbl("- Thông báo các đơn đặt bàn, check in"),
                        noiDungLbl("- Thống kê số đơn"),
                        noiDungLbl("- Thống kê khu vực"),
                        noiDungLbl("- Thống kê doanh thu và số khách"),
                        noiDungLbl("- Biểu đồ top 5 món bán chạy"),
                        noiDungLbl("- Biểu đồ lượng khách theo giờ")
                );
                break;

            case "khuyenmai":
                box.getChildren().addAll(
                        tieuDeLbl("Quản lý khuyến mãi:"),

                        mucLonLbl("1. Trang hiện danh sách các khuyến mãi"),
                        noiDungLbl("- Có 3 loại khuyến mãi: Chưa tới hạn màu vàng, hết hạn màu đỏ và đang trong hạn màu xanh"),

                        mucLonLbl("2. Thêm khuyến mãi"),
                        noiDungLbl("- Bước 1: Nhập các thông tin khuyến mãi vào form bên phải"),
                        noiDungLbl("- Bước 2: Nhấn nút thêm"),

                        mucLonLbl("3. Khi nhấn vào một khuyến mãi"),
                        noiDungLbl("- Bước 1: Thông tin khuyến mãi sẽ hiển thị bên phải"),
                        noiDungLbl("- Bước 2: Có thể nhập thông tin mới và nhấn nút sửa"),
                        noiDungLbl("- Bước 3: Có thể nhấn nút xóa khuyến mãi"),
                        noiDungLbl("- Bước 4: Có thể nhấn nút in QR cho khuyến mãi"),

                        mucLonLbl("4. Tìm kiếm khuyến mãi"),
                        noiDungLbl("- Bước 1: Nhập thông tin khuyến mãi cần tìm ở bộ lọc bên dưới"),
                        noiDungLbl("- Bước 2: Nhấn nút tìm kiếm"),
                        noiDungLbl("- Bước 3: Nhấn nút xóa trắng để làm mới bộ lọc")
                );
                break;

            case "nhanvien":
                box.getChildren().addAll(
                        tieuDeLbl("Quản lý nhân viên:"),

                        mucLonLbl("1. Trang hiển thị danh sách nhân viên"),

                        mucLonLbl("2. Thêm nhân viên"),
                        noiDungLbl("- Bước 1: Nhấn nút dấu + cột bên góc phải trang"),
                        noiDungLbl("- Bước 2: Nhập thông tin nhân viên vào form bên phải"),
                        noiDungLbl("- Bước 3: Nhấn nút xác nhận"),

                        mucLonLbl("3. Khi nhấn vào một nhân viên"),
                        noiDungLbl("- Bước 1: Nhập thông tin mới cần sửa vào formn"),
                        noiDungLbl("- Bước 2: Nhấn nút lưu thay đổi"),
                        noiDungLbl("- Bước 3: Có thể nhấn nút xóa nhân viên"),

                        mucLonLbl("4. Tìm kiếm nhân viên"),
                        noiDungLbl("- Bước 1: Nhập thông tin nhân viên cần tìm vào ô tìm kiếm ở trên"),
                        noiDungLbl("- Bước 2: Nhấn nút tìm")
                );
                break;

            case "menu":
                box.getChildren().addAll(
                        tieuDeLbl("Quản lý menu:"),

                        mucLonLbl("1. Trang hiển thị danh sách món ăn"),

                        mucLonLbl("2. Thêm món mới"),
                        noiDungLbl("- Bước 1: Nhấn nút dấu + cột bên góc phải trang"),
                        noiDungLbl("- Bước 2: Nhập thông tin món vào form bên phải"),
                        noiDungLbl("- Bước 3: Nhấn nút thêm mới"),

                        mucLonLbl("3. Khi nhấn vào một món ăn"),
                        noiDungLbl("- Bước 1: Nhập thông tin mới cần sửa vào formn"),
                        noiDungLbl("- Bước 2: Nhấn nút lưu thay đổi"),
                        noiDungLbl("- Bước 3: Có thể nhấn nút xóa món ăn"),

                        mucLonLbl("4. Tìm kiếm món ăn"),
                        noiDungLbl("- Bước 1: Nhập thông tin món cần tìm vào ô tìm kiếm ở trên"),
                        noiDungLbl("- Bước 2: Nhấn nút tìm"),
                        noiDungLbl("- Bước 3: Lọc các loại món ăn ở ô combobox trên thanh tìm kiếm")
                );
                break;

            case "ban":
                box.getChildren().addAll(
                        tieuDeLbl("Quản lý bàn:"),

                        mucLonLbl("1. Trang hiển thị danh sách các bàn của nhà hàng"),

                        mucLonLbl("2. Thêm bàn mới"),
                        noiDungLbl("- Bước 1: Nhấn nút dấu + cột bên góc phải trang"),
                        noiDungLbl("- Bước 2: Form thông tin sẽ hiện lên và nhập thông tin bàn vào form"),
                        noiDungLbl("- Bước 3: Nhấn nút thêm hoặc hủy (nếu không muốn thêm nữa)"),

                        mucLonLbl("3. Tìm kiếm bàn"),
                        noiDungLbl("- Bước 1: Nhập thông tin bàn cần tìm vào ô tìm kiếm ở trên"),
                        noiDungLbl("- Bước 2: Nhấn nút tìm"),
                        noiDungLbl("- Bước 3: Lọc các loại bàn hoặc khu vực ở ô combobox trên thanh tìm kiếm")
                );
                break;

            case "chinhsach":
                box.getChildren().addAll(
                        tieuDeLbl("Quản lý chính sách:"),

                        mucLonLbl("1. Cài đặt thời gian đợi bàn"),
                        noiDungLbl("- Bước 1: Nhập thời gian bàn đặt trước hoặc bàn đợi"),
                        noiDungLbl("- Bước 2: Nhấn xác nhận"),

                        mucLonLbl("2. Cập nhật tiền cọc"),
                        noiDungLbl("- Bước 1: Chọn loại bàn và khu vực ở bên phải"),
                        noiDungLbl("- Bước 2: Thông tin loại bàn đã chọn sẽ hiện trên form thông tin bên trái"),
                        noiDungLbl("- Bước 3: Nhập các thông tin cần sửa và nhấn xác nhận nếu muốn sửa"),
                        noiDungLbl("- Bước 4: Có thể nhấn nút xóa để xóa cọc"),
                        noiDungLbl("- Bước 5: Có thể nhấn nút xóa trắng để xóa dữ liệu trong form"),

                        mucLonLbl("3. Cập nhật phần trăm lời cho món"),
                        noiDungLbl("- Bước 1: Chọn món ăn ở bên phải"),
                        noiDungLbl("- Bước 2: Thông tin món ăn đã chọn sẽ hiện trên form thông tin bên trái"),
                        noiDungLbl("- Bước 3: Nhập các thông tin cần sửa và nhấn xác nhận nếu muốn sửa"),
                        noiDungLbl("- Bước 4: Có thể nhấn nút xóa trắng để xóa dữ liệu trong form")
                );
                break;

            case "thongke":
                box.getChildren().addAll(
                        tieuDeLbl("Báo cáo thống kê:"),

                        mucLonLbl("1. Trang hiển thị danh sách món ăn ở góc trái trên"),
                        noiDungLbl("- Bước 1: Hiển thị các món ăn và phân trăm bán ra so với tháng, năm trước"),
                        noiDungLbl("- Bước 2: Có thể thay đổi thời gian để so sánh ở 2 combobox bên trên"),
                        noiDungLbl("- Bước 3: Nhập thông tin để tìm món ăn ở ô tìm kiếm bên trên"),
                        noiDungLbl("- Bước 4: Có thể nhấn nút reset để quay lại thời gian hiện tại và hiển thị tất cả món"),

                        mucLonLbl("2. Trang hiển thị thông tin thống kê đối với tổng hóa đơn, doanh thu, doanh thu so với tháng trước, khu vực"),
                        noiDungLbl("- Bước 1: Có thể lọc thống kê theo ngày tháng năm ở các combobox bên trên"),
                        noiDungLbl("- Bước 2: Có thể nhấn nút reset để quay lại ngày hiện tại"),

                        mucLonLbl("3. Phía dưới là 2 biểu đồ thống kê doanh thu theo giờ và số lượng đơn theo ngày"),
                        noiDungLbl("- Bước 1: Có thể điều chỉnh thời gian của 2 biểu đồ bằng ô combobox ở trên")
                );
                break;

            case "caidat":
                box.getChildren().addAll(
                        tieuDeLbl("Vấn đề tài khoản:"),
                        noiDungLbl("1. Có thể thay đổi mật khẩu bằng cách nhấn nút đổi mật khẩu ở trang dashboard"),
                        noiDungLbl("2. Khi đăng nhập nếu quên mật khẩu thì nhấn nút quên mật khẩu để thay đổi")
                );
                break;

            case "phimtat":
                box.getChildren().addAll(
                        tieuDeLbl("Chính sách:"),
                        noiDungLbl("- Ctrl F: tìm kiếm món ăn"),

                        tieuDeLbl("Khuyến Mãi"),
                        noiDungLbl("- Ctrl F: Tìm kiếm khuyến mãi"),

                        tieuDeLbl("QL Bàn:"),
                        noiDungLbl("- Ctrl F: Tìm kiếm bàn"),
                        noiDungLbl("- Ctrl N: Thêm bàn mới"),

                        tieuDeLbl("QL Menu:"),
                        noiDungLbl("- Ctrl F: Tìm kiếm món ăn"),
                        noiDungLbl("- Ctrl N: Thêm món mới"),

                        tieuDeLbl("QL Nhân Viên:"),
                        noiDungLbl("- Ctrl F: Tìm kiếm nhân viên"),
                        noiDungLbl("- Ctrl N: Thêm nhân viên mới"),

                        tieuDeLbl("Thống kê:"),
                        noiDungLbl("- Ctrl F: Tìm món ăn"),

                        tieuDeLbl("Chuyển Trang QL:"),
                        noiDungLbl("Phím 1: Dashboard"),
                        noiDungLbl("Phím 2: QL Menu"),
                        noiDungLbl("Phím 3: QL Bàn"),
                        noiDungLbl("Phím 4: QL Nhân viên"),
                        noiDungLbl("Phím 5: Khuyến mãi"),
                        noiDungLbl("Phím 6: Chính sách"),
                        noiDungLbl("Phím 7: Thống kê"),
                        noiDungLbl("Phím 8: Hỗ trợ")
                );
                break;

            default:
                box.getChildren().add(
                        noiDungLbl("Hướng dẫn chi tiết cho " + card.getTitle() + " đang được cập nhật.")
                );
        }

        return box;
    }
        private Label tieuDeLbl(String text) {
            Label lb = new Label(text);
            lb.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
            return lb;
        }

        private Label mucLonLbl(String text) {
            Label lb = new Label(text);
            lb.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #2980b9;");
            return lb;
        }

        private Label noiDungLbl(String text) {
            Label lb = new Label(text);
            lb.setStyle("-fx-font-size: 14px; -fx-text-fill: #34495e;");
            lb.setWrapText(true);
            return lb;
        }


}