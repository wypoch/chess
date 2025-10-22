package service.listgames;

import model.GameData;

import java.util.HashSet;

public record ListGamesResult(HashSet<GameData> gameDataList) {
}
