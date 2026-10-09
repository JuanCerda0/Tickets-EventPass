package dev.eventpass.tickets.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> manejarSolicitudInvalida(MethodArgumentNotValidException exception) {
        String mensaje = exception.getBindingResult().getFieldErrors().stream()
            .findFirst()
            .map(error -> "El campo " + error.getField() + " " + error.getDefaultMessage())
            .orElse("La solicitud contiene datos inválidos");
        return ResponseEntity.badRequest().body(new ApiError("SOLICITUD_INVALIDA", mensaje));
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ApiError> manejarParametroInvalido(HandlerMethodValidationException exception) {
        return ResponseEntity.badRequest()
            .body(new ApiError("SOLICITUD_INVALIDA", "El parámetro usuarioId debe ser un número positivo"));
    }

    @ExceptionHandler(UsuarioNoCoincideException.class)
    public ResponseEntity<ApiError> manejarUsuarioNoCoincide(UsuarioNoCoincideException exception) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(new ApiError("USUARIO_NO_COINCIDE", exception.getMessage()));
    }

    @ExceptionHandler(ConflictoTicketException.class)
    public ResponseEntity<ApiError> manejarConflicto(ConflictoTicketException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(new ApiError("CONFLICTO_TICKET", exception.getMessage()));
    }
}
