package client;

import org.junit.jupiter.api.*;
import server.Server;
import serverfacade.HTTPException;
import serverfacade.ServerFacade;

public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(port);
    }

    @BeforeEach
    public void clearDb() {
        try {
            facade.clear();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @Test
    void registerNormal() throws HTTPException {
        var authData1 = facade.register("player1", "password1", "p1@email.com");
        Assertions.assertEquals("player1", authData1.username());
        Assertions.assertTrue(authData1.authToken().length() > 10);

        var authData2 = facade.register("player2", "password2", "p2@email.com");
        Assertions.assertEquals("player2", authData2.username());
        Assertions.assertTrue(authData2.authToken().length() > 10);
    }

    @Test
    void registerInvalid() throws HTTPException {
        // Ensure we cannot register the same user twice
        facade.register("player1", "password1", "p1@email.com");
        Assertions.assertThrows(HTTPException.class, () -> facade.register("player1", "password2", "p2@email.com"));

        // Ensure that a null input field causes problems
        Assertions.assertThrows(Exception.class, () -> facade.register(null, "password2", "p2@email.com"));
        Assertions.assertThrows(Exception.class, () -> facade.register("player2", null, "p2@email.com"));
        Assertions.assertThrows(Exception.class, () -> facade.register("player2", "password2", null));
    }

    @Test
    void loginNormal() throws HTTPException {
        // Register a user
        var authData1 = facade.register("player1", "password1", "p1@email.com");
        var authToken1 = authData1.authToken();

        // Login with correct password
        var authData2 = facade.login("player1", "password1");
        Assertions.assertEquals("player1", authData2.username());
        var authToken2 = authData2.authToken();
        Assertions.assertTrue(authToken2.length() > 10);

        // Ensure the new auth token is different
        Assertions.assertNotEquals(authToken1, authToken2);

        // Login with an incorrect password (throws exception)
        Assertions.assertThrows(HTTPException.class, () -> facade.login("player1", "password2"));
    }

    @Test
    void loginInvalid() throws HTTPException {
        // Login before registering user
        Assertions.assertThrows(HTTPException.class, () -> facade.login("player1", "password1"));

        // Ensure that a null input field causes problems
        Assertions.assertThrows(Exception.class, () -> facade.login(null, "password1"));
        Assertions.assertThrows(Exception.class, () -> facade.login("player1", null));
    }

    @Test
    void logoutNormal() throws HTTPException {
        // Register a user
        var authData1 = facade.register("player1", "password1", "p1@email.com");

        // Logout
        facade.logout(authData1.authToken());

        // Login twice
        var authData2 = facade.login("player1", "password1");
        facade.login("player1", "password1");

        // Logout with first auth data
        facade.logout(authData2.authToken());
    }

    @Test
    void logoutInvalid() throws HTTPException {
        // Register a user
        var authData1 = facade.register("player1", "password1", "p1@email.com");

        // Logout
        facade.logout(authData1.authToken());

        // Login with correct password
        facade.login("player1", "password1");

        // Try logging out with original authToken
        Assertions.assertThrows(HTTPException.class, () -> facade.logout(authData1.authToken()));
    }

}
