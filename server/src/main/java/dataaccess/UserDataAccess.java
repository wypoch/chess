package dataaccess;

import model.UserData;

public interface UserDataAccess {
    void saveUser(UserData user);
    UserData getUser(UserData user);
    UserData loginUser(UserData user);
}
