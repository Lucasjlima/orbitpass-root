package fiap.com.br.orbitpasspaymentservice.payment.messaging.dto;

import fiap.com.br.orbitpasspaymentservice.payment.entity.PaymentStatus;

public record PaymentResponseMessage(Long ticketId, PaymentStatus status) {
}
