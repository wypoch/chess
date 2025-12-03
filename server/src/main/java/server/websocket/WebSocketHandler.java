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
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(WsMessageContext ctx) {

        int gameID = -1;
        String authToken = null;
        Session session = ctx.session;
        String message = ctx.message();

        try {
            UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
            gameID = command.getGameID();
            authToken = command.getAuthToken();

            AuthData authData = userService.getAuth(authToken);
            GameData gameData = gameService.getGame(gameID);

            switch (command.getCommandType()) {
                case CONNECT -> connectPlayer(session, authData, gameData);
                //case OBSERVE -> connectObserver(session, authData, gameData);
                //case MAKE_MOVE -> makeMove(ctx.session, message);
                //case LEAVE -> leave(ctx.session, message);
                //case RESIGN -> resign(gameID, authToken, ctx.session);
            }
        } catch (Exception ex) {
            try {
                ErrorMessage msg = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, String.format("Error: %s", ex.getMessage()));
                session.getRemote().sendString(new Gson().toJson(msg));
            } catch (IOException ex2) {
                throw new RuntimeException("Communication failure");
            }
        }
    }

    @Override
    public void handleClose(@NotNull WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

    // Connection request received from server
    private void connectPlayer(Session session, AuthData authData, GameData gameData) throws IOException {

        connections.add(session);
        NotificationMessage notification;

        // Gather information for the notification from the database
        String gameName = gameData.gameName();
        String whiteUsername = gameData.whiteUsername();
        String blackUsername = gameData.blackUsername();
        String playerName = authData.username();
        ChessGame.TeamColor playerColor;
        String playerColorString;

        if (whiteUsername != null && whiteUsername.equals(playerName)) {
            playerColor = ChessGame.TeamColor.WHITE;
            playerColorString = "white";
        }
        else if (blackUsername != null && blackUsername.equals(playerName)) {
            playerColor = ChessGame.TeamColor.BLACK;
            playerColorString = "black";
        } else {
            throw new RuntimeException("Player not found in game");
        }

        // Send a LOAD_GAME message back to the client
        ChessGame game = gameData.game();
        LoadGameMessage message = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, game, playerColor);
        session.getRemote().sendString(new Gson().toJson(message));

        // Broadcast the appropriate notification
        String msg = String.format("New player %s joined game %s as color %s", playerName, gameName, playerColorString);
        notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, msg);

        connections.broadcast(session, notification);
    }

//    private void leave(Session session, String jsonInput) throws IOException {
//        connections.add(session);
//        NotificationMessage notification;
//        LeaveCommand command = new Gson().fromJson(jsonInput, LeaveCommand.class);
//
//        connections.broadcast(session, notification);
//        connections.remove(session);
//    }
}