package fiap.com.br.orbitpasscore.tour.exception;

public class TourNotFoundException extends RuntimeException {

    public TourNotFoundException(Long id) {
        super("Tour not found with id: " + id);
    }
}
