package chess;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Objects;

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

    public void pPromotion(ChessPosition origin, ChessPosition endPosition, ArrayList<ChessMove> moves) {
        moves.add(new ChessMove(origin, endPosition, PieceType.QUEEN));
        moves.add(new ChessMove(origin, endPosition, PieceType.BISHOP));
        moves.add(new ChessMove(origin, endPosition, PieceType.ROOK));
        moves.add(new ChessMove(origin, endPosition, PieceType.KNIGHT));
    }

    public void pCapture(ChessBoard board, ChessPosition origin, int rowDir, ArrayList<ChessMove> moves) {
        int row = origin.getRow() + rowDir;
        int col = origin.getColumn();

        for(int colOffset : new int[]{-1, 1}){
            int newCol = col + colOffset;

            if(newCol < 1 || newCol > 8 || row < 1 || row > 8){
                continue;
            }

            ChessPosition targetPosition = new ChessPosition(row, newCol);
            ChessPiece targetPiece = board.getPiece(targetPosition);
            ChessPiece currentPiece = board.getPiece(origin);

            if (targetPiece != null && targetPiece.getTeamColor() != currentPiece.getTeamColor()){
                if (row == 8 || row == 1){
                    pPromotion(origin, targetPosition, moves);
                } else {
                    moves.add(new ChessMove(origin, targetPosition, null));
                }
            }
        }
    }

    public void path(ChessBoard board, ChessPosition origin, ChessPosition myPosition, int rowDir, int colDir, ArrayList<ChessMove> moves) {
        int row = myPosition.getRow() +  rowDir;
        int col = myPosition.getColumn() +  colDir;

        ChessPosition newPosition = new ChessPosition(row, col);

        if (row > 8 || row < 1 || col > 8 || col < 1) {
            return;
        }

        if(board.getPiece(newPosition) == null) {
            if (type == PieceType.PAWN && (row == 8 || row == 1)){
                pPromotion(origin, newPosition, moves);
            } else {
                moves.add(new ChessMove(origin, newPosition, null));
            }
            if(type == PieceType.QUEEN || type == PieceType.BISHOP || type == PieceType.ROOK) {
                path(board, origin, newPosition, rowDir, colDir, moves);
            }
        }else if (board.getPiece(newPosition).getTeamColor() != pieceColor && type != PieceType.PAWN) {
            moves.add(new ChessMove(origin, newPosition, null));
        }
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        if(type == PieceType.BISHOP){
            path(board, myPosition, myPosition, 1, 1, moves);
            path(board, myPosition, myPosition, 1, -1, moves);
            path(board, myPosition, myPosition, -1, 1, moves);
            path(board, myPosition, myPosition, -1, -1, moves);
        } else if (type == PieceType.ROOK){
            path(board, myPosition, myPosition, 1, 0, moves);
            path(board, myPosition, myPosition, 0, 1, moves);
            path(board, myPosition, myPosition, -1, 0, moves);
            path(board, myPosition, myPosition, 0, -1, moves);
        } else if (type == PieceType.QUEEN || type == PieceType.KING) {
            path(board, myPosition, myPosition, 1, 1, moves);
            path(board, myPosition, myPosition, 1, -1, moves);
            path(board, myPosition, myPosition, -1, 1, moves);
            path(board, myPosition, myPosition, -1, -1, moves);

            path(board, myPosition, myPosition, 1, 0, moves);
            path(board, myPosition, myPosition, 0, 1, moves);
            path(board, myPosition, myPosition, -1, 0, moves);
            path(board, myPosition, myPosition, 0, -1, moves);
        }  else if(type == PieceType.KNIGHT){
            path(board, myPosition, myPosition, 1, 2, moves);
            path(board, myPosition, myPosition, 1, -2, moves);
            path(board, myPosition, myPosition, -1, 2, moves);
            path(board, myPosition, myPosition, -1, -2, moves);
            path(board, myPosition, myPosition, 2, 1, moves);
            path(board, myPosition, myPosition, 2, -1, moves);
            path(board, myPosition, myPosition, -2, 1, moves);
            path(board, myPosition, myPosition, -2, -1, moves);
        }else if (type == PieceType.PAWN){
            int rowDir = 0;

            if(pieceColor == ChessGame.TeamColor.WHITE) {
                rowDir = 1;
            }else{
                rowDir = -1;
            }

            int row = myPosition.getRow();
            int col = myPosition.getColumn();
            ChessPosition nextPosition = new ChessPosition(row + rowDir, col);
            boolean isStartRow = (row == 2 || row == 7);
            boolean isPathClear = board.getPiece(nextPosition) == null;

            if(isStartRow && isPathClear) {
                path(board, myPosition, myPosition, rowDir, 0, moves);
                pCapture(board, myPosition, rowDir, moves);
                path(board, myPosition, nextPosition, rowDir, 0, moves);
            }else{
                path(board, myPosition, myPosition, rowDir, 0, moves);
                pCapture(board, myPosition, rowDir, moves);
            }
        }
        return moves;
    }

    @Override
    public String toString() {
        return "ChessPiece{" +
                "pieceColor=" + pieceColor +
                ", type=" + type +
                ", moves=" +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }
}
