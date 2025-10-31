package dataaccess;

import java.util.HashMap;

import model.UserData;

public class MemoryUserDataAccess implements UserDataAccess {

    private final HashMap<String, UserData> userDataSaved = new HashMap<>();

    @Override
    public void createUser(UserData user) {
        userDataSaved.put(user.username(), user);
    }

    @Override
    public UserData getUser(UserData user) {
        return userDataSaved.get(user.username());
    }

    @Override
    public void clear() {
        userDataSaved.clear();
    }

}
