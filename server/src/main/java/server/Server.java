package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.MemoryUserDataAccess;
import dataaccess.MemoryAuthDataAccess;
import service.exception.AlreadyTakenException;
import service.exception.UnauthorizedException;
import io.javalin.*;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;
import service.UserService;
import service.login.LoginRequest;
import service.logout.LogoutRequest;
import service.register.RegisterRequest;

// import java.util.logging.Level;
// import java.util.logging.Logger;

import java.util.Map;

public class Server {

    private final Javalin server;
//     private static final Logger logger = Logger.getLogger(Server.class.getName());

    private final UserService userService;

    public Server() {
        server = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.\
        server.post("user", this::register);
        server.post("session", this::login);
        server.delete("session", this::logout);

        MemoryUserDataAccess dataAccess = new MemoryUserDataAccess();
        MemoryAuthDataAccess authAccess = new MemoryAuthDataAccess();
        userService = new UserService(dataAccess, authAccess);
    }

    private void register(@NotNull Context ctx) {

        var serializer = new Gson();
        String reqJson = ctx.body();
        var req = serializer.fromJson(reqJson, Map.class);

        // Get the serialized input from the JSON
        var username = req.get("username").toString();
        var password = req.get("password").toString();
        var email = req.get("email").toString();

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
            var body = new Gson().toJson(Map.of("message", String.format("Error: %s", e.getMessage())));
            ctx.status(403);
            ctx.json(body);
        }
    }

    private void login(@NotNull Context ctx) {
        var serializer = new Gson();
        String reqJson = ctx.body();
        var req = serializer.fromJson(reqJson, Map.class);

        // Get the serialized input from the JSON
        var username = req.get("username").toString();
        var password = req.get("password").toString();

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
            var body = new Gson().toJson(Map.of("message", String.format("Error: %s", e.getMessage())));
            ctx.status(401);
            ctx.json(body);
        }
        catch (DataAccessException e) {
            var body = new Gson().toJson(Map.of("message", String.format("Error: %s", e.getMessage())));
            ctx.status(500);
            ctx.json(body);
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
            res = Map.of();
            var serializer = new Gson();
            ctx.result(serializer.toJson(res));
        }
        // handle exceptions
        catch (UnauthorizedException e) {
            var body = new Gson().toJson(Map.of("message", String.format("Error: %s", e.getMessage())));
            ctx.status(401);
            ctx.json(body);
        }
        catch (DataAccessException e) {
            var body = new Gson().toJson(Map.of("message", String.format("Error: %s", e.getMessage())));
            ctx.status(500);
            ctx.json(body);
        }
    }

    public int run(int desiredPort) {
        server.start(desiredPort);
        return server.port();
    }

    public void stop() {
        server.stop();
    }
}
