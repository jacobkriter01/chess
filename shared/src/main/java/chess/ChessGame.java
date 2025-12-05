package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private ChessBoard board;
    private TeamColor turn;
    private ArrayList<ChessMove> gameLog;
    private boolean resigned = false;

    public ChessGame() {
        this.board = new ChessBoard();
        this.board.resetBoard();
        this.turn = TeamColor.WHITE;
        this.gameLog = new ArrayList<>();
    }

    public boolean getResigned() {
        return resigned;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return turn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        turn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);

        if (piece == null) {
            return null;
        }

        Collection<ChessMove> moves = piece.pieceMoves(board, startPosition);

        Collection<ChessMove> legalMoves = new ArrayList<>();

        for (ChessMove move : moves) {
            ChessBoard copy = board.cloneBoard();

            copy.movePiece(move);

            // check for check
            if (!isCopyInCheck(piece.getTeamColor(), copy)){
                legalMoves.add(move);
            }



        }

        return legalMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        //needs to make the move first then update teamColor
        ChessPiece piece = board.getPiece(move.getStartPosition());
        if (piece == null) {
            throw new InvalidMoveException("No piece at start position");
        }
        if (piece.getTeamColor() != turn) {
            throw new InvalidMoveException("Not this teams turn");
        }

        if(isInCheck(getTeamTurn())){
            if(!move.getStartPosition().equals(board.findKing(getTeamTurn()))) {
                throw new InvalidMoveException("King is already in check");
            }
        }

        Collection<ChessMove> moves = validMoves(move.getStartPosition());

        if (!moves.contains(move)) {
            throw new InvalidMoveException("Move not valid");
        }

        if(isInCheckmate(getTeamTurn())) {
            throw new InvalidMoveException("King is already in checkmate");
        }

        if(isInStalemate(getTeamTurn())){
            throw new InvalidMoveException("Game is stalemate");
        }

        board.movePiece(move);
        gameLog.add(move);


        turn = (turn == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;




    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        //find the teamColor king
        ChessPosition kingLocation = board.findKing(teamColor);

        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPiece kingHunter = board.getPiece(new ChessPosition(i, j));
                if (kingHunter == null || kingHunter.getTeamColor() == teamColor) {
                    continue;
                }for (ChessMove move : kingHunter.pieceMoves(board, new ChessPosition(i, j))) {
                    if (move.getEndPosition().equals(kingLocation)) {
                        return true;
                    }
                }

            }
        }
        return false;
    }

    public boolean isCopyInCheck(TeamColor teamColor, ChessBoard copy) {
        //find the teamColor king
        ChessPosition kingLocation = copy.findKing(teamColor);

        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPiece enemy = copy.getPiece(new ChessPosition(i, j));
                if (enemy == null || enemy.getTeamColor() == teamColor) {
                    continue;
                }
               for  (ChessMove move : enemy.pieceMoves(copy, new ChessPosition(i, j))) {
                   if (move.getEndPosition().equals(kingLocation)) {
                       return true;
                   }
               }
            }
        }

        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if(!isInCheck(teamColor)){
            return false;
        }

        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPiece piece = board.getPiece(new ChessPosition(i, j));
                if (piece != null && piece.getTeamColor() == teamColor) {
                    Collection<ChessMove> moves = validMoves(new ChessPosition(i, j));
                    if(!moves.isEmpty()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if(isInCheck(teamColor)){
            return false;
        }
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPiece piece = board.getPiece(new ChessPosition(i, j));
                if (piece != null && piece.getTeamColor() == teamColor) {
                    Collection<ChessMove> moves = validMoves(new ChessPosition(i, j));
                    if(moves != null && !moves.isEmpty()) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    public void setResigned(boolean value) {
        this.resigned = value;
    }

    @Override
    public String toString() {
        return "ChessGame{" +
                "board=" + board +
                ", turn=" + turn +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return Objects.equals(board, chessGame.board) && turn == chessGame.turn;
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, turn);
    }
}
