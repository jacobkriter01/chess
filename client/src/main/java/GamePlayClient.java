import chess.*;
import static ui.EscapeSequences.*;

public class GamePlayClient {
    private final ChessBoard board;
    private final String playerColor;

    public GamePlayClient(String playerColor, ChessBoard serverBoard) {
        this.playerColor = playerColor.toUpperCase();
        this.board = serverBoard;
    }

    public void run(){
        ChessGame.TeamColor orientation;
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

                boolean lightSquare = (row + col) % 2 == 0;
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
