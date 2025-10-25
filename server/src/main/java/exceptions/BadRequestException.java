package exceptions;

public class BadRequestException extends ServiceException {
    public BadRequestException() {
        super(400, "bad request");
    }
}
