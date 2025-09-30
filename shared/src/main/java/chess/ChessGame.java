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

    private TeamColor currTeam;
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
        // Return null if there is no piece at the startPosition
        ChessPiece piece = currBoard.getPiece(startPosition);
        ChessGame.TeamColor currColor = piece.getTeamColor();

        if (piece == null) {
            return null;
        }

        // Create a new copy of the board for each move to test whether the
        var possMoves = validMoves(startPosition);
        for (ChessMove move : possMoves) {
            ChessPosition pos = move.getStartPosition();

        }

        return new HashSet<>();
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {

        // Check that the move is valid
        var valid = this.validMoves(move.getStartPosition());
        boolean isValid = false;
        for (var possMove : valid) {
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
    public ChessPosition getKingPos(TeamColor teamColor) {
        int currRow = 1;
        for (var row : currBoard.board ) {
            int currCol = 1;
            for (ChessPiece piece : row) {
                if (piece.getTeamColor() == teamColor) {
                    if (piece.getPieceType() == ChessPiece.PieceType.KING) {
                        return new ChessPosition(currRow, currCol);
                    }
                }
                currCol += 1;
            }
            currRow += 1;
        }
        return null;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {

        ChessPosition kingPos = getKingPos(teamColor);

        // Search all enemy pieces and see if they can capture the king
        int currRow = 1;
        for (var row : currBoard.board ) {
            int currCol = 1;
            for (ChessPiece piece : row) {
                if (piece.getTeamColor() != teamColor) {
                    var possMoves = piece.pieceMoves(currBoard, new ChessPosition(currRow, currCol));
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
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
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
        int currRow = 1;
        for (var row : currBoard.board ) {
            int currCol = 1;
            for (ChessPiece piece : row) {
                if (piece.getTeamColor() == teamColor) {
                    var possMoves = piece.pieceMoves(currBoard, new ChessPosition(currRow, currCol));
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
