package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.MemoryGameDataAccess;
import dataaccess.MemoryUserDataAccess;
import dataaccess.MemoryAuthDataAccess;
import service.exception.AlreadyTakenException;
import service.exception.BadRequestException;
import service.exception.MissingGameException;
import service.exception.UnauthorizedException;

import io.javalin.*;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;

import service.UserService;
import service.GameService;
import service.joingame.JoinGameRequest;
import service.login.LoginRequest;
import service.logout.LogoutRequest;
import service.register.RegisterRequest;
import service.creategame.CreateGameRequest;

// import java.util.logging.Level;
// import java.util.logging.Logger;

import java.util.Map;

public class Server {

    private final Javalin server;
    // private static final Logger logger = Logger.getLogger(Server.class.getName());

    private final UserService userService;
    private final GameService gameService;

    public Server() {
        server = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        server.post("user", this::register);
        server.post("session", this::login);
        server.delete("session", this::logout);
        server.post("game", this::createGame);
        server.put("game", this::joinGame);

        MemoryUserDataAccess dataAccess = new MemoryUserDataAccess();
        MemoryAuthDataAccess authAccess = new MemoryAuthDataAccess();
        MemoryGameDataAccess gameAccess = new MemoryGameDataAccess();

        userService = new UserService(dataAccess, authAccess);
        gameService = new GameService(authAccess, gameAccess);
    }

    private void register(@NotNull Context ctx) {

        var serializer = new Gson();
        String reqJson = ctx.body();
        var req = serializer.fromJson(reqJson, Map.class);

        // Get the serialized input from the JSON
        var username = req.get("username").toString();
        var password = req.get("password").toString();
        var email = req.get("email").toString();

        if (username == null || password == null || email == null) {
            returnError(ctx, "bad request", 400);
            return;
        }

        // create a new register request for the specified user
        var registerRequest = new RegisterRequest(username, password, email);
        var res = Map.of();

        // try to register the user and obtain an auth token for them
        try {
            var registerResult = userService.register(registerRequest);
            res = Map.of("username", registerResult.username(),
                    "authToken", registerResult.authToken());
            ctx.result(serializer.toJson(res));
        }
        // handle exception
        catch (AlreadyTakenException e) {
            returnError(ctx, e.getMessage(), 403);
        }
    }

    private void login(@NotNull Context ctx) {
        var serializer = new Gson();
        String reqJson = ctx.body();
        var req = serializer.fromJson(reqJson, Map.class);

        // Get the serialized input from the JSON
        var username = req.get("username").toString();
        var password = req.get("password").toString();

        if (username == null || password == null) {
            returnError(ctx, "bad request", 400);
            return;
        }

        // create a new login request for the specified user
        var loginRequest = new LoginRequest(username, password);
        var res = Map.of();

        // try to log in the user and obtain an auth token for them
        try {
            var loginResult = userService.login(loginRequest);
            res = Map.of("username", loginResult.username(),
                    "authToken", loginResult.authToken());
            ctx.result(serializer.toJson(res));
        }
        // handle exceptions
        catch (UnauthorizedException e) {
            returnError(ctx, e.getMessage(), 401);
        }
        catch (DataAccessException e) {
            returnError(ctx, e.getMessage(), 500);
        }
    }

    private void logout(@NotNull Context ctx) {
        // Grab the auth token and create a logout request
        String authToken = ctx.header("authorization");
        // logger.log(Level.SEVERE, String.format("My authToken: %s", authToken));

        if (authToken == null) {
            returnError(ctx, "bad request", 400);
            return;
        }

        var logoutRequest = new LogoutRequest(authToken);
        var res = Map.of();

        // try to log out the user
        try {
            userService.logout(logoutRequest);
            var serializer = new Gson();
            ctx.result(serializer.toJson(res));
        }
        // handle exceptions
        catch (UnauthorizedException e) {
            returnError(ctx, e.getMessage(), 401);
        }
        catch (DataAccessException e) {
            returnError(ctx, e.getMessage(), 500);
        }
    }

    private void createGame(@NotNull Context ctx) {
        var serializer = new Gson();
        String reqJson = ctx.body();
        var req = serializer.fromJson(reqJson, Map.class);

        // Create a createGame request
        String gameName = req.get("gameName").toString();
        String authToken = ctx.header("authorization");

        if (authToken == null || gameName == null) {
            returnError(ctx, "bad request", 400);
            return;
        }

        var createGameRequest = new CreateGameRequest(authToken, gameName);
        var res = Map.of();

        // try to create the game
        try {
            var createGameResult = gameService.createGame(createGameRequest);
            res = Map.of("gameID", createGameResult.gameID());
            ctx.result(serializer.toJson(res));
        }
        // handle exception
        catch (UnauthorizedException e) {
            returnError(ctx, e.getMessage(), 401);
        }
    }

    private void joinGame(@NotNull Context ctx) {
        var serializer = new Gson();
        String reqJson = ctx.body();
        var req = serializer.fromJson(reqJson, Map.class);

        // Create a joinGame request
        String playerColor = req.get("playerColor").toString();
        String gameIDStr = req.get("gameID").toString();
        String authToken = ctx.header("authorization");

        if (!(playerColor.equals("WHITE") || playerColor.equals("BLACK"))
                || gameIDStr == null || authToken == null) {
            returnError(ctx, "bad request", 400);
            return;
        }

        Integer gameID = Integer.parseInt(gameIDStr);

        var joinGameRequest = new JoinGameRequest(authToken, playerColor, gameID);
        var res = Map.of();

        // try to join the game
        try {
            gameService.joinGame(joinGameRequest);
            ctx.result(serializer.toJson(res));
        }
        // handle exceptions
        catch (UnauthorizedException e) {
            returnError(ctx, e.getMessage(), 401);
        }
        catch (MissingGameException | DataAccessException e) {
            returnError(ctx, e.getMessage(), 500);
        }
        catch (AlreadyTakenException e) {
            returnError(ctx, e.getMessage(), 403);
        }
        catch (BadRequestException e) {
            returnError(ctx, e.getMessage(), 400);
        }
    }

    private void returnError(@NotNull Context ctx, String message, Integer status) {
        var body = new Gson().toJson(Map.of("message", String.format("Error: %s", message)));
        ctx.status(status);
        ctx.json(body);
    }

    public int run(int desiredPort) {
        server.start(desiredPort);
        return server.port();
    }

    public void stop() {
        server.stop();
    }
}
