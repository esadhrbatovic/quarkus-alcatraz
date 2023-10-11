package at.ac.fhcampuswien.alcatraz.client.service;

import at.ac.fhcampuswien.aclatraz.client.rmi.RmiClient;
import at.ac.fhcampuswien.alcatraz.shared.exception.DuplicatePlayerException;
import at.ac.fhcampuswien.alcatraz.shared.exception.FullLobbyException;
import at.ac.fhcampuswien.alcatraz.shared.exception.GameRunningException;
import at.ac.fhcampuswien.alcatraz.shared.exception.PlayerNotFoundException;
import at.ac.fhcampuswien.alcatraz.shared.model.NetPlayer;
import at.ac.fhcampuswien.alcatraz.shared.model.Session;
import at.ac.fhcampuswien.alcatraz.shared.rmi.ClientService;
import at.ac.fhcampuswien.alcatraz.shared.rmi.RegistrationService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Objects;
import java.util.UUID;

@ApplicationScoped
public class ClientUiService {

    RegistrationService registrationService;

    @Inject
    RmiClient rmiClient;

    @Inject
    ClientService clientService;

    private Session<NetPlayer> session;

    public void register(String name) throws RemoteException, DuplicatePlayerException, FullLobbyException, GameRunningException, NotBoundException {
        int id = registrationService.getLobbySize();
        UUID remoteIdentifier = UUID.randomUUID();
        Registry registry = getOrCreateRegistry(1098);
        try {
            registry.bind("ClientService"+ remoteIdentifier, clientService);
        } catch (AlreadyBoundException e) {
            throw new RuntimeException(e);
        }
        NetPlayer localPlayer = new NetPlayer(id, name, remoteIdentifier);

        this.session = registrationService.registerMe(localPlayer);
    }

    public void findNewPrimary() throws RemoteException {
        this.registrationService = this.rmiClient.getRegistrationService();
    }

    public void unregister(String name) throws RemoteException, PlayerNotFoundException, GameRunningException {
        NetPlayer remotePlayer = findPlayerBy(name);
        registrationService.unregister(remotePlayer);
    }

    public void ready(String name) throws RemoteException, PlayerNotFoundException, GameRunningException {
        NetPlayer remotePlayer = findPlayerBy(name);
        registrationService.ready(remotePlayer);
    }

    public void undoReady(String name) throws RemoteException, PlayerNotFoundException, GameRunningException {
        NetPlayer remotePlayer = findPlayerBy(name);
        registrationService.undoReady(remotePlayer);
    }

    private NetPlayer findPlayerBy(String name) {
        return this.session.stream()
                .filter(x -> Objects.equals(x.getName(), name))
                .findFirst()
                .orElseThrow(() -> new PlayerNotFoundException("The specified player could not be found"));
    }

    public RegistrationService getRegistrationService() {
        return registrationService;
    }


    Session<NetPlayer> getSession() {
        try {
            return registrationService.getSession();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    };

    public static Registry getOrCreateRegistry(int port) {
        Registry registry;
        try {
            // Try to get the existing registry
            registry = LocateRegistry.getRegistry(port);
            // Test if registry is actually running by retrieving list of bound names
            registry.list();
        } catch (RemoteException e) {
            // Could not get registry or registry is not running, create it
            try {
                registry = LocateRegistry.createRegistry(port);
            } catch (RemoteException ex) {
                throw new RuntimeException("Could not create registry on port " + port, ex);
            }
        }
        return registry;
    }

}
