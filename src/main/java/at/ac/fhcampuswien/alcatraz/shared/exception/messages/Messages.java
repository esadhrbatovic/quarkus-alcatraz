package at.ac.fhcampuswien.alcatraz.shared.exception.messages;

public abstract class Messages {

    public static final String SESSION_FULL = "The game session is already full";
    public static final String GAME_RUNNING = "The game is already running";
    public static final String PLAYER_EXISTS = "Your player already is already registered";
    public static final String PLAYER_NOT_FOUND = "Your player could not be found";
    public static final String NOT_ENOUGH_PLAYERS = "Not in enough players in session";
    public static final String NO_USERNAME_PROVIDED = "No username provided";
    public static final String REGISTER_FIRST = "Please register first";
    public static final String LOGOFF_SUCCESS = "You have successfully logged off";
    public static final String READY_TO_PLAY_SUCCESS = "Your player now has the status 'ready'";
    public static final String NOT_READY_TO_PLAY_SUCCES = "Your player now has the status 'not ready'";
    public static final String ERROR_GUI_START = "Error starting the Client GUI";

    public static final String NO_SERVER_AVAILABLE = "No Servers available";
    public static final String ERROR_DETERMINING_SERVER_ROLE = "Could not determine primary/backup state of the new Server";
    public static final String ERROR_SENDING_SESSION = "Error sending object to spreadgroup";
    public static final String ERROR_HANDLING_MEMBERSHIP_MESSAGE = "Error handling membership message";
    public static final String ERROR_HANDLING_SYNC_MESSAGE = "Error handling sync message";
    public static final String ERROR_ESTABLISHING_SPREAD_CONNECTION = "Error establishing Spread connection";
    public static final String ERROR_JOINING_SPREAD_GROUP = "Error joining Spread group";
    public static final String UNKNOWN_MEMBERSHIP_MESSAGE = "Unknown membership message type";
    public static final String SPREAD_DISCONNECT_DETECTED = "Spread group disconnection detected";
    public static final String SPREAD_LEAVE_DETECTED = "Spread group leave detected";
    public static final String SPREAD_JOIN_DETECTED = "Spread group join detected";
    public static final String SENDING_GAMESESSION = "Sending GameSession to the spread group";
    public static final String SPREAD_MEMBERSHIP_MESSAGE_RECIEVED = "Spread membership message recieved";
}

