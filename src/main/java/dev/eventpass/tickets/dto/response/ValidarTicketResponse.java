package dev.eventpass.tickets.dto.response;

import dev.eventpass.tickets.model.EstadoTicket;

public record ValidarTicketResponse(String codigo, EstadoTicket estado, boolean valido) {
}
