package dataaccess;

import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

public class UserDataAccessTest {

    SQLUserDataAccess userDataAccess;

    @BeforeEach
    public void clearDatabase() {
        // configure and clear the database before each test
        try {
            userDataAccess = new SQLUserDataAccess();
            userDataAccess.clear();
        }
        catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void createUserNormal() {
        try {
            var testUser1 = new UserData("testname", "testpass", "test@xyz.com");
            userDataAccess.createUser(testUser1);
            var testUser2 = userDataAccess.getUser(testUser1);
            Assertions.assertEquals(testUser1, testUser2);

        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void createUserTwice() {
        try {
            var testUser1 = new UserData("testname", "testpass", "test@xyz.com");
            // create the same user twice
            userDataAccess.createUser(testUser1);
            userDataAccess.createUser(testUser1);
            Assertions.fail();
        }
        // handle exceptions (fail if not SQLException)
        catch (DataAccessException e) {
            Assertions.fail(e.getMessage());
        }
        catch (SQLException e) {
        }
    }

    @Test
    public void getUserNormal() {
        try {
            // get a user that hasn't been created yet (expect null)
            var testUser1 = new UserData("testname", "testpass", "test@xyz.com");
            var testUser2 = userDataAccess.getUser(testUser1);
            Assertions.assertNull(testUser2);

            // get a user that has been created
            userDataAccess.createUser(testUser1);
            testUser2 = userDataAccess.getUser(testUser1);
            Assertions.assertEquals(testUser1, testUser2);

        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void getUserInvalid() {
        try {
            // create a user
            var testUser1 = new UserData("testname", "testpass", "test@xyz.com");
            userDataAccess.createUser(testUser1);

            // Try to get another user with the same password and email as the first (should be null)
            var testUser2 = new UserData("testname2", "testpass", "test@xyz.com");
            var testUser3 = userDataAccess.getUser(testUser2);
            Assertions.assertNull(testUser3);

        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }
}
