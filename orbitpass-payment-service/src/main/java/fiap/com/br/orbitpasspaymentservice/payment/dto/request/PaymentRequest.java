package fiap.com.br.orbitpasspaymentservice.payment.dto.request;

import fiap.com.br.orbitpasspaymentservice.payment.entity.PaymentStatus;
import fiap.com.br.orbitpasspaymentservice.payment.entity.PaymentType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record PaymentRequest(
        @NotNull Long ticketId,
        @NotNull @Positive BigDecimal amount,
        @NotNull PaymentType paymentType,
        @NotNull PaymentStatus paymentStatus
) {
}
