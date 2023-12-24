module com.example.socialnetworkapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;

    opens com.example.socialnetworkapp to javafx.fxml;
    exports com.example.socialnetworkapp;

    opens com.example.socialnetworkapp.domain to javafx.fxml;
    exports com.example.socialnetworkapp.domain;

    opens com.example.socialnetworkapp.repository to javafx.fxml;
    exports com.example.socialnetworkapp.repository;

    opens com.example.socialnetworkapp.service to javafx.fxml;
    exports com.example.socialnetworkapp.service;

    opens com.example.socialnetworkapp.controller to javafx.fxml;
    exports com.example.socialnetworkapp.controller;
}