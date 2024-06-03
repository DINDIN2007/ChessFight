package cockyadolescents.truechessthegame;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.stage.Popup;
import javafx.stage.Stage;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.io.IOException;

public class Boxing {
    private Timeline timer;
    private int remainingTime = 90;

    public static ChessPiece attack, defense;
    public static Stage stage;

    private GameLoop loop;

    @FXML private Parent root;
    @FXML private static Canvas canvas;
    @FXML private static GraphicsContext graphicsContext;
    private static Image source;
    private double movingx, movingy;
    private double movingx1, movingy1;

    public static void main(String[] args) throws IOException {
        Boxing newGame = new Boxing();
        newGame.showBoxingPopup(stage);
    }

    public void showBoxingPopup(Stage ownerStage) throws IOException {
        // Loads FXML file
        root = FXMLLoader.load(getClass().getResource("Boxing.fxml"));

        // Setting up the popUp and linking it to Boxing.fxml
        Popup popup = new Popup();
        popup.getContent().add(root);

        Button punchButtonPlayer1 = (Button)root.lookup("#P1");
        punchButtonPlayer1.setOnAction(e -> handlePunchAction(1));

        Button punchButtonPlayer2 = (Button)root.lookup("#P2");
        punchButtonPlayer2.setOnAction(e -> handlePunchAction(2));

        // Run Game Loop
        loop = new GameLoop(this, 5000);
        loop.start();

        // Timer text
        Text timerText = (Text)root.lookup("#timer_text");
        timerText.setText("Time remaining: 1:30");

        // Show the PopUp
        popup.setAutoHide(false);
        popup.show(ownerStage);

        // newStage.show();

        // Gets the canvas from the FXML file
        canvas = (Canvas)root.lookup("#gameScreen");
        graphicsContext = canvas.getGraphicsContext2D();
        source = new Image(getClass().getResourceAsStream("images/ChessPieces-2.png"));

        // Set the pieces at starting positions
        movingx = 300 - 24; movingy = 300 * 3.0/4;
        movingx1 = 300 - 24; movingy1 = 300 * 1.0/4;

        // Start timer countdown
        startTimer(timerText, popup);
    }

    private void startTimer(Text timerText, Popup popup) {
        remainingTime = 90;
        timer = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            remainingTime--;
            int minutes = remainingTime / 60;
            int seconds = remainingTime % 60;
            timerText.setText(String.format("Time remaining: %d:%02d", minutes, seconds));

            if (remainingTime <= 0) {
                timer.stop();
                popup.hide();
                System.out.println("RAN");
            }
        }));
        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();
    }

    private void handlePunchAction(int i) {
        System.out.println("Punch action executed!");
        // Implement punch action logic
    }

    public void updateElement() {
        // What to run after an iteration of loop
        // graphicsContext.fillRect(0, 0, 48, 48);
        int v = 0;
        int v1 = 0;
        int x = 0;
        int x1 = 0;


        switch(attack.pieceType){
            case "Pawn": v=0; break;
            case "Rook": v=48; break;
            case "Knight": v=48*2; break;
            case "Bishop": v=48*3; break;
            case "Queen": v=48*4;break;
            case "King": v=48*5;break;
        }
        switch(attack.pieceColor){
            case "White": v1=48;break;
            case "Black": v1=0;break;
        }

        switch(defense.pieceType){
            case "Pawn": x=0;break;
            case "Rook": x=48;break;
            case "Knight": x=48*2;break;
            case "Bishop": x=48*3;break;
            case "Queen": x=48*4;break;
            case "King": x=48*5;break;
        }
        switch(defense.pieceColor){
            case "White": x1=48;break;
            case "Black": x1=0;break;
        }
        

        graphicsContext.drawImage(source, v, v1, 48, 48, movingx, movingy, 48, 48);
        graphicsContext.drawImage(source, x, x1, 48, 48, movingx1, movingy1, 48, 48);

    }
}

 /*   public static void doStuff () {

        //Popup
        //Free movement
        //Punch 1 tile forward always
        //Better pieces move faster and smaller cooldown
        //Add timer 1 min 30sec

    }

    public void updateElement() {
        // What to run after an iteration of loop
    }
}*/