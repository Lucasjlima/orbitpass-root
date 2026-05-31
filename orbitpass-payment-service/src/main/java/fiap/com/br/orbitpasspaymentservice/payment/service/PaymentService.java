package fiap.com.br.orbitpasspaymentservice.payment.service;

import fiap.com.br.orbitpasspaymentservice.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
}
