package dev.eventpass.tickets.service;

import dev.eventpass.tickets.dto.response.EmitirTicketsResponse;

public record ResultadoEmision(EmitirTicketsResponse respuesta, boolean creada) {
}
