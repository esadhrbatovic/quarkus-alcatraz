package at.ac.fhcampuswien.alcatraz.server.service;

import at.ac.fhcampuswien.alcatraz.server.GameSessionService;
import at.ac.fhcampuswien.alcatraz.server.ServerContext;
import at.ac.fhcampuswien.alcatraz.server.spread.service.SpreadService;
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
    ServerContext serverContext;
    @Inject
    GameSessionService gameSessionService;
    @Inject
    SpreadService spreadService;

    protected RegistrationServiceImpl() throws RemoteException {
        super();
    }

    @Override
    public boolean isPrimary() throws RemoteException {
        return this.serverContext.isPrimary();
    }

    @Override
    public GameSession<NetPlayer> loadGameSession() throws RemoteException {
        return this.serverContext.getSession();
    }

    @Override
    public void startGame() throws RemoteException {
        gameSessionService.startGame();
        this.serverContext.getSession().clear();
        spreadService.sendMessageToSpread(this.serverContext.getSession());
    }

    @Override
    public void register(NetPlayer player) throws RemoteException {
        gameSessionService.register(player);
        spreadService.sendMessageToSpread(this.serverContext.getSession());
    }

    @Override
    public void logOff(NetPlayer player) throws RemoteException {
        gameSessionService.unregister(player);
        spreadService.sendMessageToSpread(this.serverContext.getSession());
    }

    @Override
    public void readyToPlay(NetPlayer player) throws RemoteException {
        gameSessionService.readyToPlay(player);
        spreadService.sendMessageToSpread(this.serverContext.getSession());
    }

    @Override
    public void notReadyToPlay(NetPlayer player) throws RemoteException {
        gameSessionService.undoReady(player);
        spreadService.sendMessageToSpread(this.serverContext.getSession());
    }

}
