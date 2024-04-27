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
                this.possibleMoves = new int[][]{{0, 1}, {-1, 1}, {1, 1}, {0, 2}};
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
        }

        pieceLocations[this.pieceX][this.pieceY] = this;
    }

    public static boolean checkBoardBound(int x, int y) {
        return x < 0 || x >= 8 || y < 0 || y >= 8;
    }

    public Vector<Pair<Integer, Integer>> getPossibleMoves() {
        Vector<Pair<Integer, Integer>> moves = new Vector<>();

        switch (this.pieceType) {
            case "Pawn":
                for (int[] coord : this.possibleMoves) {
                    Pair<Integer, Integer> newCoords = new Pair<>(coord[0] + this.pieceX, coord[1] + this.pieceY);
                    if (ChessBoard.checkBoardBound(newCoords.getKey(), newCoords.getValue())) moves.add(newCoords);
                }
                if (this.hasMoved) moves.removeLast();
            case "Knight": case "King":
                for (int[] coord : this.possibleMoves) {
                    Pair<Integer, Integer> newCoords = new Pair<>(coord[0] + this.pieceX, coord[1] + this.pieceY);
                    if (ChessBoard.checkBoardBound(newCoords.getKey(), newCoords.getValue())) moves.add(newCoords);
                }
            case "Bishop": case "Rook": case "Queen":
                for (int i = 0; i < this.possibleMoves.length; i++) {
                    int[] coord = this.possibleMoves[i];
                    Pair<Integer, Integer> newCoords = new Pair<>(coord[0] + this.pieceX, coord[1] + this.pieceY);
                    if (!ChessBoard.checkBoardBound(newCoords.getKey(), newCoords.getValue())) {
                        moves.add(newCoords);
                        if (ChessBoard.pieceLocations[newCoords.getKey()][newCoords.getValue()] != null) {
                            int determiningValue = Math.max(Math.abs(coord[0]), Math.abs(coord[1]));
                            i += 7 - determiningValue;
                        }
                    }
                }
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

        pieceLocations[0][0] = new ChessBoard("Rook", "White", 0, 0);
        pieceLocations[1][0] = new ChessBoard("Knight", "White", 1, 0);
        pieceLocations[2][0] = new ChessBoard("Bishop", "White", 2, 0);
        pieceLocations[3][0] = new ChessBoard("Queen", "White", 3, 0);
        pieceLocations[4][0] = new ChessBoard("King", "White", 4, 0);
        pieceLocations[5][0] = new ChessBoard("Bishop", "White", 5, 0);
        pieceLocations[6][0] = new ChessBoard("Knight", "White", 6, 0);
        pieceLocations[7][0] = new ChessBoard("Rook", "White", 7, 0);

        pieceLocations[0][7] = new ChessBoard("Rook", "Black", 0, 7);
        pieceLocations[1][7] = new ChessBoard("Knight", "Black", 1, 7);
        pieceLocations[2][7] = new ChessBoard("Bishop", "Black", 2, 7);
        pieceLocations[3][7] = new ChessBoard("Queen", "Black", 3, 7);
        pieceLocations[4][7] = new ChessBoard("King", "Black", 4, 7);
        pieceLocations[5][7] = new ChessBoard("Bishop", "Black", 5, 7);
        pieceLocations[6][7] = new ChessBoard("Knight", "Black", 6, 7);
        pieceLocations[7][7] = new ChessBoard("Rook", "Black", 7, 7);
    }
}
