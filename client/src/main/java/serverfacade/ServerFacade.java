package serverfacade;

import com.google.gson.Gson;
import model.AuthData;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Locale;
import java.util.Map;

public class ServerFacade {

    private final int port;
    private static final HttpClient httpClient = HttpClient.newHttpClient();

    public ServerFacade(int port) {
        this.port = port;
    }

//    private HttpResponse<String> get(String path) throws Exception {
//        String urlString = String.format(Locale.getDefault(), "http://localhost:%d%s", port, path);
//
//        HttpRequest request = HttpRequest.newBuilder()
//                .uri(new URI(urlString))
//                .timeout(java.time.Duration.ofMillis(5000))
//                .GET()
//                .build();
//
//        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
//    }

    private HttpResponse<String> post(String path, String jsonBody) throws Exception {
        String urlString = String.format(Locale.getDefault(), "http://localhost:%d%s", port, path);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(urlString))
                .timeout(java.time.Duration.ofMillis(5000))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> delete(String path) throws Exception {
        String urlString = String.format(Locale.getDefault(), "http://localhost:%d%s", port, path);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(urlString))
                .timeout(java.time.Duration.ofMillis(5000))
                .DELETE()
                .build();

        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public AuthData register(String username, String password, String email) throws HTTPException {

        var body = Map.of("username", username,
                "password", password,
                "email", email);

        var jsonBody = new Gson().toJson(body);
        HttpResponse<String> response;

        try {
            response = post("/user", jsonBody);
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

    public void clear() throws HTTPException {

        HttpResponse<String> response;

        try {
            response = delete("/db");
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
