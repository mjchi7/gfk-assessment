package posmy.interview.boot.exception;

public class InvalidPayloadException extends RuntimeException {

    public InvalidPayloadException(String message) {
        super(message);
    }
}
