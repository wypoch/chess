package dataaccess;

import model.GameData;

import java.util.HashMap;

public class MemoryGameDataAccess implements GameDataAccess {

    private final HashMap<Integer, GameData> gameDataSaved = new HashMap<>();

    @Override
    public void createGame(GameData gameData) {
        gameDataSaved.put(gameData.gameID(), gameData);
    }
}
