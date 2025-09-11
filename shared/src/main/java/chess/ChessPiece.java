package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Takes an existing move set and board, and adds the possible moves for a bishop of the indicated color
     * starting from the indicated position. NOTE: does not consider whether a move leaves a king in danger.
     *
     * @param moveSet existing move set
     * @param board existing board
     * @param myPosition position of the bishop
     * @param myColor color of the bishop
     */
    public void addBishopMoves(HashSet<ChessMove> moveSet, ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor myColor) {

        int[] offsets = {1, -1};

        // Bishops move in diagonal lines as far as there is open space
        for (var horOff : offsets) {
            for (var vertOff : offsets) {
                var currRow = myPosition.getRow();
                var currCol = myPosition.getColumn();
                while (true) {
                    currRow += vertOff;
                    currCol += horOff;

                    // About to hit the edge
                    if (currRow > 8 || currRow < 1 || currCol > 8 || currCol < 1) {
                        break;
                    }

                    ChessPosition currPos = new ChessPosition(currRow, currCol);
                    ChessPiece currPiece = board.getPiece(currPos);

                    // About to land on a piece
                    if (currPiece != null) {
                        // We are about to capture the opponent's pieces
                        if (currPiece.pieceColor != myColor) {
                            moveSet.add(new ChessMove(myPosition, currPos, null));
                        }
                        break;
                    }
                    moveSet.add(new ChessMove(myPosition, currPos, null));
                }
            }
        }
    }

    /**
     * Takes an existing move set and board, and adds the possible moves for a king of the indicated color
     * starting from the indicated position. NOTE: does not consider whether a move leaves a king in danger.
     *
     * @param moveSet existing move set
     * @param board existing board
     * @param myPosition position of the king
     * @param myColor color of the king
     */
    public void addKingMoves(HashSet<ChessMove> moveSet, ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor myColor) {

        int[] offsets = {-1, 0, 1};

        // Kings may move 1 square in any direction (including diagonals)
        for (var horOff : offsets) {
            for (var vertOff : offsets) {

                var currRow = myPosition.getRow();
                var currCol = myPosition.getColumn();

                currRow += horOff;
                currCol += vertOff;

                // About to hit the edge
                if (currRow > 8 || currRow < 1 || currCol > 8 || currCol < 1) {
                    continue;
                }

                var currPos = new ChessPosition(currRow, currCol);
                ChessPiece currPiece = board.getPiece(currPos);

                // About to land on a piece
                if (currPiece != null) {
                    // We are about to capture one of our own pieces
                    if (currPiece.pieceColor == myColor) {
                        continue;
                    }
                }

                moveSet.add(new ChessMove(myPosition, currPos, null));
            }
        }
    }

    /**
     * Takes an existing move set and board, and adds the possible moves for a knight of the indicated color
     * starting from the indicated position. NOTE: does not consider whether a move leaves a king in danger.
     *
     * @param moveSet existing move set
     * @param board existing board
     * @param myPosition position of the knight
     * @param myColor color of the knight
     */
    public void addKnightMoves(HashSet<ChessMove> moveSet, ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor myColor) {

        int[] horOffsets = {-2, -1, 1, 2, 2, 1, -1, -2};
        int[] vertOffsets = {1, 2, 2, 1, -1, -2, -2, -1};

        // Knights move in an L shape, moving 2 squares in one direction and 1 square in the other direction
        for (int i = 0; i < horOffsets.length; i++) {
            var currRow = myPosition.getRow();
            var currCol = myPosition.getColumn();

            currRow += horOffsets[i];
            currCol += vertOffsets[i];

            // About to hit the edge
            if (currRow > 8 || currRow < 1 || currCol > 8 || currCol < 1) {
                continue;
            }

            var currPos = new ChessPosition(currRow, currCol);
            ChessPiece currPiece = board.getPiece(currPos);

            // About to land on a piece
            if (currPiece != null) {
                // We are about to capture the opponent's pieces
                if (currPiece.pieceColor != myColor) {
                    moveSet.add(new ChessMove(myPosition, currPos, null));
                }
                continue;
            }
            moveSet.add(new ChessMove(myPosition, currPos, null));
        }
    }

    /**
     * Takes an existing move set and board, and adds the possible moves for a rook of the indicated color
     * starting from the indicated position. NOTE: does not consider whether a move leaves a king in danger.
     *
     * @param moveSet existing move set
     * @param board existing board
     * @param myPosition position of the rook
     * @param myColor color of the rook
     */
    public void addRookMoves(HashSet<ChessMove> moveSet, ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor myColor) {

        int[] offsets = {1, -1};
        boolean[] bools = {true, false};

        // Rooks may move in straight lines as far as there is open space
        for (var off : offsets) {
            for (var isVert : bools) {
                var currRow = myPosition.getRow();
                var currCol = myPosition.getColumn();
                while (true) {
                    if (isVert) {
                        currRow += off;
                    } else {
                        currCol += off;
                    }

                    // About to hit the edge
                    if (currRow > 8 || currRow < 1 || currCol > 8 || currCol < 1) {
                        break;
                    }

                    ChessPosition currPos = new ChessPosition(currRow, currCol);
                    ChessPiece currPiece = board.getPiece(currPos);

                    // About to land on a piece
                    if (currPiece != null) {
                        // We are about to capture the opponent's pieces
                        if (currPiece.pieceColor != myColor) {
                            moveSet.add(new ChessMove(myPosition, currPos, null));
                        }
                        break;
                    }
                    moveSet.add(new ChessMove(myPosition, currPos, null));
                }
            }
        }
    }

    /**
     * Takes an existing move set and board, and adds the possible moves for a queen of the indicated color
     * starting from the indicated position. NOTE: does not consider whether a move leaves a king in danger.
     *
     * @param moveSet existing move set
     * @param board existing board
     * @param myPosition position of the queen
     * @param myColor color of the queen
     */
    public void addQueenMoves(HashSet<ChessMove> moveSet, ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor myColor) {
        // Queens can move wherever Rooks and Bishops can move
        addRookMoves(moveSet, board, myPosition, myColor);
        addBishopMoves(moveSet, board, myPosition, myColor);
    }

    /**
     * Takes an existing move set and board, and adds the possible moves for a pawn of the indicated color
     * starting from the indicated position. NOTE: does not consider whether a move leaves a king in danger.
     *
     * @param moveSet existing move set
     * @param board existing board
     * @param myPosition position of the pawn
     * @param myColor color of the pawn
     */
    public void addPawnMoves(HashSet<ChessMove> moveSet, ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor myColor) {

        boolean isFirstMove = false;
        var currRow = myPosition.getRow();
        var currCol = myPosition.getColumn();
        var promotionPieces = new PieceType[]{PieceType.ROOK, PieceType.KNIGHT, PieceType.BISHOP, PieceType.QUEEN};

        if (myColor == ChessGame.TeamColor.WHITE && currRow == 2) {
            isFirstMove = true;
        } else if (myColor == ChessGame.TeamColor.BLACK && currRow == 7) {
            isFirstMove = true;
        }

        // Moving forward one or two squares, depending on whether it is the first move
        int maxSquares = 1;
        if (isFirstMove) {
            maxSquares = 2;
        }

        while (maxSquares > 0) {
            // Move forward one square
            if (myColor == ChessGame.TeamColor.WHITE) {
                currRow += 1;
            } else {
                currRow -= 1;
            }
            maxSquares -= 1;

            // About to hit the edge
            if (currRow > 8 || currRow < 1 || currCol > 8 || currCol < 1) {
                break;
            }

            ChessPosition currPos = new ChessPosition(currRow, currCol);
            ChessPiece currPiece = board.getPiece(currPos);
            // About to land on a piece; cannot capture
            if (currPiece != null) {
                break;
            }
            // If we have reached the end of the board, include promotion pieces
            if (currRow == 1 || currRow == 8) {
                for (var possPiece : promotionPieces) {
                    moveSet.add(new ChessMove(myPosition, currPos, possPiece));
                }

            }
            else {
                moveSet.add(new ChessMove(myPosition, currPos, null));
            }
        }

        // Capturing an opposing diagonal piece
        currRow = myPosition.getRow();
        currCol = myPosition.getColumn();

        // Get the rows and columns of the possible moves
        int[] possRows = {0, 0};
        if (myColor == ChessGame.TeamColor.WHITE) {
            possRows[0] = currRow + 1;
            possRows[1] = currRow + 1;
        } else {
            possRows[0] = currRow - 1;
            possRows[1] = currRow - 1;
        }

        int[] possCols = {currCol - 1, currCol + 1};

        // Check all the possible diagonal moves
        for (int i = 0; i < possRows.length; i++) {
            currRow = possRows[i];
            currCol = possCols[i];

            // About to hit the edge
            if (currRow > 8 || currRow < 1 || currCol > 8 || currCol < 1) {
                continue;
            }

            var currPos = new ChessPosition(currRow, currCol);
            ChessPiece currPiece = board.getPiece(currPos);

            // About to land on a piece
            if (currPiece != null) {
                // We are about to capture one of our own pieces
                if (currPiece.pieceColor == myColor) {
                    continue;
                }

                // If we have reached the end of the board, include promotion pieces
                if (currRow == 1 || currRow == 8) {
                    for (var possPiece : promotionPieces) {
                        moveSet.add(new ChessMove(myPosition, currPos, possPiece));
                    }
                }
                else {
                    moveSet.add(new ChessMove(myPosition, currPos, null));
                }
            }
        }
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        var piece = board.getPiece(myPosition);
        var myColor = piece.getTeamColor();
        HashSet<ChessMove> moveSet = new HashSet<>();

        switch (piece.getPieceType()) {
            case PieceType.BISHOP:
                addBishopMoves(moveSet, board, myPosition, myColor);
                break;

            case PieceType.KING:
                addKingMoves(moveSet, board, myPosition, myColor);
                break;

            case PieceType.KNIGHT:
                addKnightMoves(moveSet, board, myPosition, myColor);
                break;

            case PieceType.ROOK:
                addRookMoves(moveSet, board, myPosition, myColor);
                break;

            case PieceType.QUEEN:
                addQueenMoves(moveSet, board, myPosition, myColor);
                break;

            case PieceType.PAWN:
                addPawnMoves(moveSet, board, myPosition, myColor);
                break;

            default:
                break;
        }

        return moveSet;
    }
}
