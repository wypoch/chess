package ui;

public class PostLoginHandler implements UIHandler {
    public void parse(String[] inputs) throws InvalidInputException {
        switch (inputs[0]) {
            case "help":
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
}
