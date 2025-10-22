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
    public UserData loginUser(UserData user) {
        String targetName = user.username();
        String targetPass = user.password();

        var userData = userDataSaved.get(targetName);
        if (userData == null) {
            return null;
        }
        if (userData.password().equals(targetPass)) {
            return userData;
        } else {
            return null;
        }
    }

}
