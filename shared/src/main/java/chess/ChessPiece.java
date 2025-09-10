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

    // Bishops move in diagonal lines as far as there is open space
    public void addBishopMoves(HashSet<ChessMove> moveSet, ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor myColor) {

        int[] offsets = {1, -1};

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

    // Kings may move 1 square in any direction (including diagonals)
    public void addKingMoves(HashSet<ChessMove> moveSet, ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor myColor) {

        int[] offsets = {-1, 0, 1};

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

    // Knights move in an L shape, moving 2 squares in one direction and 1 square in the other direction
    public void addKnightMoves(HashSet<ChessMove> moveSet, ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor myColor) {

        int[] horOffsets = {-2, -1, 1, 2, 2, 1, -1, -2};
        int[] vertOffsets = {1, 2, 2, 1, -1, -2, -2, -1};

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

    // Rooks may move in straight lines as far as there is open space
    public void addRookMoves(HashSet<ChessMove> moveSet, ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor myColor) {

        int[] offsets = {1, -1};
        boolean[] bools = {true, false};

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
                // Queens can move wherever Rooks and Bishops can move
                addRookMoves(moveSet, board, myPosition, myColor);
                addBishopMoves(moveSet, board, myPosition, myColor);
                break;

            case PieceType.PAWN:
                // addPawnMoves(moveSet, board, myPosition, myColor);
                break;

            default:
                break;
        }

        return moveSet;
    }
}
