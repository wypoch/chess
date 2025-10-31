package service;

import dataaccess.AuthDataAccess;
import dataaccess.UserDataAccess;
import dataaccess.DataAccessException;
import service.exception.AlreadyTakenException;
import service.exception.UnauthorizedException;
import model.UserData;
import model.AuthData;
import service.login.LoginRequest;
import service.login.LoginResult;
import service.register.RegisterRequest;
import service.register.RegisterResult;
import service.logout.LogoutRequest;

import java.sql.SQLException;
import java.util.UUID;

public record UserService(UserDataAccess userDataAccess, AuthDataAccess authDataAccess) {

    public RegisterResult register(RegisterRequest registerRequest) throws AlreadyTakenException, DataAccessException, SQLException {
        String username = registerRequest.username();

        var userData = new UserData(username, registerRequest.password(), registerRequest.email());

        // ensure the user doesn't already exist in the database
        var responseData = userDataAccess.getUser(userData);
        if (responseData != null) {
            throw new AlreadyTakenException("username already taken");
        }
        // save the user to the database and generate an auth token
        else {
            userDataAccess.createUser(userData);
            String authToken = UUID.randomUUID().toString();

            var authData = new AuthData(authToken, username);
            authDataAccess.createAuth(authData);

            return new RegisterResult(username, authToken);
        }
    }

    public LoginResult login(LoginRequest loginRequest) throws UnauthorizedException, DataAccessException, SQLException {
        String username = loginRequest.username();

        var userData = new UserData(username, loginRequest.password(), null);

        // ensure the username/password combo is correct
        var responseData = userDataAccess.loginUser(userData);
        if (responseData == null) {
            throw new UnauthorizedException("unauthorized");
        }
        // generate an auth token and update auth data
        else {
            String authToken = UUID.randomUUID().toString();

            var authData = new AuthData(authToken, username);
            authDataAccess.createAuth(authData);

            return new LoginResult(username, authToken);
        }

    }

    public void logout(LogoutRequest logoutRequest) throws UnauthorizedException, DataAccessException {
        String authToken = logoutRequest.authToken();

        // try to find the authData associated with the authToken
        var responseData = authDataAccess.getAuth(authToken);
        if (responseData == null) {
            throw new UnauthorizedException("unauthorized");
        }
        // assuming we found the authData, delete it
        else {
            authDataAccess.deleteAuth(responseData);
        }
    }
}
