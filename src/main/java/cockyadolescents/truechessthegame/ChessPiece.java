package cockyadolescents.truechessthegame;

import javafx.util.Pair;
import java.util.Vector;

public class ChessPiece {
    String pieceType, pieceColor;       // The type of piece (knight, pawn, ...) and the side it belongs to
    int pieceX, pieceY, pieceValue;     // The piece position and its value upon being captured
    int[][] possibleMoves;              // 2d array storing all the possible coordinates to move to
    boolean hasMoved = false;           // Exclusively for pawns so that they don't move 2 up on the next move
    boolean canPromote = false;         // Exclusively for pawns so that they can promote

    // 2d array storing all the pieces on the chessboard
    public static ChessPiece[][] ChessBoard = new ChessPiece[8][8];

    // This is the class object initialization where a piece is created
    public ChessPiece(String pieceType, String pieceColor, int pieceX, int pieceY) {
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
        ChessBoard[this.pieceX][this.pieceY] = this;
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
                boolean noTwoFoward = this.hasMoved;
                for (int i = 0; i < 4; i++) {
                    Pair<Integer, Integer> newCoords = new Pair<>(this.possibleMoves[i][0] + this.pieceX, this.possibleMoves[i][1] + this.pieceY);

                    // Checks if this new location would be inbound
                    if (!ChessPiece.isOutOfBound(newCoords.getKey(), newCoords.getValue())) {
                        ChessPiece pieceOnThatPosition = ChessPiece.ChessBoard[newCoords.getKey()][newCoords.getValue()];
                        // Case : Pawn moves forward
                        if (i % 2 == 1) {
                            if (pieceOnThatPosition == null) {
                                if (i == 3 && noTwoFoward) continue;
                                moves.add(newCoords);
                            }
                            else if (i == 1) noTwoFoward = true;
                        }
                        // Case : Pawn takes enemy piece on its two diagonals
                        else  {
                            if (pieceOnThatPosition == null) continue;
                            if (!pieceOnThatPosition.pieceColor.equals(this.pieceColor)) moves.add(newCoords);
                        }
                    }
                }
                break;
            case "Knight": case "King":
                for (int[] coord : this.possibleMoves) {
                    Pair<Integer, Integer> newCoords = new Pair<>(coord[0] + this.pieceX, coord[1] + this.pieceY);

                    // Checks if this new location would be inbound
                    if (ChessPiece.isOutOfBound(newCoords.getKey(), newCoords.getValue())) continue;

                    ChessPiece pieceOnThatPosition = ChessPiece.ChessBoard[newCoords.getKey()][newCoords.getValue()];

                    // Checks so that the piece doesn't land on a spot with a piece on the same side
                    if (pieceOnThatPosition == null || !pieceOnThatPosition.pieceColor.equals(this.pieceColor)) moves.add(newCoords);
                }

                // Castling logic
                if (this.pieceType.equals("King") && !this.hasMoved) {
                    // Castle on the left
                    ChessPiece[] leftCastle = {
                            ChessPiece.ChessBoard[0][this.pieceY],
                            ChessPiece.ChessBoard[1][this.pieceY],
                            ChessPiece.ChessBoard[2][this.pieceY],
                            ChessPiece.ChessBoard[3][this.pieceY],
                    };
                    if (leftCastle[0] != null && leftCastle[0].pieceType.equals("Rook") && leftCastle[0].pieceColor.equals(this.pieceColor)) {
                        if (leftCastle[1] == null && leftCastle[2] == null && leftCastle[3] == null) {
                            moves.add(new Pair<>(2, this.pieceY));
                        }
                    }

                    // Castle on the right
                    ChessPiece[] rightCastle = {
                            ChessPiece.ChessBoard[5][this.pieceY],
                            ChessPiece.ChessBoard[6][this.pieceY],
                            ChessPiece.ChessBoard[7][this.pieceY],
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
                    if (ChessPiece.isOutOfBound(newCoords.getKey(), newCoords.getValue())) continue;

                    ChessPiece pieceOnThatPosition = ChessPiece.ChessBoard[newCoords.getKey()][newCoords.getValue()];
                    // Checks so that the piece doesn't land on a spot with a piece on the same side
                    if (pieceOnThatPosition == null) moves.add(newCoords);
                    else {
                        if (!pieceOnThatPosition.pieceColor.equals(this.pieceColor)) moves.add(newCoords);

                        // Skips the moves behind an enemy piece
                        if (ChessPiece.ChessBoard[newCoords.getKey()][newCoords.getValue()] != null) {
                            i += 7 - Math.max(Math.abs(coord[0]), Math.abs(coord[1]));
                        }
                    }
                }
                break;
        }

        return moves;
    }

    // Method to see if either king are in check, and return which side is checked
    public static String checkChecking() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                ChessPiece piece = ChessPiece.ChessBoard[i][j];
                if (piece == null) continue;
                for (Pair<Integer, Integer> coord : piece.getPossibleMoves()) {
                    // If it passes all these restrictions, then a castle must have happened
                    ChessPiece targetPiece = ChessPiece.ChessBoard[coord.getKey()][coord.getValue()];
                    if (targetPiece == null)                             continue; // Is not target a piece on the board
                    if (targetPiece.pieceColor.equals(piece.pieceColor)) continue; // Is targeting a piece on the same team
                    if (!targetPiece.pieceType.equals("King"))           continue; // Is targeting a piece that is not a king
                    // Returns which side is checked
                    return targetPiece.pieceColor;
                }
            }}
        return "None";
    }

    // Swaps the position of the piece to a new position
    public static void moveChessPiece(ChessPiece piece, int newX, int newY) {
        ChessBoard[newX][newY] = piece;
        ChessBoard[piece.pieceX][piece.pieceY] = null;
        piece.pieceX = newX; piece.pieceY = newY;

        // Pawn promotion
        if (piece.pieceType.equals("Pawn")) {
            if (piece.pieceColor.equals("White") && piece.pieceY == 7) piece.canPromote = true;
            if (piece.pieceColor.equals("Black") && piece.pieceY == 0) piece.canPromote = true;
        }
    }

    // Creates a basic chess start board
    public static void newGame() {
        String[] pieceRowPosition = {"Rook", "Knight", "Bishop", "Queen", "King", "Bishop", "Knight", "Rook"};

        for (int i = 0; i < 8; i++) {
            ChessBoard[i][1] = new ChessPiece("Pawn", "White", i, 1);
            ChessBoard[i][6] = new ChessPiece("Pawn", "Black", i, 6);

            ChessBoard[i][0] = new ChessPiece(pieceRowPosition[i], "White", i, 0);
            ChessBoard[i][7] = new ChessPiece(pieceRowPosition[i], "Black", i, 7);
        }
    }

    // Prints out a test board for debug use
    public static String printBoard() {
        StringBuilder builder = new StringBuilder();
        for (int y = 7; y >= 0; y--) {
            for (int x = 0; x < 8; x++) {
                ChessPiece piece = ChessBoard[x][y];
                builder.append((piece != null) ? piece.pieceType.charAt(0) + " " : ". ");
            }
            builder.append("\n");
        }
        return builder.toString();
    }
}
