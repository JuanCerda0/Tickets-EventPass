package dev.eventpass.tickets.exception;

public class TicketYaUtilizadoException extends RuntimeException {

    private final String codigo;

    public TicketYaUtilizadoException(String codigo) {
        super("El código ya fue utilizado.");
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }
}
