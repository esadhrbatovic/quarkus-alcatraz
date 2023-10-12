package at.ac.fhcampuswien.alcatraz.client.service;

import at.ac.fhcampuswien.alcatraz.client.rmi.RmiClient;
import at.ac.fhcampuswien.alcatraz.shared.exception.PlayerAlreadyExistsException;
import at.ac.fhcampuswien.alcatraz.shared.exception.FullSessionException;
import at.ac.fhcampuswien.alcatraz.shared.exception.GameRunningException;
import at.ac.fhcampuswien.alcatraz.shared.exception.PlayerNotFoundException;
import at.ac.fhcampuswien.alcatraz.shared.model.NetPlayer;
import at.ac.fhcampuswien.alcatraz.shared.model.GameSession;
import at.ac.fhcampuswien.alcatraz.shared.rmi.ClientService;
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
    ClientService clientService;

    private GameSession<NetPlayer> gameSession;

    public void register(String name) throws RemoteException, PlayerAlreadyExistsException, FullSessionException, GameRunningException, NotBoundException {
        try {
            int id = registrationService.getLobbySize();
            Registry registry = RegistryProvider.getOrCreateRegistry(1098);
            UUID remoteIdentifier = UUID.randomUUID();
            registry.bind("ClientService" + remoteIdentifier, clientService);
            NetPlayer localPlayer = new NetPlayer(id, name, remoteIdentifier);
            this.gameSession = registrationService.registerMe(localPlayer);
        } catch (AlreadyBoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void findNewPrimary() throws RemoteException {
        this.registrationService = this.rmiClient.getRegistrationService();
    }

    public void logOff(String name) throws RemoteException, PlayerNotFoundException, GameRunningException {
        NetPlayer remotePlayer = findPlayerBy(name);
        registrationService.logOff(remotePlayer);
    }

    public void joinSession(String name) throws RemoteException, PlayerNotFoundException, GameRunningException {
        NetPlayer remotePlayer = findPlayerBy(name);
        registrationService.joinSession(remotePlayer);
    }

    public void renameSession(String name) throws RemoteException, PlayerNotFoundException, GameRunningException {
        NetPlayer remotePlayer = findPlayerBy(name);
        registrationService.leaveSession(remotePlayer);
    }

    private NetPlayer findPlayerBy(String name) {
        return this.gameSession.stream()
                .filter(x -> Objects.equals(x.getName(), name))
                .findFirst()
                .orElseThrow(() -> new PlayerNotFoundException("The specified player could not be found"));
    }

    public RegistrationService getRegistrationService() {
        return registrationService;
    }


    GameSession<NetPlayer> getGameSession() {
        try {
            return registrationService.getGameSession();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    ;

}
