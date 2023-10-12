package at.ac.fhcampuswien.alcatraz.client.service;

import at.ac.fhcampuswien.alcatraz.shared.exception.TimeOutException;
import at.ac.fhcampuswien.alcatraz.shared.rmi.ClientService;
import at.ac.fhcampuswien.alcatraz.shared.model.AlcatrazBean;
import at.ac.fhcampuswien.alcatraz.shared.model.NetPlayer;
import at.ac.fhcampuswien.alcatraz.shared.model.GameSession;
import at.falb.games.alcatraz.api.IllegalMoveException;
import at.falb.games.alcatraz.api.MoveListener;
import at.falb.games.alcatraz.api.Player;
import at.falb.games.alcatraz.api.Prisoner;
import io.quarkus.runtime.Quarkus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.io.Serial;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

@ApplicationScoped
public class ClientServiceImpl extends UnicastRemoteObject implements ClientService, Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Inject
    MoveListener moveListener;

    AlcatrazBean alcatraz = new AlcatrazBean();

    protected ClientServiceImpl() throws RemoteException {
        super();
    }

    @Override
    public void makeMove(Player player, Prisoner prisoner, int rowOrCol, int row, int col) throws RemoteException, IllegalMoveException {
        this.alcatraz.doMove(player, prisoner, rowOrCol, row, col);
    }

    @Override
    public void startGame(GameSession<NetPlayer> players, NetPlayer localPlayer) throws RemoteException {
        this.alcatraz.init(players.size(), localPlayer.getId());
        players.forEach(remotePlayer -> this.alcatraz.getPlayer(remotePlayer.getId())
                .setName(remotePlayer.getName()));
        this.alcatraz.addMoveListener(moveListener);
        this.alcatraz.showWindow();
        this.alcatraz.start();
    }

    @Override
    public void closeGame() throws RemoteException {
        alcatraz.closeWindow();
        alcatraz.disposeWindow();
    }

    @Override
    public void quitGame() throws RemoteException {
        Quarkus.waitForExit();
    }

    @Override
    public void syncMove(Player player, Prisoner prisoner, int rowOrCol, int row, int col) throws TimeOutException, IllegalMoveException, RemoteException {
        this.alcatraz.doMove(player, prisoner, rowOrCol, row, col);
    }


}
