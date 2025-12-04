package server.websocket;

import chess.ChessGame;
import com.google.gson.Gson;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;

import org.jetbrains.annotations.NotNull;
import service.GameService;
import service.UserService;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final UserService userService;
    private final GameService gameService;

    private final ConnectionManager connections = new ConnectionManager();
    public final ConcurrentHashMap<Integer, Integer> completeGames = new ConcurrentHashMap<>();

    public WebSocketHandler(UserService userService, GameService gameService) {
        this.userService = userService;
        this.gameService = gameService;
    }

    @Override
    public void handleConnect(WsConnectContext ctx) {
        //System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(@NotNull WsMessageContext ctx) {

        try {
            UserGameCommand command = new Gson().fromJson(ctx.message(), UserGameCommand.class);
            Integer gameID = command.getGameID();
            String authToken = command.getAuthToken();

            AuthData authData = userService.getAuth(authToken);
            GameData gameData = gameService.getGame(gameID);

            switch (command.getCommandType()) {
                case CONNECT -> connect(ctx.session, gameID, authData, gameData);
                case MAKE_MOVE -> makeMove(ctx.session, ctx.message(), gameID, authData, gameData);
                case LEAVE -> leave(ctx.session, gameID, authData, gameData);
                case RESIGN -> resign(ctx.session, gameID, authData, gameData);
            }
        } catch (Exception ex) {
            try {
                ErrorMessage msg = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, ex.getMessage());
                ctx.session.getRemote().sendString(new Gson().toJson(msg));
            } catch (IOException ex2) {
                throw new RuntimeException("Communication failure");
            }
        }
    }

    @Override
    public void handleClose(@NotNull WsCloseContext ctx) {
        //System.out.println("Websocket closed");
    }

    // Connection request received from server
    private void connect(Session session, Integer gameID, AuthData authData, GameData gameData) throws IOException {

        connections.add(session, gameID);

        // Gather information for the notification from the database
        String gameName = gameData.gameName();
        String whiteUsername = gameData.whiteUsername();
        String blackUsername = gameData.blackUsername();
        String playerName = authData.username();

        String playerColorString = null;

        // Determine player color for notification
        if (whiteUsername != null && whiteUsername.equals(playerName)) {
            playerColorString = "white";
        } else if (blackUsername != null && blackUsername.equals(playerName)) {
            playerColorString = "black";
        }

        // Send a LOAD_GAME message back to the client
        ChessGame game = gameData.game();
        LoadGameMessage message = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, game);
        session.getRemote().sendString(new Gson().toJson(message));

        // Broadcast the appropriate notification
        String msg;
        if (playerColorString != null) {
            msg = String.format("User %s joined game %s as color %s", playerName, gameName, playerColorString);
        } else {
            msg = String.format("User %s joined game %s as observer", playerName, gameName);
        }

        NotificationMessage notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, msg);
        connections.broadcast(session, gameID, notification);
    }

    private void leave(Session session, Integer gameID, AuthData authData, GameData gameData) throws IOException {

        String playerName = authData.username();
        String gameName = gameData.gameName();

        String whiteUsername = gameData.whiteUsername();
        String blackUsername = gameData.blackUsername();

        // Player cases
        if (whiteUsername != null && whiteUsername.equals(playerName)) {
            // Remove the white player from the game
            try {
                gameService.removeUserFromGame(gameID, ChessGame.TeamColor.WHITE);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }

        } else if (blackUsername != null && blackUsername.equals(playerName)) {
            // Remove the black player from the game
            try {
                gameService.removeUserFromGame(gameID, ChessGame.TeamColor.BLACK);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        }

        // Send a notification that the user left
        String msg = String.format("User %s left game %s", playerName, gameName);

        NotificationMessage notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, msg);

        connections.broadcast(session, gameID, notification);
        connections.remove(session);
    }

    private void resign(Session session, Integer gameID, AuthData authData, GameData gameData) throws Exception {

        // Cannot resign if the game has ended
        if (completeGames.contains(gameID)) {
            throw new Exception("cannot resign; game is over");
        }

        // See if the user is a player or an observer
        String playerName = authData.username();
        String gameName = gameData.gameName();
        String whiteUsername = gameData.whiteUsername();
        String blackUsername = gameData.blackUsername();

        // Player cases
        if (whiteUsername != null && whiteUsername.equals(playerName)) {
            // Mark the game as complete
            completeGames.put(gameID, gameID);
        } else if (blackUsername != null && blackUsername.equals(playerName)) {
            // Mark the game as complete
            completeGames.put(gameID, gameID);
        }
        // Observer case
        else {
            throw new Exception("cannot resign as observer");
        }

        String msg = String.format("User %s resigned from game %s", playerName, gameName);
        NotificationMessage notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, msg);

        // Send the notification to ALL clients
        connections.broadcast(session, gameID, notification);
        session.getRemote().sendString(new Gson().toJson(notification));
    }

    private void makeMove(Session session, String jsonInput, Integer gameID, AuthData authData, GameData gameData) throws Exception {

        MakeMoveCommand command = new Gson().fromJson(jsonInput, MakeMoveCommand.class);
        var move = command.getMove();

        // Cannot make a move if the game has ended
        if (completeGames.contains(gameID)) {
            throw new Exception("cannot make a move; game is over");
        }

        // See if the user is a player or an observer
        String playerName = authData.username();
        String gameName = gameData.gameName();
        String whiteUsername = gameData.whiteUsername();
        String blackUsername = gameData.blackUsername();

        // Player cases
        ChessGame game = gameData.game();
        if (whiteUsername != null && whiteUsername.equals(playerName)) {
            // White cannot make move on black's turn
            if (game.getTeamTurn() != ChessGame.TeamColor.WHITE) {
                throw new Exception("cannot make move as opponent");
            }
            // Update the game in the database
            gameService.makeMove(gameID, move);

        } else if (blackUsername != null && blackUsername.equals(playerName)) {
            // Black cannot make move on white's turn
            if (game.getTeamTurn() != ChessGame.TeamColor.BLACK) {
                throw new Exception("cannot make move as opponent");
            }
            // Update the game in the database
            gameService.makeMove(gameID, move);
        }
        // Observer case
        else {
            throw new Exception("cannot make move as observer");
        }

        // Send a LOAD_GAME message back to ALL clients
        LoadGameMessage message = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, game);
        connections.broadcast(session, gameID, message);
        session.getRemote().sendString(new Gson().toJson(message));

        // Broadcast notification to all other clients
        var startPos = move.getStartPosition();
        var endPos = move.getEndPosition();

        String start = "" + (char)(startPos.getColumn() + 96) + (char)(startPos.getRow() + 48);
        String end = "" + (char)(endPos.getColumn() + 96) + (char)(endPos.getRow() + 48);

        String msg = String.format("User %s made move from %s to %s in game %s", playerName, start, end, gameName);
        NotificationMessage notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, msg);

        connections.broadcast(session, gameID, notification);

        // TODO: handle checks, checkmates, stalemates
    }
}