package responses;

import chess.ChessBoard;
import chess.ChessGame;

import java.io.Serializable;

public record GameStateResponse(int gameID, String whiteUsername, String blackUsername, ChessBoard board, ChessGame.TeamColor turn, boolean gameOver) implements Serializable {
}
