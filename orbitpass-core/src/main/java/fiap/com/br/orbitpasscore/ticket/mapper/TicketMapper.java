package fiap.com.br.orbitpasscore.ticket.mapper;

import fiap.com.br.orbitpasscore.ticket.dto.request.TicketRequest;
import fiap.com.br.orbitpasscore.ticket.dto.response.TicketResponse;
import fiap.com.br.orbitpasscore.ticket.entity.Ticket;
import fiap.com.br.orbitpasscore.tourdate.entity.TourDate;
import fiap.com.br.orbitpasscore.user.entity.User;

public final class TicketMapper {

    private TicketMapper() {
    }

    public static Ticket toEntity(TicketRequest request) {
        User user = new User();
        user.setId(request.userId());

        TourDate tourDate = new TourDate();
        tourDate.setId(request.tourDateId());

        return Ticket.builder()
                .user(user)
                .tourDate(tourDate)
                .status(request.status())
                .price(request.price())
                .build();
    }

    public static TicketResponse toResponse(Ticket ticket) {
        Long userId = ticket.getUser() != null ? ticket.getUser().getId() : null;
        Long tourDateId = ticket.getTourDate() != null ? ticket.getTourDate().getId() : null;

        return new TicketResponse(
                ticket.getId(),
                userId,
                tourDateId,
                ticket.getStatus(),
                ticket.getBookingDate(),
                ticket.getPrice()
        );
    }
}
