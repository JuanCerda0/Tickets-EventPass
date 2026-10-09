package dev.eventpass.tickets.dto.response;

import dev.eventpass.tickets.model.EstadoTicket;

public record TicketYaUtilizadoResponse(
    String codigo,
    EstadoTicket estado,
    boolean valido,
    String mensaje
) {
}
