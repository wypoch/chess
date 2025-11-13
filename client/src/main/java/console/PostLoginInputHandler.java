package console;

import serverfacade.ServerFacade;

import static ui.EscapeSequences.RESET_TEXT_COLOR;
import static ui.EscapeSequences.SET_TEXT_COLOR_BLUE;

public class PostLoginInputHandler implements InputHandler {

    String user;
    ServerFacade serverFacade;

    public PostLoginInputHandler(ServerFacade serverFacade) {
        this.serverFacade = serverFacade;
    }

    public void parse(String[] inputs) throws InvalidInputException {
        String option = inputs[0];
        switch (option) {
            case "help":
                // Display help menu
                System.out.println(getMenu());
                break;

            case "logout":
                break;

            case "create":
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

    public String getMenu() {
        return SET_TEXT_COLOR_BLUE + "create <name>" + RESET_TEXT_COLOR + " : create a game\n" +
                SET_TEXT_COLOR_BLUE + "list" + RESET_TEXT_COLOR + " : list all games\n" +
                SET_TEXT_COLOR_BLUE + "join <id> [white|black]" + RESET_TEXT_COLOR + " : join a game as color\n" +
                SET_TEXT_COLOR_BLUE + "observe <id>" + RESET_TEXT_COLOR + " : observe a game\n" +
                SET_TEXT_COLOR_BLUE + "logout" + RESET_TEXT_COLOR + " : logout the current player\n" +
                SET_TEXT_COLOR_BLUE + "quit" + RESET_TEXT_COLOR + " : exit the client\n" +
                SET_TEXT_COLOR_BLUE + "help" + RESET_TEXT_COLOR + " : display this help menu\n";
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
