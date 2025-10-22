package service;

import dataaccess.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.creategame.CreateGameRequest;
import service.exception.AlreadyTakenException;
import service.joingame.JoinGameRequest;
import service.register.RegisterRequest;
import service.register.RegisterResult;

public class GameServiceTest {
    UserDataAccess userDataAccess;
    AuthDataAccess authDataAccess;
    GameDataAccess gameDataAccess;
    UserService userService;
    GameService gameService;

    @BeforeEach
    public void setup() {
        userDataAccess = new MemoryUserDataAccess();
        authDataAccess = new MemoryAuthDataAccess();
        gameDataAccess = new MemoryGameDataAccess();
        gameService = new GameService(authDataAccess, gameDataAccess);
        userService = new UserService(userDataAccess, authDataAccess);
    }

    @Test
    public void createGameInvalid() {
        RegisterResult res = null;
        // Register a user
        try {
            res = userService.register(new RegisterRequest("test1", "test2", "test3@xyz.com"));
            Assertions.assertNotNull(res);
        } catch (AlreadyTakenException e) {
            Assertions.fail(e.getMessage());
        }

        // Try creating a game with a bad auth token (should fail)
        try {
            gameService.createGame(new CreateGameRequest(res.authToken() + "0", "test"));
            Assertions.fail();
        } catch (Exception _) {
        }
    }

    @Test
    public void createGameNormal() {
        RegisterResult res = null;
        // Register a user
        try {
            res = userService.register(new RegisterRequest("test1", "test2", "test3@xyz.com"));
            Assertions.assertNotNull(res);
        } catch (AlreadyTakenException e) {
            Assertions.fail(e.getMessage());
        }

        // Create a game with a good auth token
        try {
            gameService.createGame(new CreateGameRequest(res.authToken(), "test"));
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void joinGameInvalid() {
        RegisterResult res = null;
        // Register a user
        try {
            res = userService.register(new RegisterRequest("test1", "test2", "test3@xyz.com"));
            Assertions.assertNotNull(res);
        } catch (AlreadyTakenException e) {
            Assertions.fail(e.getMessage());
        }

        // Try joining a non-existent game (game ID 0)
        try {
            gameService.joinGame(new JoinGameRequest(res.authToken(), "WHITE", 0));
            Assertions.fail();
        } catch (Exception _) {
        }

        // Create a game
        try {
            gameService.createGame(new CreateGameRequest(res.authToken(), "test"));
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }

        // Try joining the game with a bad playerColor
        try {
            gameService.joinGame(new JoinGameRequest(res.authToken(), "BROWN", 0));
            Assertions.fail();
        } catch (Exception _) {
        }

        // Try joining the game with a bad auth token
        try {
            gameService.joinGame(new JoinGameRequest(res.authToken() + "0", "BLACK", 0));
            Assertions.fail();
        } catch (Exception _) {
        }

        // Join the game successfully
        try {
            gameService.joinGame(new JoinGameRequest(res.authToken(), "BLACK", 0));
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }

        // Try joining again as the black player
        try {
            gameService.joinGame(new JoinGameRequest(res.authToken(), "BLACK", 0));
            Assertions.fail();
        } catch (Exception _) {
        }
    }

    @Test
    public void joinGameNormal() {
        RegisterResult res = null;
        // Register a user
        try {
            res = userService.register(new RegisterRequest("test1", "test2", "test3@xyz.com"));
            Assertions.assertNotNull(res);
        } catch (AlreadyTakenException e) {
            Assertions.fail(e.getMessage());
        }

        // Create a game
        try {
            gameService.createGame(new CreateGameRequest(res.authToken(), "test"));
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }

        // Join the game successfully as the white player
        try {
            gameService.joinGame(new JoinGameRequest(res.authToken(), "WHITE", 0));
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }

        // Register a new user
        try {
            res = userService.register(new RegisterRequest("test4", "test5", "test6@xyz.com"));
            Assertions.assertNotNull(res);
        } catch (AlreadyTakenException e) {
            Assertions.fail(e.getMessage());
        }

        // Join the game successfully as the black player
        try {
            gameService.joinGame(new JoinGameRequest(res.authToken(), "BLACK", 0));
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }

    }
}
