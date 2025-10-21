package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import service.register.RegisterRequest;

public class ServiceTest {

    @Test
    public void registerTwice() throws DataAccessException {
        var dataAccess = new MemoryDataAccess();
        var userService = new UserService(dataAccess);

        // Register a user as normal
        Assertions.assertDoesNotThrow(() -> {
            var res = userService.register(new RegisterRequest("test1", "test2", "test3@xyz.com"));
            Assertions.assertNotNull(res);
        });

        // Register the user again
        Assertions.assertThrows(DataAccessException.class, () -> {
            var res = userService.register(new RegisterRequest("test1", "test3", "test3@xyz.com"));
        });
    }
}
