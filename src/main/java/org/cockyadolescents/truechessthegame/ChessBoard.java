package org.cockyadolescents.truechessthegame;

import javafx.util.Pair;

import java.util.Vector;

public class ChessBoard {
    String pieceType, pieceColor;
    int pieceX, pieceY;
    int[][] possibleMoves;
    Boolean hasMoved = false;

    public static String[][] board = new String[8][8];

    public ChessBoard(String pieceType, String pieceColor, int pieceX, int pieceY) {
        this.pieceType = pieceType; this.pieceColor = pieceColor;
        this.pieceX = pieceX; this.pieceY = pieceY;

        int[][] hDirection = {{0, 1}, {0, -1}, {-1, 0}, {1, 0}};
        int[][] dDirection = {{1, -1}, {-1, -1}, {1, 1}, {1, -1}};

        switch (this.pieceType) {
            case "Pawn":
                this.possibleMoves = new int[][]{{0, 1}, {-1, 1}, {1, 1}, {0, 2}}; break;
            case "Knight":
                this.possibleMoves = new int[][]{{-2, 1}, {-1, 2}, {1, 2}, {2,1}, {-2, -1}, {-1, -2}, {2, -1}, {1, -2}}; break;
            case "Rook":
                possibleMoves = new int[28][2];
                for (int i = 0; i < 4; i++) {
                for (int j = 1; j < 8; j++) {
                    possibleMoves[7 * i + j - 1] = new int[]{hDirection[i][0] * j, hDirection[i][1] * j};
                }}
                break;
            case "Bishop":
                possibleMoves = new int[28][2];
                for (int i = 0; i < 4; i++) {
                for (int j = 1; j < 8; j++) {
                    possibleMoves[7 * i + j - 1] = new int[]{dDirection[i][0] * j, dDirection[i][1] * j};
                }}
            case "Queen":
                possibleMoves = new int[56][2];
                for (int i = 0; i < 4; i++) {
                for (int j = 1; j < 8; j++) {
                    possibleMoves[7 * i + j - 1] = new int[]{hDirection[i][0] * j, hDirection[i][1] * j};
                    possibleMoves[2 * (7 * i + j - 1)] = new int[]{dDirection[i][0] * j, dDirection[i][1] * j};
                }}
                break;
            case "King":
                possibleMoves = new int[][] {
                        hDirection[0], hDirection[1], hDirection[2], hDirection[3],
                        dDirection[0], dDirection[1], dDirection[2], dDirection[3],
                };
        }
    }

    public Vector<Pair<Integer, Integer>> getPossibleMoves() {
        Vector<Pair<Integer, Integer>> moves = new Vector<>();
        for (int[] coord : this.possibleMoves) {
            Pair<Integer, Integer> newCoords = new Pair<>(coord[0] + this.pieceX, coord[1] + this.pieceY);
            if (newCoords.getKey() < 0 || newCoords.getKey() >= 8) continue;
            if (newCoords.getValue() < 0 || newCoords.getValue() >= 8) continue;
            moves.add(newCoords);
        }
        return moves;
    }

    public static void moveChessPiece(ChessBoard piece, int newX, int newY) {
        board[newX][newY] = piece.pieceType;
        board[piece.pieceX][piece.pieceY] = null;
        piece.pieceX = newX; piece.pieceY = newY;
    }
}
