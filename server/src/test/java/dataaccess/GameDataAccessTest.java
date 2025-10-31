package dataaccess;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import model.GameData;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.HashSet;

public class GameDataAccessTest {

    private SQLGameDataAccess gameDataAccess;

    @BeforeEach
    public void clearDatabase() {
        // configure and clear the database before each test
        try {
            gameDataAccess = new SQLGameDataAccess();
            gameDataAccess.clear();
        }
        catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void createGameNormal() {
        try {
            // create game with null ChessGame
            GameData gameData1 = new GameData(1, "white", "black", "test", null);
            gameDataAccess.createGame(gameData1);
            var gameData2 = gameDataAccess.getGame(gameData1.gameID());
            Assertions.assertEquals(gameData2, gameData1);

            // create game with ChessGame representing the default board
            gameData1 = new GameData(2, "white", "black", "test", new ChessGame());
            gameDataAccess.createGame(gameData1);
            gameData2 = gameDataAccess.getGame(gameData1.gameID());
            Assertions.assertEquals(gameData2, gameData1);

        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void createGameInvalid() {
        try {
            // try to create two games with the same gameID
            GameData gameData1 = new GameData(1, "white", "black", "test", null);
            gameDataAccess.createGame(gameData1);
            GameData gameData2 = new GameData(1, "white", "black", "test", new ChessGame());
            gameDataAccess.createGame(gameData2);
            Assertions.fail();

        } catch (DataAccessException e) {
            Assertions.fail(e.getMessage());
        } catch (SQLException e) {
        }
    }

    @Test
    public void getGameNormal() {
        try {
            // get a game created with ID = 2
            GameData gameData1 = new GameData(2, "white", "black", "test", null);
            gameDataAccess.createGame(gameData1);
            var gameData2 = gameDataAccess.getGame(gameData1.gameID());
            Assertions.assertEquals(gameData2, gameData1);

        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void getGameInvalid() {
        try {
            // try to get a game with ID = 2 that doesn't exist
            GameData gameData1 = new GameData(3, "white", "black", "test", new ChessGame());
            gameDataAccess.createGame(gameData1);
            GameData gameData2 = gameDataAccess.getGame(2);
            Assertions.assertNull(gameData2);

        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void updateGameNormal() {
        try {
            // create game with null ChessGame
            GameData gameData1 = new GameData(1, null, null, "test", null);
            gameDataAccess.createGame(gameData1);
            var gameData2 = gameDataAccess.getGame(gameData1.gameID());
            Assertions.assertEquals(gameData2, gameData1);

            // update game with usernames and ChessGame representing the default board
            ChessGame currGame = new ChessGame();
            gameData1 = new GameData(1, null, "black", "test", currGame);
            gameDataAccess.updateGame(gameData1);
            gameData2 = gameDataAccess.getGame(gameData1.gameID());
            Assertions.assertEquals(gameData2, gameData1);

            // update the game with a move
            currGame.makeMove(new ChessMove(new ChessPosition(2, 1), new ChessPosition(3, 1), null));
            gameData1 = new GameData(1, "white", "black", "test", currGame);
            gameDataAccess.updateGame(gameData1);
            gameData2 = gameDataAccess.getGame(gameData1.gameID());
            Assertions.assertEquals(gameData2, gameData1);

        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void updateGameInvalid() {
        try {
            // create game with null ChessGame
            GameData gameData1 = new GameData(1, null, null, "test", null);
            gameDataAccess.createGame(gameData1);
            var gameData2 = gameDataAccess.getGame(gameData1.gameID());
            Assertions.assertEquals(gameData2, gameData1);

            // try to update a non-existent game (will give DataAccessException)
            ChessGame currGame = new ChessGame();
            gameData1 = new GameData(2, null, "black", "test", currGame);
            gameDataAccess.updateGame(gameData1);
            Assertions.fail();

        } catch (SQLException e) {
            Assertions.fail(e.getMessage());
        } catch (DataAccessException e) {
        }
    }

    @Test
    public void listGamesNormal() {
        try {
            // Create multiple games
            GameData gameData1 = new GameData(1, null, null, "test", null);
            gameDataAccess.createGame(gameData1);
            GameData gameData2 = new GameData(2, "testWhite2", null, "test", null);
            gameDataAccess.createGame(gameData2);
            GameData gameData3 = new GameData(3, "testWhite3", "testBlack3", "test", new ChessGame());
            gameDataAccess.createGame(gameData3);

            // Ensure that the games are all in the list as expected
            var gamesList = gameDataAccess.listGames();
            for (var game : new GameData[]{gameData1, gameData2, gameData3}) {
                var checkGame = new GameData(game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName(), null);
                if (!gamesList.contains(checkGame)) {
                    Assertions.fail();
                }
            }

        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void listGamesInvalid() {
        try {
            // List games without creating any (check for empty HashSet)
            var gamesList = gameDataAccess.listGames();
            Assertions.assertEquals(new HashSet<GameData>(), gamesList);

            // Create a game with null entries and list it
            GameData gameData1 = new GameData(0, null, null, null, null);
            gameDataAccess.createGame(gameData1);
            gamesList = gameDataAccess.listGames();
            var compGamesList = new HashSet<GameData>();
            compGamesList.add(gameData1);
            Assertions.assertEquals(compGamesList, gamesList);

        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }

    }

}
