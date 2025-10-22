package service.joingame;

public record JoinGameRequest(String authToken, String playerColor, Integer gameID) {
}
