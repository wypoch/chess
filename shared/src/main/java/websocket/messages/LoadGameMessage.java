package websocket.messages;

import chess.ChessGame;

public class LoadGameMessage extends ServerMessage {

    ChessGame game;
    ChessGame.TeamColor color;

    public LoadGameMessage(ServerMessageType type, ChessGame game, ChessGame.TeamColor color) {
        super(type);
        this.game = game;
        this.color = color;
    }

    public ChessGame getGame() {
        return game;
    }

    public ChessGame.TeamColor getColor() {
        return color;
    }
}
