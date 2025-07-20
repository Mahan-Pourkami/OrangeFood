module org.example.client {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires java.net.http;
    requires org.json;
    requires com.fasterxml.jackson.databind;
    requires java.desktop;
    requires java.sql;
    opens Model to javafx.base;

    opens org to javafx.fxml;
    exports org;
    exports Controller;
    opens Controller to javafx.fxml;
    exports Controller.Admin;
    opens Controller.Admin to javafx.fxml;
    exports Controller.Buyer;
    opens Controller.Buyer to javafx.fxml;
    exports Controller.Vendor;
    opens Controller.Vendor to javafx.fxml;
    exports Controller.Courier to javafx.fxml;
    opens Controller.Courier to javafx.fxml;

}