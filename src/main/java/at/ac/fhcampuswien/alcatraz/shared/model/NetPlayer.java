package at.ac.fhcampuswien.alcatraz.shared.model;

import at.ac.fhcampuswien.alcatraz.shared.rmi.NetGameService;
import at.falb.games.alcatraz.api.Player;

import java.io.Serializable;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.UUID;

public class NetPlayer extends Player implements Serializable {

    boolean ready;

    NetGameService netGameService;

    public NetPlayer(int id, String name, UUID serviceIdentifier) throws RemoteException, NotBoundException {
        super(id);
        try {
            this.netGameService = (NetGameService) LocateRegistry.getRegistry(1098)
                    .lookup("NetGameService" + serviceIdentifier);
        } catch (NotBoundException e) {
            throw new NotBoundException("The service could not be found on the server.");
        }
        this.setName(name);
    }

    public NetGameService getNetGameService() {
        return netGameService;
    }

    public void setNetGameService(NetGameService netGameService) {
        this.netGameService = netGameService;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }
}
