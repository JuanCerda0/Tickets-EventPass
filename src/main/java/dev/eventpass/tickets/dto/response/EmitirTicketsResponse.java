package dev.eventpass.tickets.dto.response;

import java.util.List;

public record EmitirTicketsResponse(Long ordenId, List<TicketEmitidoResponse> tickets) {
}
