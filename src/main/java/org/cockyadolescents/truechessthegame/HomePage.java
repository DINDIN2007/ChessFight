package org.cockyadolescents.truechessthegame;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.IOException;
import java.util.Vector;

public class HomePage extends Application {
    @FXML Label welcomeText;
    MainGame maingame = new MainGame();

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("homepage.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root, 800, 640);
        stage.setTitle("True Chess the Game");
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void startGame() throws IOException {
        maingame.startGame((Stage) welcomeText.getScene().getWindow());
    }

    public static void main(String[] args) {launch();}

    // debug
    public static void printNewGameInConsole() {
        // THIS AREA IS TO TEST THE CHESSBOARD CLASS JAVA THROUGH CONSOLE
        System.out.println(ChessPiece.printBoard()  + "\n");

        // ChessBoard.moveChessPiece(ChessBoard.pieceLocations[3][1], 3, 3);
        Vector<Pair<Integer, Integer>> possibleMoves = ChessPiece.ChessBoard[1][0].getPossibleMoves();
        for (Pair<Integer, Integer> p : possibleMoves) {
            ChessPiece.ChessBoard[p.getKey()][p.getValue()] = new ChessPiece("X", "White", p.getKey(), p.getValue());
        }

        System.out.println(ChessPiece.printBoard() + "\n");

        for (Pair<Integer, Integer> p : possibleMoves) ChessPiece.ChessBoard[p.getKey()][p.getValue()] = null;
        ChessPiece.moveChessPiece(ChessPiece.ChessBoard[1][0], possibleMoves.get(0).getKey(), possibleMoves.get(0).getValue());
        System.out.println(ChessPiece.printBoard() + "\n");

        possibleMoves = ChessPiece.ChessBoard[possibleMoves.get(0).getKey()][possibleMoves.get(0).getValue()].getPossibleMoves();
        for (Pair<Integer, Integer> p : possibleMoves) {
            ChessPiece.ChessBoard[p.getKey()][p.getValue()] = new ChessPiece("X", "White", p.getKey(), p.getValue());
        }
        System.out.println(ChessPiece.printBoard() + "\n");

        for (Pair<Integer, Integer> p : possibleMoves) ChessPiece.ChessBoard[p.getKey()][p.getValue()] = null;
        ChessPiece.moveChessPiece(ChessPiece.ChessBoard[2][2], possibleMoves.get(0).getKey(), possibleMoves.get(0).getValue());
        possibleMoves = ChessPiece.ChessBoard[possibleMoves.get(0).getKey()][possibleMoves.get(0).getValue()].getPossibleMoves();
        for (Pair<Integer, Integer> p : possibleMoves) {
            ChessPiece.ChessBoard[p.getKey()][p.getValue()] = new ChessPiece("X", "White", p.getKey(), p.getValue());
        }
        System.out.println(ChessPiece.printBoard() + "\n");
    }
}