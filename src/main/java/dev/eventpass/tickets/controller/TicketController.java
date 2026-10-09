package dev.eventpass.tickets.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dev.eventpass.tickets.dto.response.TicketResponse;
import dev.eventpass.tickets.dto.response.ValidarTicketResponse;
import dev.eventpass.tickets.security.UsuarioAutenticado;
import dev.eventpass.tickets.service.TicketService;
import jakarta.validation.constraints.Positive;

@RestController
@RequestMapping("/tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping("/mis-tickets")
    public ResponseEntity<List<TicketResponse>> listarMisTickets(
        @RequestParam @Positive Long usuarioId,
        @AuthenticationPrincipal UsuarioAutenticado usuario
    ) {
        return ResponseEntity.ok(ticketService.listarMisTickets(usuarioId, usuario));
    }

    @PostMapping("/{codigo}/validaciones")
    public ResponseEntity<ValidarTicketResponse> validar(@PathVariable String codigo) {
        return ResponseEntity.ok(ticketService.validar(codigo));
    }
}
