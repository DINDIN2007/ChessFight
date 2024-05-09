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
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;

public class Controller {
    @FXML
    private Label welcomeText;
    private static Image source;
    private static GraphicsContext graphicsContext;
    private static Canvas canvas;

    public void startGame() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("maingame.fxml"));
        Stage window = (Stage) welcomeText.getScene().getWindow();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        window.setScene(scene);
        window.show();

        createBoard((GridPane) root.lookup("#board"), (GridPane) root.lookup("#boardBackground"));

        canvas = (Canvas) root.lookup("#canvas");
        graphicsContext = canvas.getGraphicsContext2D();
        source = new Image(getClass().getResourceAsStream("ChessPieces.png"));

        drawPiece(ChessBoard.pieceLocations[0][0]);
        drawPiece(ChessBoard.pieceLocations[1][0]);
        drawPiece(ChessBoard.pieceLocations[2][0]);
    }

    public void createBoard(GridPane board, GridPane boardBackground) {
        for (int i = 7; i >= 0; i--) {
            for (int j = 7; j >= 0; j--) {
                Label tileBackground = new Label("");
                tileBackground.getStyleClass().add("tile");

                Button tile = new Button("");
                tile.setId(i + " " + (7 - j));
                tile.getStyleClass().add("boardTiles");
                tile.setOnAction(this::tilePressed);

                if (i % 2 == 0) {
                    if (j % 2 == 0) tileBackground.getStyleClass().add("oddTiles");
                    else tileBackground.getStyleClass().add("evenTiles");
                }
                else {
                    if (j % 2 == 1) tileBackground.getStyleClass().add("oddTiles");
                    else tileBackground.getStyleClass().add("evenTiles");
                }

                boardBackground.add(tileBackground, 7 - j, 7 - i);
                board.add(tile, 7 - j, 7 - i);
            }
        }
    }

    public void tilePressed(ActionEvent event) {
        Button tile = (Button) event.getSource();
        int x = tile.getId().charAt(0) - '0';
        int y = tile.getId().charAt(2) - '0';

        System.out.println(x + " " + y);
    }

    public static void drawPiece(ChessBoard piece) {
        int pieceSource = 0, pieceColor = 0;

        switch(piece.pieceColor) {
            case "White": pieceSource = 1; break;
            case "Black": pieceSource = 0; break;
        }

        switch(piece.pieceType) {
            case "Rook" : pieceSource = 1; break;
            case "Knight" : pieceSource = 0; break;
            case "Bishop" : pieceSource = 2; break;
        }

        graphicsContext.drawImage(source,
                pieceSource * 48, 0, 48, 48,
                (piece.pieceX) * 60, (7 - piece.pieceY) * 60, 60, 60
        );
    }

    public static void clearCanvas() {
        graphicsContext.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }
}