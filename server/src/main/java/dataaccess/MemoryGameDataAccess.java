package dataaccess;

import model.GameData;

import java.util.HashMap;
import java.util.HashSet;

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

    @Override
    public HashSet<GameData> listGames() {
        HashSet<GameData> gamesList = new HashSet<>();
        // Iterate through gameDataSaved and extract everything but the game
        for (var gameData : gameDataSaved.values()) {
            gamesList.add(new GameData(gameData.gameID(),
                    gameData.whiteUsername(), gameData.blackUsername(),
                    gameData.gameName(), null));
        }
        return gamesList;
    }
}
