package fiap.com.br.orbitpasscore.tour.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record TourResponse(
        Long id,
        String name,
        String description,
        String destination,
        BigDecimal price,
        List<Long> dateIds
) {
}
