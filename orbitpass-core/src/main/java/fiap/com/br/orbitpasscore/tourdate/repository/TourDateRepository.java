package fiap.com.br.orbitpasscore.tourdate.repository;

import fiap.com.br.orbitpasscore.tourdate.entity.TourDate;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TourDateRepository extends JpaRepository<TourDate, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select td from TourDate td where td.id = :id")
    Optional<TourDate> findByIdForUpdate(@Param("id") Long id);

    @Query("SELECT td FROM TourDate td JOIN FETCH td.tour t WHERE LOWER(t.destination) LIKE LOWER(CONCAT('%', :destination, '%'))")
    List<TourDate> findByDestinationIgnoreCase(@Param("destination") String destination);
}
