module com.facebook {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.facebook to javafx.fxml;

    exports com.facebook;
}
