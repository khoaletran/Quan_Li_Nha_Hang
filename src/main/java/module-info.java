module com.example.quan_li_nha_hang {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.web;
    requires javafx.media;
    requires javafx.swing;

    requires java.desktop;
    requires java.sql;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;

    requires com.google.zxing;
    requires com.google.zxing.javase;
    requires webcam.capture;

    opens ui to javafx.fxml, javafx.graphics;
    opens ui.controllers to javafx.fxml;

    exports ui;
}
