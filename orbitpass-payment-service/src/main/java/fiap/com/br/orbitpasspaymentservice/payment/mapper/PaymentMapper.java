package fiap.com.br.orbitpasspaymentservice.payment.mapper;

import fiap.com.br.orbitpasspaymentservice.payment.dto.request.PaymentRequest;
import fiap.com.br.orbitpasspaymentservice.payment.dto.response.PaymentResponse;
import fiap.com.br.orbitpasspaymentservice.payment.entity.Payment;

public final class PaymentMapper {

    private PaymentMapper() {
    }

    public static Payment toEntity(PaymentRequest request) {
        return Payment.builder()
                .ticketId(request.ticketId())
                .amount(request.amount())
                .paymentType(request.paymentType())
                .paymentStatus(request.paymentStatus())
                .build();
    }

    public static PaymentResponse toResponse(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getTicketId(),
                payment.getAmount(),
                payment.getPaymentDate(),
                payment.getPaymentType(),
                payment.getPaymentStatus()
        );
    }
}
