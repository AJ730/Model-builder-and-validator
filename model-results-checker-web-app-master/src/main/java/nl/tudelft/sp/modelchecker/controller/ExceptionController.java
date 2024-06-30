package nl.tudelft.sp.modelchecker.controller;

import java.io.IOException;
import javassist.NotFoundException;
import nl.tudelft.sp.modelchecker.exceptions.AuthorityException;
import nl.tudelft.sp.modelchecker.exceptions.DateException;
import nl.tudelft.sp.modelchecker.exceptions.ExistsException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ExceptionController extends ResponseEntityExceptionHandler {


    /**
     * exception handler for AuthenticationCredentialsNotFoundException.
     *
     * @param ex exception
     * @return Response entity
     */
    @ExceptionHandler
    public ResponseEntity<String> handleException(AuthenticationCredentialsNotFoundException ex) {
        return new ResponseEntity<>(ExceptionUtils.getStackTrace(ex),
                HttpStatus.NOT_FOUND);
    }

    /**
     * exception handler for NotFoundException.
     *
     * @param ex exception
     * @return Response entity
     */
    @ExceptionHandler
    public ResponseEntity<String> handleException(NotFoundException ex) {
        return new ResponseEntity<>(ExceptionUtils.getStackTrace(ex),
                HttpStatus.NOT_FOUND);
    }

    /**
     * exception handler for DateException.
     *
     * @param ex exception
     * @return Response entity
     */
    @ExceptionHandler
    public ResponseEntity<String> handleException(DateException ex) {
        return new ResponseEntity<>(ExceptionUtils.getStackTrace(ex),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * exception handler for ExistsException.
     *
     * @param ex exception
     * @return Response entity
     */
    @ExceptionHandler
    public ResponseEntity<String> handleException(ExistsException ex) {
        return new ResponseEntity<>(ExceptionUtils.getStackTrace(ex),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * exception handler for AuthorityException.
     *
     * @param ex exception
     * @return Response entity
     */
    @ExceptionHandler
    public ResponseEntity<String> handleException(AuthorityException ex) {
        return new ResponseEntity<>(ExceptionUtils.getStackTrace(ex),
                HttpStatus.FORBIDDEN);
    }

    /**
     * exception handler for IOException.
     *
     * @param ex exception
     * @return Response entity
     */
    @ExceptionHandler
    public ResponseEntity<String> handleException(IOException ex) {
        return new ResponseEntity<>(ExceptionUtils.getStackTrace(ex),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
