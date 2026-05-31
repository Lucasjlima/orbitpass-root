package fiap.com.br.orbitpasspaymentservice.payment.repository;

import fiap.com.br.orbitpasspaymentservice.payment.entity.Payment;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByTicketId(Long ticketId);
}
