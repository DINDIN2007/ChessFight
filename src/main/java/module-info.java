module org.cockyadolescents.truechessthegame {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.cockyadolescents.truechessthegame to javafx.fxml;
    exports org.cockyadolescents.truechessthegame;
}