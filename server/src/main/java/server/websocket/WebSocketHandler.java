package server.websocket;

import com.google.gson.Gson;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import org.eclipse.jetty.websocket.api.Session;

import org.jetbrains.annotations.NotNull;
import websocket.commands.ConnectCommand;
import websocket.commands.LeaveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.NotificationMessage;

import chess.ChessGame.TeamColor;
import websocket.messages.ServerMessage;

import java.io.IOException;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections = new ConnectionManager();

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

            switch (command.getCommandType()) {
                case CONNECT -> connect(session, message);
                //case MAKE_MOVE -> makeMove(ctx.session, message);
                //case LEAVE -> leave(ctx.session, message);
                //case RESIGN -> resign(gameID, authToken, ctx.session);
            }
        } catch (IOException ex) {
            throw new RuntimeException("IOException occurred");
        }
    }

    @Override
    public void handleClose(@NotNull WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

    private void connect(Session session, String jsonInput) throws IOException {

        connections.add(session);
        NotificationMessage notification;
        ConnectCommand command = new Gson().fromJson(jsonInput, ConnectCommand.class);

        // Broadcast the appropriate notification
        var participantType = command.getParticipantType();
        var playerColor = command.getPlayerColor();
        var gameName = command.getGameName();
        var playerName = command.getPlayerName();

        if (participantType == ConnectCommand.ParticipantType.PLAYER) {

            var message = String.format("New player %s joined game %s as color %s", playerName, gameName, playerColor);
            notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                    NotificationMessage.NotificationType.NEW_PLAYER,
                    message);
        } else {
            var message = String.format("New observer %s viewing game %s", playerName, gameName);
            notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                    NotificationMessage.NotificationType.NEW_OBSERVER,
                    message);
        }

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