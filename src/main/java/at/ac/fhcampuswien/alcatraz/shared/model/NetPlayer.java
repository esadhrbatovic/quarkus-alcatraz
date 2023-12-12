package at.ac.fhcampuswien.alcatraz.shared.model;

import at.ac.fhcampuswien.alcatraz.shared.rmi.NetGameService;
import at.falb.games.alcatraz.api.Player;

import java.io.Serializable;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.UUID;

public class NetPlayer extends Player implements Serializable {

    boolean readToPlay;

    NetGameService netGameService;

    public NetPlayer(int id, String name, UUID rmiId) throws RemoteException, NotBoundException {
        super(id);
        try {
            this.netGameService = (NetGameService) LocateRegistry.getRegistry(1098)
                    .lookup("NetGameService" + rmiId);
        } catch (NotBoundException e) {
            throw new NotBoundException("Could not create RMI Connection.");
        }
        this.setName(name);
    }

    public NetGameService getNetGameService() {
        return netGameService;
    }

    public boolean isReadToPlay() {
        return readToPlay;
    }

    public void setReadToPlay(boolean readToPlay) {
        this.readToPlay = readToPlay;
    }

    @Override
    public boolean equals(Object o) {

        if (o instanceof Player) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            return ((NetPlayer) o).getName().equals(this.getName());
        }
        return false;
    }

}
