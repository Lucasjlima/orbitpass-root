package fiap.com.br.orbitpasscore.ticket.repository;

import fiap.com.br.orbitpasscore.ticket.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
}
