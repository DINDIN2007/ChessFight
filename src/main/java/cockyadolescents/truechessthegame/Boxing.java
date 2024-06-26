package cockyadolescents.truechessthegame;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.stage.Popup;
import javafx.stage.Stage;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.io.IOException;

public class Boxing {
    private Timeline timer;
    public int remainingTime = 30;

    public static ChessPiece attack, defense;
    public static Stage stage;
    private static Popup popup;
    private static Text timerText;

    private GameLoop loop;

    @FXML private Parent root;
    @FXML public static Canvas canvas;
    @FXML public static GraphicsContext graphicsContext;
    public static Image source, source2;

    public static BoxingPiece player1, player2;
    public static boolean attackWon = false; // Defending piece wins by default

    public static void main(String[] args) throws IOException {
        Boxing newGame = new Boxing();
        newGame.showBoxingPopup(stage);
    }

    public void showBoxingPopup(Stage ownerStage) throws IOException {
        // Loads FXML file
        root = FXMLLoader.load(getClass().getResource("boxing.fxml"));

        // Setting up the popUp and linking it to boxing.fxml
        popup = new Popup();
        popup.getContent().add(root);

        // Show the PopUp
        popup.setAutoHide(false);
        popup.show(ownerStage);

        // Gets the canvas from the FXML file
        canvas = (Canvas)root.lookup("#gameScreen");
        graphicsContext = canvas.getGraphicsContext2D();
        source = new Image(getClass().getResourceAsStream("images/ChessPieces-2.png"));
        source2 = new Image(getClass().getResourceAsStream("images/GloveSprites.png"));

        // Set the pieces at starting positions
        if (attack.pieceColor.equals("White")) { // Player 1 as white
            player1 = new BoxingPiece(300.0 - 24, 300 * 3.0/4, attack.pieceType, attack.pieceColor);
            player2 = new BoxingPiece(300.0 - 24, 300 * 1.0/4, defense.pieceType, defense.pieceColor);
        }
        else { // Player 2 as white
            player2 = new BoxingPiece(300.0 - 24, 300 * 1.0/4, attack.pieceType, attack.pieceColor);
            player1 = new BoxingPiece(300.0 - 24, 300 * 3.0/4, defense.pieceType, defense.pieceColor);
        }
        attackWon = false;

        // Timer text
        timerText = (Text)root.lookup("#timer_text");
        timerText.setText("Time remaining: 0:30");

        // Start timer countdown
        startTimer();

        // Run Game Loop
        loop = new GameLoop(this, 5);
        loop.start();

        //get the scene for movement
        Scene scene = root.getScene();

        // Detect key inputs
        scene.setOnKeyPressed(this::handleKeyPressed);
        scene.setOnKeyReleased(this::handleKeyReleased);
    }

    private void startTimer() {
        remainingTime = 30;
        timer = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            remainingTime--;
            int minutes = remainingTime / 60;
            int seconds = remainingTime % 60;
            timerText.setText(String.format("Time remaining: %d:%02d", minutes, seconds));

            if (remainingTime <= 0) {
                timer.stop();
                popup.hide();
                OfflineGame.isBoxing = false;
            }
        }));
        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();
    }

    private void handleKeyPressed(KeyEvent event) {
        switch (event.getCode()) {
            case UP:
                player1.moveUp = true; // Move up
                break;
            case DOWN:
                player1.moveDown = true; // Move down
                break;
            case LEFT:
                player1.moveLeft = true; // Move left
                break;
            case RIGHT:
                player1.moveRight = true; // Move right
                break;
            case W:
                player2.moveUp = true;  // Move up
                break;
            case S:
                player2.moveDown = true; // Move down
                break;
            case A:
                player2.moveLeft = true; // Move left
                break;
            case D:
                player2.moveRight = true; // Move right
                break;
            case L:
                player1.launchAttack();
                break;
            case F:
                player2.launchAttack();
                break;
            default:
                break;
        }
        updateElement(); // Update the canvas after moving the pieces
    }

    private void handleKeyReleased(KeyEvent event) {
        switch (event.getCode()) {
            case UP:
                player1.moveUp = false; // Move up
                break;
            case DOWN:
                player1.moveDown = false; // Move down
                break;
            case LEFT:
                player1.moveLeft = false; // Move left
                break;
            case RIGHT:
                player1.moveRight = false; // Move right
                break;
            case W:
                player2.moveUp = false;  // Move up
                break;
            case S:
                player2.moveDown = false; // Move down
                break;
            case A:
                player2.moveLeft = false; // Move left
                break;
            case D:
                player2.moveRight = false; // Move right
                break;
            default:
                break;
        }
    }

    // What to run after an iteration of loop
    public void updateElement() {
        // Stops updating when game is over
        if (remainingTime <= 0) return;

        // Clear canvas
        graphicsContext.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // Redraw background
        graphicsContext.setFill(Color.rgb(128, 128, 128));
        graphicsContext.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // graphicsContext.fillRect(0, 0, 48, 48);
        player1.update(player2); player2.update(player1);

        // If punched at the same time, decide at random
        if (player1.isDefeated && player2.isDefeated) {
            attackWon = ((int)(Math.random() * 2) == 1);
        }

        // White side won
        if (player1.isDefeated) {
            // player 1 is always white
            attackWon = !attack.pieceType.equals("White");

            Timeline cooldownTimeline = new Timeline(new KeyFrame(Duration.seconds(1.5), e -> popup.hide()));
            cooldownTimeline.setCycleCount(1);
            cooldownTimeline.play();

            timerText.setText((attack.pieceType.equals("White") ? "White" : "Black") + " Won !");
            remainingTime = 0;
            timer.stop();
            loop.stop();
        }

        // Black side won
        if (player2.isDefeated) {
            // player 2 is always black
            attackWon = !attack.pieceType.equals("Black");

            Timeline cooldownTimeline = new Timeline(new KeyFrame(Duration.seconds(1.5), e -> popup.hide()));
            cooldownTimeline.setCycleCount(1);
            cooldownTimeline.play();

            timerText.setText((attack.pieceType.equals("Black") ? "Black" : "White") + " Won !");
            remainingTime = 0;
            timer.stop();
            loop.stop();
        }
    }
}