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

    static public class BaseMoves {

        /**
         * Takes an existing board, and adds the possible moves for a piece of the indicated color starting from the indicated position.
         * The piece moves in coordinate steps included in the offsets list, with optional recursion.
         * NOTE: does not consider whether a move leaves a king in danger.
         *
         * @param startPos  position of the piece
         * @param board     existing board
         * @param myColor   color of the piece
         * @param offsets   directions the piece can move
         * @param recursive whether the piece can make multiple moves in the direction of an offset
         *
         * @return Collection of valid moves
         */
        public HashSet<ChessMove> computeMovesBase(ChessPosition startPos, ChessBoard board, ChessGame.TeamColor myColor,
                                 int[][] offsets, boolean recursive) {

            HashSet<ChessMove> moves = new HashSet<>();

            // Consider all the possible direction we can move
            for (var offset : offsets) {
                var currRow = startPos.getRow();
                var currCol = startPos.getColumn();

                while (true) {
                    currRow += offset[0];
                    currCol += offset[1];

                    // Reached end of board; cannot move forward
                    if (currRow < 1 || currRow > 8 || currCol < 1 || currCol > 8) {
                        break;
                    }

                    // Move the piece, if our own piece is not in the way
                    var newPos = new ChessPosition(currRow, currCol);
                    var newPiece = board.getPiece(newPos);
                    if (newPiece != null) {
                        // Capture enemy piece and exit the loop
                        if (newPiece.getTeamColor() != myColor) {
                            moves.add(new ChessMove(startPos, newPos, null));
                        }
                        break;
                    }
                    moves.add(new ChessMove(startPos, newPos, null));

                    // Only recurse if instructed to
                    if (!recursive) {
                        break;
                    }
                }
            }
            return moves;
        }
    }

    static public class BishopMoves extends BaseMoves {
        /**
         * Takes an existing board, and adds the possible moves for a bishop of the indicated color starting from the indicated position.
         * NOTE: does not consider whether a move leaves a king in danger.
         *
         * @param startPos position of the piece
         * @param board    existing board
         * @param myColor  color of the piece
         *
         * @return Collection of valid moves
         */
        public HashSet<ChessMove> computeMovesBishop(ChessPosition startPos, ChessBoard board, ChessGame.TeamColor myColor) {
            int[][] bishopOffsets = new int[][]{{1, -1}, {-1, 1}, {1, 1}, {-1, -1}};
            return computeMovesBase(startPos, board, myColor, bishopOffsets, true);
        }
    }

    static public class KingMoves extends BaseMoves {
        /**
         * Takes an existing board, and adds the possible moves for a king of the indicated color starting from the indicated position.
         * NOTE: does not consider whether a move leaves a king in danger.
         *
         * @param startPos position of the piece
         * @param board    existing board
         * @param myColor  color of the piece
         *
         * @return Collection of valid moves
         */
        public HashSet<ChessMove> computeMovesKing(ChessPosition startPos, ChessBoard board, ChessGame.TeamColor myColor) {
            int[][] kingOffsets = new int[][]{{1, -1}, {-1, 1}, {1, 1}, {-1, -1}, {0, 1}, {0, -1}, {1, 0}, {-1, 0}};
            return computeMovesBase(startPos, board, myColor, kingOffsets, false);
        }
    }

    static public class KnightMoves extends BaseMoves {
        /**
         * Takes an existing board, and adds the possible moves for a knight of the indicated color starting from the indicated position.
         * NOTE: does not consider whether a move leaves a king in danger.
         *
         * @param startPos position of the piece
         * @param board    existing board
         * @param myColor  color of the piece
         *
         * @return Collection of valid moves
         */
        public HashSet<ChessMove> computeMovesKnight(ChessPosition startPos, ChessBoard board, ChessGame.TeamColor myColor) {
            int[][] knightOffsets = new int[][]{{2, -1}, {-2, 1}, {2, 1}, {-2, -1}, {1, 2}, {1, -2}, {-1, -2}, {-1, 2}};
            return computeMovesBase(startPos, board, myColor, knightOffsets, false);
        }
    }

    static public class RookMoves extends BaseMoves {
        /**
         * Takes an existing board, and adds the possible moves for a rook of the indicated color starting from the indicated position.
         * NOTE: does not consider whether a move leaves a king in danger.
         *
         * @param startPos position of the piece
         * @param board    existing board
         * @param myColor  color of the piece
         *
         * @return Collection of valid moves
         */
        public HashSet<ChessMove> computeMovesRook(ChessPosition startPos, ChessBoard board, ChessGame.TeamColor myColor) {
            int[][] rookOffsets = new int[][]{{0, 1}, {0, -1}, {1, 0}, {-1, 0}};
            return computeMovesBase(startPos, board, myColor, rookOffsets, true);
        }
    }

    static public class QueenMoves extends BaseMoves {
        /**
         * Takes an existing board, and adds the possible moves for a queen of the indicated color starting from the indicated position.
         * NOTE: does not consider whether a move leaves a king in danger.
         *
         * @param startPos position of the piece
         * @param board    existing board
         * @param myColor  color of the piece
         *
         * @return Collection of valid moves
         */
        public HashSet<ChessMove> computeMovesQueen(ChessPosition startPos, ChessBoard board, ChessGame.TeamColor myColor) {
            int[][] queenOffsets = new int[][]{{1, -1}, {-1, 1}, {1, 1}, {-1, -1}, {0, 1}, {0, -1}, {1, 0}, {-1, 0}};
            return computeMovesBase(startPos, board, myColor, queenOffsets, true);
        }
    }

    static public class PawnMoves {
        /**
         * Takes an existing move set and board, and adds the possible moves for a pawn of the indicated color starting from the indicated position.
         * NOTE: does not consider whether a move leaves a king in danger.
         *
         * @param startPos position of the pawn
         * @param board existing board
         * @param myColor color of the pawn
         *
         * @return Collection of valid moves
         */
        public HashSet<ChessMove> computeMovesPawn(ChessPosition startPos, ChessBoard board, ChessGame.TeamColor myColor) {
            HashSet<ChessMove> moves = new HashSet<>();

            ChessPiece.PieceType[] promotionPieces = new ChessPiece.PieceType[]{PieceType.ROOK, PieceType.KNIGHT, PieceType.BISHOP, PieceType.QUEEN};

            int maxMoves = 1;
            var currRow = startPos.getRow();
            var currCol = startPos.getColumn();

            // Determine if this is the pawn's first move
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

                // Reached end of board; cannot move forward
                if (currRow < 1 || currRow > 8 || currCol < 1 || currCol > 8) {
                    break;
                }

                // Cannot capture a piece when moving forward
                var newPos = new ChessPosition(currRow, currCol);
                var newPiece = board.getPiece(newPos);
                if (newPiece != null ) {
                    break;
                }

                // Move the pawn forward, and potentially promote it
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

                // Reached end of board; cannot move forward
                if (currRow2 < 1 || currRow2 > 8 || currCol2 < 1 || currCol2 > 8) {
                    continue;
                }

                // Move the pawn diagonally, if possible, and potentially promote it
                var newPos = new ChessPosition(currRow2, currCol2);
                var newPiece = board.getPiece(newPos);
                if (newPiece != null && newPiece.getTeamColor() != myColor) {
                    if ((myColor == ChessGame.TeamColor.WHITE && currRow2 == 8) || (myColor == ChessGame.TeamColor.BLACK && currRow2 == 1)) {
                        for (var pPiece : promotionPieces) {
                            moves.add(new ChessMove(startPos, newPos, pPiece));
                        }
                    } else {
                        moves.add(new ChessMove(startPos, newPos, null));
                    }
                }
            }
            return moves;
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
        ChessPiece currPiece = board.getPiece(myPosition);
        var myColor = currPiece.getTeamColor();

        return switch (currPiece.getPieceType()) {
            case PieceType.BISHOP -> new BishopMoves().computeMovesBishop(myPosition, board, myColor);
            case PieceType.KING -> new KingMoves().computeMovesKing(myPosition, board, myColor);
            case PieceType.KNIGHT -> new KnightMoves().computeMovesKnight(myPosition, board, myColor);
            case PieceType.ROOK -> new RookMoves().computeMovesRook(myPosition, board, myColor);
            case PieceType.QUEEN -> new QueenMoves().computeMovesQueen(myPosition, board, myColor);
            case PieceType.PAWN -> new PawnMoves().computeMovesPawn(myPosition, board, myColor);
        };
    }
}