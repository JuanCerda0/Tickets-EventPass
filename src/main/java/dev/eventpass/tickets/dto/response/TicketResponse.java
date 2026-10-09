package dev.eventpass.tickets.dto.response;

import dev.eventpass.tickets.model.EstadoTicket;

public record TicketResponse(
    Long ticketId,
    String codigo,
    Long ordenId,
    Long eventoId,
    EstadoTicket estado
) {
}
