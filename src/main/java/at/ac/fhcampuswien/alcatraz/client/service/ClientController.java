package at.ac.fhcampuswien.alcatraz.client.service;

import at.ac.fhcampuswien.alcatraz.client.rmi.RmiClient;
import at.ac.fhcampuswien.alcatraz.shared.exception.*;
import at.ac.fhcampuswien.alcatraz.shared.exception.messages.Messages;
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
import javax.swing.*;

@ApplicationScoped
public class ClientController {

    RegistrationService registrationService;

    @Inject
    RmiClient rmiClient;

    @Inject
    NetGameService netGameService;

    JLabel sessionSizeLabel;

    private GameSession<NetPlayer> gameSession;
    private JButton startGameButton;

    public void register(String name) throws RemoteException, AlcatrazException, NotBoundException {
        try {
            this.gameSession = registrationService.loadGameSession();
            int id = gameSession.size();
            Registry registry = RegistryProvider.getOrCreateRegistry(1098);
            UUID remoteIdentifier = UUID.randomUUID();
            registry.bind("NetGameService" + remoteIdentifier, netGameService);
            NetPlayer localPlayer = new NetPlayer(id, name, remoteIdentifier);
            registrationService.register(localPlayer);
        } catch (AlreadyBoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void findCurrentPrimary() throws RemoteException {
        this.registrationService = this.rmiClient.getRegistrationService();
    }

    public void readyToPlay(String name) throws RemoteException, AlcatrazException {
        NetPlayer netPlayer = findPlayerBy(name);
        registrationService.readyToPlay(netPlayer);
    }

    public void logOff(String name) throws RemoteException, AlcatrazException {
        NetPlayer netPlayer = findPlayerBy(name);
        registrationService.logOff(netPlayer);
    }

    public void notReadyToPlay(String name) throws RemoteException, AlcatrazException {
        NetPlayer netPlayer = findPlayerBy(name);
        registrationService.notReadyToPlay(netPlayer);
    }

    private NetPlayer findPlayerBy(String name) throws RemoteException {
        return this.registrationService.loadGameSession().stream()
                .filter(x -> Objects.equals(x.getName(), name))
                .findFirst()
                .orElseThrow(() -> new AlcatrazException(Messages.PLAYER_NOT_FOUND));
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

    public void startGame () throws AlcatrazException {
        try {
            this.registrationService.startGame();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateClientGui(GameSession<NetPlayer> gameSession) throws RemoteException {
        this.gameSession = gameSession;
        int numReadyPlayers = gameSession.stream().filter(NetPlayer::isReadToPlay).toList().size();
        this.sessionSizeLabel.setText("Players ready in Session: " + numReadyPlayers);
        this.startGameButton.setEnabled(numReadyPlayers >= 2);
    }

    public void setSessionSizeLabel(JLabel sessionSizeLabel) {
        this.sessionSizeLabel = sessionSizeLabel;
    }

    public void setStartGameButton(JButton startGameButton) {
        this.startGameButton = startGameButton;
    }
}
