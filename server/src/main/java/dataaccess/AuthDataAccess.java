package dataaccess;

import model.AuthData;

public interface AuthDataAccess {
    void createAuth(AuthData auth);
    AuthData getAuth(String authToken);
    void deleteAuth(AuthData auth) throws DataAccessException;
    void clear();
}
