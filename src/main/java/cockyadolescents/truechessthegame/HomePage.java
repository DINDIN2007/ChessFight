package cockyadolescents.truechessthegame;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.util.Pair;

import java.io.IOException;
import java.util.Vector;

import static cockyadolescents.truechessthegame.ChessPiece.CheckBoard;
import static cockyadolescents.truechessthegame.Main.*;
import static cockyadolescents.truechessthegame.ChessPiece.ChessBoard;

public class HomePage {
    @FXML Label welcomeText;
    Parent root; Scene scene;

    public void display() throws IOException {
        root = FXMLLoader.load(getClass().getResource("homepage.fxml"));
        scene = new Scene(root, 800, 640);
        window.setScene(scene);
    }

    @FXML
    public void startGame() {
        maingame.display();
    }

    @FXML
    public void playOnline() throws IOException {
        waitingroom.display();
    }

    // debug
    public static void printNewGameInConsole() {
        // THIS AREA IS TO TEST THE CHESSBOARD CLASS JAVA THROUGH CONSOLE
        System.out.println(ChessPiece.printBoard()  + "\n");

        // ChessBoard.moveChessPiece(ChessBoard.pieceLocations[3][1], 3, 3);
        Vector<Pair<Integer, Integer>> possibleMoves = ChessPiece.ChessBoard[1][0].getPossibleMoves(ChessBoard);
        for (Pair<Integer, Integer> p : possibleMoves) {
            ChessPiece.ChessBoard[p.getKey()][p.getValue()] = new ChessPiece("X", "White", p.getKey(), p.getValue());
        }

        System.out.println(ChessPiece.printBoard() + "\n");

        for (Pair<Integer, Integer> p : possibleMoves) ChessPiece.ChessBoard[p.getKey()][p.getValue()] = null;
        ChessPiece.moveChessPiece(ChessPiece.ChessBoard[1][0], possibleMoves.get(0).getKey(), possibleMoves.get(0).getValue());
        System.out.println(ChessPiece.printBoard() + "\n");

        possibleMoves = ChessPiece.ChessBoard[possibleMoves.get(0).getKey()][possibleMoves.get(0).getValue()].getPossibleMoves(CheckBoard);
        for (Pair<Integer, Integer> p : possibleMoves) {
            ChessPiece.ChessBoard[p.getKey()][p.getValue()] = new ChessPiece("X", "White", p.getKey(), p.getValue());
        }
        System.out.println(ChessPiece.printBoard() + "\n");

        for (Pair<Integer, Integer> p : possibleMoves) ChessPiece.ChessBoard[p.getKey()][p.getValue()] = null;
        ChessPiece.moveChessPiece(ChessPiece.ChessBoard[2][2], possibleMoves.get(0).getKey(), possibleMoves.get(0).getValue());
        possibleMoves = ChessPiece.ChessBoard[possibleMoves.get(0).getKey()][possibleMoves.get(0).getValue()].getPossibleMoves(ChessBoard);
        for (Pair<Integer, Integer> p : possibleMoves) {
            ChessPiece.ChessBoard[p.getKey()][p.getValue()] = new ChessPiece("X", "White", p.getKey(), p.getValue());
        }
        System.out.println(ChessPiece.printBoard() + "\n");
    }
}