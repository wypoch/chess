package service;

import dataaccess.AuthDataAccess;
import dataaccess.GameDataAccess;
import dataaccess.UserDataAccess;

public record DatabaseService(UserDataAccess userDataAccess, AuthDataAccess authDataAccess,
                              GameDataAccess gameDataAccess) {

    public void clear() {
        userDataAccess.clear();
        authDataAccess.clear();
        gameDataAccess.clear();
    }
}
