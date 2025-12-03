package websocket;

import com.google.gson.Gson;
import serverfacade.HTTPException;
import websocket.commands.ConnectCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import jakarta.websocket.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;

//need to extend Endpoint for websocket to work properly
public class WebSocketFacade extends Endpoint {

    Session session;
    ServerMessageObserver serverMessageObserver;

    public WebSocketFacade(int port, ServerMessageObserver serverMessageObserver) throws HTTPException {
        try {
            String url = String.format(Locale.getDefault(), "ws://localhost:%d/ws", port);
            URI socketURI = new URI(url);
            this.serverMessageObserver = serverMessageObserver;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    serverMessageObserver.notify(message);
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new HTTPException(ex.getMessage());
        }
    }

    //Endpoint requires this method, but you don't have to do anything
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    // Connect request made to server
    public void connect(String authToken, Integer gameID,
                        ConnectCommand.ParticipantType participantType, String gameName, String playerName,
                        String playerColor) throws HTTPException {
        try {
            var command = new ConnectCommand(UserGameCommand.CommandType.CONNECT,
                    authToken, gameID, participantType, gameName, playerName, playerColor);

            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new HTTPException(ex.getMessage());
        }
    }
}