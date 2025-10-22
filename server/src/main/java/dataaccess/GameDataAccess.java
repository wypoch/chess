package dataaccess;

import model.GameData;

import java.util.HashSet;

public interface GameDataAccess {
    void createGame(GameData gameData);
    GameData getGame(Integer gameID);
    void updateGame(GameData gameData) throws DataAccessException;
    HashSet<GameData> listGames();
    void clear();
}
