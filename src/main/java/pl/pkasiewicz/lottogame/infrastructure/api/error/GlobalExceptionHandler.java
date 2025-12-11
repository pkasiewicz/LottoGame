package pl.pkasiewicz.lottogame.infrastructure.api.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pl.pkasiewicz.lottogame.numberreceiver.domain.exception.InvalidTicketNumbersException;
import pl.pkasiewicz.lottogame.numberreceiver.domain.exception.InvalidTicketSizeException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = {InvalidTicketSizeException.class})
    public ResponseEntity<ErrorResponse> handleInvalidTicketSize(InvalidTicketSizeException e){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("INVALID_TICKET_SIZE", e.getMessage()));
    }

    @ExceptionHandler(value = {InvalidTicketNumbersException.class})
    public ResponseEntity<ErrorResponse> handleInvalidNumbers(InvalidTicketNumbersException e){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("INVALID_NUMBERS", e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex) {

        String errorMessage = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getDefaultMessage())
                .findFirst()
                .orElse("Validation failed");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("VALIDATION_ERROR", errorMessage));
    }
}

