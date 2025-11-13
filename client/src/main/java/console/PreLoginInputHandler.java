package console;

import serverfacade.HTTPException;
import serverfacade.ServerFacade;

import static ui.EscapeSequences.RESET_TEXT_COLOR;
import static ui.EscapeSequences.SET_TEXT_COLOR_BLUE;

public class PreLoginInputHandler implements InputHandler {

    String user = null;
    String authToken = null;
    ServerFacade serverFacade;

    public PreLoginInputHandler(ServerFacade serverFacade) {
        this.serverFacade = serverFacade;
    }

    public void parse(String[] inputs) throws InvalidInputException, TerminationException, HTTPException {
        String option = inputs[0];
        switch (option) {
            case "help":
                // Display help menu
                System.out.println(getMenu());
                break;

            case "quit":
                throw new TerminationException();

            case "register":
                // Ensure number of inputs is correct
                if (inputs.length != 4) {
                    throw new InvalidInputException("need to supply exactly username, password, and email");
                }
                String username = inputs[1];
                String password = inputs[2];
                String email = inputs[3];

                // Register the specified user and save their info
                var registerResult = serverFacade.register(username, password, email);
                user = registerResult.username();
                authToken = registerResult.authToken();
                System.out.println("Login successful!");

                break;

            case "login":
                break;

            default:
                throw new InvalidInputException("your input is not recognized");
        }
    }

    public String getMenu() {
        return SET_TEXT_COLOR_BLUE + "register <username> <password> <email>" + RESET_TEXT_COLOR + " : register a user to play chess\n" +
                SET_TEXT_COLOR_BLUE + "login <username> <password>" + RESET_TEXT_COLOR + " : login a user\n" +
                SET_TEXT_COLOR_BLUE + "quit" + RESET_TEXT_COLOR + " : exit the client\n" +
                SET_TEXT_COLOR_BLUE + "help" + RESET_TEXT_COLOR + " : display this help menu\n";
    }

    public String getUser() {
        return user;
    }
}
