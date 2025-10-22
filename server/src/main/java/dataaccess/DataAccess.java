package dataaccess;
import model.AuthData;
import model.UserData;

public interface DataAccess {
    void saveUser(UserData user);
    UserData getUser(UserData user);
    UserData loginUser(UserData user);
    void createAuth(AuthData auth);
    void updateAuth(AuthData auth) throws DataAccessException;
    AuthData getAuth(String authToken);
    void deleteAuth(AuthData auth) throws DataAccessException;
}
