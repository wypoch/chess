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
        String target = user.username();
        for (var username : userDataSaved.keySet()) {
            if (username.equals(target)) {
                return userDataSaved.get(username);
            }
        }
        return null;
    }

    @Override
    public UserData loginUser(UserData user) {
        String targetName = user.username();
        String targetPass = user.password();
        for (var username : userDataSaved.keySet()) {
            if (username.equals(targetName)) {
                // Ensure the password is correct
                var userData = userDataSaved.get(username);
                if (userData.password().equals(targetPass)) {
                    return userData;
                } else {
                    return null;
                }
            }
        }
        return null;
    }

}
