package org.cockyadolescents.truechessthegame;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class Controller {
    @FXML
    private Label welcomeText;

    public void startGame() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("maingame.fxml"));

        Stage window = (Stage) welcomeText.getScene().getWindow();
        window.setScene(new Scene(root));
        window.show();
    }

    public void tilePressed(ActionEvent event) throws IOException {

    }
}