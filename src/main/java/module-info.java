module com.izinlapor {
    requires transitive javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.izinlapor to javafx.fxml;
    opens com.izinlapor.controller to javafx.fxml;
    opens com.izinlapor.model to javafx.base;
    
    exports com.izinlapor;
}
