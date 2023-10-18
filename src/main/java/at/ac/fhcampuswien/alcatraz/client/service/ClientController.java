package at.ac.fhcampuswien.alcatraz.client.service;

import at.ac.fhcampuswien.alcatraz.client.rmi.RmiClient;
import at.ac.fhcampuswien.alcatraz.shared.exception.*;
import at.ac.fhcampuswien.alcatraz.shared.exception.messages.Messages;
import at.ac.fhcampuswien.alcatraz.shared.model.NetPlayer;
import at.ac.fhcampuswien.alcatraz.shared.model.GameSession;
import at.ac.fhcampuswien.alcatraz.shared.rmi.NetGameService;
import at.ac.fhcampuswien.alcatraz.shared.rmi.RegistrationService;
import at.ac.fhcampuswien.alcatraz.shared.rmi.RegistryProvider;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.Objects;
import java.util.UUID;

@ApplicationScoped
public class ClientController {

    RegistrationService registrationService;

    @Inject
    RmiClient rmiClient;

    @Inject
    NetGameService netGameService;

    private GameSession<NetPlayer> gameSession;

    public void register(String name) throws RemoteException, AlcatrazException, NotBoundException {
        try {
            int id = registrationService.loadGameSession().size();
            Registry registry = RegistryProvider.getOrCreateRegistry(1098);
            UUID remoteIdentifier = UUID.randomUUID();
            registry.bind("NetGameService" + remoteIdentifier, netGameService);
            NetPlayer localPlayer = new NetPlayer(id, name, remoteIdentifier);
            registrationService.register(localPlayer);
        } catch (AlreadyBoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void findCurrentPrimary() throws RemoteException {
        this.registrationService = this.rmiClient.getRegistrationService();
    }

    public void joinSession(String name) throws RemoteException, AlcatrazException {
        NetPlayer netPlayer = findPlayerBy(name);
        registrationService.joinSession(netPlayer);
    }

    public void logOff(String name) throws RemoteException, AlcatrazException {
        NetPlayer netPlayer = findPlayerBy(name);
        registrationService.logOff(netPlayer);
    }

    public void leaveSession(String name) throws RemoteException, AlcatrazException {
        NetPlayer netPlayer = findPlayerBy(name);
        registrationService.leaveSession(netPlayer);
    }

    private NetPlayer findPlayerBy(String name) throws RemoteException {
        return this.registrationService.loadGameSession().stream()
                .filter(x -> Objects.equals(x.getName(), name))
                .findFirst()
                .orElseThrow(() -> new AlcatrazException(Messages.PLAYER_NOT_FOUND));
    }

    public RegistrationService getRegistrationService() {
        return registrationService;
    }


    public GameSession<NetPlayer> getGameSession() {
        return this.gameSession;
    }

    public void setGameSession(GameSession<NetPlayer> gameSession) {
        this.gameSession = gameSession;
    }
}
