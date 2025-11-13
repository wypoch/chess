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

}
