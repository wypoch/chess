package dataaccess;

import model.GameData;

import java.sql.SQLException;
import java.util.HashSet;

public interface GameDataAccess {
    void createGame(GameData gameData) throws DataAccessException, SQLException;
    GameData getGame(Integer gameID) throws DataAccessException, SQLException;
    void updateGame(GameData gameData) throws DataAccessException, SQLException;
    HashSet<GameData> listGames() throws DataAccessException, SQLException;
    void clear() throws DataAccessException, SQLException;
}
