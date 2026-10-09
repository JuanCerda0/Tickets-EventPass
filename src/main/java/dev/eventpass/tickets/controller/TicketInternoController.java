package dev.eventpass.tickets.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.eventpass.tickets.dto.request.EmitirTicketsRequest;
import dev.eventpass.tickets.dto.response.EmitirTicketsResponse;
import dev.eventpass.tickets.security.UsuarioAutenticado;
import dev.eventpass.tickets.service.ResultadoEmision;
import dev.eventpass.tickets.service.TicketService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/interno/tickets")
public class TicketInternoController {

    private final TicketService ticketService;

    public TicketInternoController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping
    public ResponseEntity<EmitirTicketsResponse> emitir(
        @Valid @RequestBody EmitirTicketsRequest request,
        @AuthenticationPrincipal UsuarioAutenticado usuario
    ) {
        ResultadoEmision resultado = ticketService.emitir(request, usuario);
        HttpStatus status = resultado.creada() ? HttpStatus.CREATED : HttpStatus.OK;
        return ResponseEntity.status(status).body(resultado.respuesta());
    }
}
