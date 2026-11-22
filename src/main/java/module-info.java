module com.example.di_ud67_gymfit {
    requires javafx.controls;
    requires javafx.fxml;

    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fontawesome5;

    opens com.example.di_ud67_gymfit to javafx.fxml;
    exports com.example.di_ud67_gymfit;
    exports com.example.di_ud67_gymfit.model;
}
