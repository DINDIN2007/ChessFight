package org.cockyadolescents.truechessthegame;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.IOException;
import java.util.Vector;

public class MainGame {
    private static Image source;
    private static GraphicsContext graphicsContext;

    private Button[][] tileArray= new Button[8][8];
    private static boolean lockIntoPiece = false;
    private static int selectX = -1, selectY = -1;
    private static Vector<Pair<Integer, Integer>> possibleMoves;
    private static String playingSide = "White";

    @FXML private static Canvas canvas;
    @FXML private GridPane buttonBoard, labelBoard;
    @FXML private VBox leftNumbers;
    @FXML private HBox topNumbers;

    private static boolean boardCanFlip = false;

    // Game loop for animation
    private static int delay = 2; // in seconds
    private static GameLoop animationLoop = new GameLoop(new MainGame(), delay);

    // Online game features
    public static boolean onlineGame = false;

    // Main game setup
    public void startGame(Stage window) throws IOException {
        // Load new scene to start the game
        Parent root = FXMLLoader.load(getClass().getResource("maingame.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        window.setScene(scene);
        window.show();

        // Create new chess game in ChessBoard class
        ChessPiece.newGame();

        // The two Grid pane around the canvas that make up the board
        buttonBoard = (GridPane) root.lookup("#buttonBoard");
        labelBoard = (GridPane) root.lookup("#labelBoard");

        // The canvas used to draw the Chess Pieces
        canvas = (Canvas) root.lookup("#canvas");
        graphicsContext = canvas.getGraphicsContext2D();
        graphicsContext.translate(0, canvas.getHeight() - 60);
        // Gets chess piece sprites
        source = new Image(getClass().getResourceAsStream("ChessPieces.png"));

        // The containers storing the numbers/letters on the side of the board
        leftNumbers = (VBox) root.lookup("#leftNumbers");
        topNumbers = (HBox) root.lookup("#topNumbers");

        // Create all elements in the previously mentioned containers
        createBoard(buttonBoard, labelBoard, leftNumbers, topNumbers);
    }

    // Creates the Gridpanes and the Numbers/Letters on the Side of the Board
    public void createBoard(GridPane buttonBoard, GridPane labelBoard, VBox leftNumbers, HBox topNumbers) {
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
            tile.setOnAction(event -> tilePressed(event, buttonBoard, leftNumbers, topNumbers));
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
            number.getStyleClass().add("tile");
            leftNumbers.getChildren().add(number);

            Label letter = new Label(String.valueOf((char) ('A' + i)));
            letter.getStyleClass().add("tile");
            topNumbers.getChildren().add(letter);
        }

        // turnBoard(leftNumbers, topNumbers);

        // Drawing the chess pieces on the canvas
        for (int i = 0; i < 8; i++) {
        for (int j = 0; j < 8; j++) {
            if (ChessPiece.ChessBoard[i][j] != null) drawPiece(ChessPiece.ChessBoard[i][j]);
        }}
    }

    // Either selects a piece or moves a piece to designated position through button clicks
    public void tilePressed(ActionEvent event, GridPane buttonBoard, VBox leftNumbers, HBox topNumbers) {
        // Finds the coordinate of the button pressed based on who's playing
        Button tile = (Button) event.getSource();
        int x = tile.getId().charAt(0) - '0';
        int y = tile.getId().charAt(2) - '0';

        // Finds the matching piece saved in ChessBoard class
        ChessPiece tilePiece = ChessPiece.ChessBoard[x][y];

        if (selectX == x && selectY == y) {
            drawBoard(tileArray, buttonBoard, leftNumbers, topNumbers);
            selectX = -1; selectY = -1;
            lockIntoPiece = false;
        }

        // Go to marked place
        else if (lockIntoPiece && !(tilePiece != null && tilePiece.pieceColor.equals(playingSide))) {
            if (tilePiece != null) {
                Boxing.doStuff();

            }

            ChessPiece selectedPiece = ChessPiece.ChessBoard[selectX][selectY];

            // Moving the rook when castling
            if (selectedPiece.pieceType.equals("King") && y == selectY) {
                // Left Castle
                if (selectedPiece.pieceX - x == 2) {
                    ChessPiece.moveChessPiece(ChessPiece.ChessBoard[0][y], 3, y);
                }
                // Right Castle
                if (x - selectedPiece.pieceX == 2) {
                    ChessPiece.moveChessPiece(ChessPiece.ChessBoard[7][y], 5, y);
                }
            }

            selectedPiece.hasMoved = true;

            ChessPiece.moveChessPiece(selectedPiece, x, y);
            clearCanvas();

            playingSide = (playingSide.equals("White")) ? "Black" : "White";
            drawBoard(tileArray, buttonBoard, leftNumbers, topNumbers);

            selectX = -1; selectY = -1;
            lockIntoPiece = false;

            turnBoard(leftNumbers, topNumbers);
            buttonBoard.setRotate((buttonBoard.getRotate() == 180) ? 0 : 180);
        }

        // Marks the places that the piece can move to
        else if (tilePiece != null && tilePiece.pieceColor.equals(playingSide)){
            // Resets previously marked moves
            drawBoard(tileArray, buttonBoard, leftNumbers, topNumbers);

            // Get all possible moves for this chess piece
            possibleMoves = tilePiece.getPossibleMoves();

            // Loop through board to mark possible moves
            for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                ChessPiece piece = ChessPiece.ChessBoard[i][j];
                if (piece != null && piece.pieceColor.equals(playingSide)) continue;

                if (possibleMoves.contains(new Pair<>(i, j))) {
                    tileArray[i][j].getStyleClass().add("boardTilesPossibleMoves");
                    tileArray[i][j].setDisable(false);
                }
                else {
                    tileArray[i][j].getStyleClass().remove("boardTilesPossibleMoves");
                    tileArray[i][j].setDisable(true);
                }
            }}

            lockIntoPiece = true;
            selectX = x; selectY = y;
        }
    }

    // Draws the pieces and un-disables the tiles on the board
    public static void drawBoard(Button[][] tileArray, GridPane buttonBoard, VBox leftNumbers, HBox topNumbers) {
        for (int i = 0; i < 8; i++) {
        for (int j = 0; j < 8; j++) {
            tileArray[i][j].setDisable(false);
            tileArray[i][j].getStyleClass().remove("boardTilesPossibleMoves");
            if (ChessPiece.ChessBoard[i][j] != null) drawPiece(ChessPiece.ChessBoard[i][j]);
        }}
    }

    // Changes the Number/Sides depending on the Side of the Board
    public static void turnBoard(VBox leftNumbers, HBox topNumbers) {
        for (int i = 0; i < 8; i++) {
            Label left = (Label)(leftNumbers.getChildren().get(i));
            int pos = Math.abs(8 - (left.getText().charAt(0) - '1'));
            left.setText("" + pos);

            Label top = (Label)(topNumbers.getChildren().get(i + 1));
            top.setText(String.valueOf((char)(8 - pos + 'A')));
        }
    }

    // Draws a single piece using information from the ChessBoard object
    public static void drawPiece(ChessPiece piece) {
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

        if (playingSide.equals("White")) {
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
    public static void clearCanvas() {
        graphicsContext.clearRect(0, -(canvas.getHeight() - 60), canvas.getWidth(), canvas.getHeight());
    }

    // Animation for chess game
    public static void animate() {
        // Noah do animation here (it repeats every delay you give it in startGame()
    }
}