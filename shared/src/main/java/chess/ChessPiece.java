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

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

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
     * Takes an existing move set and board, and adds the possible moves for a piece of the indicated color
     * starting from the indicated position. NOTE: does not consider whether a move leaves a king in danger.
     *
     * @param moves existing move set
     * @param startPos position of the piece
     * @param board existing board
     * @param myColor color of the piece
     */
    public void addMovesBase(HashSet<ChessMove> moves, ChessPosition startPos, ChessBoard board, ChessGame.TeamColor myColor,
                             int[][] offsets, boolean recursive) {

        for (var offset : offsets) {
            int numIters = 0;
            var currRow = startPos.getRow();
            var currCol = startPos.getColumn();

            while (true) {
                currRow += offset[0];
                currCol += offset[1];

                if (currRow < 1 || currRow > 8 || currCol < 1 || currCol > 8) {
                    break;
                }

                var newPos = new ChessPosition(currRow, currCol);
                var newPiece = board.getPiece(newPos);
                if (newPiece != null ) {
                    if (newPiece.getTeamColor() != myColor) {
                        moves.add(new ChessMove(startPos, newPos, null));
                    }
                    break;
                }
                moves.add(new ChessMove(startPos, newPos, null));
                numIters += 1;
                if (numIters == 1 && !recursive) {
                    break;
                }
            }
        }
    }

    /**
     * Takes an existing move set and board, and adds the possible moves for a pawn of the indicated color
     * starting from the indicated position. NOTE: does not consider whether a move leaves a king in danger.
     *
     * @param moves existing move set
     * @param startPos position of the pawn
     * @param board existing board
     * @param myColor color of the pawn
     */
    public void addMovesPawn(HashSet<ChessMove> moves, ChessPosition startPos, ChessBoard board, ChessGame.TeamColor myColor) {

        ChessPiece.PieceType[] promotionPieces = new ChessPiece.PieceType[]{PieceType.ROOK, PieceType.KNIGHT, PieceType.BISHOP, PieceType.QUEEN};

        int maxMoves = 1;
        var currRow = startPos.getRow();
        var currCol = startPos.getColumn();

        if (myColor == ChessGame.TeamColor.WHITE && currRow == 2) {
            maxMoves = 2;
        } else if (myColor == ChessGame.TeamColor.BLACK && currRow == 7) {
            maxMoves = 2;
        }

        // Forward moves
        while (maxMoves > 0) {
            if (myColor == ChessGame.TeamColor.WHITE) {
                currRow += 1;
            } else if (myColor == ChessGame.TeamColor.BLACK) {
                currRow -= 1;
            }

            if (currRow < 1 || currRow > 8 || currCol < 1 || currCol > 8) {
                break;
            }

            var newPos = new ChessPosition(currRow, currCol);
            var newPiece = board.getPiece(newPos);
            if (newPiece != null ) {
                break;
            }

            if ((myColor == ChessGame.TeamColor.WHITE && currRow == 8) || (myColor == ChessGame.TeamColor.BLACK && currRow == 1)) {
                for (var pPiece : promotionPieces) {
                    moves.add(new ChessMove(startPos, newPos, pPiece));
                }
            } else {
                moves.add(new ChessMove(startPos, newPos, null));
            }
            maxMoves -= 1;
        }

        // Diagonal moves
        int[][] offsets;
        if (myColor == ChessGame.TeamColor.WHITE) {
            offsets = new int[][]{{1, -1}, {1, 1}};
        } else {
            offsets = new int[][]{{-1, -1}, {-1, 1}};
        }

        for (var offset : offsets) {
            var currRow2 = startPos.getRow();
            var currCol2 = startPos.getColumn();

            currRow2 += offset[0];
            currCol2 += offset[1];

            if (currRow2 < 1 || currRow2 > 8 || currCol2 < 1 || currCol2 > 8) {
                continue;
            }

            var newPos = new ChessPosition(currRow2, currCol2);
            var newPiece = board.getPiece(newPos);
            if (newPiece != null) {
                if (newPiece.getTeamColor() != myColor) {
                    if ((myColor == ChessGame.TeamColor.WHITE && currRow2 == 8) || (myColor == ChessGame.TeamColor.BLACK && currRow2 == 1)) {
                        for (var pPiece : promotionPieces) {
                            moves.add(new ChessMove(startPos, newPos, pPiece));
                        }
                    } else {
                        moves.add(new ChessMove(startPos, newPos, null));
                    }

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
        HashSet<ChessMove> moves = new HashSet<>();
        ChessPiece currPiece = board.getPiece(myPosition);
        var myColor = currPiece.getTeamColor();

        switch (currPiece.getPieceType()) {
            case PieceType.BISHOP:
                addMovesBase(moves, myPosition, board, myColor, new int[][]{{1, -1}, {-1, 1}, {1, 1}, {-1, -1}}, true);
                break;

            case PieceType.KING:
                addMovesBase(moves, myPosition, board, myColor,
                        new int[][]{{1, -1}, {-1, 1}, {1, 1}, {-1, -1}, {0, 1}, {0, -1}, {1, 0}, {-1, 0}}, false);
                break;

            case PieceType.KNIGHT:
                addMovesBase(moves, myPosition, board, myColor,
                        new int[][]{{2, -1}, {-2, 1}, {2, 1}, {-2, -1}, {1, 2}, {1, -2}, {-1, -2}, {-1, 2}}, false);
                break;

            case PieceType.ROOK:
                addMovesBase(moves, myPosition, board, myColor,
                        new int[][]{{0, 1}, {0, -1}, {1, 0}, {-1, 0}}, true);
                break;

            case PieceType.QUEEN:
                addMovesBase(moves, myPosition, board, myColor,
                        new int[][]{{1, -1}, {-1, 1}, {1, 1}, {-1, -1}, {0, 1}, {0, -1}, {1, 0}, {-1, 0}}, true);
                break;

            case PieceType.PAWN:
                addMovesPawn(moves, myPosition, board, myColor);
                break;

            default:
                break;
        }
        return moves;
    }
}