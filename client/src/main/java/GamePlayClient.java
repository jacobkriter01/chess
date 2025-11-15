import chess.*;

public class GamePlayClient {
    private final ChessBoard board;
    private final String playerColor;

    public GamePlayClient(String playerColor) {
        this.playerColor = playerColor.toUpperCase();
        this.board = new ChessBoard();
        this.board.resetBoard();
    }

    public void run(){
        ChessGame.TeamColor orientation;
        if (this.playerColor.equals("WHITE")) {
            orientation = ChessGame.TeamColor.WHITE;
        }else{
            orientation = ChessGame.TeamColor.BLACK;
        }
        System.out.println(board.toString(orientation));

    }

    private void drawBoardFlipped(){
        for (int row = 8; row >= 1; row --){
            for (int col = 8; col >= 1; col --){
                char pieceSymbol = getPieceSymbol(row, col);
                System.out.print("|" + pieceSymbol);
            }
            System.out.println("|");
        }
    }

    private char getPieceSymbol(int row, int col){
        var piece = board.getPiece(new ChessPosition(row, col));
        if (piece == null){
            return ' ';
        }
        char symbol;
        switch(piece.getPieceType()){
            case KING -> symbol = 'K';
            case QUEEN -> symbol = 'Q';
            case ROOK -> symbol = 'R';
            case PAWN -> symbol = 'P';
            case KNIGHT -> symbol = 'N';
            case BISHOP -> symbol = 'B';
            default -> symbol = ' ';
        }
        return piece.getTeamColor() == ChessGame.TeamColor.BLACK ? Character.toLowerCase(symbol) : symbol;
    }
}
