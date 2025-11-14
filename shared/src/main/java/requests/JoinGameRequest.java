package requests;

public record JoinGameRequest(String authToken, int gameId, String playerColor) {
}
