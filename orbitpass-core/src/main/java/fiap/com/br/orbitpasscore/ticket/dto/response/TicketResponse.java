package fiap.com.br.orbitpasscore.ticket.dto.response;

import fiap.com.br.orbitpasscore.ticket.entity.TicketStatus;
import java.math.BigDecimal;
import java.time.Instant;

public record TicketResponse(
        Long id,
        Long userId,
        Long tourDateId,
        TicketStatus status,
        Instant bookingDate,
        BigDecimal price
) {
}
