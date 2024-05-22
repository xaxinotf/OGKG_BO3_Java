module com.example.ogkg_bo3 {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.ogkg_bo3 to javafx.fxml;
    exports com.example.ogkg_bo3;
}