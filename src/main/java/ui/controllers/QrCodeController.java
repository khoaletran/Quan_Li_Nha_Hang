package ui.controllers;

import com.github.sarxos.webcam.WebcamPanel;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.github.sarxos.webcam.Webcam;


import javax.swing.*;
import java.awt.image.BufferedImage;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;


public class QrCodeController {

    public static Path generateQRCodeKM(String maKhuyenMai, String fileName, int size) throws Exception {
        String outputDir = "src/main/resources/IMG/qrcode";
        Path dir = Paths.get(outputDir);

        // Tạo thư mục nếu chưa tồn tại
        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        }

        String text = maKhuyenMai;

        // Thiết lập cấu hình mã hóa
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
        hints.put(EncodeHintType.MARGIN, 1);

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, size, size, hints);

        if (!fileName.toLowerCase().endsWith(".png")) {
            fileName += ".png";
        }

        Path filePath = dir.resolve(fileName);
        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", filePath);

        return filePath;
    }


    public static String scanQRCode() {
        String resultText = null;

        try {
            Webcam webcam = Webcam.getDefault();
            webcam.open();

            while (true) {
                BufferedImage image = webcam.getImage();
                if (image == null) continue;

                LuminanceSource source = new BufferedImageLuminanceSource(image);
                BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
                Result result = null;

                try {
                    result = new MultiFormatReader().decode(bitmap);
                } catch (NotFoundException e) {
                }

                if (result != null) {
                    resultText = result.getText();
                    break;
                }

                Thread.sleep(200);
            }

            webcam.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultText;
    }

    public static String scanQRCodeWithPreview() {
        AtomicBoolean running = new AtomicBoolean(true);
        String resultText = null;
        Webcam webcam = Webcam.getDefault();
        webcam.setViewSize(new java.awt.Dimension(640, 480));

        JFrame window = new JFrame("Camera - Quét mã QR");
        window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        window.setSize(650, 500);

        WebcamPanel panel = new WebcamPanel(webcam);
        panel.setMirrored(true);
        window.add(panel);
        window.setVisible(true);
        window.setLocationRelativeTo(null);

        // ✅ Khi người dùng bấm nút tắt (dấu X)
        window.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                running.set(false); // báo cho thread dừng
                webcam.close();
                window.dispose();
            }
        });

        try {
            while (running.get()) { // ✅ chỉ chạy khi chưa tắt
                BufferedImage image = webcam.getImage();
                if (image == null) continue;

                LuminanceSource source = new BufferedImageLuminanceSource(image);
                BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
                Result result = null;

                try {
                    result = new MultiFormatReader().decode(bitmap);
                } catch (NotFoundException e) {
                    // không tìm thấy mã thì bỏ qua
                }

                if (result != null) {
                    resultText = result.getText();
                    break;
                }

                Thread.sleep(200);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (webcam.isOpen()) webcam.close();
            window.dispose();
        }

        return resultText;
    }

}
