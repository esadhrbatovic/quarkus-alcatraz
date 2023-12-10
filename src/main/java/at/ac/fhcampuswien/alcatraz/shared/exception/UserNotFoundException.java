package at.ac.fhcampuswien.alcatraz.shared.exception;

import at.ac.fhcampuswien.alcatraz.shared.exception.messages.Messages;

public class UserNotFoundException extends Exception {
    public UserNotFoundException() {
        super(Messages.PLAYER_NOT_FOUND);
    }

}