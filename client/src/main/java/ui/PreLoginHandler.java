package ui;

public class PreLoginHandler implements UIHandler {
    public void parse(String[] inputs) throws InvalidInputException {
        switch (inputs[0]) {
            case "help":
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
}
