package at.ac.fhcampuswien.alcatraz.shared.exception;

public class PlayerAlreadyExistsException extends RuntimeException {

    public PlayerAlreadyExistsException(String message) {
        super(message);
    }
}
