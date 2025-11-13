package ui;

import static ui.EscapeSequences.RESET_TEXT_COLOR;
import static ui.EscapeSequences.SET_TEXT_COLOR_BLUE;

public class PreLoginHandler implements UIHandler {
    public void parse(String[] inputs) throws InvalidInputException {
        switch (inputs[0]) {
            case "help":
                System.out.println(getMenu());
                break;
            case "quit":
                break;
            case "register":
                break;
            case "login":
                break;
            default:
                throw new InvalidInputException("your input is not recognized");
        }
    }

    public String getMenu() {
        return SET_TEXT_COLOR_BLUE + "register <username> <password> <email>" + RESET_TEXT_COLOR + " : register a user to play chess\n" +
                SET_TEXT_COLOR_BLUE + "login <username> <password>" + RESET_TEXT_COLOR + " : login a user to play chess\n" +
                SET_TEXT_COLOR_BLUE + "quit" + RESET_TEXT_COLOR + " : exit the client\n" +
                SET_TEXT_COLOR_BLUE + "help" + RESET_TEXT_COLOR + " : display this help menu\n";
    }
}
