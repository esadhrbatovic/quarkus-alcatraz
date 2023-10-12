package at.ac.fhcampuswien.alcatraz.server.spread.service;

import at.ac.fhcampuswien.alcatraz.server.spread.RmiServer;
import at.ac.fhcampuswien.alcatraz.server.spread.ServerState;
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
        log.info("------------------------------------");

        try {
            if (spreadMessage.getMembershipInfo().isCausedByDisconnect()) {
                /*
                  Handles Disconnect of a Registration Server
                  Unintended Disconnect/Network Timeout?
                  Determines if primary is gone -> Determine new primary
                 */
                log.error("Someone disconnected from the Spread Group!");
                votePrimaryAndSync(connection, group, spreadMessage, true);
            }
            if (spreadMessage.getMembershipInfo().isCausedByLeave()) {
                /*
                  Handles Leave of a Registration Server
                  Intended Leave of the Group
                  If primary gone -> Determine new primary!
                 */
                log.info("Someone left the Spread Group!");
                log.info("Checking primary/backup state of Server...");
                votePrimaryAndSync(connection, group, spreadMessage, true);
            }
            if (!spreadMessage.getMembershipInfo().isCausedByJoin()) {
                log.debug("Got Membership Message, which cannot be handled!");
                return;
            }
            /*
              Handles join of a Registration Server to the Spread Group
              Determine the primary/backup Server Role here
             */
            log.info("Someone joined the Spread Group!");
            log.info("Determining if primary or backup");
            votePrimaryAndSync(connection, group, spreadMessage, false);

        } catch (RuntimeException e) {
            log.error(e.getMessage(), e);
        }
        log.info("------------------------------------");
    }

    @Override
    public void syncSession(SpreadConnection connection, SpreadGroup group, GameSession<NetPlayer> gameSession) {
        try {
            log.info("Sending lobby State to all");

            SpreadMessage message = new SpreadMessage();
            message.setObject(gameSession);
            message.setType((short)1); // sync
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

    private void votePrimaryAndSync(SpreadConnection connection, SpreadGroup group, SpreadMessage spreadMessage, boolean someoneLeft) {
        // Check if any members in Group
        if (spreadMessage.getMembershipInfo().getMembers().length == 1) {
            // No one else except you in group and nothing to sync
            this.serverState.setPrimary(true);
        } else {
            if (this.serverState.isPrimary() && !someoneLeft) {
                // Before voting - sync Lobby State to all again (also for the newly joined one)
                //log.info("As current Primary: Syncing Lobby to newly joined Server.");
                syncSession(connection, group, this.serverState.getSession());
            }
            // Now vote for new Primary based on highest ID
            // Could never throw exception since there is always 1 Member in the group -> Should not come up!
            var maxId = Arrays.stream(spreadMessage.getMembershipInfo().getMembers())
                    .mapToInt(item -> getIdOfMember(item.toString()))
                    .max()
                    .orElseThrow(() -> new RuntimeException("Could not determine primary/backup state of the new Server"));
            this.serverState.setPrimary(this.serverState.getServerId() == maxId);
        }


        log.info(this.serverState.isPrimary() ? "[!] I am the Primary Server [!]" : "[!] I am a Backup Server! [!]");
        if(this.serverState.isPrimary()){
            this.rmiServer.registerRMIEndpoint();
        }
    }

    private int getIdOfMember(String name) {
        return Integer.parseInt(name.split("#")[1]);
    }
}
