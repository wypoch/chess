import ui.UIManager;

public class Main {

    public static void main(String[] args) {
        System.out.println("Welcome to the chess client! Type help to get started.\n");
        var uiManager = new UIManager();
        uiManager.mainLoop();
    }
}