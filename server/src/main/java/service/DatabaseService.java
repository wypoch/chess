package service;

import dataaccess.AuthDataAccess;
import dataaccess.DataAccessException;
import dataaccess.GameDataAccess;
import dataaccess.UserDataAccess;

import java.sql.SQLException;

public record DatabaseService(UserDataAccess userDataAccess, AuthDataAccess authDataAccess,
                              GameDataAccess gameDataAccess) {

    public void clear() throws DataAccessException, SQLException {
        userDataAccess.clear();
        authDataAccess.clear();
        gameDataAccess.clear();
    }
}
