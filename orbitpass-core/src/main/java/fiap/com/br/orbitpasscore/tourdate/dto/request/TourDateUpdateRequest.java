package fiap.com.br.orbitpasscore.tourdate.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;

public record TourDateUpdateRequest(
        @NotNull @Future LocalDateTime departureDate,
        @NotNull LocalDateTime returnDate,
        @Positive int totalSpots
) {
}
