package at.ac.fhcampuswien.alcatraz.shared.rmi;

import at.ac.fhcampuswien.alcatraz.shared.model.NetPlayer;
import at.ac.fhcampuswien.alcatraz.shared.model.GameSession;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RegistrationService extends Remote, Serializable {
    boolean isPrimary() throws RemoteException;

    int getLobbySize() throws RemoteException;

    GameSession<NetPlayer> registerMe(NetPlayer player) throws RemoteException;

    GameSession<NetPlayer> unregister(NetPlayer player) throws RemoteException;

    void joinSession(NetPlayer player) throws RemoteException;

    void leaveSession(NetPlayer player) throws RemoteException;

    GameSession<NetPlayer> getGameSession()  throws RemoteException;
}
