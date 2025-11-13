package console;

import model.AuthData;
import model.GameData;
import serverfacade.HTTPException;
import serverfacade.ServerFacade;

import static ui.EscapeSequences.RESET_TEXT_COLOR;
import static ui.EscapeSequences.SET_TEXT_COLOR_BLUE;

public class InputHandler {

    String user = null;
    String authToken = null;
    ServerFacade serverFacade;

    public InputHandler(ServerFacade serverFacade) {
        this.serverFacade = serverFacade;
    }

    public String getUser() {
        return user;
    }

    public void preLoginParse(String[] inputs) throws InvalidInputException, TerminationException, HTTPException {

        String option = inputs[0];
        AuthData authData;

        switch (option) {
            case "help":
                if (inputs.length > 1) {
                    throw new InvalidInputException("command takes no additional inputs");
                }
                // Display help menu
                System.out.println(getPreLoginMenu());
                break;

            case "quit":
                if (inputs.length > 1) {
                    throw new InvalidInputException("command takes no additional inputs");
                }
                throw new TerminationException();

            case "register":
                // Ensure number of inputs is correct
                if (inputs.length != 4) {
                    throw new InvalidInputException("need to supply exactly username, password, and email");
                }

                // Register the specified user and save their info
                authData = serverFacade.register(inputs[1], inputs[2], inputs[3]);
                user = authData.username();
                authToken = authData.authToken();
                System.out.println("Registration successful!");
                break;

            case "login":
                // Ensure number of inputs is correct
                if (inputs.length != 3) {
                    throw new InvalidInputException("need to supply exactly username, password");
                }

                // Login the specified user and save their info
                authData = serverFacade.login(inputs[1], inputs[2]);
                user = authData.username();
                authToken = authData.authToken();
                System.out.println("Login successful!");
                break;

            default:
                throw new InvalidInputException("your input is not recognized");
        }
    }

    public String getPreLoginMenu() {
        return SET_TEXT_COLOR_BLUE + "register <username> <password> <email>" + RESET_TEXT_COLOR + " : register a user to play chess\n" +
                SET_TEXT_COLOR_BLUE + "login <username> <password>" + RESET_TEXT_COLOR + " : login a user\n" +
                SET_TEXT_COLOR_BLUE + "quit" + RESET_TEXT_COLOR + " : exit the client\n" +
                SET_TEXT_COLOR_BLUE + "help" + RESET_TEXT_COLOR + " : display this help menu\n";
    }

    public void postLoginParse(String[] inputs) throws InvalidInputException, HTTPException, TerminationException {

        String option = inputs[0];

        switch (option) {
            case "help":
                if (inputs.length > 1) {
                    throw new InvalidInputException("command takes no additional inputs");
                }
                // Display help menu
                System.out.println(getPostLoginMenu());
                break;

            case "quit":
                if (inputs.length > 1) {
                    throw new InvalidInputException("command takes no additional inputs");
                }
                throw new TerminationException();

            case "logout":
                if (inputs.length > 1) {
                    throw new InvalidInputException("command takes no additional inputs");
                }
                // Logout the current user and nullify the info
                serverFacade.logout(authToken);
                user = null;
                authToken = null;
                System.out.println("Logout successful!");
                break;

            case "create":
                if (inputs.length != 2) {
                    throw new InvalidInputException("need to supply exactly game name");
                }
                String gameName = inputs[1];
                int gameID = serverFacade.createGame(authToken, gameName);
                System.out.printf("Created game %s with ID %d\n", gameName, gameID);
                break;

            case "list":
                break;

            case "join":
                break;

            case "observe":
                break;

            default:
                throw new InvalidInputException("your input is not recognized");
        }
    }

    public String getPostLoginMenu() {
        return SET_TEXT_COLOR_BLUE + "create <name>" + RESET_TEXT_COLOR + " : create a game\n" +
                SET_TEXT_COLOR_BLUE + "list" + RESET_TEXT_COLOR + " : list all games\n" +
                SET_TEXT_COLOR_BLUE + "join <id> [white|black]" + RESET_TEXT_COLOR + " : join a game as color\n" +
                SET_TEXT_COLOR_BLUE + "observe <id>" + RESET_TEXT_COLOR + " : observe a game\n" +
                SET_TEXT_COLOR_BLUE + "logout" + RESET_TEXT_COLOR + " : logout the current player\n" +
                SET_TEXT_COLOR_BLUE + "quit" + RESET_TEXT_COLOR + " : exit the client\n" +
                SET_TEXT_COLOR_BLUE + "help" + RESET_TEXT_COLOR + " : display this help menu\n";
    }

}
