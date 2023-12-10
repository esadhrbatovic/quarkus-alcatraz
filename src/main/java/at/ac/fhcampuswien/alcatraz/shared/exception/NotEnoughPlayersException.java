package at.ac.fhcampuswien.alcatraz.shared.exception;

import at.ac.fhcampuswien.alcatraz.shared.exception.messages.Messages;

public class NotEnoughPlayersException extends Exception {
    public NotEnoughPlayersException() {
        super(Messages.NOT_ENOUGH_PLAYERS);
    }

}