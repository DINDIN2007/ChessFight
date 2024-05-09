package org.cockyadolescents.truechessthegame;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;

public class Controller {
    @FXML
    private Label welcomeText;

    public void startGame() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("maingame.fxml"));
        Stage window = (Stage) welcomeText.getScene().getWindow();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        window.setScene(scene);
        window.show();

        createBoard((GridPane) root.lookup("board"));
    }

    public void createBoard(GridPane board) {
        for (int i = 7; i >= 0; i++) {
            for (int j = 7; j >= 0; j++) {
                Button tile = new Button("");
                tile.setId(i + "_" + j);
                tile.getStyleClass().add("tile");
                if (j % 2 + i % 2 == 0)
                    tile.getStyleClass().add("oddTile");
                else
                    tile.getStyleClass().add("evenTile");
                board.add(tile, 7 - j, 7 - i);
            }
        }
    }

    public void tilePressed(ActionEvent event) throws IOException {
        Button tile = (Button) event.getSource();
        int x = tile.getId().charAt(0) - '0';
        int y = tile.getId().charAt(2) - '0';
        System.out.println(x + " " + y);
    }
}