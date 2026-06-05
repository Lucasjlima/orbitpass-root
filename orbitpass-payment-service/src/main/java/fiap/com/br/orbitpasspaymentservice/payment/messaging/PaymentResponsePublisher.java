package fiap.com.br.orbitpasspaymentservice.payment.messaging;

import fiap.com.br.orbitpasspaymentservice.config.RabbitConfig;
import fiap.com.br.orbitpasspaymentservice.payment.entity.PaymentStatus;
import fiap.com.br.orbitpasspaymentservice.payment.messaging.dto.PaymentResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentResponsePublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publish(Long ticketId, PaymentStatus status) {
        PaymentResponseMessage message = new PaymentResponseMessage(ticketId, status);
        rabbitTemplate.convertAndSend(
                RabbitConfig.PAYMENT_EXCHANGE,
                RabbitConfig.PAYMENT_RESPONSE_ROUTING_KEY,
                message
        );
    }
}
