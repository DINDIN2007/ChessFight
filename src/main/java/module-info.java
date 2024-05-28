module org.cockyadolescents.truechessthegame {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens cockyadolescents.truechessthegame to javafx.fxml;
    exports cockyadolescents.truechessthegame;
}