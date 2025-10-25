package exceptions;

public class AlreadyTakenException extends ServiceException {
    public AlreadyTakenException() {
        super(403, "already taken");
    }
}
