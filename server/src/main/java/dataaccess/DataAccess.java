package dataaccess;
import model.AuthData;
import model.UserData;

public interface DataAccess {
    void saveUser(UserData user);
    UserData getUser(UserData user);
    void createAuth(AuthData auth);
}
