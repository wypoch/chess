package dataaccess;

import java.util.HashMap;
import model.UserData;

public class MemoryDataAccess implements DataAccess {

    private final HashMap<String, UserData> userDataSaved = new HashMap<>();

    @Override
    public void saveUser(UserData user) {
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
}
