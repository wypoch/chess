package dataaccess;

import model.GameData;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;

public class SQLGameDataAccess implements GameDataAccess {

    public SQLGameDataAccess() throws DataAccessException, SQLException {
        configureDatabase();
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS gameData (
              `gameId` int NOT NULL,
              `whiteUsername` varchar(255),
              `blackUsername` varchar(255),
              `gameName` varchar(255),
              `game` json,
              PRIMARY KEY (`gameId`),
              INDEX(`whiteUsername`),
              INDEX(`blackUsername`),
              INDEX(`gameName`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };

    private void configureDatabase() throws DataAccessException, SQLException {
        DatabaseManager.createDatabase();
        try (Connection conn = DatabaseManager.getConnection()) {
            for (String statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        }
    }

    @Override
    public void createGame(GameData gameData) throws DataAccessException, SQLException  {

    }

    @Override
    public GameData getGame(Integer gameID) throws DataAccessException, SQLException  {
        return null;
    }

    @Override
    public void updateGame(GameData gameData) throws DataAccessException, SQLException {

    }

    @Override
    public HashSet<GameData> listGames() throws DataAccessException, SQLException {
        return null;
    }

    @Override
    public void clear() throws DataAccessException, SQLException {

    }
}
