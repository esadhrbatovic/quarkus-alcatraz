package at.ac.fhcampuswien.alcatraz.client.service;

import at.ac.fhcampuswien.alcatraz.client.ClientGui;
import at.ac.fhcampuswien.alcatraz.client.rmi.RmiClient;
import at.ac.fhcampuswien.alcatraz.shared.exception.*;
import at.ac.fhcampuswien.alcatraz.shared.model.NetPlayer;
import at.ac.fhcampuswien.alcatraz.shared.model.GameSession;
import at.ac.fhcampuswien.alcatraz.shared.rmi.NetGameService;
import at.ac.fhcampuswien.alcatraz.shared.rmi.RegistrationService;
import at.ac.fhcampuswien.alcatraz.shared.rmi.RegistryProvider;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.Objects;
import java.util.UUID;

@ApplicationScoped
public class ClientController {

    RegistrationService registrationService;

    @Inject
    RmiClient rmiClient;

    @Inject
    NetGameService netGameService;

    private GameSession<NetPlayer> gameSession;

    private Integer localPlayerId;
    private ClientGui clientGui;

    public void register(String name) throws RemoteException, NotBoundException, AlreadyRegisteredException, FullSessionException, GameAlreadyRunningException {
        try {
            this.gameSession = registrationService.loadGameSession();
            Registry registry = RegistryProvider.getOrCreateRegistry(1098);
            UUID rmiUUID = UUID.randomUUID();
            registry.bind("NetGameService" + rmiUUID, netGameService);
            NetPlayer localPlayer = new NetPlayer(-1, name, rmiUUID);
            this.localPlayerId = -1;
            registrationService.register(localPlayer);
        } catch (AlreadyBoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void findCurrentPrimary() throws RemoteException {
        this.registrationService = this.rmiClient.getRegistrationService();
    }

    public void readyToPlay(String name) throws RemoteException, UserNotFoundException, GameAlreadyRunningException {
        NetPlayer netPlayer = findPlayerBy(name);
        registrationService.readyToPlay(netPlayer);
    }

    public void logOff(String name) throws RemoteException, UserNotFoundException, GameAlreadyRunningException {
        NetPlayer netPlayer = findPlayerBy(name);
        registrationService.logOff(netPlayer);
        this.localPlayerId = null;
    }

    public void notReadyToPlay(String name) throws RemoteException, UserNotFoundException, GameAlreadyRunningException {
        NetPlayer netPlayer = findPlayerBy(name);
        registrationService.notReadyToPlay(netPlayer);
    }

    private NetPlayer findPlayerBy(String name) throws RemoteException, UserNotFoundException {
        return this.registrationService.loadGameSession().stream()
                .filter(x -> Objects.equals(x.getName(), name))
                .findFirst()
                .orElseThrow(UserNotFoundException::new);
    }

    public RegistrationService getRegistrationService() {
        return registrationService;
    }


    public GameSession<NetPlayer> getGameSession() {
        return this.gameSession;
    }

    public void setGameSession(GameSession<NetPlayer> gameSession) {
        this.gameSession = gameSession;
    }

    public void startGame() throws NotEnoughPlayersException, GameAlreadyRunningException {
        try {
            this.registrationService.startGame();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateClientGui(GameSession<NetPlayer> gameSession) {
        this.gameSession = gameSession;
        int numReadyPlayers = gameSession.stream().filter(NetPlayer::isReadToPlay).toList().size();
        this.clientGui.sessionSizeLabel.setText("Players ready in Session: " + numReadyPlayers + " / " + gameSession.size());
        this.clientGui.startGameButton.setEnabled(numReadyPlayers >= 2);
    }

    public Integer getLocalPlayerId() {
        return localPlayerId;
    }

    public void setLocalPlayerId(Integer id) {
        this.localPlayerId = id;
    }


    public static void closeThisClient() {
        Thread closeClientThread = new Thread(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.exit(0);
        });
        closeClientThread.start();
    }

    public void setGui(ClientGui clientGui) {
        this.clientGui = clientGui;
    }

    public ClientGui getGui(){
        return this.clientGui;
    }
}
