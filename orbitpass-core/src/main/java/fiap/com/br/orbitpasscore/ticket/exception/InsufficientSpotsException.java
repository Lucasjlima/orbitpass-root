package fiap.com.br.orbitpasscore.ticket.exception;

public class InsufficientSpotsException extends RuntimeException {

    public InsufficientSpotsException(Long tourDateId) {
        super("No spots available for tour date with id: " + tourDateId);
    }
}
