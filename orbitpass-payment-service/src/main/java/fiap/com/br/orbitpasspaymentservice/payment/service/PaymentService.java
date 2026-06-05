package fiap.com.br.orbitpasspaymentservice.payment.service;

import fiap.com.br.orbitpasspaymentservice.payment.entity.Payment;
import fiap.com.br.orbitpasspaymentservice.payment.entity.PaymentStatus;
import fiap.com.br.orbitpasspaymentservice.payment.entity.PaymentType;
import fiap.com.br.orbitpasspaymentservice.payment.repository.PaymentRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    @Transactional
    public PaymentStatus process(Long ticketId, BigDecimal amount, PaymentType paymentType) {
        // Idempotency: if this ticket was already processed, return its existing outcome.
        Optional<Payment> existing = paymentRepository.findByTicketId(ticketId);
        if (existing.isPresent()) {
            return existing.get().getPaymentStatus();
        }
        // Simulate: 90% PAID, 10% CANCELED.
        PaymentStatus status = ThreadLocalRandom.current().nextInt(100) < 90
                ? PaymentStatus.PAID : PaymentStatus.CANCELED;
        Payment payment = Payment.builder()
                .ticketId(ticketId)
                .amount(amount)
                .paymentType(paymentType)
                .paymentStatus(status)
                .paymentDate(Instant.now())
                .build();
        paymentRepository.save(payment);
        return status;
    }
}
