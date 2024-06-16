package cockyadolescents.truechessthegame;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.Pair;

import java.io.IOException;
import java.util.Vector;

import static cockyadolescents.truechessthegame.ChessPiece.ChessBoard;
import static cockyadolescents.truechessthegame.ChessPiece.score;
import static cockyadolescents.truechessthegame.Main.*;

public class OnlineGame {
    private Button[][] tileArray= new Button[8][8];
    private static boolean lockIntoPiece = false, isPromoting = false;
    public static boolean boardCanFlip = false, boxingOn = false; // temp
    public static int x1 = -1, y1 = -1, x2, y2;
    public static int[] move;
    private static int timeLeftWhite = 60000, timeLeftBlack = 60000;
    private static String playingSide = "", winner = "";
    private static Vector<Pair<Integer, Integer>> possibleMoves;
    public static boolean onlineGame = true, hasStarted = false, playerTurn = playingSide.equals("White");

    @FXML private static Canvas canvas;
    @FXML private GridPane buttonBoard, labelBoard;
    @FXML private VBox leftNumbers;
    @FXML private HBox topNumbers;
    @FXML private static Label isCheckedLabel, whiteTimer, blackTimer;
    @FXML private static ProgressBar progressBar;
    @FXML private Button home, newgame;
    @FXML private VBox promotionBar;

    private static Image source;
    private static GraphicsContext graphicsContext;
    private static GameLoop animationLoop;

    @FXML private Parent root;
    private Scene scene;
    private Boxing boxGame = new Boxing();
    public static boolean isBoxing = false, boxingWon = false;

    @FXML
    public void home() throws IOException {
        homepage.display();
        client.textOut.println("/quit");
        client.shutdown();
    }

    @FXML
    public void newGame() throws IOException {
        onlinegame = null;
        onlinegame = new OnlineGame();
        onlinegame.startGame();
        onlinegame.display();
    }

    public void display() {
        window.setScene(scene);
        music.startGame();
    }

    // Main game setup
    public void startGame() throws IOException {
        // Load new scene to start the game
        root = FXMLLoader.load(getClass().getResource("game.fxml"));
        scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());

        // Create new chess game in ChessBoard class
        ChessPiece.newGame();

        // The two Grid pane around the canvas that make up the board
        buttonBoard = (GridPane) root.lookup("#buttonBoard");
        labelBoard = (GridPane) root.lookup("#labelBoard");

        // The canvas used to draw the Chess Pieces
        canvas = (Canvas) root.lookup("#canvas");
        graphicsContext = canvas.getGraphicsContext2D();
        graphicsContext.translate(0, canvas.getHeight() - 60);
        clearCanvas();
        // Gets chess piece sprites
        source = new Image(getClass().getResourceAsStream("images/ChessPieces-2.png"));

        // The containers storing the numbers/letters on the side of the board
        leftNumbers = (VBox) root.lookup("#leftNumbers");
        topNumbers = (HBox) root.lookup("#topNumbers");

        // Other FXML elements
        promotionBar = (VBox) root.lookup("#promotionBar");
        isCheckedLabel = (Label) root.lookup("#isCheckedLabel"); isCheckedLabel.setVisible(false);
        progressBar = (ProgressBar) root.lookup("#progressBar");

        // The two timer
        whiteTimer = (Label) root.lookup("#whiteTimer");
        blackTimer = (Label) root.lookup("#blackTimer");
        timeLeftWhite = 60000; timeLeftBlack = 60000;

        // Hide the pawn promotion selector
        promotionBar.setVisible(false);

        // Resets to starting values
        hasStarted = false;
        lockIntoPiece = false;
        playingSide = "White";
        x1 = -1; y1 = -1;

        // Start animation loop
        /*if (animationLoop != null) animationLoop.stop();
        animationLoop = new GameLoop(this, 10);
        animationLoop.start();*/

        // Assigns the pawn promotion buttons their function
        String[] possiblePromotions = {"Queen", "Rook", "Bishop", "Knight"};
        for (String promotion : possiblePromotions) {
            ((Button) root.lookup("#" + promotion)).setOnAction(event -> promotePawn(promotion, tileArray, buttonBoard, leftNumbers, topNumbers, promotionBar));
        }

        // Create all elements in the previously mentioned containers
        createBoard(buttonBoard, labelBoard, leftNumbers, topNumbers, promotionBar, window);

        // Stop any boxing game
        boxGame.remainingTime = 0;

        /*if (playingSide.equals("Black")) {
            turnBoard(leftNumbers, topNumbers);
            buttonBoard.setRotate((buttonBoard.getRotate() == 180) ? 0 : 180);
        }*/
    }

    // End game setup
    public void endGame() throws IOException {
        // Load new scene to start the game
        root = FXMLLoader.load(getClass().getResource("gameover.fxml"));
        scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());

        Label cryLabel = (Label) root.lookup("#cryLabel");
        cryLabel.setVisible(false);

        Button cryButton = (Button) root.lookup("#cryButton");
        cryButton.setOnAction(event -> cryLabel.setVisible(true));

        Label winnerButton = (Label) root.lookup("#winner");
        winnerButton.setText(winner + " won !");

        Button homeButton = (Button) root.lookup("#homebutton");
        homeButton.setOnAction(event -> {
            try {
                startGame(); newGame();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        Button rematchButton = (Button) root.lookup("#rematchbutton");
        rematchButton.setOnAction(event -> {
            try {
                newGame();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        window.setScene(scene);
    }

    // Creates the Gridpanes and the Numbers/Letters on the Side of the Board
    private void createBoard(GridPane buttonBoard, GridPane labelBoard, VBox leftNumbers, HBox topNumbers, VBox promotionBar, Stage window) {
        for (int i = 0; i < 8; i++) {
        for (int j = 0; j < 8; j++) {
            // Creates new label to add to labelBoard
            Label tileBackground = new Label("");
            tileBackground.getStyleClass().add("tile");

            // Figures out which color to give each tile
            if (i % 2 == 0) tileBackground.getStyleClass().add((j % 2 == 0) ? "oddTiles" : "evenTiles");
            else tileBackground.getStyleClass().add((j % 2 == 1) ? "oddTiles" : "evenTiles");
            labelBoard.add(tileBackground, i, 7 - j);

            // Creates new button to add to buttonBoard
            Button tile = new Button("");
            tile.setId(i + " " + (j));
            tile.getStyleClass().add("boardTiles");
            tile.setOnAction(event -> {
                try {
                    tilePressed(event, buttonBoard, leftNumbers, topNumbers, promotionBar, window);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            // tile.setText(i + " " + j); // Uncomment this to see the coordinates of the tiles
            tileArray[i][j] = tile;

            buttonBoard.add(tileArray[i][j], i, 7 - j);
        }}

        // Letter row and number column
        // Extra padding for the top left corner of the board
        Label padding = new Label("");
        padding.getStyleClass().add("tile");
        topNumbers.getChildren().add(padding);

        // Initializing the labels on the side of the board
        for (int i = 0; i < 8; i++) {
            Label number = new Label(String.valueOf(8 - i));
            number.getStyleClass().add("boardNum");
            leftNumbers.getChildren().add(number);

            Label letter = new Label(String.valueOf((char) ('A' + i)));
            letter.getStyleClass().add("boardLetters");
            topNumbers.getChildren().add(letter);
        }

        // Drawing the chess pieces on the canvas
        for (int i = 0; i < 8; i++) {
        for (int j = 0; j < 8; j++) {
            if (ChessBoard[i][j] != null) drawPiece(ChessBoard[i][j]);
        }}
    }

    // Either selects a piece or moves a piece to designated position through button clicks
    public void tilePressed(ActionEvent event, GridPane buttonBoard, VBox leftNumbers, HBox topNumbers, VBox promotionBar, Stage window) throws IOException {
        // Doesn't run this function while player is choosing what to promote his piece to
        if (isPromoting) return;

        // Finds the coordinate of the button pressed based on who's playing
        Button tile = (Button) event.getSource();
        x2 = tile.getId().charAt(0) - '0';
        y2 = tile.getId().charAt(2) - '0';

        // Finds the matching piece saved in ChessBoard class
        ChessPiece tilePiece = ChessBoard[x2][y2];

        // Unselect piece
        if (x1 == x2 && y1 == y2) {
            drawBoard(tileArray);
            x1 = -1; y1 = -1;
            lockIntoPiece = false;
        }

        // Go to marked place
        else if (lockIntoPiece && !(tilePiece != null && tilePiece.pieceColor.equals(playingSide))) {
            ChessPiece selectedPiece = ChessBoard[x1][y1];

            // Moving the rook when castling
            if (selectedPiece.pieceType.equals("King") && y2 == y1) {
                // Left Castle
                if (selectedPiece.pieceX - x2 == 2) {
                    ChessPiece.moveChessPiece(ChessBoard[0][y2], 3, y2);
                }
                // Right Castle
                if (x2 - selectedPiece.pieceX == 2) {
                    ChessPiece.moveChessPiece(ChessBoard[7][y2], 5, y2);
                }
            }

            // If capturing a piece, start the Boxing Match !!!
            if (tilePiece != null && boxingOn) {
                boxGame = new Boxing();
                Boxing.attack = selectedPiece;
                Boxing.defense = tilePiece;

                boxGame.showBoxingPopup(window);

                PauseTransition pause = new PauseTransition(Duration.seconds(1));
                pause.setOnFinished(event1 -> {
                    if (boxGame.remainingTime > 0) {
                        pause.play();
                    }

                    else {
                        if (Boxing.attackWon) {
                            if (tilePiece.pieceType.equals("King")) {
                                try {
                                    winner = (tilePiece.pieceColor.equals("White")) ? "Black" : "White";
                                    endGame();
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }

                            // Move the piece in ChessPiece 2D board array
                            ChessPiece.moveChessPiece(selectedPiece, x2, y2);
                            music.capturePiece();

                        } else {
                            // Changes who is playing now
                            clearCanvas();
                            drawBoard(tileArray);
                        }

                        // Update the board
                        movePiece(selectedPiece);
                    }
                });
                pause.play();

                return;
            }

            // If boxing mode is turned off
            else if (tilePiece != null) {
                music.capturePiece();
                if (tilePiece.pieceType.equals("King")) {
                    try {
                        winner = (tilePiece.pieceColor.equals("White")) ? "Black" : "White";
                        endGame();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            // Moves to empty tile
            else music.movePiece();

            // Moves piece in ChessPiece 2D board array
            ChessPiece.moveChessPiece(selectedPiece, x2, y2);

            // Sends move data to server
            move = new int[] {x1, y1, x2, y2};

            // Moves piece in the canvas
            movePiece(selectedPiece);

            // Rotates the board if feature is activated
            if (boardCanFlip) {
                turnBoard(leftNumbers, topNumbers);
                buttonBoard.setRotate((buttonBoard.getRotate() == 180) ? 0 : 180);
            }
        }

        // Marks the places that the piece can move to
        else if (tilePiece != null && tilePiece.pieceColor.equals(playingSide)){
            // Resets previously marked moves
            drawBoard(tileArray);

            // Turn off the ability to choose if the board can be flipped
            hasStarted = true;

            // Get all possible moves for this chess piece
            possibleMoves = tilePiece.getPossibleMoves(ChessBoard);

            // Reset the CheckBoard array
            ChessPiece.copyChessBoardToCheckBoard();

            // Loop through board to mark possible moves
            for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                ChessPiece piece = ChessBoard[i][j];
                if (piece != null && piece.pieceColor.equals(playingSide)) continue;

                // Checks if the tile should be highlighted
                if (possibleMoves.contains(new Pair<>(i, j)) && !ChessPiece.checkCheckingOnNextMove(tilePiece, i, j)) {
                    tileArray[i][j].getStyleClass().add("boardTilesPossibleMoves");
                    tileArray[i][j].setDisable(false);
                }
                else {
                    tileArray[i][j].getStyleClass().remove("boardTilesPossibleMoves");
                    tileArray[i][j].setDisable(true);
                }
            }}

            lockIntoPiece = true;
            x1 = x2; y1 = y2;
        }
    }

    // Moves piece to another position on the board
    public void movePiece(ChessPiece selectedPiece) {
        // Disable special moves for pawn (2 step forward) or king (castle)
        selectedPiece.hasMoved = true;

        // Pawn promotion if it reaches the other end of the board
        if (ChessBoard[x2][y2] != null && ChessBoard[x2][y2].canPromote) {
            promotionBar.setVisible(true);
            isPromoting = true;
            return;
        }
        else {
            promotionBar.setVisible(false);
            isPromoting = false;
        }

        // Changes who is playing now
        playingSide = (playingSide.equals("White")) ? "Black" : "White";
        clearCanvas();
        drawBoard(tileArray);

        // Detecting checking feature
        String checkedCheck = ChessPiece.checkChecking(ChessBoard);
        if (!checkedCheck.equals("None")) {
            isCheckedLabel.setVisible(true);
            isCheckedLabel.setText(checkedCheck + " King CHECK !");

            // Disappears after 1 sec
            PauseTransition delay = new PauseTransition(Duration.seconds(1));
            delay.setOnFinished(e -> isCheckedLabel.setVisible(false));
            delay.play();
        }
        else isCheckedLabel.setText("");

        // Resets which piece is selected
        x1 = -1; y1 = -1;
        lockIntoPiece = false;

        // Change the progress bar
        progressBar.setProgress((double)score[0] / (score[0] + score[1]));
    }

    // Draws the pieces and un-disables the tiles on the board
    public static void drawBoard(Button[][] tileArray) {
        for (int i = 0; i < 8; i++) {
        for (int j = 0; j < 8; j++) {
            tileArray[i][j].setDisable(false);
            tileArray[i][j].getStyleClass().remove("boardTilesPossibleMoves");
            if (ChessBoard[i][j] != null) drawPiece(ChessBoard[i][j]);
        }}
    }

    // Changes the Number/Sides depending on the Side of the Board
    private static void turnBoard(VBox leftNumbers, HBox topNumbers) {
        for (int i = 0; i < 8; i++) {
            Label left = (Label)(leftNumbers.getChildren().get(i));
            int pos = Math.abs(8 - (left.getText().charAt(0) - '1'));
            left.setText("" + pos);

            Label top = (Label)(topNumbers.getChildren().get(i + 1));
            top.setText(String.valueOf((char)(8 - pos + 'A')));
        }
    }

    // Draws a single piece using information from the ChessBoard object
    private static void drawPiece(ChessPiece piece) {
        int pieceSource = 0, pieceColor = 0;

        pieceColor = switch (piece.pieceColor) {
            case "White" -> 1;
            case "Black" -> 0;
            default -> pieceColor;
        };

        pieceSource = switch (piece.pieceType) {
            case "Pawn" -> 0;
            case "Rook" -> 1;
            case "Knight" -> 2;
            case "Bishop" -> 3;
            case "Queen" -> 4;
            case "King" -> 5;
            default -> pieceSource;
        };

        if (!boardCanFlip || playingSide.equals("White")) {
            graphicsContext.drawImage(source,
                    pieceSource * 48, pieceColor * 48, 48, 48,
                    piece.pieceX * 60, piece.pieceY * (-60), 60, 60
            );
        }
        else {
            graphicsContext.drawImage(source,
                    pieceSource * 48, pieceColor * 48, 48, 48,
                    (7 - piece.pieceX) * 60, (7 - piece.pieceY) * (-60), 60, 60
            );
        }
    }

    // Clears canvas to later redraw on it
    private static void clearCanvas() {
        graphicsContext.clearRect(0, -(canvas.getHeight() - 60), canvas.getWidth(), canvas.getHeight());
    }

    // Lets the user choose what to promote the pawn to
    public void promotePawn(String promotedPieceType, Button[][] tileArray, GridPane buttonBoard, VBox leftNumbers, HBox topNumbers, VBox promotionBar) {
        // Changes the type of piece on the board by creating a new piece to replace pawn
        ChessBoard[x2][y2] = new ChessPiece(promotedPieceType, playingSide, x2, y2);
        promotionBar.setVisible(false);

        // Changes who is playing now
        playingSide = (playingSide.equals("White")) ? "Black" : "White";
        clearCanvas();
        drawBoard(tileArray);

        // Detecting checking feature
        String checkedCheck = ChessPiece.checkChecking(ChessBoard);
        if (!checkedCheck.equals("None")) isCheckedLabel.setText(checkedCheck + " King is in DANGER !");
        else isCheckedLabel.setText("");

        // Resets which piece is selected
        x1 = -1; y1 = -1;
        lockIntoPiece = false;

        // Rotates the board if feature is activated
        if (boardCanFlip) {
            turnBoard(leftNumbers, topNumbers);
            buttonBoard.setRotate((buttonBoard.getRotate() == 180) ? 0 : 180);
        }

        isPromoting = false;
    }

    // Animation for chess game
    public static void update() throws IOException {
        // Noah do animation here (it repeats every delay you give it in startGame())

        // Changes the timer
        if (!hasStarted) return;

        if (playingSide.equals("White")) {
            timeLeftWhite--;
            String mil = "" + (timeLeftWhite % 6000 % 100); mil = ((mil.length() == 1) ? "0" : "") + mil;
            String sec = "" + (int)(timeLeftWhite % 6000 / 100); sec = ((sec.length() == 1) ? "0" : "") + sec;
            whiteTimer.setText((timeLeftWhite / 6000) + ":" + sec + ":" + mil);
        }
        else {
            timeLeftBlack--;
            String mil = "" + (timeLeftBlack % 6000 % 100); mil = ((mil.length() == 1) ? "0" : "") + mil;
            String sec = "" + (int)(timeLeftBlack % 6000 / 100); sec = ((sec.length() == 1) ? "0" : "") + sec;
            blackTimer.setText((timeLeftBlack / 6000) + ":" + sec + ":" + mil);
        }

        if (timeLeftWhite <= 0) {
            winner = "Black";
            maingame.endGame();
        }

        if (timeLeftBlack <= 0) {
            winner = "White";
            maingame.endGame();
        }
    }
}