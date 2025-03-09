package sys.tem.cloudservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import sys.tem.cloudservice.model.dto.ErrorDTO;

import java.io.IOException;

@RestControllerAdvice

public class FileExceptionAdvice extends ResponseEntityExceptionHandler {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({FilesNotFoundException.class,
            IOException.class,
            UserNotFoundException.class})
    public ErrorDTO handleFilesNotFoundException(Exception ex) {
        return new ErrorDTO(ex.getMessage(), 400);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ErrorDTO handleOtherExceptions(Exception ex) {
        return new ErrorDTO("Unknown error", 500);
    }
}
