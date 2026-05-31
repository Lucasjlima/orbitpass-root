package fiap.com.br.orbitpasspaymentservice.payment.dto.response;

import fiap.com.br.orbitpasspaymentservice.payment.entity.PaymentStatus;
import fiap.com.br.orbitpasspaymentservice.payment.entity.PaymentType;
import java.math.BigDecimal;
import java.time.Instant;

public record PaymentResponse(
        Long id,
        Long ticketId,
        BigDecimal amount,
        Instant paymentDate,
        PaymentType paymentType,
        PaymentStatus paymentStatus
) {
}
