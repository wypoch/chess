package serverfacade;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import model.AuthData;
import model.GameData;

import java.lang.reflect.Type;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.*;

import static serverfacade.HTTPHelper.*;

public class ServerFacade {

    private final int port;
    private static final HttpClient httpClient = HttpClient.newHttpClient();

    public ServerFacade(int port) {
        this.port = port;
    }

    public AuthData register(String username, String password, String email) throws HTTPException {

        var body = Map.of("username", username,
                "password", password,
                "email", email);

        var jsonBody = new Gson().toJson(body);
        HttpResponse<String> response;

        try {
            response = post(httpClient, port, "/user", jsonBody, null);
        } catch (Exception e) {
            throw new HTTPException("user registration failed due to server error");
        }

        var responseMap = new Gson().fromJson(response.body(), Map.class);
        var statusCode = response.statusCode();

        if (statusCode == 200) {
            String authToken = (String)responseMap.get("authToken");
            return new AuthData(authToken, username);

        } else {
            var errorMsg = responseMap.get("message");
            throw new HTTPException((String)errorMsg);
        }
    }

    public AuthData login(String username, String password) throws HTTPException {

        var body = Map.of("username", username,
                "password", password);

        var jsonBody = new Gson().toJson(body);
        HttpResponse<String> response;

        try {
            response = post(httpClient, port, "/session", jsonBody, null);
        } catch (Exception e) {
            throw new HTTPException("user registration failed due to server error");
        }

        var responseMap = new Gson().fromJson(response.body(), Map.class);
        var statusCode = response.statusCode();

        if (statusCode == 200) {
            String authToken = (String)responseMap.get("authToken");
            return new AuthData(authToken, username);

        } else {
            var errorMsg = responseMap.get("message");
            throw new HTTPException((String)errorMsg);
        }
    }

    public void logout(String authToken) throws HTTPException {

        HttpResponse<String> response;

        try {
            response = delete(httpClient, port, "/session", authToken);
        } catch (Exception e) {
            throw new HTTPException("user logout failed due to server error");
        }

        var responseMap = new Gson().fromJson(response.body(), Map.class);
        var statusCode = response.statusCode();

        if (statusCode != 200) {
            var errorMsg = responseMap.get("message");
            throw new HTTPException((String)errorMsg);
        }
    }

    public int createGame(String authToken, String gameName) throws HTTPException {

        var body = Map.of("gameName", gameName);

        var jsonBody = new Gson().toJson(body);
        HttpResponse<String> response;

        try {
            response = post(httpClient, port, "/game", jsonBody, authToken);
        } catch (Exception e) {
            throw new HTTPException("game creation failed due to server error");
        }

        var responseMap = new Gson().fromJson(response.body(), Map.class);
        var statusCode = response.statusCode();

        if (statusCode == 200) {
            return ((Double) responseMap.get("gameID")).intValue();

        } else {
            var errorMsg = responseMap.get("message");
            throw new HTTPException((String)errorMsg);
        }
    }

    public ArrayList<GameData> listGames(String authToken) throws HTTPException {

        HttpResponse<String> response;

        try {
            response = get(httpClient, port, "/game", authToken);
        } catch (Exception e) {
            throw new HTTPException("clearing database failed due to server error");
        }

        var statusCode = response.statusCode();

        if (statusCode == 200) {
            Type mapType = new TypeToken<Map<String, ArrayList<GameData>>>(){}.getType();
            Map<String, ArrayList<GameData>> responseMap = new Gson().fromJson(response.body(), mapType);

            return responseMap.get("games");

        } else {
            var errorMsg = new Gson().fromJson(response.body(), Map.class).get("message");
            throw new HTTPException((String)errorMsg);
        }
    }

    public void clear() throws HTTPException {

        HttpResponse<String> response;

        try {
            response = delete(httpClient, port, "/db", null);
        } catch (Exception e) {
            throw new HTTPException("clearing database failed due to server error");
        }

        var responseMap = new Gson().fromJson(response.body(), Map.class);
        var statusCode = response.statusCode();

        if (statusCode != 200) {
            var errorMsg = responseMap.get("message");
            throw new HTTPException((String)errorMsg);
        }
    }
}
