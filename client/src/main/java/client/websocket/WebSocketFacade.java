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
    private boolean gameIsOver = false;

    public interface GameMessageHandler {
        void onLoadGame(ServerMessage msg);
        void onNotification(String message);
        void onError(String error);

        void onLoadGame(ChessGame updatedGame);
    }

    public WebSocketFacade(String serverUrl, GameMessageHandler handler) {
        this.handler = handler;

        if (serverUrl.startsWith("http://")) {
            serverUrl = serverUrl.replace("http://", "ws://");
        }else if (serverUrl.startsWith("https://")) {
            serverUrl = serverUrl.replace("https://", "wss://");
        }

        this.webSocket = HttpClient.newHttpClient()
                .newWebSocketBuilder()
                .buildAsync(URI.create(serverUrl + "/ws"), this)
                .join();
    }

    public void send(UserGameCommand command) {
        if (gameIsOver && command.getCommandType().equals("RESIGN")){
            handler.onError("The game is over.");
            return;
        }

        String json = gson.toJson(command);
        webSocket.sendText(json, true);
    }

    @Override
    public CompletionStage<?> onText(WebSocket ws, CharSequence data, boolean last){
        String json = data.toString();
        ServerMessage msg = gson.fromJson(json, ServerMessage.class);

        switch (msg.getServerMessageType()){
            case LOAD_GAME -> handler.onLoadGame(msg);
            case NOTIFICATION ->  {
                handler.onNotification(msg.getMessage());

                if (msg.getMessage().toLowerCase().contains("game over")
                || msg.getMessage().toLowerCase().contains("won the game!")
                || msg.getMessage().toLowerCase().contains("resigned")) {
                    gameIsOver = true;
                }
            }
            case ERROR -> handler.onError(msg.getErrorMessage());
        }

        return WebSocket.Listener.super.onText(ws, data, last);
    }
}
