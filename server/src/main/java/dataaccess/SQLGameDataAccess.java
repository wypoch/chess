package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;

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
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "INSERT INTO gameData (gameId, whiteUsername, blackUsername, gameName, game) VALUES(?, ?, ?, ?, ?)";
            try (var preparedStatement = conn.prepareStatement(statement)) {
                // serialize the game into a JSON for storage in the database
                var gameJson = new Gson().toJson(gameData.game());

                preparedStatement.setInt(1, gameData.gameID());
                preparedStatement.setString(2, gameData.whiteUsername());
                preparedStatement.setString(3, gameData.blackUsername());
                preparedStatement.setString(4, gameData.gameName());
                preparedStatement.setString(5, gameJson);
                preparedStatement.executeUpdate();
            }
        }
    }

    @Override
    public GameData getGame(Integer gameID) throws DataAccessException, SQLException  {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameId, whiteUsername, blackUsername, gameName, game FROM gameData WHERE gameId=?";
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setInt(1, gameID);
                try (var rs = preparedStatement.executeQuery()) {
                    if (rs.next()) {
                        var whiteUsername = rs.getString("whiteUsername");
                        var blackUsername = rs.getString("blackUsername");
                        var gameName = rs.getString("gameName");
                        var gameJson = rs.getString("game");

                        // Deserialize the game from the stored JSON
                        var game = new Gson().fromJson(gameJson, ChessGame.class);
                        return new GameData(gameID, whiteUsername, blackUsername, gameName, game);
                    }
                }
            }
        }
        return null;
    }

    @Override
    public void updateGame(GameData gameData) throws DataAccessException, SQLException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "UPDATE gameData SET game=? WHERE id=?";
            try (var preparedStatement = conn.prepareStatement(statement)) {
                // serialize the game into a JSON for storage in the database
                var gameJson = new Gson().toJson(gameData.game());

                preparedStatement.setInt(1, gameData.gameID());
                preparedStatement.setString(2, gameJson);
                var numUpdates = preparedStatement.executeUpdate();
                if (numUpdates == 0) {
                    throw new DataAccessException("cannot update GameData which doesn't exist");
                }
            }
        }
    }

    @Override
    public HashSet<GameData> listGames() throws DataAccessException, SQLException {
        HashSet<GameData> gamesList = new HashSet<>();
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameId, whiteUsername, blackUsername, gameName FROM gameData";
            try (var preparedStatement = conn.prepareStatement(statement)) {
                try (var rs = preparedStatement.executeQuery()) {
                    while (rs.next()) {
                        var gameID = rs.getInt("gameId");
                        var whiteUsername = rs.getString("whiteUsername");
                        var blackUsername = rs.getString("blackUsername");
                        var gameName = rs.getString("gameName");

                        gamesList.add(new GameData(gameID, whiteUsername, blackUsername, gameName, null));
                    }
                }
            }
        }
        return gamesList;
    }

    @Override
    public void clear() throws DataAccessException, SQLException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "TRUNCATE TABLE gameData";
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        }
    }
}
