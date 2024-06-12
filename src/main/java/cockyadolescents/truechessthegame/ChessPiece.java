package cockyadolescents.truechessthegame;

import javafx.util.Pair;
import java.util.Vector;

public class ChessPiece {
    String pieceType, pieceColor;       // The type of piece (knight, pawn, ...) and the side it belongs to
    int pieceX, pieceY, pieceValue;     // The piece position and its value upon being captured
    int[][] possibleMoves;              // 2d array storing all the possible coordinates to move to
    boolean hasMoved = false;           // Exclusively for pawns so that they don't move 2 up on the next move
    boolean canPromote = false;         // Exclusively for pawns so that they can promote
    boolean enpassant = false;          // Boolean for en passant rule
    int numOfMoves = 0;

    // 2d array storing all the pieces on the chessboard
    public static ChessPiece[][] ChessBoard = new ChessPiece[8][8];

    // Extra chessboard to check if a move would result in a check
    public static ChessPiece[][] CheckBoard = new ChessPiece[8][8];

    // Scoring system during the game
    public static int[] score = {1, 1};

    // This is the class object initialization where a piece is created
    public ChessPiece(String pieceType, String pieceColor, int pieceX, int pieceY) {
        // Set the basic values for this Chess Piece
        this.pieceType = pieceType; this.pieceColor = pieceColor;
        this.pieceX = pieceX; this.pieceY = pieceY;

        // Gives it all its possible moves according to its pieceType
        this.possibleMoves = getInitialPossibleMoves(this);

        // Finds its pieceValue
        this.pieceValue = switch(this.pieceType) {
            case "Pawn" -> 1;
            case "Knight", "Bishop" -> 3;
            case "Rook" -> 5;
            case "Queen" -> 9;
            case "King" -> 1000000;
            default -> -1;
        };

        // Sets the piece on the board
        ChessBoard[this.pieceX][this.pieceY] = this;
    }

    // This is the constructor for the CheckPiece 2d array instead
    public ChessPiece(ChessPiece piece) {
        // Set the basic values for this Chess Piece
        this.pieceType = piece.pieceType; this.pieceColor = piece.pieceColor;
        this.pieceX = piece.pieceX; this.pieceY = piece.pieceY;

        // Gives it all its possible moves according to its pieceType
        this.possibleMoves = piece.possibleMoves;
    }

    // Find the possible moves according to its pieceType
    private static int[][] getInitialPossibleMoves(ChessPiece piece) {
        // Return nothing if the pieceTile is empty
        if (piece == null) return null;

        // Simplifying the straight and diagonal directions
        int[][] hDirection = {{0, 1}, {0, -1}, {-1, 0}, {1, 0}};
        int[][] dDirection = {{1, -1}, {-1, -1}, {1, 1}, {-1, 1}};

        // An array of possible moves are assigned to each respective chess piece
        int[][] possibleMoves;

        return switch (piece.pieceType) {
            case "Pawn" -> (piece.pieceColor.equals("White")) ? new int[][]{{-1, 1}, {0, 1}, {1, 1}, {0, 2}} : new int[][]{{-1, -1}, {0, -1}, {1, -1}, {0, -2}};
            case "Knight" -> {
                possibleMoves = new int[8][2];
                for (int i = 0; i < 4; i++) {
                    possibleMoves[i] = new int[]{2 * dDirection[i][0], dDirection[i][1]};
                    possibleMoves[i + 4] = new int[]{dDirection[i][0], 2 * dDirection[i][1]};
                }
                yield possibleMoves;
            }
            case "Rook" -> {
                possibleMoves = new int[28][2];
                for (int i = 0; i < 4; i++) {
                    for (int j = 1; j < 8; j++) {
                        possibleMoves[7 * i + j - 1] = new int[]{hDirection[i][0] * j, hDirection[i][1] * j};
                    }}
                yield possibleMoves;
            }
            case "Bishop" -> {
                possibleMoves = new int[28][2];
                for (int i = 0; i < 4; i++) {
                    for (int j = 1; j < 8; j++) {
                        possibleMoves[7 * i + j - 1] = new int[]{dDirection[i][0] * j, dDirection[i][1] * j};
                    }}
                yield possibleMoves;
            }
            case "Queen" -> {
                possibleMoves = new int[56][2];
                for (int i = 0; i < 4; i++) {
                    for (int j = 1; j < 8; j++) {
                        possibleMoves[7 * i + j - 1] = new int[]{hDirection[i][0] * j, hDirection[i][1] * j};
                        possibleMoves[28 + (7 * i + j - 1)] = new int[]{dDirection[i][0] * j, dDirection[i][1] * j};
                    }}
                yield possibleMoves;
            }
            case "King" -> new int[][] {
                    hDirection[0], hDirection[1], hDirection[2], hDirection[3],
                    dDirection[0], dDirection[1], dDirection[2], dDirection[3],
            };
            default -> null;
        };
    }

    // This method checks if the given coordinate is out of the chessboard's bound
    private static boolean isOutOfBound(int x, int y) {
        return x < 0 || x >= 8 || y < 0 || y >= 8;
    }

    // This method returns a vector of pairs that indicate every plausible move the selected piece
    public Vector<Pair<Integer, Integer>> getPossibleMoves(ChessPiece[][] board) {
        Vector<Pair<Integer, Integer>> moves = new Vector<>();

        return switch (this.pieceType) {
            case "Pawn" -> {
                boolean noTwoFoward = this.hasMoved;
                for (int i = 0; i < 4; i++) {
                    Pair<Integer, Integer> newCoords = new Pair<>(this.possibleMoves[i][0] + this.pieceX, this.possibleMoves[i][1] + this.pieceY);

                    // Checks if this new location would be inbound
                    if (!ChessPiece.isOutOfBound(newCoords.getKey(), newCoords.getValue())) {
                        ChessPiece pieceOnThatPosition = board[newCoords.getKey()][newCoords.getValue()];
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
                            if (pieceOnThatPosition == null) {
                                // En passant rule
                                ChessPiece adjPiece = board[newCoords.getKey()][newCoords.getValue() - 1];
                                if (adjPiece != null && !adjPiece.pieceColor.equals(this.pieceColor) &&
                                    adjPiece.pieceType.equals("Pawn") && this.numOfMoves >= 3 && adjPiece.enpassant) {
                                    moves.add(new Pair<>(newCoords.getKey(), newCoords.getValue()));
                                }
                                continue;
                            }
                            if (!pieceOnThatPosition.pieceColor.equals(this.pieceColor)) moves.add(newCoords);
                        }
                    }
                }
                yield moves;
            }
            case "Knight", "King" -> {
                for (int[] coord : this.possibleMoves) {
                    Pair<Integer, Integer> newCoords = new Pair<>(coord[0] + this.pieceX, coord[1] + this.pieceY);

                    // Checks if this new location would be inbound
                    if (ChessPiece.isOutOfBound(newCoords.getKey(), newCoords.getValue())) continue;

                    ChessPiece pieceOnThatPosition = board[newCoords.getKey()][newCoords.getValue()];

                    // Checks so that the piece doesn't land on a spot with a piece on the same side
                    if (pieceOnThatPosition == null || !pieceOnThatPosition.pieceColor.equals(this.pieceColor))
                        moves.add(newCoords);
                }

                // Castling logic
                if (this.pieceType.equals("King") && !this.hasMoved) {
                    // Castle on the left
                    ChessPiece[] leftCastle = {
                            board[0][this.pieceY],
                            board[1][this.pieceY],
                            board[2][this.pieceY],
                            board[3][this.pieceY],
                    };
                    if (leftCastle[0] != null && leftCastle[0].pieceType.equals("Rook") && leftCastle[0].pieceColor.equals(this.pieceColor)) {
                        if (leftCastle[1] == null && leftCastle[2] == null && leftCastle[3] == null) {
                            moves.add(new Pair<>(2, this.pieceY));
                        }
                    }

                    // Castle on the right
                    ChessPiece[] rightCastle = {
                            board[5][this.pieceY],
                            board[6][this.pieceY],
                            board[7][this.pieceY],
                    };

                    if (rightCastle[2] != null && rightCastle[2].pieceType.equals("Rook") && rightCastle[2].pieceColor.equals(this.pieceColor)) {
                        if (rightCastle[0] == null && rightCastle[1] == null) {
                            moves.add(new Pair<>(6, this.pieceY));
                        }
                    }
                }
                yield moves;
            }
            case "Bishop", "Rook", "Queen" -> {
                for (int i = 0; i < this.possibleMoves.length; i++) {
                    int[] coord = this.possibleMoves[i];
                    Pair<Integer, Integer> newCoords = new Pair<>(coord[0] + this.pieceX, coord[1] + this.pieceY);

                    // Checks if this new location would be inbound
                    if (ChessPiece.isOutOfBound(newCoords.getKey(), newCoords.getValue())) continue;

                    ChessPiece pieceOnThatPosition = board[newCoords.getKey()][newCoords.getValue()];
                    // Checks so that the piece doesn't land on a spot with a piece on the same side
                    if (pieceOnThatPosition == null) moves.add(newCoords);
                    else {
                        if (!pieceOnThatPosition.pieceColor.equals(this.pieceColor)) moves.add(newCoords);

                        // Skips the moves behind an enemy piece
                        if (board[newCoords.getKey()][newCoords.getValue()] != null) {
                            i += 7 - Math.max(Math.abs(coord[0]), Math.abs(coord[1]));
                        }
                    }
                }
                yield moves;
            }
            default -> moves;
        };
    }

    // En-passant rule : Can only happen directly after pawn moved 2 up
    public static void turnOffEnPassant() {
        for (int i = 0; i < 8; i++) {
        for (int j = 0; j < 8; j++) {
            if (ChessBoard[i][j] != null) ChessBoard[i][j].enpassant = false;
        }}
    }

    // Method to see if either king are in check, and return which side is checked
    public static String checkChecking(ChessPiece[][] board) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                ChessPiece piece = board[i][j];
                if (piece == null) continue;
                for (Pair<Integer, Integer> coord : piece.getPossibleMoves(board)) {
                    // If it passes all these restrictions, then a castle must have happened
                    ChessPiece targetPiece = board[coord.getKey()][coord.getValue()];
                    if (targetPiece == null)                             continue; // Is not target a piece on the board
                    if (targetPiece.pieceColor.equals(piece.pieceColor)) continue; // Is targeting a piece on the same team
                    if (!targetPiece.pieceType.equals("King"))           continue; // Is targeting a piece that is not a king
                    // Returns which side is checked
                    return targetPiece.pieceColor;
                }
            }}
        return "None";
    }

    // Recreate board with one move ahead to check if it would result in a check
    public static boolean checkCheckingOnNextMove(ChessPiece piece, int newX, int newY) {
        // We want the king to always be able to move in this version of chess
        if (piece.pieceType.equals("King")) return false;

        // Save the piece that is on coord newX and newY
        ChessPiece tempPiece = (ChessBoard[newX][newY] == null) ? null : new ChessPiece(ChessBoard[newX][newY]);

        // Moves the piece
        CheckBoard[newX][newY] = new ChessPiece(piece);
        CheckBoard[piece.pieceX][piece.pieceY] = null;

        // Check if the king on the same side is checked
        boolean isChecked = checkChecking(CheckBoard).equals(piece.pieceColor);

        // Moves the piece back
        CheckBoard[newX][newY] = tempPiece;
        CheckBoard[piece.pieceX][piece.pieceY] = new ChessPiece(piece);

        return isChecked;
    }

    // Copies the Chessboard 2D array to a temporary Chessboard array (to find checks)
    public static void copyChessBoardToCheckBoard() {
        // Recreates the board
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (ChessBoard[i][j] == null) CheckBoard[i][j] = null;
                else CheckBoard[i][j] = new ChessPiece(ChessBoard[i][j]);
            }}
    }

    // Swaps the position of the piece to a new position
    public static void moveChessPiece(ChessPiece piece, int newX, int newY) {
        // Add to score
        ChessPiece targetTile = ChessBoard[newX][newY];
        if (targetTile != null) {
            score[((piece.pieceColor.equals("White")) ? 0 : 1)] += targetTile.pieceValue;
        }

        // Record values for the pawn en-passant
        turnOffEnPassant();
        if (piece.pieceType.equals("Pawn")) {
            if (Math.abs(piece.pieceY - newY) == 2) {
                piece.enpassant = true;
                System.out.println("RAN !");
            }
            piece.numOfMoves++;

            // Checks if piece went on an en-passant
            if (targetTile == null && Math.abs(piece.pieceX - newX) == 1) {
                score[((piece.pieceColor.equals("White")) ? 0 : 1)] += ChessBoard[newX][piece.pieceY].pieceValue;
                ChessBoard[newX][piece.pieceY] = null;
            }
        }

        // Move piece
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
        ChessBoard = new ChessPiece[8][8];

        for (int i = 0; i < 8; i++) {
            ChessBoard[i][1] = new ChessPiece("Pawn", "White", i, 1);
            ChessBoard[i][6] = new ChessPiece("Pawn", "Black", i, 6);

            ChessBoard[i][0] = new ChessPiece(pieceRowPosition[i], "White", i, 0);
            ChessBoard[i][7] = new ChessPiece(pieceRowPosition[i], "Black", i, 7);
        }

        // Reset score
        score = new int[]{1, 1};
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
