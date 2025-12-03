package console;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import serverfacade.HTTPException;
import serverfacade.ServerFacade;
import ui.ChessBoardViewer;
import websocket.WebSocketFacade;

import java.util.ArrayList;
import java.util.HashMap;

import static ui.EscapeSequences.RESET_TEXT_COLOR;
import static ui.EscapeSequences.SET_TEXT_COLOR_BLUE;

public class InputHandler {

    String user = null;
    String authToken = null;
    Integer gameID = null;

    ServerFacade serverFacade;
    WebSocketFacade webSocketFacade;
    HashMap<Integer, Integer> gameNumToID = new HashMap<>();
    HashMap<Integer, String> gameNumToName = new HashMap<>();

    public InputHandler(ServerFacade serverFacade, WebSocketFacade webSocketFacade) {
        this.serverFacade = serverFacade;
        this.webSocketFacade = webSocketFacade;
    }

    public String getUser() {
        return user;
    }

    public Integer getGameID() {
        return gameID;
    }

    public void preLoginParse(String[] inputs) throws InvalidInputException, TerminationException, HTTPException {

        String option = inputs[0];

        switch (option) {
            case "help":
                parseHelpPreLogin(inputs);
                break;

            case "quit":
                parseQuit(inputs);

            case "register":
                parseRegister(inputs);
                break;

            case "login":
                parseLogin(inputs);
                break;

            default:
                throw new InvalidInputException("your input is not recognized");
        }
    }

    public void postLoginParse(String[] inputs) throws InvalidInputException, HTTPException, TerminationException {

        String option = inputs[0];

        switch (option) {
            case "help":
                parseHelpPostLogin(inputs);
                break;

            case "quit":
                parseQuit(inputs);

            case "logout":
                parseLogout(inputs);
                break;

            case "create":
                parseCreateGame(inputs);
                break;

            case "list":
                parseListGames(inputs);
                break;

            case "join":
                parseJoinGame(inputs);
                break;

            case "observe":
                parseObserveGame(inputs);
                break;

            default:
                throw new InvalidInputException("your input is not recognized");
        }
    }

    public void gameplayParse(String[] inputs) throws InvalidInputException {

        String option = inputs[0];

        switch (option) {
            case "help":
                parseHelpGameplay(inputs);
                break;

            case "redraw":
                break;

            case "leave":
                break;

            case "move":
                break;

            case "resign":
                break;

            case "highlight":
                break;

            default:
                throw new InvalidInputException("your input is not recognized");
        }
    }

    public void parseQuit(String []inputs) throws InvalidInputException, TerminationException {
        if (inputs.length > 1) {
            throw new InvalidInputException("command takes no additional inputs");
        }
        throw new TerminationException();
    }

    public void parseHelpPreLogin(String[] inputs) throws InvalidInputException {
        String preLoginMenu =
                SET_TEXT_COLOR_BLUE + "register <username> <password> <email>" + RESET_TEXT_COLOR + " : register a user to play chess\n" +
                SET_TEXT_COLOR_BLUE + "login <username> <password>" + RESET_TEXT_COLOR + " : login a user\n" +
                SET_TEXT_COLOR_BLUE + "quit" + RESET_TEXT_COLOR + " : exit the client\n" +
                SET_TEXT_COLOR_BLUE + "help" + RESET_TEXT_COLOR + " : display this help menu";

        if (inputs.length > 1) {
            throw new InvalidInputException("command takes no additional inputs");
        }
        // Display help menu
        System.out.println(preLoginMenu);
    }

    public void parseHelpPostLogin(String[] inputs) throws InvalidInputException {
        String postLoginMenu = SET_TEXT_COLOR_BLUE + "create <name>" + RESET_TEXT_COLOR + " : create a game\n" +
                SET_TEXT_COLOR_BLUE + "list" + RESET_TEXT_COLOR + " : list all games\n" +
                SET_TEXT_COLOR_BLUE + "join <id> [white|black]" + RESET_TEXT_COLOR + " : join a game as color\n" +
                SET_TEXT_COLOR_BLUE + "observe <id>" + RESET_TEXT_COLOR + " : observe a game\n" +
                SET_TEXT_COLOR_BLUE + "logout" + RESET_TEXT_COLOR + " : logout the current player\n" +
                SET_TEXT_COLOR_BLUE + "quit" + RESET_TEXT_COLOR + " : exit the client\n" +
                SET_TEXT_COLOR_BLUE + "help" + RESET_TEXT_COLOR + " : display this help menu";

        if (inputs.length > 1) {
            throw new InvalidInputException("command takes no additional inputs");
        }
        // Display help menu
        System.out.println(postLoginMenu);
    }

    public void parseHelpGameplay(String[] inputs) throws InvalidInputException {
        String postLoginMenu = SET_TEXT_COLOR_BLUE + "redraw" + RESET_TEXT_COLOR + " : redraw the board\n" +
                SET_TEXT_COLOR_BLUE + "highlight <pos>" + RESET_TEXT_COLOR + " : highlight legal moves for piece at position\n" +
                SET_TEXT_COLOR_BLUE + "move <start> <end>" + RESET_TEXT_COLOR + " : moves piece from start position to end position\n" +
                SET_TEXT_COLOR_BLUE + "resign" + RESET_TEXT_COLOR + " : forfeit the game\n" +
                SET_TEXT_COLOR_BLUE + "leave" + RESET_TEXT_COLOR + " : exit the game\n" +
                SET_TEXT_COLOR_BLUE + "help" + RESET_TEXT_COLOR + " : display this help menu";

        if (inputs.length > 1) {
            throw new InvalidInputException("command takes no additional inputs");
        }
        // Display help menu
        System.out.println(postLoginMenu);
    }

    public void parseRegister(String[] inputs) throws InvalidInputException {
        // Ensure number of inputs is correct
        if (inputs.length != 4) {
            throw new InvalidInputException("need to supply exactly username, password, and email");
        }

        // Register the specified user and save their info
        AuthData authData = serverFacade.register(inputs[1], inputs[2], inputs[3]);
        user = authData.username();
        authToken = authData.authToken();
        System.out.println("Registration successful!");
    }

    public void parseLogin(String[] inputs) throws InvalidInputException {
        // Ensure number of inputs is correct
        if (inputs.length != 3) {
            throw new InvalidInputException("need to supply exactly username, password");
        }

        // Login the specified user and save their info
        AuthData authData = serverFacade.login(inputs[1], inputs[2]);
        user = authData.username();
        authToken = authData.authToken();
        System.out.println("Login successful!");
    }

    public void parseLogout(String[] inputs) throws InvalidInputException {
        if (inputs.length > 1) {
            throw new InvalidInputException("command takes no additional inputs");
        }
        // Logout the current user and nullify the info
        serverFacade.logout(authToken);
        user = null;
        authToken = null;
        System.out.println("Logout successful!");
    }

    public void parseCreateGame(String[] inputs) throws InvalidInputException {
        if (inputs.length != 2) {
            throw new InvalidInputException("need to supply exactly game name");
        }
        String gameName = inputs[1];
        serverFacade.createGame(authToken, gameName);
        System.out.printf("Created game %s\n", gameName);
    }

    public void parseListGames(String[] inputs) throws InvalidInputException {
        if (inputs.length > 1) {
            throw new InvalidInputException("command takes no additional inputs");
        }
        ArrayList<GameData> games = serverFacade.listGames(authToken);
        System.out.println("Listing game information");

        // Print table header
        System.out.println("|-------|-----------------|-----------------|-----------------|");
        String row = "| %-5s | %-15s | %-15s | %-15s |\n";
        System.out.printf(row, "Num", "Game Name", "White Player", "Black Player");
        System.out.println("|-------|-----------------|-----------------|-----------------|");

        // Print table rows
        for (int i = 0; i < games.size(); i++) {
            GameData game = games.get(i);
            gameNumToID.put(i+1, game.gameID());
            gameNumToName.put(i+1, game.gameName());
            System.out.printf(row, i+1, game.gameName(), game.whiteUsername(), game.blackUsername());
        }

        // Print table footer
        System.out.println("|-------|-----------------|-----------------|-----------------|");
    }

    public void parseJoinGame(String[] inputs) throws InvalidInputException {
        if (inputs.length != 3) {
            throw new InvalidInputException("need to supply exactly game ID and color");
        }
        Integer gameNumAsInt;
        String gameNum;
        Integer gameID;

        gameNum = inputs[1];
        String playerColor = inputs[2];

        if (!playerColor.equals("white") && !playerColor.equals("black")) {
            throw new InvalidInputException("invalid color provided");
        }

        try {
            gameNumAsInt = Integer.parseInt(gameNum);
        } catch (NumberFormatException e) {
            throw new InvalidInputException("provided value is not an integer");
        }

        gameID = gameNumToID.get(gameNumAsInt);
        if (gameID == null) {
            throw new InvalidInputException("provided value does not correspond to any known games...try the list command again?");
        }

        // Join the game and remember the game ID
        serverFacade.joinGame(authToken, playerColor, gameID);
        this.gameID = gameID;
        String gameName = gameNumToName.get(gameNumAsInt);

        System.out.printf("Joined game %s (ID %d) as %s color\n", gameName, gameNumAsInt, playerColor);

        // Initiate connect request
        webSocketFacade.connectPlayer(authToken, gameID);
    }

    public void parseObserveGame(String[] inputs) throws InvalidInputException {
        if (inputs.length != 2) {
            throw new InvalidInputException("need to supply exactly game ID");
        }
        Integer gameNumAsInt;
        String gameNum;
        Integer gameID;

        gameNum = inputs[1];

        try {
            gameNumAsInt = Integer.parseInt(gameNum);
        } catch (NumberFormatException e) {
            throw new InvalidInputException("provided value is not an integer");
        }

        gameID = gameNumToID.get(gameNumAsInt);
        if (gameID == null) {
            throw new InvalidInputException("provided value does not correspond to any known games...try the list command again?");
        }

        this.gameID = gameID;

        System.out.printf("Observing game %s (ID %d)\n", gameNumToName.get(gameNumAsInt), gameNumAsInt);

        var chessGameObserve = new ChessGame();
        ChessBoardViewer.showBoard(chessGameObserve.getBoard(), ChessGame.TeamColor.WHITE);
    }
}
