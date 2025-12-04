package ui;

import chess.ChessBoard;
import chess.ChessGame.TeamColor;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.Collection;
import java.util.HashSet;

import static ui.EscapeSequences.*;

public class ChessBoardViewer {

    public static void showBoard(ChessBoard chessBoard, TeamColor playerColor) {
        System.out.println("Game board:");
        int i;
        int j;

        if (playerColor == TeamColor.WHITE) {
            System.out.println(SET_BG_COLOR_LIGHT_GREY + "    a  b  c  d  e  f  g  h    " + RESET_BG_COLOR);
            for (i = 8; i >= 1; i--) {
                System.out.print(SET_BG_COLOR_LIGHT_GREY + " " + i + " " + RESET_BG_COLOR);
                for (j = 1; j <= 8; j++) {
                    printPiece(chessBoard, i, j);
                }
                System.out.print(SET_BG_COLOR_LIGHT_GREY + " " + i + " " + RESET_BG_COLOR);
                System.out.println();
            }
            System.out.println(SET_BG_COLOR_LIGHT_GREY + "    a  b  c  d  e  f  g  h    " + RESET_BG_COLOR);
        }

        else {
            System.out.println(SET_BG_COLOR_LIGHT_GREY + "    h  g  f  e  d  c  b  a    " + RESET_BG_COLOR);
            for (i = 1; i <= 8; i++) {
                System.out.print(SET_BG_COLOR_LIGHT_GREY + " " + i + " " + RESET_BG_COLOR);
                for (j = 8; j >= 1; j--) {
                    printPiece(chessBoard, i, j);
                }
                System.out.print(SET_BG_COLOR_LIGHT_GREY + " " + i + " " + RESET_BG_COLOR);
                System.out.println();
            }
            System.out.println(SET_BG_COLOR_LIGHT_GREY + "    h  g  f  e  d  c  b  a    " + RESET_BG_COLOR);
        }
    }

    public static void showBoardWithMoves(ChessBoard chessBoard, TeamColor playerColor, Collection<ChessMove> possMoves) {
        System.out.println("Game board:");
        int i;
        int j;

        HashSet<ChessPosition> highlightPositions = new HashSet<>();
        for (var move : possMoves) {
            highlightPositions.add(move.getEndPosition());
        }

        if (playerColor == TeamColor.WHITE) {
            System.out.println(SET_BG_COLOR_LIGHT_GREY + "    a  b  c  d  e  f  g  h    " + RESET_BG_COLOR);
            for (i = 8; i >= 1; i--) {
                System.out.print(SET_BG_COLOR_LIGHT_GREY + " " + i + " " + RESET_BG_COLOR);
                for (j = 1; j <= 8; j++) {
                    ChessPosition pos = new ChessPosition(i, j);
                    if (highlightPositions.contains(pos)) {
                        printPieceHighlighted(chessBoard, i, j);
                    } else {
                        printPiece(chessBoard, i, j);
                    }
                }
                System.out.print(SET_BG_COLOR_LIGHT_GREY + " " + i + " " + RESET_BG_COLOR);
                System.out.println();
            }
            System.out.println(SET_BG_COLOR_LIGHT_GREY + "    a  b  c  d  e  f  g  h    " + RESET_BG_COLOR);
        }

        else {
            System.out.println(SET_BG_COLOR_LIGHT_GREY + "    h  g  f  e  d  c  b  a    " + RESET_BG_COLOR);
            for (i = 1; i <= 8; i++) {
                System.out.print(SET_BG_COLOR_LIGHT_GREY + " " + i + " " + RESET_BG_COLOR);
                for (j = 8; j >= 1; j--) {
                    ChessPosition pos = new ChessPosition(i, j);
                    if (highlightPositions.contains(pos)) {
                        printPieceHighlighted(chessBoard, i, j);
                    } else {
                        printPiece(chessBoard, i, j);
                    }
                }
                System.out.print(SET_BG_COLOR_LIGHT_GREY + " " + i + " " + RESET_BG_COLOR);
                System.out.println();
            }
            System.out.println(SET_BG_COLOR_LIGHT_GREY + "    h  g  f  e  d  c  b  a    " + RESET_BG_COLOR);
        }
    }

    public static void printPieceHighlighted(ChessBoard chessBoard, int i, int j) {
        if ((i + j) % 2 == 0) {
            System.out.print(SET_BG_COLOR_DARK_GREEN);
        } else {
            System.out.print(SET_BG_COLOR_GREEN);
        }
        ChessPiece chessPiece = chessBoard.getPiece(new ChessPosition(i, j));
        System.out.print(pieceToUnicode(chessPiece));
        System.out.print(RESET_BG_COLOR);
    }

    public static void printPiece(ChessBoard chessBoard, int i, int j) {
        if ((i + j) % 2 == 0) {
            System.out.print(SET_BG_COLOR_BLACK);
        } else {
            System.out.print(SET_BG_COLOR_WHITE);
        }
        ChessPiece chessPiece = chessBoard.getPiece(new ChessPosition(i, j));
        System.out.print(pieceToUnicode(chessPiece));
        System.out.print(RESET_BG_COLOR);
    }

    public static String pieceToUnicode(ChessPiece chessPiece) {
        // no piece present
        if (chessPiece == null) {
            return "   ";
        }
        // piece is white
        else if (chessPiece.getTeamColor() == TeamColor.WHITE) {
            return switch (chessPiece.getPieceType()) {
                case ChessPiece.PieceType.KING -> SET_TEXT_COLOR_RED + " K " + RESET_TEXT_COLOR;
                case ChessPiece.PieceType.QUEEN -> SET_TEXT_COLOR_RED + " Q " + RESET_TEXT_COLOR;
                case ChessPiece.PieceType.BISHOP -> SET_TEXT_COLOR_RED + " B " + RESET_TEXT_COLOR;
                case ChessPiece.PieceType.KNIGHT -> SET_TEXT_COLOR_RED + " N " + RESET_TEXT_COLOR;
                case ChessPiece.PieceType.ROOK -> SET_TEXT_COLOR_RED + " R " + RESET_TEXT_COLOR;
                case ChessPiece.PieceType.PAWN -> SET_TEXT_COLOR_RED + " P " + RESET_TEXT_COLOR;
            };
        }
        // piece is black
        else {
            return switch (chessPiece.getPieceType()) {
                case ChessPiece.PieceType.KING -> SET_TEXT_COLOR_BLUE + " K " + RESET_TEXT_COLOR;
                case ChessPiece.PieceType.QUEEN -> SET_TEXT_COLOR_BLUE + " Q " + RESET_TEXT_COLOR;
                case ChessPiece.PieceType.BISHOP -> SET_TEXT_COLOR_BLUE + " B " + RESET_TEXT_COLOR;
                case ChessPiece.PieceType.KNIGHT -> SET_TEXT_COLOR_BLUE + " N " + RESET_TEXT_COLOR;
                case ChessPiece.PieceType.ROOK -> SET_TEXT_COLOR_BLUE + " R " + RESET_TEXT_COLOR;
                case ChessPiece.PieceType.PAWN -> SET_TEXT_COLOR_BLUE + " P " + RESET_TEXT_COLOR;
            };
        }
    }
}
