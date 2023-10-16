package at.ac.fhcampuswien.alcatraz.server.spread.service;

import at.ac.fhcampuswien.alcatraz.server.rmi.RmiServer;
import at.ac.fhcampuswien.alcatraz.server.ServerState;
import at.ac.fhcampuswien.alcatraz.shared.model.NetPlayer;
import at.ac.fhcampuswien.alcatraz.shared.model.GameSession;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.jboss.logging.Logger;
import spread.SpreadConnection;
import spread.SpreadException;
import spread.SpreadGroup;
import spread.SpreadMessage;

import java.io.Serial;
import java.io.Serializable;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.util.Arrays;

@Singleton
public class SpreadMessageHandlerImpl implements SpreadMessageHandler, Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private static final Logger log = Logger.getLogger(SpreadMessageHandlerImpl.class);

    @Inject
    ServerState serverState;

    @Inject
    RmiServer rmiServer;

    @Override
    public void handleMembershipMessage(SpreadConnection connection, SpreadGroup group, SpreadMessage spreadMessage) throws RemoteException, AlreadyBoundException {
        log.info("Handling membership message...");

        if (spreadMessage.getMembershipInfo().isCausedByDisconnect()) {
            handleDisconnect(connection, group, spreadMessage);
        } else if (spreadMessage.getMembershipInfo().isCausedByLeave()) {
            handleLeave(connection, group, spreadMessage);
        } else if (spreadMessage.getMembershipInfo().isCausedByJoin()) {
            handleJoin(connection, group, spreadMessage);
        } else {
            log.debug("Unhandled membership message type.");
        }
    }

    private void handleDisconnect(SpreadConnection connection, SpreadGroup group, SpreadMessage spreadMessage) {
        log.error("Someone disconnected from the Spread Group!");
        determinePrimaryOrBackup(connection, group, spreadMessage, true);
    }

    private void handleLeave(SpreadConnection connection, SpreadGroup group, SpreadMessage spreadMessage) {
        log.info("Someone left the Spread Group!");
        determinePrimaryOrBackup(connection, group, spreadMessage, true);
    }

    private void handleJoin(SpreadConnection connection, SpreadGroup group, SpreadMessage spreadMessage) {
        log.info("Someone joined the Spread Group!");
        determinePrimaryOrBackup(connection, group, spreadMessage, false);
    }

    public void syncGameSessionWithGroup(SpreadConnection connection, SpreadGroup group, GameSession<NetPlayer> gameSession) {
        try {
            log.info("Sending GameSession to all");
            SpreadMessage message = new SpreadMessage();
            message.setObject(gameSession);
            message.setType((short) 1); // sync
            message.addGroup(group);
            message.setSafe();
            connection.multicast(message);
        } catch (SpreadException e) {
            log.error("Could not send object to Spread Group...", e);
        }
    }

    @Override
    public void handleSyncSession(GameSession<NetPlayer> gameSession) {
        this.serverState.setSession(gameSession);
    }

    private void determinePrimaryOrBackup(SpreadConnection connection, SpreadGroup group, SpreadMessage spreadMessage, boolean someoneLeft) {
        if (isOnlyMemberInGroup(spreadMessage)) {
            setAsPrimary();
        } else {
            determineRoleBasedOnHighestId(spreadMessage);
            if (this.serverState.isPrimary() && !someoneLeft) {
                syncSessionToGroup(connection, group);
            }
        }
        logServerRole();
        if (this.serverState.isPrimary()) {
            registerRMIEndpoint();
        }
    }

    private boolean isOnlyMemberInGroup(SpreadMessage spreadMessage) {
        return spreadMessage.getMembershipInfo().getMembers().length == 1;
    }

    private void setAsPrimary() {
        this.serverState.setPrimary(true);
    }

    private void determineRoleBasedOnHighestId(SpreadMessage spreadMessage) {
        int highestId = getHighestIdFromMembers(spreadMessage);
        boolean isPrimary = this.serverState.getServerId() == highestId;
        this.serverState.setPrimary(isPrimary);
    }

    private int getHighestIdFromMembers(SpreadMessage spreadMessage) {
        return Arrays.stream(spreadMessage.getMembershipInfo().getMembers())
                .mapToInt(item -> getIdOfMember(item.toString()))
                .max()
                .orElseThrow(() -> new RuntimeException("Could not determine primary/backup state of the new Server"));
    }

    private void logServerRole() {
        log.info(this.serverState.isPrimary() ? "[!] I am the Primary Server [!]" : "[!] I am a Backup Server! [!]");
    }

    private void registerRMIEndpoint() {
        this.rmiServer.registerRMIEndpoint();
    }

    private void syncSessionToGroup(SpreadConnection connection, SpreadGroup group) {
        syncGameSessionWithGroup(connection, group, this.serverState.getSession());
    }

    private int getIdOfMember(String name) {
        return Integer.parseInt(name.split("#")[1]);
    }
}