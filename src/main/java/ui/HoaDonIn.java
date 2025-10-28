package ui;

import entity.HoaDon;
import javafx.fxml.FXMLLoader;
import javafx.print.PrinterJob;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ui.controllers.HoaDonInController;

import java.io.File;

/**
 * Tiện ích in hóa đơn bằng JavaFX FXML template
 * Hỗ trợ: Xem trước, In PDF (đặt tên = mã hóa đơn), In nhanh không hộp thoại
 */
public class HoaDonIn {

    /**
     * 🔹 In hóa đơn có hộp thoại chọn máy in (có thể chọn Microsoft Print to PDF)
     * Gợi ý tên file PDF theo mã hóa đơn (VD: HD2410250001.pdf)
     */
    public static void inHoaDonFXML(HoaDon hd) {
        try {
            if (hd == null) {
                System.err.println("⚠️ Không có dữ liệu hóa đơn để in!");
                return;
            }

            FXMLLoader loader = new FXMLLoader(HoaDonIn.class.getResource("/FXML/HoaDonIn.fxml"));
            VBox hoaDonUI = loader.load();

            HoaDonInController ctrl = loader.getController();
            ctrl.setData(hd);

            PrinterJob job = PrinterJob.createPrinterJob();
            if (job == null) {
                System.err.println("❌ Không khởi tạo được PrinterJob!");
                return;
            }

            // Hộp thoại lưu file PDF (chỉ dùng khi chọn Print to PDF)
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Lưu hóa đơn");
            chooser.setInitialFileName(hd.getMaHD() + ".pdf");
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
            File file = chooser.showSaveDialog(null);

            if (file == null) {
                System.out.println("🟡 Hủy in hóa đơn.");
                return;
            }

            job.getJobSettings().setJobName(hd.getMaHD());
            boolean success = job.showPrintDialog(null) && job.printPage(hoaDonUI);
            if (success) {
                job.endJob();
                System.out.println("✅ In hóa đơn " + file.getName() + " thành công!");
            } else {
                System.err.println("❌ In hóa đơn thất bại!");
            }

        } catch (Exception e) {
            System.err.println("💥 Lỗi khi in hóa đơn:");
            e.printStackTrace();
        }
    }

    /**
     * 🔹 In nhanh không hiển thị hộp thoại chọn máy in (in ra máy mặc định)
     */
    public static void inHoaDonNhanh(HoaDon hd) {
        try {
            if (hd == null) {
                System.err.println("⚠️ Không có dữ liệu hóa đơn để in!");
                return;
            }

            FXMLLoader loader = new FXMLLoader(HoaDonIn.class.getResource("/FXML/HoaDonIn.fxml"));
            VBox hoaDonUI = loader.load();

            HoaDonInController ctrl = loader.getController();
            ctrl.setData(hd);

            PrinterJob job = PrinterJob.createPrinterJob();
            if (job != null) {
                job.getJobSettings().setJobName(hd.getMaHD());
                boolean success = job.printPage(hoaDonUI);
                if (success) {
                    job.endJob();
                    System.out.println("✅ In nhanh hóa đơn " + hd.getMaHD() + " thành công!");
                } else {
                    System.err.println("❌ In nhanh thất bại.");
                }
            } else {
                System.err.println("⚠️ Không có máy in mặc định!");
            }

        } catch (Exception e) {
            System.err.println("💥 Lỗi khi in nhanh hóa đơn:");
            e.printStackTrace();
        }
    }

    /**
     * 🔹 Xem trước hóa đơn trên giao diện (không in)
     */
    public static void previewHoaDon(HoaDon hd) {
        try {
            if (hd == null) {
                System.err.println("⚠️ Không có dữ liệu hóa đơn để xem trước!");
                return;
            }

            FXMLLoader loader = new FXMLLoader(HoaDonIn.class.getResource("/FXML/HoaDonIn.fxml"));
            VBox root = loader.load();

            HoaDonInController ctrl = loader.getController();
            ctrl.setData(hd);

            Stage stage = new Stage();
            stage.setTitle("🧾 Xem trước hóa đơn - " + hd.getMaHD());
            stage.setScene(new Scene(root));
            stage.show();

            System.out.println("👁️ Đã hiển thị xem trước hóa đơn " + hd.getMaHD());

        } catch (Exception e) {
            System.err.println("💥 Lỗi khi xem trước hóa đơn:");
            e.printStackTrace();
        }
    }
}
