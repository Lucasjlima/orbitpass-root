package fiap.com.br.orbitpasscore.tourdate.repository;

import fiap.com.br.orbitpasscore.tourdate.entity.TourDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TourDateRepository extends JpaRepository<TourDate, Long> {
}
