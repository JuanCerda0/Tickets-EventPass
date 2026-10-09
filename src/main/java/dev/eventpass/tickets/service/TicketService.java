package dev.eventpass.tickets.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.eventpass.tickets.dto.request.EmitirTicketsRequest;
import dev.eventpass.tickets.dto.response.EmitirTicketsResponse;
import dev.eventpass.tickets.dto.response.TicketEmitidoResponse;
import dev.eventpass.tickets.exception.ConflictoTicketException;
import dev.eventpass.tickets.exception.UsuarioNoCoincideException;
import dev.eventpass.tickets.model.EstadoTicket;
import dev.eventpass.tickets.model.Ticket;
import dev.eventpass.tickets.repository.TicketRepository;
import dev.eventpass.tickets.security.UsuarioAutenticado;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final CodigoTicketService codigoTicketService;

    public TicketService(TicketRepository ticketRepository, CodigoTicketService codigoTicketService) {
        this.ticketRepository = ticketRepository;
        this.codigoTicketService = codigoTicketService;
    }

    @Transactional
    public ResultadoEmision emitir(EmitirTicketsRequest request, UsuarioAutenticado usuario) {
        if (!Objects.equals(request.usuarioId(), usuario.id())) {
            throw new UsuarioNoCoincideException();
        }

        List<Ticket> existentes = ticketRepository.findAllByOrdenIdOrderByNumeroEnOrdenAsc(request.ordenId());
        if (!existentes.isEmpty()) {
            if (!coincidenConSolicitud(existentes, request)) {
                throw new ConflictoTicketException(
                    "La orden ya tiene tickets asociados con datos distintos a la solicitud"
                );
            }
            return new ResultadoEmision(crearRespuesta(request.ordenId(), existentes), false);
        }

        List<Ticket> nuevos = new ArrayList<>(request.cantidad());
        for (int numero = 1; numero <= request.cantidad(); numero++) {
            String codigo = generarCodigoDisponible();
            nuevos.add(new Ticket(
                codigo,
                request.ordenId(),
                request.usuarioId(),
                request.eventoId(),
                numero,
                EstadoTicket.EMITIDO
            ));
        }

        List<Ticket> guardados = ticketRepository.saveAllAndFlush(nuevos);
        return new ResultadoEmision(crearRespuesta(request.ordenId(), guardados), true);
    }

    private boolean coincidenConSolicitud(List<Ticket> existentes, EmitirTicketsRequest request) {
        return existentes.size() == request.cantidad()
            && existentes.stream().allMatch(ticket ->
                Objects.equals(ticket.getUsuarioId(), request.usuarioId())
                    && Objects.equals(ticket.getEventoId(), request.eventoId())
            );
    }

    private String generarCodigoDisponible() {
        String codigo;
        do {
            codigo = codigoTicketService.generar();
        } while (ticketRepository.existsByCodigo(codigo));
        return codigo;
    }

    private EmitirTicketsResponse crearRespuesta(Long ordenId, List<Ticket> tickets) {
        List<TicketEmitidoResponse> respuestas = tickets.stream()
            .map(ticket -> new TicketEmitidoResponse(ticket.getTicketId(), ticket.getCodigo()))
            .toList();
        return new EmitirTicketsResponse(ordenId, respuestas);
    }
}
