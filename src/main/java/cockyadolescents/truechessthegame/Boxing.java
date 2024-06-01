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

    @FXML private Parent root;
    @FXML private static Canvas canvas;
    @FXML private static GraphicsContext graphicsContext;
    private static Image source;

    public static void main(String[] args) throws IOException {
        Boxing newGame = new Boxing();
        newGame.showBoxingPopup(stage);
    }

    /*public static void startMatch(Stage primaryStage) {
        Application.launch(Boxing.class);
    }

    /*@Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Box!");

        Button startButton = new Button("Start Boxing");
        startButton.setOnAction(e -> showBoxingPopup(primaryStage));

        VBox layout = new VBox(10);
        layout.getChildren().addAll(startButton);

        Scene scene = new Scene(layout, 700, 450);
        primaryStage.setScene(scene);
        primaryStage.show();
    }*/

    public void showBoxingPopup(Stage ownerStage) throws IOException {
        // Loads FXML file
        root = FXMLLoader.load(getClass().getResource("Boxing.fxml"));

        // Setting up the popUp and linking it to Boxing.fxml
        Popup popup = new Popup();
        popup.getContent().add(root);

        /*
        Pane pane = new Pane();
        pane.setPrefSize(700, 450);

        Text timerText = new Text("Time remaining: 1:30");
        timerText.setLayoutX(300);
        timerText.setLayoutY(20);

        Button punchButtonPlayer1 = new Button("Player 1 Punch");
        punchButtonPlayer1.setLayoutX(150);
        punchButtonPlayer1.setLayoutY(200);
        punchButtonPlayer1.setOnAction(e -> handlePunchAction(1));

        Button punchButtonPlayer2 = new Button("Player 2 Punch");
        punchButtonPlayer2.setLayoutX(450);
        punchButtonPlayer2.setLayoutY(200);
        punchButtonPlayer2.setOnAction(e -> handlePunchAction(2));

        pane.getChildren().addAll(timerText, punchButtonPlayer1, punchButtonPlayer2);
        // popup.getContent().add(pane);
         */

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