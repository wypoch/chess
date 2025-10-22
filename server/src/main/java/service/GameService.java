package service;

import dataaccess.AuthDataAccess;
import dataaccess.GameDataAccess;
import service.creategame.CreateGameRequest;
import service.creategame.CreateGameResult;
import service.exception.UnauthorizedException;

public record GameService(AuthDataAccess authDataAccess, GameDataAccess gameDataAccess) {

    public CreateGameResult createGame(CreateGameRequest createGameRequest) throws UnauthorizedException {
        return new CreateGameResult("");
    }
}
