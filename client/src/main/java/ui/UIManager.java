package ui;

import java.util.Scanner;

public class UIManager {

    private String currUser = null;
    private boolean loggedIn = false;

    private String generateTag() {
        if (!loggedIn) {
            return "(logged out) >>> ";
        } else {
            return String.format("(%s) >>> ", currUser);
        }
    }

    public void mainLoop() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            var tag = generateTag();
            System.out.print(tag);
            String[] inputs = scanner.nextLine().split(" ");

            UIHandler uiHandler;
            if (!loggedIn) {
                uiHandler = new PreLoginHandler();
            } else {
                uiHandler = new PostLoginHandler();
            }

            try {
                uiHandler.parse(inputs);
            } catch (InvalidInputException e) {
                System.out.printf("Error occurred: %s\n", e.getMessage());
            }
        }

    }

}
