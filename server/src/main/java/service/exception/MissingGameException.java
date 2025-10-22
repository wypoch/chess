package service.exception;

public class MissingGameException extends RuntimeException {
    public MissingGameException(String message) {
        super(message);
    }
}
