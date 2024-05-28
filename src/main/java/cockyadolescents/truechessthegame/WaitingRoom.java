package cockyadolescents.truechessthegame;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;

import java.io.IOException;

// Online game
public class WaitingRoom {
    Parent root;
    Scene scene;

    public void queue() throws IOException {
        root = FXMLLoader.load(getClass().getResource("waitingroom.fxml"));
        scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        Main.window.setScene(scene);
        Game.onlineGame = true; // to change some features in the normal game
    }
}
