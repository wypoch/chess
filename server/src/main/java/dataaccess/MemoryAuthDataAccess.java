package dataaccess;

import model.AuthData;

import java.util.HashMap;

public class MemoryAuthDataAccess implements AuthDataAccess {

    private final HashMap<String, AuthData> authDataSaved = new HashMap<>();

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
    public void clear() {
        authDataSaved.clear();
    }
}
