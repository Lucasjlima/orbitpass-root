package fiap.com.br.orbitpasscore.ticket.messaging;

import fiap.com.br.orbitpasscore.ticket.entity.PaymentType;
import java.math.BigDecimal;

public record PaymentRequestedEvent(
        Long ticketId,
        BigDecimal amount,
        PaymentType paymentType
) {
}
