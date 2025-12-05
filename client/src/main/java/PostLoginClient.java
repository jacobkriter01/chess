import chess.ChessBoard;
import client.ServerFacade;
import requests.CreateGameRequest;
import requests.JoinGameRequest;
import responses.*;
import exceptions.ServiceException;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class PostLoginClient {
    private final ServerFacade server;
    private final Scanner scanner = new Scanner(System.in);
    private final State state = State.POSTLOGIN;
    private final Map<Integer, Integer> fakeID = new HashMap<>();

    public PostLoginClient(String url) {
        this.server = new ServerFacade(url);
    }

    public void run(String authToken){
        while (true) {
            System.out.print(state + ">>> ");
            String input = scanner.nextLine().trim().toLowerCase();

            String[] parts = input.split(" ");
            String command = parts[0].toLowerCase();

            switch (command){
                case "help" -> printHelp();
                case "list" -> listGames(authToken);
                case "create" -> createGame(authToken, parts);
                case "join" -> joinGame(authToken, parts);
                case "observe" -> observeGame(authToken, parts);
                case "logout" -> {
                    logout(authToken);
                    return;
                }
                default -> System.out.println("Invalid input. Type 'help'");
            }
        }
    }

    private void printHelp(){
        System.out.println("""
                    Commands:
                    - list
                    - create
                    - join
                    - observe
                    - logout
                    """);
    }

    private void listGames(String authToken){
        try{
            ListGamesResponse response = server.listGames(authToken);
            System.out.println("Games:");
            fakeID.clear();
            for (var i = 1; i <= response.games().size(); i++){
                var gameInfo = response.games().get(i-1);
                fakeID.put(i, gameInfo.gameID());
                System.out.println(i + "- " +  gameInfo.gameName() + "\tWHITE: " + gameInfo.whiteUsername() + "\tBLACK: " + gameInfo.blackUsername());
            }
        }catch (ServiceException e){
            System.out.println(e.getMessage());
        }
    }

    private void createGame(String authToken, String[] parts){
        if (parts.length != 2){
            System.out.println("Usage: create <game name>");
            return;
        }

        String gameName = parts[1];

        try{
            var request = new CreateGameRequest(gameName);
            server.createGame(authToken, request);
            System.out.println("Created game");
        } catch (ServiceException e){
            System.out.println(e.getMessage());
        }
    }

    private void joinGame(String authToken, String[] parts){
        if (parts.length != 3){
            System.out.println("Usage: join <game ID> [BLACK][WHITE]");
            return;
        }

        try{
            int fake = Integer.parseInt(parts[1]);
            var gameID = fakeID.get(fake);

            if (gameID == null){
                System.out.println("Game ID does not exist");
                return;
            }
            String color = parts[2].toLowerCase();

            JoinGameRequest request = new JoinGameRequest(authToken, gameID, color);
            server.joinGame(authToken, request);

            System.out.println("Joined game " + fake +" as "+ color);

            GameStateResponse gameState = server.getGameState(authToken, gameID);
            ChessBoard serverBoard = gameState.board();

            GamePlayClient gamePlayClient = new GamePlayClient(color, serverBoard, authToken, gameID, server.getServerUrl());
            gamePlayClient.run();
        }catch (NumberFormatException ex){
            System.out.println("Game ID must be a number");
        }catch (ServiceException ex){
            System.out.println("Whoops " + ex.getMessage());
        }
    }

    private void observeGame(String authToken, String[] parts){
        if (parts.length != 2){
            System.out.println("Usage: observe <game ID>");
            return;
        }
        try{
            int fake = Integer.parseInt(parts[1]);
            var gameID = fakeID.get(fake);

            if (gameID == null){
                System.out.println("Game ID does not exist");
                return;
            }

            var gameState = server.getGameState(authToken, gameID);

            System.out.println("Observing game: " + fake);

            GamePlayClient gamePlayClient = new GamePlayClient("OBSERVER", gameState.board(),  authToken, gameID, server.getServerUrl());
            gamePlayClient.run();
        }catch (NumberFormatException ex){
            System.out.println("Game ID must be a number");
        }catch (ServiceException ex){
            System.out.println(ex.getMessage());
        }
    }

    private void logout(String authToken){
        try{
            server.logout(authToken);
            System.out.println("Logged out");
        }catch (ServiceException e){
            System.out.println(e.getMessage());
        }
    }
}
