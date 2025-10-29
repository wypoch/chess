package dataaccess;

import model.GameData;

import java.util.HashSet;

public class SQLGameDataAccess implements GameDataAccess {
    @Override
    public void createGame(GameData gameData) {

    }

    @Override
    public GameData getGame(Integer gameID) {
        return null;
    }

    @Override
    public void updateGame(GameData gameData) throws DataAccessException {

    }

    @Override
    public HashSet<GameData> listGames() {
        return null;
    }

    @Override
    public void clear() {

    }
}
