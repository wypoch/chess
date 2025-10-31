package service;

import dataaccess.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.creategame.CreateGameRequest;
import service.exception.AlreadyTakenException;
import service.listgames.ListGamesRequest;
import service.listgames.ListGamesResult;
import service.register.RegisterRequest;
import service.register.RegisterResult;

public class DatabaseServiceTest {
    UserDataAccess userDataAccess;
    AuthDataAccess authDataAccess;
    GameDataAccess gameDataAccess;
    UserService userService;
    GameService gameService;
    DatabaseService databaseService;

    @BeforeEach
    public void setup() {
        userDataAccess = new MemoryUserDataAccess();
        authDataAccess = new MemoryAuthDataAccess();
        gameDataAccess = new MemoryGameDataAccess();
        gameService = new GameService(authDataAccess, gameDataAccess);
        userService = new UserService(userDataAccess, authDataAccess);
        databaseService = new DatabaseService(userDataAccess, authDataAccess, gameDataAccess);
    }

    @Test
    public void clearAll() {
        RegisterResult res = null;
        // Register a user
        try {
            res = userService.register(new RegisterRequest("test1", "test2", "test3@xyz.com"));
            Assertions.assertNotNull(res);
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }

        // Create a game
        try {
            gameService.createGame(new CreateGameRequest(res.authToken(), "test"));
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }

        // Clear the database
        try {
            databaseService.clear();
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }

        // Make sure we can register the same user again
        try {
            res = userService.register(new RegisterRequest("test1", "test2", "test3@xyz.com"));
            Assertions.assertNotNull(res);
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }

        ListGamesResult res2 = null;
        // List the current games
        try {
            res2 = gameService.listGames(new ListGamesRequest(res.authToken()));
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }

        // Ensure that the list contains no games
        Assertions.assertEquals(0, res2.gameDataList().size());
    }
}
