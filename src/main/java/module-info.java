module com.example.di_ud67_gymfit {
    requires javafx.controls;
    requires javafx.fxml;

    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;

    opens com.example.di_ud67_gymfit to javafx.fxml;
    exports com.example.di_ud67_gymfit;
}