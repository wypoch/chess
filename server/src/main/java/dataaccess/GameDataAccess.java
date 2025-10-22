package dataaccess;

import model.GameData;

public interface GameDataAccess {
    void createGame(GameData gameData);
    GameData getGame(Integer gameID);
    void updateGame(GameData gameData) throws DataAccessException;
}
