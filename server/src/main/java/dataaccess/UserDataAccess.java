package dataaccess;

import model.UserData;

import java.sql.SQLException;

public interface UserDataAccess {
    void createUser(UserData user);
    UserData getUser(UserData user) throws DataAccessException, SQLException;
    UserData loginUser(UserData user);
    void clear();
}
