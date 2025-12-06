import chess.*;
import static ui.EscapeSequences.*;

import datamodel.GameData;
import websocket.commands.UserGameCommand;
import client.websocket.WebSocketFacade;
import websocket.messages.ServerMessage;

import java.util.Collection;
import java.util.Scanner;

public class GamePlayClient implements WebSocketFacade.GameMessageHandler{
    private ChessBoard board;
    private ChessGame currentGame;
    private final String playerColor;
    private ChessGame.TeamColor orientation;
    private final String authToken;
    private final int gameID;
    private final WebSocketFacade ws;

    private final Scanner scanner = new Scanner(System.in);

    public GamePlayClient(String playerColor, ChessBoard serverBoard, String authToken, int gameID, String serverUrl) {
        this.playerColor = playerColor.toUpperCase();
        this.board = serverBoard;
        this.authToken = authToken;
        this.gameID = gameID;
        this.ws = new WebSocketFacade(serverUrl, this);
        this.currentGame = new ChessGame();
        this.currentGame.setBoard(this.board);
    }

    public void run(){
        ws.send(new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID));


        while(true){
            printHelp();
            String input = scanner.nextLine().trim().toLowerCase();

            switch(input){
                case "help" -> printHelp();
                case "redraw" -> redrawBoard();
                case "highlight" -> highlightMoves();
                case "move" -> makeMove();
                case "resign" -> resign();
                case "leave" -> {
                    leave();
                    return;
                }
                default -> System.out.println("Invalid input");
            }
        }
    }

    private void printHelp(){
        System.out.println("""
                Commands:
                - help
                - redraw
                - highlight
                - move
                - resign
                - leave
                """);
    }

    private void redrawBoard(){
        drawBoard(board, orientation);
    }

    private void highlightMoves(){
        System.out.println("Enter a piece to highlight (e.g, a3");
        String input = scanner.nextLine().trim().toLowerCase();
        ChessPosition pos = parsePosition(input);
        if(pos == null){
            System.out.println("Invalid coordinate");
            return;
        }

        ChessPiece piece = board.getPiece(pos);
        if(piece == null){
            System.out.println("No piece at that position");
            return;
        }

        if(currentGame == null) {
            currentGame = new ChessGame();
        }
        currentGame.setBoard(board);


        Collection<ChessMove> legal = currentGame.validMoves(pos);
        if(legal.isEmpty() || legal == null) {
            System.out.println("No legal moves for that piece");
            return;
        }

        boolean[][] highlight = new boolean[8][8];
        highlight[pos.getRow()-1][pos.getColumn()-1] = true;
        for (var move : legal) {
            var end = move.getEndPosition();
            highlight[end.getRow()-1][end.getColumn()-1] = true;
        }

        drawBoardHighlights(board, orientation, highlight);
    }

    private void makeMove(){
        System.out.print("Enter move (e.g., a2 a3): ");
        String [] parts = scanner.nextLine().trim().split("\\s+");
        if (parts.length != 2){
            System.out.println("Invalid input");
            return;
        }

        var start = parsePosition(parts[0]);
        var end = parsePosition(parts[1]);

        if(start == null || end == null){
            System.out.println("Invalid coordinates");
            return;
        }

        ChessMove move = new ChessMove(start, end, null);
        UserGameCommand cmd = UserGameCommand.makeMove(authToken, gameID, move);
        ws.send(cmd);
    }

    private ChessPosition parsePosition(String position){
        position = position.trim().toLowerCase();
        if(position.length() != 2){
            return null;
        }

        char file = position.charAt(0);
        char rank = position.charAt(1);

        if(file < 'a' || file > 'h'){
            return null;
        }
        if(rank < '1' || rank > '8'){
            return null;
        }

        int col = file - 'a' + 1;
        int row = rank - '1' + 1;

        return new ChessPosition(row, col);
    }

    private void resign(){
        System.out.print("Confirm resign? (y/n): ");
        if (scanner.nextLine().trim().equalsIgnoreCase("y")){
            ws.send(new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID));
            System.out.println("You resigned.");
        }
    }

    private void leave(){
        ws.send(new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID));
        System.out.println("You left.");
    }

    @Override
    public void onLoadGame(ServerMessage msg) {
        if (msg == null){
            return;
        }
        ChessGame game = msg.getGame();
        if (game != null){
            onLoadGame(game);
        } else {
            System.out.println("Game is not stored");
        }
    }

    @Override
    public void onNotification(String message) {
        System.out.println("\n[NOTIFICATION] " + message);
    }

    @Override
    public void onError(String error){
        System.out.println("\n[ERROR] " + error);
    }

    @Override
    public void onLoadGame(ChessGame updatedGame){
        if (updatedGame == null){
            return;
        }
        this.currentGame = updatedGame;
        this.board = updatedGame.getBoard();

        if(playerColor.equals("WHITE")){
            orientation = ChessGame.TeamColor.WHITE;
        }else if(playerColor.equals("BLACK")){
            orientation = ChessGame.TeamColor.BLACK;
        }else{
            orientation = ChessGame.TeamColor.WHITE;
        }


        drawBoard(board, orientation);
    }

    private void drawBoard(ChessBoard board, ChessGame.TeamColor orientation){
        String header;
        if(ChessGame.TeamColor.WHITE == orientation){
            header = "   a  b  c  d  e  f  g  h";
        } else if (ChessGame.TeamColor.BLACK == orientation) {
            header = "   h  g  f  e  d  c  b  a";
        }else{
            header = "   a  b  c  d  e  f  g  h"; //Observer
        }
        System.out.println(header);

        for (int r = 0; r < 8; r++){
            int row = orientation == ChessGame.TeamColor.WHITE ? 7-r : r;
            System.out.print((row + 1) + " ");

            for (int c = 0; c < 8; c++){
                int col = orientation == ChessGame.TeamColor.WHITE ? c : 7-c;
                ChessPiece piece = board.getPiece(new ChessPosition(row+1, col+1));

                boolean lightSquare = (row + col) % 2 == 1;
                String bg = lightSquare ? SET_BG_COLOR_LIGHT_GREY : SET_BG_COLOR_DARK_GREY;

                String symbol;
                if (piece == null) {
                    symbol = EMPTY;
                }else{
                    switch(piece.getPieceType()){
                        case KING -> symbol = piece.getTeamColor() == ChessGame.TeamColor.WHITE ?  BLACK_KING : WHITE_KING;
                        case QUEEN -> symbol = piece.getTeamColor() == ChessGame.TeamColor.WHITE ?  BLACK_QUEEN : WHITE_QUEEN;
                        case BISHOP -> symbol = piece.getTeamColor() == ChessGame.TeamColor.WHITE ?  BLACK_BISHOP : WHITE_BISHOP;
                        case ROOK -> symbol = piece.getTeamColor() == ChessGame.TeamColor.WHITE ?  BLACK_ROOK : WHITE_ROOK;
                        case KNIGHT -> symbol = piece.getTeamColor() == ChessGame.TeamColor.WHITE ?  BLACK_KNIGHT : WHITE_KNIGHT;
                        case PAWN -> symbol = piece.getTeamColor() == ChessGame.TeamColor.WHITE ?  BLACK_PAWN : WHITE_PAWN;
                        default -> symbol = "?";
                    }
                }
                System.out.print(bg + symbol + RESET_BG_COLOR);
            }
            System.out.print(" " + (row + 1));
            System.out.println();
        }
        System.out.println(header);
    }

    private void drawBoardHighlights(ChessBoard board, ChessGame.TeamColor orientation, boolean[][] highlights){
        String header;
        if(ChessGame.TeamColor.WHITE == orientation){
            header = "   a  b  c  d  e  f  g  h";
        } else {
            header =  "   h  g  f  e  d  c  b  a";
        }
        System.out.println(header);

        for(int r = 0; r < 8; r++) {
            int row = orientation == ChessGame.TeamColor.WHITE ? 7 - r : r;
            System.out.print((row + 1) + " ");
            for (int c = 0; c < 8; c++) {
                int col = orientation == ChessGame.TeamColor.WHITE ? c : 7 - c;
                ChessPiece piece = board.getPiece(new ChessPosition(row + 1, col + 1));
                boolean isHighlighted = highlights[row][col];
                String back;
                if (isHighlighted) {
                    back = SET_BG_COLOR_DARK_GREEN;
                } else {
                    boolean lightSquare = (row + col) % 2 == 1;
                    back = lightSquare ? SET_BG_COLOR_LIGHT_GREY : SET_BG_COLOR_DARK_GREY;
                }

                String symbol;
                if (piece == null) {
                    symbol = EMPTY;
                } else {
                    switch (piece.getPieceType()) {
                        case KING ->
                                symbol = piece.getTeamColor() == ChessGame.TeamColor.WHITE ? BLACK_KING : WHITE_KING;
                        case QUEEN ->
                                symbol = piece.getTeamColor() == ChessGame.TeamColor.WHITE ? BLACK_QUEEN : WHITE_QUEEN;
                        case BISHOP ->
                                symbol = piece.getTeamColor() == ChessGame.TeamColor.WHITE ? BLACK_BISHOP : WHITE_BISHOP;
                        case ROOK ->
                                symbol = piece.getTeamColor() == ChessGame.TeamColor.WHITE ? BLACK_ROOK : WHITE_ROOK;
                        case KNIGHT ->
                                symbol = piece.getTeamColor() == ChessGame.TeamColor.WHITE ? BLACK_KNIGHT : WHITE_KNIGHT;
                        case PAWN ->
                                symbol = piece.getTeamColor() == ChessGame.TeamColor.WHITE ? BLACK_PAWN : WHITE_PAWN;
                        default -> symbol = "?";
                    }
                }
                System.out.print(back + symbol + RESET_BG_COLOR);
            }
            System.out.print(" " + (row + 1));
            System.out.println();
        }
        System.out.println(header);
    }

}
