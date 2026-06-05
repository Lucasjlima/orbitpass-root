package fiap.com.br.orbitpasspaymentservice.payment.messaging.dto;

import fiap.com.br.orbitpasspaymentservice.payment.entity.PaymentType;
import java.math.BigDecimal;

public record PaymentRequestMessage(Long ticketId, BigDecimal amount, PaymentType paymentType) {
}
