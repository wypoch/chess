package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.NotificationMessage;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    // Data structure maps Sessions to their appropriate gameIDs
    public final ConcurrentHashMap<Session, Integer> connections = new ConcurrentHashMap<>();

    public void add(Session session, Integer gameID) {
        connections.put(session, gameID);
    }

    public void remove(Session session) {
        connections.remove(session);
    }

    public void broadcast(Session excludeSession, Integer gameID, NotificationMessage notification) throws IOException {
        String msg = new Gson().toJson(notification);
        for (Session c : connections.keySet()) {
            // Only broadcast to sessions connected to our same gameID
            Integer currGameID = connections.get(c);
            if (!currGameID.equals(gameID)) {
                continue;
            }
            // Broadcast to all other clients
            if (c.isOpen()) {
                if (!c.equals(excludeSession)) {
                    c.getRemote().sendString(msg);
                }
            }
        }
    }
}