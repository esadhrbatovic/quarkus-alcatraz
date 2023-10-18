package at.ac.fhcampuswien.alcatraz.shared.rmi;

import at.ac.fhcampuswien.alcatraz.shared.model.NetPlayer;
import at.ac.fhcampuswien.alcatraz.shared.model.GameSession;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RegistrationService extends Remote, Serializable {
    void register(NetPlayer player) throws RemoteException;

    void logOff(NetPlayer player) throws RemoteException;

    void joinSession(NetPlayer player) throws RemoteException;

    void leaveSession(NetPlayer player) throws RemoteException;

    boolean isPrimary() throws RemoteException;

    GameSession<NetPlayer> loadGameSession() throws RemoteException;
}
