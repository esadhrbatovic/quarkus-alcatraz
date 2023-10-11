package at.ac.fhcampuswien.alcatraz.server.spread;



import at.ac.fhcampuswien.alcatraz.server.spread.service.SpreadMessageHandler;
import at.ac.fhcampuswien.alcatraz.shared.model.NetPlayer;
import at.ac.fhcampuswien.alcatraz.shared.model.Session;
import at.ac.fhcampuswien.alcatraz.server.spread.enums.SpreadMessageType;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.Serial;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.util.Objects;
import java.util.Random;

import org.jboss.logging.Logger;
import spread.*;

@Singleton
public class SpreadCommunicator implements AdvancedMessageListener, Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @Inject
    SpreadMessageHandler spreadMessageHandler;
    ServerState serverState;
    SpreadGroupBean spreadGroup;
    SpreadConnectionBean spreadConnection;
    @ConfigProperty(name = "spread-server")
    String spreadServer;

    private static final Logger log = Logger.getLogger(SpreadCommunicator.class);

    public SpreadCommunicator( SpreadMessageHandler spreadMessageHandler, ServerState serverState, @ConfigProperty(name = "spread-server") String spreadServer) throws AlreadyBoundException, RemoteException {
        this.spreadMessageHandler = spreadMessageHandler;
        this.spreadServer = spreadServer;
        this.serverState = serverState;
        this.spreadConnection = spreadConnection();
        this.spreadGroup = spreadGroup();
    }

    @Override
    public void regularMessageReceived(SpreadMessage spreadMessage) {
        // TODO: Implement Message Received Listener
        log.info("Received Message");
        if (Objects.equals(SpreadMessageType.valueOfLabel(spreadMessage.getType()), SpreadMessageType.SYNC) /* && spreadMessage.getObject() instanceof Session session */) {
            log.info("Got Lobby Object to sync!");
            log.info(this.serverState.getSession().toString());
            spreadMessageHandler.handleSyncSession(this.serverState.getSession());
        }
    }

    @Override
    public void membershipMessageReceived(SpreadMessage spreadMessage) {
        try {
            spreadMessageHandler.handleMembershipMessage(spreadConnection, spreadGroup, spreadMessage);
        } catch (RemoteException | AlreadyBoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendMessageToSpread(Session<NetPlayer> lobby) {
        spreadMessageHandler.syncSession(spreadConnection, spreadGroup, lobby);
    }

    public SpreadConnectionBean spreadConnection() {
        SpreadConnectionBean connection = new SpreadConnectionBean();

        try {
            Random random = new Random();
            int privateName = random.nextInt(99999);
            InetAddress hostName = InetAddress.getByName(spreadServer);
            int port = 0;
            // Priority Connection or not -> In our case true
            boolean priority = true;
            //  Set group_membership flag to true so membership messages can be sent after connection
            boolean groupMembership = true;

            connection.connect(hostName, port, Integer.toString(privateName), priority, groupMembership);
            log.info("creating spreadConnection " + Integer.toString(privateName) + " " + priority + " " + groupMembership);
            this.serverState.setServerId(privateName);
        } catch (UnknownHostException | SpreadException e) {
            log.error("Could not establish connection with Spread. Please start spread first and make sure it can be resolved, then restart the game server.");
            shutdownServer();
        }
        connection.add(this);
        return connection;
    }

    public void shutdownServer() {
        System.exit(0);
    }

    public SpreadGroupBean spreadGroup() {
        SpreadGroupBean spreadGroup = new SpreadGroupBean();

        try {
            spreadGroup.join(spreadConnection(), "group");
        } catch (SpreadException e) {
            log.error("Could not join the Spread Group. Please make sure the spread server is running and the group is created.");
            shutdownServer();
        }
        return spreadGroup;
    }
}