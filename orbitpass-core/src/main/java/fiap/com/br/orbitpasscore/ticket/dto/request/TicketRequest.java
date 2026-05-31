package fiap.com.br.orbitpasscore.ticket.dto.request;

import fiap.com.br.orbitpasscore.ticket.entity.TicketStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record TicketRequest(
        @NotNull Long userId,
        @NotNull Long tourDateId,
        @NotNull TicketStatus status,
        @NotNull @Positive BigDecimal price
) {
}
