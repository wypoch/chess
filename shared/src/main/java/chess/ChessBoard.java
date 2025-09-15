package chess;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    ChessPiece[][] board = new ChessPiece[8][8];

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(board, that.board);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }

    public ChessBoard() {

    }

    /**
     * Removes a chess piece from the chessboard
     *
     * @param position where to remove the piece from
     */
    public void removePiece(ChessPosition position) {
        board[position.getRow()-1][position.getColumn()-1] = null;
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        board[position.getRow()-1][position.getColumn()-1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return board[position.getRow()-1][position.getColumn()-1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        // Define the ordering of the pieces on the base row
        var basePieces = new ChessPiece.PieceType[]{ChessPiece.PieceType.ROOK,
                ChessPiece.PieceType.KNIGHT,
                ChessPiece.PieceType.BISHOP,
                ChessPiece.PieceType.QUEEN,
                ChessPiece.PieceType.KING,
                ChessPiece.PieceType.BISHOP,
                ChessPiece.PieceType.KNIGHT,
                ChessPiece.PieceType.ROOK};

        for (int i = 0; i < 8; i++) {
            // Define white piece positions, and add the pieces in column i + 1
            var whitePos1 = new ChessPosition(1, i + 1);
            var whitePos2 = new ChessPosition(2, i+1);
            addPiece(whitePos1, new ChessPiece(ChessGame.TeamColor.WHITE, basePieces[i]));
            addPiece(whitePos2, new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN));

            // Define black piece positions, and add the pieces in column i + 1
            var blackPos1 = new ChessPosition(8, i + 1);
            var blackPos2 = new ChessPosition(7, i+1);
            addPiece(blackPos1, new ChessPiece(ChessGame.TeamColor.BLACK, basePieces[i]));
            addPiece(blackPos2, new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));
        }
    }
}
