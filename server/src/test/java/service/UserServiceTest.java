package service;

import service.exception.AlreadyTakenException;
import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import service.exception.UnauthorizedException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.login.LoginRequest;
import service.login.LoginResult;
import service.logout.LogoutRequest;
import service.register.RegisterRequest;
import service.register.RegisterResult;

public class UserServiceTest {

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

    @Test
    public void logoutTwice() {

        RegisterResult res = null;
        // Register a user
        try {
            res = userService.register(new RegisterRequest("test1", "test2", "test3@xyz.com"));
            Assertions.assertNotNull(res);
        } catch (AlreadyTakenException e) {
            Assertions.fail(e.getMessage());
        }

        // Log the user out
        try {
            userService.logout(new LogoutRequest(res.authToken()));
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }

        // Try logging out again (should fail)
        try {
            userService.logout(new LogoutRequest(res.authToken()));
            Assertions.fail();
        } catch (Exception _) {
        }
    }

    @Test
    public void logoutNormal() {
        RegisterResult res1;
        LoginResult res2 = null;
        // Register a user
        try {
            res1 = userService.register(new RegisterRequest("test1", "test2", "test3@xyz.com"));
            Assertions.assertNotNull(res1);
        } catch (AlreadyTakenException e) {
            Assertions.fail(e.getMessage());
        }

        // Log in the user
        try {
            res2 = userService.login(new LoginRequest("test1", "test2"));
            Assertions.assertNotNull(res2);
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }

        // Log the user out with the most recent auth token
        try {
            userService.logout(new LogoutRequest(res2.authToken()));
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

}
