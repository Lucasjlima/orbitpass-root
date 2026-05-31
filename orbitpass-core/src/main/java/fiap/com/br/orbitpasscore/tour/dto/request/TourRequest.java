package fiap.com.br.orbitpasscore.tour.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record TourRequest(
        @NotBlank String name,
        String description,
        @NotBlank String destination,
        @NotNull @Positive BigDecimal price
) {
}
