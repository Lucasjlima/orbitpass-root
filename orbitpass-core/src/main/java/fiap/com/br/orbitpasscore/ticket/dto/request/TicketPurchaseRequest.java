package fiap.com.br.orbitpasscore.ticket.dto.request;

import fiap.com.br.orbitpasscore.ticket.entity.PaymentType;
import jakarta.validation.constraints.NotNull;

public record TicketPurchaseRequest(
        @NotNull(message = "Tour date ID is required") Long tourDateId,
        @NotNull(message = "Payment type is required") PaymentType paymentType
) {
}
