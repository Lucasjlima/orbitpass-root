package fiap.com.br.orbitpasscore.tourdate.exception;

public class TourDateNotFoundException extends RuntimeException {

    public TourDateNotFoundException(Long id) {
        super("Tour date not found with id: " + id);
    }
}
