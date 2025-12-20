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
                "1. Vì sao không thể check in đơn đặt bàn?",
                "• Đơn chưa đến thời gian hẹn\n" +
                        "• Đơn đã bị hủy hoặc đã check in trước đó\n" +
                        "• Bàn chưa sẵn sàng\n" +
                        "• Kiểm tra lại trạng thái đơn trong danh sách"
        ));

        faqList.add(new FAQItem(
                "2. Không tìm thấy đơn đặt bàn thì phải làm sao?",
                "• Kiểm tra đúng ngày đặt bàn\n" +
                        "• Kiểm tra số điện thoại khách hàng\n" +
                        "• Thử tìm theo mã đơn\n" +
                        "• Kiểm tra bộ lọc trạng thái đơn"
        ));

        faqList.add(new FAQItem(
                "3. Khi nào đơn đặt bàn bị tự động hủy?",
                "• Khách không đến sau thời gian hẹn cho phép\n" +
                        "• Nhân viên hủy đơn thủ công\n"
        ));

        faqList.add(new FAQItem(
                "4. Có thể chỉnh sửa đơn sau khi đã check in không?",
                "• Có thể thêm món ăn\n" +
                        "• Không thể xóa món đã phục vụ\n" +
                        "• Không thể xóa đơn bàn đã nhận"
        ));

        faqList.add(new FAQItem(
                "5. Vì sao tôi không truy cập được một số chức năng?",
                "• Tài khoản chưa được cấp quyền\n" +
                        "• Bạn đang đăng nhập sai vai trò\n" +
                        "• Tài khoản bị khóa tạm thời\n" +
                        "→ Liên hệ quản lý để kiểm tra"
        ));

        faqList.add(new FAQItem(
                "6. Có mẹo nào thao tác nhanh hơn không?",
                "• Sử dụng phím tắt thay vì chuột\n" +
                        "• Tìm kiếm bằng số điện thoại\n" +
                        "• Xóa bộ lọc khi không cần thiết"
        ));

        faqList.add(new FAQItem(
                "7. Thanh toán xong có sửa lại hóa đơn được không?",
                "• Không thể chỉnh sửa hóa đơn đã thanh toán\n" +
                        "• Chỉ có thể xem hoặc in lại hóa đơn"
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

            case "khachhang":
                box.getChildren().addAll(
                        tieuDeLbl("Quản lý thành viên:"),

                        mucLonLbl("1. Tự động thêm khách hàng mới khi đặt bàn"),

                        mucLonLbl("2. Thêm khách hàng bằng tay:"),
                        noiDungLbl("- Bước 1: Nhấn nút + bên góc phải trên màn hình"),
                        noiDungLbl("- Bước 2: Nhập thông tin khách hàng"),
                        noiDungLbl("- Bước 3: Nhấn thêm"),

                        mucLonLbl("3. Sửa khách hàng"),
                        noiDungLbl("- Bước 1: Nhấn vào khách hàng cần sửa"),
                        noiDungLbl("- Bước 2: Nhập thông tin mới vào form"),
                        noiDungLbl("- Bước 3: Nhấn xác nhận"),

                        mucLonLbl("4. Tìm kiếm khách hàng"),
                        noiDungLbl("- Bước 1: Nhập sdt hoặc mã khách hàng vào ô tìm kiếm"),
                        noiDungLbl("- Bước 2: Nhấn tìm kiếm")
                );
                break;

            case "datban":
                box.getChildren().addAll(
                        tieuDeLbl("Quản lý đặt bàn:"),

                        mucLonLbl("1. Đặt bàn"),
                        noiDungLbl("- Bước 1: Nhập số lượng người và chọn thời gian đặt bàn"),
                        noiDungLbl("- Bước 2: Chọn loại bàn phù hợp"),
                        noiDungLbl("- Bước 3: Hệ thống tự chuyển sang trang chọn món"),
                        noiDungLbl("- Bước 4: Nhập thông tin khách hàng"),
                        noiDungLbl("- Bước 5: Chọn món ăn và số lượng"),
                        noiDungLbl("- Bước 6: Nhấn đặt bàn"),

                        mucLonLbl("2. Check In"),
                        noiDungLbl("- Bước 1: Hiển thị danh sách đơn đặt trước và danh sách chờ"),
                        noiDungLbl("- Bước 2: Chọn đơn cần check in, xem thông tin bên phải"),
                        noiDungLbl("- Bước 3: Nhấn check in"),
                        noiDungLbl("- Bước 4: Nhập thông tin lọc để tìm kiếm đơn"),

                        mucLonLbl("3. Check Out"),
                        noiDungLbl("- Bước 1: Trang hiển thị danh sách hóa đơn chưa check out"),
                        noiDungLbl("- Bước 2: Nhập thông tin tìm kiếm vào ô tìm kiếm trên cùng"),
                        noiDungLbl("- Bước 3: Chọn đơn cần check out"),
                        noiDungLbl("- Bước 4: Khi chọn đơn sẽ hiển thị danh sách các món ăn bên dưới và thông tin đơn hàng bên phải"),
                        noiDungLbl("- Bước 5: Chọn phương thức thanh toán"),
                        noiDungLbl("- Bước 6: Thực hiện thanh toán và Nhấn thanh toán"),

                        mucLonLbl("4. Cập nhật đơn bàn"),
                        noiDungLbl("- Bước 1: Trang hiển thị đơn đặt trước và đơn đã nhận"),
                        noiDungLbl("- Bước 2: Chọn đơn để xem thông tin ở form bên phải"),
                        noiDungLbl("- Bước 3: Có thể đổi món, nhưng không thể xóa món của đơn đã nhận"),
                        noiDungLbl("- Bước 4: Chỉ hủy được đơn đặt trước")
                );
                break;

            case "hoadon":
                box.getChildren().addAll(
                        tieuDeLbl("Quản lý hóa đơn:"),

                        mucLonLbl("1. Trang hiển thị danh sách các hóa đơn"),
                        noiDungLbl("- Có 3 trạng thái: Đặt trước, đang phục vụ, đã thanh toán"),

                        mucLonLbl("2. Xem chi tiết hóa đơn"),
                        noiDungLbl("- Nhấn vào đơn để xem thông tin bên phải"),
                        noiDungLbl("- Nhấn nút in hóa đơn nếu cần"),

                        mucLonLbl("3. Tìm kiếm hóa đơn"),
                        noiDungLbl("- Bước 1: Nhập thông tin cần tìm"),
                        noiDungLbl("- Bước 2: Nhấn tìm kiếm"),
                        noiDungLbl("- Bước 3: Có thể xóa trắng để tìm lại")
                );
                break;

            case "caidat":
                box.getChildren().addAll(
                        tieuDeLbl("Vấn đề tài khoản:"),

                        noiDungLbl("1. Có thể đổi mật khẩu tại trang dashboard"),
                        noiDungLbl("2. Khi đăng nhập nếu quên mật khẩu thì nhấn nút quên mật khẩu để thay đổi")
                );
                break;

            case "phimtat":
                box.getChildren().addAll(
                        tieuDeLbl("Check in:"),
                        noiDungLbl("- Ctrl + F : Tìm số điện thoại"),
                        noiDungLbl("- Ctrl + B : Check in"),
                        noiDungLbl("- Ctrl + L : Clear thông tin"),

                        tieuDeLbl("Check out:"),
                        noiDungLbl("- Ctrl + F : Tìm kiếm hóa đơn"),
                        noiDungLbl("- Ctrl + B : Check out"),

                        tieuDeLbl("Chọn món:"),
                        noiDungLbl("- Ctrl + F : Tìm món ăn"),
                        noiDungLbl("- Ctrl + D : Điền SĐT khách hàng"),
                        noiDungLbl("- Ctrl + B : Đặt bàn"),

                        tieuDeLbl("Đặt bàn:"),
                        noiDungLbl("- Ctrl + D : Nhập số lượng chỗ"),

                        tieuDeLbl("QL Thành Viên:"),
                        noiDungLbl("- Ctrl + F : Tìm kiếm"),
                        noiDungLbl("- Ctrl + N : Thêm mới"),

                        tieuDeLbl("Tra cứu hóa đơn:"),
                        noiDungLbl("- Ctrl + D : Nhập số điện thoại"),
                        noiDungLbl("- Ctrl + F : Tìm kiếm"),
                        noiDungLbl("- Ctrl + L : Xóa trắng"),
                        noiDungLbl("- Ctrl + P : In hóa đơn"),

                        tieuDeLbl("Chuyển trang NV:"),
                        noiDungLbl("Phím 1 : Dashboard"),
                        noiDungLbl("Phím 3 : QL thành viên"),
                        noiDungLbl("Phím 4 : Tra cứu hóa đơn"),
                        noiDungLbl("Phím 5 : Hỗ trợ"),
                        noiDungLbl("Phím 6 : Bàn giao ca"),
                        noiDungLbl("F1 : Đặt bàn"),
                        noiDungLbl("F2 : Check in"),
                        noiDungLbl("F3 : Check out"),
                        noiDungLbl("F4 : QL Đặt bàn")
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