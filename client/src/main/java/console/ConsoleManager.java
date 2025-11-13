package console;

import serverfacade.HTTPException;
import serverfacade.ServerFacade;

import java.util.Scanner;

public class ConsoleManager {

    private String currUser = null;
    private final ServerFacade serverFacade;

    public ConsoleManager(ServerFacade serverFacade) {
        this.serverFacade = serverFacade;
    }

    private String generateTag() {
        if (currUser == null) {
            return "(logged out) >>> ";
        } else {
            return String.format("(%s) >>> ", currUser);
        }
    }

    public void mainLoop() {
        Scanner scanner = new Scanner(System.in);
        var inputHandler = new InputHandler(serverFacade);

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
                } else {
                    inputHandler.postLoginParse(inputs);
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
        }

    }

}
