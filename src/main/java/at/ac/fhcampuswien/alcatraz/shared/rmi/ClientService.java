package at.ac.fhcampuswien.alcatraz.shared.rmi;

import at.ac.fhcampuswien.alcatraz.shared.exception.AlcatrazException;
import at.ac.fhcampuswien.alcatraz.shared.model.NetPlayer;
import at.ac.fhcampuswien.alcatraz.shared.model.GameSession;
import at.falb.games.alcatraz.api.IllegalMoveException;
import at.falb.games.alcatraz.api.Player;
import at.falb.games.alcatraz.api.Prisoner;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientService extends Remote {

    void startGame(GameSession<NetPlayer> players, NetPlayer localPlayer) throws RemoteException;

    void closeGame() throws RemoteException;

    void quitGame() throws RemoteException;

    void syncMove(Player player, Prisoner prisoner, int rowOrCol, int row, int col) throws AlcatrazException, IllegalMoveException, RemoteException;

}