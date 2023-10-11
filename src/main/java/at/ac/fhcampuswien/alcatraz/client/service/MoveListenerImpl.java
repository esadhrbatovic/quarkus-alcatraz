package at.ac.fhcampuswien.alcatraz.client.service;


import at.ac.fhcampuswien.alcatraz.shared.model.CustomMoveListener;
import at.ac.fhcampuswien.alcatraz.shared.model.NetPlayer;
import at.falb.games.alcatraz.api.IllegalMoveException;
import at.falb.games.alcatraz.api.Player;
import at.falb.games.alcatraz.api.Prisoner;

import org.jboss.logging.Logger;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MoveListenerImpl implements CustomMoveListener {

    ClientUiService clientUiService;

    private static final Logger log = Logger.getLogger(MoveListenerImpl.class);

    public static final int TIME_TO_RETRY_CONNECTION = 100;
    public static final int MAX_REMOTE_EXCEPTIONS = 5;

    public MoveListenerImpl(ClientUiService clientUiService) {
        this.clientUiService = clientUiService;
    }

    @Override
    public void moveDone(Player player, Prisoner prisoner, int rowOrCol, int row, int col) {
        List<NetPlayer> otherPlayers = this.clientUiService
                .getSession()
                .stream()
                .filter(remotePlayer -> remotePlayer.getId() != player.getId()).toList();

        for (NetPlayer rp: otherPlayers) {
            log.error("syncing move for player " + rp.getName());
            boolean connectionWithoutException = false;
            int countRemoteExceptions = 0;
            while (!connectionWithoutException && countRemoteExceptions < MAX_REMOTE_EXCEPTIONS) {
                try {
                    rp.getClientService().syncMove(player, prisoner, rowOrCol, row, col);
                    connectionWithoutException = true;

                } catch (IllegalMoveException | RemoteException e) {
                    countRemoteExceptions++;
                    handleRMIException(countRemoteExceptions);
                    log.error("Move Done is not possible.");
                }
            }
            if (!connectionWithoutException) {
                handleQuitGame();
            }
        }
    }
    public static void handleRMIException(int countRemoteExceptions) {
        System.out.println("The connection between players is not possible. System tried" + countRemoteExceptions + "times.");
        try {
            Thread.sleep(TIME_TO_RETRY_CONNECTION);

        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }

    }

    private void handleQuitGame() {
        this.clientUiService
                .getSession()
                .forEach(remotePlayer -> {
                    try {
                        remotePlayer.getClientService().quitGame();
                    } catch (RemoteException e) {
                        log.error("Aborting the game was not possible on all players.");
                    }
                });
    }

    @Override
    public void gameWon(Player player) {
        // todo empty lobby, restart
        new Timer().schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        clientUiService
                                .getSession()
                                .forEach(remotePlayer -> {
                                    try {
                                        remotePlayer.getClientService().closeGame();
                                    } catch (RemoteException e) {
                                        throw new RuntimeException("The synchronization of closing the games on all players failed.");
                                    }
                                });
                    }
                },
                5000
        );
    }
}
