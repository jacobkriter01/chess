package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     */

    private void path(ChessBoard board, ChessPosition origin, ChessPosition myPosition,int rowDirection, int colDirection, List<ChessMove> moves, PieceType piece){
        int row = myPosition.getRow() + rowDirection;
        int col = myPosition.getColumn() + colDirection;

        if (row > 8 || col > 8 || row < 1 || col < 1) {
            return;
        }

        ChessPosition endPosition = new ChessPosition(row,col);
        moves.add(new ChessMove(origin, endPosition, null));

        if (piece == PieceType.QUEEN || piece == PieceType.BISHOP || piece == PieceType.ROOK) {
            path(board, origin, endPosition, rowDirection, colDirection, moves, piece);
        }
    }


    /**
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece piece = board.getPiece(myPosition);
        List<ChessMove> moves = new ArrayList<>();
        if (getPieceType() == PieceType.BISHOP) {
            path(board, myPosition, myPosition,1,1, moves, getPieceType()); //up and right
            path(board, myPosition, myPosition,-1,1, moves, getPieceType()); //down and right
            path(board, myPosition, myPosition,1,-1, moves, getPieceType()); //up and left
            path(board, myPosition, myPosition,-1,-1, moves, getPieceType()); //down and left

            return moves;
        } else if (getPieceType() == PieceType.ROOK) {
            path(board, myPosition, myPosition,0,1, moves, getPieceType()); //right
            path(board, myPosition, myPosition,-1,0, moves, getPieceType()); //down
            path(board, myPosition, myPosition,1,0, moves, getPieceType()); //up
            path(board, myPosition, myPosition,0,-1, moves, getPieceType()); //left

            return moves;
        }else if (getPieceType() == PieceType.QUEEN || getPieceType() == PieceType.KING) {
            path(board, myPosition, myPosition,1,1, moves, getPieceType()); //up and right
            path(board, myPosition, myPosition,-1,1, moves, getPieceType()); //down and right
            path(board, myPosition, myPosition,1,-1, moves, getPieceType()); //up and left
            path(board, myPosition, myPosition,-1,-1, moves, getPieceType()); // down and left

            path(board, myPosition, myPosition,0,1, moves, getPieceType()); //right
            path(board, myPosition, myPosition,-1,0, moves, getPieceType()); //down
            path(board, myPosition, myPosition,1,0, moves, getPieceType()); //up
            path(board, myPosition, myPosition,0,-1, moves, getPieceType()); //left

            return moves;
        }else if (getPieceType() == PieceType.KNIGHT) {
            path(board, myPosition, myPosition,2,1, moves, getPieceType());
            path(board, myPosition, myPosition,2,-1, moves, getPieceType());
            path(board, myPosition, myPosition,-2,1, moves, getPieceType());
            path(board, myPosition, myPosition,-2,-1, moves, getPieceType());
            path(board, myPosition, myPosition,1,2, moves, getPieceType());
            path(board, myPosition, myPosition,1,-2, moves, getPieceType());
            path(board, myPosition, myPosition,-1,2, moves, getPieceType());
            path(board, myPosition, myPosition,-1,-2, moves, getPieceType());

            return moves;
        }else if (getPieceType() == PieceType.PAWN) {


            return moves;
        }
        return moves;
    }
}
