package responses;

import java.util.List;
import java.util.Map;

public record ListGamesResponse(List<GameHeader> games, String message) {
    public record GameHeader(int gameID, String gameName, String whiteUsername, String blackUsername) {
    }
}
