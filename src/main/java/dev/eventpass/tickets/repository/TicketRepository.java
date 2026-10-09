package dev.eventpass.tickets.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import dev.eventpass.tickets.model.Ticket;
import jakarta.persistence.LockModeType;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    boolean existsByCodigo(String codigo);

    List<Ticket> findAllByOrdenIdOrderByNumeroEnOrdenAsc(Long ordenId);

    List<Ticket> findAllByUsuarioIdOrderByTicketIdDesc(Long usuarioId);

    Optional<Ticket> findByCodigo(String codigo);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select t from Ticket t where t.codigo = :codigo")
    Optional<Ticket> buscarPorCodigoParaValidar(@Param("codigo") String codigo);
}
