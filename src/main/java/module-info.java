module com.example.quan_li_nha_hang {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires java.desktop;
    requires java.sql;
    requires javafx.graphics;
    requires com.google.zxing.javase;
    requires com.google.zxing;
    requires webcam.capture;
    requires javafx.swing;


    opens ui to javafx.fxml, javafx.graphics;
    opens ui.controllers to javafx.fxml;
    exports ui;
}