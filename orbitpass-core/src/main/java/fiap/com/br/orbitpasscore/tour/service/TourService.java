package fiap.com.br.orbitpasscore.tour.service;

import fiap.com.br.orbitpasscore.tour.entity.Tour;
import fiap.com.br.orbitpasscore.tour.exception.TourNotFoundException;
import fiap.com.br.orbitpasscore.tour.repository.TourRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TourService {

    private final TourRepository tourRepository;

    @Transactional
    public Tour create(Tour tour) {
        return tourRepository.save(tour);
    }

    @Transactional(readOnly = true)
    public Tour findById(Long id) {
        return tourRepository.findById(id)
                .orElseThrow(() -> new TourNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public List<Tour> findAll() {
        return tourRepository.findAll();
    }

    @Transactional
    public Tour update(Long id, Tour updated) {
        Tour existing = findById(id);
        existing.setName(updated.getName());
        existing.setDescription(updated.getDescription());
        existing.setDestination(updated.getDestination());
        existing.setPrice(updated.getPrice());
        return tourRepository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        if (!tourRepository.existsById(id)) {
            throw new TourNotFoundException(id);
        }
        tourRepository.deleteById(id);
    }
}
