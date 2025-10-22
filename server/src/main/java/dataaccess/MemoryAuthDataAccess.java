package dataaccess;

import model.AuthData;

import java.util.HashMap;

public class MemoryAuthDataAccess implements AuthDataAccess {

    private final HashMap<String, AuthData> authDataSaved = new HashMap<>();

    @Override
    public void createAuth(AuthData auth) {
        authDataSaved.put(auth.authToken(), auth);
    }

    @Override
    public AuthData getAuth(String authToken) {
        return authDataSaved.get(authToken);
    }

    @Override
    public void deleteAuth(AuthData auth) throws DataAccessException {
        String target = auth.authToken();
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
