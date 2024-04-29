package org.cockyadolescents.truechessthegame;

import javafx.util.Pair;

import java.util.Vector;

public class ChessBoard {
    String pieceType, pieceColor;
    int pieceX, pieceY, pieceValue;
    int[][] possibleMoves;
    Boolean hasMoved = false;

    public static ChessBoard[][] pieceLocations = new ChessBoard[8][8];

    public ChessBoard(String pieceType, String pieceColor, int pieceX, int pieceY) {
        this.pieceType = pieceType; this.pieceColor = pieceColor;
        this.pieceX = pieceX; this.pieceY = pieceY;

        int[][] hDirection = {{0, 1}, {0, -1}, {-1, 0}, {1, 0}};
        int[][] dDirection = {{1, -1}, {-1, -1}, {1, 1}, {-1, 1}};

        switch (this.pieceType) {
            case "Pawn":
                this.possibleMoves = new int[][]{{-1, 1}, {0, 1}, {1, 1}, {0, 2}};
                this.pieceValue = 1; break;
            case "Knight":
                this.possibleMoves = new int[][]{{-2, 1}, {-1, 2}, {1, 2}, {2,1}, {-2, -1}, {-1, -2}, {2, -1}, {1, -2}};
                this.pieceValue = 3; break;
            case "Rook":
                possibleMoves = new int[28][2];
                for (int i = 0; i < 4; i++) {
                for (int j = 1; j < 8; j++) {
                    possibleMoves[7 * i + j - 1] = new int[]{hDirection[i][0] * j, hDirection[i][1] * j};
                }}
                this.pieceValue = 3;
                break;
            case "Bishop":
                possibleMoves = new int[28][2];
                for (int i = 0; i < 4; i++) {
                for (int j = 1; j < 8; j++) {
                    possibleMoves[7 * i + j - 1] = new int[]{dDirection[i][0] * j, dDirection[i][1] * j};
                }}
                this.pieceValue = 3;
                break;
            case "Queen":
                possibleMoves = new int[56][2];
                for (int i = 0; i < 4; i++) {
                for (int j = 1; j < 8; j++) {
                    possibleMoves[7 * i + j - 1] = new int[]{hDirection[i][0] * j, hDirection[i][1] * j};
                    possibleMoves[28 + (7 * i + j - 1)] = new int[]{dDirection[i][0] * j, dDirection[i][1] * j};
                }}
                this.pieceValue = 9;
                break;
            case "King":
                possibleMoves = new int[][] {
                        hDirection[0], hDirection[1], hDirection[2], hDirection[3],
                        dDirection[0], dDirection[1], dDirection[2], dDirection[3],
                };
                this.pieceValue = 1000000;
            case "X":
                this.pieceValue = -1;
        }

        pieceLocations[this.pieceX][this.pieceY] = this;
    }

    public static boolean isOutOfBound(int x, int y) {
        return x < 0 || x >= 8 || y < 0 || y >= 8;
    }

    // This method returns a vector of pairs that indicate every plausible move the selected piece
    public Vector<Pair<Integer, Integer>> getPossibleMoves() {
        Vector<Pair<Integer, Integer>> moves = new Vector<>();

        switch (this.pieceType) {
            case "Pawn":
                for (int i = 0; i < 4; i++) {
                    Pair<Integer, Integer> newCoords = new Pair<>(this.possibleMoves[i][0] + this.pieceX, this.possibleMoves[i][1] + this.pieceY);

                    if (!ChessBoard.isOutOfBound(newCoords.getKey(), newCoords.getValue())) {
                        ChessBoard pieceOnThatPosition = ChessBoard.pieceLocations[newCoords.getKey()][newCoords.getValue()];
                        if (pieceOnThatPosition == null) {
                            if (i % 2 == 1) moves.add(newCoords);
                        }
                        else if (pieceOnThatPosition.pieceColor.equals(this.pieceColor)) moves.add(newCoords);
                    }
                }
                if (this.hasMoved) moves.removeLast();
                this.hasMoved = true;
                break;
            case "Knight": case "King":
                for (int[] coord : this.possibleMoves) {
                    Pair<Integer, Integer> newCoords = new Pair<>(coord[0] + this.pieceX, coord[1] + this.pieceY);
                    if (ChessBoard.isOutOfBound(newCoords.getKey(), newCoords.getValue())) continue;

                    ChessBoard pieceOnThatPosition = ChessBoard.pieceLocations[newCoords.getKey()][newCoords.getValue()];
                    if (pieceOnThatPosition == null || !pieceOnThatPosition.pieceColor.equals(this.pieceColor)) moves.add(newCoords);
                }
                break;
            case "Bishop": case "Rook": case "Queen":
                for (int i = 0; i < this.possibleMoves.length; i++) {
                    int[] coord = this.possibleMoves[i];
                    Pair<Integer, Integer> newCoords = new Pair<>(coord[0] + this.pieceX, coord[1] + this.pieceY);
                    if (ChessBoard.isOutOfBound(newCoords.getKey(), newCoords.getValue())) continue;

                    ChessBoard pieceOnThatPosition = ChessBoard.pieceLocations[newCoords.getKey()][newCoords.getValue()];
                    if (pieceOnThatPosition == null || !pieceOnThatPosition.pieceColor.equals(this.pieceColor)) {
                        moves.add(newCoords);
                        if (ChessBoard.pieceLocations[newCoords.getKey()][newCoords.getValue()] != null) {
                            i += 7 - Math.max(Math.abs(coord[0]), Math.abs(coord[1]));
                        }
                    }
                }
                break;
        }

        return moves;
    }

    public static void moveChessPiece(ChessBoard piece, int newX, int newY) {
        pieceLocations[newX][newY] = piece;
        pieceLocations[piece.pieceX][piece.pieceY] = null;
        piece.pieceX = newX; piece.pieceY = newY;
    }

    public static String printBoard() {
        StringBuilder builder = new StringBuilder();
        for (int y = 7; y >= 0; y--) {
            for (int x = 0; x < 8; x++) {
                ChessBoard piece = pieceLocations[x][y];
                builder.append((piece != null) ? piece.pieceType.charAt(0) + " " : ". ");
            }
            builder.append("\n");
        }
        return builder.toString();
    }

    public static void newGameSample() {
        for (int i = 0; i < 8; i++) {
            pieceLocations[i][1] = new ChessBoard("Pawn", "White", i, 1);
            pieceLocations[i][6] = new ChessBoard("Pawn", "Black", i, 6);
        }

        String[] pieceRowPosition = {"Rook", "Knight", "Bishop", "Queen", "King", "Bishop", "Knight", "Rook"};

        for (int i = 0; i < 8; i++) {
            pieceLocations[i][0] = new ChessBoard(pieceRowPosition[i], "White", i, 0);
        }

        for (int i = 0; i < 8; i++) {
            pieceLocations[i][7] = new ChessBoard(pieceRowPosition[i], "Black", i, 7);
        }
    }
}
