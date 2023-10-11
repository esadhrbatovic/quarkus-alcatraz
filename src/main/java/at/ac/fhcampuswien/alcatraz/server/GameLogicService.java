package at.ac.fhcampuswien.alcatraz.server;
import at.ac.fhcampuswien.alcatraz.server.spread.ServerState;
import at.ac.fhcampuswien.alcatraz.shared.exception.DuplicatePlayerException;
import at.ac.fhcampuswien.alcatraz.shared.exception.FullLobbyException;
import at.ac.fhcampuswien.alcatraz.shared.exception.PlayerNotFoundException;
import at.ac.fhcampuswien.alcatraz.shared.model.NetPlayer;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.io.Serial;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.Objects;

@Singleton
public class GameLogicService implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @Inject
    ServerState serverState;
    static int MIN_PLAYERS = 2;
    static int MAX_PLAYERS = 4;
    boolean gameRunning = false;

    public void register(NetPlayer player) {
        checkGameRunning();
        this.validatePlayerName(player.getName());
        this.validatePlayerCount();
        this.serverState.getSession()
                .add(player);
    }

    public void unregister(NetPlayer player) {
        checkGameRunning();
        this.serverState.getSession()
                .remove(player);
    }

    public void ready(NetPlayer player) throws RemoteException {
        checkGameRunning();
        NetPlayer findPlayer = findPlayerBy(player.getName());
        findPlayer.setReady(true);
        if (this.serverState.getSession()
                .stream()
                .allMatch(NetPlayer::isReady) && this.serverState.getSession()
                .size() >= MIN_PLAYERS) {
            for (NetPlayer remotePlayer : this.serverState.getSession()) {
                remotePlayer.getClientService()
                        .startGame(this.serverState.getSession(), remotePlayer);
                this.gameRunning = true;
            }
        }
    }

    public void undoReady(NetPlayer player) {
        checkGameRunning();

        NetPlayer findPlayer = findPlayerBy(player.getName());

        findPlayer.setReady(false);
    }

    private void validatePlayerName(String name)  {

        this.serverState.getSession()
                .forEach(remotePlayer -> {
                    if (Objects.equals(remotePlayer.getName(), name)) {
                        throw new DuplicatePlayerException("This player name is already taken.");
                    }
                });
    }

    private void validatePlayerCount() {
        if (this.serverState.getSession()
                .size() >= MAX_PLAYERS) {
            throw new FullLobbyException("The lobby is already full.");
        }
    }
    private void checkGameRunning() {
    }

    private NetPlayer findPlayerBy(String name) {
        return this.serverState.getSession()
                .stream()
                .filter(x -> Objects.equals(x.getName(), name))
                .findFirst()
                .orElseThrow(() -> new PlayerNotFoundException("The player could not be found."));
    }

}
