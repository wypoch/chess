package dataaccess;
import model.UserData;

public interface DataAccess {
    public void saveUser(UserData user);
    public UserData getUser(UserData user);
}
