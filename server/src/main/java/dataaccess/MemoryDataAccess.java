package dataaccess;

import java.util.HashMap;

import model.AuthData;
import model.UserData;

public class MemoryDataAccess implements DataAccess {

    private final HashMap<String, UserData> userDataSaved = new HashMap<>();
    private final HashMap<String, AuthData> authDataSaved = new HashMap<>();

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

    @Override
    public AuthData getAuth(String authToken) {
        for (var username : authDataSaved.keySet()) {
            var authData = authDataSaved.get(username);
            if (authData.authToken().equals(authToken)) {
                return authData;
            }
        }
        return null;
    }

    @Override
    public void deleteAuth(AuthData auth) throws DataAccessException {
        String target = auth.username();
        if (!(authDataSaved.containsKey(target))) {
            throw new DataAccessException("cannot delete AuthData which doesn't exist");
        }
        authDataSaved.remove(target);
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

    @Override
    public void createAuth(AuthData auth) {
        authDataSaved.put(auth.username(), auth);
    }

    @Override
    public void updateAuth(AuthData auth) throws DataAccessException {
        String target = auth.username();
        if (!(authDataSaved.containsKey(target))) {
            throw new DataAccessException("cannot update AuthData which doesn't exist");
        }
        authDataSaved.put(target, auth);
    }

}
