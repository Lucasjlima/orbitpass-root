package fiap.com.br.orbitpasscore.tourdate.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TourDateServiceTest {

    @Mock
    private TourDateRepository tourDateRepository;

    @Mock
    private TourRepository tourRepository;

    @InjectMocks
    private TourDateService tourDateService;

    private final LocalDateTime departure = LocalDateTime.now().plusDays(10);
    private final LocalDateTime ret = LocalDateTime.now().plusDays(12);

    @Test
    void createForcesBookedSpotsToZeroEvenWhenRequestProvidesValue() {
        Tour tour = Tour.builder().id(1L).build();
        when(tourRepository.findById(1L)).thenReturn(Optional.of(tour));
        when(tourDateRepository.save(any(TourDate.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TourDateRequest request = new TourDateRequest(1L, departure, ret, 50, 30);

        TourDate result = tourDateService.create(request);

        assertThat(result.getBookedSpots()).isZero();
        assertThat(result.getTotalSpots()).isEqualTo(50);
        assertThat(result.getTour()).isSameAs(tour);
    }

    @Test
    void createThrowsWhenTourDoesNotExist() {
        when(tourRepository.findById(99L)).thenReturn(Optional.empty());

        TourDateRequest request = new TourDateRequest(99L, departure, ret, 50, 0);

        assertThatThrownBy(() -> tourDateService.create(request))
                .isInstanceOf(TourNotFoundException.class);
        verify(tourDateRepository, never()).save(any());
    }

    @Test
    void createThrowsWhenReturnDateIsBeforeDepartureDate() {
        Tour tour = Tour.builder().id(1L).build();
        when(tourRepository.findById(1L)).thenReturn(Optional.of(tour));

        TourDateRequest request = new TourDateRequest(1L, departure, departure.minusHours(1), 50, 0);

        assertThatThrownBy(() -> tourDateService.create(request))
                .isInstanceOf(InvalidTourDateException.class);
        verify(tourDateRepository, never()).save(any());
    }

    @Test
    void createAllowsSameDayReturnAfterDeparture() {
        Tour tour = Tour.builder().id(1L).build();
        when(tourRepository.findById(1L)).thenReturn(Optional.of(tour));
        when(tourDateRepository.save(any(TourDate.class))).thenAnswer(invocation -> invocation.getArgument(0));

        LocalDateTime morning = departure.withHour(9);
        LocalDateTime evening = departure.withHour(18);
        TourDateRequest request = new TourDateRequest(1L, morning, evening, 20, 0);

        TourDate result = tourDateService.create(request);

        assertThat(result.getReturnDate()).isEqualTo(evening);
    }

    @Test
    void updateThrowsWhenTotalSpotsBelowCurrentBookedSpots() {
        TourDate existing = TourDate.builder()
                .id(5L)
                .departureDate(departure)
                .returnDate(ret)
                .totalSpots(50)
                .bookedSpots(30)
                .build();
        when(tourDateRepository.findById(5L)).thenReturn(Optional.of(existing));

        TourDateUpdateRequest request = new TourDateUpdateRequest(departure, ret, 20);

        assertThatThrownBy(() -> tourDateService.update(5L, request))
                .isInstanceOf(InvalidTourDateException.class);
        verify(tourDateRepository, never()).save(any());
    }

    @Test
    void updateSucceedsWhenTotalSpotsEqualsBookedSpots() {
        TourDate existing = TourDate.builder()
                .id(5L)
                .departureDate(departure)
                .returnDate(ret)
                .totalSpots(50)
                .bookedSpots(30)
                .build();
        when(tourDateRepository.findById(5L)).thenReturn(Optional.of(existing));
        when(tourDateRepository.save(any(TourDate.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TourDateUpdateRequest request = new TourDateUpdateRequest(departure, ret, 30);

        TourDate result = tourDateService.update(5L, request);

        assertThat(result.getTotalSpots()).isEqualTo(30);
        assertThat(result.getBookedSpots()).isEqualTo(30);
    }

    @Test
    void findByIdThrowsWhenMissing() {
        when(tourDateRepository.findById(7L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tourDateService.findById(7L))
                .isInstanceOf(TourDateNotFoundException.class);
    }
}
