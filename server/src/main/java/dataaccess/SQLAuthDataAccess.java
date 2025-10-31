package dataaccess;

import model.AuthData;

import java.sql.Connection;
import java.sql.SQLException;

public class SQLAuthDataAccess implements AuthDataAccess {

    public SQLAuthDataAccess() throws DataAccessException, SQLException {
        configureDatabase();
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS authData (
              `authToken` varchar(36) NOT NULL,
              `username` varchar(255) NOT NULL,
              PRIMARY KEY (`authToken`),
              INDEX(`username`)
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
    public void createAuth(AuthData auth) throws DataAccessException, SQLException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "INSERT INTO authData (authToken, username) VALUES(?, ?)";
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, auth.authToken());
                preparedStatement.setString(2, auth.username());
                preparedStatement.executeUpdate();
            }
        }
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException, SQLException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT authToken, username FROM authData WHERE authToken=?";
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, authToken);
                try (var rs = preparedStatement.executeQuery()) {
                    if (rs.next()) {
                        var username = rs.getString("username");
                        return new AuthData(authToken, username);
                    }
                }
            }
        }
        return null;
    }

    @Override
    public void deleteAuth(AuthData auth) throws DataAccessException, SQLException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "DELETE FROM authData WHERE authToken=?";
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, auth.authToken());
                var numRows = preparedStatement.executeUpdate();
                if (numRows == 0) {
                    throw new DataAccessException("cannot delete AuthData which doesn't exist");
                }
            }
        }
    }

    @Override
    public void clear() throws DataAccessException, SQLException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "TRUNCATE TABLE authData";
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        }
    }
}
