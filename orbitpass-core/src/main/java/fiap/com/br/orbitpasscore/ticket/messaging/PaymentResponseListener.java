package fiap.com.br.orbitpasscore.ticket.messaging;

import fiap.com.br.orbitpasscore.ticket.messaging.dto.PaymentResponseMessage;
import fiap.com.br.orbitpasscore.ticket.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentResponseListener {

    private final TicketService ticketService;

    @RabbitListener(queues = "payment.response.queue")
    public void onPaymentResponse(PaymentResponseMessage message) {
        ticketService.applyPaymentResult(message.ticketId(), message.status());
    }
}
