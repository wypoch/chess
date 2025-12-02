package websocket.commands;

public class ConnectCommand extends UserGameCommand {

    private final ParticipantType participantType;
    private final String gameName;
    private final String playerName;
    private final String playerColor;

    public ConnectCommand(CommandType commandType, String authToken, Integer gameID,
                          ParticipantType participantType, String gameName, String playerName,
                          String playerColor) {

        super(commandType, authToken, gameID);
        this.participantType = participantType;
        this.gameName = gameName;
        this.playerName = playerName;
        this.playerColor = playerColor;
    }

    public enum ParticipantType {
        PLAYER,
        OBSERVER
    }

    public String getGameName() {
        return gameName;
    }

    public ParticipantType getParticipantType() {
        return participantType;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getPlayerColor() {
        return playerColor;
    }

}
