package org.cockyadolescents.truechessthegame;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class OnlineWaitingRoom {
    @FXML private Label welcomeText;

    // Online game version
    public void queue(Stage window) throws IOException {
        // Load new scene to start the game
        Parent root = FXMLLoader.load(getClass().getResource("onlineWaitingRoom.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());

        window.setScene(scene);
        window.show();

        // Changes some features in the normal game
        MainGame.onlineGame = true;
    }
}
