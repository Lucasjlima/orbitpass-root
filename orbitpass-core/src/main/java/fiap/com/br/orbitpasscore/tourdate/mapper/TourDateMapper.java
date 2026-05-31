package fiap.com.br.orbitpasscore.tourdate.mapper;

import fiap.com.br.orbitpasscore.ticket.entity.Ticket;
import fiap.com.br.orbitpasscore.tour.entity.Tour;
import fiap.com.br.orbitpasscore.tourdate.dto.request.TourDateRequest;
import fiap.com.br.orbitpasscore.tourdate.dto.response.TourDateResponse;
import fiap.com.br.orbitpasscore.tourdate.entity.TourDate;
import java.util.List;
import java.util.Optional;

public final class TourDateMapper {

    private TourDateMapper() {
    }

    public static TourDate toEntity(TourDateRequest request) {
        Tour tour = new Tour();
        tour.setId(request.tourId());

        return TourDate.builder()
                .tour(tour)
                .departureDate(request.departureDate())
                .returnDate(request.returnDate())
                .totalSpots(request.totalSpots())
                .bookedSpots(request.bookedSpots())
                .build();
    }

    public static TourDateResponse toResponse(TourDate tourDate) {
        Long tourId = tourDate.getTour() != null ? tourDate.getTour().getId() : null;
        List<Long> ticketIds = Optional.ofNullable(tourDate.getTickets())
                .orElseGet(List::of)
                .stream()
                .map(Ticket::getId)
                .toList();

        return new TourDateResponse(
                tourDate.getId(),
                tourId,
                tourDate.getDepartureDate(),
                tourDate.getReturnDate(),
                tourDate.getTotalSpots(),
                tourDate.getBookedSpots(),
                ticketIds
        );
    }
}
