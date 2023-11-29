package at.ac.fhcampuswien.alcatraz.shared.rmi;

import at.ac.fhcampuswien.alcatraz.shared.exception.AlcatrazException;
import at.ac.fhcampuswien.alcatraz.shared.model.NetPlayer;
import at.ac.fhcampuswien.alcatraz.shared.model.GameSession;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RegistrationService extends Remote, Serializable {
    void register(NetPlayer player) throws RemoteException;

    void logOff(NetPlayer player) throws RemoteException;

    void readyToPlay(NetPlayer player) throws RemoteException;

    void notReadyToPlay(NetPlayer player) throws RemoteException;

    boolean isPrimary() throws RemoteException;

    GameSession<NetPlayer> loadGameSession() throws RemoteException;

    void startGame() throws RemoteException, AlcatrazException;
}
