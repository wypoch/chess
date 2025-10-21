package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor currTeam = TeamColor.WHITE;
    private ChessBoard currBoard;

    public ChessGame() {
        // Set up an immediately playable board
        currBoard = new ChessBoard();
        currBoard.resetBoard();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return currTeam == chessGame.currTeam && Objects.equals(currBoard, chessGame.currBoard);
    }

    @Override
    public int hashCode() {
        return Objects.hash(currTeam, currBoard);
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return currTeam;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        currTeam = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {

        HashSet<ChessMove> allValidMoves = new HashSet<>();

        // Return null if there is no piece at the startPosition
        ChessPiece piece = currBoard.getPiece(startPosition);
        if (piece == null) {
            return null;
        }

        // Create a new copy of the board for each move to test whether the move puts the king in check
        var possMoves = piece.pieceMoves(currBoard, startPosition);
        for (ChessMove move : possMoves) {
            var boardCopy = new ChessBoard();

            // Make a copy of the board
            for (int row = 1; row <= 8; row++) {
                for (int col = 1; col <= 8; col++) {
                    var piecePos = new ChessPosition(row, col);
                    boardCopy.addPiece(piecePos, currBoard.getPiece(piecePos));
                }
            }

            // Simulate moving the piece and check our team's king goes into check; if not, consider the move valid
            boardCopy.movePiece(move.getStartPosition(), move.getEndPosition(), move.getPromotionPiece());
            var inCheck = isInCheckBoard(piece.getTeamColor(), boardCopy);
            if (!inCheck) {
                allValidMoves.add(move);
            }
        }

        return allValidMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {

        var startPos = move.getStartPosition();
        ChessPiece piece = currBoard.getPiece(startPos);

        // Check the piece to be moved exists
        if (piece == null) {
            throw new InvalidMoveException();
        }
        // Check the piece to be moved is on the current team
        else if (piece.getTeamColor() != currTeam) {
            throw new InvalidMoveException();
        }
        // Check that the move is valid in terms of chess rules
        boolean isValid = false;
        for (var possMove : this.validMoves(startPos)) {
            if (move.equals(possMove)) {
                isValid = true;
                break;
            }
        }
        if (!isValid) {
            throw new InvalidMoveException();
        }

        // If so, move the piece
        currBoard.movePiece(move.getStartPosition(), move.getEndPosition(), move.getPromotionPiece());

        // Change the current team
        if (currTeam == TeamColor.WHITE) {
            currTeam = TeamColor.BLACK;
        } else {
            currTeam = TeamColor.WHITE;
        }
    }

    /**
     * Determines the position of the king belonging to the specified team
     * @param teamColor king color
     * @return position of the king
     */
    public ChessPosition getKingPos(TeamColor teamColor, ChessBoard board) {
        int currRow = 1;
        for (var row : board.board ) {
            int currCol = 1;
            for (ChessPiece piece : row) {
                if (piece != null && piece.getTeamColor() == teamColor && piece.getPieceType() == ChessPiece.PieceType.KING) {
                    return new ChessPosition(currRow, currCol);
                }
                currCol += 1;
            }
            currRow += 1;
        }
        return null;
    }

    public boolean isInCheckBoard(TeamColor teamColor, ChessBoard board) {
        ChessPosition kingPos = getKingPos(teamColor, board);

        // Search all enemy pieces and see if they can capture the king
        int currRow = 1;
        for (var row : board.board ) {
            int currCol = 1;
            for (ChessPiece piece : row) {
                if (piece != null && piece.getTeamColor() != teamColor) {
                    var possMoves = piece.pieceMoves(board, new ChessPosition(currRow, currCol));
                    for (var move : possMoves) {
                        if (move.getEndPosition().equals(kingPos)) {
                            return true;
                        }
                    }
                }
                currCol += 1;
            }
            currRow += 1;
        }

        return false;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        return isInCheckBoard(teamColor, currBoard);
    }

    /**
     * Determines if the given team has any valid moves
     *
     * @param teamColor which team to check for valid moves
     * @return True if the specified team has a valid move
     */
    public boolean hasValidMoves(TeamColor teamColor) {
        int currRow = 1;
        for (var row : currBoard.board ) {
            int currCol = 1;
            for (ChessPiece piece : row) {
                if (piece != null && piece.getTeamColor() == teamColor) {
                    var possMoves = validMoves(new ChessPosition(currRow, currCol));
                    if (!possMoves.isEmpty()) {
                        return false;
                    }
                }
                currCol += 1;
            }
            currRow += 1;
        }

        return true;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        // Must be in check to be in stalemate
        boolean inCheck = this.isInCheck(teamColor);
        if (!inCheck) {
            return false;
        }

        // Must have no valid moves to be in stalemate
        return hasValidMoves(teamColor);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        // Must not be in check to be in stalemate
        boolean inCheck = this.isInCheck(teamColor);
        if (inCheck) {
            return false;
        }

        // Must have no valid moves to be in stalemate
        return hasValidMoves(teamColor);
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        currBoard = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return currBoard;
    }
}
