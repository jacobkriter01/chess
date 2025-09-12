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

    private void diagonal(ChessPosition origin, ChessPosition myPosition,int rowDirection, int colDirection, List<ChessMove> moves){
        int row = myPosition.getRow() + rowDirection;
        int col = myPosition.getColumn() + colDirection;

        if (row > 8 || col > 8 || row < 1 || col < 1) {
            return;
        }

        ChessPosition endPosition = new ChessPosition(row,col);
        moves.add(new ChessMove(origin, endPosition, null));

        diagonal(origin, endPosition,rowDirection,colDirection,moves);
    }

    /**
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece piece = board.getPiece(myPosition);
        if (getPieceType() == PieceType.BISHOP) {
            List<ChessMove> moves = new ArrayList<>();

            diagonal(myPosition, myPosition,1,1, moves); //up and right
            diagonal(myPosition, myPosition,-1,1, moves); //down and right
            diagonal(myPosition, myPosition,1,-1, moves); //up and left
            diagonal(myPosition, myPosition,-1,-1, moves); // down and left

            return moves;
        }
        return List.of();
    }
}
