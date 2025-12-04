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
import service.DatabaseService;
import service.GameService;
import service.UserService;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

import websocket.messages.ServerMessage;

import java.io.IOException;

public record WebSocketHandler(UserService userService, GameService gameService, DatabaseService databaseService)
        implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private static final ConnectionManager connections = new ConnectionManager();

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
                //case MAKE_MOVE -> makeMove(ctx.session, message);
                case LEAVE -> leave(ctx.session, gameID, authData, gameData);
                //case RESIGN -> resign(gameID, authToken, ctx.session);
            }
        } catch (Exception ex) {
            try {
                ErrorMessage msg = new ErrorMessage(ServerMessage.ServerMessageType.ERROR,
                        String.format("Error: %s", ex.getMessage()));
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
        ChessGame.TeamColor playerColor;
        String playerColorString = null;

        // Player cases
        if (whiteUsername != null && whiteUsername.equals(playerName)) {
            playerColor = ChessGame.TeamColor.WHITE;
            playerColorString = "white";
        } else if (blackUsername != null && blackUsername.equals(playerName)) {
            playerColor = ChessGame.TeamColor.BLACK;
            playerColorString = "black";
        }
        // Observer case
        else {
            playerColor = ChessGame.TeamColor.WHITE;
        }

        // Send a LOAD_GAME message back to the client
        ChessGame game = gameData.game();
        LoadGameMessage message = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, game, playerColor);
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
}