package fiap.com.br.orbitpasscore.tourdate.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TourAvailabilityInfo(
        Long tourDateId,
        String tourName,
        String destination,
        BigDecimal price,
        LocalDateTime departureDate,
        LocalDateTime returnDate,
        int availableSpots,
        boolean available
) {}
