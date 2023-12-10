package at.ac.fhcampuswien.alcatraz.shared.exception;

import at.ac.fhcampuswien.alcatraz.shared.exception.messages.Messages;

public class AlreadyRegisteredException extends Exception {
    public AlreadyRegisteredException() {
        super(Messages.PLAYER_EXISTS);
    }

}