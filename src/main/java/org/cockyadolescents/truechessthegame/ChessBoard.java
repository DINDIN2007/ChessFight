package org.cockyadolescents.truechessthegame;

import javafx.util.Pair;
import java.util.Vector;

public class ChessBoard {
    String pieceType, pieceColor;       // The type of piece (knight, pawn, ...) and the side it belongs to
    int pieceX, pieceY, pieceValue;     // The piece position and its value upon being captured
    int[][] possibleMoves;              // 2d array storing all the possible coordinates to move to
    boolean hasMoved = false;           // Exclusively for pawns so that they don't move 2 up on the next move

    // To check if the white king (i = 0) or black king (i = 1) is checked
    public static boolean[] isChecked = {false, false};

    // 2d array storing all the pieces on the chessboard
    public static ChessBoard[][] pieceLocations = new ChessBoard[8][8];

    // This is the class object initialization where a piece is created
    public ChessBoard(String pieceType, String pieceColor, int pieceX, int pieceY) {
        this.pieceType = pieceType; this.pieceColor = pieceColor;
        this.pieceX = pieceX; this.pieceY = pieceY;

        int[][] hDirection = {{0, 1}, {0, -1}, {-1, 0}, {1, 0}};
        int[][] dDirection = {{1, -1}, {-1, -1}, {1, 1}, {-1, 1}};

        // An array of possible moves are assigned to each respective chess piece
        switch (this.pieceType) {
            case "Pawn":
                if (this.pieceColor.equals("White")) {
                    this.possibleMoves = new int[][]{{-1, 1}, {0, 1}, {1, 1}, {0, 2}};
                }
                else {
                    this.possibleMoves = new int[][]{{-1, -1}, {0, -1}, {1, -1}, {0, -2}};
                }
                this.pieceValue = 1; break;
            case "Knight":
                this.possibleMoves = new int[8][2];
                for (int i = 0; i < 4; i++) {
                    this.possibleMoves[i] = new int[]{2 * dDirection[i][0], dDirection[i][1]};
                    this.possibleMoves[i + 4] = new int[]{dDirection[i][0], 2 * dDirection[i][1]};
                }
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

        // Sets the piece on the board
        pieceLocations[this.pieceX][this.pieceY] = this;
    }

    // This method checks if the given coordinate is out of the chessboard's bound
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

                    // Checks if this new location would be inbound
                    if (!ChessBoard.isOutOfBound(newCoords.getKey(), newCoords.getValue())) {
                        ChessBoard pieceOnThatPosition = ChessBoard.pieceLocations[newCoords.getKey()][newCoords.getValue()];
                        // Case : Pawn moves forward
                        if (pieceOnThatPosition == null) {
                            if (i % 2 == 1) moves.add(newCoords);
                        }
                        // Case : Pawn takes enemy piece on its two diagonals
                        else if (!pieceOnThatPosition.pieceColor.equals(this.pieceColor)) moves.add(newCoords);
                    }
                }
                if (this.hasMoved) moves.removeLast();
                break;
            case "Knight": case "King":
                for (int[] coord : this.possibleMoves) {
                    Pair<Integer, Integer> newCoords = new Pair<>(coord[0] + this.pieceX, coord[1] + this.pieceY);

                    // Checks if this new location would be inbound
                    if (ChessBoard.isOutOfBound(newCoords.getKey(), newCoords.getValue())) continue;

                    ChessBoard pieceOnThatPosition = ChessBoard.pieceLocations[newCoords.getKey()][newCoords.getValue()];

                    // Checks so that the piece doesn't land on a spot with a piece on the same side
                    if (pieceOnThatPosition == null || !pieceOnThatPosition.pieceColor.equals(this.pieceColor)) moves.add(newCoords);
                }

                // Castling logic
                if (this.pieceType.equals("King") && !this.hasMoved) {
                    // Castle on the left
                    ChessBoard[] leftCastle = {
                            ChessBoard.pieceLocations[0][this.pieceY],
                            ChessBoard.pieceLocations[1][this.pieceY],
                            ChessBoard.pieceLocations[2][this.pieceY],
                            ChessBoard.pieceLocations[3][this.pieceY],
                    };
                    if (leftCastle[0] != null && leftCastle[0].pieceType.equals("Rook") && leftCastle[0].pieceColor.equals(this.pieceColor)) {
                        if (leftCastle[1] == null && leftCastle[2] == null && leftCastle[3] == null) {
                            moves.add(new Pair<>(2, this.pieceY));
                        }
                    }

                    // Castle on the right
                    ChessBoard[] rightCastle = {
                            ChessBoard.pieceLocations[5][this.pieceY],
                            ChessBoard.pieceLocations[6][this.pieceY],
                            ChessBoard.pieceLocations[7][this.pieceY],
                    };

                    if (rightCastle[2] != null && rightCastle[2].pieceType.equals("Rook") && rightCastle[2].pieceColor.equals(this.pieceColor)) {
                        if (rightCastle[0] == null && rightCastle[1] == null) {
                            moves.add(new Pair<>(6, this.pieceY));
                        }
                    }
                }
                break;
            case "Bishop": case "Rook": case "Queen":
                for (int i = 0; i < this.possibleMoves.length; i++) {
                    int[] coord = this.possibleMoves[i];
                    Pair<Integer, Integer> newCoords = new Pair<>(coord[0] + this.pieceX, coord[1] + this.pieceY);

                    // Checks if this new location would be inbound
                    if (ChessBoard.isOutOfBound(newCoords.getKey(), newCoords.getValue())) continue;

                    ChessBoard pieceOnThatPosition = ChessBoard.pieceLocations[newCoords.getKey()][newCoords.getValue()];
                    // Checks so that the piece doesn't land on a spot with a piece on the same side
                    if (pieceOnThatPosition == null) moves.add(newCoords);
                    else {
                        if (!pieceOnThatPosition.pieceColor.equals(this.pieceColor)) moves.add(newCoords);

                        // Skips the moves behind an enemy piece
                        if (ChessBoard.pieceLocations[newCoords.getKey()][newCoords.getValue()] != null) {
                            i += 7 - Math.max(Math.abs(coord[0]), Math.abs(coord[1]));
                        }
                    }
                }
                break;
        }

        return moves;
    }

    // Method to see if either king are in check
    public static void checkChecking(ChessBoard firstPiece, ChessBoard secondPiece) {
        if (firstPiece == null || secondPiece == null) return;
        switch (firstPiece.pieceColor) {
            case "White" : if (secondPiece.pieceColor.equals("Black")) isChecked[1] = true;
            case "Black" : if (secondPiece.pieceColor.equals("White")) isChecked[0] = true;
        }
    }

    // Swaps the position of the piece to a new position
    public static void moveChessPiece(ChessBoard piece, int newX, int newY) {
        pieceLocations[newX][newY] = piece;
        pieceLocations[piece.pieceX][piece.pieceY] = null;
        piece.pieceX = newX; piece.pieceY = newY;

        isChecked = new boolean[]{false, false};
        for (Pair<Integer, Integer> moves : piece.getPossibleMoves()) {
            checkChecking(piece, pieceLocations[moves.getKey()][moves.getValue()]);
        }
    }

    // Creates a basic chess start board
    public static void newGame() {
        String[] pieceRowPosition = {"Rook", "Knight", "Bishop", "Queen", "King", "Bishop", "Knight", "Rook"};

        for (int i = 0; i < 8; i++) {
            pieceLocations[i][1] = new ChessBoard("Pawn", "White", i, 1);
            pieceLocations[i][6] = new ChessBoard("Pawn", "Black", i, 6);

            pieceLocations[i][0] = new ChessBoard(pieceRowPosition[i], "White", i, 0);
            pieceLocations[i][7] = new ChessBoard(pieceRowPosition[i], "Black", i, 7);
        }
    }

    // Prints out a test board for debug use
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
}
