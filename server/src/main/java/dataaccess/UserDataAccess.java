package dataaccess;

import model.UserData;

import java.sql.SQLException;

public interface UserDataAccess {
    void createUser(UserData user) throws DataAccessException, SQLException;
    UserData getUser(UserData user) throws DataAccessException, SQLException;
    void clear() throws DataAccessException, SQLException;
}
