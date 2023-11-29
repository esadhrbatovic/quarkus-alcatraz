package at.ac.fhcampuswien.alcatraz.client.service;

import at.ac.fhcampuswien.alcatraz.shared.exception.AlcatrazException;
import at.ac.fhcampuswien.alcatraz.shared.rmi.NetGameService;
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
public class NetGameServiceImpl extends UnicastRemoteObject implements NetGameService, Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Inject
    MoveListener moveListener;

    @Inject
    ClientController clientController;

    AlcatrazBean alcatraz = new AlcatrazBean();

    protected NetGameServiceImpl() throws RemoteException {
        super();
    }

    @Override
    public void startGame(GameSession<NetPlayer> gameSession, NetPlayer localPlayer) throws RemoteException {
        clientController.setGameSession(gameSession);
        this.alcatraz.init(gameSession.size(), localPlayer.getId());
        gameSession.forEach(netPlayer -> this.alcatraz.getPlayer(netPlayer.getId())
                .setName(netPlayer.getName()));
        this.alcatraz.addMoveListener(moveListener);
        this.alcatraz.showWindow();
        this.alcatraz.start();
    }

    @Override
    public void closeGame() throws RemoteException {
        alcatraz.closeWindow();
        alcatraz.disposeWindow();
        Quarkus.waitForExit();
    }

    @Override
    public void makeMove(Player player, Prisoner prisoner, int rowOrCol, int row, int col) throws AlcatrazException, IllegalMoveException, RemoteException {
        this.alcatraz.doMove(player, prisoner, rowOrCol, row, col);
    }

    @Override
    public void printLobby(GameSession<NetPlayer> players) throws RemoteException {
        this.clientController.updateClientGui(players);
        System.out.println("Current Session: " + players.toString());
    }


}
