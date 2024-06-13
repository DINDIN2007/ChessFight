package cockyadolescents.truechessthegame;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import java.io.IOException;

import static cockyadolescents.truechessthegame.Game.*;
import static cockyadolescents.truechessthegame.SecondMain.*;

public class Setting {
    private Scene scene;
    private Parent root;
    @FXML public Button flippingbutton;
    @FXML public Button boxingbutton;
    @FXML public Slider volumeControl;

    // Links FXML file and its components to the setting scene
    public void setupPage() throws IOException {
        root = FXMLLoader.load(getClass().getResource("settingpage.fxml"));
        flippingbutton = (Button) root.lookup("#flippingbutton");
        boxingbutton = (Button) root.lookup("#boxingbutton");
        volumeControl = (Slider) root.lookup("#volumeControl");

        volumeControl.valueProperty().addListener((observable, oldValue, newValue) -> {
            music.songPlayer.setVolume(newValue.doubleValue());
        });

        scene = new Scene(root);
    }

    // Displays this scene as on the window
    public void display() {
        window.setScene(scene);
    }

    // Resets scene to the one at the homepage
    public void home() throws IOException {
        homepage.display();
    }

    // Turn on the flipping feature
    @FXML
    public void turnBoardOn() throws IOException {
        boardCanFlip = boardCanFlip ? false : true;

        if (boardCanFlip) {
            flippingbutton.getStyleClass().remove("off-button");
            flippingbutton.getStyleClass().add("on-button");
            flippingbutton.setText("On");
        }
        else {
            flippingbutton.getStyleClass().remove("on-button");
            flippingbutton.getStyleClass().add("off-button");
            flippingbutton.setText("Off");
        }

        maingame = null;
        maingame = new Game();
        maingame.startGame();
    }

    // Turn on the boxing feature
    @FXML
    public void turnBoxingOn() throws IOException {
        boxingOn = !boxingOn;

        if (boxingOn) {
            boxingbutton.getStyleClass().remove("dark-button");
            boxingbutton.getStyleClass().add("orange-button");
        }
        else {
            boxingbutton.getStyleClass().remove("orange-button");
            boxingbutton.getStyleClass().add("dark-button");
        }

        maingame = new Game();
        maingame.startGame();
    }
}
