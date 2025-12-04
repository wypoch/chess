package console;

import com.google.gson.Gson;
import serverfacade.HTTPException;
import serverfacade.ServerFacade;
import ui.ChessBoardViewer;
import websocket.ServerMessageObserver;
import websocket.WebSocketFacade;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.util.Scanner;

public class Client implements ServerMessageObserver {

    private String currUser = null;
    private String currGame = null;

    private final ServerFacade serverFacade;
    private final WebSocketFacade webSocketFacade;

    public Client(int port) {

        // Start the ServerFacade and WebSocketFacade
        var serverFacade = new ServerFacade(port);
        var webSocketFacade = new WebSocketFacade(port, this);

        this.serverFacade = serverFacade;
        this.webSocketFacade = webSocketFacade;
    }

    @Override
    public void notify(String message) {
        ServerMessage msg = new Gson().fromJson(message, ServerMessage.class);
        switch (msg.getServerMessageType()) {
            case NOTIFICATION -> displayNotification(new Gson().fromJson(message, NotificationMessage.class).getMessage());
            case ERROR -> displayError(new Gson().fromJson(message, ErrorMessage.class).getMessage());
            case LOAD_GAME -> loadGame(new Gson().fromJson(message, LoadGameMessage.class));
        }
    }

    void displayNotification(String message) {
        System.out.println(message);
        System.out.print(generateTag());
    }

    void displayError(String error) {
        System.out.println(error);
        System.out.print(generateTag());
    }

    void loadGame(LoadGameMessage message) {
        ChessBoardViewer.showBoard(message.getGame().getBoard(), message.getColor());
        System.out.print(generateTag());
    }

    private String generateTag() {
        if (currUser == null) {
            return "(logged out) >>> ";
        } else if (currGame == null) {
            return String.format("(%s) >>> ", currUser);
        } else {
            return String.format("(%s playing game %s) >>> ", currUser, currGame);
        }
    }

    public void mainLoop() {
        Scanner scanner = new Scanner(System.in);
        var inputHandler = new InputHandler(serverFacade, webSocketFacade);

        while (true) {
            // Get the tag for the console
            var tag = generateTag();
            System.out.print(tag);

            // Get user input and parse it
            String[] inputs = scanner.nextLine().split(" ");

            try {
                // Determine which state we are in and update the inputHandler
                if (currUser == null) {
                    inputHandler.preLoginParse(inputs);
                } else if (currGame == null) {
                    inputHandler.postLoginParse(inputs);
                } else {
                    inputHandler.gameplayParse(inputs);
                }
            }
            // User supplied an invalid input
            catch (InvalidInputException e) {
                System.out.printf("Error: %s\n", e.getMessage());
            }
            // User requested application to quit
            catch (TerminationException e) {
                System.out.println("Quitting...");
                break;
            }
            // Some other HTTP error
            catch (HTTPException e) {
                System.out.println(e.getMessage());
            }

            // Update the current user
            currUser = inputHandler.getUser();
            currGame = inputHandler.getGameName();
        }

    }
}
