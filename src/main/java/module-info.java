module org.cockyadolescents.truechessthegame {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires javafx.media;


    opens cockyadolescents.truechessthegame to javafx.fxml;
    exports cockyadolescents.truechessthegame;
    exports cockyadolescents.truechessthegame.serverTest;
    opens cockyadolescents.truechessthegame.serverTest to javafx.fxml;
}