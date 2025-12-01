import chess.*;
import static ui.EscapeSequences.*;

import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;
import client.websocket.WebSocketFacade;

import java.util.Scanner;

public class GamePlayClient implements WebSocketFacade.GameMessageHandler{
    private final ChessBoard board;
    private final String playerColor;
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
    }

    public void run(){
        ws.send(new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID));

        ChessGame.TeamColor orientation;
        if(playerColor.equals("WHITE")){
            orientation = ChessGame.TeamColor.WHITE;
        }else if(playerColor.equals("BLACK")){
            orientation = ChessGame.TeamColor.BLACK;
        }else{
            orientation = ChessGame.TeamColor.WHITE;
        }
        drawBoard(board, orientation);

        gameLoop();
    }

    public void gameLoop(){
        while(true){
            System.out.print("\nCommands: help, redraw, highlight, move, resign, leave");
            System.out.print(">>> ");
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

}
