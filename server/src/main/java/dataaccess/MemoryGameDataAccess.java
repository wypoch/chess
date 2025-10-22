package dataaccess;

import model.GameData;

import java.util.HashMap;

public class MemoryGameDataAccess implements GameDataAccess {

    private final HashMap<Integer, GameData> gameDataSaved = new HashMap<>();

    @Override
    public void createGame(GameData gameData) {
        gameDataSaved.put(gameData.gameID(), gameData);
    }

    @Override
    public GameData getGame(Integer gameID) {
        return gameDataSaved.get(gameID);
    }

    @Override
    public void updateGame(GameData gameData) throws DataAccessException {
        Integer target = gameData.gameID();
        if (!(gameDataSaved.containsKey(target))) {
            throw new DataAccessException("cannot update GameData which doesn't exist");
        }
        gameDataSaved.put(target, gameData);
    }
}
