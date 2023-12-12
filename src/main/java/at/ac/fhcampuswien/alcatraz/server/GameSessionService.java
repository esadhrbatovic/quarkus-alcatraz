package at.ac.fhcampuswien.alcatraz.server;

import at.ac.fhcampuswien.alcatraz.shared.exception.*;
import at.ac.fhcampuswien.alcatraz.shared.model.GameSession;
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

    static final int MAX_PLAYERS = 4;
    boolean gameRunning = false;

    public void register(NetPlayer player) throws RemoteException, AlreadyRegisteredException, GameAlreadyRunningException, FullSessionException {
        checkGameRunning();
        this.validatePlayerName(player.getName());
        this.validatePlayerCount();
        this.serverContext.getSession()
                .add(player);
        updateGameSessionOnClients();
    }

    public void unregister(NetPlayer player) throws RemoteException, GameAlreadyRunningException, UserNotFoundException {
        checkGameRunning();
        findPlayer(player.getName());
        this.serverContext.getSession()
                .remove(player);
        updateGameSessionOnClients();
    }


    public void readyToPlay(NetPlayer player) throws RemoteException, GameAlreadyRunningException, UserNotFoundException {
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

    public void undoReady(NetPlayer player) throws RemoteException, GameAlreadyRunningException, UserNotFoundException {
        checkGameRunning();
        NetPlayer findPlayer = findPlayer(player.getName());
        findPlayer.setReadToPlay(false);
        updateGameSessionOnClients();
    }

    private void validatePlayerName(String name) throws AlreadyRegisteredException {
        for (NetPlayer netPlayer: this.serverContext.getSession()) {
            if (Objects.equals(netPlayer.getName(), name)) {
                throw new AlreadyRegisteredException();
            }
        }
    }

    private void validatePlayerCount() throws FullSessionException {
        if (this.serverContext.getSession()
                .size() >= MAX_PLAYERS) {
            throw new FullSessionException();
        }
    }

    private void checkGameRunning() throws GameAlreadyRunningException {
        if(this.gameRunning){
            throw new GameAlreadyRunningException();
        }
    }

    private NetPlayer findPlayer(String name) throws UserNotFoundException {
        return this.serverContext.getSession()
                .stream()
                .filter(x -> Objects.equals(x.getName(), name))
                .findFirst()
                .orElseThrow(UserNotFoundException::new);
    }

    public void startGame() throws RemoteException, GameAlreadyRunningException, NotEnoughPlayersException {
        checkGameRunning();

        if(this.serverContext.getSession()
                .stream()
                .filter(NetPlayer::isReadToPlay).toList().size()<2){
            throw new NotEnoughPlayersException();
        }

        GameSession<NetPlayer> finalSession = preparePlayersForStart(this.serverContext.gameSession);
        for (NetPlayer p: finalSession) {
            if(p.isReadToPlay()){
                p.getNetGameService().startGame(this.serverContext.gameSession, p);
            }
        }

    }

    private GameSession<NetPlayer> preparePlayersForStart(GameSession<NetPlayer> gameSession){
        for (NetPlayer p : gameSession) {
            p.setId(gameSession.indexOf(p));
        }
        return gameSession;
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
