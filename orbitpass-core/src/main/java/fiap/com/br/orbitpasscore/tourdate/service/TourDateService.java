package fiap.com.br.orbitpasscore.tourdate.service;

import fiap.com.br.orbitpasscore.tourdate.repository.TourDateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TourDateService {

    private final TourDateRepository tourDateRepository;
}
