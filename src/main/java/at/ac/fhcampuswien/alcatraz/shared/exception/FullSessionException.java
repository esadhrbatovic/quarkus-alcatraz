package at.ac.fhcampuswien.alcatraz.shared.exception;

import at.ac.fhcampuswien.alcatraz.shared.exception.messages.Messages;

public class FullSessionException extends Exception {
    public FullSessionException() {
        super(Messages.SESSION_FULL);
    }
}
