package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import model.UserData;
import service.login.LoginRequest;
import service.login.LoginResult;
import service.register.RegisterRequest;
import service.register.RegisterResult;
import service.logout.LogoutRequest;
import dataaccess.DataAccess;

public class UserService {
    public RegisterResult register(RegisterRequest registerRequest) throws DataAccessException {
        String username = registerRequest.username();

        var userData = new UserData(username, registerRequest.password(), registerRequest.email());

        var dataAccess = new MemoryDataAccess();
        var responseData = dataAccess.getUser(userData);
        if (responseData != null) {
            throw new DataAccessException("Error: username already taken");
        } else {
            dataAccess.saveUser(userData);
        }
        return new RegisterResult(username, "");
    }

    public LoginResult login(LoginRequest loginRequest) {
        return new LoginResult("", "");
    }
    public void logout(LogoutRequest logoutRequest) {}
}
