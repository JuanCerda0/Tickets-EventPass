package dev.eventpass.tickets.exception;

public class TicketNoEncontradoException extends RuntimeException {

    public TicketNoEncontradoException(String codigo) {
        super("No existe un ticket con el código " + codigo);
    }
}
