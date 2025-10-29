package server;

import com.google.gson.Gson;
import dataaccess.*;
import model.GameData;

import service.exception.AlreadyTakenException;
import service.exception.BadRequestException;
import service.exception.MissingGameException;
import service.exception.UnauthorizedException;

import io.javalin.*;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;

import service.UserService;
import service.GameService;
import service.DatabaseService;

import service.joingame.JoinGameRequest;
import service.listgames.ListGamesRequest;
import service.login.LoginRequest;
import service.logout.LogoutRequest;
import service.register.RegisterRequest;
import service.creategame.CreateGameRequest;

// import java.util.logging.Level;
// import java.util.logging.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Server {

    private final Javalin server;
    // private static final Logger logger = Logger.getLogger(Server.class.getName());

    private final UserService userService;
    private final GameService gameService;
    private final DatabaseService databaseService;

    public Server() {
        server = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        server.post("user", this::register);
        server.post("session", this::login);
        server.delete("session", this::logout);
        server.post("game", this::createGame);
        server.put("game", this::joinGame);
        server.get("game", this::listGames);
        server.delete("db", this::clear);

        // Create the SQL database, if it does not already exist
        try {
            DatabaseManager.createDatabase();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

        // Set up each SQL DataAccess layer
        SQLUserDataAccess dataAccess;
        SQLAuthDataAccess authAccess;
        SQLGameDataAccess gameAccess;
        try {
            dataAccess = new SQLUserDataAccess();
            authAccess = new SQLAuthDataAccess();
            gameAccess = new SQLGameDataAccess();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

        // Set up each of the services
        userService = new UserService(dataAccess, authAccess);
        gameService = new GameService(authAccess, gameAccess);
        databaseService = new DatabaseService(dataAccess, authAccess, gameAccess);
    }

    private void register(@NotNull Context ctx) {

        var serializer = new Gson();
        String reqJson = ctx.body();
        var req = serializer.fromJson(reqJson, Map.class);

        // Make sure the JSON body is valid
        var usernameReq = req.get("username");
        var passwordReq = req.get("password");
        var emailReq = req.get("email");

        if (usernameReq == null || passwordReq == null || emailReq == null) {
            returnError(ctx, "bad request", 400);
            return;
        }

        // Get the serialized input from the JSON
        var username = usernameReq.toString();
        var password = passwordReq.toString();
        var email = emailReq.toString();

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

        // Make sure the JSON body is valid
        var usernameReq = req.get("username");
        var passwordReq = req.get("password");

        if (usernameReq == null || passwordReq == null) {
            returnError(ctx, "bad request", 400);
            return;
        }

        // Get the serialized input from the JSON
        var username = usernameReq.toString();
        var password = passwordReq.toString();

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

        // Validate the request
        String authToken = ctx.header("authorization");
        var gameNameReq = req.get("gameName");

        if (gameNameReq == null) {
            returnError(ctx, "bad request", 400);
            return;
        }

        // Create a createGame request
        String gameName = gameNameReq.toString();
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

        // Validate the request
        var playerColorReq = req.get("playerColor");
        var gameIDReq = req.get("gameID");
        String authToken = ctx.header("authorization");

        if (playerColorReq == null || gameIDReq == null) {
            returnError(ctx, "bad request", 400);
            return;
        }

        // Ensure the player color is in the expected format
        String playerColor = playerColorReq.toString();
        if (!(playerColor.equals("WHITE") || playerColor.equals("BLACK"))) {
            returnError(ctx, "bad request", 400);
            return;
        }

        // Convert the gameID to an Integer
        Integer gameID = ((Double) gameIDReq).intValue();

        // Create a joinGame request
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

    private void listGames(@NotNull Context ctx) {
        // Grab the auth token and create a list games request
        String authToken = ctx.header("authorization");

        var listGamesRequest = new ListGamesRequest(authToken);

        // try to list the games
        try {
            var listGamesResult = gameService.listGames(listGamesRequest);
            List<GameData> gamesList = new ArrayList<>(listGamesResult.gameDataList());
            var serializer = new Gson();
            var res = Map.of("games", gamesList);
            ctx.result(serializer.toJson(res));
        }
        // handle exception
        catch (UnauthorizedException e) {
            returnError(ctx, e.getMessage(), 401);
        }
    }

    private void clear(@NotNull Context ctx) {
        // clear each database
        databaseService.clear();
        var serializer = new Gson();
        var res = Map.of();
        ctx.result(serializer.toJson(res));
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
