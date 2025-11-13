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
            // Get the tag for the console
            var tag = generateTag();
            System.out.print(tag);

            // Determine which state we are in
            UIHandler uiHandler;
            if (!loggedIn) {
                uiHandler = new PreLoginHandler();
            } else {
                uiHandler = new PostLoginHandler();
            }

            // Get user input and parse it
            String[] inputs = scanner.nextLine().split(" ");
            try {
                uiHandler.parse(inputs);
            }
            // User supplied an invalid input
            catch (InvalidInputException e) {
                System.out.printf("Error occurred: %s\n", e.getMessage());
            }
            // User requested application to quit
            catch (TerminationException e) {
                System.out.println("Quitting...");
                break;
            }
        }

    }

}
