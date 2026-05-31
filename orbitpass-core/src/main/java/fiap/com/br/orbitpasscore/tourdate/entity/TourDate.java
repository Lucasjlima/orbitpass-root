package fiap.com.br.orbitpasscore.tourdate.entity;

import fiap.com.br.orbitpasscore.ticket.entity.Ticket;
import fiap.com.br.orbitpasscore.tour.entity.Tour;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tour_dates")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TourDate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tour_id", nullable = false)
    private Tour tour;

    @OneToMany(mappedBy = "tourDate", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Ticket> tickets = new ArrayList<>();

    @Column(name = "departure_date", nullable = false)
    private LocalDateTime departureDate;

    @Column(name = "return_date", nullable = false)
    private LocalDateTime returnDate;

    @Column(name = "total_spots", nullable = false)
    private int totalSpots;

    @Column(name = "booked_spots")
    private int bookedSpots;
}
