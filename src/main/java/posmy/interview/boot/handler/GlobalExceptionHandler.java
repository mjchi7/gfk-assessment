package posmy.interview.boot.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import posmy.interview.boot.constant.Message;
import posmy.interview.boot.data.ephemeral.APIErrorData;
import posmy.interview.boot.exception.InvalidTransitionException;

import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.UUID;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private String getXcpId() {
        return UUID.randomUUID().toString();
    }

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public APIErrorData handleNoSuchElementException(NoSuchElementException ex, WebRequest req) {
        String xcpId = getXcpId();
        APIErrorData data = new APIErrorData(xcpId, HttpStatus.NOT_FOUND
                .value(), Message.OBJECT_NOT_FOUND_MESSAGE, new HashMap<>());
        logger.error(data.toString());
        return data;
    }

    @ExceptionHandler(InvalidTransitionException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public APIErrorData handleInvalidTransitionException(InvalidTransitionException ex, WebRequest req) {
        String xcpId = getXcpId();
        APIErrorData data = new APIErrorData(xcpId, HttpStatus.BAD_REQUEST
                .value(), ex.getMessage(), new HashMap<>());
        logger.error(data.toString());
        return data;
    }
}
