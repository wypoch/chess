package dataaccess;

import model.AuthData;

public interface AuthDataAccess {
    void createAuth(AuthData auth);
    void updateAuth(AuthData auth) throws DataAccessException;
    AuthData getAuth(String authToken);
    void deleteAuth(AuthData auth) throws DataAccessException;
}
