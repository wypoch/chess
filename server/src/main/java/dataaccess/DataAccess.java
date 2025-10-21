package dataaccess;
import model.UserData;

public interface DataAccess {
    void saveUser(UserData user);
    UserData getUser(UserData user);
}
