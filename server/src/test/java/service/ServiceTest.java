package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import service.register.RegisterRequest;

public class ServiceTest {

    @Test
    public void registerTwice() {
        var dataAccess = new MemoryDataAccess();
        var userService = new UserService(dataAccess);

        // Register a user as normal
        Assertions.assertDoesNotThrow(() -> {
            var res = userService.register(new RegisterRequest("test1", "test2", "test3@xyz.com"));
            Assertions.assertNotNull(res);
        });

        // Register the user again
        Assertions.assertThrows(DataAccessException.class, () ->
                userService.register(new RegisterRequest("test1", "test3", "test3@xyz.com")));
    }

    @Test
    public void registerNormal() {
        var dataAccess = new MemoryDataAccess();
        var userService = new UserService(dataAccess);

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
}
