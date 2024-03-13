package catchytape.spring.common.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class CatchyExceptionHandler {

    @ExceptionHandler(CatchyException.class)
    public ResponseEntity<Object> handleResponse(CatchyException e, HttpServletRequest request) {
        
        CatchyExceptionResponse errorResponse = new CatchyExceptionResponse(e.getErrorCode(),  e.getMessage());
        return ResponseEntity.status(e.getStatusCode()).body(errorResponse);
    }
}
