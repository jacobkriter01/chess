package client.websocket;

import chess.ChessGame;
import com.google.gson.Gson;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.util.concurrent.CompletionStage;


public class WebSocketFacade implements WebSocket.Listener {
    private final Gson gson = new Gson();
    private WebSocket webSocket;
    private final GameMessageHandler handler;

    public interface GameMessageHandler {
        void onLoadGame(ServerMessage msg);
        void onNotification(String message);
        void onError(String error);

        void onLoadGame(ChessGame updatedGame);
    }

    public WebSocketFacade(String serverUrl, GameMessageHandler handler) {
        this.handler = handler;
        this.webSocket = HttpClient.newHttpClient()
                .newWebSocketBuilder()
                .buildAsync(URI.create(serverUrl + "/ws"), this)
                .join();
    }

    public void send(UserGameCommand command) {
        String json = gson.toJson(command);
        webSocket.sendText(json, true);
    }

    @Override
    public CompletionStage<?> onText(WebSocket ws, CharSequence data, boolean last){
        String json = data.toString();
        ServerMessage msg = gson.fromJson(json, ServerMessage.class);

        switch (msg.getServerMessageType()){
            case LOAD_GAME -> handler.onLoadGame(msg);
            case NOTIFICATION ->  handler.onNotification(msg.getMessage());
            case ERROR -> handler.onError(msg.getMessage());
        }

        return WebSocket.Listener.super.onText(ws, data, last);
    }
}
