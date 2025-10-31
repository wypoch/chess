package dataaccess;

import model.AuthData;

import java.sql.SQLException;

public interface AuthDataAccess {
    void createAuth(AuthData auth) throws DataAccessException, SQLException;
    AuthData getAuth(String authToken) throws DataAccessException, SQLException;
    void deleteAuth(AuthData auth) throws DataAccessException, SQLException;
    void clear() throws DataAccessException, SQLException;
}
