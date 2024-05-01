package org.cockyadolescents.truechessthegame;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.IOException;
import java.util.Vector;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        newGame(stage);
    }

    public void newGame(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 640);
        stage.setTitle("True Chess the Game");
        stage.setScene(scene);
        stage.show();

        // THIS AREA IS TO TEST THE CHESSBOARD CLASS JAVA THROUGH CONSOLE
        ChessBoard.newGame();
        System.out.println(ChessBoard.printBoard()  + "\n");

        // ChessBoard.moveChessPiece(ChessBoard.pieceLocations[3][1], 3, 3);
        Vector<Pair<Integer, Integer>> possibleMoves = ChessBoard.pieceLocations[1][0].getPossibleMoves();
        for (Pair<Integer, Integer> p : possibleMoves) {
            ChessBoard.pieceLocations[p.getKey()][p.getValue()] = new ChessBoard("X", "White", p.getKey(), p.getValue());
        }

        System.out.println(ChessBoard.printBoard() + "\n");

        for (Pair<Integer, Integer> p : possibleMoves) {
            ChessBoard.pieceLocations[p.getKey()][p.getValue()] = null;
        }

        ChessBoard.moveChessPiece(ChessBoard.pieceLocations[1][0], possibleMoves.get(0).getKey(), possibleMoves.get(0).getValue());

        System.out.println(ChessBoard.printBoard() + "\n");

        possibleMoves = ChessBoard.pieceLocations[possibleMoves.get(0).getKey()][possibleMoves.get(0).getValue()].getPossibleMoves();
        for (Pair<Integer, Integer> p : possibleMoves) {
            ChessBoard.pieceLocations[p.getKey()][p.getValue()] = new ChessBoard("X", "White", p.getKey(), p.getValue());
        }

        System.out.println(ChessBoard.printBoard() + "\n");

        for (Pair<Integer, Integer> p : possibleMoves) {
            ChessBoard.pieceLocations[p.getKey()][p.getValue()] = null;
        }

        ChessBoard.moveChessPiece(ChessBoard.pieceLocations[2][2], possibleMoves.get(0).getKey(), possibleMoves.get(0).getValue());
        possibleMoves = ChessBoard.pieceLocations[possibleMoves.get(0).getKey()][possibleMoves.get(0).getValue()].getPossibleMoves();
        for (Pair<Integer, Integer> p : possibleMoves) {
            ChessBoard.pieceLocations[p.getKey()][p.getValue()] = new ChessBoard("X", "White", p.getKey(), p.getValue());
        }

        System.out.println(ChessBoard.printBoard() + "\n");

        for (Pair<Integer, Integer> p : possibleMoves) {
            ChessBoard.pieceLocations[p.getKey()][p.getValue()] = null;
        }
    }

    public static void main(String[] args) {launch();}
}