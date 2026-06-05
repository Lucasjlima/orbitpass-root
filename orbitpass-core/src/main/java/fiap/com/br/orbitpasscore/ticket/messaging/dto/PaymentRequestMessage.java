package fiap.com.br.orbitpasscore.ticket.messaging.dto;

import fiap.com.br.orbitpasscore.ticket.entity.PaymentType;
import java.math.BigDecimal;

public record PaymentRequestMessage(
        Long ticketId,
        BigDecimal amount,
        PaymentType paymentType
) {
}
