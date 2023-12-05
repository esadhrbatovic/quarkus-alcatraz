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
    ServerContext serverContext;

    static int MAX_PLAYERS = 4;
    boolean gameRunning = false;

    public void register(NetPlayer player) throws RemoteException {
        checkGameRunning();
        this.validatePlayerName(player.getName());
        this.validatePlayerCount();
        this.serverContext.getSession()
                .add(player);
        updateGameSessionOnClients();
    }

    public void unregister(NetPlayer player) throws RemoteException {
        checkGameRunning();
        this.serverContext.getSession()
                .remove(player);
        updateGameSessionOnClients();
    }

    public void readyToPlay(NetPlayer player) throws RemoteException {
        checkGameRunning();
        NetPlayer findPlayer = findPlayer(player.getName());
        findPlayer.setReadToPlay(true);
        updateGameSessionOnClients();
        if (this.serverContext.getSession()
                .stream()
                .allMatch(NetPlayer::isReadToPlay) && this.serverContext.getSession()
                .size() == MAX_PLAYERS) {
            for (NetPlayer netPlayer : this.serverContext.getSession()) {
                netPlayer.getNetGameService()
                        .startGame(this.serverContext.getSession(), netPlayer);
                this.gameRunning = true;
            }
        }
    }

    public void undoReady(NetPlayer player) throws RemoteException {
        checkGameRunning();
        NetPlayer findPlayer = findPlayer(player.getName());
        findPlayer.setReadToPlay(false);
        updateGameSessionOnClients();
    }

    private void validatePlayerName(String name) {
        this.serverContext.getSession()
                .forEach(netPlayer -> {
                    if (Objects.equals(netPlayer.getName(), name)) {
                        throw new AlcatrazException(Messages.PLAYER_EXISTS);
                    }
                });
    }

    private void validatePlayerCount() {
        if (this.serverContext.getSession()
                .size() >= MAX_PLAYERS) {
            throw new AlcatrazException(Messages.SESSION_FULL);
        }
    }

    private void checkGameRunning() {
    }

    private NetPlayer findPlayer(String name) {
        return this.serverContext.getSession()
                .stream()
                .filter(x -> Objects.equals(x.getName(), name))
                .findFirst()
                .orElseThrow(() -> new AlcatrazException(Messages.PLAYER_NOT_FOUND));
    }

    public void startGame() throws RemoteException {
        checkGameRunning();

        if(this.serverContext.getSession()
                .stream()
                .filter(NetPlayer::isReadToPlay).toList().size()<2){
            throw new AlcatrazException("Not enough players ready to play");
        }

        for (NetPlayer p: this.serverContext.getSession()) {
            if(p.isReadToPlay()){
                p.getNetGameService().startGame(this.serverContext.gameSession, p);
            }
        }

    }

    private void updateGameSessionOnClients() throws RemoteException {
        if(!this.serverContext.isPrimary){
            return;
        }

        for (NetPlayer p : this.serverContext.getSession()) {
            p.getNetGameService().printLobby(this.serverContext.gameSession);
        }
    }
}
