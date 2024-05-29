module org.cockyadolescents.truechessthegame {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires javafx.media;


    opens cockyadolescents.truechessthegame to javafx.fxml;
    exports cockyadolescents.truechessthegame;
}