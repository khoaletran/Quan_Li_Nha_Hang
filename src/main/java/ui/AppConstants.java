package ui;

import javafx.scene.image.Image;

public class AppConstants {

    // 🦀 Thông tin thương hiệu
    public static final String APP_NAME = "CrabKing Restaurant";
    public static final String APP_SLOGAN = "Hệ thống nhà hàng CrabKing";
    public static final String APP_TITLE = "CrabKing";

    // 🧭 Đường dẫn tài nguyên
    public static final String LOGO_PATH = "/IMG/logo_1.png";
    public static final String ICON_PATH = "/IMG/icon/";
    public static final String STYLE_PATH = "/CSS/";

    // Ảnh
    public static final Image APP_LOGO = new Image(
            AppConstants.class.getResource(LOGO_PATH) != null
                    ? AppConstants.class.getResource(LOGO_PATH).toExternalForm()
                    : ""
    );

    // 🎨 Màu chủ đạo
    public static final String COLOR_PRIMARY = "#E6763E";   // cam chủ đạo
    public static final String COLOR_SECONDARY = "#F5C9A4"; // pastel cua
    public static final String COLOR_TEXT = "#6B240C";      // nâu đậm

    // ⚙️ Kích thước màn hình
    public static final int WINDOW_WIDTH = 1500;
    public static final int WINDOW_HEIGHT = 1000;

    private AppConstants() {
        // Chặn khởi tạo class (chỉ chứa hằng số)
    }
}
