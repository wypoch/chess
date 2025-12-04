package service;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import dataaccess.AuthDataAccess;
import dataaccess.DataAccessException;
import dataaccess.GameDataAccess;
import model.GameData;

import service.creategame.CreateGameRequest;
import service.creategame.CreateGameResult;
import service.exception.AlreadyTakenException;
import service.exception.BadRequestException;
import service.exception.MissingGameException;
import service.joingame.JoinGameRequest;
import service.exception.UnauthorizedException;
import service.listgames.ListGamesRequest;
import service.listgames.ListGamesResult;

import java.sql.SQLException;

public class GameService {

    private Integer currGameID;
    private final AuthDataAccess authDataAccess;
    private final GameDataAccess gameDataAccess;

    public GameService(AuthDataAccess authDataAccess, GameDataAccess gameDataAccess) {
        currGameID = 1;
        this.authDataAccess = authDataAccess;
        this.gameDataAccess = gameDataAccess;
    }

    public CreateGameResult createGame(CreateGameRequest createGameRequest) throws UnauthorizedException, DataAccessException, SQLException {
        String authToken = createGameRequest.authToken();

        // try to find the authData associated with the authToken
        var responseData = authDataAccess.getAuth(authToken);
        if (responseData == null) {
            throw new UnauthorizedException("unauthorized");
        } else {
            // generate unique game ID
            Integer gameID = currGameID;
            currGameID += 1;

            // add the game to the database
            String gameName = createGameRequest.gameName();
            var gameData = new GameData(gameID, null, null, gameName, new ChessGame());
            gameDataAccess.createGame(gameData);

            return new CreateGameResult(gameID);
        }
    }

    public void joinGame(JoinGameRequest joinGameRequest) throws UnauthorizedException, MissingGameException,
            AlreadyTakenException, BadRequestException, DataAccessException, SQLException {

        String authToken = joinGameRequest.authToken();

        // try to find the authData associated with the authToken
        var responseData = authDataAccess.getAuth(authToken);
        if (responseData == null) {
            throw new UnauthorizedException("unauthorized");
        } else {
            // get gameData for the game
            var gameData = gameDataAccess.getGame(joinGameRequest.gameID());
            if (gameData == null) {
                throw new MissingGameException("game does not exist");
            }

            GameData newGameData;
            var desiredUsername = responseData.username();

            // ensure the user slot we want to fill isn't already occupied;
            // assuming it isn't, we can fill in the new gameData
            var desiredColor = joinGameRequest.playerColor();
            if (desiredColor.equals("WHITE")) {
                if (gameData.whiteUsername() != null) {
                    throw new AlreadyTakenException("already taken");
                } else {
                    newGameData = new GameData(gameData.gameID(),
                            desiredUsername, gameData.blackUsername(),
                            gameData.gameName(), gameData.game());
                }
            } else if (desiredColor.equals("BLACK")) {
                if (gameData.blackUsername() != null) {
                    throw new AlreadyTakenException("already taken");
                } else {
                    newGameData = new GameData(gameData.gameID(),
                            gameData.whiteUsername(), desiredUsername,
                            gameData.gameName(), gameData.game());
                }
            } else {
                throw new BadRequestException("bad request");
            }

            // we can now safely update the gameData
            gameDataAccess.updateGame(newGameData);
        }
    }

    public ListGamesResult listGames(ListGamesRequest listGamesRequest) throws UnauthorizedException, DataAccessException, SQLException {
        String authToken = listGamesRequest.authToken();

        // try to find the authData associated with the authToken
        var responseData = authDataAccess.getAuth(authToken);
        if (responseData == null) {
            throw new UnauthorizedException("unauthorized");
        } else {
            // Get a list of all the GameData
            var gameDataList = gameDataAccess.listGames();
            return new ListGamesResult(gameDataList);
        }
    }

    public GameData getGame(int gameID) throws MissingGameException, DataAccessException, SQLException {
        var gameData = gameDataAccess.getGame(gameID);
        if (gameData == null) {
            throw new MissingGameException("game does not exist");
        }
        return gameData;
    }

    public void removeUserFromGame(int gameID, ChessGame.TeamColor playerColor) throws DataAccessException, SQLException {
        GameData gameData = gameDataAccess.getGame(gameID);
        GameData newGameData;

        // Remove the specified user from the gameData
        if (playerColor == ChessGame.TeamColor.WHITE) {
            newGameData = new GameData(gameID, null, gameData.blackUsername(), gameData.gameName(), gameData.game());
        } else {
            newGameData = new GameData(gameID, gameData.whiteUsername(), null, gameData.gameName(), gameData.game());
        }

        // Update the gameData
        gameDataAccess.updateGame(newGameData);
    }

    public void makeMove(int gameID, ChessMove move) throws Exception {
        GameData gameData = gameDataAccess.getGame(gameID);

        GameData newGameData;
        ChessGame currGame = gameData.game();
        ChessBoard currBoard = currGame.getBoard();

        var moveStart = move.getStartPosition();

        // Go through disqualifying cases
        var movePiece = currBoard.getPiece(moveStart);
        if (movePiece == null) {
            throw new Exception("no piece provided for move");
        }

        var pieceColor = movePiece.getTeamColor();
        if (pieceColor != currGame.getTeamTurn()) {
            throw new Exception("invalid move");
        }

        // Check if the move is valid
        var validMoves = currGame.validMoves(moveStart);
        if (validMoves.contains(move)) {
            currGame.makeMove(move);
            newGameData = new GameData(gameID, gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), currGame);
            gameDataAccess.updateGame(newGameData);

        } else {
            throw new Exception("invalid move");
        }
    }
}
