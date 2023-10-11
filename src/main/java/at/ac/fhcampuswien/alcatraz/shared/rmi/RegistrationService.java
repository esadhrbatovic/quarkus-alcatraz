package at.ac.fhcampuswien.alcatraz.shared.rmi;

import at.ac.fhcampuswien.alcatraz.shared.model.NetPlayer;
import at.ac.fhcampuswien.alcatraz.shared.model.Session;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RegistrationService extends Remote, Serializable {
    boolean isPrimary() throws RemoteException;

    int getLobbySize() throws RemoteException;

    Session<NetPlayer> registerMe(NetPlayer player) throws RemoteException;


    Session<NetPlayer> unregister(NetPlayer player) throws RemoteException;

    void ready(NetPlayer player) throws RemoteException;


    void undoReady(NetPlayer player) throws RemoteException;

    Session<NetPlayer> getSession()  throws RemoteException;
}
