package responses;

import java.util.List;

public record ListGamesResponse(List<GameHeader> games) {

    public record GameHeader(int gameId, String gameName, String whiteUsername, String blackUsername) {}
}
