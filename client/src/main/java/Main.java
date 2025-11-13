import console.ConsoleManager;
import serverfacade.ServerFacade;

public class Main {

    public static void main(String[] args) {
        System.out.println("Welcome to the chess client! Type help to get started.\n");

        // Start the serverFacade
        var serverFacade = new ServerFacade(8080);
        var uiManager = new ConsoleManager(serverFacade);
        uiManager.mainLoop();
    }
}