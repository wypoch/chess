import console.Client;
import serverfacade.ServerFacade;
import websocket.WebSocketFacade;

public class Main {

    public static void main(String[] args) {
        System.out.println("Welcome to the chess client! Type help to get started.\n");
        int port = 8080;

        // Start the main loop for the client
        var client = new Client(port);
        client.mainLoop();
    }
}