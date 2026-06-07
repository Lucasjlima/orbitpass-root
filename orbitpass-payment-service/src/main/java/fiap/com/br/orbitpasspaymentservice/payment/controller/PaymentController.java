package fiap.com.br.orbitpasspaymentservice.payment.controller;

import fiap.com.br.orbitpasspaymentservice.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "Payment", description = "Endpoints for managing payments")
public class PaymentController {

    private final PaymentService paymentService;
}
