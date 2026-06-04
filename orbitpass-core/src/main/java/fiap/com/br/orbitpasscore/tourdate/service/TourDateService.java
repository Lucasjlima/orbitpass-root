package fiap.com.br.orbitpasscore.tourdate.service;

import fiap.com.br.orbitpasscore.tour.entity.Tour;
import fiap.com.br.orbitpasscore.tour.exception.TourNotFoundException;
import fiap.com.br.orbitpasscore.tour.repository.TourRepository;
import fiap.com.br.orbitpasscore.tourdate.dto.request.TourDateRequest;
import fiap.com.br.orbitpasscore.tourdate.dto.request.TourDateUpdateRequest;
import fiap.com.br.orbitpasscore.tourdate.entity.TourDate;
import fiap.com.br.orbitpasscore.tourdate.exception.InvalidTourDateException;
import fiap.com.br.orbitpasscore.tourdate.exception.TourDateNotFoundException;
import fiap.com.br.orbitpasscore.tourdate.repository.TourDateRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TourDateService {

    private final TourDateRepository tourDateRepository;
    private final TourRepository tourRepository;

    @Transactional
    public TourDate create(TourDateRequest request) {
        Tour tour = tourRepository.findById(request.tourId())
                .orElseThrow(() -> new TourNotFoundException(request.tourId()));
        validateDates(request.departureDate(), request.returnDate());

        TourDate tourDate = TourDate.builder()
                .tour(tour)
                .departureDate(request.departureDate())
                .returnDate(request.returnDate())
                .totalSpots(request.totalSpots())
                .bookedSpots(0)
                .build();
        return tourDateRepository.save(tourDate);
    }

    @Transactional(readOnly = true)
    public TourDate findById(Long id) {
        return tourDateRepository.findById(id)
                .orElseThrow(() -> new TourDateNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public List<TourDate> findAll() {
        return tourDateRepository.findAll();
    }

    @Transactional
    public TourDate update(Long id, TourDateUpdateRequest request) {
        TourDate existing = findById(id);
        validateDates(request.departureDate(), request.returnDate());

        if (request.totalSpots() < existing.getBookedSpots()) {
            throw new InvalidTourDateException(
                    "totalSpots (" + request.totalSpots() + ") cannot be less than the current bookedSpots ("
                            + existing.getBookedSpots() + ")");
        }

        existing.setDepartureDate(request.departureDate());
        existing.setReturnDate(request.returnDate());
        existing.setTotalSpots(request.totalSpots());
        return tourDateRepository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        if (!tourDateRepository.existsById(id)) {
            throw new TourDateNotFoundException(id);
        }
        tourDateRepository.deleteById(id);
    }

    private void validateDates(LocalDateTime departureDate, LocalDateTime returnDate) {
        if (!returnDate.isAfter(departureDate)) {
            throw new InvalidTourDateException("returnDate must be chronologically after departureDate");
        }
    }
}
