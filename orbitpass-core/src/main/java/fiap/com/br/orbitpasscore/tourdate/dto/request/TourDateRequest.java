package fiap.com.br.orbitpasscore.tourdate.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;

public record TourDateRequest(
        @NotNull Long tourId,
        @NotNull @Future LocalDateTime departureDate,
        @NotNull LocalDateTime returnDate,
        @Positive int totalSpots,
        @PositiveOrZero int bookedSpots
) {
}
