package cockyadolescents.truechessthegame;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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

public class Boxing extends Application {

    private Timeline timer;
    private int remainingTime = 90;

    public static void main(String[] args) {
        doStuff();
    }

    public static void doStuff() {
        Application.launch(Boxing.class);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Box!");

        Button startButton = new Button("Start Boxing");
        startButton.setOnAction(e -> showBoxingPopup(primaryStage));

        VBox layout = new VBox(10);
        layout.getChildren().addAll(startButton);

        Scene scene = new Scene(layout, 300, 200);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showBoxingPopup(Stage ownerStage) {
        Popup popup = new Popup();

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
        popup.getContent().add(pane);

        popup.setAutoHide(false);
        popup.show(ownerStage);

        startTimer(timerText, popup);
    }

    private void startTimer(Text timerText, Popup popup) {
        timer = new Timeline(new KeyFrame(Duration.seconds(1), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                remainingTime--;
                int minutes = remainingTime / 60;
                int seconds = remainingTime % 60;
                timerText.setText(String.format("Time remaining: %d:%02d", minutes, seconds));

                if (remainingTime <= 0) {
                    timer.stop();
                    popup.hide();
                }
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