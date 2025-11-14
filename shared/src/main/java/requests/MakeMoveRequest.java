package requests;

import chess.*;

public record MakeMoveRequest(String authToken, int gameId, ChessMove move) {
}
