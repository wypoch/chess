package service;

import dataaccess.AlreadyTakenException;
import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import dataaccess.UnauthorizedException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.login.LoginRequest;
import service.register.RegisterRequest;

public class ServiceTest {

    DataAccess dataAccess;
    UserService userService;

    @BeforeEach
    public void setup() {
        dataAccess = new MemoryDataAccess();
        userService = new UserService(dataAccess);
    }

    @Test
    public void registerTwice() {
        // Register a user as normal
        Assertions.assertDoesNotThrow(() -> {
            var res = userService.register(new RegisterRequest("test1", "test2", "test3@xyz.com"));
            Assertions.assertNotNull(res);
        });

        // Register the user again
        Assertions.assertThrows(AlreadyTakenException.class, () ->
                userService.register(new RegisterRequest("test1", "test3", "test3@xyz.com")));
    }

    @Test
    public void registerNormal() {
        // Register a user as normal
        Assertions.assertDoesNotThrow(() -> {
            var res = userService.register(new RegisterRequest("test4", "test5", "test6@xyz.com"));
            Assertions.assertNotNull(res);
        });

        // Register another user as normal
        Assertions.assertDoesNotThrow(() -> {
            var res = userService.register(new RegisterRequest("test7", "test8", "test9@xyz.com"));
            Assertions.assertNotNull(res);
        });

    }

    @Test
    public void loginInvalid() {
        // Try to log in without registering
        Assertions.assertThrows(UnauthorizedException.class, () ->
                userService.login(new LoginRequest("test1", "test2")));

        // Now register the user
        Assertions.assertDoesNotThrow(() -> {
            var res = userService.register(new RegisterRequest("test1", "test2", "test3@xyz.com"));
            Assertions.assertNotNull(res);
        });

        // Now try to log in with the wrong password
        Assertions.assertThrows(UnauthorizedException.class, () ->
                userService.login(new LoginRequest("test1", "test3")));
    }

    @Test
    public void loginNormal() {
        // Register a user and then log in
        Assertions.assertDoesNotThrow(() -> {
            var res = userService.register(new RegisterRequest("test4", "test5", "test6@xyz.com"));
            Assertions.assertNotNull(res);
        });

        // Register another user as normal
        Assertions.assertDoesNotThrow(() -> {
            var res = userService.login(new LoginRequest("test4", "test5"));
            Assertions.assertNotNull(res);
        });
    }

}
