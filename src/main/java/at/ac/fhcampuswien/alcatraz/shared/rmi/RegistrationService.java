package at.ac.fhcampuswien.alcatraz.shared.rmi;

import at.ac.fhcampuswien.alcatraz.shared.exception.*;
import at.ac.fhcampuswien.alcatraz.shared.model.NetPlayer;
import at.ac.fhcampuswien.alcatraz.shared.model.GameSession;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RegistrationService extends Remote, Serializable {
    void register(NetPlayer player) throws RemoteException, AlreadyRegisteredException, GameAlreadyRunningException, FullSessionException;

    void logOff(NetPlayer player) throws RemoteException, UserNotFoundException, GameAlreadyRunningException;

    void readyToPlay(NetPlayer player) throws RemoteException, UserNotFoundException, GameAlreadyRunningException;

    void notReadyToPlay(NetPlayer player) throws RemoteException, UserNotFoundException, GameAlreadyRunningException;

    boolean isPrimary() throws RemoteException;

    GameSession<NetPlayer> loadGameSession() throws RemoteException;

    void startGame() throws RemoteException, GameAlreadyRunningException, NotEnoughPlayersException;
}
