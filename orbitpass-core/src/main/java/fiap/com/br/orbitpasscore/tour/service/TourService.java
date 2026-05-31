package fiap.com.br.orbitpasscore.tour.service;

import fiap.com.br.orbitpasscore.tour.repository.TourRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TourService {

    private final TourRepository tourRepository;
}
