package at.ac.fhcampuswien.alcatraz.shared.exception;

import at.ac.fhcampuswien.alcatraz.shared.exception.messages.Messages;

public class GameAlreadyRunningException extends Exception {
    public GameAlreadyRunningException() {
        super(Messages.GAME_RUNNING);
    }
}
