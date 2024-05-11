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
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.IOException;
import java.util.Objects;
import java.util.Vector;

public class Controller {
    @FXML
    private Label welcomeText;
    private static Image source;
    private static GraphicsContext graphicsContext;
    private static Canvas canvas;

    private Button[][] tileArray= new Button[8][8];
    private static boolean lockIntoPiece = false;
    private static int selectX = -1, selectY = -1;
    private static Vector<Pair<Integer, Integer>> possibleMoves;
    private static String playingSide = "White";

    @FXML
    private GridPane buttonBoard, labelBoard;
    private VBox leftNumbers;
    private HBox topNumbers;

    public void startGame() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("maingame.fxml"));
        Stage window = (Stage) welcomeText.getScene().getWindow();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        window.setScene(scene);
        window.show();

        buttonBoard = (GridPane) root.lookup("#buttonBoard");
        labelBoard = (GridPane) root.lookup("#labelBoard");

        leftNumbers = (VBox) root.lookup("#leftNumbers");
        topNumbers = (HBox) root.lookup("#topNumbers");

        createBoard(buttonBoard, labelBoard, leftNumbers, topNumbers);

        canvas = (Canvas) root.lookup("#canvas");
        graphicsContext = canvas.getGraphicsContext2D();
        graphicsContext.translate(0, canvas.getHeight() - 60);

        source = new Image(getClass().getResourceAsStream("ChessPieces.png"));

        drawPiece(ChessBoard.pieceLocations[0][0]);
        drawPiece(ChessBoard.pieceLocations[1][0]);
        drawPiece(ChessBoard.pieceLocations[2][0]);

        drawPiece(ChessBoard.pieceLocations[0][7]);
        drawPiece(ChessBoard.pieceLocations[1][7]);
        drawPiece(ChessBoard.pieceLocations[2][7]);
    }

    public void createBoard(GridPane buttonBoard, GridPane labelBoard, VBox leftNumbers, HBox topNumbers) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Label tileBackground = new Label("");
                tileBackground.getStyleClass().add("tile");

                Button tile = new Button("");
                tile.setId(i + " " + (j));
                tile.getStyleClass().add("boardTiles");
                tile.setOnAction(event -> tilePressed(event, buttonBoard, leftNumbers, topNumbers));
                tileArray[i][j] = tile;

                if (i % 2 == 0) {
                    if (j % 2 == 0) tileBackground.getStyleClass().add("oddTiles");
                    else tileBackground.getStyleClass().add("evenTiles");
                }
                else {
                    if (j % 2 == 1) tileBackground.getStyleClass().add("oddTiles");
                    else tileBackground.getStyleClass().add("evenTiles");
                }

                labelBoard.add(tileBackground, i, 7 - j);
                buttonBoard.add(tileArray[i][j], i, 7 - j);
            }
        }

        Label padding = new Label(String.valueOf(""));
        padding.getStyleClass().add("tile");
        topNumbers.getChildren().add(padding);

        for (int i = 0; i < 8; i++) {
            Label number = new Label(String.valueOf(8 - i));
            Label letter = new Label(String.valueOf((char) ('A' + i)));
            number.getStyleClass().add("tile");
            letter.getStyleClass().add("tile");

            leftNumbers.getChildren().add(number);
            topNumbers.getChildren().add(letter);
        }
    }

    public void tilePressed(ActionEvent event, GridPane buttonBoard, VBox leftNumbers, HBox topNumbers) {
        Button tile = (Button) event.getSource();
        int x = tile.getId().charAt(0) - '0';
        int y = tile.getId().charAt(2) - '0';

        ChessBoard tilePiece = ChessBoard.pieceLocations[x][y];

        if (tilePiece != null && tilePiece.pieceColor.equals(playingSide)) {

            possibleMoves = tilePiece.getPossibleMoves();
            for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (i == x && y == j) continue;

                ChessBoard piece = ChessBoard.pieceLocations[i][j];
                Pair<Integer, Integer> move = new Pair<>(i, j);

                if (!possibleMoves.contains(move)) {
                    if (piece != null && piece.pieceColor.equals(playingSide)) continue;
                    tileArray[i][j].getStyleClass().remove("boardTilesPossibleMoves");
                    tileArray[i][j].setDisable(true);
                }
                else {
                    tileArray[i][j].getStyleClass().add("boardTilesPossibleMoves");
                    tileArray[i][j].setDisable(false);
                }
            }}

            selectX = x; selectY = y;
            lockIntoPiece = true;
        }
        else if (lockIntoPiece) {
            if (!(selectX == x && selectY == y)) {
                ChessBoard.moveChessPiece(ChessBoard.pieceLocations[selectX][selectY], x, y);
                clearCanvas();
                playingSide = (playingSide.equals("White")) ? "Black" : "White";
            }

            drawBoard(tileArray, buttonBoard, leftNumbers, topNumbers);

            selectX = -1; selectY = -1;
            lockIntoPiece = false;
        }
    }

    public static void drawBoard(Button[][] tileArray, GridPane buttonBoard, VBox leftNumbers, HBox topNumbers) {
        for (int i = 0; i < 8; i++) {
        for (int j = 0; j < 8; j++) {
            tileArray[i][j].setDisable(false);
            tileArray[i][j].getStyleClass().remove("boardTilesPossibleMoves");
            if (ChessBoard.pieceLocations[i][j] != null) drawPiece(ChessBoard.pieceLocations[i][j]);
        }}

        for (int i = 0; i < 8; i++) {
            Label left = (Label)(leftNumbers.getChildren().get(i));
            System.out.println(left.getText().charAt(0) - '0');
            int pos = Math.abs(8 - (left.getText().charAt(0) - '1'));
            left.setText("" + pos);

            Label top = (Label)(topNumbers.getChildren().get(i + 1));
            top.setText(String.valueOf((char)(pos + 'A' - 1)));
        }

        buttonBoard.setRotate((buttonBoard.getRotate() == 180) ? 0 : 180);
    }

    public static void drawPiece(ChessBoard piece) {
        int pieceSource = 0, pieceColor = 0;

        switch(piece.pieceColor) {
            case "White": pieceColor = 1; break;
            case "Black": pieceColor = 0; break;
        }

        switch(piece.pieceType) {
            case "Rook" : pieceSource = 0; break;
            case "Knight" : pieceSource = 1; break;
            case "Bishop" : pieceSource = 2; break;
        }

        System.out.println(piece.pieceX + " " + piece.pieceY);

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

    public static void clearCanvas() {
        graphicsContext.clearRect(0, -(canvas.getHeight() - 60), canvas.getWidth(), canvas.getHeight());
    }
}