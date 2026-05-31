package fiap.com.br.orbitpasscore.tour.mapper;

import fiap.com.br.orbitpasscore.tour.dto.request.TourRequest;
import fiap.com.br.orbitpasscore.tour.dto.response.TourResponse;
import fiap.com.br.orbitpasscore.tour.entity.Tour;
import fiap.com.br.orbitpasscore.tourdate.entity.TourDate;
import java.util.List;
import java.util.Optional;

public final class TourMapper {

    private TourMapper() {
    }

    public static Tour toEntity(TourRequest request) {
        return Tour.builder()
                .name(request.name())
                .description(request.description())
                .destination(request.destination())
                .price(request.price())
                .build();
    }

    public static TourResponse toResponse(Tour tour) {
        List<Long> dateIds = Optional.ofNullable(tour.getDates())
                .orElseGet(List::of)
                .stream()
                .map(TourDate::getId)
                .toList();

        return new TourResponse(
                tour.getId(),
                tour.getName(),
                tour.getDescription(),
                tour.getDestination(),
                tour.getPrice(),
                dateIds
        );
    }
}
