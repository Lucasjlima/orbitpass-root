package fiap.com.br.orbitpasspaymentservice.payment.messaging;

import fiap.com.br.orbitpasspaymentservice.payment.entity.PaymentStatus;
import fiap.com.br.orbitpasspaymentservice.payment.messaging.dto.PaymentRequestMessage;
import fiap.com.br.orbitpasspaymentservice.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentRequestListener {

    private final PaymentService paymentService;
    private final PaymentResponsePublisher paymentResponsePublisher;

    @RabbitListener(queues = "payment.request.queue")
    public void onPaymentRequest(PaymentRequestMessage msg) {
        if (msg.ticketId() == null) {
            throw new IllegalArgumentException("ticketId must not be null");
        }
        PaymentStatus status = paymentService.process(msg.ticketId(), msg.amount(), msg.paymentType());
        paymentResponsePublisher.publish(msg.ticketId(), status);
    }
}
