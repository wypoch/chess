package service;

import dataaccess.DataAccess;
import dataaccess.AlreadyTakenException;
import model.UserData;
import model.AuthData;
import service.login.LoginRequest;
import service.login.LoginResult;
import service.register.RegisterRequest;
import service.register.RegisterResult;
import service.logout.LogoutRequest;

import java.util.UUID;

public class UserService {

    private final DataAccess dataAccess;

    public UserService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public RegisterResult register(RegisterRequest registerRequest) throws AlreadyTakenException {
        String username = registerRequest.username();

        var userData = new UserData(username, registerRequest.password(), registerRequest.email());

        // ensure the user doesn't already exist in the database
        var responseData = dataAccess.getUser(userData);
        if (responseData != null) {
            throw new AlreadyTakenException("username already taken");
        }
        // save the user to the database and generate an auth token
        else {
            dataAccess.saveUser(userData);
            String authToken = UUID.randomUUID().toString();

            var authData = new AuthData(authToken, username);
            dataAccess.createAuth(authData);

            return new RegisterResult(username, authToken);
        }
    }

    public LoginResult login(LoginRequest loginRequest) {
        return new LoginResult("", "");
    }
    public void logout(LogoutRequest logoutRequest) {}
}
