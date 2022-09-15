package uplus.nucube.common.trace.advice.exception.advice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import uplus.nucube.common.trace.advice.exception.ErrorResult;
import uplus.nucube.common.trace.advice.exception.UserException;

@Slf4j
//@RestControllerAdvice
public class ExControllerAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public ErrorResult illegalExHandler(IllegalArgumentException e) {
        log.error( "[exceptionHandler] ex" );
        return new ErrorResult( "BAD", e.getMessage() );
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResult> userExHandler(UserException e) {
        log.error( "[exceptionHandler] ex" );
        ErrorResult errorResult = new ErrorResult( "USER-EX", e.getMessage() );
        return new ResponseEntity<>( errorResult, HttpStatus.BAD_REQUEST );
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public ErrorResult exhandler(Exception e) {
        log.error( "[exceptionHandler] ex" );
        return new ErrorResult( "Ex", "내부 오류" );
    }
}
