package at.ac.fhcampuswien.alcatraz.server.service;

import at.ac.fhcampuswien.alcatraz.server.GameSessionService;
import at.ac.fhcampuswien.alcatraz.server.spread.ServerState;
import at.ac.fhcampuswien.alcatraz.server.spread.SpreadCommunicator;
import at.ac.fhcampuswien.alcatraz.shared.model.NetPlayer;
import at.ac.fhcampuswien.alcatraz.shared.model.GameSession;
import at.ac.fhcampuswien.alcatraz.shared.rmi.RegistrationService;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.io.Serial;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

@Singleton
public class RegistrationServiceImpl extends UnicastRemoteObject implements RegistrationService, Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @Inject
    ServerState serverState;
    @Inject
    GameSessionService gameSessionService;
    @Inject
    SpreadCommunicator spreadCommunicator;

    protected RegistrationServiceImpl() throws RemoteException {
        super();
    }

    @Override
    public boolean isPrimary() throws RemoteException {
        return this.serverState.isPrimary();
    }

    @Override
    public int getLobbySize() throws RemoteException {
        return this.serverState.getSession().size();
    }

    @Override
    public GameSession<NetPlayer> registerMe(NetPlayer player) throws RemoteException {
        gameSessionService.register(player);
        spreadCommunicator.sendMessageToSpread(this.serverState.getSession());
        return this.serverState.getSession();
    }

    @Override
    public void logOff(NetPlayer player) throws RemoteException {
        gameSessionService.unregister(player);
        spreadCommunicator.sendMessageToSpread(this.serverState.getSession());
    }

    @Override
    public void joinSession(NetPlayer player) throws RemoteException {
        gameSessionService.ready(player);
        spreadCommunicator.sendMessageToSpread(this.serverState.getSession());
    }

    @Override
    public void leaveSession(NetPlayer player) throws RemoteException {
        gameSessionService.undoReady(player);
        spreadCommunicator.sendMessageToSpread(this.serverState.getSession());
    }

    @Override
    public GameSession<NetPlayer> getGameSession() throws RemoteException {
        return this.serverState.getSession();
    }

}
