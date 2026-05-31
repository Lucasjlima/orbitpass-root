package fiap.com.br.orbitpasscore.tour.repository;

import fiap.com.br.orbitpasscore.tour.entity.Tour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TourRepository extends JpaRepository<Tour, Long> {
}
