package org.cockyadolescents.truechessthegame;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class Controller {
    @FXML
    private Label welcomeText;

    public void startGame() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("maingame.fxml"));

        Stage window = (Stage) welcomeText.getScene().getWindow();
        Scene scene = new Scene(root);
        window.setScene(scene);
        window.show();

        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
    }

    public void tilePressed(ActionEvent event) throws IOException {
        Button tile = (Button) event.getSource();
        int x = tile.getId().charAt(0) - '0';
        int y = tile.getId().charAt(2) - '0';
        System.out.println(x + " " + y);
    }
}