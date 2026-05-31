package fiap.com.br.orbitpasscore.ticket.service;

import fiap.com.br.orbitpasscore.ticket.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
}
