package dev.eventpass.tickets.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    name = "tickets",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_ticket_codigo", columnNames = "codigo"),
        @UniqueConstraint(
            name = "uk_ticket_orden_numero",
            columnNames = {"orden_id", "numero_en_orden"}
        )
    }
)
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ticket_id")
    private Long ticketId;

    @Column(nullable = false, length = 16)
    private String codigo;

    @Column(name = "orden_id", nullable = false)
    private Long ordenId;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(name = "evento_id", nullable = false)
    private Long eventoId;

    @Column(name = "numero_en_orden", nullable = false)
    private Integer numeroEnOrden;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private EstadoTicket estado;

    protected Ticket() {
    }

    public Ticket(String codigo, Long ordenId, Long usuarioId, Long eventoId,
            Integer numeroEnOrden, EstadoTicket estado) {
        this.codigo = codigo;
        this.ordenId = ordenId;
        this.usuarioId = usuarioId;
        this.eventoId = eventoId;
        this.numeroEnOrden = numeroEnOrden;
        this.estado = estado;
    }

    public Long getTicketId() {
        return ticketId;
    }

    public String getCodigo() {
        return codigo;
    }

    public Long getOrdenId() {
        return ordenId;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public Long getEventoId() {
        return eventoId;
    }

    public Integer getNumeroEnOrden() {
        return numeroEnOrden;
    }

    public EstadoTicket getEstado() {
        return estado;
    }

    public void marcarUtilizado() {
        this.estado = EstadoTicket.UTILIZADO;
    }
}
