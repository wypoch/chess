package websocket.messages;

public class NotificationMessage extends ServerMessage {

    private final NotificationType notificationType;
    private final String message;

    public NotificationMessage(ServerMessageType type, NotificationType notificationType, String message) {
        super(type);
        this.notificationType = notificationType;
        this.message = message;
    }

    public enum NotificationType {
        NEW_PLAYER,
        NEW_OBSERVER,
        NEW_MOVE,
        NEW_QUIT,
        NEW_RESIGN,
        NEW_CHECK,
        NEW_CHECKMATE
    }

    public String getMessage() {
        return message;
    }
}
