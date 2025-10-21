package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import io.javalin.*;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;
import service.UserService;
import service.register.RegisterRequest;

import java.util.Map;

public class Server {

    private final Javalin server;

    public Server() {
        server = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.\
        server.post("user", this::register);
        server.delete("db", ctx -> ctx.result("delete"));

    }

    private void register(@NotNull Context ctx) throws DataAccessException {
        var serializer = new Gson();
        String reqJson = ctx.body();
        var req = serializer.fromJson(reqJson, Map.class);

        // Get the serialized input from the JSON
        var username = req.get("username").toString();
        var password = req.get("password").toString();
        var email = req.get("email").toString();

        // create a new register request for the specified user
        var dataAccess = new MemoryDataAccess();
        var userService = new UserService(dataAccess);
        var registerRequest = new RegisterRequest(username, password, email);
        var res = Map.of("", "");

        // try to register the user and obtain an auth token for them
        try {
            var registerResult = userService.register(registerRequest);
            res = Map.of("username", registerResult.username(),
                    "authToken", registerResult.authToken());

        } catch (DataAccessException ex) {
            res = Map.of("message", ex.toString());
        }

        ctx.result(serializer.toJson(res));
    }

    public int run(int desiredPort) {
        server.start(desiredPort);
        return server.port();
    }

    public void stop() {
        server.stop();
    }
}
