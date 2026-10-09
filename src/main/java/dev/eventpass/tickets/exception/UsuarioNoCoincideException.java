package dev.eventpass.tickets.exception;

public class UsuarioNoCoincideException extends RuntimeException {

    public UsuarioNoCoincideException() {
        super("El usuario de la solicitud no coincide con el usuario autenticado");
    }
}
