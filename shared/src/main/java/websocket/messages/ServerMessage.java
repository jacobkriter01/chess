package websocket.messages;

import chess.ChessGame;

import java.util.Objects;

/**
 * Represents a Message the server can send through a WebSocket
 * <p>
 * Note: You can add to this class, but you should not alter the existing
 * methods.
 */
public class ServerMessage {
    ServerMessageType serverMessageType;

    public enum ServerMessageType {
        LOAD_GAME,
        ERROR,
        NOTIFICATION
    }

    public ServerMessage(ServerMessageType type) {
        this.serverMessageType = type;
    }

    public ServerMessageType getServerMessageType() {
        return this.serverMessageType;
    }

    private ChessGame game;
    private String errorMessage;
    private String message;

    public static ServerMessage loadGame(ChessGame game) {
        var msg = new ServerMessage(ServerMessageType.LOAD_GAME);
        msg.game = game;
        return msg;
    }

    public static ServerMessage error(String errorMessage) {
        var msg = new ServerMessage(ServerMessageType.ERROR);
        msg.errorMessage = errorMessage;
        return msg;
    }

    public static ServerMessage notification(String message) {
        var msg = new ServerMessage(ServerMessageType.NOTIFICATION);
        msg.message = message;
        return msg;
    }

    public ChessGame getGame() {
        return game;
    }
    public String getErrorMessage() {
        return errorMessage;
    }
    public String getMessage() {
        return message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ServerMessage that)) {
            return false;
        }
        return getServerMessageType() == that.getServerMessageType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getServerMessageType());
    }
}
