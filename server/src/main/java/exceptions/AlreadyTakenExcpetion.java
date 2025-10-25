package exceptions;

public class AlreadyTakenExcpetion extends ServiceException {
    public AlreadyTakenExcpetion() {
        super(403, "already taken");
    }
}
