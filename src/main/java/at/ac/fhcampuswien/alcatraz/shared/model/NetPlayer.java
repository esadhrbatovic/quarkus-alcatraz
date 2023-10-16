package at.ac.fhcampuswien.alcatraz.shared.model;

import at.ac.fhcampuswien.alcatraz.shared.rmi.ClientService;
import at.ac.fhcampuswien.alcatraz.shared.rmi.RegistryProvider;
import at.falb.games.alcatraz.api.Player;

import java.io.Serializable;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.UUID;

public class NetPlayer extends Player implements Serializable {

    boolean ready;

    ClientService clientService;

    public NetPlayer(int id, String name, UUID serviceIdentifier) throws RemoteException, NotBoundException {
        super(id);
        try {
            this.clientService = (ClientService) LocateRegistry.getRegistry(1098)
                    .lookup("ClientService" + serviceIdentifier);
        } catch (NotBoundException e) {
            throw new NotBoundException("The service could not be found on the server.");
        }
        this.setName(name);
    }

    public ClientService getClientService() {
        return clientService;
    }

    public void setClientService(ClientService clientService) {
        this.clientService = clientService;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }
}
