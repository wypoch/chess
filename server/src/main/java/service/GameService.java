package service;

import dataaccess.AuthDataAccess;
import dataaccess.GameDataAccess;
import model.GameData;

import service.creategame.CreateGameRequest;
import service.creategame.CreateGameResult;
import service.joingame.JoinGameRequest;
import service.exception.UnauthorizedException;

public class GameService {

    private Integer currGameID;
    private final AuthDataAccess authDataAccess;
    private final GameDataAccess gameDataAccess;

    public GameService(AuthDataAccess authDataAccess, GameDataAccess gameDataAccess) {
        currGameID = 0;
        this.authDataAccess = authDataAccess;
        this.gameDataAccess = gameDataAccess;
    }

    public CreateGameResult createGame(CreateGameRequest createGameRequest) throws UnauthorizedException {
        String authToken = createGameRequest.authToken();

        // try to find the authData associated with the authToken
        var responseData = authDataAccess.getAuth(authToken);
        if (responseData == null) {
            throw new UnauthorizedException("unauthorized");
        } else {
            // generate unique game ID
            Integer gameID = currGameID;
            currGameID += 1;

            // add the game to the database
            String gameName = createGameRequest.gameName();
            var gameData = new GameData(gameID, null, null, gameName, null);
            gameDataAccess.createGame(gameData);

            return new CreateGameResult(gameID);
        }
    }

    public void joinGame(JoinGameRequest joinGameRequest) throws UnauthorizedException {
        return;
    }
}
