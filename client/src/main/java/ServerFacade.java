import com.google.gson.Gson;
import exceptions.ServiceException;
import datamodel.UserData;
import requests.CreateGameRequest;
import requests.JoinGameRequest;
import responses.CreateGameResponse;
import responses.ListGamesResponse;
import responses.LoginResponse;
import responses.RegisterResponse;

import java.net.URI;
import java.net.http.*;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Map;

public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;

    public ServerFacade(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public RegisterResponse register(UserData user) throws ServiceException {
        var request = buildRequest("POST", "/user", user, null);
        var response = sendRequest(request);
        return handleResponse(response, RegisterResponse.class);
    }

    public LoginResponse login(UserData user) throws ServiceException {
        var request = buildRequest("POST", "/session", user, null);
        var response = sendRequest(request);
        return handleResponse(response, LoginResponse.class);
    }

    public void logout(String authToken) throws ServiceException {
        var request = buildRequest("DELETE", "/session", authToken, null);
        var response = sendRequest(request);
        handleResponse(response, null);
    }

    public CreateGameResponse createGame(String authToken, CreateGameRequest gameRequest) throws ServiceException {
        var request = buildRequest("POST", "/game", gameRequest, authToken);
        var response = sendRequest(request);
        return handleResponse(response, CreateGameResponse.class);
    }

    public void joinGame(String authToken, JoinGameRequest gameRequest) throws ServiceException {
        var request = buildRequest("PUT", "/game", gameRequest, authToken);
        var response = sendRequest(request);
        handleResponse(response, null);
    }

    public ListGamesResponse listGames(String authToken) throws ServiceException {
        var request = buildRequest("GET", "/game", null, authToken);
        var response = sendRequest(request);
        return handleResponse(response, ListGamesResponse.class);
    }

    public void clearDb() throws ServiceException {
        var request = buildRequest("DELETE", "/game", null, null);
        var response = sendRequest(request);
        handleResponse(response, null);
    }

    private HttpRequest buildRequest(String method, String path, Object body, String authToken) {
        var builder = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, makeRequestBody(body));

        if (authToken != null) {
            builder.header("Authorization", authToken);
        }

        if (body != null) {
            builder.header("Content-Type", "application/json");
        }
        return builder.build();
    }

    private HttpRequest.BodyPublisher makeRequestBody(Object request) {
        if (request == null){
            return BodyPublishers.noBody();
        }
        return BodyPublishers.ofString((new Gson()).toJson(request));
    }

    private HttpResponse<String> sendRequest(HttpRequest request) throws ServiceException {
        try{
            return client.send(request, BodyHandlers.ofString());
        } catch (Exception ex) {
            throw new ServiceException(500, ex.getMessage());
        }
    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> type) throws ServiceException {
        int status = response.statusCode();

        if (status / 100 != 2){
            var body = response.body();
            throw new ServiceException(status, extractMessage(body));
        }

        if (type == null) {
            return null;
        }

        return new Gson().fromJson(response.body(), type);
    }

    private String extractMessage(String jsonError) {
        try{
            Map m = new Gson().fromJson(jsonError, Map.class);
            return(String) m.get("message");
        } catch (Exception ex){
            return "unknown error";
        }
    }
}
