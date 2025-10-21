package server;

import com.google.gson.Gson;
import dataaccess.AlreadyTakenException;
import dataaccess.MemoryDataAccess;
import io.javalin.*;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;
import service.UserService;
import service.register.RegisterRequest;

// import java.util.logging.Level;
// import java.util.logging.Logger;

import java.util.Map;

public class Server {

    private final Javalin server;
    private static final Logger logger = Logger.getLogger(Server.class.getName());

    private final MemoryDataAccess dataAccess;
    private final UserService userService;

    public Server() {
        server = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.\
        server.post("user", this::register);
        server.delete("db", ctx -> ctx.result("delete"));

        dataAccess = new MemoryDataAccess();
        userService = new UserService(dataAccess);
    }

    private void register(@NotNull Context ctx) throws AlreadyTakenException {

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
            // logger.log(Level.SEVERE, "In exception");
            var body = new Gson().toJson(Map.of("message", String.format("Error: %s", e.getMessage())));
            ctx.status(403);
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
