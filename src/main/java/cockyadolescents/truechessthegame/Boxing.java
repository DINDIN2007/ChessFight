package cockyadolescents.truechessthegame;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
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

    private GameLoop loop;

    @FXML
    private Parent root;
    @FXML
    private static Canvas canvas;
    @FXML
    private static GraphicsContext graphicsContext;
    private static Image source;

    private double movingx;
    private double movingy;
    private double movingx1, movingy1;

    private static double movementSpeed = 5;

    //private Set<KeyCode> pressedKeys = new HashSet<>();
    //private Thread movementThread1, movementThread2;

    private boolean isUpPressed, isDownPressed, isLeftPressed, isRightPressed;
    private boolean isWPressed, isSPressed, isAPressed, isDPressed;

    private boolean isHitboxActive = false;
    private boolean canPressL = true;
    private double hitboxX, hitboxY;
    private boolean isSecondHitboxActive = false;
    private boolean canPressF = true;
    private double secondHitboxX, secondHitboxY;

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

        //Button punchButtonPlayer1 = (Button)root.lookup("#P1");
        //punchButtonPlayer1.setOnAction(e -> handlePunchAction(1));

        //Button punchButtonPlayer2 = (Button)root.lookup("#P2");
        //punchButtonPlayer2.setOnAction(e -> handlePunchAction(2));

        // Run Game Loop
        loop = new GameLoop(this, 5);
        loop.start();

        // Timer text
        Text timerText = (Text) root.lookup("#timer_text");
        timerText.setText("Time remaining: 0:30");

        // Show the PopUp
        popup.setAutoHide(false);
        popup.show(ownerStage);

        // Gets the canvas from the FXML file
        canvas = (Canvas) root.lookup("#gameScreen");
        graphicsContext = canvas.getGraphicsContext2D();
        source = new Image(getClass().getResourceAsStream("images/ChessPieces-2.png"));

        // Set the pieces at starting positions
        movingx = 300 - 24;
        movingy = 300 * 3.0 / 4;
        movingx1 = 300 - 24;
        movingy1 = 300 * 1.0 / 4;

        // Start timer countdown
        startTimer(timerText, popup);

        //get the scene for movement
        Scene scene = root.getScene();
        scene.setOnKeyPressed(this::handleKeyPressed);
        scene.setOnKeyReleased(this::handleKeyReleased);
        /*movementThread1 = newThread(this::movePlayer1);
        movementThread2 = new Thread(this::movePlayer2);
        movementThread1.start();
        movementThread2.start();*/
    }

    private void startTimer(Text timerText, Popup popup) {
        remainingTime = 30;
        timer = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            remainingTime--;
            int minutes = remainingTime / 60;
            int seconds = remainingTime % 60;
            timerText.setText(String.format("Time remaining: %d:%02d", minutes, seconds));

            if (remainingTime <= 0) {
                timer.stop();
                popup.hide();
                Game.isBoxing = false;
            }
        }));
        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();
    }

    private void handlePunchAction(int i) {
        if (i == 2 && canPressL) {
            activateHitbox();
            canPressL = false;
            Timeline cooldownTimeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> canPressL = true));
            cooldownTimeline.setCycleCount(1);
            cooldownTimeline.play();
        }
    }

    private void handlePunchAction1(int i) {
        if (i == 2 && canPressF) {
            activateHitbox();
            canPressF = false;
            Timeline cooldownTimeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> canPressF = true));
            cooldownTimeline.setCycleCount(1);
            cooldownTimeline.play();
        }
    }

    /*private void handlePunchAction(int i) {
        System.out.println("Punch action executed!");
        // Implement punch action logic
    }*/

    private void activateHitbox() {
        isHitboxActive = true;
        hitboxX = movingx1 + 24;
        hitboxY = movingy1;

        isSecondHitboxActive = true;
        secondHitboxX = movingx + 24;
        secondHitboxY = movingy;

        Timeline hitboxTimeline = new Timeline(new KeyFrame(Duration.seconds(0.5), e -> isHitboxActive = false));
        hitboxTimeline.setCycleCount(1);
        hitboxTimeline.play();
    }

    private void handleKeyPressed(KeyEvent event) {
        switch (event.getCode()) {
            case UP:
                isUpPressed = true; // Move up
                break;
            case DOWN:
                isDownPressed = true; // Move down
                break;
            case LEFT:
                isLeftPressed = true; // Move left
                break;
            case RIGHT:
                isRightPressed = true; // Move right
                break;
            case W:
                isWPressed = true;  // Move up
                break;
            case S:
                isSPressed = true; // Move down
                break;
            case A:
                isAPressed = true; // Move left
                break;
            case D:
                isDPressed = true; // Move right
                break;
            case F:
                handlePunchAction(1);
                break;
            case L:
                handlePunchAction(2);
                break;
            default:
                break;
        }
        updateElement(); // Update the canvas after moving the pieces
    }

    private void handleKeyReleased(KeyEvent event) {
        switch (event.getCode()) {
            case UP:
                isUpPressed = false; // Move up
                break;
            case DOWN:
                isDownPressed = false; // Move down
                break;
            case LEFT:
                isLeftPressed = false; // Move left
                break;
            case RIGHT:
                isRightPressed = false; // Move right
                break;
            case W:
                isWPressed = false;  // Move up
                break;
            case S:
                isSPressed = false; // Move down
                break;
            case A:
                isAPressed = false; // Move left
                break;
            case D:
                isDPressed = false; // Move right
                break;
            default:
                break;
        }
    }

    public void updatePositions() {
        if (isUpPressed) movingy -= movementSpeed;
        if (isDownPressed) movingy += movementSpeed;
        if (isRightPressed) movingx += movementSpeed;
        if (isLeftPressed) movingx -= movementSpeed;

        if (isWPressed) movingy1 -= movementSpeed;
        if (isSPressed) movingy1 += movementSpeed;
        if (isDPressed) movingx1 += movementSpeed;
        if (isAPressed) movingx1 -= movementSpeed;
    }

    // What to run after an iteration of loop
    public void updateElement() {
        // Clear canvas
        graphicsContext.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // Redraw background
        graphicsContext.setFill(Color.rgb(128, 128, 128));
        graphicsContext.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // graphicsContext.fillRect(0, 0, 48, 48);
        int v = 0, v1 = 0;
        int x = 0, x1 = 0;

        switch (attack.pieceType) {
            case "Pawn":
                v = 0;
                break;
            case "Rook":
                v = 48;
                break;
            case "Knight":
                v = 48 * 2;
                break;
            case "Bishop":
                v = 48 * 3;
                break;
            case "Queen":
                v = 48 * 4;
                break;
            case "King":
                v = 48 * 5;
                break;
        }
        switch (attack.pieceColor) {
            case "White":
                v1 = 48;
                break;
            case "Black":
                v1 = 0;
                break;
        }

        switch (defense.pieceType) {
            case "Pawn":
                x = 0;
                break;
            case "Rook":
                x = 48;
                break;
            case "Knight":
                x = 48 * 2;
                break;
            case "Bishop":
                x = 48 * 3;
                break;
            case "Queen":
                x = 48 * 4;
                break;
            case "King":
                x = 48 * 5;
                break;
        }
        switch (defense.pieceColor) {
            case "White":
                x1 = 48;
                break;
            case "Black":
                x1 = 0;
                break;
        }

        // Draw the two pieces
        graphicsContext.drawImage(source, v, v1, 48, 48, movingx, movingy, 48, 48);
        graphicsContext.drawImage(source, x, x1, 48, 48, movingx1, movingy1, 48, 48);

        if (isHitboxActive) {
            hitboxX = movingx1 + 24;  // Update hitbox position to follow the bottom piece
            hitboxY = movingy1;
            graphicsContext.setFill(Color.RED);
            graphicsContext.fillRect(hitboxX, hitboxY, 24, 24);
            checkHitboxCollision();
        }
        if (isSecondHitboxActive) {
            secondHitboxX = movingx + 24;  // Update second hitbox position to follow the top piece
            secondHitboxY = movingy;
            graphicsContext.setFill(Color.RED);
            graphicsContext.fillRect(secondHitboxX, secondHitboxY, 24, 24);
            checkSecondHitboxCollision();
        }

        // movementSpeed = Math.max(0.0000001, movementSpeed - 0.001);
        updatePositions();
    }

    private void checkHitboxCollision() {
        if (hitboxX < movingx + 48 && hitboxX + 24 > movingx &&
                hitboxY < movingy + 48 && hitboxY + 24 > movingy) {
            Popup popup = (Popup) root.getScene().getWindow();
            popup.hide();
        }
    }

    private void checkSecondHitboxCollision() {
        if (secondHitboxX < movingx1 + 48 && secondHitboxX + 24 > movingx1 &&
                secondHitboxY < movingy1 + 48 && secondHitboxY + 24 > movingy1) {
            Popup popup = (Popup) root.getScene().getWindow();
            popup.hide();
        }
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