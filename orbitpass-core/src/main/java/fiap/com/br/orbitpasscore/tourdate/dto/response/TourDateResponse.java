package fiap.com.br.orbitpasscore.tourdate.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record TourDateResponse(
        Long id,
        Long tourId,
        LocalDateTime departureDate,
        LocalDateTime returnDate,
        int totalSpots,
        int bookedSpots,
        List<Long> ticketIds
) {
}
