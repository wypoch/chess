package serverfacade;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Locale;

public class HTTPHelper {
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

    public static HttpResponse<String> post(HttpClient httpClient, int port, String path, String jsonBody, String authorization) throws Exception {
        String urlString = String.format(Locale.getDefault(), "http://localhost:%d%s", port, path);
        HttpRequest request;

        if (authorization != null) {
            request = HttpRequest.newBuilder()
                    .uri(new URI(urlString))
                    .timeout(java.time.Duration.ofMillis(5000))
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .header("authorization", authorization)
                    .build();
        } else {
            request = HttpRequest.newBuilder()
                    .uri(new URI(urlString))
                    .timeout(java.time.Duration.ofMillis(5000))
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();
        }

        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public static HttpResponse<String> delete(HttpClient httpClient, int port, String path, String authorization) throws Exception {
        String urlString = String.format(Locale.getDefault(), "http://localhost:%d%s", port, path);
        HttpRequest request;

        if (authorization != null) {
            request = HttpRequest.newBuilder()
                    .uri(new URI(urlString))
                    .timeout(java.time.Duration.ofMillis(5000))
                    .DELETE()
                    .header("authorization", authorization)
                    .build();
        } else {
            request = HttpRequest.newBuilder()
                    .uri(new URI(urlString))
                    .timeout(java.time.Duration.ofMillis(5000))
                    .DELETE()
                    .build();
        }

        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
