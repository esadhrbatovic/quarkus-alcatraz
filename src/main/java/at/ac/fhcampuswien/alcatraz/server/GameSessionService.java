package at.ac.fhcampuswien.alcatraz.server;

import at.ac.fhcampuswien.alcatraz.shared.exception.AlcatrazException;
import at.ac.fhcampuswien.alcatraz.shared.exception.messages.Messages;
import at.ac.fhcampuswien.alcatraz.shared.model.NetPlayer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.io.Serial;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.Objects;

@ApplicationScoped
public class GameSessionService implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    @Inject
    ServerState serverState;

    //TODO: make configurable
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

    //TODO: change
    public void ready(NetPlayer player) throws RemoteException {
        checkGameRunning();
        NetPlayer findPlayer = findPlayer(player.getName());
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
        NetPlayer findPlayer = findPlayer(player.getName());
        findPlayer.setReady(false);
    }

    private void validatePlayerName(String name) {
        this.serverState.getSession()
                .forEach(remotePlayer -> {
                    if (Objects.equals(remotePlayer.getName(), name)) {
                        throw new AlcatrazException(Messages.PLAYER_EXISTS);
                    }
                });
    }

    private void validatePlayerCount() {
        if (this.serverState.getSession()
                .size() >= MAX_PLAYERS) {
            throw new AlcatrazException(Messages.SESSION_FULL);
        }
    }

    private void checkGameRunning() {
    }

    private NetPlayer findPlayer(String name) {
        return this.serverState.getSession()
                .stream()
                .filter(x -> Objects.equals(x.getName(), name))
                .findFirst()
                .orElseThrow(() -> new AlcatrazException(Messages.PLAYER_NOT_FOUND));
    }

}
