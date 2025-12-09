module com.facebook {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;

    opens com.facebook to javafx.fxml;

    exports com.facebook;
}
