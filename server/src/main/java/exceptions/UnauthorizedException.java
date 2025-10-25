package exceptions;

public class UnauthorizedException extends ServiceException {
    public UnauthorizedException() {
        super(401, "unauthorized");
    }
}
