package at.ac.fhcampuswien.alcatraz.server.spread.service;

import at.ac.fhcampuswien.alcatraz.server.rmi.RmiServer;
import at.ac.fhcampuswien.alcatraz.server.ServerContext;
import at.ac.fhcampuswien.alcatraz.shared.exception.messages.Messages;
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
import java.util.Arrays;

@Singleton
public class SpreadHandler implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private static final Logger log = Logger.getLogger(SpreadHandler.class);

    @Inject
    ServerContext serverContext;

    @Inject
    RmiServer rmiServer;

    public void handleMembershipMessage(SpreadConnection connection, SpreadGroup group, SpreadMessage spreadMessage) {
        log.info(Messages.SPREAD_MEMBERSHIP_MESSAGE_RECIEVED);

        if (spreadMessage.getMembershipInfo().isCausedByDisconnect()) {
            handleDisconnect(connection, group, spreadMessage);
        } else if (spreadMessage.getMembershipInfo().isCausedByLeave()) {
            handleLeave(connection, group, spreadMessage);
        } else if (spreadMessage.getMembershipInfo().isCausedByJoin()) {
            handleJoin(connection, group, spreadMessage);
        } else {
            log.debug(Messages.UNKNOWN_MEMBERSHIP_MESSAGE);
        }
    }

    private void handleDisconnect(SpreadConnection connection, SpreadGroup group, SpreadMessage spreadMessage) {
        log.error(Messages.SPREAD_DISCONNECT_DETECTED);
        determinePrimaryOrBackup(connection, group, spreadMessage, true);
    }

    private void handleLeave(SpreadConnection connection, SpreadGroup group, SpreadMessage spreadMessage) {
        log.info(Messages.SPREAD_LEAVE_DETECTED);
        determinePrimaryOrBackup(connection, group, spreadMessage, true);
    }

    private void handleJoin(SpreadConnection connection, SpreadGroup group, SpreadMessage spreadMessage) {
        log.info(Messages.SPREAD_JOIN_DETECTED);
        determinePrimaryOrBackup(connection, group, spreadMessage, false);
        log.info("current state of server="+this.serverContext.toString());
    }

    public void syncGameSessionWithGroup(SpreadConnection connection, SpreadGroup group, GameSession<NetPlayer> gameSession) {
        try {
            log.info(Messages.SENDING_GAMESESSION);
            SpreadMessage message = new SpreadMessage();
            message.setObject(gameSession);
            message.setType((short) 1); // sync
            message.addGroup(group);
            message.setSafe();
            connection.multicast(message);
        } catch (SpreadException e) {
            log.error(Messages.ERROR_SENDING_SESSION, e);
        }
    }

    public void handleSyncSession(GameSession<NetPlayer> gameSession) {
        this.serverContext.setSession(gameSession);
    }

    private void determinePrimaryOrBackup(SpreadConnection connection, SpreadGroup group, SpreadMessage spreadMessage, boolean someoneLeft) {
        if (isOnlyMemberInGroup(spreadMessage)) {
            setAsPrimary();
        } else {
            if (this.serverContext.isPrimary() && !someoneLeft) {
                syncSessionToGroup(connection, group);
            }
            determineRoleBasedOnHighestId(spreadMessage);
        }
        logServerRole();
        if (this.serverContext.isPrimary()) {
            registerRMIEndpoint();
        }
    }

    private boolean isOnlyMemberInGroup(SpreadMessage spreadMessage) {
        return spreadMessage.getMembershipInfo().getMembers().length == 1;
    }

    private void setAsPrimary() {
        this.serverContext.setPrimary(true);
    }

    private void determineRoleBasedOnHighestId(SpreadMessage spreadMessage) {
        int highestId = getHighestIdFromMembers(spreadMessage);
        boolean isPrimary = this.serverContext.getServerId() == highestId;
        this.serverContext.setPrimary(isPrimary);
    }

    private int getHighestIdFromMembers(SpreadMessage spreadMessage) {
        return Arrays.stream(spreadMessage.getMembershipInfo().getMembers())
                .mapToInt(item -> getIdOfMember(item.toString()))
                .max()
                .orElseThrow(() -> new RuntimeException(Messages.ERROR_DETERMINING_SERVER_ROLE));
    }

    private void logServerRole() {
        log.info(this.serverContext.isPrimary() ? "SERVER ROLE: PRIMARY" : "SERVER ROLE: BACKUP");
    }

    private void registerRMIEndpoint() {
        this.rmiServer.registerRMIEndpoint();
    }

    private void syncSessionToGroup(SpreadConnection connection, SpreadGroup group) {
        syncGameSessionWithGroup(connection, group, this.serverContext.getSession());
    }

    private int getIdOfMember(String name) {
        return Integer.parseInt(name.split("#")[1]);
    }
}