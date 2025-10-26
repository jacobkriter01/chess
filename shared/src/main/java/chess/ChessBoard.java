package chess;


import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    ChessPiece[][] board = new ChessPiece[8][8];

    public ChessBoard() {
        
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        board[position.getRow()-1][position.getColumn()-1] = piece;
    }

    public void movePiece(ChessMove move) {
        board[move.getEndPosition().getRow()-1][move.getEndPosition().getColumn()-1] = getPiece(move.getStartPosition());
        board[move.getStartPosition().getRow()-1][move.getStartPosition().getColumn()-1] =  null;

        if(move.getPromotionPiece() != null) {
            ChessGame.TeamColor color = getPiece(move.getEndPosition()).getTeamColor();
            board[move.getEndPosition().getRow()-1][move.getEndPosition().getColumn()-1] = new ChessPiece(color, move.getPromotionPiece());
        }
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return board[position.getRow()-1][position.getColumn()-1];
    }

    public ChessBoard cloneBoard(){
        ChessBoard newBoard = new ChessBoard();

        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition position = new ChessPosition(i, j);
                ChessPiece piece = getPiece(position);

                if(piece != null){
                    newBoard.addPiece(position, piece);
                }
            }
        }
        return newBoard;
    }

    public ChessPosition findKing(ChessGame.TeamColor teamColor) {
        ChessPosition kingLocation = new ChessPosition(0, 0);
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                if(getPiece(new ChessPosition(i, j)) != null){
                    ChessPiece piece = getPiece(new ChessPosition(i, j));
                    if(piece != null && piece.getTeamColor() == teamColor && piece.getPieceType() == ChessPiece.PieceType.KING){
                       kingLocation = new ChessPosition(i, j);
                    }
                }
            }
        }
        return kingLocation;
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        board = new ChessPiece[8][8];

        board[1][0] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        board[1][1] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        board[1][2] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        board[1][3] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        board[1][4] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        board[1][5] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        board[1][6] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        board[1][7] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);

        board[0][0] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
        board[0][7] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);

        board[0][1] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
        board[0][6] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);

        board[0][2] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
        board[0][5] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);

        board[0][3] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN);
        board[0][4] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING);


        board[6][0] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
        board[6][1] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
        board[6][2] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
        board[6][3] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
        board[6][4] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
        board[6][5] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
        board[6][6] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
        board[6][7] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);

        board[7][0] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);
        board[7][7] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);

        board[7][1] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
        board[7][6] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);

        board[7][2] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
        board[7][5] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);

        board[7][3] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN);
        board[7][4] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING);

    }

    @Override
    public String toString() {
        StringBuilder boardBuilder = new StringBuilder();

        for(int row = 8; row >= 1; row--) {
            for (int col = 8; col >= 1; col--) {
                ChessPiece piece = getPiece(new ChessPosition(row, col));
                boardBuilder.append("|");

                if(piece == null){
                    boardBuilder.append(" ");
                }else{
                    char symbol;
                    switch(piece.getPieceType()){
                        case KING: symbol = 'K'; break;
                        case QUEEN: symbol = 'Q'; break;
                        case BISHOP: symbol = 'B'; break;
                        case ROOK: symbol = 'R'; break;
                        case KNIGHT: symbol = 'N'; break;
                        case PAWN: symbol = 'P'; break;
                        default: symbol = '?'; break;
                    }
                    if(piece.getTeamColor() == ChessGame.TeamColor.BLACK){
                        symbol = Character.toLowerCase(symbol);
                    }
                    boardBuilder.append(symbol);
                }
            }
            boardBuilder.append("\n");
        }

        return boardBuilder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChessBoard)){
            return false;
        }

        ChessBoard other = (ChessBoard) o;

        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece thisPiece = this.getPiece(pos);
                ChessPiece otherPiece = other.getPiece(pos);

                if (thisPiece == null && otherPiece == null){
                    continue;
                }
                if (thisPiece == null || otherPiece == null){
                    return false;
                }
                if (!thisPiece.equals(otherPiece)) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = 17;
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = this.getPiece(pos);
                result = 31 * result + (piece == null ? 0 : piece.hashCode());
            }
        }
        return result;
    }
}
