package fiap.com.br.orbitpasscore.ticket.messaging;

import fiap.com.br.orbitpasscore.config.RabbitConfig;
import fiap.com.br.orbitpasscore.ticket.messaging.dto.PaymentRequestMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class PaymentRequestPublisher {

    private final RabbitTemplate rabbitTemplate;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onPaymentRequested(PaymentRequestedEvent event) {
        PaymentRequestMessage message = new PaymentRequestMessage(
                event.ticketId(),
                event.amount(),
                event.paymentType());
        rabbitTemplate.convertAndSend(
                RabbitConfig.PAYMENT_EXCHANGE,
                RabbitConfig.REQUEST_ROUTING_KEY,
                message);
    }
}
