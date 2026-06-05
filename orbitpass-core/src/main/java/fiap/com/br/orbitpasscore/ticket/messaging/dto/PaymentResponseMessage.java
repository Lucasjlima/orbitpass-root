package fiap.com.br.orbitpasscore.ticket.messaging.dto;

import fiap.com.br.orbitpasscore.ticket.entity.TicketStatus;

public record PaymentResponseMessage(
        Long ticketId,
        TicketStatus status
) {
}
