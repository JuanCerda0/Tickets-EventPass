package dev.eventpass.tickets.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record EmitirTicketsRequest(
    @NotNull @Positive Long ordenId,
    @NotNull @Positive Long usuarioId,
    @NotNull @Positive Long eventoId,
    @NotNull @Positive Integer cantidad
) {
}
