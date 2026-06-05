package fiap.com.br.orbitpasscore.ticket.service;

import fiap.com.br.orbitpasscore.ticket.entity.PaymentType;
import fiap.com.br.orbitpasscore.ticket.entity.Ticket;
import fiap.com.br.orbitpasscore.ticket.entity.TicketStatus;
import fiap.com.br.orbitpasscore.ticket.exception.InsufficientSpotsException;
import fiap.com.br.orbitpasscore.ticket.exception.TicketNotFoundException;
import fiap.com.br.orbitpasscore.ticket.messaging.PaymentRequestedEvent;
import fiap.com.br.orbitpasscore.ticket.repository.TicketRepository;
import fiap.com.br.orbitpasscore.tourdate.entity.TourDate;
import fiap.com.br.orbitpasscore.tourdate.exception.TourDateNotFoundException;
import fiap.com.br.orbitpasscore.tourdate.repository.TourDateRepository;
import fiap.com.br.orbitpasscore.user.entity.User;
import fiap.com.br.orbitpasscore.user.repository.UserRepository;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final TourDateRepository tourDateRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public Ticket purchase(Long userId, Long tourDateId, PaymentType paymentType) {
        TourDate tourDate = tourDateRepository.findByIdForUpdate(tourDateId)
                .orElseThrow(() -> new TourDateNotFoundException(tourDateId));
        if (tourDate.getBookedSpots() >= tourDate.getTotalSpots()) {
            throw new InsufficientSpotsException(tourDateId);
        }
        tourDate.setBookedSpots(tourDate.getBookedSpots() + 1);
        User user = userRepository.getReferenceById(userId);
        Ticket ticket = Ticket.builder()
                .user(user).tourDate(tourDate)
                .status(TicketStatus.PENDING)
                .bookingDate(Instant.now())
                .price(tourDate.getTour().getPrice())
                .build();
        ticketRepository.save(ticket);
        eventPublisher.publishEvent(new PaymentRequestedEvent(ticket.getId(), ticket.getPrice(), paymentType));
        return ticket;
    }

    @Transactional
    public void applyPaymentResult(Long ticketId, TicketStatus result) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException(ticketId));
        if (ticket.getStatus() != TicketStatus.PENDING) {
            return; // idempotent: ignore duplicates
        }
        if (result == TicketStatus.PAID) {
            ticket.setStatus(TicketStatus.PAID);
        } else {
            ticket.setStatus(TicketStatus.CANCELED);
            TourDate td = tourDateRepository.findByIdForUpdate(ticket.getTourDate().getId())
                    .orElseThrow(() -> new TourDateNotFoundException(ticket.getTourDate().getId()));
            td.setBookedSpots(Math.max(0, td.getBookedSpots() - 1)); // release reserved spot
        }
    }
}
