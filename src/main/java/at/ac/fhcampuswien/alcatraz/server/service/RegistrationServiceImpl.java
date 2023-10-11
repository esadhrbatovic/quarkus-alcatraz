package at.ac.fhcampuswien.alcatraz.server.service;

import at.ac.fhcampuswien.alcatraz.server.GameLogicService;
import at.ac.fhcampuswien.alcatraz.server.spread.ServerState;
import at.ac.fhcampuswien.alcatraz.server.spread.SpreadCommunicator;
import at.ac.fhcampuswien.alcatraz.shared.model.NetPlayer;
import at.ac.fhcampuswien.alcatraz.shared.model.Session;
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
    GameLogicService gameLogicService;
    @Inject
    SpreadCommunicator spreadMessageSender;

    protected RegistrationServiceImpl() throws RemoteException {
        super();
    }

    @Override
    public boolean isPrimary() throws RemoteException {
        return this.serverState.isPrimary();
    }

    @Override
    public int getLobbySize() throws RemoteException {
        if(this.serverState.getSession() == null){
            this.serverState.setSession(new Session<>());
        }
        return this.serverState.getSession().size();
    }

    @Override
    public Session<NetPlayer> registerMe(NetPlayer player) throws RemoteException {
        gameLogicService.register(player);
        spreadMessageSender.sendMessageToSpread(this.serverState.getSession());
        return this.serverState.getSession();
    }

    @Override
    public Session<NetPlayer> unregister(NetPlayer player) throws RemoteException {
        gameLogicService.unregister(player);
        spreadMessageSender.sendMessageToSpread(this.serverState.getSession());
        return this.serverState.getSession();
    }

    @Override
    public void ready(NetPlayer player) throws RemoteException {
        gameLogicService.ready(player);
        spreadMessageSender.sendMessageToSpread(this.serverState.getSession());
    }

    @Override
    public void undoReady(NetPlayer player) throws RemoteException {
        gameLogicService.undoReady(player);
        spreadMessageSender.sendMessageToSpread(this.serverState.getSession());
    }

    @Override
    public Session<NetPlayer> getSession() throws RemoteException {
        return this.serverState.getSession();
    }

}
