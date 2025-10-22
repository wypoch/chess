package dataaccess;

import model.UserData;

public interface UserDataAccess {
    void createUser(UserData user);
    UserData getUser(UserData user);
    UserData loginUser(UserData user);
    void clear();
}
