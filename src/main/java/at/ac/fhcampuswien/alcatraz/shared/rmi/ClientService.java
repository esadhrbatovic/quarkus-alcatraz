package at.ac.fhcampuswien.alcatraz.shared.rmi;

import at.ac.fhcampuswien.alcatraz.shared.exception.TimeOutException;
import at.ac.fhcampuswien.alcatraz.shared.model.NetPlayer;
import at.ac.fhcampuswien.alcatraz.shared.model.Session;
import at.falb.games.alcatraz.api.IllegalMoveException;
import at.falb.games.alcatraz.api.Player;
import at.falb.games.alcatraz.api.Prisoner;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientService extends Remote {

    void makeMove(Player player, Prisoner prisoner, int rowOrCol, int row, int col) throws RemoteException, IllegalMoveException;

    void startGame(Session<NetPlayer> players, NetPlayer localPlayer) throws RemoteException;

    void closeGame() throws RemoteException;

    void quitGame() throws RemoteException;

    void syncMove(Player player, Prisoner prisoner, int rowOrCol, int row, int col) throws TimeOutException, IllegalMoveException, RemoteException;



}