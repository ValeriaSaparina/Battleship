module com.example.semestrovkalast {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.semestrovkalast to javafx.fxml;
    exports com.example.semestrovkalast;
}