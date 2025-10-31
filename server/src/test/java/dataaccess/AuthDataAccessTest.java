package dataaccess;

import model.AuthData;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

public class AuthDataAccessTest {

    private SQLAuthDataAccess authDataAccess;

    @BeforeEach
    public void clearDatabase() {
        // configure and clear the database before each test
        try {
            authDataAccess = new SQLAuthDataAccess();
            authDataAccess.clear();
        }
        catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void createAuthNormal() {
        try {
            String authToken = "00000000-1111-2222-3333-444444444444";
            var testAuth1 = new AuthData(authToken, "testname");
            authDataAccess.createAuth(testAuth1);
            var testAuth2 = authDataAccess.getAuth(authToken);
            Assertions.assertEquals(testAuth1, testAuth2);

        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void createAuthInvalid() {
        try {
            // authToken is too long
            String authToken = "00000000-1111-2222-3333-444444444444-5";
            var testAuth1 = new AuthData(authToken, "testname");
            authDataAccess.createAuth(testAuth1);
            Assertions.fail();

        } catch (DataAccessException e) {
            Assertions.fail(e.getMessage());
        } catch (SQLException e) {
        }

        try {
            // creating the same auth twice
            String authToken = "00000000-1111-2222-3333-444444444444";
            var testAuth1 = new AuthData(authToken, "testname");
            authDataAccess.createAuth(testAuth1);
            authDataAccess.createAuth(testAuth1);
            Assertions.fail();

        } catch (DataAccessException e) {
            Assertions.fail(e.getMessage());
        } catch (SQLException e) {
        }
    }

    @Test
    public void getAuthNormal() {
        try {
            // get an auth that hasn't been created yet (expect null)
            String authToken = "00000000-1111-2222-3333-444444444444";
            var testAuth1 = authDataAccess.getAuth(authToken);
            Assertions.assertNull(testAuth1);

            // get an auth that has been created
            var testAuth2 = new AuthData(authToken,"testname");
            authDataAccess.createAuth(testAuth2);
            testAuth1 = authDataAccess.getAuth(authToken);
            Assertions.assertEquals(testAuth1, testAuth2);

        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void getAuthInvalid() {
        try {
            // authToken is too long
            String authToken = "00000000-1111-2222-3333-444444444444";
            var testAuth1 = new AuthData(authToken, "testname");
            authDataAccess.createAuth(testAuth1);

            // authToken is too long
            authToken = "00000000-1111-2222-3333-444444444444-5";
            var testAuth2 = authDataAccess.getAuth(authToken);
            Assertions.assertNull(testAuth2);

        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void deleteAuthNormal() {
        try {
            String authToken = "00000000-1111-2222-3333-444444444444";
            var testAuth1 = new AuthData(authToken,"testname");

            // delete an auth that has been created
            authDataAccess.createAuth(testAuth1);
            authDataAccess.deleteAuth(testAuth1);
            var testAuth2 = authDataAccess.getAuth(authToken);
            Assertions.assertNull(testAuth2);

        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void deleteAuthInvalid() {
        try {
            // delete an auth that hasn't been created yet
            String authToken = "00000000-1111-2222-3333-444444444444";
            authDataAccess.deleteAuth(new AuthData(authToken, "user"));
            Assertions.fail();

        } catch (SQLException e) {
            Assertions.fail(e.getMessage());
        } catch (DataAccessException e) {
        }
    }
}
