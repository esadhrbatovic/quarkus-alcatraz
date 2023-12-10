package at.ac.fhcampuswien.alcatraz.client.service;
import at.ac.fhcampuswien.alcatraz.shared.model.NetPlayer;
import at.falb.games.alcatraz.api.IllegalMoveException;
import at.falb.games.alcatraz.api.MoveListener;
import at.falb.games.alcatraz.api.Player;
import at.falb.games.alcatraz.api.Prisoner;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.rmi.RemoteException;
import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class MoveListenerImpl implements MoveListener {

    @Inject
    ClientController clientController;

    private static final Logger log = Logger.getLogger(MoveListenerImpl.class);


    @ConfigProperty(name = "fault-tolerance-rmi-wait-time")
    int TIME_TO_RETRY_CONNECTION;

    @ConfigProperty(name = "fault-tolerance-rmi-max-retries")
    int MAX_REMOTE_EXCEPTIONS;

    @Override
    //refactor this method
    public void moveDone(Player player, Prisoner prisoner, int rowOrCol, int row, int col) {
        List<NetPlayer> otherPlayers = this.clientController
                .getGameSession()
                .stream()
                .filter(netPlayer -> netPlayer.getId() != player.getId()).toList();

        for (NetPlayer rp : otherPlayers) {
            log.error("trying to send move to player " + rp.getName());
            boolean connectionWithoutException = false;
            int countRemoteExceptions = 0;
            while (!connectionWithoutException && countRemoteExceptions < MAX_REMOTE_EXCEPTIONS) {
                try {
                    rp.getNetGameService().makeMove(player, prisoner, rowOrCol, row, col);
                    connectionWithoutException = true;

                } catch (IllegalMoveException | RemoteException e) {
                    countRemoteExceptions++;
                    handleRMIException(countRemoteExceptions);
                    log.error("sending move to " + rp.getName() + " is not possible");
                }
            }
            if (!connectionWithoutException) {
                quitAllWorkingClients(rp.getId());
            }
        }
    }

    public void handleRMIException(int countRemoteExceptions) {
        System.out.println("The connection between players is not possible. System tried" + countRemoteExceptions + "times.");
        try {
            Thread.sleep(TIME_TO_RETRY_CONNECTION);
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }

    @Override
    public void gameWon(Player player) {
        log.info("Player " + player.getName() + " won!");
        ClientController.closeThisClient();
    }

    private void quitAllWorkingClients(Integer faultyPlayerId){
        this.clientController
                .getGameSession()
                .stream().filter(p->p.getId()!=this.clientController.getLocalPlayerId() && p.getId() != faultyPlayerId)
                .forEach(netPlayer -> {
                    try {
                        netPlayer.getNetGameService().closeGame();
                    } catch (RemoteException e) {
                        log.error("Aborting the game was not possible on all players.");
                    }
                });
        ClientController.closeThisClient();
    }
}
